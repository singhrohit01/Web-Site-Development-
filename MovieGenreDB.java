package CinemaEbooking.DatabaseAccessor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import CinemaEbooking.DatabaseAccessor.DataManager;

public class MovieGenreDB {
	
	DataManager db = new DataManager();
	
	 public void addGenreToMovie(int movieID, int genreID) throws SQLException, Exception {
	    PreparedStatement pstmt = db.getConnection().prepareStatement("insert into MovieGenre (MovieID, GenreID) values (?, ?)");
		pstmt.setInt(1, movieID);
		pstmt.setInt(2, genreID);
		pstmt.execute();
	 } // createNewMovieEntry
	    

	 public ResultSet getGenreList(int movieID) throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("select * from MovieGenre where MovieID=?");
		 p.setInt(1, movieID);
		 return p.executeQuery();
	 }
	 
	 public boolean doesMovieHaveGenre(int movieID, int genreID) throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("select * from MovieGenre where MovieID=?");
		 p.setInt(1, movieID);
		 ResultSet rs = p.executeQuery();
		 while(rs.next()){
			 if(rs.getInt("GenreID")==genreID)
				 return true;
		 }
		 return false;
	 }
	 
	 public void deleteAllGenresForMovie(int movieID) throws Exception{
		 PreparedStatement pstmt2 = db.getConnection().prepareStatement("Delete from MovieGenre where MovieID=?");
		pstmt2.setInt(1, movieID);
		pstmt2.executeUpdate();
	 }

}
