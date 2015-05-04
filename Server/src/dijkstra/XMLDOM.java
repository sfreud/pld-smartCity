/* ############################################################################
 * 
 * XMLDOM.java : création, écriture, lecture et parcours d'arbres XML en
 *               utilisant l'API DOM.
 * 
 * Auteur : Christophe Jacquet, Supélec
 * 
 * Historique
 * 2006-11-27  Création
 * 
 * ############################################################################
 */

package XMLParser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XMLDOM {
	/**
	 * Crée un petit document XML d'exemple, à l'aide d'un Document Builder.
	 * 
	 * @param docBuilder un document builder
	 * @return un document XML d'exemple, sous forme d'un objet DOM Document
	 */
	private static Document creerDocumentExemple(DocumentBuilder docBuilder) {
		Document doc = docBuilder.newDocument();
		
		Element racine = doc.createElement("root");
		racine.setAttribute("lang", "fr");
		doc.appendChild(racine);
		
		Element sujet = doc.createElement("node");
		sujet.setAttribute("role", "sujet");
		sujet.setTextContent("Supélec");
		racine.appendChild(sujet);
		
		Element verbe = doc.createElement("node");
		verbe.setAttribute("role", "verbe");
		verbe.setTextContent("est");
		racine.appendChild(verbe);
		
		Element complement = doc.createElement("node");
		complement.setAttribute("role", "complément de lieu");
		complement.setTextContent("en France");
		racine.appendChild(complement);
		
		return doc;
	}
	
	/**
	 * Écrit dans un fichier un document DOM, étant donné un nom de fichier.
	 * 
	 * @param doc le document à écrire
	 * @param nomFichier le nom du fichier de sortie
	 */
	private static void ecrireDocument(Document doc, String nomFichier) {
		// on considère le document "doc" comme étant la source d'une 
		// transformation XML
		Source source = new DOMSource(doc);
		
		// le résultat de cette transformation sera un flux d'écriture dans
		// un fichier
		Result resultat = new StreamResult(new File(nomFichier));
		
		// création du transformateur XML
		Transformer transfo = null;
		try {
			transfo = TransformerFactory.newInstance().newTransformer();
		} catch(TransformerConfigurationException e) {
			System.err.println("Impossible de créer un transformateur XML.");
			System.exit(1);
		}
		
		// configuration du transformateur
		
		// sortie en XML
		transfo.setOutputProperty(OutputKeys.METHOD, "xml");
		
		// inclut une déclaration XML (recommandé)
		transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		
		// codage des caractères : UTF-8. Ce pourrait être également ISO-8859-1
		transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		
		// idente le fichier XML
		transfo.setOutputProperty(OutputKeys.INDENT, "yes");
		
		try {
			transfo.transform(source, resultat);
		} catch(TransformerException e) {
			System.err.println("La transformation a échoué : " + e);
			System.exit(1);
		}
	}
	
	/**
	 * Lit un document XML à partir d'un fichier sur le disque, et construit
	 * le document DOM correspondant.
	 * 
	 * @param docBuilder une instance de DocumentBuilder
	 * @param nomFichier le fichier où est stocké le document XML
	 * @return un objet DOM Document correspondant au document XML 
	 */
	private static Document lireDocument(DocumentBuilder docBuilder, 
			String nomFichier) {
		
		try {
			return docBuilder.parse(new File(nomFichier));
		} catch(SAXException e) {
			System.err.println("Erreur de parsing de " + nomFichier);
		} catch (IOException e) {
			System.err.println("Erreur d'entrée/sortie sur " + nomFichier);
		}
		
		return null;
	}
	
	/**
	 * Affiche à l'écran un document XML fourni sous forme d'un objet DOM
	 * Document.
	 * 
	 * @param doc le document
	 */
	private static void afficherDocument(Document doc) {
		Element e = doc.getDocumentElement();
		afficherElement(e);
	}
	
	/**
	 * Affiche à l'écran un élément XML, ainsi que ses attributs, ses noeuds
	 * de texte, et ses sous-éléments.
	 * 
	 * @param e l'élément à afficher
	 */
	private static void afficherElement(Element e) {
		System.out.print("<" + e.getNodeName() + " ");
		
		NamedNodeMap attr = e.getAttributes();
		for(int i=0; i<attr.getLength(); i++) {
			Attr a = (Attr)attr.item(i);
			System.out.print(a.getName() + "=\"" + a.getNodeValue() + "\" ");
		}
		System.out.println(">");
		
		for(Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
			switch(n.getNodeType()) {
			case Node.ELEMENT_NODE:
				afficherElement((Element)n);
				break;
			case Node.TEXT_NODE:
				String data = ((Text)n).getData();
				System.out.print(data);
				break;
			}
		}
		System.out.println("</" + e.getNodeName() + ">");
	}
	
	public static void main(String[] args) {
		// obtention d'un Document Builder qui permet de créer de nouveaux
		// documents ou de parser des documents à partir de fichiers
		DocumentBuilder docBuilder = null;
		
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {
			System.err.println("Impossible de créer un DocumentBuilder.");
			System.exit(1);
		}
		/*
		// crée un petit document d'exemple
		Document doc = creerDocumentExemple(docBuilder);
		
		// l'écrire sur le disque dans un fichier
		ecrireDocument(doc, "test.xml");
		*/
		// re-charger ce document à partir du fichier
		Document doc2 = lireDocument(docBuilder, "C:\\Users\\tarik_000\\Desktop\\Villeurbanne.osm");
		if(doc2 == null) System.exit(1);

		afficherDocument(doc2);
	}
}
