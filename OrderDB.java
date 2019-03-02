package CinemaEbooking.DatabaseAccessor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import CinemaEbooking.DatabaseAccessor.DataManager;

public class OrderDB {
	
	DataManager db = new DataManager();

	 public int startOrder(int userID) throws Exception {
		 clearEmptyOrder(userID);
		 PreparedStatement p = db.getConnection().prepareStatement("insert into Orders (UserID) values (?)");
		 p.setInt(1, userID);
		 p.execute();
		 return getEmptyOrder(userID);
	 }
	 
	 public void clearEmptyOrder(int userID) throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("select * from Orders where UserID=?");
		 p.setInt(1, userID);
		 ResultSet rs = p.executeQuery();
		 
		 int orderID = 0;
		 while(rs.next()){
			 if(rs.getInt("CardID")==0 && rs.getDouble("TotalPrice")==0){
				 orderID = rs.getInt("OrderID");
			 }
			 if(orderID!=0){
					TicketDB tDB = new TicketDB();
				 	try{
					 	tDB.deleteAllTicketsForOrder(orderID);
				 	} catch (Exception e){
					 	e.printStackTrace();
				 	}
				 	PreparedStatement p2 = db.getConnection().prepareStatement("Delete from Orders where OrderID=?");// and CardID=null limit 1");
				 	p2.setInt(1, orderID);
				 	p2.executeUpdate();
			 }
		 }
	 }
	 
	 //If 0 is returned, there are no empty orders
	 public int getEmptyOrder(int userID) throws Exception{
		 PreparedStatement p = db.getConnection().prepareStatement("select * from Orders where UserID=?");
		 p.setInt(1, userID);
		 ResultSet rs = p.executeQuery();
		 
		 while (rs.next()){
			 if(rs.getInt("CardID")==0 && rs.getDouble("TotalPrice")==0){
				 return rs.getInt("OrderID");
			 }
		 }
		 return 0;
	 }
	 
	 public void fillOrder(int orderID, int cardID, int promoID, double totalPrice) throws Exception{
		PreparedStatement p = db.getConnection().prepareStatement("Update Orders set CardID=? where OrderID=?");
		p.setInt(1, cardID);
		p.setInt(2, orderID);
		p.executeUpdate();
		PreparedStatement p3 = db.getConnection().prepareStatement("Update Orders set TotalPrice=? where OrderID=?");
		p3.setDouble(1, totalPrice);
		p3.setInt(2, orderID);
		p3.executeUpdate();
		if(promoID!=0){
			PreparedStatement alter = db.getConnection().prepareStatement("ALTER TABLE Orders DROP FOREIGN KEY PromoIDOrder");
			alter.executeUpdate();
			PreparedStatement p2 = db.getConnection().prepareStatement("Update Orders set PromoID=? where OrderID=?");
			p2.setInt(1, promoID);
			p2.setInt(2, orderID);
			p2.executeUpdate();
			PreparedStatement alterBack = db.getConnection().prepareStatement("ALTER TABLE Orders ADD CONSTRAINT PromoIDOrder FOREIGN KEY (PromoID) REFERENCES Promo(PromoID)");
			alterBack.executeUpdate();
		}
		 
	 }
	 
	 public void deleteOrdersForUser(int userID) throws Exception{
		ResultSet rs = getOrderList(userID);
		while(rs.next()){
			TicketDB tDB = new TicketDB();
			tDB.deleteAllTicketsForOrder(rs.getInt("OrderID"));
		}
		PreparedStatement p = db.getConnection().prepareStatement("Delete from Orders where UserID=?");
		p.setInt(1, userID);
		p.executeUpdate();
	 }
	 
	 public void deleteOrder(int orderID) throws Exception{
		TicketDB tDB = new TicketDB();
		tDB.deleteAllTicketsForOrder(orderID);
		PreparedStatement p = db.getConnection().prepareStatement("Delete from Orders where OrderID=?");
		p.setInt(1, orderID);
		p.executeUpdate();
	 }
	 
	 public ResultSet getOrderList(int userID) throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from Orders where UserID=?");
		mstmt.setInt(1, userID);
		ResultSet userList = mstmt.executeQuery();
		return userList;
	 }
	 
	 public void nullOrdersWithCard(int orderID) throws Exception{
		 PreparedStatement alter = db.getConnection().prepareStatement("ALTER TABLE Orders DROP FOREIGN KEY CardIDOrder");
		alter.executeUpdate();
		PreparedStatement p2 = db.getConnection().prepareStatement("Update Orders set CardID=null where OrderID=?");
		p2.setInt(1, orderID);
		p2.executeUpdate();
		PreparedStatement alterBack = db.getConnection().prepareStatement("ALTER TABLE Orders ADD CONSTRAINT CardIDOrder FOREIGN KEY (CardID) REFERENCES Card(CardID)");
		alterBack.executeUpdate();
	 }
	 
	 public ResultSet getList() throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from Orders");
		ResultSet orderList = mstmt.executeQuery();
		return orderList;
	 }
	 
}
