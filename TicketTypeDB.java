package CinemaEbooking.DatabaseAccessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TicketTypeDB {
	
	DataManager db = new DataManager();
	
	public double getTicketPrice(int ticketType) throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from TicketType where TicketTypeID=?");
		mstmt.setInt(1, ticketType);
		ResultSet ticketList = mstmt.executeQuery();
		if(ticketList.next())
			return ticketList.getDouble("Price");
		else
			return 0.0;
	}
	
	public void setTicketPrice(int ticketType, double price) throws Exception{
		PreparedStatement pstmt=db.getConnection().prepareStatement("Update TicketType set Price=? where TicketTypeID=?");
	    pstmt.setDouble(1, price);
		pstmt.setInt(2, ticketType);
		pstmt.executeUpdate();
	}
}
