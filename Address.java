package CinemaEbooking.Entity;

import CinemaEbooking.DatabaseAccessor.AddressDB;

public class Address {

	private String streetAddress;
	private String apt;
	private String city;
	private String state;
	private int zipCode;
	private int userID;
	
	public Address(String streetAddress, String apt, String city, String state, int zipCode){
		this.streetAddress = streetAddress;
		this.apt=apt;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
	}
	
	public Address(int addressID){
		AddressDB aDB = new AddressDB();
		try{
			Address a = aDB.getAddress(addressID);
			this.streetAddress = a.getStreet();
			this.apt= a.getApt();
			this.city = a.getCity();
			this.state = a.getState();
			this.zipCode = a.getZipCode();
		}catch(Exception e){
			e.printStackTrace();
		} 		
	}
	
	public void setStreet(String street){
		streetAddress = street;
	}
	
	public String getStreet(){
		return streetAddress;
	}
	
	public void setApt(String apt){
		this.apt = apt;
	}
	
	public String getApt(){
		return apt;
	}
	
	public void setCity(String city){
		this.city = city;
	}
	
	public String getCity(){
		return city;
	}
	
	public void setState(String state){
		this.state = state;
	}
	
	public String getState(){
		return state;
	}
	
	public void setZipCode(int zip){
		zipCode = zip;
	}
	
	public int getZipCode(){
		return zipCode;
	}
	
	public int getUserID(){
		return userID;
	}

	
	public String toString(){
		String fullAddress = streetAddress + ", ";
		if (apt!=null)
			fullAddress+="Apt " + apt + ", ";
		fullAddress += city + ", " + state + ", " + zipCode;
		return fullAddress;
	}
}
