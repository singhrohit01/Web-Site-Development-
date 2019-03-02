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
import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.DatabaseAccessor.MovieShowDB;
import CinemaEbooking.DatabaseAccessor.OrderDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.TicketDB;
import CinemaEbooking.DatabaseAccessor.TicketTypeDB;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Status;

/**
 * Servlet implementation class ViewUserController
 */
@WebServlet("/ViewReportsController")
public class ViewReportsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RequestDispatcher rd = request.getRequestDispatcher("ReportsTable.html");
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
		
		MovieShowDB msDB = new MovieShowDB();
		MovieDB mDB = new MovieDB();
		TicketDB tDB = new TicketDB();
		OrderDB oDB = new OrderDB();
		ResultSet rs = null;
		ResultSet rsOrder = null;
		double totalPrice = 0;
		try{
			rs = msDB.getShows();
			while(rs.next()){
				out.print("<tr>");
				out.print("<td>" + mDB.getMovieName(rs.getInt("MovieID")) + "</td>");
				out.print("<td>" + rs.getInt("MovieShowID") + "</td>");
				out.print("<td>" + rs.getTimestamp("ShowStartTime").toLocaleString() + "</td>");
				rsOrder = oDB.getList();
				double price=0;
				while(rsOrder.next()){
					ResultSet rsTicket = tDB.getList();
					while(rsTicket.next()){
						if(rsTicket.getInt("OrderID")==rsOrder.getInt("OrderID")){
							if(rs.getInt("MovieShowID") == rsTicket.getInt("MovieShowID") && rs.getTimestamp("ShowStartTime").compareTo(rsTicket.getTimestamp("ShowStartTime"))==0){
								price += rsOrder.getDouble("TotalPrice");
								break;
							}
						}
					}
				}
				totalPrice += price;
				out.print("<td>$" + ((""+ price).substring((""+ price).indexOf(".")  + 1).length() > 1 ? price : "" + price + "0") + "</td>");
				out.print("</tr>");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		totalPrice = (int)(totalPrice*100);
		totalPrice = totalPrice/100;
		out.print("<tr><td>&emsp;</td><td></td><td></td><td></td></tr>");
		out.print("<tr><td><b>Total Sales</b></td><td></td><td></td><td><b>$" + ((""+ totalPrice).substring((""+ totalPrice).indexOf(".")  + 1).length() > 1 ? totalPrice : "" + totalPrice + "0") + "</b></td></tr></table>");
		
	}

}
