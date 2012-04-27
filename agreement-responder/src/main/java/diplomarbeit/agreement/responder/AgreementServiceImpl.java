package diplomarbeit.agreement.responder;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.AgreementState;
import diplomarbeit.agreement.Context;
import diplomarbeit.agreement.GuaranteeState;
import diplomarbeit.agreement.GuaranteeTerm;
import diplomarbeit.agreement.ServiceState;
import diplomarbeit.agreement.TermCompositor;
import diplomarbeit.agreement.osgi.AgreementResponder;
import diplomarbeit.agreement.rest.AgreementService;

/**
 * Eine Umsetzung der REST-Schnittstelle für die Ressource Agreement und ihre Unterressourcen.
 * @see AgreementService
 * @see Agreement
 * @author Florian Blümel
 */
public class AgreementServiceImpl implements AgreementService {
	private Agreement agreement;
	private AgreementState state = AgreementState.PENDING;
	private Map<String, ServiceState> serviceStates = new HashMap<String, ServiceState>();
	private Map<String, GuaranteeState> guaranteeStates = new HashMap<String, GuaranteeState>();
	private Map<Integer, URI> subscriptions = new HashMap<Integer, URI>();
	private int nextSubscriptionId = 1;
	private AgreementResponder responder;
	
	public AgreementServiceImpl(Agreement agreement, AgreementResponder responder) {
		this.agreement = agreement;
		this.responder = responder;
	}
	
	@Override
	public Agreement fetch() {
		return agreement;
	}

	@Override
	public AgreementState getState() {
		return state;
	}
	
	@Override
	public void setState(AgreementState state) {
		if (!state.equals(AgreementState.TERMINATED))
			// Forbidden! setState ist nur zum Kündigen da.
			throw new WebApplicationException(403);
		
		if (this.state.equals(AgreementState.COMPLETE) || this.state.equals(AgreementState.REJECTED))
			// Conflict! Diese Vereinbarung kann man nicht kündigen.
			throw new WebApplicationException(409);
		
		if (this.state.equals(AgreementState.TERMINATED))
			// Nichts zu tun.
			return;
		
		if (this.state.equals(AgreementState.OBSERVED_AND_TERMINATING) || this.state.equals(AgreementState.PENDING_AND_TERMINATING))
			// Bin ja schon dabei! Nicht hetzen lassen ...
			return;
		
		TerminationDecision decision = new TerminationDecision(this);
		responder.decideTerminationRequest(agreement, decision);
		if (!decision.isMade()) {
			// FIXME: auch hier harte Probleme mit der thread-safety ...
			if (this.state.equals(AgreementState.PENDING))
				setStateLocal(AgreementState.PENDING_AND_TERMINATING);
			else // OBSERVED
				setStateLocal(AgreementState.OBSERVED_AND_TERMINATING);
			//return Response.status(Status.ACCEPTED).build();
		}
		//return Response.noContent().build();
	}
	
	public void setStateLocal(AgreementState state) {
		// Sinn dieser Methode: lokal durfen alle Zustandsübergange ausgelöst werden
		// FIXME statt dieser behelfsmäßigen Lösung wäre es schöner, rauszufinden, in welchem Kontext setState aufgerufen wird
		this.state = state;
		notifySubscribers("state", state.toString());
	}

	@Override
	public ServiceState getServiceState(String termName) {
		return serviceStates.get(termName);
	}

	@Override
	public void setServiceState(String serviceName, ServiceState state) {
		// TODO vielleicht mal prüfen, obs den Dienst überhaupt gibt ... siehe setGuaranteeState
		serviceStates.put(serviceName, state);
		notifySubscribers("state/service/" + serviceName, state.toString());
		responder.onServiceStateChanged(agreement, serviceName);
	}

	@Override
	public Context getContext() {
		return agreement.getContext();
	}

	@Override
	public String getId() {
		return agreement.getId();
	}

	@Override
	public String getName() {
		return agreement.getName();
	}

	@Override
	public TermCompositor getTerms() {
		return agreement.getTerms();
	}

	@Override
	public String getSubscriptions() {
		String uriList = "";
		for (Integer subscription : subscriptions.keySet())
			// FIXME relative URIs ... sehr unschön.
			uriList += subscription.toString() + "\r\n";
		return uriList;
	}

	@Override
	public Response subscribe(URI endpoint) {
		int id = nextSubscriptionId++;
		subscriptions.put(id, endpoint);
		try {
			return Response.created(new URI(id + "")).build();
		} catch (URISyntaxException e) {
			throw new WebApplicationException(500);
		}
	}

	@Override
	public void unsubscribe(String subscription) {
		subscriptions.remove(Integer.valueOf(subscription));
	}
	
	private void notifySubscribers(String changedResourceRelative, String newValue) {
		String agreementResource = "";	 // TODO absoluten URI der Vereinbarung bestimmen
		String changedResource = agreementResource + "/" + changedResourceRelative;
		for (Entry<Integer, URI> subscription : subscriptions.entrySet()) {
			try {
				URLConnection connection = new URL(subscription.getValue().toString()).openConnection();
				connection.setDoOutput(true);
				connection.addRequestProperty("Content-Type", "text/plain");
				connection.addRequestProperty("Link", "<" + changedResource + ">; rel=Resource");
				String subscriptionResource = agreementResource + "/subscriptions/" + subscription.getKey().toString();
				connection.addRequestProperty("Link", "<" + subscriptionResource + ">; rel=Subscription");
				OutputStream stream = connection.getOutputStream();
				try {
					stream.write(newValue.getBytes());
				}
				finally {
					stream.close();
				}
			}
			catch (Throwable t) {
				// FIXME man könnte zumindest /etwas/ spezifischer sein ...
			}
		}
	}

	@Override
	public GuaranteeState getGuaranteeState(String termName) {
		return guaranteeStates.get(termName);
	}

	@Override
	public void setGuaranteeState(String termName, GuaranteeState state) {	
		Collection<GuaranteeTerm> guarantees = agreement.getTerms().getTerms(GuaranteeTerm.class);
		for (GuaranteeTerm g : guarantees)
			if (g.getName().equals(termName)) {
				guaranteeStates.put(termName, state);
				notifySubscribers("state/guarantee/" + termName, state.toString());
				responder.onGuaranteeStateChanged(agreement, g);
				return;
			}
		throw new WebApplicationException(404);
	}
}
