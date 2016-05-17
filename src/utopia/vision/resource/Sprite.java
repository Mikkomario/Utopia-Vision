package utopia.vision.resource;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import utopia.genesis.util.Vector3D;


/**
 * A sprite represents a drawn image that can be animated. The sprite class does not handle 
 * animation or other mutable effects, but is an immutable struct.
 * @author Mikko Hilpinen.
 * @since 27.11.2012.
 */
public class Sprite
{	
	// ATTRIBUTES	-------------------------------------------------------
	
	private BufferedImage[] images, originalImages;
	private Vector3D origin, originalSize;
	
	private Vector3D scaling = Vector3D.IDENTITY;
	private int sharpness = 0; // 0 = neutral, > = sharp, < = blur
	private float luminosity = 1;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * Creates a new sprite from an image file containing one or more images
	 * @param file The image file for the strip
	 * @param stripLength How many separate images does the strip contain?
	 * @param origin The sprite's origin's coordinates (relative). Use null for centered origin.
	 * @throws IOException If image reading failed
	 */
	public Sprite(File file, int stripLength, Vector3D origin) throws IOException
	{
		// Checks the variables
		if (file == null || !file.exists())
			throw new FileNotFoundException("Image file " + file + " doesn't exist");
		if (stripLength < 1)
			stripLength = 1;
		
		this.origin = origin;
		
		// Loads the image
		BufferedImage strip = ImageIO.read(file);
		
		// Creates the subimages
		this.images = new BufferedImage[stripLength];
		this.originalSize = new Vector3D(strip.getWidth() / stripLength, strip.getHeight());
		
		for (int i = 0; i < stripLength; i++)
		{	
			this.images[i] = strip.getSubimage(i * this.originalSize.getXInt(), 0, 
					this.originalSize.getXInt(), this.originalSize.getYInt());
		}
		
		this.originalImages = this.images.clone();
		
		// If an origin position was set to null, sets it to the middle of the 
		// sprite
		if (this.origin == null)
			this.origin = this.originalSize.dividedBy(2);
	}
	
	// TODO: Add constructor with filtering / etc.
	
	// Copies another sprite
	private Sprite(Sprite other)
	{
		this.images = other.images.clone();
		this.origin = other.origin;
		this.originalSize = other.originalSize;
		this.originalImages = other.originalImages;
		this.scaling = other.scaling;
		this.sharpness = other.sharpness;
		this.luminosity = other.luminosity;
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
	 * @return How much the sprite is scaled from the original version
	 */
	public Vector3D getScaling()
	{
		return this.scaling;
	}
	
	/**
	 * @return The size of the sprite
	 */
	public Vector3D getSize()
	{
		return this.originalSize.times(getScaling());
	}
	
	/**
	 * @return returns how many frames exist within this sprite
	 */
	public int getLength()
	{
		return this.images.length;
	}
	
	/**
	 * @return How sharp the image is. 0 Is neutral (source image), 1 or more is sharpened and 
	 * -1 or less is blurred.
	 */
	public int getSharpness()
	{
		return this.sharpness;
	}
	
	/**
	 * @return How luminous the sprite is. 1 Is neutral (source image). 0 is pitch black. 
	 * >1 is increased luminosity.
	 */
	public float getLuminosity()
	{
		return this.luminosity;
	}
	
	
	// OTHER METHODS	------------------------------------------------------------
	
	/**
	 * This method returns a single frame from the sprite.
	 * @param imageIndex The index of the image [0, length[. Indices will be looped and no 
	 * {@link IndexOutOfBoundsException} will be thrown.
	 * @return The frame from the given index
	 * @see #getLength()
	 */
	public BufferedImage getFrame(int imageIndex)
	{
		// Loops the index to correct range
		while (imageIndex < 0)
		{
			imageIndex += getLength();
		}
		
		return this.images[imageIndex % getLength()];
	}
	
	/**
	 * Copies a sprite that has the given dimensions
	 * 
	 * @param newDimensions The new size of the sprite. Use null if you want to use the 
	 * sprite's original size.
	 * @return A sprite with the given dimensions
	 */
	public Sprite withDimensions(Vector3D newDimensions) // TODO: Rename. Create another method for original size
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
		// TODO: Length of 1 should suffice here
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
	
	// TODO: Make this static, return bufferedimage
	private Sprite filteredWith(BufferedImageOp op)
	{
		Sprite newSprite = new Sprite(this);
		
		for (int i = 0; i < this.images.length; i++)
		{
			newSprite.images[i] = op.filter(this.images[i], null);
		}
		
		return newSprite;
	}
	
	private void modifyImages()
	{
		for (int i = 0; i < getLength(); i++)
		{
			BufferedImage image = this.originalImages[i];
			
			for (int iteration = 0; iteration < Math.abs(getSharpness()); iteration++)
			{
				// TODO: Process images
				//if (getSharpness() < 0)
				//	image = 
			}
		}
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
