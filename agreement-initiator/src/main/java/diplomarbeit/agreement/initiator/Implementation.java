package diplomarbeit.agreement.initiator;

import javax.ws.rs.WebApplicationException;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.AgreementState;
import diplomarbeit.agreement.rest.AgreementFactory;
import diplomarbeit.agreement.rest.AgreementService;

/**
 * Minimaltest für die CXF/JAX-RS-Funktionalität. Hat außer für die Fehlersuche nach exotischen
 * Problemen mit JAXB etc. keine Bedeutung.
 * @author Florian Blümel
 */
public class Implementation {
	public Implementation() {
	}
	
	public void bindAgreementFactory(AgreementFactory af) {
		System.out.println("bind!");
		AgreementService agreement = af.getAgreement("123");
		try {
			agreement.setState(AgreementState.TERMINATED);
			System.out.println("terminiert!");
		}
		catch (WebApplicationException e) {
			if (e.getResponse().getStatus() != 403)
				throw e;
			System.out.println("hmm ... ich durfte das Agreement nicht terminieren :(");
		}
		agreement.fetch();
		
		Agreement blub = new Agreement("666", "Test");
		af.createAgreement(blub, null, true);
	}
	
	public void unbindAgreementFactory(AgreementFactory af) {
		System.out.println("unbind!");
	}
}