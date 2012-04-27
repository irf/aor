package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import diplomarbeit.agreement.Context;

/**
 * Repr�sentiert eine Dienstg�tevereinbarung. Bietet als Datenmodell den in WSAgreement
 * beschriebenen Umfang; durch JAXB-Annotationen wird eine Serialisierung zum WSAgreement-
 * XML-Dokument erm�glicht.
 * 
 * Realisiert NICHT die REST-Schnittstelle; diese wird in AgreementService beschrieben und im
 * Bundle agreement-responder umgesetzt.
 * @see AgreementService
 * @author Florian Bl�mel
 */
@XmlType(name = "AgreementType")
@XmlRootElement(name = "Agreement")
public class Agreement {
	@XmlAttribute(name = "AgreementId", required = true)
	private String id;
	
	@XmlElement(name = "Name")
	private String name;
	
	@XmlElement(name = "Context", required = true)
	private Context context = new Context();
	
	@XmlElement(name = "Terms", required = true)
	private All terms = new All();
	
	@SuppressWarnings("unused")
	private Agreement() {
		// doofer ctor f�r JAXB
		this("(no id)");
	}
	
	public Agreement(String id) {
		this.setId(id);
	}
	
	public Agreement(String id, String name) {
		this(id);
		this.setName(name);
	}
	
	public Agreement(Template t, String id, String name) {
		this(id, name);
		this.context = new Context(t.getContext());
		this.context.setTemplateId(t.getId());
		this.context.setTemplateName(t.getName());
		this.terms = t.getTerms();	// FIXME kopieren/klonen
	}
	
	public String getId() {
		return this.id;
	}
	
	private void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Context getContext() {
		return this.context;
	}

	public All getTerms() {
		return terms;
	}
}
