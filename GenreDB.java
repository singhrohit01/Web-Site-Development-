package CinemaEbooking.DatabaseAccessor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import CinemaEbooking.DatabaseAccessor.DataManager;

public class GenreDB {
	
	DataManager db = new DataManager();

	 public String getGenre(int genreID) throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("select * from Genre where MovieGenreID=?");
		 p.setInt(1, genreID);
		 ResultSet rs = p.executeQuery();
		 rs.next();
		 return rs.getString("MovieGenre");
	 }

}
