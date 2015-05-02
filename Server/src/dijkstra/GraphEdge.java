package dijkstra;

public  class GraphEdge {
    private Float weight;
    private GraphNode begin;    
    private GraphNode end;
    
    public GraphEdge(GraphNode b, GraphNode e, Float w){
        begin = b;
        end = e; 
        weight = w;
        begin.addEdge(this);
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

}
