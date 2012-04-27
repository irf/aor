package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlElement;

public class KPITarget {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getTarget() {
		return target;
	}

	public void setTarget(float target) {
		this.target = target;
	}

	@XmlElement(name = "KPIName")
	private String name;
	
	@XmlElement(name = "Target")
	private float target;
}
