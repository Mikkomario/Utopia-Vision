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
	
	private static final float[] SHARPEN = new float[]
	{
	     0.0f, -1.0f, 0.0f,
	    -1.0f, 5.0f, -1.0f,
	     0.0f, -1.0f, 0.0f
	};
	private static final float[] BLUR = new float[]
	{
		0.05f, 0.15f, 0.05f,
		0.15f, 0.2f, 0.15f, 
		0.05f, 0.15f, 0.05f
	};
	
	
	private BufferedImage[] images, originalImages;
	private Vector3D origin, originalSize;
	private File sourceFile;
	
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
		loadSprite(file, stripLength, origin);
	}
	
	/**
	 * Creates a modified sprite
	 * @param file The image file for the strip
	 * @param stripLength How many separate images does the strip contain?
	 * @param origin The sprite's origin's coordinates (relative). Use null for centered origin.
	 * @param size The sprite's in-game size. Null for original image size.
	 * @param sharpness How much the sprite is sharpened. Use a negative value for blurring.
	 * @param luminosity How luminous the sprite is. Default at 1.
	 * @throws IOException If image reading failed
	 */
	public Sprite(File file, int stripLength, Vector3D origin, Vector3D size, int sharpness, 
			float luminosity) throws IOException
	{
		loadSprite(file, stripLength, origin);
		
		if (size != null)
			this.scaling = size.dividedBy(this.originalSize);
		this.sharpness = sharpness;
		this.luminosity = luminosity;
		
		modifyImages();
	}
	
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
	
	private void loadSprite(File file, int stripLength, Vector3D origin) throws IOException
	{
		if (file == null || !file.exists())
			throw new FileNotFoundException("Image file " + file + " doesn't exist");
		if (stripLength < 1)
			stripLength = 1;
		
		this.sourceFile = file;
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
	 * @return How much the sprite should be scaled when drawn
	 */
	public Vector3D getScaling()
	{
		return this.scaling;
	}
	
	/**
	 * @return The size of the sprite (includes scaling)
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
	
	/**
	 * @return The image file of this sprite's source image
	 */
	public File getSourceFile()
	{
		return this.sourceFile;
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
	 * Creates a new sprite with different size
	 * @param size The size of the new sprite
	 * @return A sprite with the given size
	 */
	public Sprite withSize(Vector3D size)
	{
		return withScaling(size.dividedBy(this.originalSize));
	}
	
	/**
	 * Creates a scaled version of this sprite
	 * @param scaling The scaling applied to the sprite
	 * @return A sprite with different scaling
	 */
	public Sprite withScaling(Vector3D scaling)
	{
		Sprite sprite = new Sprite(this);
		sprite.scaling = scaling;
		return sprite;
	}
	
	/**
	 * Creates a scaled version of this sprite
	 * @param scaling How much the sprite is scaled
	 * @return A scaled sprite
	 */
	public Sprite scaled(Vector3D scaling)
	{
		return withScaling(getScaling().times(scaling));
	}
	
	/**
	 * @param amount How many sharpening iterations are performed
	 * @return A sharpened version of this sprite
	 */
	public Sprite sharpened(int amount)
	{
		if (amount == 0)
			return this;
		else
		{
			Sprite sprite = new Sprite(this);
			sprite.sharpness += amount;
			sprite.modifyImages();
			
			return sprite;
		}
	}
	
	/**
	 * @param amount How many blurring iterations are performed
	 * @return A blurred version of this sprite
	 */
	public Sprite blurred(int amount)
	{
		return sharpened(-amount);
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
		if (this.luminosity == scale)
			return this;
		else
		{
			Sprite sprite = new Sprite(this);
			sprite.luminosity = scale;
			sprite.modifyImages();
			
			return sprite;
		}
	}
	
	private void modifyImages()
	{
		for (int i = 0; i < getLength(); i++)
		{
			BufferedImage image = this.originalImages[i];
			
			// Sets sharpness
			for (int iteration = 0; iteration < Math.abs(getSharpness()); iteration++)
			{
				if (getSharpness() < 0)
					image = blur(image);
				else
					image = sharpen(image);
			}
			
			// Sets luminosity
			if (this.luminosity != 1)
				image = setLuminosity(image, this.luminosity);
			
			this.images[i] = image;
		}
	}
	
	private static BufferedImage sharpen(BufferedImage image)
	{
		return convolve(image, SHARPEN, 3, 3);
	}
	
	private static BufferedImage blur(BufferedImage image)
	{
		return convolve(image, BLUR, 3, 3);
	}
	
	private static BufferedImage setLuminosity(BufferedImage image, float scale)
	{
		float[] scales = {scale};//{scale, scale, scale, 1.0f};
		float[] offsets = {0};//{0, 0, 0, 0};
	    return filter(image, new RescaleOp(scales, offsets, null));
	}
	
	private static BufferedImage convolve(BufferedImage image, float[] data, int kernelWidth, int kernelHeight)
	{
		// Creates the operation
		ConvolveOp op = new ConvolveOp(new Kernel(kernelWidth, kernelHeight, data), 
				ConvolveOp.EDGE_NO_OP, null);
		
		// Creates the sharpened sprite and returns it
		return filter(image, op);
	}
	
	private static BufferedImage filter(BufferedImage image, BufferedImageOp op)
	{
		return op.filter(image, null);
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
