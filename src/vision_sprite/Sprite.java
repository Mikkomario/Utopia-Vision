package vision_sprite;

import genesis_util.HelpMath;
import genesis_util.Killable;
import genesis_util.StateOperator;
import genesis_util.Vector3D;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
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
public class Sprite implements Killable
{	
	// ATTRIBUTES	-------------------------------------------------------
	
	private BufferedImage[] images;
	
	private Vector3D origin, forcedDimensions;
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
	public Sprite(String filename, int numberOfImages, Vector3D origin)
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
		
		this.origin = new Vector3D(origX, origY);
	}
	
	// Copies another sprite
	private Sprite(Sprite other)
	{
		this.images = new BufferedImage[other.images.length];
		this.origin = other.origin;
		this.forcedDimensions = other.forcedDimensions;
		this.isDeadStateOperator = new StateOperator(other.isDeadStateOperator.getState(), 
				true);
		
		for (int i = 0; i < this.images.length; i++)
		{
			this.images[i] = other.images[i];
		}
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------------------------
	
	@Override
	public StateOperator getIsDeadStateOperator()
	{
		return this.isDeadStateOperator;
	}
	
	
	// GETTERS & SETTERS	------------------------------------------------
	
	/**
	 * @return The sprite's origin's coordinates (relative). 
	 * Notice that this is the scaled value.
	 */
	public Vector3D getOrigin()
	{
		return this.origin.times(getScaling());
	}
	
	/**
	 * @return How much the sprite is scaled to fill the forced dimensions
	 */
	public Vector3D getScaling()
	{
		if (this.forcedDimensions != null)
			return getDimensions().dividedBy(getImageDimensions());
		else
			return Vector3D.identityVector();
	}
	
	/**
	 * @return The size of the sprite
	 */
	public Vector3D getDimensions()
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
	 * Copies a sprite that has the given dimensions
	 * 
	 * @param newDimensions The new size of the sprite. Use null if you want to use the 
	 * sprite's original size.
	 * @return A sprite with the given dimensions
	 */
	public Sprite withDimensions(Vector3D newDimensions)
	{
		Sprite s = new Sprite(this);
		s.forcedDimensions = newDimensions;
		return s;
	}
	
	/**
	 * Gives a sprite that is scaled from the original
	 * @param scaling How much the sprite is scaled
	 * @return A sprite that is scaled the given amount
	 */
	public Sprite scaled(Vector3D scaling)
	{
		// If there is not yet any forced scaling, initializes it
		return withDimensions(getDimensions().times(scaling));
	}
	
	/**
	 * @return A sharpened version of this sprite
	 */
	public Sprite sharpened()
	{
		// Creates the sharpening kernel
		float[] sharpen = new float[] {
			     0.0f, -1.0f, 0.0f,
			    -1.0f, 5.0f, -1.0f,
			     0.0f, -1.0f, 0.0f
				};
		return convolve(sharpen, 3, 3);
	}
	
	/**
	 * @return A blurred version of this sprite
	 */
	public Sprite blurred()
	{
		float[] blur = new float[] {
				0.05f, 0.15f, 0.05f,
				0.15f, 0.2f, 0.15f, 
				0.05f, 0.15f, 0.05f
				};
		
		return convolve(blur, 3, 3);
	}
	
	/**
	 * Creates a sprite based on this one, but having different luminosity
	 * @param scale how much the luminosity of the sprite is scaled. With 0.8 the 
	 * luminosity would be decreased by 20% wile 0 will make the image pitch black. Scaling 
	 * with 2 will make the image 200% as luminous.
	 * @return A version of this sprite that has different luminosity
	 */
	public Sprite withLuminosity(float scale)
	{
		float[] scales = {scale, scale, scale, 1.0f};
		float[] offsets = {0, 0, 0, 0};
		RescaleOp op = new RescaleOp(scales, offsets, null);
	    return filteredWith(op);
	}
	
	private Sprite convolve(float[] data, int kernelWidth, int kernelHeight)
	{
		// Creates the operation
		ConvolveOp op = new ConvolveOp(new Kernel(kernelWidth, kernelHeight, data), 
				ConvolveOp.EDGE_NO_OP, null);
		
		// Creates the sharpened sprite and returns it
		return filteredWith(op);
	}
	
	private Vector3D getImageDimensions()
	{
		return new Vector3D(getSubImage(0).getWidth(), getSubImage(0).getHeight());
	}
	
	private Sprite filteredWith(BufferedImageOp op)
	{
		Sprite newSprite = new Sprite(this);
		
		for (int i = 0; i < this.images.length; i++)
		{
			newSprite.images[i] = op.filter(this.images[i], null);
		}
		
		return newSprite;
	}
	
	// TODO: If you get bored, try to implement filters into the project
	// check: http://docs.oracle.com/javase/tutorial/2d/images/drawimage.html
	
	/* ConvolveOP (http://www.informit.com/articles/article.aspx?p=1013851&seqNum=5)
	*/
	/*
	 * protected LookupOp createColorizeOp(short R1, short G1, short B1) {
    short[] alpha = new short[256];
    short[] red = new short[256];
    short[] green = new short[256];
    short[] blue = new short[256];

    int Y = 0.3*R + 0.59*G + 0.11*B

    for (short i = 0; i < 256; i++) {
        alpha[i] = i;
        red[i] = (R1 + i*.3)/2;
        green[i] = (G1 + i*.59)/2;
        blue[i] = (B1 + i*.11)/2;
    }

    short[][] data = new short[][] {
            red, green, blue, alpha
    };

    LookupTable lookupTable = new ShortLookupTable(0, data);
    return new LookupOp(lookupTable, null);
}
	 */
}
