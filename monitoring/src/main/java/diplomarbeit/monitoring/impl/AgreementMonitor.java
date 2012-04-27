package diplomarbeit.monitoring.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.resource.ClientResource;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.GuaranteeState;
import diplomarbeit.agreement.GuaranteeTerm;
import diplomarbeit.agreement.ServiceReference;
import diplomarbeit.agreement.ServiceState;
import diplomarbeit.agreement.rest.AgreementService;

public class AgreementMonitor {
	private AgreementService agreement;
	private String name;
	private Map<String, URI> services = new HashMap<String, URI>();
	private Collection<GuaranteeTerm> guarantees;
	
	/*private class AssessmentPeriod {
		public float value;
		public int samples;
		public Date end;
		public void reset(Duration d) {
			end = new Date();
			d.addTo(end);
			value = 0;
			samples = 0;
		}
	}
	private Map<Penalty, AssessmentPeriod> assessments = new HashMap<Penalty, AssessmentPeriod>();*/
	
	private int availabilityCount = 0;
	private int availabilitySamples = 0;
	
	public AgreementMonitor(AgreementService agreement) {
		this.agreement = agreement;
		Agreement a = this.agreement.fetch();
		this.name = a.getId();

		this.guarantees = a.getTerms().getTerms(GuaranteeTerm.class);
		Collection<ServiceReference> serviceReferences = a.getTerms().getTerms(ServiceReference.class);
		for (final ServiceReference ref : serviceReferences)
			try {
				services.put(ref.getName(), new URI(ref.getReference()));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public Collection<String> getServices() {
		return services.keySet();
	}
	
	public String getName() {
		return name;
	}
	
	private Collection<GuaranteeTerm> getGuarantees(String serviceName, String kpiName) {
		ArrayList<GuaranteeTerm> guarantees = new ArrayList<GuaranteeTerm>();
		for (GuaranteeTerm g : this.guarantees) {
			//if (!g.getServiceScope().getServiceName().equals(serviceName))
			//	continue;
			if (g.getObjective().getKPI().getName().equals(kpiName))
				guarantees.add(g);
		}
		return guarantees;
	}
	
	private void handleResponseTime(String serviceName, double t) {
		// TODO; für das implementierte Anwendungsbeispiel uninteressant; Übermittlung als KPI analog zu Availability
		// avg = p*avg + (1-p)*t; 
	}
	
	private void handleAvailability(String serviceName, boolean a) {
		agreement.setServiceState(serviceName, a ? ServiceState.READY : ServiceState.NOT_READY);
		if (a)
			++availabilityCount;
		++availabilitySamples;
		if (availabilitySamples < 100)
			return;

		Collection<GuaranteeTerm> guarantees = getGuarantees(serviceName, "Availability");
		for (GuaranteeTerm g : guarantees) {
			if (g.getObjective().getKPI().getTarget() > (float)availabilityCount / availabilitySamples)
				agreement.setGuaranteeState(g.getName(), GuaranteeState.VIOLATED);
			else
				agreement.setGuaranteeState(g.getName(), GuaranteeState.FULFILLED);
		}
	}
	
	private ClientResource prepare(
			Method method,
			String serviceName,
			String relativeUri,
			HttpHeaders requestHeaders,
			byte[] entity)
	{
		URI serviceUri = services.get(serviceName);
		if (serviceUri == null)
			throw new WebApplicationException(404);
		ClientResource resource = new ClientResource(method, serviceUri.toString() + relativeUri);
		resource.setRetryOnError(false);
		//Form headers = (Form)resource.getRequestAttributes().get("org.restlet.http.headers");
		//TODO request header
		if (entity != null)
			resource.getRequest().setEntity(
					new String(entity), 
					new MediaType(requestHeaders.getMediaType().toString()));
		return resource;
	}
	
	/**
	 * Leitet ein HTTP-Request an den zu überwachenden Dienst weiter. Dabei werden Erreichbarkeit
	 * ("Web-Service antwortet"), Verfügbarkeit ("Web-Service bearbeitet Anfragen") und Antwortzeit
	 * gemessen.
	 * @param method durchzuführende HTTP-Operation
	 * @param serviceName Name des genutzten Dienstes laut Agreement
	 * @param relativeUri angefragter Dienst-URI (Ressource/Unterressource)
	 * @param requestHeaders HTTP-Metadaten
	 * @param entity Nachrichtenrumpf der Anfrage
	 * @return Antwort des Dienstes (oder HTTP 504 bei Nicht-Erreichbarkeit)
	 */
	private Response proxy(
			Method method,
			String serviceName,
			String relativeUri,
			HttpHeaders requestHeaders,
			byte[] entity)
	{
		/* TODO messen!
		 * a) Erreichbarkeit (sonst 504)
		 * b) Bearbeitungszeit
		 * c) Status (5xx wird als nicht funktionierender Dienst ausgelegt)
		 */
		ClientResource resource = prepare(method, serviceName, relativeUri, requestHeaders, entity);
		long before = System.nanoTime();
		resource.handle();
		handleResponseTime(serviceName, System.nanoTime() - before);
		
		if (resource.getStatus().isConnectorError()) {
			handleAvailability(serviceName, false);
			return Response
				.status(504) // Gateway Timeout -- vielleicht würde auch 502 Sinn machen?
				.build();
		}
		
		handleAvailability(serviceName, !resource.getStatus().isServerError());
		return Response
			.status(resource.getResponse().getStatus().getCode())
			//TODO response header
			.entity(resource.getResponse().getEntityAsText())
			.build();
	}
	
	@GET
	@Path("{service}/{request:.*}")
	public Response get(
			@PathParam("service") String serviceName,
			@PathParam("request") String relativeUri,
			@Context HttpHeaders headers)
	{
		return proxy(Method.GET, serviceName, relativeUri, headers, null);
	}

	@PUT
	@Path("{service}/{request:.*}")
	public Response put(
			@PathParam("service") String serviceName,
			@PathParam("request") String relativeUri,
			@Context HttpHeaders headers,
			byte[] entity)
	{
		return proxy(Method.PUT, serviceName, relativeUri, headers, entity);
	}
	
	@POST
	@Path("{service}/{request:.*}")
	public Response post(
			@PathParam("service") String serviceName,
			@PathParam("request") String relativeUri,
			@Context HttpHeaders headers,
			byte[] entity)
	{
		return proxy(Method.POST, serviceName, relativeUri, headers, entity);
	}

	@DELETE
	@Path("{service}/{request:.*}")
	public Response delete(
			@PathParam("service") String serviceName,
			@PathParam("request") String relativeUri,
			@Context HttpHeaders headers)
	{
		return proxy(Method.DELETE, serviceName, relativeUri, headers, null);
	}

	@HEAD
	@Path("{service}/{request:.*}")
	public Response head(
			@PathParam("service") String serviceName,
			@PathParam("request") String relativeUri,
			@Context HttpHeaders headers)
	{
		return proxy(Method.HEAD, serviceName, relativeUri, headers, null);
	}
}
