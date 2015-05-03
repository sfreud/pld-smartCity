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
/**
 *
 * @author Stéphane ROUX
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        O5MHandler file = new O5MHandler();
        try {
            O5MReader toRead = new O5MReader(new FileInputStream(new File("Villeurbanne.osm")));
            toRead.read(file);
        } catch (FileNotFoundException ex) {
            System.out.println("Erreur fichier");
        } catch (IOException ex) {
            System.out.println("Erreur fichier");
        } 
    }
}

