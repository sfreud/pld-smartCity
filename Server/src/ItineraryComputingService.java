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
import org.restlet.representation.StringRepresentation;
import org.w3c.dom.Document;

import dijkstra.Graph;
import dijkstra.GraphException;
import dijkstra.GraphNode;
import dijkstra.Pair;
import dijkstra.XMLDOM;


public class ItineraryComputingService extends Restlet {
	
	/*This class will be dedicated to shortest path computing.
	 * The implementation uses Dijkstra algorithm.
	 * 
	 * The exposed interface will be mapped to /itinerary, will take four parameters (departure and arrival points coordinates)
	 * and will return a complete itinerary composed of an array of points and a duration.
	 */
	private Graph map;
	
	public ItineraryComputingService(String inputFile){
		map = prepareGraph(inputFile);
	}
	
	public Graph prepareGraph(String inputFile){
		DocumentBuilder docBuilder = null;
		
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        System.out.println("Lecture...");
                        Document carte=XMLDOM.lireDocument(docBuilder, inputFile);
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
            
            return Graph.getGraph(nodes,edges);
		}
            catch(ParserConfigurationException e) {
			//System.out.println("Impossible de créer un DocumentBuilder.");
		} catch (GraphException e) {
            //System.out.println(e.getMessage());
        }
        return null;  
    }
	
	public void handle(Request request, Response response){
		
		//get parameters from the request
		String departureLatitude = getParamValue("dlat",request);
		String departureLongitude = getParamValue("dlong",request);
		String arrivalLatitude = getParamValue("alat",request);
		String arrivalLongitude = getParamValue("along",request);
		
		//convert the given String parameters
		double dlatitude = Double.valueOf(departureLatitude);
		double dlongitude = Double.valueOf(departureLongitude);
		double alatitude = Double.valueOf(arrivalLatitude);
		double alongitude = Double.valueOf(arrivalLongitude);
		
		//retrieve the nodes in the graph associated with the given coordinates
		GraphNode departureNode = map.getNode(dlongitude, dlatitude);
		GraphNode arrivalNode = map.getNode(alongitude,alatitude);
        //GraphNode n1 = map.getNode(4.8785753,45.7738037);
        //GraphNode n2 = map.getNode(4.8788661,45.7736091);
		//do the actual path computing
		Pair<List<GraphNode>,Float> m = map.getShorterWay(departureNode, arrivalNode);
		
		//map the result to a JSON object
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
        //return the JSON object
		response.setEntity(new StringRepresentation(obj.toString()));;
	}
	
	public String getParamValue(String param, Request request){
		String rs = request.toString();
		int i = rs.indexOf(param);
		String tmp = rs.substring(i+param.length()+1,rs.length());
		int j = tmp.indexOf("&");
		int k = i+param.length()+1;
		if(j==-1){
			//last parameter (no & after)
			j = rs.substring(k,rs.length()).indexOf(" ");
			return rs.substring(k, k+j);
		}
		else
			return rs.substring(k, k+j);
	}

}
