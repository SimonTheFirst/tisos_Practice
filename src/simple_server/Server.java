package simple_server;
import java.io.*;
import java.net.*;

public class Server {
	public final static int PORT = 43721;
	
	public static void main(String[] args) throws IOException
	{
		ServerSocket server = new ServerSocket(PORT);
		System.out.println("Server started");
		try
		{
			while(true)
			{
				Socket socket = server.accept();
				try
				{
					System.out.println("Someone connected");
					new ServerThread(socket);
				} catch (IOException e) {socket.close();}
			}
		}finally {server.close();}
	}
}
