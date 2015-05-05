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
		String departureLatitude = getQuery().getValues("dlat");
		String departureLongitude = getQuery().getValues("dlong");
		String arrivalLatitude = getQuery().getValues("alat");
		String arrivalLongitude = getQuery().getValues("along");
		
		//convert the given String parameters to ...? (object representating gps coordinates)
		double dlatitude = Double.valueOf(departureLatitude);
		double dlongitude = Double.valueOf(departureLongitude);
		double alatitude = Double.valueOf(arrivalLatitude);
		double alongitude = Double.valueOf(arrivalLongitude);
		
		
		//call to the class implementing the calculation, with parameters
		
		
		
		
		//the results are returned to the client
		return "test";
	}

}
