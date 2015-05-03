package dijkstra.main.java.osm.o5mreader.datasets;

import java.util.List;
import dijkstra.main.java.osm.o5mreader.datasets.NWRDataSet;


public class Way extends NWRDataSet {

	private List<Long> nodes;

	public Way(long id) {
		super(id);
	}

	public List<Long> getNodes() {
		return nodes;
	}

	public void setNodes(List<Long> nodes) {
		this.nodes = nodes;
	}
	
}
