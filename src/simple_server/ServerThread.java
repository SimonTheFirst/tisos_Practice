package simple_server;
import java.io.*;
import java.net.*;
import org.json.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class ServerThread extends Thread{
	private  Socket socket;
	private  BufferedReader in;
	private  DataOutputStream out;
	
	public ServerThread(Socket socket) throws IOException
	{
		this.socket=socket;
		in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		out = new DataOutputStream(this.socket.getOutputStream());
		start();
	}

	@Override
	public void run()
	{
		String inWord;
		DataBaseInterface dbInterface = new DataBaseInterface();
		Connection conn = dbInterface.connect();
		try
		{
			try
			{
					inWord = in.readLine();
					System.out.println("Echoing: " + inWord);
					if (inWord.equals("1"))
					{
						JSONObject replyObject = new JSONObject();
						replyObject.put("cities", getData(conn,"SELECT CityName FROM cities"));
						replyObject.put("airports", getData(conn,"SELECT Name FROM airports"));
						replyObject.put("aircrafts", getData(conn,"SELECT AircraftType FROM aircrafts"));
						this.send(replyObject.toString());
					}
					
					else
					{
						PreparedStatement pst = conn.prepareStatement(inWord);
						ResultSet rs = pst.executeQuery();
						if (!rs.next())
						{
							this.send("Error");
							rs.close();
							pst.close();
							this.downService();
						}
						else
						{
							JSONArray replyArray = new JSONArray();
							int count = rs.getMetaData().getColumnCount();
							String columnNames[] = {"cityFrom", "airFrom", "timezoneFrom","cityTo", "airTo", "timezoneTo", "departureDateTime", "landingDateTime", "aircraft", "price"}; 
							while(rs.next())
							{
								JSONObject jTemp = new JSONObject();
								for (int i = 1; i<= count; i++)
									jTemp.put(columnNames[i-1],rs.getString(i));
								replyArray.put(jTemp);
							}
							
							this.send(replyArray.toString());
							rs.close();
							pst.close();
							this.downService();
						}
					}
					
			} catch (SQLException e) 
				{
				  	System.out.println("An sql exception");
				  	this.send("Wrong sql query");
				  	e.printStackTrace();
				}
		} catch (IOException e) 
			{
				this.downService();
				this.send("Error has occured. Try again");
			}
		finally 
			{
				dbInterface.disconnect();
			}
	}
	
	private JSONArray getData (Connection conn, String query)
	{
		JSONArray keyArray = new JSONArray();
		try
		{
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				keyArray.put(rs.getString(1));
			}
			rs.close();
			pst.close();
		} 
		catch (SQLException e) 
		{
			System.out.println("An sql exception");
		  	this.send("Wrong sql query");
		  	e.printStackTrace();
		}
		return keyArray;
	}
	
	private void send(String word)
	{
		try
		{
			out.writeUTF(word+"\n");
			out.flush();
		}
		catch(IOException ignored) {}
	}

	private void downService()
	{
		try
		{
			if (!socket.isClosed())
			{
				socket.close();
				in.close();
				out.close();
				this.interrupt();
			}
		} catch (IOException ignored) {}
	}
}
