package CinemaEbooking.Entity;

import java.sql.ResultSet;

import CinemaEbooking.DatabaseAccessor.*;


public abstract class User {
	
	public void search(MovieDB db, String query) throws Exception{
		ResultSet movieList = db.getMovieList();
    	while(movieList.next()){ //Checks if character sequence in query exists in the title of all available movies
    		if(movieList.getString("Movie").toLowerCase().contains(query.toLowerCase()))
    			System.out.println(movieList.getString("Movie"));
    	}
	}	
}
