package vision_sprite;

import java.util.HashMap;

import genesis_event.Handled;
import genesis_event.HandlerRelay;

/**
 * MultiSpriteDrawer can easily change between multiple different sprites to 
 * draw.
 * 
 * @author Mikko Hilpinen. 
 * Created 16.1.2014
 */
public class MultiSpriteDrawer extends SpriteDrawer
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private Sprite[] sprites;
	private int currentid;
	private HashMap<String, Integer> keywords;
	// TODO: Use model with sprites or something?
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new MultiSpriteDrawer with the given data
	 * 
	 * @param sprites A table containing the sprites the drawer will draw
	 * @param user The object the drawer is tied into. The spritedrawer will 
	 * automatically die when the user dies.
	 * @param handlers The handlers that will handle (animate) this drawer
	 */
	public MultiSpriteDrawer(Sprite[] sprites, Handled user, HandlerRelay handlers)
	{
		super(user, handlers);
		
		// Initializes attributes
		this.sprites = sprites;
		this.currentid = 0;
		this.keywords = new HashMap<String, Integer>();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------

	@Override
	protected Sprite getCurrentSprite()
	{
		return this.sprites[this.currentid];
	}

	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * Changes the currently shown sprite to the sprite with the given 
	 * index in the table
	 * 
	 * @param newIndex The index of the new shown sprite
	 * @param resetImageIndex Should the sprite's animation restart from 
	 * the beginning
	 */
	public void setSpriteIndex(int newIndex, boolean resetImageIndex)
	{
		if (this.sprites == null || getSpriteAmount() == 0)
			this.currentid = 0;
		// If the index is too large / small, loops through the list
		else
			this.currentid = Math.abs(newIndex % getSpriteAmount());
		
		if (resetImageIndex)
			setImageIndex(0);
	}
	
	/**
	 * Changes the currently shown sprite to the sprite tied to the given 
	 * keyword
	 * 
	 * @param keyword The keyword that tells which sprite should be changed to. 
	 * Use {@link #setKeyword(String, int)} to add a keyword to a sprite
	 * @param resetImageIndex Should the sprite's animation restart from 
	 * the beginning
	 * @see #setKeyword(String, int)
	 */
	public void setSpriteIndex(String keyword, boolean resetImageIndex)
	{
		// Only works if the keyword has been created
		if (!this.keywords.containsKey(keyword))
		{
			// TODO: Throw a real exception
			System.err.println("The spritedrawer doesn't have the keyword " + keyword);
			return;
		}
		
		setSpriteIndex(this.keywords.get(keyword), resetImageIndex);
	}
	
	/**
	 * Changes the shown sprite to the next one in the table. If the end of the 
	 * table was reached the index loops
	 * 
	 * @param resetImageIndex 
	 */
	public void changeToNextSprite(boolean resetImageIndex)
	{
		setSpriteIndex(this.currentid + 1, resetImageIndex);
	}
	// TODO: Rename these
	
	/**
	 * Changes the shown sprite to the last one in the table. If the start of 
	 * the table was reached the index loops
	 * 
	 * @param resetImageIndex
	 */
	public void changeToPreviousSprite(boolean resetImageIndex)
	{
		setSpriteIndex(this.currentid - 1, resetImageIndex);
	}
	
	/**
	 * This method ties a certain keyword to a certain sprite index so that 
	 * the index can be accessed with the keyword.
	 * 
	 * @param keyword The new keyword to be tied to the given index
	 * @param spriteindex The index of the sprite the keyword is tied to
	 * @see #setSpriteIndex(String, boolean)
	 */
	public void setKeyword(String keyword, int spriteindex)
	{
		this.keywords.put(keyword, spriteindex);
	} // TODO: Rename, perhaps remove
	
	/**
	 * @return How many sprites are being used in this drawer
	 */
	public int getSpriteAmount()
	{
		return this.sprites.length;
	} // TODO: Rename
	
	
	// GETTERS & SETTERS	----------------------
	
	/**
	 * @return The current sprite index
	 */
	public int getSpriteIndex()
	{
		return this.currentid;
	} // TODO: Rename
}
