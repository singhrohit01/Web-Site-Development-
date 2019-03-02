package CinemaEbooking.Servlet.Profile;

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

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.CardDB;
import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.DatabaseAccessor.MovieShowDB;
import CinemaEbooking.DatabaseAccessor.OrderDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.TicketDB;
import CinemaEbooking.Entity.Card;
import CinemaEbooking.Entity.Movie;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Seat;
import CinemaEbooking.Entity.Status;

/**
 * Servlet implementation class ViewUserController
 */
@WebServlet("/ViewOrdersController")
public class ViewOrdersController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		int id=0;
		Cookie[] cookies= request.getCookies();
		try{
			for(int i=0; i<cookies.length; i++){
				if (cookies[i].getName().equals("userID"))
					id = Integer.parseInt(cookies[i].getValue());
			}
		} catch (Exception e){
			response.sendRedirect("MovieTheaterPage.html");
			return;
		}
		
		if (id==0){
			response.sendRedirect("MovieTheaterPage.html");
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("OrdersTable.html");
		rd.include(request, response);
		
		OrderDB oDB = new OrderDB();
		PromoDB pDB = new PromoDB();
		CardDB cDB = new CardDB();
		TicketDB tDB = new TicketDB();
		MovieShowDB msDB = new MovieShowDB();
		MovieDB mDB = new MovieDB();
		ResultSet rs = null;
		try{
			int check = oDB.getEmptyOrder(id);
			if(check!=0){
				oDB.clearEmptyOrder(id);
			}
			rs = oDB.getOrderList(id);
			while(rs.next()){
				out.print("<tr>");
				out.print("<td>" + rs.getInt("OrderID") + "</td>");
				Card c = null;
				try{
					c = cDB.getCard(rs.getInt("CardID"));
				} catch (Exception e){
					c = null;
				}
				if(c!=null)
					out.print("<td>" + c.toString() + "</td>");
				else
					out.print("<td>Card used for purchase has been deleted</td>");
				ResultSet rsTicket = tDB.getList();
				int movieID = 0;
				Movie movie = null;
				Timestamp ts = null;
				while(rsTicket.next()){
					if(rsTicket.getInt("OrderID") == rs.getInt("OrderID")){
						movieID = msDB.movieIDFromMovieShowIDAndTime(rsTicket.getInt("MovieShowID"), rsTicket.getTimestamp("ShowStartTime"));
						ts = rsTicket.getTimestamp("ShowStartTime");
						movie = new Movie(movieID);
						out.print("<td>" + movie.getMovieTitle() + "</td>");
						out.print("<td>" + ts.toLocaleString().substring(0, ts.toLocaleString().indexOf(":") + 3) +" PM</td>");
						break;
					}
				}
				out.print("<td width=\"20%\">");
				rsTicket.beforeFirst();
				while (rsTicket.next()){
					if(rsTicket.getInt("OrderID") == rs.getInt("OrderID")){
						Seat s = new Seat(rsTicket.getInt("SeatID"));
						if(rsTicket.getInt("TicketType")==1){
							out.print("Child Ticket " + s.toString() + "<br>");
						}
						else if(rsTicket.getInt("TicketType")==2){
							out.print("Adult Ticket " + s.toString() + "<br>");
						}
						else if(rsTicket.getInt("TicketType")==3){
							out.print("Senior Ticket " + s.toString() + "<br>");
						}
					}
				}
				out.print("</td>");
				int promoID=0;
				try{
					promoID = rs.getInt("PromoID");
					PromotionCode p = pDB.getPromo(promoID);
					out.print("<td>" + p.getName() + " " + p.getSalePercent() + "%</td>");
				} catch (Exception e){
					out.print("<td>None</td>");
				}
				out.print("<td>$" + rs.getDouble("TotalPrice") + "</td>");
				out.print("<td><button onclick=\"toggleRefund(" + rs.getInt("OrderID") + ")\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">Refund</button></td></tr>");
			}
			out.print("</table>");
			out.print("</div>");
			out.print("<div id=\"refund\" class=\"w3-left\" style=\"display: none\">");
			out.print("Are you sure you want to refund this purchase?<br>");
			out.print("<div class=\"button-container\">");
			out.print("<button onclick=\"cancelRefund()\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">Cancel</button>");
			out.print("<form action=\"Refund\" method=\"post\">");
			out.print("<input type=\"submit\" name=\"refund\" value=\"Confirm Refund\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 50px; padding-right: 50px\">");
			out.print("</div>");
			out.print("</div>");
			out.print("</body>");
			out.print("</html>");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
