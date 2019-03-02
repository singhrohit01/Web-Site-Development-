package CinemaEbooking.DatabaseAccessor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import CinemaEbooking.DatabaseAccessor.DataManager;

public class RatingDB {
	
	DataManager db = new DataManager();

	 public String getRating(int ratingID) throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("select * from Rating where RatingID=?");
		 p.setInt(1, ratingID);
		 ResultSet rs = p.executeQuery();
		 rs.next();
		 return rs.getString("Rating");
	 }

}
