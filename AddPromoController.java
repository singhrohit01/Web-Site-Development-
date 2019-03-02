package CinemaEbooking.Servlet.Profile;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CinemaEbooking.DatabaseAccessor.AccountDB;
import CinemaEbooking.DatabaseAccessor.PromoDB;
import CinemaEbooking.Entity.Mailer;
import CinemaEbooking.Entity.PromotionCode;

/**
 * Servlet implementation class AddPromoController
 */
@WebServlet("/AddPromoController")
public class AddPromoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Date startDate = null, expDate = null;
		int sale=0;
		
		String promoName = request.getParameter("promoName");
		String start = request.getParameter("startDate");
		String exp = request.getParameter("expDate");
		try{
			sale = Integer.parseInt(request.getParameter("sale"));
			PromoDB pDB = new PromoDB();
			ResultSet rs = pDB.getList();
			while(rs.next()){
				if(promoName.equals(rs.getString("PromoName"))){
					out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
					out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
							+ "<font color=red>Promo name already exists. Please choose another name or delete the other named promotion.</font>");
					out.print("</div>");
					RequestDispatcher rd = request.getRequestDispatcher("AddPromo.html");
					rd.include(request, response);
					return;
				}
			}
		} catch(Exception e){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Fill out all fields before submitting.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddPromo.html");
			rd.include(request, response);
			return;
		}
		if(start.equals("") || exp.equals("") || promoName.equals("")){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Fill out all fields before submitting.</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddPromo.html");
			rd.include(request, response);
			return;
		}
		
		startDate = Date.valueOf(start);
		expDate = Date.valueOf(exp);
		
		if(startDate.after(expDate)){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Starting Date is later than Expiring Date, please try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddPromo.html");
			rd.include(request, response);
			return;
		}
		if(expDate.before(startDate)){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Expiration Date is before Starting Date, please try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddPromo.html");
			rd.include(request, response);
			return;
		}
		if(startDate.equals(expDate)){
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=red>Promotion cannot have an Expiring Date on the same date as the Starting Date, please try again...</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("AddPromo.html");
			rd.include(request, response);
			return;
		}
		
		PromotionCode p = new PromotionCode(promoName, sale, startDate, expDate);
		PromoDB pDB = new PromoDB();
		try{
			pDB.addPromo(p);
			int promoID = pDB.getPromoIDFromPromoName(promoName);
			p.setID(promoID);
			sendPromo(p);
			out.print("<div class=\"alert\" style=\"margin-top: 50px\">");
			out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
					+ "<font color=white>Promo Added</font>");
			out.print("</div>");
			RequestDispatcher rd = request.getRequestDispatcher("Promo");
			rd.include(request, response);
			return;
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("deprecation")
	public void sendPromo(PromotionCode promo) throws Exception{
		
		String subject, body;
		
		Date startDate = promo.getStartDate();
		Date expDate = promo.getExpDate();
		
		subject = "Promotion Code";
		body = "Promotion Code (Dates are in the following format: YYYY-MM-DD):\n\n";
		
		body += "Promo name: " + promo.getName() +
				"\nSale: " + promo.getSalePercent() + "% off" +
				"\nStarting Date: " + startDate +
				"\nExpiration Date: " + expDate + "\n";						

		body +="\nEnjoy the promotion and thank you for your loyalty!";
		InternetAddress[] recipients = new InternetAddress[0];
		AccountDB aDB = new AccountDB();
		ResultSet userList = null;
		try{
			userList = aDB.getList();
		}catch (Exception e){
			e.printStackTrace();
		}
		while(userList.next()){
			if(userList.getInt("Promo") == 1){
				InternetAddress[] newRecipients = new InternetAddress[recipients.length+1];
				for(int i=0; i<recipients.length; i++){
					newRecipients[i] = recipients[i]; 
				}
				newRecipients[recipients.length] = new InternetAddress(userList.getString("Email"));
				recipients = newRecipients;
			}	
		}
		Mailer.send(subject, body, recipients, Message.RecipientType.BCC);
		
	}

}
