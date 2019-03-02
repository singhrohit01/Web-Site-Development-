package CinemaEbooking.Servlet.RegisterLogin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.DataManager;
import CinemaEbooking.Entity.Administrator;
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class Session
 */
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String email = request.getParameter("email");
		String pwd = request.getParameter("pass");
		pwd = CryptWithMD5.cryptWithMD5(pwd);
		boolean rememberMe = (request.getParameter("remember")==null ? false : true);
		try{
			
			if (login(email, pwd).equals("complete")) {
				RegisteredUser user = new RegisteredUser(email, pwd);
				if(user.getID()==0){
					user = new RegisteredUser(Integer.parseInt(email));
				}
				Cookie userCookie = new Cookie("userID", ""+user.getID());
				if (rememberMe)
					userCookie.setMaxAge(60*60*24*7); //Cookie lasts one week
				else
					userCookie.setMaxAge(60*60); 
				response.addCookie(userCookie);
				AccountDB aDB = new AccountDB();
				int type = aDB.getUserType(user.getID());
				if(type==2 || type==3)
					response.sendRedirect("MovieTheaterPageLoggedIn.html");
				else{
					Administrator admin = new Administrator(email, pwd);
					Cookie adminCookie = new Cookie("adminID", ""+admin.getID());
					Cookie empty = new Cookie("userID", ""+user.getID());
					empty.setMaxAge(0);
					response.addCookie(empty);
					if (rememberMe)
						adminCookie.setMaxAge(60*60*24*7); //Cookie lasts one week
					else
						adminCookie.setMaxAge(60*60);
					response.addCookie(adminCookie);
					response.sendRedirect("AdminPage.html");
				}	
			} else if (login(email, pwd).equals("unconfirmed")){
				out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Account has not been confirmed, check your email!</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("Login.html");
				rd.include(request, response);
			}else if(login(email, pwd).equals("wrongPass")){
				out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Password is wrong, try again...</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("Login.html");
				rd.include(request, response);
			}else if(login(email, pwd).equals("notExist")){
				out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Email does not exist</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("Login.html");
				rd.include(request, response);
			}else{}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
public String login(String email, String password) throws Exception{
	
	AccountDB aDB = new AccountDB();
	ResultSet users=null;
	try{
		users = aDB.getList();
	}catch (Exception e){
		e.printStackTrace();
	}
		while (users.next()){
			String emailCheck = users.getString("Email");
			int accountCheck = users.getInt("UserID");
			if(email.toLowerCase().equals(emailCheck.toLowerCase()) || email.equals(""+accountCheck)){
				String passwordCheck = users.getString("Password");
				if(password.equals(passwordCheck)){
					int status = users.getInt("Status");
					if(status==4){
						System.out.println("Account has not been confirmed, check your email");
						return "unconfirmed";
					}
					return "complete";
				} else {
					return "wrongPass";
				}
			}
		}
		return "notExist";
	}
}