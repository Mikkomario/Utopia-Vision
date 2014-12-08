package vision_drawing;

import vision_sprite.SingleSpriteDrawer;
import vision_sprite.Sprite;
import exodus_object.GameObject;
import exodus_util.Transformable;
import genesis_event.HandlerRelay;
import genesis_util.StateOperator;

/**
 * These drawers are dependent spriteDrawers that draw a single sprite (at once)
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of object that uses this drawer
 * @since 8.12.2014
 */
public class DependentSingleSpriteDrawer<T extends GameObject & Transformable> extends 
		DependentSpriteDrawer<T, SingleSpriteDrawer>
{
	// CONSTRUCTOR	------------------------------
	
	/**
	 * Creates a new drawer
	 * @param user The object that uses the drawer
	 * @param initialDepth The drawing depth used by the drawer
	 * @param sprite The sprite used by the drawer
	 * @param handlers The handlers that will handle the drawer
	 */
	public DependentSingleSpriteDrawer(T user, int initialDepth, Sprite sprite, 
			HandlerRelay handlers)
	{
		super(user, initialDepth, null, handlers);
		setSpriteDrawer(new SingleSpriteDrawer(sprite, this, handlers));
	}

	/**
	 * Creates a new drawer
	 * @param user The object that uses the drawer
	 * @param initialDepth The drawing depth used by the drawer
	 * @param sprite The sprite used by the drawer
	 * @param handlers The handlers that will handle the drawer
	 * @param isVisibleOperator The stateOperator that handles the drawer's visibility
	 */
	public DependentSingleSpriteDrawer(T user, int initialDepth, Sprite sprite, 
			HandlerRelay handlers, StateOperator isVisibleOperator)
	{
		super(user, initialDepth, null, handlers, isVisibleOperator);
		setSpriteDrawer(new SingleSpriteDrawer(sprite, this, handlers));
	}
}
