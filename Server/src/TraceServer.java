import org.restlet.resource.Get;

public class TraceServer extends org.restlet.resource.ServerResource {
	
	
	@Get 
	public String represent(){
		String s = "A web service will be added here soon.\n" + getQuery().toString();
		return s;
	}
	
	//Snippet : get sur une page html
	//new ClientResource("http://restlet.com").get().write(System.out);
	
	
	//Déplacé vers ServerController.java
	/*public static void main(String[] args){
		System.setProperty("java.util.logging.config.file",
		        "./libs/logging.properties");
		TraceServer test = new TraceServer();
	    try {
			test.createTraceServer(8183);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createTraceServer(int port) throws Exception
	{

		
		/*Restlet restlet = new Restlet() {
		    @Override
		    public void handle(Request request, Response response) {
		        response.setEntity(new StringRepresentation("A web service will be added here soon.\n"
		        		+request.toString(), MediaType.TEXT_PLAIN));
		    }
		};*/
		
		// Instantiates a Verifier of identifier/secret couples based on a simple Map.
		/*MapVerifier mapVerifier = new MapVerifier();
		mapVerifier.getLocalSecrets().put("loginB", "secret".toCharArray());
		
		//Create an authenticator for HTTP BASIC auth...
		ChallengeAuthenticator guard = new ChallengeAuthenticator(null, ChallengeScheme.HTTP_BASIC, "testRealm");
		guard.setVerifier(mapVerifier);
		//Or for HTTP Digest
		//DigestAuthenticator guard = new DigestAuthenticator(null, "TestRealm", "mySecretServerKey");
		//guard.setWrappedVerifier(mapVerifier);  
	    

	    guard.setNext(restlet);*/
	    /*Component component = new Component();
	    component.getServers().add(Protocol.HTTP, port);
	    component.getDefaultHost().attach("/trace",guard);
	    component.getDefaultHost().attach("/register",RegisterService.class);
	    //requiert l'authentification pour toutes les ressources exposées (n'écrase pas celles qui ont un téglage spécifique)
	    //component.getDefaultHost().attachDefault(guard);

	    component.start(); 

	    //extinction auto au bout de 20s
	    Thread.sleep(120000);
	    component.stop();
	}*/

}