package CinemaEbooking.Servlet.Profile;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.AddressDB;
import CinemaEbooking.DatabaseAccessor.CardDB;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Address;
import CinemaEbooking.Entity.Card;

/**
 * Servlet implementation class EditProfileController
 */
@WebServlet("/EditProfileController")
public class EditProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RequestDispatcher rd = request.getRequestDispatcher("EditProfile.html");
		rd.include(request, response);

		int id=0;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
		}
		
		AccountDB aDB = new AccountDB();
		RegisteredUser user=null;
		try{
			user = aDB.getUser(id);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		out.print("<h1> Welcome " + user.getFirstName() + " " + user.getLastName() + "!</h1><br>");
		out.print("<h2>Email : " + user.getEmail() + "</h2><br><br>");
		out.print("<h2><a href=\"ResetPassword\" class=\"w3-bar-item w3-button w3-padding-large w3-hover-text-blue w3-hover-black\">Change Password</a><br><br></h2>");
		out.print("<h2>Address(es) :<br></h2>");
		out.print("<form action=\"DeleteAddresses\" method=\"post\">");
		try{
			AddressDB addDB = new AddressDB();
			ResultSet rs = addDB.getUserAddress(id);
			while(rs.next()){
				int addressID = rs.getInt("AddressID");
				Address a = addDB.getAddress(addressID);
				out.print("&emsp;" + a.toString() + "<input style=\"float: left; margin-top: 5px;\" type=\"checkbox\" name=\"deleteAddress" + addressID + "\" style=\"vertical-align: bottom;\"><div style=\"margin-left: 20px;\"></div><br>");
			}
			out.print("<a href=\"AddAddressProfile.html\" value=\"AddAddress\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 50px; padding-right: 50px\">Add Address</a>");
			out.print("<input type=\"submit\" value=\"Delete Addresses\"class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">");
			out.print("</form>");
		}catch (Exception e){
			e.printStackTrace();
		}
		out.print("<br><h2>Card(s) :<br></h2>");
		out.print("<form action=\"DeleteCards\" method=\"post\">");
		try{
			CardDB cDB = new CardDB();
			ResultSet rs = cDB.getUserCard(id);
			while(rs.next()){
				int cardID = rs.getInt("CardID");
				Card c = cDB.getCard(cardID);
				out.print("&emsp;" + c.toString() + "<input style=\"float: left; margin-top: 5px;\" type=\"checkbox\" name=\"deleteCard" + cardID + "\" style=\"vertical-align: bottom;\"><div style=\"margin-left: 20px;\"></div><br>");
			}
			out.print("<a href=\"AddCardProfile.html\" value=\"AddCard\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 50px; padding-right: 50px\">Add Card</a>");
			out.print("<input type=\"submit\" value=\"Delete Cards\"class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">");
			out.print("</form>");
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
