package diplomarbeit.agreement;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;

public class BusinessValueList {
	@XmlElement(name = "Importance")
	private int importance;
	
	@XmlElement(name = "Penalty")
	private Collection<Penalty> penalties;

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public Collection<Penalty> getPenalties() {
		return penalties;
	}

	//public void setPenalties(Collection<Penalty> penalties) {
//		this.penalties = penalties;
	//}
}
