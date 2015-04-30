/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstra;

import java.util.List;

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
        Graph g = new Graph(0);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 3);
        g.addEdge(0, 3, 2);
        g.addEdge(1, 5, 2);
        g.addEdge(1, 4, 2);
        g.addEdge(2, 1, 1);
        g.addEdge(3, 2, 1);
        g.addEdge(3, 4, 3);
        g.addEdge(3, 6, 2);
        g.addEdge(5, 4, 4);
        g.addEdge(5, 7, 7);
        g.addEdge(6, 5, 1);
        g.addEdge(6, 7, 6);
        g.displayGraph();
        
        //Sol : 0,3,6,7
        
        //E : 0, A : 1, B : 2, C : 3, G : 4, D : 5, F : 6, S : 7
        List<GraphNode> way = g.getShorterWay(g.getNode(0), g.getNode(7)).first;
        for(GraphNode n : way)
        {
            System.out.print(n.getID()+"->");
        }
        System.out.println();
    }
}
