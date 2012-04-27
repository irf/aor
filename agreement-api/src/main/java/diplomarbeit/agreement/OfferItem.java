package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="Item")
public class OfferItem {
	@XmlAttribute(name = "Name")
	private String name;
	
	@XmlElement(name = "Location")
	private String location;		// XPath
	
	@XmlElement(name = "ItemConstraint")
	private Object itemConstraint;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Object getItemConstraint() {
		return itemConstraint;
	}

	public void setItemConstraint(Object itemConstraint) {
		this.itemConstraint = itemConstraint;
	}
}
