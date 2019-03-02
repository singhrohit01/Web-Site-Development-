package CinemaEbooking.Servlet.Booking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.MovieShowDB;
import CinemaEbooking.Entity.Movie;

/**
 * Servlet implementation class ShowMovieInfoController
 */
@WebServlet("/ShowMovieInfoController")
public class ShowMovieInfoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		int movieID=0;
		long time=0L;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("movieID"))
				movieID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("date"))
				time = Long.parseLong(cookies[i].getValue());
		}
		
		
		if(movieID==0){
			out.println("<meta http-equiv='refresh' content='3;URL=MovieTheaterPage.html'>");
			out.println("<p style='color:black;'>Cookies expired, please try searching again.<br>Please wait while you are being redirected...</p>");
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("MovieInfo.html");
		rd.include(request, response);
		
		Movie movie = new Movie(movieID);
		Date day = null;
		day = new Date(2018-1900, 11, 1);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		if (time==0)
			date = new java.util.Date();
		else
			 date = new java.util.Date(time);
		
		String stringDay = request.getParameter("showday");
		try{
			day = Date.valueOf(stringDay);
		} catch (IllegalArgumentException e){
			day = new Date(date.getTime());
		}
		
		out.print("<h1 style=\"font-size: 35px; padding-left: 10%\"><b>" + movie.getMovieTitle() + "</b></h1>");
		out.print("<form action=\"MovieInfo\" method=\"post\" onclick=\"setCookieDate("+ date.getTime() + ");\">");
		
		out.print("<div style=\"float: left; padding-left: 10%\">Showday&nbsp;&nbsp;</div><input type=\"date\" name=\"showday\" value=\"" + dateFormat.format(day) + "\">");
		out.print("&nbsp;<input type=\"submit\" value=\"Change Day\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black\"></div></form>");
		out.print("<p><img src=\"" + movie.getImage() + "\" style=\"margin-left: 10px; margin-right: 20px;  margin-bottom: 20px; float:left; width:250px; height:400px\" title=\"" + movie.getMovieTitle() + " Image\" alt=\"Movie Image\">");
		
		out.print("<div style=\"margin-right: 30px;\"><font size=\"+1\">Showtimes for " + day.toLocaleString().substring(0, day.toLocaleString().indexOf(":")-2) +"<br>");
		
		MovieShowDB msDB = new MovieShowDB();
		boolean showtimeForDay = false;
		Timestamp[] shows;
		try{
			shows = msDB.getShowsForMovieOnDate(movieID, day);
			out.print("<div>");
			for(int i=0; i<shows.length; i++){
				showtimeForDay = true;
				out.print("<form style=\"float: left\" action=\"ViewAddTicket\" method=\"post\" onclick=\"setCookieDateTime("+ shows[i].getTime() +")\"><button type=\"submit\" class=\"w3-bar-item w3-button w3-padding-large w3-hover-text-blue w3-hover-black\"");
				String temp = shows[i].toLocaleString().substring(shows[i].toLocaleString().indexOf(",")+7, shows[i].toLocaleString().indexOf(":")+3);
				if(shows[i].before(new Timestamp(System.currentTimeMillis())))
					out.print("disabled=\"disable\"");
				out.print(">");
				out.print((temp.indexOf("0")==0 ? temp.substring(1) : temp) + " PM</button></form>&nbsp;&nbsp;");
			}
			out.print("</div>");
		} catch (Exception e){
			e.printStackTrace();
		}
		if(!showtimeForDay){
			out.print("<b>No showtimes available for this movie on this day</b>");
		}
		
		out.print("</font><br><br>");
		out.print("" + movie.getSynopsis() + "<br><br>");
		out.print(movie.getLength()/60 + " HR " + movie.getLength()%60 + " MIN<br>");
		out.print("Rated " + movie.getRating() + "<br><br>");
		out.print("<b>Cast</b>: " + movie.getCast() + "<br><br>");
		out.print("<a href=\"" + movie.getTrailer() + "\" target=\"_blank\">Play Trailer</a></p>");
		out.print("</div>");
		out.print("</body>");
		out.print("</html>");
	}

}
