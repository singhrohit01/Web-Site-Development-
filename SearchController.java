package CinemaEbooking.Servlet.Booking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.GenreDB;
import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.DatabaseAccessor.MovieGenreDB;
import CinemaEbooking.DatabaseAccessor.MovieShowDB;
import CinemaEbooking.Entity.Rating;

/**
 * Servlet implementation class SearchController
 */
@WebServlet("/SearchController")
public class SearchController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RequestDispatcher rd = request.getRequestDispatcher("SearchResultsTable.html");
		rd.include(request, response);
		
		Date date = null;
		GenreDB gDB = new GenreDB();

		String query = request.getParameter("search");
		int genre = Integer.parseInt(request.getParameter("filter"));
		String dateString = request.getParameter("dateFilter");
		
		if(!dateString.equals("")){
			date = Date.valueOf(dateString);
		}
		
		if(!query.equals(""))
			out.print("<h1 style=\"font-size: 25px; padding-left: 20%\">Search results for: <b>" + query + "</b>");
		else
			out.print("<h1 style=\"font-size: 25px; padding-left: 20%\"><b>All Results</b>");
		
		if(genre!=0){
			try{
				out.print(" and " + gDB.getGenre(genre));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		if(date!=null){
			out.print(" on " + date.toLocaleString().substring(0, date.toLocaleString().indexOf(":")-2));
		}
		
		out.print("</h1>");
	    out.print("<table style=\"width: 90%; padding-left: 5%; padding-right: 5%\">");
			
		MovieDB mDB = new MovieDB();
		MovieGenreDB mgDB = new MovieGenreDB();
		MovieShowDB msDB = new MovieShowDB();
		ResultSet movies = null;
		try{
			movies = mDB.getMoviesFromQuery(query);
			if(genre==0 && date==null){
				while(movies.next()){
					Timestamp ts = new Timestamp(System.currentTimeMillis());
					date = new Date(ts.getYear(), ts.getMonth(), ts.getDate());
					Cookie dateCookie = new Cookie("date", ""+ date.getTime());
					dateCookie.setPath("/");
					dateCookie.setMaxAge(60*60*24*7); //Cookie lasts one week		
					response.addCookie(dateCookie);
					out.print("<tr>");
					out.print("<td><img src=\"" + movies.getString("Image") + "\" style=\"width:100px; height:150px\" title=\"" + movies.getString("Movie") + " Image\" alt=\"Movie Image\"></a></td>");
					out.print("<td width=\"10%\">" + movies.getString("Movie") + "</td>");
					out.print("<td>" + movies.getString("Synopsis") + "</td>");
					Rating rating = null;
					switch(movies.getInt("Rating")){
					case 1:
						rating = Rating.G;
						break;
					case 2:
						rating = Rating.PG;
						break;
					case 3:
						rating = Rating.PG13;
						break;
					case 4:
						rating = Rating.R;
						break;
					}
					out.print("<td width=\"10%\">Rated " + rating.toString() + "</td>");
					out.print("<td width=\"10%\"><a href=\"" + movies.getString("Trailer") + "\" target=\"_blank\">Play Trailer</a></td>");
					out.print("<td width=\"10%\"><form action=\"MovieInfo\" method=\"post\" onclick=\"setCookieID("+ movies.getInt("MovieID") + ");clearCookieDate();\"><input type=\"submit\" value=\"Get Tickets\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black\"></form></td>");
				}
			}else if (genre!=0 && date==null){
				while(movies.next()){
					if(mgDB.doesMovieHaveGenre(movies.getInt("MovieID"), genre)){
						Timestamp ts = new Timestamp(System.currentTimeMillis());
						date = new Date(ts.getYear(), ts.getMonth(), ts.getDate());
						Cookie dateCookie = new Cookie("date", ""+ date.getTime());
						dateCookie.setPath("/");
						dateCookie.setMaxAge(60*60*24*7); //Cookie lasts one week		
						response.addCookie(dateCookie);
						out.print("<tr>");
						out.print("<td><img src=\"" + movies.getString("Image") + "\" style=\"width:100px; height:150px\" title=\"" + movies.getString("Movie") + " Image\" alt=\"Movie Image\"></a></td>");
						out.print("<td width=\"10%\">" + movies.getString("Movie") + "</td>");
						out.print("<td>" + movies.getString("Synopsis") + "</td>");
						out.print("<td width=\"10%\"><form action=\"MovieInfo\" method=\"post\" onclick=\"setCookieID("+ movies.getInt("MovieID") + ");clearCookieDate();\"><input type=\"submit\" value=\"Get Tickets\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black\"></form></td>");
					}
				}
			}else if (genre==0 && date!=null){
				while(movies.next()){
					if(msDB.doesMovieShowOnDate(movies.getInt("MovieID"), date)){
						out.print("<tr>");
						out.print("<td><img src=\"" + movies.getString("Image") + "\" style=\"width:100px; height:150px\" title=\"" + movies.getString("Movie") + " Image\" alt=\"Movie Image\"></a></td>");
						out.print("<td width=\"10%\">" + movies.getString("Movie") + "</td>");
						out.print("<td>" + movies.getString("Synopsis") + "</td>");
						out.print("<td width=\"10%\"><form action=\"MovieInfo\" method=\"post\" onclick=\"setCookieID("+ movies.getInt("MovieID") + ");setCookieDate("+ date.getTime() + ");\"><input type=\"submit\" value=\"Get Tickets\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black\"></form></td>");
					}
				}
			}else{
				while(movies.next()){
					if(msDB.doesMovieShowOnDate(movies.getInt("MovieID"), date) && mgDB.doesMovieHaveGenre(movies.getInt("MovieID"), genre)){
						out.print("<tr>");
						out.print("<td><img src=\"" + movies.getString("Image") + "\" style=\"width:100px; height:150px\" title=\"" + movies.getString("Movie") + " Image\" alt=\"Movie Image\"></a></td>");
						out.print("<td width=\"10%\">" + movies.getString("Movie") + "</td>");
						out.print("<td>" + movies.getString("Synopsis") + "</td>");
						out.print("<td width=\"10%\"><form action=\"MovieInfo\" method=\"post\" onclick=\"setCookieID("+ movies.getInt("MovieID") + ");setCookieDate("+ date.getTime() + ");\"><input type=\"submit\" value=\"Get Tickets\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black\"></form></td>");
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
