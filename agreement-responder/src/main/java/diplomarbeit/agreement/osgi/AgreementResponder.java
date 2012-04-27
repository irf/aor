package diplomarbeit.agreement.osgi;

import java.util.Collection;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.GuaranteeTerm;
import diplomarbeit.agreement.Template;

/**
 * Diese OSGi-Dienstschnittstelle erm�glicht die Umsetzung von dienstspezifischer Entscheidungen
 * �ber Angebote und K�ndigungsgesuche.
 * 
 * @see AgreementFactory
 * @author Florian Bl�mel
 */
public interface AgreementResponder {
	// FIXME "service provider" trifft es nicht so ganz; wth, was ist das richtige Wort ...
	
	/**
	 * Entscheidet �ber das Annehmen eines unterbreiteten Angebots.
	 * @param offer die zu entscheidende Vereinbarung
	 * @param decision ein Listener, �ber den die getroffene Entscheidung mitgeteilt wird
	 * @param deferralAllowed <c>true</c>, wenn eine Entscheidung verz�gert/asynchron getroffen werden darf
	 * @return <c>false</c> wenn dieser AgreementResponder dieses Angebot nicht entscheiden wird (weil er nicht zust�ndig ist oder so)
	 */
	public boolean decideOffer(Agreement offer, DecisionListener decision, boolean deferralAllowed);
	
	/**
	 * Entscheidet, ob eine bestehende Vereinbarung gek�ndigt werden darf. Eine asynchrone
	 * Behandlung ist hier grunds�tzlich zul�ssig.
	 * @param agreement die zu k�ndigende Vereinbarung
	 * @param decision ein Listener, �ber den die getroffene Entscheidung mitgeteilt wird
	 */
	public void decideTerminationRequest(Agreement agreement, DecisionListener decision);

	public void onStateChanged(Agreement agreement);
	public void onServiceStateChanged(Agreement agreement, String serviceName);
	public void onGuaranteeStateChanged(Agreement agreement, GuaranteeTerm guarantee);

	public Collection<Template> getTemplates();
}
