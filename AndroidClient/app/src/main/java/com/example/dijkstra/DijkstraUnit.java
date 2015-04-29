package com.example.dijkstra;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DijkstraUnit {

	public static List<GraphNode> dijkstraAlgorithm(GraphNode start, GraphNode end, Graph graph) {
        List<GraphNode> nodes = graph.getGraphNodes();
		Map<GraphNode, Long> seen = new HashMap<GraphNode, Long>();
		Map<GraphNode, GraphNode> previous = new HashMap<GraphNode, GraphNode>();

		for (GraphNode n : nodes) {
			seen.put(n, (Long.MAX_VALUE)/2);
			previous.put(n, start);
		}

		seen.put(start, (long) 0);

		List<GraphNode> notSeenYet = new ArrayList<GraphNode>();
		notSeenYet.addAll(nodes);
		while (!notSeenYet.isEmpty()) {
			GraphNode n1 = minimum(seen, notSeenYet);
			notSeenYet.remove(n1);
			
			List<GraphNode> sons = n1.getSons();
			for (GraphNode n2 : sons) {
				GraphEdge edgeP1P2 = n1.getGraphEdgeWith(n2);
				long newWeight = seen.get(n1) + edgeP1P2.getWeight();
				if (seen.get(n2) > newWeight) {
					seen.put(n2, newWeight);
					previous.put(n2, n1);
				}
			}
		}

		List<GraphNode> path = new ArrayList<GraphNode>();

		GraphNode n = end;

		while (n != start) {
			path.add(0, n);
			n = previous.get(n);
		}
		path.add(0, start);

		return path;
	}

	private static GraphNode minimum(Map<GraphNode, Long> seen, List<GraphNode> nodes) {
		long minValue = seen.get(nodes.get(0));
		GraphNode minNode = nodes.get(0);

		for (GraphNode n : nodes) {
			long seenNode = seen.get(n);
			if (minValue > seenNode) {
				minValue = seenNode;
				minNode = n;
			}
		}
		return minNode;
	}
}
