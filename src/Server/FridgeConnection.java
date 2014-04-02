package Server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Client.MagnetData;



/**
 * Instantiates a connection to a FridgeClient
 * 	extends Thread to allow server to handle 
 * 	multiple clients
 * 
 * @author Casey
 */
public class FridgeConnection extends Thread {
	
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	// The server that holds the connection
	private FridgeServer server;
	
	
	/**
	 * Instantiates the connection with
	 * 	server and socket, creating input 
	 * 	and output streams and sending setup 
	 * 	messages to client
	 */
	public FridgeConnection(FridgeServer server, Socket s){
		this.server = server;
		constructSocket(s);
		
		for (MagnetData m : server.getMagnets())
			send(m);
	}
	
	/**
	 * Instantiates the socket and creates input
	 * 	 and output streams.
	 * Prints stack trace on exception
	 */
	private void constructSocket(Socket s){
		try{
			this.socket = s;
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());	
		} catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * Reads magnet data from the input stream
	 * 	and returns it
	 * Prints stack trace and returns null on exception
	 */
	public MagnetData readData(){
		try{
			MagnetData data = (MagnetData) this.input.readObject();
			return data;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Send magnet data via output stream
	 * Prints stack trace on exception
	 */
	public void send(MagnetData data) {
		try{
			this.output.writeObject(data);
		} catch (IOException e){
			e.printStackTrace();
		}		
	}
	
	/**
	 * Runs the thread by starting an
	 * 	infinite loop that reads data from
	 * 	the client.
	 * Disconnects the connection on "teardown"
	 * 	message, otherwise informs the server, 
	 * 	which updates internal data and other clients
	 */
	public void run(){
		MagnetData data;
		while (true){
			data = readData();
			if (data.getType() == MagnetData.Type.TEARDOWN){
				this.server.disconnect(this);
				return;
			}
			
			this.server.send(data);	
		}
	}
	

}
