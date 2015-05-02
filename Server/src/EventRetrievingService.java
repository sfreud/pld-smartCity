import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.Header;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import com.sun.xml.internal.messaging.saaj.util.Base64;


public class EventRetrievingService extends ServerResource {
	//This service will retrieve the events sent by the client and store them in the DB.
	
	
	
	
	@Get
	public String represent(){
		String summary = getQuery().getValues("summary");
		String date = getQuery().getValues("date");
		//date will have to be reformatted to the mysql timestamp format
		String location = getQuery().getValues("location");
		//also get userid from http headers (basic or perhaps digest auth)
		String formattedDate = "2015-05-01 00:00:01";
		
		Series<Header> headers = ((HttpRequest) getRequest()).getHeaders();
		for(int i = 0;i<headers.size();i++){
			System.out.println(headers.get(i).toString());
		}
		String h = headers.getFirstValue("Authorization");
		System.out.println(h);
		String dh = Base64.base64Decode(h.substring("Basic ".length(), h.length()));
		String username = dh.substring(0, dh.indexOf(':'));
		
		//System.out.println(dh);
		
		//write directly the results in the database
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
		}

		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			String query = "select id from users where name=\""+username+"\"";
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			rs.next();
			int id = rs.getInt(1);
			/*while(rs.next()){//numÃ©ros de colonnes sont 1-based
				for(int i = 1; i<columnsNumber+1; i++)
					System.out.println(rs.getInt(i));
			}*/
			
			
			
			//faudra une connexion ssl que les utilisateurs puissent s'inscrire sans risque que le hash du mdp soit intercepté
			String query2 = "insert into calendarevents(userid,title,location,eventdate) values(\""+id+"\",\""+summary+"\",\""+location+"\",'"+formattedDate+"\');" ;
			System.out.println(query2);
			//Lire les résultats d'une requête (requête qui renvoie un résultat, par ex. select).
			int r = stmt.executeUpdate(query2) ;
			
			/*
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			
			while(rs.next()){//numÃ©ros de colonnes sont 1-based
				for(int i = 1; i<columnsNumber+1; i++)
					sb.append(rs.getString(i));
			}
			s = sb.toString();*/
			if(r==1)
				return "Success";
				//s = "Successfully registered.";
			else
				return "Operation failed.";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//s = "Registration failed. Reason was :\n" + e.toString();
		}

		
		//return : acknowledgement code. 
		return "Success";
	}
	
	
}
