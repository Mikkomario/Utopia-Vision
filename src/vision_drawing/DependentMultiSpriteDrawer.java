package vision_drawing;

import omega_util.GameObject;
import omega_util.Transformable;
import vision_sprite.MultiSpriteDrawer;
import vision_sprite.Sprite;
import genesis_event.HandlerRelay;
import genesis_util.StateOperator;

/**
 * These drawers are dependent spriteDrawers that draw multiple sprites
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of object that uses this drawer
 * @since 8.12.2014
 */
public class DependentMultiSpriteDrawer<T extends GameObject & Transformable> extends 
		DependentSpriteDrawer<T, MultiSpriteDrawer>
{
	// CONSTRUCTOR	------------------------------
	
	/**
	 * Creates a new drawer
	 * @param user The object that uses the drawer
	 * @param initialDepth The drawing depth used by the drawer
	 * @param sprites The sprites used by the drawer
	 * @param handlers The handlers that will handle the drawer
	 */
	public DependentMultiSpriteDrawer(T user, int initialDepth, Sprite[] sprites, 
			HandlerRelay handlers)
	{
		super(user, initialDepth, null, handlers);
		setSpriteDrawer(new MultiSpriteDrawer(sprites, this, handlers));
	}

	/**
	 * Creates a new drawer
	 * @param user The object that uses the drawer
	 * @param initialDepth The drawing depth used by the drawer
	 * @param sprites The sprites used by the drawer
	 * @param handlers The handlers that will handle the drawer
	 * @param isVisibleOperator The stateOperator that handles the drawer's visibility
	 */
	public DependentMultiSpriteDrawer(T user, int initialDepth, Sprite[] sprites, 
			HandlerRelay handlers, StateOperator isVisibleOperator)
	{
		super(user, initialDepth, null, handlers, isVisibleOperator);
		setSpriteDrawer(new MultiSpriteDrawer(sprites, this, handlers));
	}
}
