package CinemaEbooking.Servlet.Booking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.CardDB;
import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.Entity.Card;

/**
 * Servlet implementation class ViewTicketsController
 */
@WebServlet("/ViewPaymentMethodController")
public class ViewPaymentMethodController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		MovieDB mDB = new MovieDB();
		int movieID=0, id=0, cardID=0;
		long time=0L;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("movieID"))
				movieID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieShowTime"))
				time = Long.parseLong(cookies[i].getValue());
			else if(cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("cardID"))
				cardID = Integer.parseInt(cookies[i].getValue());
		}
		
		if(id==0){
			out.println("<meta http-equiv='refresh' content='6;URL=MovieTheaterPage.html'>");
			out.println("<p style='color:black;'>Please sign in to order tickets!<br>If you don't have an account click <a href=\"Register.html\">here</a> to register.<br>Otherwise, please wait while you are being redirected...</p>");
			return;
		}
		
		if(movieID==0 || time==0){
			out.println("<meta http-equiv='refresh' content='3;URL=MovieTheaterPage.html'>");
			out.println("<p style='color:black;'>Cookies expired, please try searching again.<br>Please wait while you are being redirected...</p>");
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("Checkout.html");
		rd.include(request, response);
		
		ResultSet rs = null;
		String movieName=null;
		Timestamp ts = new Timestamp(time);
		try{
			rs = mDB.getMovieList();
			while(rs.next()){
				if(rs.getInt("MovieID")==movieID)
					movieName = rs.getString("Movie");
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		
		out.print("<h2 class=\"w3-margin w3-dark-grey\" style=\"font-size:24px;\">Movie: " + movieName + " | Showtime: " + ts.toLocaleString().substring(0, ts.toLocaleString().indexOf(":") + 3) +" PM</h2>");
        out.print("<h3 class=\"w3-margin\" style=\"font-size:36px;\">Payment Method</h3>");
        
        CardDB cDB = new CardDB();
        ResultSet rs2 = null;
        if(cardID==0){
        	try{
        		rs2 = cDB.getUserCard(id);
        		while(rs2.next()){
        			cardID = rs2.getInt("CardID");
	        		Card card = cDB.getCard(cardID);
	        		out.print("<form action=\"MoveToPromo\" method=\"post\" onclick=\"setCookieCardLong("+ cardID +")\">");
	        		out.print("<font size=\"+1\">Would you like to use card ending in " + card.lastFour() + " Exp : " + card.getExpMonth() + "/" + card.getExpYear()  +"?&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>");
	        		out.print("<button input=\"submit\" class=\"w3-bar-item w3-button w3-padding-large w3-hover-text-blue w3-hover-black\">Use Card</button>");
	        		out.print("</form><br><br>");
        		}
        		if(cardID!=0 && cardID!=-1){
	        		out.print("<form action=\"ViewCheckout\" method=\"post\" onclick=\"setCookieCardLong(-1)\">");
        			out.print("<font size=\"+1\">Would you like to use another card?&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>");
	        		out.print("<button input=\"submit\" class=\"w3-bar-item w3-button w3-padding-large w3-hover-text-blue w3-hover-black\">Use Another Card</button>");
	        		out.print("</form>");
        		}
        	} catch (Exception e){
        		e.printStackTrace();
        	}
        }
        if(cardID==0 || cardID==-1){
        	rd = request.getRequestDispatcher("CardForm.html");
        	rd.include(request, response);
        }
	}
}
