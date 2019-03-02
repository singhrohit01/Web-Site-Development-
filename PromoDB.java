package CinemaEbooking.DatabaseAccessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

import CinemaEbooking.Entity.*;

public class PromoDB {

	DataManager db = new DataManager();
	
	public void addPromo(PromotionCode promo) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("insert into Promo (PromoName, Sale, StartDate, ExpDate) values (?, ?, ?, ?)");
		pstmt.setString(1,  promo.getName());
		pstmt.setInt(2, promo.getSalePercent());
		pstmt.setDate(3, promo.getStartDate());
		pstmt.setDate(4, promo.getExpDate());
		pstmt.execute();
	}
	
	public ResultSet getList() throws Exception{
		PreparedStatement mstmt = db.getConnection().prepareStatement("select * from Promo");
		ResultSet promoList = mstmt.executeQuery();
		return promoList;
	}
	
	public PromotionCode getPromo(int promoID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("select * from Promo where PromoID=?");
		pstmt.setInt(1, promoID);
		ResultSet pL = pstmt.executeQuery();
		pL.next();
		String name = pL.getString("PromoName");
		int sale = pL.getInt("Sale");
		Date sDate = pL.getDate("StartDate");
		Date eDate = pL.getDate("ExpDate");
		PromotionCode p = new PromotionCode(name, sale, sDate, eDate);
		p.setID(promoID);
		return p;
	}
	
	public void deletePromo(int promoID) throws Exception{
		PreparedStatement pstmt = db.getConnection().prepareStatement("Delete from Promo where PromoID=?");
		pstmt.setInt(1, promoID);
		pstmt.executeUpdate();
	}
	
	public int getPromoIDFromPromoName(String promoName) throws Exception{
		PreparedStatement p = db.getConnection().prepareStatement("Select PromoID from Promo where promoName=?");
		p.setString(1, promoName);
		ResultSet rs = p.executeQuery(); // Get the result table from the query 
		int promoID=0;
		while (rs.next()) {                     // Position the cursor                   
			promoID = rs.getInt("PromoID");
		}
		return promoID;
	}
	
	public static void main(String args[]){
		PromoDB pDB = new PromoDB();
		PromotionCode p = null;
		try{
			p = pDB.getPromo(pDB.getPromoIDFromPromoName("FREESTUFF"));
			System.out.println(p.getID());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
