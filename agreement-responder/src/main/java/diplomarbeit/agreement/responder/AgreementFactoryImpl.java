package diplomarbeit.agreement.responder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.AgreementState;
import diplomarbeit.agreement.Template;
import diplomarbeit.agreement.osgi.AgreementResponder;
import diplomarbeit.agreement.rest.AgreementService;

/**
 * Umsetzung einer mit OSGi-Mechanismen erweiterbaren AgreementFactory.
 * Diese Implementierung bietet die allgemeine Bereitstellung und Verwaltung von Vereinbarungen
 * über die im Rahmen der Diplomarbeit erarbeitete REST-Schnittstelle.
 * 
 * Dienstspezifische Aufgaben wie das Bereitstellen von Vorlagen und das Entscheiden von Angeboten
 * oder Kündigungsgesuchen wird hier nicht implementiert, sondern kann dieser Umsetzung als
 * AgreementResponder bereitgestellt werden.
 * 
 * @see AgreementResponder
 * @author Florian Blümel
 */
public class AgreementFactoryImpl implements HtmlRepresentation {
	private Map<String, Draft> drafts = new HashMap<String, Draft>();
	private Map<String, AgreementService> agreements = new HashMap<String, AgreementService>();
	private Set<AgreementResponder> responders = new HashSet<AgreementResponder>();
	private Bundle thisBundle;

	protected void activate(ComponentContext context) {
		thisBundle = context.getBundleContext().getBundle();
	}
	
	protected void deactivate() {
		thisBundle = null;
	}
	
	public AgreementFactoryImpl() {
	}
	
	protected void serviceOffered(AgreementResponder responder/*, Map<String, Object> properties*/) {
		responders.add(responder);
	}
	
	protected void serviceRevoked(AgreementResponder responder/*, Map<String, Object> properties*/) {
		responders.remove(responder);
	}
	
	@Override
	public Response createAgreement(
			Agreement offer,
			URI subscription,
			//URI agreementAcceptance,
			boolean deferralAllowed)
	{
		if (agreements.containsKey(offer.getId()))
			throw new WebApplicationException(Status.CONFLICT);
		
		URI location;
		try {
			// FIXME pfui pfui pfui. Saubere Lösung (wahrscheinlich): in activate() context.getProperties().get("org.apache.cxf.rs.address")
			location = new URI("http://localhost:9001/agreements/" + offer.getId());
		} catch (URISyntaxException e) {
			throw new WebApplicationException(400);
		}
		
		// soweit alles ok. Jetzt einen Responder finden, der über das Angebot entscheidet ...
		AcceptanceDecision decision = new AcceptanceDecision();
		for (AgreementResponder responder : responders) {
			if (!responder.decideOffer(offer, decision, deferralAllowed))
				// Dieser Responder interessiert sich nicht für das Angebot, vielleicht ja der nächste ...
				continue;
			
			// FIXME Ab hier ist nix mehr thread-safe. Läss sich sicher irgendwie mit synchronized lösen.
			
			if (!decision.isMade() && !deferralAllowed)
				// sorry lieber Responder, aber du machst Unsinn und deine Antwort braucht später keiner ...
				continue;
				
			if (decision.isRejected())
				// TODO: ... oder lieber einfach continue sagen, vielleicht will es ein anderer Responder annehmen?
				return Response.status(Status.FORBIDDEN).build();
			
			// entweder sofort akzeptiert oder Entscheidung steht aus, in beiden Fällen muss ne Ressource angelegt werden
			AgreementServiceImpl agreement = new AgreementServiceImpl(offer, responder);
			if (subscription == null)
				agreement.subscribe(subscription);
			agreements.put(offer.getId(), agreement);
			
			if (decision.isAccepted()) {
				agreement.setStateLocal(AgreementState.OBSERVED);
				System.out.println("neues Agreement '" + agreement.getId() + "' akzeptiert");
				return Response
					.created(location)
					.build();
			}
			else {
				// Entscheidungen, die ab "jetzt" (thread-safety und so) noch kommen, müssen auch einen Zustandsübergang auslösen
				decision.setAgreement(agreement);
				
				agreement.setStateLocal(AgreementState.PENDING);
				System.out.println("neues Agreement '" + agreement.getId() + "' haengt");
				return Response
					.status(Status.ACCEPTED)
					.location(location)
					.build();
			}
		}
		
		// Vereinbarung hat keinen interessiert ... also ablehnen.
		return Response.status(Status.FORBIDDEN).build();
	}

	@Override
	public Collection<Template> getTemplates() {
		Collection<Template> templates = new ArrayList<Template>();
		for (AgreementResponder responder : responders)
			templates.addAll(responder.getTemplates());
		return templates;
	}

	@Override
	public AgreementService getAgreement(String id) {
		return agreements.get(id);
	}
	
	@Override
	public String getAgreementAsHTML(String id) {
		AgreementService agreement = getAgreement(id);
		if (agreement == null)
			return null;
		return HtmlRenderer.agreement(agreement);
	}
	
	private Template getTemplate(String id) {
		for (Template t : getTemplates())
			if (t.getId().equals(id))
				return t;
		return null;
	}

	@Override
	public String getTemplateAsHTML(String id) {
		Template template = getTemplate(id);
		return HtmlRenderer.template(template);
	}

	@Override
	public Response createDraft(String templateId) {
		String id = UUID.randomUUID().toString();
		drafts.put(id, new Draft(getTemplate(templateId)));
		try {
			return Response.created(new URI("drafts/" + id + "/")).build();
		} catch (URISyntaxException e) {
			throw new WebApplicationException(500);
		}
	}

	@Override
	public Draft getDraft(String draftId) {
		return drafts.get(draftId);
	}
	
	@Override
	public String getFile(String name) {
		try {
			byte[] bytes = new byte[1024];
			InputStream is = thisBundle.getEntry(name).openStream();
			int bytesRead = is.read(bytes);
			String content = "";
			while (bytesRead > 0) {
				content += new String(bytes, 0, bytesRead);
				bytesRead = is.read(bytes);
			}
			return content;
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String getAgreements() {
		String list = "";
		for (String key : this.agreements.keySet())
			// FIXME pfui pfui pfui (s.o.) Saubere Lösung (wahrscheinlich): in activate() context.getProperties().get("org.apache.cxf.rs.address")
			list += "http://localhost:9001/agreements/" + key + "/\r\n";
		return list;
	}

	/*@Override
	public String getTemplateTerm(String id, String name) {
		// TODO
		return null;
	}

	@Override
	public String getDraftTerm(String id, String name) {
		// TODO
		return null;
	}*/
}