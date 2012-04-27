package diplomarbeit.agreement.osgi;

/**
 * Schnittstelle zur Behandlung von Benachrichtigungen �ber getroffene Entscheidungen.
 * 
 * @see AgreementResponder
 * @author Florian Bl�mel
 */
public interface DecisionListener {
	public void onAcceptance();
	public void onRejection();
}
