/**
 * 
 */
package diplomarbeit.agreement.responder;

import diplomarbeit.agreement.AgreementState;
import diplomarbeit.agreement.osgi.DecisionListener;

/**
 * Nimmt eine Entscheidung �ber das Annehmen einer Vereinbarung entgegen. F�hrt den entsprechenden
 * Zustands�bergang f�r die Vereinbarung durch.
 * @author Florian Bl�mel
 */
class AcceptanceDecision implements DecisionListener {
	private Boolean accepted;
	private AgreementServiceImpl agreement;
	
	public void setAgreement(AgreementServiceImpl agreement) {
		this.agreement = agreement;
	}
	
	public boolean isMade() {
		return accepted != null;
	}
	
	public boolean isAccepted() {
		return isMade() && accepted.booleanValue();
	}

	public boolean isRejected() {
		return isMade() && !accepted.booleanValue();
	}
	
	@Override
	public void onAcceptance() {
		accepted = Boolean.TRUE;
		if (agreement != null)
			agreement.setStateLocal(AgreementState.OBSERVED);
	}

	@Override
	public void onRejection() {
		accepted = Boolean.FALSE;
		if (agreement != null)
			agreement.setStateLocal(AgreementState.REJECTED);
	}
}