package CinemaEbooking.Servlet.Profile;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.MovieDB;
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class ManageUserController
 */
@WebServlet("/DeleteMovieController")
public class DeleteMovieController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		
		MovieDB mDB = new MovieDB();
		ResultSet rs = null;
		boolean deleted = false;
		try{
			rs = mDB.getMovieList();
			while(rs.next()){
				if(request.getParameter("delete" + rs.getInt("MovieID")) !=null){
					mDB.deleteMovie(rs.getInt("MovieID"));
					deleted =true;
				}
			}
		} catch (Exception e){
			
		}
		if(deleted){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=white>Movie successfully deleted.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Movie");
			rd.include(request, response);
		}
		else{
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>No movies selected to delete.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Movie");
			rd.include(request, response);
		}
	}
}
