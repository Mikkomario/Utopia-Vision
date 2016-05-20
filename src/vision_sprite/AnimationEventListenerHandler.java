package vision_sprite;

import genesis_event.Handler;
import genesis_event.HandlerRelay;
import genesis_event.HandlerType;
import utopia.vision.event.AnimationListenerHandler;

/**
 * Animationlistenerhandler informs numerous animationlisteners about animation 
 * events
 *
 * @author Mikko Hilpinen.
 * @since 28.8.2013.
 * @see SpriteDrawer
 * @deprecated Replaced with {@link AnimationListenerHandler}
 */
public class AnimationEventListenerHandler extends Handler<AnimationListener> implements 
		AnimationListener
{
	// ATTRIBUTES	----------------------------------------------------
	
	private SpriteDrawer lastdrawer;
	
	
	// CONSTRUCTOR	----------------------------------------------------
	
	/**
	 * Creates a new empty animationlistenerhandler with the given information
	 *
	 * @param autodeath Will the handler die when it runs out of listeners
	 * @param superhandler The animationlistenerhandler that will inform 
	 * the handler about animation events (optional)
	 */
	public AnimationEventListenerHandler(boolean autodeath, AnimationEventListenerHandler superhandler)
	{
		super(autodeath);
		
		if (superhandler != null)
			superhandler.add(this);
	}
	
	/**
	 * Creates a new handler
	 * @param autoDeath Will the handler die once it runs out of handled objects
	 * @param handlers The handlers that will handle this handler
	 */
	public AnimationEventListenerHandler(boolean autoDeath, HandlerRelay handlers)
	{
		super(autoDeath, handlers);
	}
	
	/**
	 * Creates a new handler
	 * @param autoDeath Will the handler die once it runs out of handled objects
	 */
	public AnimationEventListenerHandler(boolean autoDeath)
	{
		super(autoDeath);
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------

	// TODO: Create new animation events (animation started, animation stopped, image changed, 
	// cycle looped, even sprite changed for multi sprite drawers?)
	
	@Override
	public HandlerType getHandlerType()
	{
		return VisionHandlerType.ANIMATIONLISTENERHANDLER;
	}

	@Override
	protected boolean handleObject(AnimationListener h)
	{
		// Informs the onject about the animation event
		h.onAnimationEnd(this.lastdrawer);
		
		return true;
	}
	
	@Override
	public void onAnimationEnd(SpriteDrawer spritedrawer)
	{
		// Remembers the data
		this.lastdrawer = spritedrawer;
		// Informs all listeners about the event
		handleObjects(true);
		this.lastdrawer = null;
	}
}
