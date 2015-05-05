/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstra;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
                        Document carte=XMLDOM.lireDocument(docBuilder, "essai.osm");
                        System.out.println("Doc lu");


                        Map<Long, Pair<Float, Float>> nodes = XMLDOM.recupererNodes(carte);
                        List<Pair<Pair<Long,Long>,String>> edges = XMLDOM.recupererEdge(carte,nodes);

                        
                        List<Pair<Pair<Long,Long>,String>> edgesToDelete = new ArrayList<>();
                        List<Long> nodesToDelete = new ArrayList<>();
                        for(Pair<Pair<Long,Long>,String> p : edges)
                        {
                            if(!nodes.containsKey(p.getKey().getKey())||!nodes.containsKey(p.getKey().getValue()))
                            {
                                edgesToDelete.add(p);
                            }
                        }
                        for(Pair<Pair<Long,Long>,String> p : edgesToDelete)
                        {
                            edges.remove(p);
                        }
                        for(Long n : nodes.keySet())
                        {
                            boolean toDelete = true;
                            for(Pair<Pair<Long,Long>,String> p : edges)
                            {
                                if(p.getKey().getKey().equals(n)||p.getKey().getValue().equals(n))
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
                        GraphNode n1 = map.getNode(1);
                        GraphNode n2 = map.getNode(10);
                        System.out.println(map.getShorterWay(n1, n2).getKey().size());
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

