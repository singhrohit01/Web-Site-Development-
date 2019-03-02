package CinemaEbooking.Entity;

import java.sql.ResultSet;

import CinemaEbooking.DatabaseAccessor.MovieDB;

public class Movie {

	int movieID;
	String movieTitle;
	int length;
	String cast;
	String trailer; //URL link as a String
	String image; //URL link as a String
	Rating rating;
	String synopsis;
	String director;
	String producer;
	
	public Movie(int movieID){
		this.movieID = movieID;
		MovieDB mDB = new MovieDB();
		ResultSet rs = null;
		try{
			rs = mDB.getMovieList();
			while(rs.next()){
				if(rs.getInt("MovieID") == movieID){
					movieTitle = rs.getString("Movie");
					length = rs.getInt("Length");
					cast = rs.getString("Cast");
					trailer = rs.getString("Trailer");
					image = rs.getString("Image");
					synopsis = rs.getString("Synopsis");
					director = rs.getString("director");
					producer = rs.getString("producer");
					switch(rs.getInt("Rating")){
					case 1:
						rating = Rating.G;
						break;
					case 2:
						rating = Rating.PG;
						break;
					case 3:
						rating = Rating.PG13;
						break;
					case 4:
						rating = Rating.R;
						break;
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Movie(String movieTitle, int length){		
		this.movieTitle=movieTitle;
		this.length=length;
	}
	
	public void setMovieTitle(String title){
		movieTitle=title;
	}
	
	public String getMovieTitle(){
		return movieTitle;
	}
	
	public void setLength(int length){
		this.length=length;
	}
	
	public int getLength(){
		return length;
	}
	
	public void setImage(String image){
		this.image=image;
	}
	
	public String getImage(){
		return image;
	}
	
	public void setCast(String cast){
		this.cast=cast;
	}
	
	public String getCast(){
		return cast;
	}
	
	public void setTrailer(String trailer){
		this.trailer=trailer;
	}
	
	public String getTrailer(){
		return trailer;
	}
	
	public void setRating(int rating){
		switch(rating){
		case 1:
			this.rating = Rating.G;
			break;
		case 2:
			this.rating = Rating.PG;
			break;
		case 3:
			this.rating = Rating.PG13;
			break;
		case 4:
			this.rating = Rating.R;
			break;
		default:
			this.rating = Rating.G;
		}
	}
	
	public Rating getRating(){
		return rating;
	}
	
	public int getRatingInt(){
		switch(rating){
		case G:
			return 1;
		case PG:
			return 2;
		case PG13:
			return 3;
		case R:
			return 4;
		default:
			return 0;
		}
	}
	
	
	public void setSynopsis(String synopsis){
		this.synopsis=synopsis;
	}
	
	public String getSynopsis(){
		return synopsis;
	}
	
	public void setDirector(String director){
		this.director=director;
	}
	
	public String getDirector(){
		return director;
	}
	
	public void setProducer(String producer){
		this.producer=producer;
	}
	
	public String getProducer(){
		return producer;
	}
	
}
