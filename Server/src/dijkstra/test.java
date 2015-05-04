/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstra;

import dijkstra.main.java.osm.o5mreader.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
/**
 *
 * @author Stéphane ROUX
 */
public class test {

    /**
     * @param args the command line arguments
     */
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
		Document doc2 = XMLDOM.lireDocument(docBuilder, "C:\\Users\\tarik_000\\Desktop\\Villeurbanne.osm");
		if(doc2 == null) System.exit(1);

		XMLDOM.afficherDocument(doc2);
    }
}

