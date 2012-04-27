package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GuaranteeTerm")
public class GuaranteeTerm implements Term {
	// TODO Collection<ServiceScope>
	@XmlAttribute(name = "Name")
	private String name;
	
	@XmlAttribute(name = "Obligated")
	private String obligated;		// FIXME ServiceRole
	
	@XmlElement(name = "ServiceLevelObjective")
	private ServiceLevelObjective objective;

	@XmlElement(name = "QualifyingCondition")
	private Object qualifyingCondition;

	public Object getQualifyingCondition() {
		return qualifyingCondition;
	}

	public void setQualifyingCondition(Object qualifyingCondition) {
		this.qualifyingCondition = qualifyingCondition;
	}

	public ServiceLevelObjective getObjective() {
		return objective;
	}

	public void setObjective(ServiceLevelObjective objective) {
		this.objective = objective;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "BusinessValueList")
	private BusinessValueList businessValues;

	public BusinessValueList getBusinessValues() {
		return businessValues;
	}

	public void setBusinessValues(BusinessValueList businessValues) {
		this.businessValues = businessValues;
	}

	public void setObligated(String obligated) {
		this.obligated = obligated;
	}

	public String getObligated() {
		return obligated;
	}
}
