package diplomarbeit.agreement.osgi;

/**
 * Schnittstelle zur Behandlung von Benachrichtigungen über getroffene Entscheidungen.
 * 
 * @see AgreementResponder
 * @author Florian Blümel
 */
public interface DecisionListener {
	public void onAcceptance();
	public void onRejection();
}
