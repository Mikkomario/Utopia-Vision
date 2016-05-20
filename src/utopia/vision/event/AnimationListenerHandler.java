package utopia.vision.event;

import utopia.inception.event.EventSelector;
import utopia.inception.event.StrictEventSelector;
import utopia.inception.handling.Handler;
import utopia.inception.handling.HandlerType;

/**
 * This handler informs numerous animation event listeners about animation 
 * events
 * @author Mikko Hilpinen
 * @since 28.8.2013
 */
public class AnimationListenerHandler extends Handler<AnimationEventListener> implements 
		AnimationEventListener
{
	// ATTRIBUTES	-----------------------
	
	private AnimationEvent lastEvent = null;
	private EventSelector<AnimationEvent> selector = new StrictEventSelector<>();
	
	
	// IMPLEMENTED METHODS	---------------
	
	@Override
	public EventSelector<AnimationEvent> getAnimationEventSelector()
	{
		// The handler accepts all events
		return this.selector;
	}

	@Override
	public void onAnimationEvent(AnimationEvent event)
	{
		// Informs all listeners about the event
		this.lastEvent = event;
		handleObjects(true);
		this.lastEvent = null;
	}

	@Override
	protected boolean handleObject(AnimationEventListener h)
	{
		if (h.getAnimationEventSelector().selects(this.lastEvent))
			h.onAnimationEvent(this.lastEvent);
		return true;
	}
	
	@Override
	public HandlerType getHandlerType()
	{
		return VisionHandlerType.ANIMATION_LISTENER_EVENT_HANDLER;
	}
}
