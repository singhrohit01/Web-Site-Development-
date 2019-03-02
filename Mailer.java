package CinemaEbooking.Entity;

/**
 * 1. Download JavaMail API
 * 2. Add the mail.jar , and all the jars in "lib" to project
 * 4. import java.util.Properties
      import javax.mail.*; 
	  */
	  
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/*
 * This class connects to GMail and allows the transmission of messages within the GMail server via the use
 * of SMTP (i.e. Simple Mail Transfer Protocol.
 * 
 * The documentation for the API can be found here: https://javaee.github.io/javamail/docs/api/.
 * 
 * To proceed, these .jar files need to be downloaded: javax.mail, dsn, imap, mailapi, pop3, smtp.
 * 
 * To ensure mail transmission,  you need to allow your GMail to be accessed via "less secure apps". Go here:
 * https://myaccount.google.com/lesssecureapps
 * 
 * @author: Mary Brown
 */

public class Mailer {
	
	public static void send(String subject, String body, InternetAddress[] recipients, Message.RecipientType howToSend) throws MessagingException {

		Properties props = new Properties();
		  props.put("mail.smtp.host","smtp.gmail.com"); 														//insert name of host/mail server here, format: "smtp.[server].com" (i.e. smtp.gmail.com)
		  props.put("mail.smtp.auth", "true");																	//attempts to authenticate user using the AUTH command
		  props.put("mail.smtp.port","587");														        	//insert port number of the mail server for the protocol. (i.e. 587 for TLS, 465 for SSL)
		  props.put("mail.smtp.starttls.enable", "true");														//allows the use of TLS for SMTP connections
		  
		  
		  Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator(){
					protected PasswordAuthentication getPasswordAuthentication(){
						return new PasswordAuthentication("listservebooking","CSCI4050");			
					}
				}
		  );
		  
		  
		  
		  try{
			Message mess = new MimeMessage(session);
			mess.setFrom(new InternetAddress("listservebooking@gmail.com"));		   
			mess.setRecipients(howToSend, recipients);   			                               
			mess.setSubject(subject);																
			mess.setText(body);                                                                  
			Transport.send(mess, "listservebooking@gmail.com", "CSCI4050");		
		  } catch(Exception e){
			  
			//if unsuccessful email transmission, explain error
			e.printStackTrace();
		  }
	}
}

