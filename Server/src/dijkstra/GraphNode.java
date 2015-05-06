package dijkstra;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {

    private Long ID;
    private Double longitude;
    private Double latitude;
    private List<GraphEdge> edgeLeaving;
    public Long getID() {
        return ID;
    }
    
     public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
    
    public GraphNode(long i, double lat,double longi){
        ID=i;
        longitude = ((int)(longi*Math.pow(10, 7)))/Math.pow(10, 7);
        latitude= ((int)(lat*Math.pow(10, 7)))/Math.pow(10, 7);
        edgeLeaving = new ArrayList<>();
    }
    
    public void addEdge(GraphEdge e){
        edgeLeaving.add(e);
    }
    public List<GraphNode> getSons() {
        List<GraphNode> sons = new ArrayList<>();
        for (GraphEdge e : edgeLeaving){
            sons.add(e.getEnd());
        }
        return sons;
    }
    
    public List<GraphEdge> getEdgeLeaving() {
        
        return edgeLeaving;
    }
    
    public GraphEdge getGraphEdgeWith(GraphNode n2) {
        for (GraphEdge e : edgeLeaving){
            if(e.getEnd()==n2)
            {
                return e;
            }
        }
        return null;
    }
}
