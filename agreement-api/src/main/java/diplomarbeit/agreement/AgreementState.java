package diplomarbeit.agreement;

public enum AgreementState {
    REJECTED("Rejected"),
    PENDING("Pending"),   
    OBSERVED("Observed"),
    COMPLETE("Complete"),
    PENDING_AND_TERMINATING("PendingAndTerminating"),
    OBSERVED_AND_TERMINATING("ObservedAndTerminating"),
    TERMINATED("Terminated");

    private final String value;

    AgreementState(String v) {
    	value = v;
    }

    public String value() {
        return value;
    }

    public static AgreementState fromValue(String v) {
        for (AgreementState c: AgreementState.values())
            if (c.value.equals(v))
                return c;
    	throw new IllegalArgumentException(v);
    }
    
    public String toString() {
    	return value();
    }
}
