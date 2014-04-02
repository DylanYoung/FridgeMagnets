package Client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Casey
 * 	~ Based on ChatClient/ChatServer as presented in class
 * 
 * FridgeClient allows you to choose a host and connect with a FridgeServer,
 * 	either locally or remotely
 */
public class FridgeClient extends Thread {

	public static final int HEIGHT = 600;
	private String host;
	private static final int port = 13000;
	private JFrame frame;
	private Socket s = null;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	private ArrayList<Magnet> magnets;
	
	

	/**
	 * Constructs a fridge client by creating a JFrame to depict the fridge
	 * 	and initializing a socket
	 */
	public FridgeClient()
	{
		this.magnets = new ArrayList<Magnet>();
		constructFrame();
		constructSocket();
	}
	
	
	/**
	 * Constructs the JFrame with a ratio of 2:3 
	 * 	and centered on the desktop. Also adds 
	 * 	a window closing listener to gracefully
	 * 	terminate connection with the server	
	 */
	private void constructFrame()
	{
		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setSize(HEIGHT*2/3, HEIGHT);
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);
		this.frame.setTitle("Fridge");
		
		this.frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if (FridgeClient.this.s != null)
					FridgeClient.this.send(new MagnetData());
				System.exit(0);
			}
		});
		
		this.frame.getContentPane().setLayout(null);	
	}
	
	/**
	 * Prompts for a FridgeServer host
	 * 	and attempts to connect via socket
	 *  while creating Object input/output streams
	 * 	If connection fails, it prompts for another host
	 */
	private void constructSocket(){
		host = JOptionPane.showInputDialog("Enter a host for your fridge: ");
		try{
			this.s = new Socket(host, port);
			this.output = new ObjectOutputStream(this.s.getOutputStream());
			this.input = new ObjectInputStream(this.s.getInputStream());	
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Cannot connect to host; try again.");
			constructSocket();
		}	
	}
	
    /**
     * returns a reference to the JFrame frame
     */
    public JFrame getFrame(){
    	return this.frame;
    }

	/**
	 * Sends the MagnetData object to the server
	 * 	via the Object Output Stream
	 * 	Prints stack trace on exception
	 */
	public void send(MagnetData data) {
		try{
			this.output.writeObject(data);
		} catch (IOException e){
			e.printStackTrace();
		}		
	}
	
	/**
	 * Reads a MagnetData object from the server
	 * 	and returns it
	 * 	Prints stack trace on exception and returns null
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
	 * Runs the client by creating an infinite loop
	 * 	that polls for MagnetData from the server
	 * Adds or moves the magnet depending on type of data
	 */
	public void run(){
		MagnetData data;
		while(true){
			data = readData();
			switch(data.getType()){
			case SETUP:
				addMagnet(new Magnet(data.getId()
						, data.getX(), data.getY(), data.getLetter(), this));
				break;
			case MOVE:
				this.magnets.get(data.getId()).move(data.getX(), data.getY());
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Adds the given magnet to the JFrame
	 * 	and repaints
	 */
	private void addMagnet(Magnet magnet) {
		this.magnets.add(magnet);
		this.frame.getContentPane().add(magnet);
		this.frame.repaint();
	}

	
	/**
	 * Main function schedules the creation of
	 *  a new fridge client by the EDT
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				FridgeClient client = new FridgeClient();
				client.getFrame().setVisible(true);
				client.start();
			}
		});	
	}
}
