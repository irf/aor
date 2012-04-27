package diplomarbeit.random.consumer;

import org.osgi.service.component.ComponentContext;
import diplomarbeit.random.RandomService;

/**
 * Nutzung des Testdienstes. Die Aushandlung und lokale Bereitstellung des Dienstes erfolgt durch
 * den Negotiator. Da der auszuhandelnde Dienst als Abhängigkeit für diese Komponente konfiguriert
 * ist (siehe src/main/resources/OSGI-INF/component.xml), wird die Komponente von der OSGi-Runtime
 * automatisch aktiviert, sobald der ausgehandelte Dienst lokal bereitgestellt wurde.
 * @see Negotiator
 * @see RandomService
 * @author Florian Blümel
 */
public class Consumer {
    protected void activate(ComponentContext context) {
    	System.out.println("Consumer aktiviert");
    	RandomService random = (RandomService)context.locateService("diplomarbeit.random.RandomService");
    	for (int i=0; i<600; ++i) {
    		try {
    			System.out.println("Service antwortet mit " + random.sayHello());
    		}
    		catch (Throwable t) {
    			System.out.println("Service funktioniert nicht :(");
    		}
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
    	}
    }

    protected void deactivate() {
        System.out.println("Consumer deaktiviert");
    }
}
