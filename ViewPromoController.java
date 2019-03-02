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

import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.Entity.PromotionCode;

/**
 * Servlet implementation class ViewPromoController
 */
@WebServlet("/ViewPromoController")
public class ViewPromoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		RequestDispatcher rd = request.getRequestDispatcher("PromoCodeTable.html");
		rd.include(request, response);

		int id=0;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("adminID"))
				id = Integer.parseInt(cookies[i].getValue());
		}
		if (id==0){
			response.sendRedirect("MovieTheaterPageLoggedIn.html");
			return;
		}
		
		PromoDB pDB = new PromoDB();
		ResultSet rs;
		try{
			rs = pDB.getList();
			while(rs.next()){
				int promoID = rs.getInt("PromoID");
				PromotionCode p = pDB.getPromo(promoID);
				out.print("<tr>");
				out.print("<td>" + p.getID() + "</td>");
				out.print("<td>" + p.getName() + "</td>");
				out.print("<td>" + p.getSalePercent() + "</td>");
				out.print("<td>" + p.getStartDate().toLocaleString() + "</td>");
				out.print("<td>" + p.getExpDate().toLocaleString() + "</td>");
				out.print("<td><input style=\"float: left; margin-top: 5px;\" type=\"checkbox\" name=\"delete" + p.getID() + "\" style=\"vertical-align: bottom;\"><div style=\"margin-left: 15px;\">Delete?</div></td>");
				out.print("</tr>");
			}
			out.print("</table><br><br>");
			out.print("<a href=\"AddPromo.html\" value=\"AddPromo\" class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 50px; padding-right: 50px\">Add Promo</a>");
			out.print("<input type=\"submit\" value=\"Delete Selected Promotions\"class=\"w3-button w3-black w3-text-blue w3-hover-blue w3-hover-text-black w3-center\" style=\"padding-left: 30px; padding-right: 30px\">");
			out.print("</form>");
			out.print("</div>");
			out.print("</body>");
			out.print("</html>");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
