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
@WebServlet("/DeletePromoController")
public class DeletePromoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		
		PromoDB pDB = new PromoDB();
		ResultSet rs;
		PromotionCode p = null;
		boolean deleted = false;
		try{
			rs = pDB.getList();
			while(rs.next()){
				p = pDB.getPromo(rs.getInt("PromoID"));
				if(request.getParameter("delete" + p.getID()) !=null && request.getParameter("delete" + p.getID()).equals("on")){
					pDB.deletePromo(p.getID());
					deleted = true;
				}
			}
		} catch (Exception e){
			
		}
		if(deleted){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=white>Promo(s) deleted successfully</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Promo");
			rd.include(request, response);
			return;		
		}else{
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>No promotions were selected to be deleted</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Promo");
			rd.include(request, response);
			return;
		}
	}

}
