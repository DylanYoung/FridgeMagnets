package Server;
/**
 * 
 */

/**
 * @author Dylan
 *
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import Client.FridgeClient;
import Client.MagnetData;

/**
 * @author Dylan
 *
 */
public class FridgeServer {
	private static final int port = 13000;
	
	
	private ArrayList<FridgeConnection> connections;
	private ArrayList<MagnetData> magnets;
	private ServerSocket server;
	private Socket client;
	private final ReentrantLock lock;
	
	/**
	 * Instantiates a new fridge server with
	 * 	60 random magnets, positioned within the 
	 * 	bounds of the fridge.
	 * Requires FridgeClient for height reference
	 */
	public FridgeServer()
	{
		this.connections = new ArrayList<FridgeConnection>();
		this.magnets = new ArrayList<MagnetData>();
		this.lock = new ReentrantLock();
		
		
		for (int i =0; i < 60; i++)
		{
			int x = (int) (Math.random() * ( (FridgeClient.HEIGHT*2/3) - 15 ) );
			int y = (int) (Math.random() * (FridgeClient.HEIGHT - 40) );
			char letter = (char) (65 + (int) (Math.random() * 26) );
	
			this.magnets.add(new MagnetData(i, x, y, letter) );
		}
		
		String name = "CASEY MEIJER";
		int x = FridgeClient.HEIGHT*2/3 - 20 ;
		int y = 10;
		
		for (int i = name.length(); i > 0; i--)
		{
			this.magnets.add(new MagnetData(60+name.length()-i
					, x,  y + (int) Math.round(Math.random()*2 - 1)*4
					, name.charAt(i-1) ));
			x = x - 15;
			y = y + (int) Math.round(Math.random()*2 - 1)*2;
		}		
	}

	/**
	 * Runs an infinite loop looking for new connections
	 * Spins off a FridgeConnection thread on connection
	 * Prints stack trace and exits with error on Exception
	 */
	public void listen()
	{
		try
		{
			this.server = new ServerSocket(port);
			while (true){
				System.out.println("SERVER: Waiting for client...");
				client = server.accept();
				FridgeConnection connection = new FridgeConnection(this, this.client);
				connections.add(connection);
				connection.start();
				System.out.println("SERVER: Client launched.");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	/**
	 * Returns the list of magnets on the fridge
	 */
	public ArrayList<MagnetData> getMagnets() {
		return magnets;
	}
	
	
	/**
	 * Locks the object and moves the magnet
	 * 	corresponding to data.id to position
	 * 	specified by data.x and data.y
	 * Broadcasts the changes to clients
	 * 	and unlocks the object
	 */
	public void send(MagnetData data){
		this.lock.lock();
		this.magnets.get(data.getId()).move(data.getX(), data.getY());
		try
		{
			for (FridgeConnection c: connections)
				c.send(data);
		}
		catch (Exception e)	{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Removes a client connection (in response
	 * 	to "teardown")
	 */
	public void disconnect(FridgeConnection connection)
	{
		connections.remove(connection);
	}
	
	// driver.  

	/**
	 * Instantiates a new fridge server and
	 * 	listens for incoming connections
	 */
	public static void main(String arg[])
	{
		FridgeServer server = new FridgeServer();
		server.listen();
	}



}


