package CinemaEbooking.Servlet.Booking;

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

import CinemaEbooking.DatabaseAccessor.CardDB;
import CinemaEbooking.Entity.Card;
import CinemaEbooking.Entity.Month;

/**
 * Servlet implementation class CheckoutPromoController
 */
@WebServlet("/CheckoutPromoController")
public class CheckoutPromoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		int movieID=0, cardID=0, id=0;
		long time=0L;
		Cookie[] cookies= request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if (cookies[i].getName().equals("userID"))
				id = Integer.parseInt(cookies[i].getValue());
			else if (cookies[i].getName().equals("movieID"))
				movieID = Integer.parseInt(cookies[i].getValue());
			else if(cookies[i].getName().equals("date"))
				time = Long.parseLong(cookies[i].getValue());
			else if(cookies[i].getName().equals("cardID"))
				cardID = 0;
		}
	
		
		if(movieID==0){
			out.println("<meta http-equiv='refresh' content='3;URL=MovieTheaterPage.html'>");
			out.println("<p style='color:black;'>Cookies expired, please try searching again.<br>Please wait while you are being redirected...</p>");
			return;
		}
		
		if(cardID == -1){
			long ccNo;
			int expYear, ccv;
			String cardType = request.getParameter("cardType");
			try{
				 ccNo = Long.parseLong(request.getParameter("cardNo"));
			}catch (NumberFormatException e){
				 ccNo = 0;
			}
			String name = request.getParameter("name");
			try{
				ccv = Integer.parseInt(request.getParameter("ccv"));
			}catch (NumberFormatException e){
				ccv = 0;
			}
			String expMonth = request.getParameter("month");
			try{
				expYear = Integer.parseInt(request.getParameter("year"));
			}catch (NumberFormatException e){
				expYear = 0;
			}
			
			String result = null;
			try{
				result = addCard(cardType, ccNo, name, ccv, expMonth, expYear, id);
			} catch (Exception e){
				e.printStackTrace();
			}
			if(result.equals("fillFields")){
				out.print("<div class=\"alert\" style=\"margin-top: 55px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Fill out all fields before submitting.</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("ViewCheckout");
				rd.include(request, response);
			}
			else if(result.equals("invalidType")){
				out.print("<div class=\"alert\" style=\"margin-top: 55px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Please select a card type</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("ViewCheckout");
				rd.include(request, response);
			}
			else if(result.equals("invalidMonth")){
				out.print("<div class=\"alert\" style=\"margin-top: 55px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Please select a month</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("ViewCheckout");
				rd.include(request, response);
			}
			else if(result.equals("invalidCardNo")){
				out.print("<div class=\"alert\" style=\"margin-top: 55px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Please enter a valid card number</font>");
				out.print("</div>");
				RequestDispatcher rd = request.getRequestDispatcher("ViewCheckout");
				rd.include(request, response);
			}
			else if (result.equals("complete")) {
				Month month = Month.JANUARY;
				try{
					switch(expMonth){
					case "Jan":
						month = Month.JANUARY;
						break;
					case "Feb":
						month = Month.FEBRUARY;
						break;
					case "Mar":
						month = Month.MARCH;
						break;
					case "Apr":
						month = Month.APRIL;
						break;
					case "May":
						month = Month.MAY;
						break;
					case "Jun":
						month = Month.JUNE;
						break;
					case "Jul":
						month = Month.JULY;
						break;
					case "Aug":
						month = Month.AUGUST;
						break;
					case "Sep":
						month = Month.SEPTEMBER;
						break;
					case "Oct":
						month = Month.OCTOBER;
						break;
					case "Nov":
						month = Month.NOVEMBER;
						break;
					case "Dec":
						month = Month.DECEMBER;
						break;
					default:
						System.out.println("Not a valid month, try again");
					}
					Card card = new Card(cardType, ccNo, name, ccv, month, expYear);
					CardDB cardDB = new CardDB();
					cardDB.addCard(card, id);
					Cookie empty = new Cookie("cardID", null);
					empty.setMaxAge(0);
					response.addCookie(empty);
					
					int newCardID = 0;
					ResultSet rs = null;
					try{
						rs = cardDB.getUserCard(id);
						while(rs.next()){
							if(rs.getInt("CardID")>newCardID){
								newCardID = rs.getInt("CardID");
							}
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					Cookie cookie = new Cookie("cardID", "" +newCardID);
					cookie.setMaxAge(1*60*60);
					response.addCookie(empty);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			else{
				RequestDispatcher rd = request.getRequestDispatcher("MovieTheaterPage.html");
				out.print("<div class=\"alert\" style=\"margin-top: 55px\">");
				out.print("<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';\">&times;</span> "
						+ "<font color=red>Fatal Error occured addding card</font>");
				out.print("</div>");
				rd.include(request, response);
			}
			
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("PromoCheckout.html");
		rd.include(request, response);
	}
	
	public String addCard(String type, long ccNo, String name, int ccv, String expMonth, int expYear, int id){
		if(ccNo==0 || name.equals("") || ccv==0 || expYear==0){
			return "fillFields";
		}
		if(type.equals("SO"))
			return "invalidType";
		String ccNoString = ccNo + "";
		if ((ccNoString.length() >= 13 &&  
		        ccNoString.length() <= 16) && 
		       (ccNoString.substring(0, 1).equals("4") ||  //Visa 
                ccNoString.substring(0, 1).equals("5") ||  //Mastercards
                ccNoString.substring(0, 1).equals("37") || //American Express
                ccNoString.substring(0, 1).equals("6"))){//Discover
		}else{
			return "invalidCardNo";
		}
		if(expMonth.equals("SO")){
			return "invalidMonth";
		}
		return "complete";
	}
}
