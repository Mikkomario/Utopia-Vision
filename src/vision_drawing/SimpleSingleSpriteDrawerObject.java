package vision_drawing;

import genesis_event.HandlerRelay;
import vision_sprite.SingleSpriteDrawer;
import vision_sprite.Sprite;

/**
 * This object uses a singleSpriteDrawer to draw itself.
 * 
 * @author Mikko Hilpinen
 * @since 7.12.2014
 */
public class SimpleSingleSpriteDrawerObject extends SimpleSpriteDrawerObject<SingleSpriteDrawer>
{
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new object
	 * @param initialDepth The drawing depth the object initially has
	 * @param sprite The sprite the object uses to draw itself
	 * @param handlers The handlers that will handle the object
	 */
	public SimpleSingleSpriteDrawerObject(int initialDepth, Sprite sprite, HandlerRelay handlers)
	{
		super(initialDepth, handlers);
		setSpriteDrawer(new SingleSpriteDrawer(sprite, this, handlers));
	}
}
