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
@WebServlet("/AddTicketToOrderController")
public class AddTicketToOrderController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int tickets = 0, child = 0, adult = 0, senior = 0;
		
		int id=0, movieID=0;
		long time=0L;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if(cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieShowTime"))
				time = Long.parseLong(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieID"))
				movieID = Integer.parseInt(cookies[i].getValue());
		}
		
		if(id==0){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>You need to be logged in to access this page</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Login.html");
			rd.include(request, response);
			return;
		}
		
		if(time==0 || movieID==0){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>That's an error on our end, please try again later</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("MovieTheaterPage.html");
			rd.include(request, response);
			return;
		}
		
		try{
			tickets = Integer.parseInt(request.getParameter("tickets"));
		} catch(Exception e){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Must select at least one ticket.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ViewAddTicket");
			rd.include(request, response);
			return;
		}
		try{
			child = Integer.parseInt(request.getParameter("child"));
		} catch (Exception e){
			child = 0;
		}
		try{
			adult = Integer.parseInt(request.getParameter("adult"));
		} catch (Exception e){
			adult = 0;
		}
		try{
			senior = Integer.parseInt(request.getParameter("senior"));
		} catch (Exception e){
			senior = 0;
		}
		
		if (child+senior+adult!=tickets){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Number of child, adult and senior tickets must equal total tickets!</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ViewAddTicket");
			rd.include(request, response);
			return;
		}
		
		int seatCount=0;
		for(int i=1; i<=70; i++){
			String seat = request.getParameter("seat" + i);
			if(seat!=null){
				seatCount++;
			}
		}
		
		if (seatCount!=tickets){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Number of seats selected does not equal total number of tickets!</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ViewAddTicket");
			rd.include(request, response);
			return;
		}
		
		OrderDB oDB = new OrderDB();
		MovieShowDB msDB = new MovieShowDB();
		TicketDB tDB = new TicketDB();
		Timestamp ts = new Timestamp(time);
		int orderID=0;
		int movieShowID =0;
		try{
			orderID = oDB.startOrder(id);
			movieShowID = msDB.movieShowIDFromMovieIDAndTime(movieID, ts);
		} catch (Exception e){
			
		}
		
		int childCheck=0;
		int adultCheck=0;
		int seniorCheck=0;
		for(int i=1; i<=70; i++){
			String seat = request.getParameter("seat" + i);
			if(seat!=null){
				Ticket t = null;
				if(childCheck<child){
					t = new Ticket(1, i, movieShowID, ts);
					childCheck++;
				} else if(adultCheck<adult){
					t = new Ticket(2, i, movieShowID, ts);
					adultCheck++;
				} else if(seniorCheck<senior){
					t = new Ticket(3, i, movieShowID, ts);
					seniorCheck++;
				}
				try{
					tDB.addTicket(t, orderID);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("ViewCheckout");
		rd.include(request, response);
	}

}
