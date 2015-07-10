package vision_sprite;

import genesis_event.Actor;
import genesis_event.HandlerRelay;
import genesis_util.Vector3D;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import omega_util.DependentGameObject;
import omega_util.GameObject;

/**
 * Spritedrawer is able to draw animated sprites for an object. Object's can 
 * draw the sprite(s) calling the drawSprite method.<p>
 * 
 * The spriteDrawer can be tied into a single object, making it die 
 * when that object does.
 *
 * @author Mikko Hilpinen.
 * @since 2.7.2013.
 */
public abstract class SpriteDrawer extends DependentGameObject<GameObject> implements Actor
{
	// ATTRIBUTES	-------------------------------------------------------
	
	private double imageSpeed, imageIndex;
	private AnimationListenerHandler listenerhandler;
	private Vector3D forcedOrigin;
		
		
	// CONSTRUCTOR	-------------------------------------------------------
		
	/**
	 * Creates a new spritedrawer with the given sprite to draw.
	 *
	 * @param user The object the drawer is tied into. The spritedrawer will 
	 * automatically die when the user dies. (Optional)
	 * @param handlers The handlers that will be used in the animation process
	 */
	public SpriteDrawer(GameObject user, HandlerRelay handlers)
	{
		super(user, handlers);
		
		// Initializes the attributes
		this.listenerhandler = new AnimationListenerHandler(false);
		
		this.imageSpeed = 0.1;
		this.imageIndex = 0;
	}
	
	
	// ABSTRACT METHODS	--------------------------------------------------
	
	/**
	 * @return The sprite that is currently being drawn / used
	 */
	protected abstract Sprite getCurrentSprite();
	
	
	// IMPLEMENTED METHODS	----------------------------------------------

	@Override
	public void act(double steps)
	{
		// Animates the sprite
		animate(steps);
	}
	
	
	// GETTERS & SETTERS	-----------------------------------------------
	
	/**
	 * @return The sprite as which the object is represented
	 */
	public Sprite getSprite()
	{
		return getCurrentSprite();
	}
	
	/**
	 * @return How fast the frames in the animation change (frames / step) 
	 * (default at 0.1)
	 */
	public double getImageSpeed()
	{
		return this.imageSpeed;
	}
	
	/**
	 * Changes how fast the frames in the animation change
	 * 
	 * @param imageSpeed The new animation speed (frames / step) (0.1 by default)
	 */
	public void setImageSpeed(double imageSpeed)
	{
		this.imageSpeed = imageSpeed;
	}
	
	/**
	 * Changes the image speed so that a single animation cycle will last 
	 * <b>duration</b> steps
	 *
	 * @param duration How many steps will a single animation cycle last
	 */
	public void setAnimationDuration(int duration)
	{
		// Checks the argument
		if (duration == 0)
			setImageSpeed(0);
		else
			setImageSpeed(getSprite().getImageNumber() / (double) duration);
	}
	
	/**
	 * @return Which subimage from the animation is currently drawn [0, numberOfSubimages[
	 */
	public int getImageIndex()
	{
		return (int) this.imageIndex;
	}
	
	/**
	 * Changes which subimage from the animation is currently drawn
	 * 
	 * @param imageIndex The index of the subimage drawn [0, numberOfSubimages[
	 */
	public void setImageIndex(int imageIndex)
	{
		this.imageIndex = imageIndex;
		// Also checks the new index
		checkImageIndex();
	}
	
	/**
	 * @return The animationlistenerhandler that will inform animationlisteners 
	 * about the events in the animation
	 */
	public AnimationListenerHandler getAnimationListenerHandler()
	{
		return this.listenerhandler;
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
	 * @param origin The new origin to be used. Null means that the sprite's origin 
	 * should be used (default)
	 */
	public void setOrigin(Vector3D origin)
	{
		this.forcedOrigin = origin;
	}
	
	
	// OTHER METHODS	---------------------------------------------------
	
	/**
	 * Draws the sprite. Should be called in the DrawnObject's drawSelf 
	 * method or in another similar method. The sprite's origin will automatically be placed 
	 * in the (0, 0) coordinates.
	 * 
	 * @param g2d The graphics object that does the actual drawing
	 */
	public void drawSprite(Graphics2D g2d)
	{
		// Draws the sprite
		drawSprite(g2d, getImageIndex());
	}
	
	/**
	 * Draws the sprite. Should be called in the DrawnObject's drawSelfBasic 
	 * method or in another similar method. The sprite's origin will automatically be placed 
	 * in the (0, 0) coordinates.
	 * 
	 * @param g2d The graphics object that does the actual drawing
	 * @param imageindex Which subimage of the sprite is drawn (used with 
	 * drawers that aren't automatically animated)
	 */
	public void drawSprite(Graphics2D g2d, int imageindex)
	{
		AffineTransform lastTransform = g2d.getTransform();
		
		// Moves the sprite according to its origin
		g2d.translate(-getOrigin().getFirst(), -getOrigin().getSecond());
		
		// Scales the sprite according to it's status
		g2d.scale(getSprite().getScaling().getFirst(), getSprite().getScaling().getSecond());
		
		// Draws the sprite
		g2d.drawImage(getSprite().getSubImage(imageindex), 0, 0, null);
		
		g2d.setTransform(lastTransform);
	}
	
	// Handles the change of the image index
	private void animate(double steps)
	{
		this.imageIndex += getImageSpeed() * steps;
		checkImageIndex();
	}
	
	// Returns the imageindex to a valid value
	private void checkImageIndex()
	{
		int imageindexlast = getImageIndex();
		
		// TODO: Can apprarently throw a nullPointer exception in very rare circumstances
		this.imageIndex = this.imageIndex % getSprite().getImageNumber();
		
		if (this.imageIndex < 0)
			this.imageIndex += getSprite().getImageNumber();
		
		// If image index changed (cycle ended / looped), informs the listeners
		if (getImageIndex() != imageindexlast)
			getAnimationListenerHandler().onAnimationEnd(this);
	}
}
