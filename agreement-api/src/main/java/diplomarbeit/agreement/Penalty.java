package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.Duration;

public class Penalty {
	@XmlElement(name = "AssessmentInterval")
	private Duration duration;
	
	@XmlElement(name = "ValueUnit")
	private String unit;
	
	@XmlElement(name = "ValueExpr")
	private String value;

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
