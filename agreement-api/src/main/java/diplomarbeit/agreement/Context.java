package diplomarbeit.agreement;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AgreementContextType")
@XmlRootElement(name = "Context")
public class Context {
	@XmlElement(name = "AgreementInitiator")
	private String initiator;
	
	@XmlElement(name = "AgreementResponder")
	private String responder;
	
	@XmlElement(name = "ServiceProvider", required = true)
	private AgreementRole serviceProvider;
	
	// TODO jaxb annotation
	private Date expirationTime;
	
	@XmlElement(name = "TemplateId")
	private String templateId;
	
	@XmlElement(name = "TemplateName")
	private String templateName;
	
	public Context() {
		this.serviceProvider = AgreementRole.AGREEMENT_RESPONDER;
	}
	
	public Context(Context other) {
		this.initiator = other.initiator;
		this.responder = other.responder;
		this.serviceProvider = other.serviceProvider;
		this.expirationTime = other.expirationTime;
		this.templateId = other.templateId;
		this.templateName = other.templateName;
	}
	
	public String getInitiator() {
		return this.initiator;
	}

	public String getResponder() {
		return responder;
	}

	public void setResponder(String responder) {
		this.responder = responder;
	}

	public AgreementRole getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(AgreementRole serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}
}
