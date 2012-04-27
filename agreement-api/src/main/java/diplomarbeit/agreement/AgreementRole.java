package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AgreementRoleType")
@XmlEnum
public enum AgreementRole {
	@XmlEnumValue("AgreementInitiator")
    AGREEMENT_INITIATOR("AgreementInitiator"),
    
	@XmlEnumValue("AgreementResponder")
    AGREEMENT_RESPONDER("AgreementResponder");

    private final String value;

    AgreementRole(String v) {
    	value = v;
    }

    public String value() {
        return value;
    }

    public static AgreementRole fromValue(String v) {
        for (AgreementRole c: AgreementRole.values())
            if (c.value.equals(v))
                return c;
    	throw new IllegalArgumentException(v);
    }
}
