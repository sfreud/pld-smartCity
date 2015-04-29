import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.representation.StringRepresentation;
import org.restlet.security.MapVerifier;

public class TraceServer extends org.restlet.resource.ServerResource {
	
	//Snippet : get sur une page html
	//new ClientResource("http://restlet.com").get().write(System.out);
	
	public static void main(String[] args){
		System.setProperty("java.util.logging.config.file",
		        "./libs/logging.properties");
		TraceServer test = new TraceServer();
	    try {
			test.createTraceServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createTraceServer() throws Exception
	{

		Restlet restlet = new Restlet() {
		    @Override
		    public void handle(Request request, Response response) {
		        response.setEntity(new StringRepresentation("A web service will be added here soon.\n"+request.toString(), MediaType.TEXT_PLAIN));
		        
		        //récupération des paramètres
		        /*
		        Form form = request.getResourceRef().getQueryAsForm(); 
		        for (Parameter parameter : form) {
		            System.out.print("parameter " + parameter.getName());
		            System.out.println("/" + parameter.getValue());
		        }
		        */
		        //récupération des cookies
	            /*
	            for (Cookie cookie : request.getCookies()) {
		            System.out.println("name = " + cookie.getName());
		            System.out.println("value = " + cookie.getValue());
		            System.out.println("domain = " + cookie.getDomain());
		            System.out.println("path = " + cookie.getPath());
		            System.out.println("version = " + cookie.getVersion());
		        }
		        */
		    }
		};
		
		

	    
	    
	    
		 // Guard the restlet with BASIC authentication.
		DigestAuthenticator guard = new DigestAuthenticator(null, "TestRealm", "mySecretServerKey");
		//ChallengeAuthenticator guard = new ChallengeAuthenticator(null, ChallengeScheme.HTTP_BASIC, "testRealm");
	    // Instantiates a Verifier of identifier/secret couples based on a simple Map.
	    MapVerifier mapVerifier = new MapVerifier();
	    // Load a single static login/secret pair.
	    mapVerifier.getLocalSecrets().put("loginB", "secret".toCharArray());
	    guard.setWrappedVerifier(mapVerifier);
	    //guard.setVerifier(mapVerifier);

	    guard.setNext(restlet);
	    Component component = new Component();
	    component.getServers().add(Protocol.HTTP, 8183);
	    component.getDefaultHost().attach("/trace",guard);
	    component.getDefaultHost().attach("/register",RegisterService.class);
	    //component.getServers().add(Protocol.HTTP, 8182);  
	    //component.getDefaultHost().attachDefault(guard);
	    
	    
	    
	    // Now, let's start the component!
	    // Note that the HTTP server connector is also automatically started.
	    component.start(); 

	    
	    //extinction auto au bout de 20s
	    Thread.sleep(20000);
	    component.stop();
	}


}