package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ServiceDescriptionTerm")
public class ServiceDescription implements Term {
	@XmlAttribute(name = "ServiceName", required = true)
	private String serviceName;

	@XmlAttribute(name = "Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
