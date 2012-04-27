package diplomarbeit.agreement.responder;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import diplomarbeit.agreement.rest.AgreementFactory;

/**
 * Erweitert die von AgreementFactory beschriebene REST-Schnittstelle um eine menschenlesbare
 * HTML-Darstellung.
 * @author Florian Blümel
 */
public interface HtmlRepresentation extends AgreementFactory {
	@Path("agreements/{id}")
	@GET
	@Produces("text/html")
	String getAgreementAsHTML(@PathParam("id") String id);

	@Path("templates/{id}")
	@GET
	@Produces("text/html")
	String getTemplateAsHTML(@PathParam("id") String id);
	
	/*@Path("templates/{id}/terms/{term}")
	@GET
	@Produces("text/html")
	String getTemplateTerm(@PathParam("id") String id, @PathParam("term") String name);*/
	
	@Path("drafts")
	@POST
	@Consumes("text/plain")
	Response createDraft(String templateId);
	
	@Path("drafts/{id}")
	Draft getDraft(@PathParam("id") String draftId);

	/*@Path("drafts/{id}/terms/{term}")
	@GET
	@Produces("text/html")
	String getDraftTerm(@PathParam("id") String id, @PathParam("term") String name);*/

	@GET
	@Path("static/{file}")
	String getFile(@PathParam("file") String name);
	
	@GET
	@Path("agreements")
	@Produces("text/uri-list")
	String getAgreements();
}
