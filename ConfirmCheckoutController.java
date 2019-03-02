package CinemaEbooking.Servlet.Booking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.MovieShowDB;
import CinemaEbooking.DatabaseAccessor.OrderDB;
import CinemaEbooking.DatabaseAccessor.TicketDB;
import CinemaEbooking.Entity.Ticket;

/**
 * Servlet implementation class AddPromoController
 */
@WebServlet("/ConfirmCheckoutontroller")
public class ConfirmCheckoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		int id=0, movieID=0, cardID=0, promoID=0, orderID=0;
		double price = 0;
		long time=0L;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if(cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieShowTime"))
				time = Long.parseLong(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieID"))
				movieID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("cardID"))
				cardID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("promoID"))
				promoID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("priceID"))
				price = Double.parseDouble(cookies[i].getValue());
			else if(cookies[i].getName().equals("orderID"))
				orderID = Integer.parseInt(cookies[i].getValue());

		}
		
		if(id==0){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>You need to be logged in to access this page</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Login");
			rd.include(request, response);
			return;
		}
		
		if(time==0 || movieID==0 || cardID==0){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>That's an error on our end, please try again later</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("MovieTheaterPage.html");
			rd.include(request, response);
			return;
		}
		
		OrderDB oDB = new OrderDB();
		try{
			if(orderID==0){
				orderID = oDB.getEmptyOrder(id);
				oDB.fillOrder(orderID, cardID, promoID, price);
				Cookie orderCookie = new Cookie("orderID", ""+ orderID);
				orderCookie.setPath("/");
				orderCookie.setMaxAge(60*60);
				response.addCookie(orderCookie);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("Receipt");
		rd.include(request, response);
		
	}

}
