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
import CinemaEbooking.DatabaseAccessor.FeeDB;
import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.DatabaseAccessor.OrderDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.TicketDB;
import CinemaEbooking.DatabaseAccessor.TicketTypeDB;
import CinemaEbooking.Entity.Card;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.Seat;

/**
 * Servlet implementation class CheckoutConfirmation
 */
@WebServlet("/ViewCheckoutController")
public class ViewCheckoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		int id=0, movieID=0, promoID=0, cardID=0;
		long time=0L;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if(cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieShowTime"))
				time = Long.parseLong(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieID"))
				movieID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("promoID"))
				promoID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("cardID"))
				cardID = Integer.parseInt(cookies[i].getValue());
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
		
		if(promoID==0){
			out.println("<meta http-equiv='refresh' content='0;URL=Checkout'>");
			return;
		}
		
		if(time==0 || movieID==0 || cardID==0){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Cookies expired, please try again later</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("MovieTheaterPage.html");
			rd.include(request, response);
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("CheckoutConfirmation.html");
		rd.include(request, response);
		
		MovieDB mDB = new MovieDB();
        CardDB cDB = new CardDB();
        Card card = null;
		ResultSet rs = null;
		String movieName=null;
		Timestamp ts = new Timestamp(time);
		try{
    		card = cDB.getCard(cardID);
			rs = mDB.getMovieList();
			while(rs.next()){
				if(rs.getInt("MovieID")==movieID)
					movieName = rs.getString("Movie");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		out.print("<h2 class=\"w3-margin w3-dark-grey\" style=\"font-size:24px;\">Movie: " + movieName + " | Showtime: " + ts.toLocaleString().substring(0, ts.toLocaleString().indexOf(":") + 3) +" PM<br>");
		out.print("Card: " + card.getName() + ", Last four digits on card " + card.lastFour() + " Exp : " + card.getExpMonth() + "/" + card.getExpYear()  +"</h2>");

        out.print("<h3 class=\"w3-margin\" style=\"font-size:36px;\">Confirm Checkout</h3>");
        
        out.print("<ul>");
        
        OrderDB oDB = new OrderDB();
        PromoDB pDB = new PromoDB();
        TicketDB tDB= new TicketDB();
        TicketTypeDB ttDB= new TicketTypeDB();
		ResultSet rs2 = null;
		double totalPrice = 0;
		try{
			double ticketPrice = 0.0;
			rs2 = tDB.getList();
			while(rs2.next()){
				if(rs2.getInt("OrderID")==oDB.getEmptyOrder(id)){
					String ticketType = "";
					switch(rs2.getInt("TicketType")){
					case 1:
						ticketType += "Child";
						ticketPrice = ttDB.getTicketPrice(1);
						break;
					case 2:
						ticketType += "Adult";
						ticketPrice = ttDB.getTicketPrice(2);
						break;
					case 3:
						ticketType += "Senior";
						ticketPrice = ttDB.getTicketPrice(3);
						break;
					}
					totalPrice += ticketPrice;
					Seat seat = new Seat(rs2.getInt("SeatID"));
					out.print("<li>" + ticketType + " ticket &emsp;Seat: " + seat.toString() + "&emsp;&emsp;&emsp;&emsp;$" + ticketPrice + "</li>");
				}
			}
			if(promoID!=0){
				PromotionCode p = pDB.getPromo(promoID);
				double promoSavings = totalPrice * (p.getSalePercent()/100.0);
				promoSavings = (int)(promoSavings * 100);
				promoSavings = promoSavings/100;
				totalPrice = totalPrice - promoSavings;
				out.print("<br><li>" + p.getName() + " " + p.getSalePercent() + "%&emsp;&emsp;&emsp;&emsp;&emsp;-$" + promoSavings + "</li>");
			}
			FeeDB fDB = new FeeDB();
			String totalString = "" + totalPrice;
			totalString = (totalString.substring(totalString.indexOf(".") + 1).length() == 1 ? totalString+="0" : totalString);
			totalString = (totalString.substring(totalString.indexOf(".") + 1).length() > 2 ? totalString.substring(0, totalString.indexOf(".") + 3) : totalString);
			out.print("<br><li>Subtotal &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;$" + totalString + "</li>");
			double tax = totalPrice * .06;
			tax = (int)(tax*100);
			tax = tax/100;
			String taxString = "" + tax;
			taxString = (taxString.substring(taxString.indexOf(".") + 1).length() == 1 ? taxString+="0" : taxString);
			out.print("<li>&emsp;Tax (6%)&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;$" + taxString + "</li>");
			double fee = fDB.getFee();
			totalPrice += fee;
			totalPrice = (int)(totalPrice*100);
			totalPrice = totalPrice/100;
			out.print("<li>&emsp;Booking Fee&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;$" + fee + "</li>");
			totalPrice += tax;
			totalPrice = (int)(totalPrice*100);
			totalPrice = totalPrice/100;
			totalString = "" + (totalPrice);
			totalString = (totalString.substring(totalString.indexOf(".") + 1).length() == 1 ? totalString+="0" : totalString);
			out.print("<br><br><li>Total &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;$" + totalString + "</li>");
			
			
			out.print("</ul>");
			
			Cookie priceCookie = new Cookie("priceID", ""+ totalPrice);
			priceCookie.setPath("/");
			priceCookie.setMaxAge(60*60);
			response.addCookie(priceCookie);
			
			out.print("<form action=\"CheckoutConfirm\" method=\"post\">");
    		out.print("<button input=\"submit\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\">Confirm Purchase</button>&emsp;");
    		out.print("<a href=\"Movie\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 50px; padding-right: 50px\">Cancel</a>");
    		out.print("</form><br><br>");
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
