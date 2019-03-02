package CinemaEbooking.DatabaseAccessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import CinemaEbooking.Entity.*;

public class AddressDB {

	DataManager db = new DataManager();
	
	public void addAddress(Address address, int userID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("insert into Address (Street, Apt, City, State, Zip, UserID) values (?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, address.getStreet());
		pstmt.setString(2, address.getApt());
		pstmt.setString(3, address.getCity());
		pstmt.setString(4, address.getState());
		pstmt.setInt(5, address.getZipCode());
		pstmt.setInt(6, userID);
		pstmt.execute();
	}
	
	public Address getAddress(int addressID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("select * from Address where AddressID=?");
		pstmt.setInt(1, addressID);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		Address a = new Address(rs.getString("Street"), rs.getString("Apt"), rs.getString("City"), rs.getString("State"), rs.getInt("Zip"));
		return a;
	}
	
	public ResultSet getUserAddress(int userID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("select * from Address where UserID=?");
		pstmt.setInt(1, userID);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	public void deleteAllAddresses(int userID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from Address where UserID=?");
		pstmt.setInt(1, userID);
		pstmt.executeUpdate();
	}
	
	public void deleteAddress(int addressID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from Address where AddressID=?");
		pstmt.setInt(1, addressID);
		pstmt.executeUpdate();
	}
}
