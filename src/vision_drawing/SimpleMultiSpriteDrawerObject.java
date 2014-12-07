package vision_drawing;

import genesis_event.HandlerRelay;
import vision_sprite.MultiSpriteDrawer;
import vision_sprite.Sprite;

/**
 * This class uses a multiSpriteDrawer to represent itself
 * 
 * @author Mikko Hilpinen
 * @since 7.12.2014
 */
public class SimpleMultiSpriteDrawerObject extends SimpleSpriteDrawerObject<MultiSpriteDrawer>
{
	// CONSTRUCTOR	-----------------------
	
	/**
	 * Creates a new object.
	 * @param initialDepth The drawing depth used by the object
	 * @param sprites The sprites that represent this object
	 * @param handlers The handlers that will handle this object
	 */
	public SimpleMultiSpriteDrawerObject(int initialDepth, Sprite[] sprites, 
			HandlerRelay handlers)
	{
		super(initialDepth, handlers);
		setSpriteDrawer(new MultiSpriteDrawer(sprites, this, handlers));
	}
}
