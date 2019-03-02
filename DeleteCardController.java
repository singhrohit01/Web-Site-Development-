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

import CinemaEbooking.DatabaseAccessor.CardDB;
import CinemaEbooking.DatabaseAccessor.MovieDB;

/**
 * Servlet implementation class DeleteAddressController
 */
@WebServlet("/DeleteCardController")
public class DeleteCardController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		int id=0;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
		}
		
		CardDB cDB = new CardDB();
		ResultSet rs = null;
		boolean deleted = false;
		try{
			rs = cDB.getUserCard(id);
			while(rs.next()){
				if(request.getParameter("deleteCard" + rs.getInt("CardID")) !=null){
					cDB.deleteCard(rs.getInt("CardID"), id);
					deleted =true;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		if(deleted){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=white>Cards successfully deleted.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("EditProfile");
			rd.include(request, response);
		}
		else{
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>No cards selected to delete.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("EditProfile");
			rd.include(request, response);
		}
	}

}
