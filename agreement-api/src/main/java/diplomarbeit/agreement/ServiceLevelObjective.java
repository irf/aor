package diplomarbeit.agreement;

import javax.xml.bind.annotation.XmlElement;

public class ServiceLevelObjective {
	@XmlElement(name = "KPITarget")
	private KPITarget kpi;

	public KPITarget getKPI() {
		return kpi;
	}

	public void setKPI(KPITarget kpi) {
		this.kpi = kpi;
	}
}
