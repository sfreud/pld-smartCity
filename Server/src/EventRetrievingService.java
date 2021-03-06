import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.Header;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import com.sun.xml.internal.messaging.saaj.util.Base64;


public class EventRetrievingService extends ServerResource {
	//This service will receive the events sent by the client and store them in the DB.
	
	
	
	
	@Post
	public String accept(){
		String summary = getQuery().getValues("summary");
		String date = getQuery().getValues("date");
		//date will have to be reformatted to the mysql timestamp format
		String location = getQuery().getValues("location");
		//also get userid from http headers (basic or perhaps digest auth)
		String formattedDate = "2015-05-01 00:00:01";
		
		Series<Header> headers = ((HttpRequest) getRequest()).getHeaders();
		//display headers (debugging purpose)
		/*for(int i = 0;i<headers.size();i++){
			System.out.println(headers.get(i).toString());
		}*/
		
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
			//Looks for the user id in the DB, given its username (see auth headers).
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
					" values(\""+id+"\",\""+summary+"\",\""+location+"\",'"+date+"\');" ;
			System.out.println(query2);
			//Lire les r�sultats d'une requ�te (requ�te qui renvoie un r�sultat, par ex. select).
			int r = stmt.executeUpdate(query2) ;
			
			/*
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			
			while(rs.next()){//numéros de colonnes sont 1-based
				for(int i = 1; i<columnsNumber+1; i++)
					sb.append(rs.getString(i));
			}
			s = sb.toString();*/
			
			
			//Return codes.
			//0 : operation terminated correctly.
			//1 : no title sent
			//2 : no location sent
			//3 : no date sent
			//4 : an event with the same (userid, title) key already exists in the DB
			//other : unknown error
			if(r==1)
				return "0";
			else
				return "Operation failed.";
		} catch (SQLException e) {
			//analyse du log
			if(e.toString().contains("Field 'title' doesn't have a default value"))
				return "1";
			else if(e.toString().contains("Field 'location' doesn't have a default value"))
				return "2";
			else if(e.toString().contains("Field 'eventdate' doesn't have a default value"))
				return "3";
			else if(e.toString().contains("Duplicate entry"))
				return "4";
			
			
			return e.toString();
		}
	}
	
	
}
