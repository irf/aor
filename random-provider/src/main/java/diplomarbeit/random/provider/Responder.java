package diplomarbeit.random.provider;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import diplomarbeit.agreement.Agreement;
import diplomarbeit.agreement.GuaranteeTerm;
import diplomarbeit.agreement.KPITarget;
import diplomarbeit.agreement.OfferItem;
import diplomarbeit.agreement.Template;
import diplomarbeit.agreement.osgi.AgreementResponder;
import diplomarbeit.agreement.osgi.DecisionListener;

/**
 * Beispiel für die Implementierung einer dienstspezifischen Behandlung von Angeboten.
 * Es werden 3 verschiedene Vorlagen bereitgestellt (hinterlegt in src/main/resources). Angebotene
 * Vereinbarungen müssen einer dieser Vorlagen genügen. Angenommen werden außerdem nur Angebote,
 * in denen der Dienstnutzer eine Verfügbarkeit von weniger als 95% fordert.
 * 
 * @author Florian Blümel
 */
public class Responder implements AgreementResponder {
	private ArrayList<Template> templates;

	@Override
	public boolean decideOffer(Agreement offer, DecisionListener decision, boolean deferralAllowed) {
		// erstmal schauen, dass auch eins der Templates benutzt wurde.
		boolean basedOnTemplate = false;
		for (Template t : getTemplates())
			// FIXME für die Vorführung ist mir das mit den Constraints zu unzuverlässig.
			if (t.getId().equals(offer.getContext().getTemplateId()) /*&& constraintsHold(offer, t)*/) {
				basedOnTemplate = true;
				break;
			}
		if (!basedOnTemplate)
			return false;
		
		// reichlich schlampige Behandlung; für Testzwecke passts schon.
		Collection<GuaranteeTerm> references = offer.getTerms().getTerms(GuaranteeTerm.class);
		if (references.isEmpty())
			return false;
		KPITarget kpi = references.iterator().next().getObjective().getKPI();
		if (!kpi.getName().equals("Availability"))
			return false;
		
		// So. Nachdem jetzt aufs genaueste alle Voraussetzungen geprüft wurden :/ kann das Ding entschieden werden. 
		float target = kpi.getTarget();	
		if (target > 0 && target < 0.95)
			decision.onAcceptance();
		else
			decision.onRejection();
		
		return true;
	}

	@Override
	public void decideTerminationRequest(Agreement agreement, DecisionListener decision) {
		// Kündigen ist einfach immer erlaubt.
		decision.onAcceptance();
	}

	@Override
	public Collection<Template> getTemplates() {
		if (templates == null) {
			templates = new ArrayList<Template>();
			templates.add(JAXB.unmarshal(getClass().getResource("/template10.xml"), Template.class));
			templates.add(JAXB.unmarshal(getClass().getResource("/template90.xml"), Template.class));
			templates.add(JAXB.unmarshal(getClass().getResource("/template99.xml"), Template.class));
		}
		return templates;
	}

	@Override
	public void onStateChanged(Agreement agreement) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGuaranteeStateChanged(Agreement agreement, GuaranteeTerm guarantee) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onServiceStateChanged(Agreement agreement, String serviceName) {
		// TODO Auto-generated method stub
	}

	/**
	 * Testet, ob die von der Vorlage beschriebenen Einschränkungen im Angebot eingehalten werden.
	 * Diese Implementierung unterstützt einfache Wertebeschränkungen, die mit XMLSchema-restrictions
	 * formuliert sind. Der Bezug auf den zu beschränkenden Punkt wird (wie in WSAG vorgeschlagen)
	 * durch XPath hergestellt.
	 * 
	 * @see OfferItem
	 * 
	 * @param offer die angebotene Vereinbarung
	 * @param template die Vorlage
	 * @return
	 */
	boolean constraintsHold(Agreement offer, Template template) {
		// mangels vernünftiger JAXB-Doku: schwarze Magie
		// Der Plan ist, ein DOM-Objekt aus offer zu bauen, damit die XPath-Ausdrücke aus den
		// Constraints im Template auf irgendwas angewendet werden können
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Node document = builder.newDocument();
				try {
			Binder<Node> binder = JAXBContext.newInstance(Agreement.class).createBinder();
			binder.marshal(offer, document);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		// So. Aus "offer" ist jetzt "document" geworden. Jetzt die Constraints durchgehen und testen.
		Collection<OfferItem> constraints = template.getConstraints();
		for (OfferItem constraint : constraints) {
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes;
			try {
				nodes = (NodeList)xpath.evaluate(constraint.getLocation(), document, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			for (int i=0; i<nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				if (constraint.getItemConstraint() instanceof Element)
					if (!constraintsHold(node, (Element)constraint.getItemConstraint()))
						return false;
			}
		}
		return true;
	}
	
	/**
	 * Testet, ob ein Wert bestimmten Einschränkungen genügt.
	 * @param node ein XML-Knoten, dessen Textinhalt den zu beschränkenden Wert ausdrückt
	 * @param constraints Einschränkungen, formuliert als XMLSchema-Teildokument (xs:restriction)
	 * @return
	 */
	boolean constraintsHold(Node node, Element constraints) {
		// Einfach nur hardgecodet minimal XMLSchema verstehen. EXTREM zerbrechlich.
		for (int i=0; i!=constraints.getChildNodes().getLength(); ++i) {
			Node restrictions = constraints.getChildNodes().item(i);
			try {
				if (restrictions.getNodeType() != Node.ELEMENT_NODE)
					continue;
				if (!restrictions.getNodeName().equals("restriction") || !restrictions.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema"))
					continue;
				for (int j=0; j!=restrictions.getChildNodes().getLength(); ++i) {
					Node restriction = restrictions.getChildNodes().item(i);
					if (!restrictions.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema"))
						continue;
					float constraint = Float.valueOf(restriction.getAttributes().getNamedItem("value").getTextContent());
					float value = Float.valueOf(node.getTextContent());
					if (restrictions.getNodeName().equals("minInclusive") && value < constraint)
						return false;
					if (restrictions.getNodeName().equals("maxInclusive") && value > constraint)
						return false;
					if (restrictions.getNodeName().equals("minExclusive") && value <= constraint)
						return false;
					if (restrictions.getNodeName().equals("maxExclusive") && value <= constraint)
						return false;
				}
			}
			catch (Exception e) {
				// insbesondere fehlen oben allerlei Tests auf null ...
			}
		}
		return true;
	}
}
