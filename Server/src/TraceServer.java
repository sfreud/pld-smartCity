import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;

public class TraceServer extends org.restlet.resource.ServerResource {
	
	//Snippet : get sur une page html
	//new ClientResource("http://restlet.com").get().write(System.out);
	
	public static void main(String[] args){
		System.setProperty("java.util.logging.config.file",
		        "C:\\Users\\Sylvain\\Documents\\GitHub\\pld-smartCity\\Server\\libs\\logging.properties");
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
	// Create a new Restlet component and add a HTTP server connector to it
	    Component component = new Component();
	    component.getServers().add(Protocol.HTTP, 8183);
	
	    // Then attach it to the local host
	    component.getDefaultHost().attach("/trace",TraceServer.class);
	    
	    // Now, let's start the component!
	    // Note that the HTTP server connector is also automatically started.
	    component.start(); 
	    
	    
	    //extinction auto au bout de 20s
	    Thread.sleep(20000);
	    component.stop();
	}
	
    @Get("txt")
    //version de test : renvoyer la trace en texte
    public String toString() {
    	return "This is the port dedicated to traces. Here is the recorded trace :\nResource URI  : " + getReference() + '\n' + "Root URI      : "
        + getRootRef() + '\n' + "Routed part   : "
        + getReference().getBaseRef() + '\n' + "Remaining part: "
        + getReference().getRemainingPart();
    }
    //Les logs sont stockés dans un fichier indiqué dans ./libs/logging.properties

}