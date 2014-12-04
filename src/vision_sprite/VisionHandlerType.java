package vision_sprite;

import genesis_event.HandlerType;

/**
 * These are the different types of handlers used in the Vision module
 * 
 * @author Mikko Hilpinen
 * @since 4.12.2014
 */
public enum VisionHandlerType implements HandlerType
{
	/**
	 * AnimationListenerHandler informs objects about animation lapses
	 */
	ANIMATIONLISTENERHANDLER;
	
	
	// IMPLEMENTED METHODS	-------------------------

	@Override
	public Class<?> getSupportedHandledClass()
	{
		return AnimationListener.class;
	}
}
