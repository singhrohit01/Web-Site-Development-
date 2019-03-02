package CinemaEbooking.Servlet.RegisterLogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.AddressDB;
import CinemaEbooking.Entity.Address;
import CinemaEbooking.Entity.Mailer;
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class Register
 */
public class RegistrationController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException{
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		//Getting paramters from form
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String pass = request.getParameter("pass");
		String passCheck = request.getParameter("passCheck");
		String promo = request.getParameter("promo");

		String result=null;
		try{
			result = register(firstName, lastName, email, pass, passCheck, promo);
		}catch (Exception e){
			e.printStackTrace();
		}
		//Checking multiple fail cases
		if (result.equals("fillFields")) {
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Fill out all fields before submitting.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Register.html");
			rd.include(request, response);
		}
		else if (result.equals("invalidEmail")) {
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Invalid email format, try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Register.html");
			rd.include(request, response);
		}
		else if (result.equals("doubleEmail")) {
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Email already in use, try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Register.html");
			rd.include(request, response);
		}
		else if (result.equals("matchPass")) {
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Passwords do not match, try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Register.html");
			rd.include(request, response);
		}
		else if (result.equals("passLength")) {
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Password must have at least 8 characters</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Register.html");
			rd.include(request, response);
		}
		//Redirect to enter addresses
		else if (result.equals("complete")) {
			RegisteredUser user = new RegisteredUser(firstName, lastName, email, CryptWithMD5.cryptWithMD5(pass));
			user.setID();
			Cookie userCookie = new Cookie("userID", ""+user.getID());
			userCookie.setMaxAge(60*60);
			userCookie.setPath("/Cinema_EBooking");
			response.addCookie(userCookie);
			RequestDispatcher rd = request.getRequestDispatcher("AddAddress.html");
			rd.include(request, response);
		}
		else{
			RequestDispatcher rd = request.getRequestDispatcher("Login.html");
			out.println("<font color=red>Fatal error occured during account creation</font>");
			rd.include(request, response);
		}
	}
	
	public String register(String firstName, String lastName, String email, String pass, String passCheck, String promo) throws Exception{
	
		AccountDB accountDB = new AccountDB();
		int promoNo;
		RegisteredUser user;
		
		if(firstName.equals("") || lastName.equals("") || email.equals("") || pass.equals("") || passCheck.equals("")){
			return "fillFields";
		}
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			return "invalidEmail";
		}
		ResultSet userList;
		try{
			userList = accountDB.getList(); //Access database to get list of emails
		}catch (Exception e){
			System.out.print("Error");
			return "error";
		}
		//Check for email uniqueness
		while (userList.next()) {
			String emailCheck = userList.getString("Email");
			if(email.toLowerCase().equals(emailCheck.toLowerCase())){
				return "doubleEmail";
			}
		}
		if (!pass.equals(passCheck)){
			return "matchPass";
		} else if (pass.length()<8){
			return "passLength";
		}
		if (promo==null)
			promoNo = 0;
		else
			promoNo = 1;
		
		//Enter user in Database
		user = new RegisteredUser(firstName, lastName, email, CryptWithMD5.cryptWithMD5(pass));
		int randomNum = java.util.concurrent.ThreadLocalRandom.current().nextInt(100000, 1000000);
		accountDB.addAccount(user, promoNo, 4, 2, randomNum);
		user.setID();
		Mailer.send("E-booking Confirmation Email", "AccountID: " + user.getID() + "\nClick the following link to confirm your account creation:\n http://localhost:8080/Cinema_EBooking/ConfirmAccount.html\nConfirmation code is "+ randomNum, InternetAddress.parse(email), Message.RecipientType.TO);
		return "complete";
	}

}