package CinemaEbooking.Entity;

import java.sql.Date;

public class PromotionCode {

	private int promoID;
	private String promoName;
	private int salePercent;	
	private Date startDate;
	private Date expDate;
	
	
	public PromotionCode(String name, int sale, Date start, Date exp){
		promoName=name;
		salePercent=sale;	
		startDate=start;
		expDate=exp;		
	}
	
	public int getID(){
		return promoID;
	}
	
	public void setID(int id){
		promoID = id;
	}
	
	public String getName(){
		return promoName;
	}
	
	public int getSalePercent(){
		return salePercent;
	}
	
	public Date getExpDate(){
		return expDate;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
}
