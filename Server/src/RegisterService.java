import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.resource.Get;


public class RegisterService extends org.restlet.resource.ServerResource{
	
	@Get
	public String represent() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBNAME", "usrname", "pswd");
			Statement stmt = conn.createStatement() ;
			String query = "select users from users ;" ;
			ResultSet rs = stmt.executeQuery(query) ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "/register called";
		
	}

}
