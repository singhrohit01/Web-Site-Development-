package CinemaEbooking.DatabaseAccessor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import CinemaEbooking.DatabaseAccessor.DataManager;
import CinemaEbooking.Entity.Ticket;

public class TicketDB {
	
	DataManager db = new DataManager();

	public void addTicket(Ticket ticket, int orderID) throws Exception{
		 PreparedStatement p = db.getConnection().prepareStatement("insert into Ticket (OrderID, TicketType, SeatID, MovieShowID, ShowStartTime) values (?,?,?,?,?)");
		 p.setInt(1, orderID);
		 p.setInt(2, ticket.getTicketType());
		 p.setInt(3, ticket.getSeatID());
		 p.setInt(4, ticket.getMovieShowID());
		 p.setTimestamp(5, ticket.getStartTime());
		 p.execute();
		
	}
	
	 public boolean isSeatOccupied(int seatID, Timestamp showTime, int movieShowID) throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("select * from Ticket where ShowStartTime=? and MovieShowID=?");
		 p.setTimestamp(1, showTime);
		 p.setInt(2, movieShowID);
		 ResultSet rs = p.executeQuery();
		 while(rs.next()){
			 if(seatID==rs.getInt("SeatID")){
				 return true;
			 }
			 
		 }
		 return false;
	 }
	 
	public void deleteAllTicketsForOrder(int orderID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from Ticket where OrderID=?");
		pstmt.setInt(1, orderID);
		pstmt.executeUpdate();
	}
	
	public ResultSet getList() throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from Ticket");
		ResultSet ticketList = mstmt.executeQuery();
		return ticketList;
	}
	 
}
