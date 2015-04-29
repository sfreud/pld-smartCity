package com.example.dijkstra;

import java.util.ArrayList;
import java.util.List;

public abstract class Graph {

    protected List<GraphNode> graphNodes;
    protected List<GraphEdge> graphEdges;

    public Graph() {
        graphNodes = new ArrayList<GraphNode>();
        graphEdges = new ArrayList<GraphEdge>();
    }

    public List<GraphNode> getGraphNodes() {
        return graphNodes;
    }

    public List<GraphEdge> getGraphEdges() {
        return graphEdges;
    }
}
