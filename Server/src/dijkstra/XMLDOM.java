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

package dijkstra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import dijkstra.main.java.osm.o5mreader.Pair;
//Attention : certains noeuds d'arrivée ne sont pas dans les noeuds de départ
public class XMLDOM {
	/**
	 * Crée un petit document XML d'exemple, à l'aide d'un Document Builder.
	 * 
	 * @param docBuilder un document builder
	 * @return un document XML d'exemple, sous forme d'un objet DOM Document
	 */
	public static Document creerDocumentExemple(DocumentBuilder docBuilder) {
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
	public static void ecrireDocument(Document doc, String nomFichier) {
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
	public static Document lireDocument(DocumentBuilder docBuilder, 
			String nomFichier) {
		
		try {
                    System.out.println("Avant parse...");
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
	public static void afficherDocument(Document doc) {
		Element e = doc.getDocumentElement();
		afficherElement(e);
	}
	
	/**
	 * Affiche à l'écran un élément XML, ainsi que ses attributs, ses noeuds
	 * de texte, et ses sous-éléments.
	 * 
	 * @param e l'élément à afficher
	 */
	public static void afficherElement(Element e) {
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
	
	public static Map<Long, Pair<Float, Float>> recupererNodes(Document doc)
	{
            System.out.println("récup nodes");
		Map<Long, Pair<Float, Float>> ret = new HashMap<>();
		NodeList listeNoeud = doc.getElementsByTagName("node");
		for(int i=0; i<listeNoeud.getLength(); i++){
			Element e = (Element) listeNoeud.item(i);
			Long id = Long.parseLong(e.getAttribute("id"));
                        System.out.println("récup node" + id);
			Float lat = Float.parseFloat(e.getAttribute("lat"));
			Float lon = Float.parseFloat(e.getAttribute("lon"));
			Pair<Float,Float> next = new Pair<>(lat,lon);
			ret.put(id, next);
		}
		return ret;
	}
	
	public static List<Pair<Long,Long>> recupererEdge(Document doc, Map<Long, Pair<Float, Float>> noeuds) {
            System.out.println("récup edges");
		List<Pair<Long,Long>> ret = new ArrayList<>();
		NodeList listeWay = doc.getElementsByTagName("way");
		for(int i=0; i<listeWay.getLength(); i++){
                        System.out.println("récup edge " + i);
			Element e = (Element) listeWay.item(i);
			NodeList listeNoeud = e.getElementsByTagName("nd");
			for(int j=0; j<listeNoeud.getLength()-1; j++){
				Element e2 = (Element) listeNoeud.item(j);
				Long idDep = Long.parseLong(e2.getAttribute("ref"));
				Element e3 = (Element) listeNoeud.item(j+1);
				Long idArr = Long.parseLong(e3.getAttribute("ref"));
				// V�rification si les id des deux noeuds font partie de la carte
				if(noeuds.containsKey(idDep) && noeuds.containsKey(idArr)) {
					Pair<Long,Long> next = new Pair<>(idDep,idArr);
					Pair<Long,Long> reverse = new Pair<>(idArr,idDep);
					ret.add(next);
					ret.add(reverse);
				}
				
			}
		}
		return ret;
	}
}
