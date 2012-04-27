/**
 * 
 */
package diplomarbeit.agreement.responder;

import diplomarbeit.agreement.AgreementState;
import diplomarbeit.agreement.osgi.DecisionListener;

/**
 * Nimmt eine Entscheidung über die Zulässigkeit des Kündigens einer Vereinbarung entgegen.
 * Führt den entsprechenden Zustandsübergang am Agreement durch.
 * @author Florian Blümel
 */
class TerminationDecision implements DecisionListener {
	private AgreementServiceImpl agreement;
	private AgreementState before;
	private boolean made = false;
	
	public TerminationDecision(AgreementServiceImpl agreement) {
		this.agreement = agreement;
		before = agreement.getState();
	}
	
	public boolean isMade() {
		return made;
	}

	@Override
	public void onAcceptance() {
		made = true;
		agreement.setStateLocal(AgreementState.TERMINATED);
	}

	@Override
	public void onRejection() {
		made = true;
		if (!agreement.getState().equals(before))
			agreement.setStateLocal(before);
	}
}