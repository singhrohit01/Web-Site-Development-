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
import CinemaEbooking.DatabaseAccessor.GenreDB;
import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.DatabaseAccessor.MovieGenreDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.RatingDB;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Status;

/**
 * Servlet implementation class ViewUserController
 */
@WebServlet("/ViewMovieController")
public class ViewMovieController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RequestDispatcher rd = request.getRequestDispatcher("MovieTable.html");
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
		
		MovieDB mDB = new MovieDB();
		ResultSet rs = null;
		try{
			rs = mDB.getMovieList();
			while(rs.next()){
				out.print("<tr>");
				out.print("<td><img src=\"" + rs.getString("Image") + "\" style=\"width:82px; height:86px\" title=\"" + rs.getString("Movie") + " Image\" alt=\"Movie Image\"></a></td>");
				out.print("<td>" + rs.getInt("MovieID") + "</td>");
				out.print("<td>" + rs.getString("Movie") + "</td>");
				out.print("<td>" + rs.getInt("Length") + "</td>");
				out.print("<td>" + rs.getString("Synopsis") + "</td>");
				out.print("<td>" + rs.getString("Cast") + "</td>");
				out.print("<td>" + rs.getString("Director") + "</td>");
				out.print("<td>" + rs.getString("Producer") + "</td>");
				out.print("<td>");
				RatingDB rDB = new RatingDB();
				out.print(rDB.getRating((rs.getInt("Rating"))) +" </td>");
				out.print("<td>");
				
				MovieGenreDB mgDB = new MovieGenreDB();
				ResultSet rs2 = null;
				rs2 = mgDB.getGenreList(rs.getInt("MovieID"));
				while(rs2.next()){ //Lists all genres for movie
					int genreID = rs2.getInt("GenreID");
					GenreDB gDB = new GenreDB();
					out.print(gDB.getGenre(genreID) + "<br>");
				}
				
				out.print("</td>");
				out.print("<td><a href=\"" + rs.getString("Trailer") + "\" target=\"_blank\">Play Trailer</a></td>");
				out.print("<td><input style=\"float: left; margin-top: 5px;\" type=\"checkbox\" name=\"delete" + rs.getInt("MovieID") + "\" style=\"vertical-align: bottom;\"><div style=\"margin-left: 15px;\">Delete?</div></td>");
				out.print("</tr>");
			}	
			out.print("</table>");
			out.print("<a href=\"AddMovie.html\"class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">Add Movie</a>");
			out.print("<input type=\"submit\" value=\"Delete Movies\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">");
			out.print("</form>");
			out.print("</div>");
			out.print("</body>");
			out.print("</html>");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
