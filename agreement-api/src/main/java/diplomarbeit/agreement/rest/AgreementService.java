package diplomarbeit.agreement.rest;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.AgreementState;
import diplomarbeit.agreement.Context;
import diplomarbeit.agreement.GuaranteeState;
import diplomarbeit.agreement.ServiceState;
import diplomarbeit.agreement.TermCompositor;

/** 
 * <p>Beschreibt die OSGi-Dienstschnittstelle AgreementService, die den Zugriff auf eine
 * Vereinbarung bereitstellt. Umfasst die REST-Schnittstellen der Ressource Agreement und ihrer
 * Unterressourcen.</p>
 * 
 * <p>Die annotierten Medientypen sehen eine Nutzung als OSGi-Dienst vor. Eine Erweiterung um
 * zusätzliche Medientypen kann durch erbende Interfaces erfolgen.</p>
 * 
 * <p>Übertragung von WSAgreement: Entspricht Port wsag:Agreement und Port wsag:AgreementState.</p>
 * 
 * @author Florian Blümel
 */
public interface AgreementService {
	/**
	 * Liefert die Vereinbarung. Diese kann zur WSAgreement/XML-Repräsentation serialisiert werden.
	 * 
	 * @return Vereinbarung -- HTTP-Status 200
	 */
	@GET
	@Produces("application/x-wsag+xml")
	//@Produces("application/xml")
	Agreement fetch();
	
	/**
	 * <p>Liefert den Namen der Vereinbarung.</p>
	 * 
	 * Entspricht Resource Property wsag:Name aus Port wsag:Agreement.
	 * It MAY be empty if no name has been defined in the offer submitted.
	 * @return 200, 204
	 * @note 404 oder 204?
	 */
	@Path("name")
	@GET
	@Produces("text/plain")
	String getName();
	
	@Path("id")
	@GET
	@Produces("text/plain")
	String getId();
	
	/** Entspricht Resource Property wsag:Context.<br/>
	 * The context is static information about the agreement such as the
	 * parties involved in the agreement. See the section in this document about
	 * the agreement context.
	 * @return 200
	 */
	@Path("context")
	@GET
	@Produces("application/x-wsag+xml")
	Context getContext();

	/** Entspricht Resource Property wsag:Terms.<br/>
	 * This property specifies the terms of the agreement.
	 * @param id
	 * @return 200
	 */
	@Path("terms")
	@GET
	@Produces("application/x-wsag+xml")
	TermCompositor getTerms();
	
	@Path("state/service/{name}")
	@GET
	@Produces("text/plain")
	ServiceState getServiceState(@PathParam("name") String termName);
	
	@Path("state/guarantee/{name}")
	@GET
	@Produces("text/plain")
	GuaranteeState getGuaranteeState(@PathParam("name") String termName);
	
	@Path("state/service/{name}")
	@PUT
	@Consumes("text/plain")
	void setServiceState(@PathParam("name") String serviceName, ServiceState state);

	@Path("state/guarantee/{name}")
	@PUT
	@Consumes("text/plain")
	void setGuaranteeState(@PathParam("name") String termName, GuaranteeState state);

	@Path("state")
	@GET
	@Produces("text/plain")
	AgreementState getState();
	
	@Path("state")
	@PUT
	@Consumes("text/plain")
	void setState(AgreementState state);
	
	@Path("subscriptions")
	@GET
	@Produces("text/uri-list")
	String getSubscriptions();
	
	@Path("subscriptions")
	@POST
	@Consumes("text/uri-list")
	Response subscribe(URI endpoint);
	
	@Path("subscriptions/{id}")
	@DELETE
	void unsubscribe(@PathParam("id") String subscription);
}
