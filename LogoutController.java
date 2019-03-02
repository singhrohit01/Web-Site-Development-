package CinemaEbooking.Servlet.RegisterLogin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LogoutSession
 */
public class LogoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Cookie empty = new Cookie("userID", null);
		empty.setMaxAge(0);
		response.addCookie(empty);
		Cookie empty2 = new Cookie("adminID", null);
		empty2.setMaxAge(0);
		response.addCookie(empty2);
		RequestDispatcher rd = request.getRequestDispatcher("MovieTheaterPage.html");
		rd.include(request, response);
		
	}
}
