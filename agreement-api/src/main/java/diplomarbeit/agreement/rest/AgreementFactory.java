package diplomarbeit.agreement.rest;

import java.net.URI;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.Template;

/**
 * <p>Beschreibt den OSGi-Dienst zum Vereinbaren von SLAs.</p>
 * 
 * <p>Die annotierten Medientypen sehen eine Nutzung als OSGi-Dienst vor. Eine Erweiterung um
 * zusätzliche Medientypen kann durch erbende Interfaces erfolgen.</p>
 * 
 * <p>WSAgreement: Entspricht Port wsag:AgreementFactory und Port wsag:PendingAgreementFactory</p>
 * 
 * @author Florian Blümel
 */
public interface AgreementFactory {
	@Path("agreements/{id}")
	public AgreementService getAgreement(@PathParam("id") String id);
	
	// FIXME eigentlich soll die Subscription in einen Link-Header. Saubere Lösung:
	// JAX-RS/CXF erweitern um @LinkParam(String relation, Enum type=REL) (oder so)
	// - "Serverseite" wär wohl kein Akt, aber "Clientseite" dürfte Probleme machen 
    @POST
	@Path("agreements")
    @Consumes("application/x-wsag+xml")
    public Response createAgreement(
    	Agreement offer,
    	//@HeaderParam("X-Initiator-Agreement") URI initiatorAgreement,
    	@HeaderParam("X-Link-Subscription") URI subscription,
    	@HeaderParam("X-Deferral-Allowed") @DefaultValue("false") boolean deferralAllowed);
    
    /** Entspricht Resource Property wsag:Template<br/>
     * The templates resource property represents a sequence of 0 or more
     * templates of offers that can be accepted by the wsag:AgreementFactory
     * operations in order to create an Agreement. A template defines a prototype
     * agreement along with creation constraints, as defined in section 5.
     * @note List?
     * @return 200
     */
    @GET
    @Produces("application/x-wsag+xml")
    @Path("templates")
    public Collection<Template> getTemplates();
}
