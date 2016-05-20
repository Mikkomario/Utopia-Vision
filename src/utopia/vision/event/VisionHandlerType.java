package utopia.vision.event;

import utopia.inception.handling.HandlerType;

/**
 * These are the different types of handlers introduced in the Vision project
 * @author Mikko Hilpinen
 * @since 4.12.2014
 */
public enum VisionHandlerType implements HandlerType
{
	/**
	 * AnimationListenerHandler informs objects about animation lapses
	 */
	ANIMATION_LISTENER_EVENT_HANDLER;
	
	
	// IMPLEMENTED METHODS	-------------------------

	@Override
	public Class<?> getSupportedHandledClass()
	{
		return AnimationEventListener.class;
	}
}
