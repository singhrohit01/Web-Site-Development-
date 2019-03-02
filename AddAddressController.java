package CinemaEbooking.Servlet.RegisterLogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.AddressDB;
import CinemaEbooking.DatabaseAccessor.DataManager;
import CinemaEbooking.Entity.Address;
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class Register
 */
@WebServlet("/AddAddressController")
public class AddAddressController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int zip;
		
		//Getting parameters from form
		String street = request.getParameter("street");
		boolean apt = (request.getParameter("apt")==null ? false : true);
		String aptNo;
		if(apt)
			aptNo = request.getParameter("aptNo");
		else
			aptNo = null;
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		try{
			zip = Integer.parseInt(request.getParameter("zip"));
		} catch (NumberFormatException e){
			zip = 0;
		}
		int id=0;
		
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
		}
		
		String result = null;
		try{
			result = addAddress(street, aptNo, city, state, zip, id);
		}catch (Exception e){
			e.printStackTrace();
		}
		if (result.equals("fillFields")) {
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Fill out all fields before submitting.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddAddress.html");
			rd.include(request, response);
		}
		else if (result.equals("zipFormat")) {
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Submit a proper zip code</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddAddress.html");
			rd.include(request, response);
		}
		else if (result.equals("invalidState")) {
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Please select a state</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddAddress.html");
			rd.include(request, response);
		}
		else if (result.equals("complete")) {
			try{
				Address address = new Address(street, aptNo, city, state, zip);
				AddressDB addressDB = new AddressDB();
				addressDB.addAddress(address, id);
			}catch(Exception e){
				e.printStackTrace();
			}
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font>You may add another address or click next to continue</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddAddress.html");
			rd.include(request, response);
		}
		
		else{
			RequestDispatcher rd = request.getRequestDispatcher("Login.html");
			out.println("<font color=red>Fatal error occured while adding address</font>");
			rd.include(request, response);
		}
	}
	
	public String addAddress(String street, String aptNo, String city, String state, int zip, int userID) throws Exception{
		
		if(street.equals("") || (!(aptNo==null) && aptNo.equals("")) || city.equals("") || state.equals("SO")){
			return "fillFields";
		}
		if(zip/10000 == 0){
			return "zipFormat";
		}
		if (state.equals("SO")){
			return "invalidState";
		}
		return "complete";
	}
}
	
	//Verify passwords match