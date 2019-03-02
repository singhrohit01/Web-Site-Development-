package CinemaEbooking.DatabaseAccessor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import CinemaEbooking.DatabaseAccessor.DataManager;
import CinemaEbooking.Entity.Movie;
import CinemaEbooking.Entity.RegisteredUser;

public class MovieDB {
	
	DataManager db = new DataManager();
	
	 public void addMovie(Movie movie) throws SQLException, Exception {
	    PreparedStatement pstmt = db.getConnection().prepareStatement("insert into MovieList (Movie, Length, Cast, Rating, Synopsis, Image, Trailer, Director, Producer) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, movie.getMovieTitle());
		pstmt.setInt(2, movie.getLength());
		pstmt.setString(3, movie.getCast());
		pstmt.setInt(4, movie.getRatingInt());
		pstmt.setString(5, movie.getSynopsis());
		pstmt.setString(6, movie.getImage());
		pstmt.setString(7, movie.getTrailer());
		pstmt.setString(8, movie.getDirector());
		pstmt.setString(9, movie.getProducer());
		pstmt.execute();
	 } // createNewMovieEntry
	    
	 public void editMovieLength(String movieTitle, int length) throws SQLException, Exception{
	    PreparedStatement pstmt=db.getConnection().prepareStatement("Update MovieList set Length=? where Movie=?");
	    pstmt.setInt(1, length);
		pstmt.setString(2, movieTitle);
		pstmt.executeUpdate();
	 }
	 
	 public int getMovieID(Movie movie) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("select * from MovieList where "
				+ "Movie like '%" + movie.getMovieTitle() +"%' "
				+ "and Length like '%" + movie.getLength() +"%' "
				+ "and Cast like '%" + movie.getCast() +"%' "
				+ "and Rating like '%" + movie.getRatingInt() +"%' "
				+ "and Synopsis like '%" + movie.getSynopsis() +"%' "
				+ "and Image like '%" + movie.getImage() +"%' "
				+ "and Trailer like '%" + movie.getTrailer() +"%'");
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			return rs.getInt("MovieID");
		else
			return 0;
	 }	    
	 
	 public void deleteMovie(int movieID) throws SQLException, Exception{
		 try{
			 MovieShowDB msDB = new MovieShowDB();
			 MovieGenreDB mgDB = new MovieGenreDB();
			 msDB.deleteAllShowsForMovie(movieID);
			 mgDB.deleteAllGenresForMovie(movieID);
		 }catch (Exception e){
			 e.printStackTrace();
		 }
		 PreparedStatement pstmt=db.getConnection().prepareStatement("Delete from MovieList where MovieID=?");
		 pstmt.setInt(1, movieID);
		 pstmt.executeUpdate();
	 }

	 public ResultSet getMovieList() throws Exception {
		 PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieList");
		 return mstmt.executeQuery();    	
	 }
	 
	 public String getMovieName(int movieID) throws Exception{
		 PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieList where MovieID=?");
		 mstmt.setInt(1, movieID);
		 ResultSet rs = mstmt.executeQuery(); 
		 rs.next();
		 return rs.getString("Movie");
	 }
	 
	 public ResultSet getMoviesFromQuery(String query) throws Exception{
		 PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieList where Movie like '%" + query +"%'");
		 return mstmt.executeQuery();
	 }
}
