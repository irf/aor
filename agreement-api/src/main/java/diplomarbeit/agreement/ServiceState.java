package diplomarbeit.agreement;

public enum ServiceState {
    NOT_READY("Not Ready"),
    READY("Ready"),   
    COMPLETED("Completed"),
    PROCESSING("Processing"),
    IDLE("Idle");

    private final String value;

    ServiceState(String v) {
    	value = v;
    }

    public String value() {
        return value;
    }

    public static ServiceState fromValue(String v) {
        for (ServiceState c: ServiceState.values())
            if (c.value.equals(v))
                return c;
    	throw new IllegalArgumentException(v);
    }
    
    public String toString() {
    	return value();
    }
}
