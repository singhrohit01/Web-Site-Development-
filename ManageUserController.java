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

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class ManageUserController
 */
@WebServlet("/ManageUserController")
public class ManageUserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		
		AccountDB aDB = new AccountDB();
		ResultSet rs;
		RegisteredUser user = null;
		boolean updated = false;
		try{
			rs = aDB.getList();
			while(rs.next()){
				user = aDB.getUser(rs.getInt("UserID"));
				if(rs.getInt("UserType")==2 || rs.getInt("UserType")==3){
					int statusNo = Integer.parseInt(request.getParameter("status" + user.getID()));
					if(statusNo != rs.getInt("Status")){
						aDB.updateAccountStatus(user.getEmail(), statusNo);
						updated = true;
					}
				}
				if(request.getParameter("delete" + user.getID()) !=null){
					aDB.deleteUser(user.getID());
					updated = true;
				}
				if(request.getParameter("userType" + user.getID())!=null && Integer.parseInt(request.getParameter("userType" + user.getID())) != rs.getInt("UserType")){
					aDB.updateUserType(user.getID(), Integer.parseInt(request.getParameter("userType" + user.getID())));
					if(Integer.parseInt(request.getParameter("userType" + user.getID()))==1)
						aDB.updateAccountStatus(user.getEmail(), 1);
					updated = true;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		if(updated){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=white>User information has been changed successfully.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("User");
			rd.include(request, response);
		}
		else{
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>No users have had any changes to update.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("User");
			rd.include(request, response);
		}
	}
}
