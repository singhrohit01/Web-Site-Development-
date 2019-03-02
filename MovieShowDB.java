package CinemaEbooking.DatabaseAccessor;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import CinemaEbooking.DatabaseAccessor.DataManager;

public class MovieShowDB {
	
	DataManager db = new DataManager();
	
	public ResultSet getShowsForMovie(int movieID) throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieShow where MovieID=?");
		mstmt.setInt(1, movieID);
		return mstmt.executeQuery();
	}
	
	public ResultSet getShows() throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieShow order by ShowStartTime, MovieID asc");
		return mstmt.executeQuery();
	}
	
	public boolean doesMovieShowOnDate(int movieID, Date day) throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieShow");
		ResultSet rs = mstmt.executeQuery();
		while(rs.next()){
			int checkID = rs.getInt("MovieID");
			Timestamp ts = rs.getTimestamp("ShowStartTime");
			Date stamp = new Date(ts.getYear(), ts.getMonth(), ts.getDate());
			if (movieID == checkID && day.equals(stamp)){
				return true;
			}
		}
		return false;
	}
	
	public int movieShowIDFromMovieIDAndTime(int movieID, Timestamp ts) throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieShow where MovieID=? and ShowStartTime=?");
		mstmt.setInt(1, movieID);
		mstmt.setTimestamp(2, ts);
		ResultSet rs = mstmt.executeQuery();
		if(rs.next())
			return rs.getInt("MovieShowID");
		else
			return 0;
	}
	
	public int movieIDFromMovieShowIDAndTime(int movieShowID, Timestamp ts) throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from MovieShow where MovieShowID=? and ShowStartTime=?");
		mstmt.setInt(1, movieShowID);
		mstmt.setTimestamp(2, ts);
		ResultSet rs = mstmt.executeQuery();
		if(rs.next())
			return rs.getInt("MovieID");
		else
			return 0;
	}
	
	@SuppressWarnings("deprecation")
	public Timestamp[] getShowsForMovieOnDate(int movieID, Date day) throws Exception{
		ResultSet rs = null;
		try{
			rs = getShowsForMovie(movieID);
		} catch (Exception e){
			e.printStackTrace();
		}
		int length = 0;
		while(rs.next()){
			Timestamp ts = rs.getTimestamp("ShowStartTime");
			Date stamp = new Date(ts.getYear(), ts.getMonth(), ts.getDate());
			if(day.equals(stamp)){
				length++;
			}
		}
		rs.beforeFirst();
		Timestamp[] shows = new Timestamp[length];
		int i=0;
		while(rs.next()){
			Timestamp ts = rs.getTimestamp("ShowStartTime");
			Date stamp = new Date(ts.getYear(), ts.getMonth(), ts.getDate());
			if(day.equals(stamp)){
				shows[i] = ts;
				i++;
			}
		}
		return shows;
	}
	
	public void deleteAllShowsForMovie(int movieID) throws Exception{
		ResultSet rs = null;
		try{
			rs = getShows();
			while(rs.next()){
				if (rs.getInt("MovieID") == movieID){
					PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from Ticket where MovieShowID=? and ShowStartTime=?");
					pstmt.setInt(1, rs.getInt("MovieShowID"));
					pstmt.setTimestamp(2, rs.getTimestamp("ShowStartTime"));
					pstmt.executeUpdate();
					PreparedStatement pstmt2 = db.getConnection().prepareStatement("Delete from MovieShow where MovieID=?");
					pstmt2.setInt(1, movieID);
					pstmt2.executeUpdate();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
