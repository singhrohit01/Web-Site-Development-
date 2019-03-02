package CinemaEbooking.Entity;

import java.sql.ResultSet;

import CinemaEbooking.DatabaseAccessor.AccountDB;

public class Administrator extends User{
	
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	
	public Administrator(String firstName, String lastName, String email, String password){
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		
		AccountDB accountDB = new AccountDB();
		ResultSet userList;
		try{
			userList = accountDB.getList(); //Access database to get list of emails
			while (userList.next()) {
				if(email.toLowerCase().equals(userList.getString("Email").toLowerCase())){
					if(password.equals(userList.getString("Password"))){
						setID(userList.getInt("UserID")); //Sets ID if email exists and matches password
						break;
					}
				}
			}
		}
		catch (Exception e){
			System.out.print("Error");
			return;
		}
	}
	
	public Administrator(String email, String password){
		AccountDB accountDB = new AccountDB();
		ResultSet userList;
		try{
			userList = accountDB.getList(); //Access database to get list of emails
			while (userList.next()) {
				if(email.toLowerCase().equals(userList.getString("Email").toLowerCase())){
					if(password.equals(userList.getString("Password"))){
						setFirstName(userList.getString("FirstName"));
						setLastName(userList.getString("LastName"));
						setID(userList.getInt("UserID")); //Sets ID if email exists and matches password
						break;
					}
				}
			}
		}
		catch (Exception e){
			System.out.print("Error");
			return;
		}
	}
	
	public int getID(){
		return id;
	}
	
	private void setID(int id){
		this.id = id;
	}
	
	public void setID(){
		AccountDB accountDB = new AccountDB();
		try{
			id = accountDB.getUserIDFromEmail(email);
		} catch (Exception e){
			System.out.print("Error");
			return;
		}
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public void setFirstName(String name){
		 firstName = name;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public void setLastName(String name){
		 lastName = name;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}

}
