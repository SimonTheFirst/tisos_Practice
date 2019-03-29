package simple_server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class DataBaseInterface {
	private final String url = "jdbc:mysql://localhost:3306/coursework?useSSL=false&serverTimezone=Europe/Moscow";
	private final String user = "root";
	private final String password = "2hj7x0rHitman";
	static Connection connection;
	
	public Connection connect()
	{
		if (connection == null)
		{
			try
			{
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(url, user, password);
			}
			catch (ClassNotFoundException | SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return connection;
	}
	
	public void disconnect()
	{
		if (connection!=null)
		{
			try 
			{
				connection.close();
				connection=null;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

}
