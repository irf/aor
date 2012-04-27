package diplomarbeit.agreement;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;

public class Variable {
	@XmlAttribute(name = "Name")
	private String name;
	
	@XmlAttribute(name = "Metric")
	private URI metric;
	
	@XmlAttribute(name = "Location")
	private Object location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URI getMetric() {
		return metric;
	}

	public void setMetric(URI metric) {
		this.metric = metric;
	}

	public Object getLocation() {
		return location;
	}

	public void setLocation(Object location) {
		this.location = location;
	}
}
