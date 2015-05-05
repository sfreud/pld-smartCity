import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.Header;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.resource.Post;
import org.restlet.util.Series;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.sun.xml.internal.messaging.saaj.util.Base64;

public class RegisterService extends org.restlet.resource.ServerResource{
	//Class dedicated for users registration. Mapped on /register URI.
	
	@Post
	public String accept() {
		

		Series<Header> headers = ((HttpRequest) getRequest()).getHeaders();
		//display headers (debugging purpose)
		/*for(int i = 0;i<headers.size();i++){
			System.out.println(headers.get(i).toString());
		}*/
		String h = headers.getFirstValue("Authorization");
		String dh = Base64.base64Decode(h.substring("Basic ".length(), h.length()));
		String username = dh.substring(0, dh.indexOf(':'));
		String password = dh.substring(dh.indexOf(':')+1, dh.length());
		if(username.equals("") || username.length()>50)
			return "1";//Code 1 : empty or too long
		if(username.contains(":"))
			return "2";//code 2 : semicolon character in the username
		//the http auth sends the user ids in the format :
		// username:password encoded in base64
		//preventing the use of ':' allows for easier demarshalling
		
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
		}

		//connexion Ã  la DB locale
		Connection conn;
		try {
			//ça c'est notre fameux serveur. Celui auquel on peut pas accéder.
			//conn = DriverManager.getConnection("jdbc:mysql://91.229.95.251:10443/PLD", "h4315", "h4315pass");
			//et ça c'est le bon vieux localhost bien fiable pour les tests
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			String query = "insert into "
					+ DBPLD.users.TABLE_NAME+"("+DBPLD.users.COLUMN_NAME_TITLE+","
					+DBPLD.users.COLUMN_NAME_PASSWORD+") values(\""+username+"\",\""+password+"\");" ;
			//System.out.println(query);
			//result code : number of rows modified by the query.
			int r = stmt.executeUpdate(query) ;

			if(r==1)
				return "0";//success : code 0
		} catch (MySQLIntegrityConstraintViolationException e) {
			return"4";//exception sql : tout renvoyer pour l'instatn
		} catch (SQLException e){
			
		}

		return "3";
		
	}

}
