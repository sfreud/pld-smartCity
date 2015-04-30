package dijkstra;

public  class GraphEdge {
    private Long weight;
    private GraphNode begin;    
    private GraphNode end;
    
    public GraphEdge(GraphNode b, GraphNode e, Long w){
        begin = b;
        end = e; 
        weight = w;
        begin.addEdge(this);
    }
    
    public Long getWeight() {
        return weight;
    }
    public GraphNode getBegin() {
        return begin;
    }

    public GraphNode getEnd() {
        return end;
    }

}
