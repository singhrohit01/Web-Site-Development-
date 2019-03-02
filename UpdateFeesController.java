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
import CinemaEbooking.DatabaseAccessor.FeeDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.DatabaseAccessor.TicketTypeDB;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class ManageUserController
 */
@WebServlet("/UpdateFeesController")
public class UpdateFeesController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		
		FeeDB fDB = new FeeDB();
		boolean updated = false;
		try{
			double newPrice = Double.parseDouble(request.getParameter("booking"));
			if(newPrice!=fDB.getFee()){
				fDB.setFee(newPrice);
				updated=true;
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		if(updated){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=white>Prices updated successfully.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Price");
			rd.include(request, response);
		}
		else{
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>No values have changed, no update needed.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Price");
			rd.include(request, response);
		}
	}
}
