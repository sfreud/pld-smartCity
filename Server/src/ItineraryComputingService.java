import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.w3c.dom.Document;

import dijkstra.Graph;
import dijkstra.GraphException;
import dijkstra.GraphNode;
import dijkstra.XMLDOM;
import dijkstra.main.java.osm.o5mreader.Pair;


public class ItineraryComputingService extends Restlet {
	
	/*This class will be dedicated to shortest path computing.
	 * The implementation uses Dijkstra algorithm.
	 * 
	 * The exposed interface will be mapped to /itinerary, will take two parameters (departure point, arrival point)
	 * and will return a complete itinerary composed of an array of points and a duration.
	 * 
	 * 
	 */
	private Graph map;
	
	public ItineraryComputingService(String inputFile){
		map = prepareGraph(inputFile);
	}
	
	public Graph prepareGraph(String inputFile){
		DocumentBuilder docBuilder = null;
		
		System.out.println("Building graph.");
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        	Document carte=XMLDOM.lireDocument(docBuilder, inputFile);

            Map<Long, Pair<Float, Float>> nodes = XMLDOM.recupererNodes(carte);
            List<Pair<Long,Long>> edges = XMLDOM.recupererEdge(carte,nodes);

            //Checks for ways with one end outside of the map and for unreachable nodes, and delete them to preserve graph coherence.            
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
            
            
            return Graph.getGraph(nodes,edges);
		}
            catch(ParserConfigurationException e) {
			//System.out.println("Impossible de crÃ©er un DocumentBuilder.");
		} catch (GraphException e) {
            //System.out.println(e.getMessage());
        }
        return null;  
    }
	
	public void handle(Request request, Response response){
		
		
		String departureLatitude = getParamValue("dlat",request);
		String departureLongitude = getParamValue("dlong",request);
		String arrivalLatitude = getParamValue("alat",request);
		String arrivalLongitude = getParamValue("along",request);
		
		//convert the given String parameters to ...? (object representating gps coordinates)
		double dlatitude = Double.valueOf(departureLatitude);
		double dlongitude = Double.valueOf(departureLongitude);
		double alatitude = Double.valueOf(arrivalLatitude);
		double alongitude = Double.valueOf(arrivalLongitude);
		
		GraphNode departureNode = map.getNode(dlongitude, dlatitude);
		GraphNode arrivalNode = map.getNode(alongitude,alatitude);
		
		Pair<List<GraphNode>,Float> m = map.getShorterWay(departureNode, arrivalNode);
		StringBuilder sb = new StringBuilder();
        for(GraphNode g : m.getKey()){
        	sb.append(g.getID());
        	sb.append("->");
        }
        
        JSONObject obj=new JSONObject();
        JSONArray points = new JSONArray();
        try {
			obj.put("distance",m.getValue().toString());
			for(GraphNode g : m.getKey()){
	        	JSONObject p = new JSONObject();
	        	p.put("latitude", g.getLatitude());
	        	p.put("longitude", g.getLongitude());
	        	points.put(p);
	        }
			obj.put("points", points);

		} catch (JSONException e) {
		}
	
		response.setEntity(new StringRepresentation(obj.toString()));;
	}
	
	public String getParamValue(String param, Request request){
		String rs = request.toString();
		int i = rs.indexOf(param);
		String tmp = rs.substring(i+param.length()+1,rs.length());
		int j = tmp.indexOf("&");
		int k = i+param.length()+1;
		if(j==-1){
			//Dernier paramètre
			j = rs.substring(k,rs.length()).indexOf(" ");
			//System.out.println(rs.substring(k, k+j));
			return rs.substring(k, k+j);
		}
		else
			//System.out.println(rs.substring(k, k+j));
			return rs.substring(k, k+j);
	}

}
