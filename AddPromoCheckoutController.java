package CinemaEbooking.Servlet.Booking;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.sql.Timestamp;

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
 * Servlet implementation class AddPromoCheckoutController
 */
@WebServlet("/AddPromoCheckoutController")
public class AddPromoCheckoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String promoName = request.getParameter("promoName");
		PromoDB pDB = new PromoDB();
		
		Date date= new Date();
		 
		long time = date.getTime();		 
		Timestamp ts = new Timestamp(time);
		java.sql.Date dateCheck = new java.sql.Date(ts.getYear(), ts.getMonth(), ts.getDate());
		
		PromotionCode p = null;
		try{
			p = pDB.getPromo(pDB.getPromoIDFromPromoName(promoName));
		}catch(Exception e){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Promotion code given does not exist.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("PromoCheckout.html");
			rd.include(request, response);
			return;
		}
			
		 if(p.getStartDate().after(dateCheck)){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Promotion code hasn't been released yet, try again when the code is available for use.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("PromoCheckout.html");
			rd.include(request, response);
			return;
		} else if(p.getExpDate().before(dateCheck)){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Promotion code has expired.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("PromoCheckout.html");
			rd.include(request, response);
			return;
		}
		
		
		Cookie promoCookie = new Cookie("promoID", "" + p.getID());
		promoCookie.setMaxAge(60*60);
		promoCookie.setPath("/");
		response.addCookie(promoCookie);
		RequestDispatcher rd = request.getRequestDispatcher("Checkout");
		rd.include(request, response);
	}

}
