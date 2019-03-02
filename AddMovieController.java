package CinemaEbooking.Servlet.Profile;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.DatabaseAccessor.MovieGenreDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.Entity.Movie;
import CinemaEbooking.Entity.PromotionCode;

/**
 * Servlet implementation class AddPromoController
 */
@WebServlet("/AddMovieController")
public class AddMovieController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int length = 0;
		
		String title = request.getParameter("title");
		try{
			length = Integer.parseInt(request.getParameter("length"));
		} catch(Exception e){
			length = 0;
		}
		String cast = request.getParameter("cast");
		int rating = Integer.parseInt(request.getParameter("rating"));
		String[] genre = request.getParameterValues("genre");
		String synopsis = request.getParameter("synopsis");
		String image = request.getParameter("image");
		String trailer = request.getParameter("trailer");
		String director = request.getParameter("director");
		String producer = request.getParameter("producer");

		
		if(title.equals("") || length == 0 || cast.equals("") || rating == 0 || genre.length == 0 || synopsis.equals("") || image.equals("") || trailer.equals("") || director.equals("") || producer.equals("")){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Fill out all fields before submitting.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddMovie.html");
			rd.include(request, response);
			return;
		}
		Movie movie = new Movie(title, length);
		movie.setCast(cast);
		movie.setRating(rating);
		movie.setSynopsis(synopsis);
		movie.setImage(image);
		movie.setTrailer(trailer);
		movie.setDirector(director);
		movie.setProducer(producer);

		
		MovieGenreDB mgDB = new MovieGenreDB();
		MovieDB mDB = new MovieDB();
		try{
			mDB.addMovie(movie);
			int movieID = mDB.getMovieID(movie);
			for(int i=0; i<genre.length; i++){
				mgDB.addGenreToMovie(movieID, Integer.parseInt(genre[i]));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		out.println("<meta http-equiv='refresh' content='4;URL=AdminPage.html'>");//redirects after 3 seconds
		out.println("<p style='color:black;'>Movie successfully created!<br>Please wait while you are being redirected...</p>");
	}

}
