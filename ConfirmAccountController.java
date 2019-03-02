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
import CinemaEbooking.Entity.Status;

/**
 * Servlet implementation class ConfirmAccountController
 */
@WebServlet("/ConfirmAccountController")
public class ConfirmAccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
	
		
		String email = request.getParameter("email");
		String pwd = request.getParameter("pass");
		pwd = CryptWithMD5.cryptWithMD5(pwd);
		int code=0;
		try{
			code = Integer.parseInt(request.getParameter("code"));
		}catch (NumberFormatException e){
			code = 0;
		}
		if (login(email, pwd, code).equals("fillFields")) {
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Fill out all fields before submitting.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ConfirmAccount.html");
			rd.include(request, response);
		}
		if (login(email, pwd, code).equals("complete")) {
			out.println("<meta http-equiv='refresh' content='4;URL=MovieTheaterPage.html'>");//redirects after 3 seconds
			out.println("<p style='color:black;'>Account successfully confirmed, try logging in!<br>Please wait while you are being redirected...</p>");
			AccountDB aDB = new AccountDB();
			try{
				aDB.updateAccountStatus(email, 1);
			} catch (Exception e){
				e.printStackTrace();
			}
		}else if(login(email, pwd, code).equals("wrongPass")){
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Password is wrong, try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ConfirmAccount.html");
			rd.include(request, response);
		}else if(login(email, pwd, code).equals("notExist")){
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Email does not exist.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ConfirmAccount.html");
			rd.include(request, response);
		}else if(login(email, pwd, code).equals("wrongCode")){
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Confirmation code is wrong, check your email again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ConfirmAccount.html");
			rd.include(request, response);
		}else if(login(email, pwd, code).equals("noNeed")){
			out.print("<div class=\"alert\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Account has already been confirmed!</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("ConfirmAccount.html");
			rd.include(request, response);
		}
		else{}
	}

	public String login(String email, String password, int code){ //throws Exception{
		
		if(email.equals("") || password.equals("") || code==0){
			return "fillFields";
		}
		
		AccountDB aDB = new AccountDB();
		int userID=0, codeCheck = 0;
		RegisteredUser user = null;
		try{
			userID = aDB.getUserIDFromEmail(email);
			user = aDB.getUser(userID);
			codeCheck = aDB.getCode(userID);
		}catch (Exception e){
			e.printStackTrace();
		}
		String emailCheck = user.getEmail();
		if(email.toLowerCase().equals(emailCheck.toLowerCase())){
			String passwordCheck = user.getPassword();
			if(password.equals(passwordCheck)){
				Status status = user.getStatus();
				if(status==Status.UNCONFIRMED && code == codeCheck)
					return "complete";
				else if (status==Status.UNCONFIRMED && code != codeCheck)
					return "wrongCode";	
				else
					return "noNeed";
			} else 
				return "wrongPass";
		}
		return "notExist";
	}
}
