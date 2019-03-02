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
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;
import CinemaEbooking.Entity.Status;

/**
 * Servlet implementation class ViewUserController
 */
@WebServlet("/ViewUserController")
public class ViewUserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		

		int id=0;
		Cookie[] cookies= request.getCookies();
		try{
			for(int i=0; i<cookies.length; i++){
				if (cookies[i].getName().equals("adminID"))
					id = Integer.parseInt(cookies[i].getValue());
			}
		} catch (Exception e){
			response.sendRedirect("MovieTheaterPageLoggedIn.html");
			return;
		}
		
		if (id==0){
			response.sendRedirect("MovieTheaterPageLoggedIn.html");
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("UserTable.html");
		rd.include(request, response);
		
		AccountDB aDB = new AccountDB();
		ResultSet rs = null;
		try{
			rs = aDB.getList();
			while(rs.next()){
				int userID = rs.getInt("UserID");
				RegisteredUser user = aDB.getUser(userID);
				int userType = rs.getInt("UserType");
				out.print("<tr>");
				out.print("<td>" + user.getID() + "</td>");
				out.print("<td>" + user.getFirstName() + "</td>");
				out.print("<td>" + user.getLastName() + "</td>");
				out.print("<td>" + user.getEmail() + "</td>");
				out.print("<td>" + user.getPassword() + "</td>");
				if (userType==1){
					out.print("<td>" + user.getStatus() + "</td>");
					out.print("<td>Administrator</td>");
					out.print("<td></td>");
				}
				if (userType==2 || userType==3){
					out.print("<td><select name=\"status" + user.getID() + "\">");
					if(user.getStatus()==Status.ACTIVE){
			  			out.print("<option value=\"1\"><b>ACTIVE</b></option>");
			  			out.print("<option style=\"color:red\" value=\"2\">INACTIVE</option>");
			  			out.print("<option style=\"color:red\" value=\"3\">SUSPENDED</option>");
					}else if(user.getStatus()==Status.INACTIVE){
						out.print("<option value=\"2\"><b>INACTIVE</b></option>");
						out.print("<option style=\"color:red\" value=\"1\">ACTIVE</option>");
						out.print("<option style=\"color:red\" value=\"3\">SUSPENDED</option>");
					}else if(user.getStatus()==Status.SUSPENDED){
						out.print("<option value=\"3\"><b>SUSPENDED</b></option>");
						out.print("<option style=\"color:red\" value=\"1\">ACTIVE</option>");
			  			out.print("<option style=\"color:red\" value=\"2\">INACTIVE</option>");
					}else{
						out.print("<option value=\"4\"><b>UNCONFIRMED</b></option>");
						out.print("<option style=\"color:red\" value=\"1\">ACTIVE</option>");
			  			out.print("<option style=\"color:red\" value=\"2\">INACTIVE</option>");
			  			out.print("<option style=\"color:red\" value=\"3\">SUSPENDED</option>");
					}
					out.print("</select></td>");
					out.print("<td><select name=\"userType" + user.getID() + "\">");
					if(userType==3){
						out.print("<option value=\"3\"><b>Employee</b></option>");
						out.print("<option style=\"color:red\" value=\"1\">Administrator</option>");
						out.print("<option style=\"color:red\" value=\"2\">Customer</option>");
					}
					else{
						out.print("<option value=\"2\"><b>Customer</b></option>");
						out.print("<option style=\"color:red\" value=\"1\">Administrator</option>");
						out.print("<option style=\"color:red\" value=\"3\">Employee</option>");
					}
					out.print("</select></td>");
					out.print("<td><input style=\"float: left; margin-top: 5px;\" type=\"checkbox\" name=\"delete" + user.getID() + "\" style=\"vertical-align: bottom;\"><div style=\"margin-left: 15px;\">Delete?</div></td>");
					out.print("</tr>");
				}
			}
			out.print("</table>");
			out.print("<input type=\"submit\" value=\"Update Users\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">");
			out.print("</form>");
			out.print("</div>");
			out.print("</body>");
			out.print("</html>");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
