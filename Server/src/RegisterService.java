import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.resource.Get;

public class RegisterService extends org.restlet.resource.ServerResource{
	//Classe gérant l'enregistrement des utilisateurs. Mappée sur l'uri /register (cf main).
	
	
	
	@Get
	public String represent() {
		

		String username = getQuery().getValues("username");
		if(username.equals("") || username.length()>50)
			return "Incorrect username (empty or too long).";
		if(username.contains(":"))
			return "Username cannot contain the semicolon character ( : )";
		//the http auth sends the user ids in the format :
		// username:password encoded in base64
		//preventing the use of ':' allows for easier demarshalling

		String s = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
		}

		//connexion à la DB locale
		Connection conn;
		try {
			//�a c'est notre fameux serveur. Celui auquel on peut pas acc�der.
			//conn = DriverManager.getConnection("jdbc:mysql://91.229.95.251:10443/PLD", "h4315", "h4315pass");
			//et �a c'est le bon vieux localhost bien fiable pour les tests
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			//faudra une connexion ssl que les utilisateurs puissent s'inscrire sans risque que le hash du mdp soit intercepté
			String query = "insert into "
					+ DBPLD.users.TABLE_NAME+"("+DBPLD.users.COLUMN_NAME_TITLE+","+DBPLD.users.COLUMN_NAME_PASSWORD+") values(\""+username+"\",password);" ;
			System.out.println(query);
			//result code : number of rows modified by the query.
			int r = stmt.executeUpdate(query) ;
			
			/*
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			
			while(rs.next()){//numÃ©ros de colonnes sont 1-based
				for(int i = 1; i<columnsNumber+1; i++)
					sb.append(rs.getString(i));
			}
			s = sb.toString();*/
			if(r==1)
				s = "Successfully registered.";
		} catch (SQLException e) {
			s = "Registration failed. Reason was :\n" + e.toString();
		}

		return s;
		
	}

}
