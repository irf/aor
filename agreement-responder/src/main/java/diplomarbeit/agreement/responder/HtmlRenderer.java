package diplomarbeit.agreement.responder;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.AgreementRole;
import diplomarbeit.agreement.Context;
import diplomarbeit.agreement.GuaranteeTerm;
import diplomarbeit.agreement.ServiceDescription;
import diplomarbeit.agreement.ServiceReference;
import diplomarbeit.agreement.Template;
import diplomarbeit.agreement.Term;
import diplomarbeit.agreement.rest.AgreementService;

/**
 * Stellt die HTML-Repräsentation für einige Datenmodell-Klassen bereit. "Sauber implementiert" ist
 * was anderes -- das hier ist eher "Weg des geringsten Widerstands".
 * @author Florian Blümel
 */
public class HtmlRenderer {
	public static String context(Context context) {
		String html = "<h2>Rahmeninformationen</h2>"
			+ "<ul>";
		if (context.getInitiator() != null)
			html += "<li>Die Vereinbarung wird von <em>" + context.getInitiator() + "</em> angeboten.</li>";
		if (context.getResponder() != null)
			html += "<li>Die Vereinbarung wird von <em>" + context.getResponder() + "</em> entgegengenommen.</li>";
		
		html += "<li>Ausgehandelte Dienste werden von der <em>";
		if (context.getServiceProvider().equals(AgreementRole.AGREEMENT_INITIATOR))
			html +=	"einleitenden";
		else
			html += "antwortenden";
		html += "</em> Partei bereitgestellt.</em>";
		return html + "</ul>";
	}
	
	public static String terms(Collection<Term> terms) {
		String html = "";
		html += "<h2>Bestimmungen</h2>";
		html += "<ul>";
		for (Term term : terms) {
			if (term instanceof ServiceReference) {
				ServiceReference ref = (ServiceReference)term;
				html += "<li>Der Dienst <em>" + ref.getServiceName() + "</em> wird unter der Adresse <em>" + ref.getReference() + "</em> bereitgestellt.</li>\n";
			}
			if (term instanceof ServiceDescription) {
				ServiceDescription desc = (ServiceDescription)term;
				html += "<li>Der Dienst <em>" + desc.getServiceName() + "</em> entspricht der <a href='terms/" + desc.getName() + "'>gegebenen Beschreibung</a>.</li>\n";
			}
			if (term instanceof GuaranteeTerm) {
				GuaranteeTerm guarantee = (GuaranteeTerm)term;
				html += "<li>Der <em>"; 
				html += guarantee.getObligated().equals("ServiceProvider") ? "Dienstanbieter" : "Dienstnehmer";
				html += "</em> sichert zu, dass ";//bei den Diensten <ul></ul>";
				html += " f&uuml;r die Kennzahl <em>&bdquo;" + guarantee.getObjective().getKPI().getName() + "&rdquo;</em>";
				html += " ein Wert von <em>" + guarantee.getObjective().getKPI().getTarget() + "</em> erreicht wird.</li>\n";
			}
		}
		html += "</ul>";
		return html;
	}

	public static String agreement(AgreementService agreement) {
		return "<html><head><title>Vereinbarung &bdquo;" + agreement.getName() + "&rdquo;</title><link rel='stylesheet' href='/static/stylesheet.css'/>"
			+ "<body>"
			+ "<h1>Vereinbarung &bdquo;" + agreement.getName() + "&rdquo;</h1>"
			+ context(agreement.getContext())
			+ terms(agreement.getTerms().getTerms(Term.class))
			+ "</body></html>";
	}
	
	public static String template(Template template) {
		String html = "<html><head><title>Vorlage &bdquo;" + template.getName() + "&rdquo;</title>"
			+ "<link rel='stylesheet' href='/static/stylesheet.css'></link>"
			+ "<script type='text/javascript' src='/static/prototype.js'></script>"
			+ "<h1>Vorlage " + template.getId() + " -- &bdquo;" + template.getName() + "&rdquo;</h1>";
		
		html += context(template.getContext());
		html += terms(template.getTerms().getTerms(Term.class));
		
		html += "<input type='submit' value='Interaktive Aushandlung' onclick=\"javascript:new Ajax.Request('/drafts', { method: 'post', parameters: '" + template.getId() + "', " 
			+ "onSuccess: function(response) { window.location = response.getHeader('Location'); }, contentType: 'text/plain' });\" />";
		
		// TODO: Constraints
		return html + "</body></html>";
	}
	
	public static String draft(Agreement agreement, Map<String, String> items) {
		String html = "<html><head><title>Entwurf</title>"
			+ "<link rel='stylesheet' href='/static/stylesheet.css'></link>"
			+ "<script type='text/javascript' src='/static/prototype.js'></script>"
			+ "</head>"
			+ "<body>"
			+ "<h1>Entwurf auf Basis von &bdquo;" + agreement.getContext().getTemplateName() + "&rdquo;</h1>"
			+ HtmlRenderer.context(agreement.getContext())
			+ "<div id='terms'>"
			+ HtmlRenderer.terms(agreement.getTerms().getTerms(Term.class))
			+ "</div>"
			
			+ "<h2>Aushandelbare Punkte</h2>";
		
		for (Entry<String, String> item : items.entrySet()) {
			html += "<p>"
				+ "<label for='" + item.getKey() + "'>" + item.getKey() + "</label><br/>"
				+ "<input type='text' id='" + item.getKey() + "' name='val' value='" + item.getValue() + "' />"
				+ "<input type='submit' value='setzen' onclick=\"javascript:new Ajax.Request(window.location + 'items/" + item.getKey() + "', {method: 'put', parameters: $('" + item.getKey() + "').value, contentType: 'text/plain', onSuccess: function(x) {new Ajax.Updater('terms', window.location + 'terms', {method: 'get'});}, onFailure: function(x) { alert(x.responseText); } });\" />"
				+ "</p>";
			
			/*html += "<form action='items/id' method='POST'>"
				+ "<p><label for='" + item.getKey() + "'>" + item.getKey() + "</label></br>"
				+ "<input type='text' id='" + item.getKey() + "' name='value' value='" + item.getValue() + "' /></p>"
				+ "<p><input type='submit' value='Wert setzen' /></p></form>";*/
		}
		html += "<br><br><input type='submit' value='Angebot unterbreiten' onclick=\"javascript:new Ajax.Request(window.location, {method: 'get', requestHeaders: {Accept: 'application/x-wsag+xml'}, onSuccess: function(x) { new Ajax.Request('/agreements', { method: 'post', parameters: x.responseText, " 
			+ "onSuccess: function(response) { window.location = response.getHeader('Location'); }, contentType: 'application/x-wsag+xml' }); } });\" />";
		return html + "</body></html>";
	}
}
