package diplomarbeit.agreement;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * Von JAXB geforderte Factory.
 * @author Florian Blümel
 */
@XmlRegistry
public class ObjectFactory {
	//http://cxf.547215.n5.nabble.com/DOSGi-wsdl-first-approach-td3212289.html
	private final static String NAMESPACE = "http://schemas.ggf.org/graap/2007/03/ws-agreement";
	private final static QName AGREEMENT = new QName(NAMESPACE, "Agreement");
	private final static QName TEMPLATE = new QName(NAMESPACE, "Template");
	private final static QName CONTEXT = new QName(NAMESPACE, "Context");
	private final static QName ALL = new QName(NAMESPACE, "All");
	private final static QName ONE_OR_MORE = new QName(NAMESPACE, "OneOrMore");
	private final static QName EXACTLY_ONE = new QName(NAMESPACE, "ExactlyOne");
	private final static QName SERVICE_REFERENCE = new QName(NAMESPACE, "ServiceReference");

    @XmlElementDecl(name = "Agreement")
    public JAXBElement<Agreement> createAgreement(Agreement value) {
        return new JAXBElement<Agreement>(AGREEMENT, Agreement.class, null, value);
    }

    @XmlElementDecl(name = "Template")
    public JAXBElement<Template> createTemplate(Template value) {
        return new JAXBElement<Template>(TEMPLATE, Template.class, null, value);
    }

    @XmlElementDecl(name = "Context")
    public JAXBElement<Context> createContext(Context value) {
        return new JAXBElement<Context>(CONTEXT, Context.class, null, value);
    }

    @XmlElementDecl(name = "All")
    public JAXBElement<All> createAll(All value) {
        return new JAXBElement<All>(ALL, All.class, null, value);
    }

    @XmlElementDecl(name = "OneOrMore")
    public JAXBElement<OneOrMore> createOneOrMore(OneOrMore value) {
        return new JAXBElement<OneOrMore>(ONE_OR_MORE, OneOrMore.class, null, value);
    }
    
    @XmlElementDecl(name = "ExactlyOne")
    public JAXBElement<ExactlyOne> createExactlyOne(ExactlyOne value) {
        return new JAXBElement<ExactlyOne>(EXACTLY_ONE, ExactlyOne.class, null, value);
    }

    @XmlElementDecl(name = "ServiceReference")
    public JAXBElement<ServiceReference> createServiceReference(ServiceReference value) {
        return new JAXBElement<ServiceReference>(SERVICE_REFERENCE, ServiceReference.class, null, value);
    }
}
