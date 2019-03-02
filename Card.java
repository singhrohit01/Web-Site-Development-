package CinemaEbooking.Entity;

public class Card {

	private String cardType;
	private long ccNo;
	private String nameOnCard;
	private int ccv;
	private Month expMonth;
	private int expYear;
	
	public Card(String cardType, long ccNo, String nameOnCard, int ccv, Month expMonth, int expYear){
		this.cardType = cardType;
		this.ccNo = ccNo;
		this.nameOnCard = nameOnCard;
		this.ccv = ccv;
		this.expMonth = expMonth;
		this.expYear = expYear;
	}
	
	public String getCardType(){
		return cardType;
	}
	
	public long getCCNo(){
		return ccNo;
	}
	
	public void setCCNo(long ccNo){
		this.ccNo = ccNo;
	}
	
	public String getName(){
		return nameOnCard;
	}
	
	public void setName(String name){
		nameOnCard = name;
	}
	
	public int getCCV(){
		return ccv;
	}
	
	public void setCCV(int ccv){
		this.ccv = ccv;
	}
	
	public Month getExpMonth(){
		return expMonth;
	}
	
	public void setExpMonth(Month expMonth){
		this.expMonth = expMonth;
	}
	
	public int getExpYear(){
		return expYear;
	}
	
	public void setExpYear(int expYear){
		this.expYear = expYear;
	}
	
	public String toString(){
		String cardInfo = nameOnCard + ": ****-****-****-" + ccNo%10000 + " " + expMonth + "/" + expYear;
		return cardInfo;
	}
	
	public String lastFour(){
		return "" + ccNo%10000;
	}
}
