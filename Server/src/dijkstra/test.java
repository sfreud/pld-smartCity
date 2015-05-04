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
                        System.out.println("Lecture...");
                        Document carte=XMLDOM.lireDocument(docBuilder, "Villeurbanne.osm");
                        System.out.println("Doc lu");
                        Graph map = Graph.getGraph(XMLDOM.recupererNodes(carte),XMLDOM.recupererEdge(carte));
                        map.displayGraph();
		}
                catch(ParserConfigurationException e) {
			System.err.println("Impossible de créer un DocumentBuilder.");
			
		}
                catch (GraphException e)
                {
                    System.out.println(e.getMessage());
                }
                
		
    }
}

