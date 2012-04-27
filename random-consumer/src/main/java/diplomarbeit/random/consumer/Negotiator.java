package diplomarbeit.random.consumer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
//import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.Template;
import diplomarbeit.agreement.rest.AgreementFactory;
import diplomarbeit.monitoring.Monitoring;

/**
 * Aushandlung und lokale Bereitstellung des Testdienstes. Zum Formulieren einer Vereinbarung wird
 * eine der vom Provider angebotenen Vorlagen zufällig ausgewählt und unverändert als Angebot
 * unterbreitet. Es wird eine Überwachung eingeleitet und der Dienst für die lokale OSGi-Runtime
 * bereitgestellt. (Die Nutzung kann dann als Reaktion auf die lokale Bereitstellung erfolgen.)
 * @see Consumer
 * @author Florian Blümel
 */
public class Negotiator {
	public void activate(ComponentContext context) throws Exception {
		AgreementFactory factory = (AgreementFactory)context.locateService("agreementFactory");
		
		// 1. Vorlagen holen
		Collection<Template> templates = factory.getTemplates();

		// 2. eine auswählen
		Random random = new Random();
		Template[] ta = (Template[])templates.toArray();
		Template template = ta[random.nextInt(templates.size())];
		
		// 3. Vereinbarung anbieten
		String id = UUID.randomUUID().toString();
		Agreement offer = new Agreement(template, id, "Testvereinbarung");
		Response response = factory.createAgreement(offer, null, false);
		if (response.getStatus() != 200) {
			System.out.println("ups, Vereinbarung abgelehnt ...?!");
			return;
		}
		//AgreementService agreement = factory.getAgreement(id);
		
		// 4. Überwachung einleiten
		Monitoring m = (Monitoring)context.locateService("monitoring");
		Map<String, String> serviceMapping = m.startMonitoring((String)response.getMetadata().getFirst("Location"));
		
		// 5. Dienst(e) holen
		RemoteServiceAdmin remoteServiceAdmin = (RemoteServiceAdmin)context.locateService("remoteServiceAdmin");
		//Collection<ServiceReference> references = agreement.getTerms().getTerms(ServiceReference.class);
		for (Entry<String, String> ref : serviceMapping.entrySet()) {
			String location = ref.getValue();
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(RemoteConstants.ENDPOINT_ID, location.toString());
			properties.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "org.apache.cxf.rs");
			properties.put(Constants.OBJECTCLASS, new String[]{ref.getKey()});
			properties.put("org.apache.cxf.rs.address", location.toString());
			EndpointDescription endpoint = new EndpointDescription(properties);
			//ImportRegistration registration =
				remoteServiceAdmin.importService(endpoint);
			//org.osgi.framework.ServiceReference service = registration.getImportReference().getImportedService();
			//return (AgreementService)context.getBundleContext().getService(service);
		}
		
		// 6. Consumer wird den Dienst benutzen
	}
}
