import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.security.LocalVerifier;


public class AccessVerifier extends LocalVerifier {
	
    @Override
    public char[] getLocalSecret(String username) {

        try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
		}

		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			String query = "select password from users where name=\""+username+"\"";
			//System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
				String password = rs.getString(1);
				if(password!=null)
					return password.toCharArray();
			}

		} catch (SQLException e) {
		}
        return null;
    }
}