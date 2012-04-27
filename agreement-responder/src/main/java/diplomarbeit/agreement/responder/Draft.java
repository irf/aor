package diplomarbeit.agreement.responder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.GuaranteeTerm;
import diplomarbeit.agreement.OfferItem;
import diplomarbeit.agreement.Template;
import diplomarbeit.agreement.Term;

public class Draft {
	//private Template template;
	private Agreement agreement;
	private Map<String, String> items = new HashMap<String, String>();
	
	public Draft(Template template) {
		//this.template = template;
		this.agreement = new Agreement(template, UUID.randomUUID().toString(), template.getName());
		for (OfferItem item : template.getConstraints()) {
			items.put(item.getName(), "");
			Collection<GuaranteeTerm> guarantees = template.getTerms().getTerms(GuaranteeTerm.class);
			for (GuaranteeTerm g : guarantees)
				if (g.getObjective().getKPI().getName().equals(item.getName()))
					items.put(item.getName(), g.getObjective().getKPI().getTarget() + "");
		}
			
	}
	
	@Path("terms")
	@GET
	@Produces("text/html")
	public String getTerms() {
		return HtmlRenderer.terms(agreement.getTerms().getTerms(Term.class));
	}
	
	@Path("items/{id}")
	@GET
	@Produces("text/plain")
	public String getItemValue(@PathParam("id") String item) {
		return items.get(item);
	}
	
	@Path("items/{id}")
	@PUT
	@Consumes("text/plain")
	public void setItemValue(@PathParam("id") String item, String value) {
		if (!items.containsKey(item))
			throw new WebApplicationException(404);
		
		float f = Float.parseFloat(value);
		if (f < 0 || f >= 1)
			throw new WebApplicationException(Response.status(403).entity("Wert muss >=0 und <1 sein").build());
		// TODO Konformität mit Constraints überprüfen
		items.put(item, value);
		Collection<GuaranteeTerm> guarantees = agreement.getTerms().getTerms(GuaranteeTerm.class);
		for (GuaranteeTerm g : guarantees)
			if (g.getObjective().getKPI().getName().equals(item))
				g.getObjective().getKPI().setTarget(f);
	}
	
	@Path("items/{id}")
	@POST
	@Consumes("text/plain")
	public Response browserWorkaround(@PathParam("id") String item, String value) {
		float f = Float.parseFloat(value);
		if (f < 0 || f >= 1)
			return Response.status(403).entity("Wert muss >=0 und <1 sein").build();
		setItemValue(item, value);
		return Response.noContent().build();
	}
	
	@GET
	@Produces("text/html")
	public String get() {
		return HtmlRenderer.draft(agreement, items);
	}
	
	@GET
	@Produces("application/x-wsag+xml")
	public Agreement getAsAgreement() {
		return agreement;
	}
}
