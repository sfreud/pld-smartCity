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

	@Post
	public String accept() {

		Series<Header> headers = ((HttpRequest) getRequest()).getHeaders();
		//display headers (debugging purpose)
		 //headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
		for(int i = 0;i<headers.size();i++){
			System.out.println(headers.get(i).toString());
		}
		String h = headers.getFirstValue("Authorization");
		String dh = Base64.base64Decode(h.substring("Basic ".length(), h.length()));
		String username = dh.substring(0, dh.indexOf(':'));
		String password = dh.substring(dh.indexOf(':')+1, dh.length());
		
		if(username.equals("") || username.length()>50)
			return "1";//Code 1 : empty or too long
		if(username.contains(":"))
			return "2";//code 2 : semicolon character in the username
		
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
		}
		

		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			String query = "insert into " + DBPLD.users.TABLE_NAME + "(" + DBPLD.users.COLUMN_NAME_TITLE + "," + 
			DBPLD.users.COLUMN_NAME_PASSWORD +")" +
					" values (\"" + username + "\",\"" + password + "\");";
			System.out.println(query);
			int rs = stmt.executeUpdate(query);
			if(rs==1)
				return "0";
		} catch (SQLException e) {
			return e.toString();
		}
		return "3"; //Default error code
	}

}
