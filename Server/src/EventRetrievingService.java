import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.Header;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import com.sun.xml.internal.messaging.saaj.util.Base64;


public class EventRetrievingService extends ServerResource {
	//This service will receive the events sent by the client and store them in the DB.
	
	
	
	
	@Get
	public String represent(){
		String summary = getQuery().getValues("summary");
		String date = getQuery().getValues("date");
		//date will have to be reformatted to the mysql timestamp format
		String location = getQuery().getValues("location");
		//also get userid from http headers (basic or perhaps digest auth)
		String formattedDate = "2015-05-01 00:00:01";
		
		Series<Header> headers = ((HttpRequest) getRequest()).getHeaders();
		//display headers (debugging purpose)
		for(int i = 0;i<headers.size();i++){
			System.out.println(headers.get(i).toString());
		}
		String h = headers.getFirstValue("Authorization");
		String dh = Base64.base64Decode(h.substring("Basic ".length(), h.length()));
		String username = dh.substring(0, dh.indexOf(':'));

		//write directly the results in the database
		try {
			Class.forName("com.mysql.jdbc.Driver") ;
		} catch (ClassNotFoundException e) {
		}

		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testpld", "root", "password");
			Statement stmt = conn.createStatement() ;
			String query = "select " + DBPLD.users.COLUMN_NAME_ID +
					" from " + DBPLD.users.TABLE_NAME + 
					" where " + DBPLD.users.COLUMN_NAME_TITLE + "=\""+username+"\"";
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			int id = rs.getInt(1);
			
			//Display the full results if needed
			//ResultSetMetaData rsmd = rs.getMetaData();
			//int columnsNumber = rsmd.getColumnCount();
			/*while(rs.next()){
				for(int i = 1; i<columnsNumber+1; i++)
					System.out.println(rs.getInt(i));
			}*/
			

			String query2 = "insert into "+
					DBPLD.calendarEvents.TABLE_NAME + "("+
					DBPLD.calendarEvents.COLUMN_NAME_UID+","+DBPLD.calendarEvents.COLUMN_NAME_TITLE+","+
					DBPLD.calendarEvents.COLUMN_NAME_LOCATION+","+DBPLD.calendarEvents.COLUMN_NAME_EVENTDATE+")"+
					" values(\""+id+"\",\""+summary+"\",\""+location+"\",'"+formattedDate+"\');" ;
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
			else
				return "Operation failed.";
		} catch (SQLException e) {
			return e.toString();
		}
	}
	
	
}
