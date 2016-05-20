package vision_sprite;

import genesis_event.Handled;
import utopia.vision.event.AnimationEventListener;
import utopia.vision.event.AnimationListenerHandler;

/**
 * Animationlistener is informed when an animation cycle ends.<br>
 * Remember to add the object into an AnimationListenerHandler
 *
 * @author Mikko Hilpinen.
 * @since 28.8.2013.
 * @see AnimationListenerHandler
 * @see SpriteDrawer
 * @deprecated Replaced with {@link AnimationEventListener}
 */
public interface AnimationListener extends Handled
{
	// TODO: Add new animation events + event selector
	/**
	 * This method is called when an animation of the sprite ends or, more 
	 * precisely, a cycle in the animation ends.
	 *
	 * @param source The spriteDrawer that draws the sprite who's 
	 * animation just completed a cycle.
	 */
	public void onAnimationEnd(SpriteDrawer source);
}
