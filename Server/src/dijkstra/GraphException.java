/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstra;

/**
 *
 * @author Stéphane ROUX
 */
public class GraphException extends Exception {
        public GraphException(){
            super("Incoherent graph");
        }
}
