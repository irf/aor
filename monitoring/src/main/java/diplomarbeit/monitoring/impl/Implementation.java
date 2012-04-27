package diplomarbeit.monitoring.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;

import diplomarbeit.agreement.rest.AgreementService;
import diplomarbeit.monitoring.Monitoring;

public class Implementation implements Monitoring {
	private ComponentContext context;
	private RemoteServiceAdmin remoteServiceAdmin;
	private Map<String, AgreementMonitor> monitors;

	private UriInfo uriInfo;
	@Context
	public void setUriInfo(UriInfo ui) { uriInfo = ui; }
	
	protected void activate(ComponentContext context) {
		this.context = context;
		this.remoteServiceAdmin = (RemoteServiceAdmin)context.locateService("remoteServiceAdmin");
		this.monitors = new HashMap<String, AgreementMonitor>();
	}

	protected void deactivate() {
		this.remoteServiceAdmin = null;
		this.context = null;
		this.monitors = null;
	}

	@Override
	public Map<String, String> startMonitoring(String agreementLocation) {
		AgreementService agreement;
		try {
			agreement = getAgreement(new URI(agreementLocation));
		} catch (URISyntaxException e) {
			throw new WebApplicationException(415);
		}
		AgreementMonitor monitor = new AgreementMonitor(agreement);
		monitors.put(monitor.getName(), monitor);
		
		/*String mapping = "";
		for (final String service : monitor.getServices())
			mapping += service + ": " + uriInfo.getRequestUri().toString() + monitor.getName() + "/" + service + "/\n";
		return mapping;*/
		Map<String, String> mapping = new HashMap<String, String>();
		for (final String service : monitor.getServices())
			mapping.put(service, uriInfo.getRequestUri().toString() + monitor.getName() + "/" + service);
		return mapping;
	}

	private AgreementService getAgreement(URI location) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(RemoteConstants.ENDPOINT_ID, location.toString());
		properties.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "org.apache.cxf.rs");
		properties.put(Constants.OBJECTCLASS, new String[]{AgreementService.class.getName()});
		properties.put("org.apache.cxf.rs.address", location.toString());
		EndpointDescription endpoint = new EndpointDescription(properties);
		ImportRegistration registration = this.remoteServiceAdmin.importService(endpoint);
		ServiceReference service = registration.getImportReference().getImportedService();
		return (AgreementService)context.getBundleContext().getService(service);
	}
	
	@Override
	public AgreementMonitor getMonitor(String agreementId) {
		return monitors.get(agreementId);
	}
}