package CinemaEbooking.DatabaseAccessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import CinemaEbooking.Entity.*;

public class CardDB {
	
	DataManager db = new DataManager();
	
	public void addCard(Card card, int userID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("insert into Card (CardType, CCNo, CCV, NameOnCard, ExpMonth, ExpYear, UserID) values (?, ?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, card.getCardType());
		pstmt.setLong(2, card.getCCNo());
		pstmt.setInt(3, card.getCCV());
		pstmt.setString(4, card.getName());
		pstmt.setString(5, card.getExpMonth().toString());
		pstmt.setInt(6, card.getExpYear());
		pstmt.setInt(7, userID);
		boolean cardExists = false;
		try{
			cardExists = doesExistForUser(card, userID);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(cardExists)
			System.out.println("Card already exists for account");
		else
			pstmt.execute();
	}
	
	private boolean doesExistForUser(Card card, int userID) throws Exception{
		PreparedStatement p = db.getConnection().prepareStatement("select * from Card where UserID=?");
		p.setInt(1, userID); 
		ResultSet rs = p.executeQuery();
		while(rs.next()){
			long ccNo = rs.getLong("ccNo");
			int ccv = rs.getInt("CCV");
			String month = rs.getString("ExpMonth");
			int year = rs.getInt("ExpYear");
			if (ccNo == card.getCCNo() && ccv == card.getCCV() && month.equals(card.getExpMonth().toString()) && year == card.getExpYear()){
				return true;
			}
		}
		return false;
	}
	
	public Card getCard(int cardID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("select * from Card where CardID=?");
		pstmt.setInt(1, cardID);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		String month = rs.getString("ExpMonth");
		Month expMonth = Month.JANUARY;
		switch(month){
		case "Jan":
			expMonth = Month.JANUARY;
			break;
		case "Feb":
			expMonth = Month.FEBRUARY;
			break;
		case "Mar":
			expMonth = Month.MARCH;
			break;
		case "Apr":
			expMonth = Month.APRIL;
			break;
		case "May":
			expMonth = Month.MAY;
			break;
		case "Jun":
			expMonth = Month.JUNE;
			break;
		case "Jul":
			expMonth = Month.JULY;
			break;
		case "Aug":
			expMonth = Month.AUGUST;
			break;
		case "Sep":
			expMonth = Month.SEPTEMBER;
			break;
		case "Oct":
			expMonth = Month.OCTOBER;
			break;
		case "Nov":
			expMonth = Month.NOVEMBER;
			break;
		case "Dec":
			expMonth = Month.DECEMBER;
			break;
		}
		Card c = new Card(rs.getString("CardType"), rs.getLong("CCNo"), rs.getString("NameOnCard"), rs.getInt("CCV"), expMonth, rs.getInt("ExpYear"));
		return c;
	}
	
	public ResultSet getUserCard(int userID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("select * from Card where UserID=?");
		pstmt.setInt(1, userID);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	public void deleteAllCards(int userID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from Card where UserID=?");
		pstmt.setInt(1, userID);
		pstmt.executeUpdate();
	}
	
	public void deleteCard(int cardID, int userID) throws Exception{
		OrderDB oDB = new OrderDB();
		ResultSet rs = oDB.getOrderList(userID);
		while(rs.next()){
			oDB.nullOrdersWithCard(rs.getInt("OrderID"));
		}
		PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from Card where CardID=?");
		pstmt.setInt(1, cardID);
		pstmt.executeUpdate();
	}

}
