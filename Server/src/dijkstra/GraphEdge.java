package dijkstra;

public  class GraphEdge {
    private Float weight;
    private GraphNode begin;    
    private GraphNode end;
    private String name;

    public GraphEdge(GraphNode b, GraphNode e, Float w, String n){
        begin = b;
        end = e; 
        weight = w;
        begin.addEdge(this);
        name = n;
    }
    
    public Float getWeight() {
        return weight;
    }
    public GraphNode getBegin() {
        return begin;
    }

    public GraphNode getEnd() {
        return end;
    }
    public String getName() {
        return name;
    }
    

}
