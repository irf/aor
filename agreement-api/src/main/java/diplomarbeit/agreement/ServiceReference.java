package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "ServiceReferenceType")
@XmlRootElement(name = "ServiceReference")
public class ServiceReference implements Term {
	@XmlAttribute(name = "ServiceName")
	private String serviceName;
	
	@XmlValue
	private String reference;

	@XmlAttribute(name = "Name")
	private String name;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
