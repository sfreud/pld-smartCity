package dijkstra.main.java.osm.o5mreader.datasets;

import dijkstra.main.java.osm.o5mreader.datasets.NWRDataSet.BaseObjectTypes;
import dijkstra.main.java.osm.o5mreader.datasets.DataSet;

public class SkipDS extends DataSet {
	
	private BaseObjectTypes type;
	
	private byte[] bytes;

	public BaseObjectTypes getType() {
		return type;
	}

	public void setType(BaseObjectTypes type) {
		this.type = type;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public SkipDS(BaseObjectTypes type, byte[] bytes) {
		super();
		this.type = type;
		this.bytes = bytes;
	}
	
	
}
