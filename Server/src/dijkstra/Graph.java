package dijkstra;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private GraphNode begin;
    public Graph(int IDbegin) {
        begin = new GraphNode(IDbegin);
    }

    public List<GraphNode> getGraphNodes(){
        List<GraphNode> greyNodes = new ArrayList<>();
        List<GraphNode> blackNodes = new ArrayList<>();
        List<GraphNode> ret = new ArrayList<>();
        greyNodes.add(begin);
        ret.add(begin);
        while(!greyNodes.isEmpty()){
            GraphNode cur = greyNodes.get(0);
            List<GraphNode> sons = cur.getSons();
            for(GraphNode n : sons){
                if(!(greyNodes.contains(n))&&!(blackNodes.contains(n)))
                {
                    greyNodes.add(n);
                    ret.add(n);
                }
            }
            greyNodes.remove(cur);
            blackNodes.add(cur);
        }
        
        return ret;
    }
    
    public long getBiggestNumberNode()
    {
        long max = begin.getID();
        List<GraphNode> greyNodes = new ArrayList<>();
        List<GraphNode> blackNodes = new ArrayList<>();
        greyNodes.add(begin);
        while(!greyNodes.isEmpty()){
            GraphNode cur = greyNodes.get(0);
            List<GraphNode> sons = cur.getSons();
            for(GraphNode n : sons){
                if(!(greyNodes.contains(n))&&!(blackNodes.contains(n)))
                {
                    greyNodes.add(n);
                    if(n.getID()>max)
                    {
                        max=n.getID();
                    }
                }
            }
            greyNodes.remove(cur);
            blackNodes.add(cur);
        }
        return max;
    }
    
    public boolean addEdge(long nbBegin, long nbEnd, long length)
    {
        GraphNode nodeBegin = null;
        GraphNode nodeEnd = null;
        List<GraphNode> greyNodes = new ArrayList<>();
        List<GraphNode> blackNodes = new ArrayList<>();
        greyNodes.add(begin);
        if(begin.getID()==nbBegin)
        {
            nodeBegin=begin;
        }
        if(begin.getID()==nbEnd)
        {
            nodeEnd=begin;
        }    
        while(!greyNodes.isEmpty()){
            GraphNode cur = greyNodes.get(0);
            List<GraphNode> sons = cur.getSons();
            for(GraphNode n : sons){
                if(!(greyNodes.contains(n))&&!(blackNodes.contains(n)))
                {
                    greyNodes.add(n);
                    if(n.getID()==nbBegin)
                    {
                        nodeBegin=n;
                    }
                    if(n.getID()==nbEnd)
                    {
                        nodeEnd=n;
                    }
                    
                }
            }
            greyNodes.remove(cur);
            blackNodes.add(cur);
        }
        
        if(nodeBegin == null)
        {
            return false;
        }
        if(nodeEnd == null)
        {
            nodeEnd = new GraphNode(nbEnd);
        }
        new GraphEdge(nodeBegin,nodeEnd,length);
        return true;
        
        
    }
    
    public void displayGraph()
    {
        List<GraphNode> listNodes = this.getGraphNodes();
        for(GraphNode dep : listNodes)
        {
            List<GraphEdge> listEdge = dep.getEdgeLeaving();
            for(GraphEdge leav : listEdge)
            {
                System.out.println(dep.getID().toString()+" ->("+leav.getWeight().toString()+") "+leav.getEnd().getID().toString());

            }
        }
    }
    
    public Pair<List<GraphNode>, Long> getShorterWay(GraphNode begin, GraphNode end)
    {
        return DijkstraUnit.dijkstraAlgorithm(begin, end, this);
    }
    
    public GraphNode getNode(long ID)
    {
        GraphNode ret = null;
        List<GraphNode> greyNodes = new ArrayList<>();
        List<GraphNode> blackNodes = new ArrayList<>();
        greyNodes.add(begin);
        if(begin.getID()==ID)
        {
            ret=begin;
        }
        else
        {
            while(!greyNodes.isEmpty()){
                GraphNode cur = greyNodes.get(0);
                List<GraphNode> sons = cur.getSons();
                for(GraphNode n : sons){
                    if(!(greyNodes.contains(n))&&!(blackNodes.contains(n)))
                    {
                        greyNodes.add(n);
                        if(n.getID()==ID)
                        {
                            ret=n;
                        }
                        

                    }
                }
                greyNodes.remove(cur);
                blackNodes.add(cur);
            }
        }
        return ret;
        
        
    }
    
}
