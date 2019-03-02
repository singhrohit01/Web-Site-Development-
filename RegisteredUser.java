package CinemaEbooking.Entity;

import java.sql.ResultSet;

import CinemaEbooking.DatabaseAccessor.*;

public class RegisteredUser extends User{

	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	protected Status status;
	
	public RegisteredUser(String firstName, String lastName, String email, String password){
		
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
						int status = userList.getInt("Status");
						switch (status){
						case 1:
							this.status = Status.ACTIVE;
							break;
						case 2:
							this.status = Status.INACTIVE;
							break;
						case 3:
							this.status = Status.SUSPENDED;
							break;
						case 4:
							this.status = Status.UNCONFIRMED;
							break;
						}
						break;
					}
				}
			}
		}catch (Exception e){
			System.out.print("Error");
			return;
		}
	}
	
	public RegisteredUser(String email, String password){
		this.email = email;
		this.password = password;
		AccountDB accountDB = new AccountDB();
		ResultSet userList;
		try{
			userList = accountDB.getList(); //Access database to get list of emails
			while (userList.next()) {
				if(email.toLowerCase().equals(userList.getString("Email").toLowerCase())){
					if(password.equals(userList.getString("Password"))){
						setFirstName(userList.getString("FirstName"));
						setLastName(userList.getString("LastName"));
						int status = userList.getInt("Status");
						switch (status){
						case 1:
							this.status = Status.ACTIVE;
							break;
						case 2:
							this.status = Status.INACTIVE;
							break;
						case 3:
							this.status = Status.SUSPENDED;
							break;
						case 4:
							this.status = Status.UNCONFIRMED;
							break;
						}
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
	
	public RegisteredUser(int userID){
		AccountDB accountDB = new AccountDB();
		ResultSet userList;
		try{
			userList = accountDB.getList(); //Access database to get list of emails
			while (userList.next()) {
				if(userList.getInt("UserID") == userID){
					email = userList.getString("Email");
					password = userList.getString("Password");
					setFirstName(userList.getString("FirstName"));
					setLastName(userList.getString("LastName"));
					int status = userList.getInt("Status");
					switch (status){
					case 1:
						this.status = Status.ACTIVE;
						break;
					case 2:
						this.status = Status.INACTIVE;
						break;
					case 3:
						this.status = Status.SUSPENDED;
						break;
					case 4:
						this.status = Status.UNCONFIRMED;
						break;
					}
					setID(userID); //Sets ID if email exists and matches password
					break;
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
	
	public Status getStatus(){
		return status;
	}
	
	public void setStatus(Status status){
		if (status == Status.SUSPENDED){
			System.out.println("You are suspended!");
			return;
		}
		else{
			if(status == Status.ACTIVE)		
				this.status = status;
			else{
				System.out.println("You have no access to this status");
				return;
			}
		}
	}
}
