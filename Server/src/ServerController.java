import java.io.File;

import org.restlet.Component;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;


public class ServerController {
	public static void main(String[] args){
		//set the logging config file
		System.setProperty("java.util.logging.config.file",
		        "./libs/logging.properties");
		System.out.println(new File("location.txt").getAbsolutePath());
		
		Component server = new Component();
		server.getServers().add(Protocol.HTTP, 8182);
		server.getDefaultHost().attach("/trace", TraceServer.class);
		server.getDefaultHost().attach("/register", RegisterService.class);
		
		ChallengeAuthenticator auth1 = createHTTPBasic();
		auth1.setNext(ItineraryComputingService.class);
		server.getDefaultHost().attach("/itinerary",auth1);



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
