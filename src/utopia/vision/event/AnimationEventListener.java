package utopia.vision.event;

import utopia.inception.event.EventSelector;
import utopia.inception.handling.Handled;

/**
 * These listeners are interested in new animation events
 * @author Mikko Hilpinen
 * @since 20.5.2016
 */
public interface AnimationEventListener extends Handled
{
	/**
	 * The return value of this method determines which events cause the 
	 * {@link #onAnimationEvent(AnimationEvent)} to be called. Only events selected by the 
	 * returned selector are delivered to the listener.
	 * @return A selector which determines, what kind of events the listener will receive
	 */
	public EventSelector<AnimationEvent> getAnimationEventSelector();
	
	/**
	 * This method is called when an animation event occurs. Only events accepted by the 
	 * listener's selector are included.
	 * @param event The event that occurred
	 */
	public void onAnimationEvent(AnimationEvent event);
}
