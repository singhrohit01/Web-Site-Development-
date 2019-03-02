package CinemaEbooking.Servlet.Booking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
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
import CinemaEbooking.DatabaseAccessor.MovieShowDB;
import CinemaEbooking.DatabaseAccessor.OrderDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.TicketDB;
import CinemaEbooking.DatabaseAccessor.TicketTypeDB;
import CinemaEbooking.Entity.Card;
import CinemaEbooking.Entity.Mailer;
import CinemaEbooking.Entity.Movie;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Seat;

/**
 * Servlet implementation class ViewReceiptController
 */
@WebServlet("/ViewReceiptController")
public class ViewReceiptController extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		int id=0, movieID=0, promoID=0, cardID=0, orderID=0;
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
			else if(cookies[i].getName().equals("orderID"))
				orderID = Integer.parseInt(cookies[i].getValue());
		}
		
		if (id==0){
			response.sendRedirect("MovieTheaterPageLoggedIn.html");
			return;
		}
		
		if(orderID==0){
			out.println("<meta http-equiv='refresh' content='0;URL=Receipt'>");
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
		
		RequestDispatcher rd = request.getRequestDispatcher("Receipt.html");
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
		
		out.print("Order number: " + orderID + "<br>");
		out.print("Movie: " + movieName + "<br>Showtime: " + ts.toLocaleString().substring(0, ts.toLocaleString().indexOf(":") + 3) +" PM<br>");
		out.print("Card: " + card.getName() + ", Last four digits on card " + card.lastFour() + " Exp : " + card.getExpMonth() + "/" + card.getExpYear()  +"<br><br>");

        out.print("<b>Tickets</b>: <br>");
                
        PromoDB pDB = new PromoDB();
        TicketDB tDB= new TicketDB();
        TicketTypeDB ttDB= new TicketTypeDB();
		ResultSet rs2 = null;
		double totalPrice = 0;
		try{
			double ticketPrice = 0.0;
			rs2 = tDB.getList();
			while(rs2.next()){
				if(rs2.getInt("OrderID")==orderID){
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
					out.print("&emsp;&emsp;" + ticketType + " ticket &emsp;Seat: " + seat.toString() + "&emsp;$" + ticketPrice + "<br>");
				}
			}
			out.print("<br>");
			if(promoID!=0){
				PromotionCode p = pDB.getPromo(promoID);
				double promoSavings = totalPrice * (p.getSalePercent()/100.0);
				promoSavings = (int)(promoSavings * 100);
				promoSavings = promoSavings/100;
				totalPrice = totalPrice - promoSavings;
				out.print("&emsp;" + p.getName() + " " + p.getSalePercent() + "%&emsp;-$" + promoSavings + "<br>");
			}
			FeeDB fDB = new FeeDB();
			String totalString = "" + totalPrice;
			totalString = (totalString.substring(totalString.indexOf(".") + 1).length() == 1 ? totalString+="0" : totalString);
			totalString = (totalString.substring(totalString.indexOf(".") + 1).length() > 2 ? totalString.substring(0, totalString.indexOf(".") + 3) : totalString);
			out.print("&emsp;Subtotal &emsp;$" + totalString + "<br>");
			double tax = totalPrice * .06;
			tax = (int)(tax*100);
			tax = tax/100;
			String taxString = "" + tax;
			taxString = (taxString.substring(taxString.indexOf(".") + 1).length() == 1 ? taxString+="0" : taxString);
			out.print("&emsp;Tax (6%)&emsp;$" + taxString + "<br>");
			double fee = fDB.getFee();
			totalPrice += fee;
			totalPrice = (int)(totalPrice*100);
			totalPrice = totalPrice/100;
			out.print("&emsp;Booking Fee&emsp;$" + fee + "<br>");
			totalPrice += tax;
			totalPrice = (int)(totalPrice*100);
			totalPrice = totalPrice/100;
			totalString = "" + (totalPrice);
			totalString = (totalString.substring(totalString.indexOf(".") + 1).length() == 1 ? totalString+="0" : totalString);
			out.print("&emsp;&emsp;Total &emsp;$" + totalString + "<br><br>");
			
			out.print("To get a refund on your order go to the Theater Home page.<br>Once you've logged in, "
					+ "navigate to <br><b>Edit Profile</b> --> <b>Order History</b><br>"
					+ "then select the order for refund and click <b>Refund</b><br>"
					+ "<font color=\"red\" size=\"-1\">*Note: this can only be done up to 1 hour (60 mins) prior to showtime*</font>");

		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		try{
			sendReceipt(id, orderID);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		out.print("</body></html>");
		
		
	}
	
	public void sendReceipt(int id, int orderID) throws Exception{
		String subject, body;
		
		subject = "Booking Confirmation";
		body = "Your booking order has been processed:\n\n";
		
		OrderDB oDB = new OrderDB();
		PromoDB pDB = new PromoDB();
		CardDB cDB = new CardDB();
		TicketDB tDB = new TicketDB();
		MovieShowDB msDB = new MovieShowDB();
		ResultSet rs = null;
		ResultSet rsTicket = null;
		try{
			int check = oDB.getEmptyOrder(id);
			if(check!=0){
				oDB.clearEmptyOrder(id);
			}
			rs = oDB.getOrderList(id);
			while(rs.next()){
				if(orderID == rs.getInt("OrderID")){
					body += "Order ID: " + rs.getInt("OrderID");
					Card c = null;
					try{
						c = cDB.getCard(rs.getInt("CardID"));
					} catch (Exception e){
						c = null;
					}
					if(c!=null)
						body += "\nCard used: " + c.toString();
					else
						body += "\nCard used for this account has been deleted, come to the theater to receive a cash payment of your refund";
					rsTicket = tDB.getList();
					int movieID = 0;
					Movie movie = null;
					Timestamp ts = null;
					while(rsTicket.next()){
						if(rsTicket.getInt("OrderID") == rs.getInt("OrderID")){
							movieID = msDB.movieIDFromMovieShowIDAndTime(rsTicket.getInt("MovieShowID"), rsTicket.getTimestamp("ShowStartTime"));
							ts = rsTicket.getTimestamp("ShowStartTime");
							movie = new Movie(movieID);
							body += "\nMovie: " + movie.getMovieTitle();
							body += "\nShowtime: " + ts.toLocaleString().substring(0, ts.toLocaleString().indexOf(":") + 3) +" PM";
							break;
						}
					}
					rsTicket.beforeFirst();
					body += "\nTickets: ";
					while (rsTicket.next()){
						if(rsTicket.getInt("OrderID") == rs.getInt("OrderID")){
							Seat s = new Seat(rsTicket.getInt("SeatID"));
							if(rsTicket.getInt("TicketType")==1){
								body += "\n\tChild Ticket " + s.toString();
							}
							else if(rsTicket.getInt("TicketType")==2){
								body += "\n\tAdult Ticket " + s.toString();
							}
							else if(rsTicket.getInt("TicketType")==3){
								body += "\n\tSenior Ticket " + s.toString();
							}
						}
					}
					int promoID=0;
					try{
						promoID = rs.getInt("PromoID");
						PromotionCode p = pDB.getPromo(promoID);
						body+= "\nPromotion Code: " + p.getName() + "\t" + p.getSalePercent() + "%";
					} catch (Exception e){
						body += "";
					}
					body += "\nTotal Price: $" + rs.getDouble("TotalPrice");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		body += "\n\nThank you for your support!";
		body += "\nTo get a refund on your order go to the Theater Home page. Once you've logged in, "
				+ "navigate to \nEdit Profile --> Order History\n"
				+ "then select the order for refund and click \"Refund\"";
		
		InternetAddress[] recipients = new InternetAddress[1];
		
		RegisteredUser user = new RegisteredUser(id);
		
		recipients[0] = new InternetAddress(user.getEmail());
		Mailer.send(subject, body, recipients, Message.RecipientType.BCC);

	}

}
