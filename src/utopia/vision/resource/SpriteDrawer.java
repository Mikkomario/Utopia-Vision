package utopia.vision.resource;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import utopia.genesis.util.Vector3D;

/**
 * Spritedrawer is able to draw and animate sprites.
 * @author Mikko Hilpinen.
 * @since 2.7.2013.
 */
public abstract class SpriteDrawer
{
	// TODO: Make the user call animation instead. Remove handled and actor implementations
	
	// ATTRIBUTES	-------------------------------------------------------
	
	private double imageSpeed = 0.1, imageIndex = 0;
	//private AnimationListenerHandler listenerhandler;
	private Vector3D forcedOrigin = null;
		
		
	// CONSTRUCTOR	-------------------------------------------------------
		
	/**
	 * Creates a new sprite drawer that uses the sprite's default origin
	 */
	public SpriteDrawer()
	{
		// Simple constructor
	}
	
	/**
	 * Creates a new sprite drawer
	 * @param origin The origin the drawer uses. This will override the sprite's default 
	 * origin
	 */
	public SpriteDrawer(Vector3D origin)
	{
		this.forcedOrigin = origin;
	}
	
	
	// ABSTRACT METHODS	--------------------------------------------------
	
	/**
	 * @return The sprite that is currently being drawn / used
	 */
	public abstract Sprite getSprite();
	
	
	// GETTERS & SETTERS	-----------------------------------------------
	
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
		this.imageIndex = index;
		// Also checks the new index // TODO: Use differently
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
	private void setFrameIndex(double index)
	{
		if (getSprite() == null)
			return;
		
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
		
		this.imageIndex = newIndex;
		// If image index changed (cycle ended / looped), informs the listeners
		//if (getImageIndex() != imageindexlast)
		//	getAnimationListenerHandler().onAnimationEnd(this);
	}
}
