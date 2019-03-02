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
import CinemaEbooking.Entity.Seat;

/**
 * Servlet implementation class ViewTicketsController
 */
@WebServlet("/ViewTicketsController")
public class ViewTicketsController extends HttpServlet {

	String cssLocation = request.getContextPath() + "/theaterstyle.css"; //keep it in same folder
    String addCss = "<link rel='stylesheet' type='text/css' href='" + cssLocation + "'>";
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		TicketDB tDB = new TicketDB();
		MovieShowDB msDB = new MovieShowDB();
		int movieID=0, check=0;
		long time=0L;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("movieID"))
				movieID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("movieShowTime"))
				time = Long.parseLong(cookies[i].getValue());
			else if(cookies[i].getName().equals("userID"))
				check = Integer.parseInt(cookies[i].getValue());
		}
		
		if(check==0){
			out.println("<meta http-equiv='refresh' content='6;URL=Login.html'>");
			out.println("<p style='color:black;'>Please sign in to order tickets!<br>If you don't have an account click <a href=\"Register.html\">here</a> to register.<br>Otherwise, please wait while you are being redirected...</p>");
			return;
		}
		
		if(movieID==0 || time==0){
			out.println("<meta http-equiv='refresh' content='3;URL=MovieTheaterPage.html'>");
			out.println("<p style='color:black;'>Cookies expired, please try searching again.<br>Please wait as you're being redirected</p>");
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("AddTicket.html");
		rd.include(request, response);
		
		OrderDB oDB = new OrderDB();
		Timestamp ts = new Timestamp(time);
		try{
			oDB.clearEmptyOrder(check);
			out.println(addCss);
			for(int i=0; i<10; i++){
				out.print("<tr>");
				for(int j=0; j<7; j++){
					if(tDB.isSeatOccupied(70-(i*7 + 6-j), ts, msDB.movieShowIDFromMovieIDAndTime(movieID, ts))){
						Seat s = new Seat(70-(i*7 + 6-j));
						out.print("<td><input style=\"float: left; margin-top: 5px;\" disabled=\"disabled\" type=\"checkbox\" class=\"sitting\" name=\"seat" + (70-(i*7 + 6-j)) + "\" style=\"vertical-align: bottom;\"><div style=\"margin-left: 20px;\"><font color=\"black\">" + s.toString() + "&nbsp;</font></div></td>");
					}
					else{
						Seat s = new Seat(70-(i*7 + 6-j));
						out.print("<td><input style=\"float: left; margin-top: 5px;\" type=\"checkbox\" class=\"sitting\" name=\"seat" + (70-(i*7 + 6-j)) + "\" style=\"vertical-align: bottom;\"><div style=\"margin-left: 15px;\">" + s.toString() + "&nbsp;</div></td>");
					}
				}
				out.print("</tr>");
			}
			rd = request.getRequestDispatcher("RestOfTicket.html");
			rd.include(request, response);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
