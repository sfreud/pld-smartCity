import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.Header;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.util.Series;

import com.sun.xml.internal.messaging.saaj.util.Base64;

public class AuthentificationService extends org.restlet.resource.ServerResource{
	//Classe gerant l'authentification des utilisateurs. Mappee sur l'uri /authentification (cf main).
	
	@Get
	public String represent(){
		return "bouh";
	}
	@Post
	public String accept() {

		//Series<Header> headers = ((HttpRequest) getRequest()).getHeaders();
		//display headers (debugging purpose)
		 //headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
		/*for(int i = 0;i<headers.size();i++){
			System.out.println(headers.get(i).toString());
		}
		String h = headers.getFirstValue("Authorization");
		String dh = Base64.base64Decode(h.substring("Basic ".length(), h.length()));
		String username = dh.substring(0, dh.indexOf(':'));
		String password = dh.substring(dh.indexOf(':'), dh.indexOf(h.length()));
		
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
		}
		return username + "/" + password;*/
		return "yop";

		/*Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			String query = "select " + DBPLD.users.COLUMN_NAME_PASSWORD +
					" from " + DBPLD.users.TABLE_NAME + 
					" where " + DBPLD.users.COLUMN_NAME_TITLE + "=\""+username+"\"";
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			return rs.toString();
		} catch (SQLException e) {
			return e.toString();
		}*/
	}

}
