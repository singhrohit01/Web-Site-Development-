package CinemaEbooking.Servlet.Booking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
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
import CinemaEbooking.DatabaseAccessor.MovieShowDB;
import CinemaEbooking.DatabaseAccessor.OrderDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.TicketDB;
import CinemaEbooking.Entity.Card;
import CinemaEbooking.Entity.Mailer;
import CinemaEbooking.Entity.Movie;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Seat;

/**
 * Servlet implementation class RefundController
 */
@WebServlet("/RefundController")
public class RefundController extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		int id=0, orderID=0;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if(cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
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
		
		if(orderID==0){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Cookies expired, please try again later</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("MovieTheaterPage.html");
			rd.include(request, response);
			return;
		}
		
		OrderDB oDB = new OrderDB();
		try{
			ResultSet rs = oDB.getOrderList(id);
			while(rs.next()){
				if(rs.getInt("OrderID")==orderID){
					TicketDB tDB = new TicketDB();
					ResultSet rsTicket = tDB.getList();
					while(rsTicket.next()){
						if(rsTicket.getInt("OrderID")==orderID){
							Timestamp ts = rsTicket.getTimestamp("ShowStartTime");
							Timestamp currentPlus60 = new Timestamp(System.currentTimeMillis() + (60*60*1000));
							if(currentPlus60.after(ts)){
								out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
								out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
										+ "<font color=red>Refunds are not valid within 60 minutes of showtime</font>");
								out.print("</div>");
								RequestDispatcher rd = request.getRequestDispatcher("ViewOrders");
								rd.include(request, response);
								return;
							}
						}
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try{
			sendRefundEmail(id, orderID);
			oDB.deleteOrder(orderID);
			Cookie empty = new Cookie("orderID", null);
			empty.setMaxAge(0);
			response.addCookie(empty);
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=white>Refund processed, email has been sent</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ViewOrders");
			rd.include(request, response);
			return;
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendRefundEmail(int id, int orderID) throws Exception{
		String subject, body;
		
		subject = "Refund Confirmation";
		body = "Your refund for the following order has been processed:\n\n";
		
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

		body +="\n\nThank you for your support!";
		InternetAddress[] recipients = new InternetAddress[1];
		
		RegisteredUser user = new RegisteredUser(id);
		
		recipients[0] = new InternetAddress(user.getEmail());
		Mailer.send(subject, body, recipients, Message.RecipientType.BCC);

	}
}
