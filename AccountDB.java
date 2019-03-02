package CinemaEbooking.DatabaseAccessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import CinemaEbooking.Entity.*;

public class AccountDB {
	
	DataManager db = new DataManager();
	
	public void addAccount(RegisteredUser user, int promo, int statusNo, int userTypeNo, int code) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("insert into User (FirstName, LastName, Email, Password, Promo, Status, UserType, Code) values (?, ?, ?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, user.getFirstName());
		pstmt.setString(2, user.getLastName());
		pstmt.setString(3, user.getEmail());
		pstmt.setString(4, user.getPassword());
		pstmt.setInt(5, promo);
		pstmt.setInt(6, statusNo);
		pstmt.setInt(7, userTypeNo);
		pstmt.setInt(8, code);
		pstmt.execute();
		
	}
	
	public ResultSet getList() throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from User");
		ResultSet userList = mstmt.executeQuery();
		return userList;
	}
	
	public RegisteredUser getUser(int userID) throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from User where UserID=?"); //If confirmation email was accepted
		mstmt.setInt(1, userID);
		ResultSet userList = mstmt.executeQuery();
		userList.next();
		RegisteredUser user = new RegisteredUser(userList.getString("FirstName"), userList.getString("LastName"), userList.getString("Email"), userList.getString("Password"));
		return user;
	}
	
	public void updateAccountStatus(String email, int statusID) throws Exception{
		PreparedStatement p1 = db.getConnection().prepareStatement("ALTER TABLE User DROP FOREIGN KEY StatusID");
		p1.executeUpdate();
		PreparedStatement p2 = db.getConnection().prepareStatement("Update User set Status=? where Email=?");
		p2.setInt(1, statusID);
		p2.setString(2, email);
		p2.executeUpdate();
		PreparedStatement p3 = db.getConnection().prepareStatement("ALTER TABLE User ADD CONSTRAINT StatusID FOREIGN KEY (Status) REFERENCES Status(StatusID)");
		p3.executeUpdate();
	}
	
	public void updateUserType(int userID, int type) throws Exception{
		PreparedStatement p1 = db.getConnection().prepareStatement("ALTER TABLE User DROP FOREIGN KEY UserTypeID");
		p1.executeUpdate();
		PreparedStatement p2 = db.getConnection().prepareStatement("Update User set UserType=? where UserID=?");
		p2.setInt(1, type);
		p2.setInt(2, userID);
		p2.executeUpdate();
		PreparedStatement p3 = db.getConnection().prepareStatement("ALTER TABLE User ADD CONSTRAINT UserTypeID FOREIGN KEY (UserType) REFERENCES UserType(UserTypeID)");
		p3.executeUpdate();
	}
	
	public void deleteUser(int userID){
		CardDB cDB = new CardDB();
		AddressDB aDB = new AddressDB();
		OrderDB oDB = new OrderDB();
		try{
			oDB.deleteOrdersForUser(userID);
			cDB.deleteAllCards(userID);
			aDB.deleteAllAddresses(userID);
			PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from User where UserID=?");
			pstmt.setInt(1, userID);
			pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void changePassword(String email, String newPass) throws Exception{
		PreparedStatement p2 = db.getConnection().prepareStatement("Update User set Password=? where Email=?");
		p2.setString(1, newPass);
		p2.setString(2, email);
		p2.executeUpdate();
	}
	
	public int getUserIDFromEmail(String email) throws Exception{
		PreparedStatement p = db.getConnection().prepareStatement("Select UserID from User where email=?");
		p.setString(1, email);
		ResultSet rs = p.executeQuery(); // Get the result table from the query 
		int userID=0;
		while (rs.next()) {                     // Position the cursor                   
			userID = rs.getInt("UserID");
		}
		return userID;
	}
	
	public int getUserType(int userID) throws Exception{
		PreparedStatement p = db.getConnection().prepareStatement("Select UserType from User where UserID=?");
		p.setInt(1, userID);
		ResultSet rs = p.executeQuery();
		rs.next();
		int num = rs.getInt("UserType");
		return num;
	}
	
	public int getCode(int userID) throws Exception{
		PreparedStatement p = db.getConnection().prepareStatement("Select Code from User where UserID=?");
		p.setInt(1, userID);
		ResultSet rs = p.executeQuery();
		rs.next();
		return rs.getInt("Code");
	}
}
