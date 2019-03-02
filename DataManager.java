package CinemaEbooking.DatabaseAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataManager {
    String driver;
    Connection connection = null;
    
    public DataManager() {
        driver = "com.mysql.jdbc.Driver";
    }

    public Connection getConnection() throws Exception {

        Class.forName(driver);
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Schema?user=root&password=LeroyJenkins12&allowPublicKeyRetrieval=true&useSSL=false");

        return connection;
    }
}

