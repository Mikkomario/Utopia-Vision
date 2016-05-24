package utopia.vision.resource;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

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
	
	private double imageSpeed = 0.1, imageIndex = 0;
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
	 * @return How fast the frames in the animation change (frames / step) 
	 * (default at 0.1 = one frame in 10 steps)
	 */
	public double getAnimationSpeed()
	{
		return this.imageSpeed;
	}
	
	/**
	 * Changes how fast the frames in the animation change
	 * @param speed The new animation speed (frames / step) (0.1 by default)
	 */
	public void setAnimationSpeed(double speed)
	{
		this.imageSpeed = speed;
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
		return (int) this.imageIndex;
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
		drawSprite(getSprite(), frameIndex, getOrigin(), g2d);
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
	 * @param animationSpeed The speed at which the animation traversed (default at 0.1)
	 */
	public void animate(double steps, double animationSpeed)
	{
		int previousIndex = getFrameIndex();
		
		// Checks whether the animation cycled
		if (setFrameIndex(this.imageIndex + animationSpeed * steps))
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
	
	/**
	 * Draws a sprite
	 * @param sprite The sprite that is drawn
	 * @param frameIndex The index of the drawn frame
	 * @param origin The origin that is used. Use null for sprite's default origin
	 * @param g2d The graphics object that does the drawing
	 */
	public static void drawSprite(Sprite sprite, int frameIndex, Vector3D origin, Graphics2D g2d)
	{
		AffineTransform lastTransform = g2d.getTransform();
		
		if (origin == null)
			origin = sprite.getOrigin();
		
		Vector3D scaling = sprite.getScaling();
		
		// Moves the sprite according to its origin
		g2d.translate(-origin.getX(), -origin.getY());
		
		// Scales the sprite according to it's status
		g2d.scale(scaling.getX(), scaling.getY());
		
		// Draws the sprite
		g2d.drawImage(sprite.getFrame(frameIndex), 0, 0, null);
		
		g2d.setTransform(lastTransform);
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
		
		this.imageIndex = newIndex;
		if (frameChanged)
			generateAnimationEvent(EventType.FRAME_CHANGED);
		
		return frameChanged;
	}
}
