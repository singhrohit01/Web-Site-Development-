package CinemaEbooking.Entity;

import java.sql.Timestamp;

public class Ticket {
	
	int ticketType;
	int seatID;
	int movieShowID;
	Timestamp startTime;
	
	public Ticket(int ticketType, int seatID, int movieShowID, Timestamp startTime){
		this.ticketType = ticketType;
		this.seatID = seatID;
		this.movieShowID = movieShowID;
		this.startTime = startTime;
	}
	
	public int getTicketType(){
		return ticketType;
	}
	
	public int getSeatID(){
		return seatID;
	}
	
	public int getMovieShowID(){
		return movieShowID;
	}
	
	public Timestamp getStartTime(){
		return startTime;
	}

}
