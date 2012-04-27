package diplomarbeit.agreement;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlElementRef;
//import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

// ein Template IST KEIN Agreement, drum hier keine Vererbung.
@XmlRootElement(name = "Template")
public class Template {
	@XmlAttribute(name = "TemplateId", required = true)
	private String id;
	
	@XmlElement(name = "Name")
	private String name;
	
	@XmlElement(name = "Context", required = true)
	private Context context = new Context();
	
	@XmlElement(name = "Terms", required = true)
	private All terms = new All();
	
	/*@XmlElementWrapper(name = "CreationConstraints")
	@XmlElementRefs({
		@XmlElementRef(name = "Item", type = OfferItem.class),
		@XmlElementRef(name = "Constraint", type = Constraint.class)
	})
	private Collection<Object> constraints;*/
	@XmlElement(name = "Item")
	@XmlElementWrapper(name = "CreationConstraints")
	private Collection<OfferItem> constraints;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Context getContext() {
		return context;
	}
	public All getTerms() {
		return terms;
	}
	public Collection<OfferItem> getConstraints() {
		return constraints;
	}
	public void setConstraints(Collection<OfferItem> constraints) {
		this.constraints = constraints;
	}
}
