package vision_sprite;

import genesis_event.Handled;

/**
 * Animationlistener is informed when an animation cycle ends.<br>
 * Remember to add the object into an AnimationListenerHandler
 *
 * @author Mikko Hilpinen.
 * @since 28.8.2013.
 * @see AnimationListenerHandler
 * @see SpriteDrawer
 */
public interface AnimationListener extends Handled
{
	/**
	 * This method is called when an animation of the sprite ends or, more 
	 * precisely, a cycle in the animation ends.
	 *
	 * @param source The spriteDrawer that draws the sprite who's 
	 * animation just completed a cycle.
	 */
	public void onAnimationEnd(SpriteDrawer source);
}
