import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.resource.Get;

public class RegisterService extends org.restlet.resource.ServerResource{
	//Classe g�rant l'enregistrement des utilisateurs. Mapp�e sur l'uri /register (cf main).
	
	
	
	@Get
	public String represent() {
		

		String username = getQuery().getValues("username");
		if(username.equals("") || username.length()>50)
			return "Incorrect username (empty or too long).";
        //for (Parameter parameter : form) {
        	
            //System.out.print("parameter " + parameter.getName());
            //System.out.println("/" + parameter.getValue());
        //}
		
		
		
		
		//valeur de retour sur un GET. Utilis� pour les tests. Servira � terme � indiquer les erreurs (ex : username d�j� utilis�).
		//StringBuilder sb = new StringBuilder();
		String s = null;
		
		//charger le connecteur JDBC
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//connexion � la DB locale
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			//faudra une connexion ssl que les utilisateurs puissent s'inscrire sans risque que le hash du mdp soit intercept�
			String query = "insert into users(name,password) values(\""+username+"\",password);" ;
			System.out.println(query);
			//Lire les r�sultats d'une requ�te (requ�te qui renvoie un r�sultat, par ex. select).
			int r = stmt.executeUpdate(query) ;
			
			/*
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			
			while(rs.next()){//numéros de colonnes sont 1-based
				for(int i = 1; i<columnsNumber+1; i++)
					sb.append(rs.getString(i));
			}
			s = sb.toString();*/
			if(r==1)
				s = "Successfully registered.";
			//else
				//s = "Operation failed.";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			s = "Registration failed. Reason was :\n" + e.toString();
		}

		return s;
		
	}

}
