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
import CinemaEbooking.DatabaseAccessor.TicketTypeDB;
import CinemaEbooking.Entity.PromotionCode;
import CinemaEbooking.Entity.RegisteredUser;

/**
 * Servlet implementation class ManageUserController
 */
@WebServlet("/UpdatePricesController")
public class UpdatePricesController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		
		TicketTypeDB ttDB = new TicketTypeDB();
		boolean updated = false;
		try{
			for(int i=1; i<=3; i++){
				double newPrice = Double.parseDouble(request.getParameter("" + i));
				if(newPrice!=ttDB.getTicketPrice(i)){
					ttDB.setTicketPrice(i, newPrice);
					updated=true;
				}
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
