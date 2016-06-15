package utopia.vision.resource;

import java.awt.Graphics2D;

import utopia.genesis.util.Vector3D;
import utopia.vision.event.AnimationEvent;
import utopia.vision.event.AnimationEvent.EventType;
import utopia.vision.event.AnimationListenerHandler;

/**
 * Spritedrawer is able to draw and animate sprites.
 * @author Mikko Hilpinen.
 * @since 2.7.2013.
 */
public class SpriteDrawer
{
	// ATTRIBUTES	-------------------------------------------------------
	
	private boolean animationSpeedDefined = false;
	private double animationSpeed = 0.1, frameIndex = 0;
	private Vector3D forcedOrigin = null;
	
	private Sprite sprite;
	private AnimationListenerHandler listenerHandler = null;
		
		
	// CONSTRUCTOR	-------------------------------------------------------
		
	/**
	 * Creates a new sprite drawer that uses the sprite's default origin
	 * @param sprite The sprite used by the drawer
	 */
	public SpriteDrawer(Sprite sprite)
	{
		this.sprite = sprite;
		if (sprite != null)
			this.animationSpeed = sprite.getDefaultAnimationSpeed();
	}
	
	/**
	 * Creates a new sprite drawer
	 * @param sprite The sprite used by the drawer
	 * @param origin The origin the drawer uses. This will override the sprite's default 
	 * origin
	 */
	public SpriteDrawer(Sprite sprite, Vector3D origin)
	{
		this.sprite = sprite;
		this.forcedOrigin = origin;
		
		if (sprite != null)
			this.animationSpeed = sprite.getDefaultAnimationSpeed();
	}
	
	
	// GETTERS & SETTERS	---------------------
	
	/**
	 * @return The sprite that is currently being drawn / used
	 */
	public Sprite getSprite()
	{
		return this.sprite;
	}
	
	/**
	 * Changes the sprite drawn by this drawer
	 * @param sprite The new sprite that will be drawn
	 * @param resetAnimation Should the animation be reset back to the first frame?
	 */
	public void setSprite(Sprite sprite, boolean resetAnimation)
	{
		this.sprite = sprite;
		
		if (!this.animationSpeedDefined && sprite != null)
			this.animationSpeed = sprite.getDefaultAnimationSpeed();
		
		generateAnimationEvent(EventType.SPRITE_CHANGED);
		if (resetAnimation)
			resetAnimation();
	}
	
	/**
	 * @return How fast the frames in the animation change (frames / step). The default value 
	 * depends from the sprite that is being drawn.
	 */
	public double getAnimationSpeed()
	{
		return this.animationSpeed;
	}
	
	/**
	 * Changes how fast the frames in the animation change
	 * @param speed The new animation speed (frames / step)
	 */
	public void setAnimationSpeed(double speed)
	{
		this.animationSpeedDefined = true;
		
		// Generates animation events if necessary
		if (speed == 0)
		{
			if (this.animationSpeed != 0)
				generateAnimationEvent(EventType.ANIMATION_SUSPENDED);
		}
		else if (this.animationSpeed == 0)
			generateAnimationEvent(EventType.ANIMATION_RESUMED);
			
		this.animationSpeed = speed;
	}
	
	/**
	 * Resets the animation speed back to the sprite's default
	 */
	public void resetAnimationSpeed()
	{
		this.animationSpeedDefined = false;
		if (this.sprite != null)
			this.animationSpeed = this.sprite.getDefaultAnimationSpeed();
	}
	
	/**
	 * Changes the image speed so that a single animation cycle will last 
	 * <b>duration</b> steps
	 * @param duration How many steps will a single animation cycle last
	 */
	public void setAnimationDuration(double duration)
	{
		// Checks the argument
		if (duration == 0)
			setAnimationSpeed(0);
		else
			setAnimationSpeed(getSprite().getLength() / duration);
	}
	
	/**
	 * @return Which subimage from the animation is currently drawn [0, sprite's length[
	 */
	public int getFrameIndex()
	{
		return (int) this.frameIndex;
	}
	
	/**
	 * Changes which frame from the animation is currently drawn
	 * @param index The index of the frame drawn [0, Sprite's length[
	 */
	public void setFrameIndex(int index)
	{
		setFrameIndex((double) index);
	}
	
	/**
	 * Resets the drawer's animation back to the first frame. Creates a new animation reset 
	 * event as well
	 */
	public void resetAnimation()
	{
		setFrameIndex(0.0);
		generateAnimationEvent(EventType.ANIMATION_RESET);
	}
	
	/**
	 * @return The animation event listener handler used with this drawer
	 */
	public AnimationListenerHandler getAnimationListenerHandler()
	{
		if (this.listenerHandler == null)
			this.listenerHandler = new AnimationListenerHandler();
		return this.listenerHandler;
	}
	
	/**
	 * @return The origin / offset used when drawing the sprite
	 */
	public Vector3D getOrigin()
	{
		if (this.forcedOrigin != null)
			return this.forcedOrigin;
		else
			return getSprite().getOrigin();
	}
	
	/**
	 * Changes the origin / offset that is used when drawing the sprite.
	 * @param origin The new origin to be used. Null sets the origin to the sprite's default
	 */
	public void setOrigin(Vector3D origin)
	{
		this.forcedOrigin = origin;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * Draws the sprite. Should be called in the DrawnObject's drawSelf 
	 * method or in another similar method. The drawer's origin will automatically be placed 
	 * in the (0, 0) coordinates and the sprite will be scaled to its real size.
	 * @param g2d The graphics object that does the actual drawing
	 */
	public void drawSprite(Graphics2D g2d)
	{
		// Draws the sprite
		drawSprite(g2d, getFrameIndex());
	}
	
	/**
	 * Draws the sprite. The drawer's origin will automatically be placed 
	 * in the (0, 0) coordinates and the sprite will be scaled to its real size.
	 * @param g2d The graphics object that does the actual drawing
	 * @param frameIndex Which frame of the sprite is draw
	 */
	public void drawSprite(Graphics2D g2d, int frameIndex)
	{
		drawSprite(g2d, frameIndex, getOrigin());
	}
	
	/**
	 * Draws the sprite using specific origin coordinates. This overrides the drawer's origin 
	 * status.
	 * @param g2d The graphics object that does the actual drawing
	 * @param frameIndex Which frame of the sprite is draw
	 * @param origin The origin used when drawing the sprite. Null will be interpreted as 
	 * the sprite's default origin.
	 */
	public void drawSprite(Graphics2D g2d, int frameIndex, Vector3D origin)
	{
		Sprite.drawSprite(getSprite(), frameIndex, origin, g2d);
	}
	
	/**
	 * Updates the drawer's animation
	 * @param steps The duration of the update
	 */
	public void animate(double steps)
	{
		animate(steps, getAnimationSpeed());
	}
	
	/**
	 * Updates the drawer's animation
	 * @param steps The duration of the update
	 * @param animationSpeed The speed at which the animation traversed (frames / step)
	 */
	public void animate(double steps, double animationSpeed)
	{
		int previousIndex = getFrameIndex();
		
		// Checks whether the animation cycled
		if (setFrameIndex(this.frameIndex + animationSpeed * steps))
		{
			if (animationSpeed > 0)
			{
				if (getFrameIndex() < previousIndex)
					generateAnimationEvent(EventType.ANIMATION_COMPLETED);
			}
			else if (getFrameIndex() > previousIndex)
				generateAnimationEvent(EventType.ANIMATION_COMPLETED);
		}
	}
	
	/**
	 * Generates a new animation event and informs all of the animation listeners attached 
	 * to this drawer
	 * @param type The type of the generated event
	 */
	public void generateAnimationEvent(EventType type)
	{
		if (this.listenerHandler != null)
		{
			this.listenerHandler.onAnimationEvent(new AnimationEvent(type, this, getSprite()));
		}
	}
	
	// Returns the imageindex to a valid value
	private boolean setFrameIndex(double index)
	{
		if (getSprite() == null)
			return false;
		
		double newIndex;
		if (index < 0)
		{
			newIndex = index;
			while (newIndex < 0)
			{
				newIndex += getSprite().getLength();
			}
		}
		else
			newIndex = index % getSprite().getLength();
		
		// Generates animation events when the frame changes
		boolean frameChanged = (int) newIndex != getFrameIndex();
		
		this.frameIndex = newIndex;
		if (frameChanged)
			generateAnimationEvent(EventType.FRAME_CHANGED);
		
		return frameChanged;
	}
}
