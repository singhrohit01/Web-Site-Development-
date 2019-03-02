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
import CinemaEbooking.DatabaseAccessor.FeeDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.TicketTypeDB;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Status;

/**
 * Servlet implementation class ViewUserController
 */
@WebServlet("/ViewPricesController")
public class ViewPricesController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RequestDispatcher rd = request.getRequestDispatcher("PricesTable.html");
		rd.include(request, response);

		int id=0;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("adminID"))
				id = Integer.parseInt(cookies[i].getValue());
		}
		
		if (id==0){
			response.sendRedirect("MovieTheaterPageLoggedIn.html");
			return;
		}
		
		TicketTypeDB ttDB = new TicketTypeDB();
		try{
			for (int i=1; i<=3; i++){
				out.print("<tr>");
				out.print("<td>" + i + "</td>");
				switch(i){
				case 1:
					out.print("<td>Child</td>");
					break;
				case 2:
					out.print("<td>Adult</td>");
					break;
				case 3:
					out.print("<td>Senior</td>");
					break;
				}
				out.print("<td><input onchange=\"setTwoNumberDecimal(this)\" type=\"number\" step=\".01\" name =\"" + i + "\"value=\"" + ttDB.getTicketPrice(i) + "\"></td>");
				out.print("</tr>");
			}
			out.print("</table>");
			out.print("<input type=\"submit\" value=\"Update Prices\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">");
			out.print("</form>");
			out.print("</div>");
			out.print("<div class=\"w3-black\" style=\"margin-top: 10%\">");
			out.print("<h1 style=\"font-size: 25px; padding-left: 20%\">Fees</h1>");
			out.print("<form action=\"ChangeFees\" method=\"post\">");
			out.print("<table class=\"w3-left\" style=\"width: 60%\"><tr>");
			out.print("<td><b>Fee Name</b></td>");
			out.print("<td><b>Price</b></td></tr>");
			out.print("<tr><td>Online Booking</td>");
			FeeDB fDB = new FeeDB();
			out.print("<td><input onchange=\"setTwoNumberDecimal(this)\" type=\"number\" step=\".01\" name =\"booking\"value=\"" + fDB.getFee() + "\"></td></tr>");
			out.print("</table>");
			out.print("<input type=\"submit\" value=\"Update Fees\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">");
			out.print("</form>");
			out.print("</div>");
			out.print("</body>");
			out.print("</html>");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
