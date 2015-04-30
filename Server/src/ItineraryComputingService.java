import org.restlet.resource.Get;


public class ItineraryComputingService extends org.restlet.resource.ServerResource{
	
	/*This class will be dedicated to shortest path computing.
	 * The implementation uses Dijkstra algorithm.
	 * 
	 * The exposed interface will be mapped to /itinerary, will take two parameters (departure point, arrival point)
	 * and will return a complete itinerary composed of an array of points and a duration.
	 * 
	 * 
	 */
	@Get
	public String represent(){
		return "test";
	}

}
