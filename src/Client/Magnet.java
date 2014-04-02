package Client;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;


/**
 * 
 */

/**
 * @author Casey
 * 
 * Magnet is a JLabel that represents a magnet
 * 	shaped like a Character to be added to a fridge
 * 	by FridgeClient
 * 
 * Acts as a Mouse(motion)Listener to detect movements
 */
public class Magnet extends JLabel
implements MouseMotionListener, MouseListener {
	
	
	private static final long serialVersionUID = 1L;
	
	// Unique identifier
	private int id;
	
	// x and y coordinates
	private int x, y;
	
	// dx and dy to keep track of offset of mouse from 
	//	x and y (for moving magnets)
	private int dx, dy;
	
	//FridgeClient instantiates the fridge to hold the magnets
	private FridgeClient client;
	
	// Keeps track of whether the magnet is being moved
	private boolean moving = false;
	
	/**
	 * Instantiates Magnet with coordinates, client, 
	 * 	id, and Character
	 */
	public Magnet(int id, int x, int y
			, char letter, FridgeClient client){
		setFont(new Font("Monospaced", Font.BOLD, 25));
		setText( Character.toString(letter) );
		this.x = x;
		this.y = y;
		this.client = client;
		this.id = id;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		setBounds(x, y, 20, 20);
	}
	

	/**
	 * Change coordinates of magnet to x, y
	 */
	public void move(int x, int y){
		if(x != this.x && y != this.y){
			this.x = x;
			this.y = y;
			setBounds(this.x, this.y, 20, 20);
		}
	}
	
	/**
	 * Helper function: checks if the coordinates x,y
	 * 	are in the bounds of the magnet (for moving)
	 */
	public boolean inBounds(int x, int y)
	{
		if(0 <= x && 20 >= x && 0 <= y && 20 >= y)
			return true;
		return false;
	}
	

	/**
	 * Sets magnet to moving on mouse press
	 * 	and gets the offset of mouse from position
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(inBounds(x, y)){
			this.dx = x;
			this.dy = y;
			this.moving = true;
		}else
			this.moving = false;
		
	}

	
	/**
	 * Moves the magnet on mouse dragged
	 * 	(if it is toggles to move)
	 */
	public void mouseDragged(MouseEvent e) {
		if (this.moving){
			move(this.x - this.dx + e.getX()
					, this.y - this.dy + e.getY());
			this.client.send(new MagnetData(this.id, this.x, this.y));
		}
		
	}
	
	/**
	 * Toggles magnet moving to false
	 * 	on mouse release
	 */
	public void mouseReleased(MouseEvent e) {
		if (this.moving)
			this.moving = false;
		
	}


	/* (non-Javadoc)
	 * Unused MouseListener and 
	 * 	MouseMotionListener functions
	 */
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}

	
}
