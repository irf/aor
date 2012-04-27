package diplomarbeit.agreement;

public enum GuaranteeState {
    NOT_DETERMINED("NotDetermined"),
    FULFILLED("Fulfilled"),   
    VIOLATED("Violated");

    private final String value;

    GuaranteeState(String v) {
    	value = v;
    }

    public String value() {
        return value;
    }

    public static GuaranteeState fromValue(String v) {
        for (GuaranteeState c: GuaranteeState.values())
            if (c.value.equals(v))
                return c;
    	throw new IllegalArgumentException(v);
    }
    
    public String toString() {
    	return value();
    }
}
