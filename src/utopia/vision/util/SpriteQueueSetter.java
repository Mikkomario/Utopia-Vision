package utopia.vision.util;

import java.util.ArrayList;
import java.util.List;

import utopia.inception.event.EventSelector;
import utopia.inception.util.SimpleHandled;
import utopia.vision.event.AnimationEvent;
import utopia.vision.event.AnimationEventListener;
import utopia.vision.event.AnimationEvent.EventType;
import utopia.vision.resource.Sprite;

/**
 * This class is able to interact with sprite drawers so that they display multiple sprites 
 * in succession.
 * @author Mikko Hilpinen
 * @since 26.6.2016
 */
public class SpriteQueueSetter extends SimpleHandled implements AnimationEventListener
{
	// ATTRIBUTES	-----------------
	
	private EventSelector<AnimationEvent> selector = 
			AnimationEvent.createSingleTypeSelector(EventType.ANIMATION_COMPLETED);
	private List<Sprite> sprites = new ArrayList<>();
	private boolean dieOnceComplete = false;
	private SetterType type;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new sprite setter. Remember to add it to a working animation event listener 
	 * handler later
	 * @param type How the setter should behave when the animation cycle ends
	 * @param sprites The sprites in this queue
	 */
	public SpriteQueueSetter(SetterType type, Sprite... sprites)
	{
		this.type = type;
		for (Sprite sprite : sprites)
		{
			this.sprites.add(sprite);
		}
	}
	
	/**
	 * Copies another sprite queue setter
	 * @param other the setter that is copied
	 */
	public SpriteQueueSetter(SpriteQueueSetter other)
	{
		this.type = other.type;
		this.sprites.addAll(other.sprites);
	}
	
	
	// IMPLEMENTED METHODS	---------

	@Override
	public EventSelector<AnimationEvent> getAnimationEventSelector()
	{
		return this.selector;
	}

	@Override
	public void onAnimationEvent(AnimationEvent event)
	{
		int lastIndex = this.sprites.indexOf(event.getSprite());
		int nextIndex = lastIndex + 1;
		
		if (nextIndex < this.sprites.size())
			event.getSource().setSprite(this.sprites.get(nextIndex), true);
		else if (this.dieOnceComplete || getType() == SetterType.SINGLE_USE)
			getIsDeadStateOperator().setState(true);
		else if (getType() == SetterType.LOOPING)
			event.getSource().setSprite(this.sprites.get(0), true);
	}
	
	
	// ACCESSORS	------------------
	
	/**
	 * @return How the setter behaves when the animation cycle ends
	 */
	public SetterType getType()
	{
		return this.type;
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Adds a sprite to the queue
	 * @param sprite The sprite added to the queue
	 */
	public void append(Sprite sprite)
	{
		this.sprites.add(sprite);
	}
	
	/**
	 * Removes a sprite from the queue
	 * @param sprite The sprite that will be removed
	 * @return Whether the queue contained the sprite
	 */
	public boolean remove(Sprite sprite)
	{
		return this.sprites.remove(sprite);
	}
	
	/**
	 * Makes the setter die once the animation cycle completes
	 */
	public void killOnceCompleted()
	{
		this.dieOnceComplete = true;
	}
	
	
	// ENUMERATIONS	------------------
	
	/**
	 * These are the different types of ways a sprite setter can work
	 * @author Mikko Hilpinen
	 * @since 26.6.2016
	 */
	public static enum SetterType
	{
		/**
		 * A single use sprite setter will die once it has completed once. The setter won't 
		 * function after that
		 */
		SINGLE_USE,
		/**
		 * A looping sprite setter will go back to the first sprite in the queue once the 
		 * cycle is complete
		 */
		LOOPING,
		/**
		 * A bring to end sprite setter will remain usable once the cycle is complete. The 
		 * setter will attempt to bring the cycle to an end.
		 */
		BRING_TO_END;
	}
}
