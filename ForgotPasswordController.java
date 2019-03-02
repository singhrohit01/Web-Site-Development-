package CinemaEbooking.Servlet.RegisterLogin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.Entity.Mailer;

/**
 * Servlet implementation class ForgotPasswordController
 */
@WebServlet("/ForgotPasswordController")
public class ForgotPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String email = request.getParameter("email");
		
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Invalid email format, try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ForgotPassword.html");
			rd.include(request, response);
			return;
		}
		
		AccountDB aDB = new AccountDB();
		try{
			if(aDB.getUserIDFromEmail(email)==0){
				out.print("<div class=\"alert\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Email isn't registered!</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("ForgotPassword.html");
				rd.include(request, response);
				return;
			}
		} catch (Exception e){
			e.printStackTrace();
			return;
		}
		try{
			int randomNum = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000000, 100000000);
			aDB.changePassword(email, "" + randomNum);
			Mailer.send("E-booking Forgot Password", "Click the following link to reset your password:\n http://localhost:8080/Cinema_EBooking/ResetPassword.html\nTemporary Password is: " + randomNum, InternetAddress.parse(email), Message.RecipientType.TO);
			out.println("<meta http-equiv='refresh' content='3;URL=MovieTheaterPage.html'>");//redirects after 3 seconds
			out.println("<p style='color:black;'>Email is sent! Please wait while you are being redirected...</p>");
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
