/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstra;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Stéphane ROUX
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //Map<dep, Map<Arr,long>>
        //0 : 0, A : 1, B : 2, C : 3, D : 4, E : 5, T : 6
        Map<Long, Map<Long,Long>> edges = new HashMap<>();
        edges.put(new Long(0),new HashMap<Long,Long>());
        edges.get(new Long(0)).put(new Long(1), new Long(2));
        edges.get(new Long(0)).put(new Long(2), new Long(5));
        edges.get(new Long(0)).put(new Long(3), new Long(4));
        edges.put(new Long(1),new HashMap<Long,Long>());
        edges.get(new Long(1)).put(new Long(0), new Long(2));
        edges.get(new Long(1)).put(new Long(2), new Long(2));
        edges.get(new Long(1)).put(new Long(4), new Long(7));
        edges.put(new Long(2),new HashMap<Long,Long>());
        edges.get(new Long(2)).put(new Long(0), new Long(5));
        edges.get(new Long(2)).put(new Long(1), new Long(2));
        edges.get(new Long(2)).put(new Long(4), new Long(4));
        edges.get(new Long(2)).put(new Long(5), new Long(3));
        edges.get(new Long(2)).put(new Long(3), new Long(1));
        edges.put(new Long(3), new HashMap<Long,Long>());
        edges.get(new Long(3)).put(new Long(0), new Long(4));
        edges.get(new Long(3)).put(new Long(2), new Long(1));
        edges.get(new Long(3)).put(new Long(6), new Long(4));
        edges.put(new Long(4), new HashMap<Long,Long>());
        edges.get(new Long(4)).put(new Long(1), new Long(7));
        edges.get(new Long(4)).put(new Long(2), new Long(4));
        edges.get(new Long(4)).put(new Long(5), new Long(1));
        edges.get(new Long(4)).put(new Long(6), new Long(5));
        edges.put(new Long(5), new HashMap<Long,Long>());
        edges.get(new Long(5)).put(new Long(2), new Long(3));
        edges.get(new Long(5)).put(new Long(3), new Long(4));
        edges.get(new Long(5)).put(new Long(4), new Long(1));
        edges.get(new Long(5)).put(new Long(6), new Long(7));
        edges.put(new Long(6), new HashMap<Long,Long>());
        edges.get(new Long(6)).put(new Long(4), new Long(5));
        edges.get(new Long(6)).put(new Long(5), new Long(7));
        
        Graph g = null;
        
        try
         {
             
             g = new Graph(edges);
         
        g.displayGraph();
        
        //Sol : 0,1,2,4,
        
        //0 : 0, A : 1, B : 2, C : 3, D : 4, E : 5, T : 6
        List<GraphNode> way = g.getShorterWay(g.getNode(0), g.getNode(6)).first;
        for(GraphNode n : way)
        {
            System.out.print(n.getID()+"->");
        }
        System.out.println();
         }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
