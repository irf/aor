package diplomarbeit.monitoring;

//import java.net.URI;

//import javax.ws.rs.Consumes;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import diplomarbeit.monitoring.impl.AgreementMonitor;

public interface Monitoring {
	@POST
	//@Consumes("text/uri-list")	// FIXME?
	Map<String, String> startMonitoring(String agreement);
	
	@Context
	public void setUriInfo(UriInfo ui);

	@Path("{agreement}")
	public AgreementMonitor getMonitor(@PathParam("agreement") String agreementId);
}
