package CinemaEbooking.DatabaseAccessor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import CinemaEbooking.DatabaseAccessor.DataManager;

public class FeeDB {
	
	DataManager db = new DataManager();

	 public double getFee() throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("select * from Fee where FeeID=1");
		 ResultSet rs = p.executeQuery();
		 rs.next();
		 return rs.getDouble("Price");
	 }
	 
	 public void setFee(double price) throws Exception {
		 PreparedStatement p = db.getConnection().prepareStatement("Update Fee set Price=? where FeeID=1");
		 p.setDouble(1, price);
		 p.executeUpdate();
	 }

}
