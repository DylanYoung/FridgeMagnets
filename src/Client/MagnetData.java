package Client;
import java.io.Serializable;

/**
 * Serializable object class that is used as a means for transmitting
 * MagnetData between client and server.  Also contains an instance of a special type,
 * Type, which indicates whether it's a magnet movement, a "setup" message, 
 * or a "teardown" message.
 * 
 * @author Casey
 *
 */
public class MagnetData implements Serializable {
	
	/**
	 * enum for the type of data:
	 * 	"move", "setup", or "teardown"
	 */
	public enum Type
	{
		SETUP,
		MOVE,
		TEARDOWN
	}
	public static final long serialVersionUID = 1L;

	// unique identifier
	private int id;
	
	// type of data
	private Type type;
	
	// Magnet shape (character)
	private char letter;
	
	// Magnet location
	private int x,y;
	
	
	/**
	 * Initialize data with location, id,
	 * 	and type = "move"
	 */
	public MagnetData(int id, int x, int y) {
		this.type = Type.MOVE;
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Initialize data with location, id, character
	 * 	and type = "setup"
	 */
	public MagnetData(int id, int x, int y, char letter){
		this.type = Type.SETUP;
		this.id = id;
		this.x = x;
		this.y = y;
		this.letter = letter;
	}

	/**
	 * Initialize with type = "teardown"
	 */
	public MagnetData(){
		this.type = Type.TEARDOWN;
	}
	
	/**
	 * Sets the location to x, y
	 */
	public void move(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the shape of the magnet
	 * 	(character)
	 */
	public char getLetter(){
		return this.letter;
	}

	/**
	 * returns x coordinate
	 */
	public int getX(){
		return this.x;
	}
	
	/**
	 * returns y coordinate
	 */
	public int getY(){
		return this.y;
	}
	
	/**
	 * returns the unique identifier
	 */
	public int getId()
	{
		return this.id;
	}
	
	/**
	 * returns the type of the data
	 */
	public Type getType()
	{
		return this.type;
	}
}
