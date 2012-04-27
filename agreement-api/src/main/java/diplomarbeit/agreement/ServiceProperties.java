package diplomarbeit.agreement;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServiceProperties implements Term {
	@XmlAttribute(name = "ServiceName")
	private String serviceName;
	
	@XmlElementWrapper(name = "VariableSet")
	@XmlElement(name = "Variable")
	private Collection<Variable> variables;

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

	public Collection<Variable> getVariables() {
		return variables;
	}

	public void setVariables(Collection<Variable> variables) {
		this.variables = variables;
	}
}
