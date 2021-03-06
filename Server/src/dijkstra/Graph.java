package dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Graph {
    private static final float rayon = 6371000;
    private GraphNode begin;
    //Map<IdNoeud,Pair<Lat,Long>>
    public static Graph getGraph(Map<Long, Pair<Float,Float>> coor, List<Pair<Pair<Long,Long>,String>> e) throws GraphException, IndexOutOfBoundsException{
        if(coor.isEmpty()||e.isEmpty())
        {
            throw new GraphException();
        }
        for(Pair<Pair<Long,Long>,String> w : e)
        {
            System.out.println("Chargement coordonnées...");
            if(!coor.containsKey(w.getKey().getKey())||!coor.containsKey(w.getKey().getValue()))
            {
                throw new GraphException();
            }
        }
        Map<Long, Map<Long,Pair<Float,String>>> edges = new HashMap<>();
        for(Long nodeDep : coor.keySet())
        {
            System.out.println("Chargement chemins...");
            edges.put(nodeDep, new HashMap<Long,Pair<Float,String>>());
            for(Pair<Pair<Long,Long>,String> w : e)
            {
                if(w.getKey().getKey().equals(nodeDep))
                {
                    double latA = coor.get(w.getKey().getKey()).getKey()*Math.PI/180;
                    double latB = coor.get(w.getKey().getValue()).getKey()*Math.PI/180;
                    double longA = coor.get(w.getKey().getKey()).getValue()*Math.PI/180;
                    double longB = coor.get(w.getKey().getValue()).getValue()*Math.PI/180;
                    double dlat = Math.abs(latA-latB);
                    double dlong = Math.abs(longA-longB);
                    double dy = rayon*dlat;
                    double dx = rayon*Math.cos(latA)*dlong;
                    int dist = (int) Math.sqrt(dx*dx+dy*dy);
                    Pair<Float,String> carac = new Pair<>(new Float(dist),w.getValue());
                    edges.get(w.getKey().getKey()).put(w.getKey().getValue(),carac);
                }
            }
        }
        return new Graph(edges, coor);
    }
    private Graph(Map<Long, Map<Long,Pair<Float,String>>> edges, Map<Long, Pair<Float,Float>> coor) throws GraphException {
        if(edges.isEmpty())
        {
            throw new GraphException();
        }
        
        for(Long nodeToVerify : edges.keySet())
        {
            boolean weCanGoTo = false;
            for(Long nodeBeg : edges.keySet())
            {
                for(Long nodeArr : edges.get(nodeBeg).keySet())
                {
                    System.out.println("Chargement...");
                    if(nodeToVerify.equals(nodeArr))
                    {
                        weCanGoTo = true;
                    }
                }
            }
            if(!weCanGoTo)
            {
                System.out.println("Noeud "+nodeToVerify+" inatteignable");
                throw new GraphException();
            }
        }
        
        for(Long nodeBeg : edges.keySet())
        {
            for(Long nodeArr : edges.get(nodeBeg).keySet())
            {
                System.out.println("Chargement...");
                if(!edges.keySet().contains(nodeArr))
                {
                    System.out.println("Noeud d'arrivée "+nodeArr+" non présent en clé");
                    throw new GraphException();
                }
            }
        }
        begin = new GraphNode((Long)edges.keySet().toArray()[0],coor.get((Long)edges.keySet().toArray()[0]).getKey(),coor.get(((Long)edges.keySet().toArray()[0])).getValue());
        List<Long> nodesBlack = new ArrayList<>();
        List<Long> nodesGrey = new ArrayList<>();
        nodesGrey.add((Long)edges.keySet().toArray()[0]);
        while(!nodesGrey.isEmpty())
        {
           System.out.println("Chargement...");
           Long cur = nodesGrey.get(0);
           System.out.println("On ajoute le noeud " + cur);
           Map<Long,Pair<Float,String>> edgesLeaving = edges.get(cur);
           if(edgesLeaving == null)
           {
               System.out.println("Noeud " + cur + " sans chemins partants");
               throw new GraphException();
           }
           for(Long arr : edgesLeaving.keySet())
           {
               System.out.println("Chargement...");
               if(!this.addEdge(cur, arr, edgesLeaving.get(arr).getKey(),edgesLeaving.get(arr).getValue(), coor))
               {
                   System.out.println("Impossible d'ajouter un chemin de "+cur+" à "+arr);
                   throw new GraphException();
               }
               System.out.println("Ajout noeud("+coor.get(arr).getKey()+","+coor.get(arr).getValue()+")");
               if(!nodesGrey.contains(arr)&&!nodesBlack.contains(arr))
               {
                     nodesGrey.add(arr);
               }
           }
           nodesGrey.remove(cur);
           nodesBlack.add(cur);
        }
        
        
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
    
    public final boolean addEdge(long nbBegin, long nbEnd, float length, String name, Map<Long, Pair<Float,Float>> coor)
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
            if(!coor.containsKey(nbEnd))
            {
                return false;
            }
            nodeEnd = new GraphNode(nbEnd, coor.get(nbEnd).getKey(), coor.get(nbEnd).getValue());
        }
        new GraphEdge(nodeBegin,nodeEnd,length, name);
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
                System.out.println(dep.getID().toString()+"("+dep.getLatitude()+","+dep.getLongitude()+")"+" ->("+leav.getWeight().toString()+","+leav.getName()+") "+leav.getEnd().getID().toString()+"("+leav.getEnd().getLatitude()+","+leav.getEnd().getLongitude()+")");

            }
        }
    }
    
    public Pair<List<GraphNode>, Float> getShorterWay(GraphNode begin, GraphNode end)
    {
        return DijkstraUnit.dijkstraAlgorithm(begin, end, this);
    }
    
    public final GraphNode getNode(long ID)
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
    
    public final GraphNode getNode(double lo, double la)
    {
        lo = ((int)(lo*Math.pow(10, 7)))/Math.pow(10, 7);
        la = ((int)(la*Math.pow(10, 7)))/Math.pow(10, 7);
        GraphNode ret = null;
        List<GraphNode> greyNodes = new ArrayList<>();
        List<GraphNode> blackNodes = new ArrayList<>();
        greyNodes.add(begin);
        if(Math.abs(begin.getLongitude()-lo)<Math.pow(10, -5) &&Math.abs(begin.getLatitude()-la)<Math.pow(10, -5))
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
                        if(Math.abs(n.getLongitude()-lo)<Math.pow(10, -5) &&Math.abs(n.getLatitude()-la)<Math.pow(10, -5))
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
