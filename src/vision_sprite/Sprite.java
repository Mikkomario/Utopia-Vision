package vision_sprite;

import genesis_event.Handled;
import genesis_util.HelpMath;
import genesis_util.StateOperator;
import genesis_util.Vector2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import arc_bank.ResourceInitializationException;


/**
 * This object represents a drawn image that can be animated. Sprites are
 * meant to be used in multiple objects and those objects should handle the
 * animation (this class merely loads and provides all the neccessary images)
 *
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public class Sprite implements Handled
{	
	// ATTRIBUTES	-------------------------------------------------------
	
	private BufferedImage[] images;
	
	private Vector2D origin, forcedDimensions;
	private StateOperator isDeadStateOperator;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * This method creates a new sprite based on the information provided by 
	 * the caller. The images are loaded from a strip that contains one or more 
	 * images.
	 *
	 * @param filename The location of the loaded image (data/ is added 
	 * automatically to the beginning)
	 * @param numberOfImages How many separate images does the strip contain?
	 * @param origin The sprite's origin's coordinates (relative)
	 */
	public Sprite(String filename, int numberOfImages, Vector2D origin)
	{
		// Checks the variables
		if (filename == null || numberOfImages <= 0)
			throw new IllegalArgumentException();
		
		// Initializes attributes
		this.origin = origin;
		this.isDeadStateOperator = new StateOperator(false, true);
		this.forcedDimensions = null;
		
		// Loads the image
		File img = new File("data/" + filename);
		BufferedImage strip = null;
		
		try
		{
			strip = ImageIO.read(img);
		}
		catch (IOException ioe)
		{
			throw new ResourceInitializationException("Failed to load the image data/" + 
					filename);
		}
		
		// Creates the subimages
		this.images = new BufferedImage[numberOfImages];
		
		// Calculates the subimage width
		int sw = strip.getWidth() / numberOfImages;
		
		for (int i = 0; i < numberOfImages; i++)
		{
			// Calculates the needed variables
			int sx;
			sx = i*sw;
			
			this.images[i] = strip.getSubimage(sx, 0, sw, strip.getHeight());
		}
		
		// If an origin position was set to -1, sets it to the middle of the 
		// sprite
		double origX = getOrigin().getFirst();
		double origY = getOrigin().getSecond();
		
		if (HelpMath.areApproximatelyEqual(origX, -1))
			origX = getImageDimensions().getFirst() / 2;
		if (HelpMath.areApproximatelyEqual(origY, -1))
			origY = getImageDimensions().getSecond() / 2;
		
		this.origin = new Vector2D(origX, origY);
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------------------------
	
	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadStateOperator;
	}
	
	
	// GETTERS & SETTERS	------------------------------------------------
	
	/**
	 * @return The sprite's origin's coordinates (relative)
	 */
	public Vector2D getOrigin()
	{
		return this.origin.times(getScaling());
	}
	
	/**
	 * @return How much the sprite is scaled to fill the forced dimensions
	 */
	public Vector2D getScaling()
	{
		if (this.forcedDimensions != null)
			return getDimensions().dividedBy(getImageDimensions());
		else
			return Vector2D.identityVector();
	}
	
	/**
	 * @return The size of the sprite
	 */
	public Vector2D getDimensions()
	{
		if (this.forcedDimensions != null)
			return this.forcedDimensions;
		
		return getImageDimensions();
	}
	
	/**
	 * @return returns how many subimages exist within this sprite
	 */
	public int getImageNumber()
	{
		return this.images.length;
	}
	
	
	// OTHER METHODS	------------------------------------------------------------
	
	/**
	 * This method returns a single subimage from the sprite.
	 *
	 * @param imageIndex The index of the image to be drawn [0, numberOfImages[
	 * @return The subimage from the given index
	 * @see #getImageNumber()
	 */
	public BufferedImage getSubImage(int imageIndex)
	{
		// Checks the given index and adjusts it if needed
		if (imageIndex < 0 || imageIndex >= this.images.length)
			imageIndex = Math.abs(imageIndex % this.images.length);
		
		return this.images[imageIndex];
	}
	
	/**
	 * Sets the sprite to have the given size.
	 * 
	 * @param newDimensions The new size of the sprite. Use null if you want to use the 
	 * sprite's original size.
	 * @notice This size is used when the sprite is drawn using a SpriteDrawer, 
	 * if you draw the sprite using another class, you must use the getXScale() 
	 * and getYScale() -methods.
	 */
	public void forceDimensions(Vector2D newDimensions)
	{
		this.forcedDimensions = newDimensions;
	}
	
	/**
	 * Permanently scales the sprite with the given modifiers
	 * @param scaling How much the image is scaled
	 */
	public void scale(Vector2D scaling)
	{
		// If there is not yet any forced scaling, initializes it
		forceDimensions(getDimensions().times(scaling));
	}
	
	private Vector2D getImageDimensions()
	{
		return new Vector2D(getSubImage(0).getWidth(), getSubImage(0).getHeight());
	}
	
	// TODO: If you get bored, try to implement filters into the project
	// check: http://docs.oracle.com/javase/tutorial/2d/images/drawimage.html
}
