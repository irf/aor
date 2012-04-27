package diplomarbeit.agreement.osgi;

import java.util.Collection;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.GuaranteeTerm;
import diplomarbeit.agreement.Template;

/**
 * Diese OSGi-Dienstschnittstelle ermöglicht die Umsetzung von dienstspezifischer Entscheidungen
 * über Angebote und Kündigungsgesuche.
 * 
 * @see AgreementFactory
 * @author Florian Blümel
 */
public interface AgreementResponder {
	// FIXME "service provider" trifft es nicht so ganz; wth, was ist das richtige Wort ...
	
	/**
	 * Entscheidet über das Annehmen eines unterbreiteten Angebots.
	 * @param offer die zu entscheidende Vereinbarung
	 * @param decision ein Listener, über den die getroffene Entscheidung mitgeteilt wird
	 * @param deferralAllowed <c>true</c>, wenn eine Entscheidung verzögert/asynchron getroffen werden darf
	 * @return <c>false</c> wenn dieser AgreementResponder dieses Angebot nicht entscheiden wird (weil er nicht zuständig ist oder so)
	 */
	public boolean decideOffer(Agreement offer, DecisionListener decision, boolean deferralAllowed);
	
	/**
	 * Entscheidet, ob eine bestehende Vereinbarung gekündigt werden darf. Eine asynchrone
	 * Behandlung ist hier grundsätzlich zulässig.
	 * @param agreement die zu kündigende Vereinbarung
	 * @param decision ein Listener, über den die getroffene Entscheidung mitgeteilt wird
	 */
	public void decideTerminationRequest(Agreement agreement, DecisionListener decision);

	public void onStateChanged(Agreement agreement);
	public void onServiceStateChanged(Agreement agreement, String serviceName);
	public void onGuaranteeStateChanged(Agreement agreement, GuaranteeTerm guarantee);

	public Collection<Template> getTemplates();
}
