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
import java.util.ArrayList;
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
                        Document carte=XMLDOM.lireDocument(docBuilder, "map.osm");
                        System.out.println("Doc lu");


                        Map<Long, Pair<Float, Float>> nodes = XMLDOM.recupererNodes(carte);
                        List<Pair<Long,Long>> edges = XMLDOM.recupererEdge(carte,nodes);

                        
                        List<Pair<Long,Long>> edgesToDelete = new ArrayList<>();
                        List<Long> nodesToDelete = new ArrayList<>();
                        for(Pair<Long,Long> p : edges)
                        {
                            if(!nodes.containsKey(p.getKey())||!nodes.containsKey(p.getValue()))
                            {
                                edgesToDelete.add(p);
                            }
                        }
                        for(Pair<Long,Long> p : edgesToDelete)
                        {
                            edges.remove(p);
                        }
                        for(Long n : nodes.keySet())
                        {
                            boolean toDelete = true;
                            for(Pair<Long,Long> p : edges)
                            {
                                if(p.getKey().equals(n)||p.getValue().equals(n))
                                {
                                    toDelete = false;
                                }
                            }
                            if(toDelete)
                            {
                                nodesToDelete.add(n);
                            }
                        }
                        for(Long n : nodesToDelete)
                        {
                            nodes.remove(n);
                        }
                        Graph map = Graph.getGraph(nodes,edges);
 
                        map.displayGraph();
                        System.out.println(map.getShorterWay(map.getNode(206997177), map.getNode(207724089)).getKey().size());
		}
                catch(ParserConfigurationException e) {
			System.err.println("Impossible de créer un DocumentBuilder.");
			
		}
                catch (GraphException e)
                {
                    System.out.println(e.getMessage());
                }
                catch (IndexOutOfBoundsException e)
                {
                    System.out.println(e.getMessage());
                }
                
    }
}

