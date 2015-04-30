import org.restlet.Component;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;


/* Main class.
 * Set the port, the services mapping, the auth methods here.
 * See each *Service class for services implementation.
 * 
 */


public class ServerController {
	public static void main(String[] args){
		
		//set the logging config file
		System.setProperty("java.util.logging.config.file",
		        "./libs/logging.properties");
		//If log files are missing, check the location of the config file. 
		//Note : it requires to import java.io.File
		//System.out.println(new File("./libs/logging.properties").getAbsolutePath());
		
		
		Component server = new Component();
		//the port on which we will deploy the services
		server.getServers().add(Protocol.HTTP, 8182);
		
		//map services to URI
		server.getDefaultHost().attach("/trace", TraceServer.class);
		server.getDefaultHost().attach("/register", RegisterService.class);
		
		//add http basic auth to a particular URI
		ChallengeAuthenticator auth1 = createHTTPBasic();
		auth1.setNext(ItineraryComputingService.class);
		server.getDefaultHost().attach("/itinerary",auth1);
		
		
		/* Build the graph for Dijkstra calculation from xml map. This will be done only once
		 * at server startup.
		 * 
		 * 
		 * 
		 */


		//We make the server run for only 20s for testing purposes. 
		//It is enough to test a few requests, and the server will be auto shut down before you try to rebuild
		//and deploy again on the same port, thus avoiding the dreadful AddressAlreadyInUse exception.
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static ChallengeAuthenticator createHTTPBasic(){
		
		// Instantiates a Verifier of identifier/secret couples based on a simple Map.
		MapVerifier mapVerifier = new MapVerifier();
		mapVerifier.getLocalSecrets().put("loginB", "secret".toCharArray());
		
		//Create an authenticator for HTTP BASIC auth
		ChallengeAuthenticator guard = new ChallengeAuthenticator(null, ChallengeScheme.HTTP_BASIC, "testRealm");
		guard.setVerifier(mapVerifier);
		
		return guard;
	}
	
	public static DigestAuthenticator createHTTPDigest(){
		MapVerifier mapVerifier = new MapVerifier();
		mapVerifier.getLocalSecrets().put("loginB", "secret".toCharArray());
		DigestAuthenticator guard = new DigestAuthenticator(null, "TestRealm", "mySecretServerKey");
		guard.setWrappedVerifier(mapVerifier); 
		return guard;
	}
}