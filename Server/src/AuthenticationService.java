import org.restlet.resource.Post;

public class AuthenticationService extends org.restlet.resource.ServerResource{
	//URL accepting only POST request, mapped on /login. Hidden behind a DB-backed authenticator. 
	//Its only use is to check whether the credentials are correct, which is in fact
	//completely done by the authenticor.

	@Post
	public String accept() {
		//Always return 0. This stands for "correct credentials".
		//If they are wrong the authenticator won't route the request to this class
		//and the client will receive an http 403 code.
		return "0";
	}
	
}
