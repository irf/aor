package diplomarbeit.agreement;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

//@XmlType(name = "TermCompositorType")
public class TermCompositor {
	@XmlElementRefs({
		@XmlElementRef(name = "All", type = All.class),
		@XmlElementRef(name = "OneOrMore", type = OneOrMore.class),
		@XmlElementRef(name = "ExactlyOne", type = ExactlyOne.class),
		@XmlElementRef(name = "ServiceDescriptionTerm", type = ServiceDescription.class),
		//@XmlElementRef(name = "ServiceProperties", type = ServiceProperties.class),
		@XmlElementRef(name = "ServiceReference", type = ServiceReference.class),
		@XmlElementRef(name = "GuaranteeTerm", type = GuaranteeTerm.class)
	})
	private Collection<Object> children = new ArrayList<Object>();
	
	private <TermType extends Term> void collect(ArrayList<TermType> terms, Class<TermType> type) {
		for (final Object child : children)
			if (type.isAssignableFrom(child.getClass()))
				terms.add(type.cast(child));
			else if (child instanceof TermCompositor)
				((TermCompositor)child).collect(terms, type);
	}
	
	public <TermType extends Term> Collection<TermType> getTerms(Class<TermType> type) {
		ArrayList<TermType> terms = new ArrayList<TermType>();
		collect(terms, type);
		return terms;
	}
	
	public void add(Term term) {
		children.add(term);
	}

	public void add(TermCompositor term) {
		children.add(term);
	}
}