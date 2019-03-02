package CinemaEbooking.Servlet.RegisterLogin;

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
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class ResetPasswordController
 */
@WebServlet("/ResetPasswordController")
public class ResetPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String email = request.getParameter("email");
		String pass = request.getParameter("pass");
		String newPass = request.getParameter("newPass");
		String newPassCheck = request.getParameter("newPassCheck");
		
		if(email.equals("") || pass.equals("") || newPass.equals("") || newPassCheck.equals("")){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Fill out all fields before submitting.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ResetPassword.html");
			rd.include(request, response);
			return;
		}
		
		pass = CryptWithMD5.cryptWithMD5(pass);
		AccountDB aDB = new AccountDB();
		RegisteredUser user = null;
		try{
			user = aDB.getUser(aDB.getUserIDFromEmail(email));
		}catch (Exception e){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Email does not exist in database</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ResetPassword.html");
			rd.include(request, response);
			return;
		}
		System.out.println(user.getPassword());
		System.out.println(pass);
		if(user.getPassword().equals(pass)){
			if(newPass.equals(newPassCheck)){
				if(newPass.length()<8){
					out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
					out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
							+ "<font color=red>Password must be 8 characters long...</font>");
					out.print("</div>");
					RequestDispatcher rd = request.getRequestDispatcher("ResetPassword.html");
					rd.include(request, response);
				}else{
					try{
						aDB.changePassword(email, CryptWithMD5.cryptWithMD5(newPass));
						aDB.updateAccountStatus(email, 1);
						out.println("<meta http-equiv='refresh' content='3;URL=MovieTheaterPage.html'>");//redirects after 3 seconds
						out.println("<p style='color:black;'>Password has been reset! Please wait while you are being redirected...</p>");
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}else{
				out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Passwords do not match, try again...</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("ResetPassword.html");
				rd.include(request, response);
			}
		}else{
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Password is wrong <font size=\"-1\">(If you forgot your password, enter the temporary password found in the email)</font></font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ResetPassword.html");
			rd.include(request, response);
		}
		
	}
}
