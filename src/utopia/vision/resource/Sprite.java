package utopia.vision.resource;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
	
	/**
	 * The default animation speed used. In frames per second.
	 */
	public static final double DEFAULT_ANIMATION_SPEED_PER_SECOND = 15;
	
	private BufferedImage[] images;
	private Vector3D origin, originalSize;
	private File sourceFile;
	
	private Vector3D scaling = Vector3D.IDENTITY;
	private double animationSpeed = DEFAULT_ANIMATION_SPEED_PER_SECOND;
	
	
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
	 * @param defaultAnimationSpeed The default animation speed used with the sprite (frames per 
	 * second. Default 15)
	 * @throws IOException If image reading failed
	 */
	public Sprite(File file, int stripLength, Vector3D origin, Vector3D size, 
			double defaultAnimationSpeed) throws IOException
	{
		loadSprite(file, stripLength, origin);
		
		if (size != null)
			this.scaling = size.dividedBy(this.originalSize);
		this.animationSpeed = defaultAnimationSpeed;
	}
	
	// Copies another sprite
	private Sprite(Sprite other)
	{
		this.images = other.images;
		this.origin = other.origin;
		this.originalSize = other.originalSize;
		this.scaling = other.scaling;
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
	 * @return The image file of this sprite's source image
	 */
	public File getSourceFile()
	{
		return this.sourceFile;
	}
	
	/**
	 * @return The speed at which this sprite should be animated by default, in frames per 
	 * second. Default is 15.
	 */
	public double getDefaultAnimationSpeed()
	{
		return this.animationSpeed;
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
		if (scaling.equalsIn2D(getScaling()))
			return this;
		
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
	 * @return A version of this sprite where the order of the frames has been reversed
	 */
	public Sprite reverse()
	{
		Sprite s = new Sprite(this);
		
		for(int i = 0; i < this.images.length; i++)
		{
			s.images[i] = this.images[this.images.length - 1 - i];
		}
		
		return s;
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
		if (origin == null)
			origin = sprite.getOrigin();
		
		drawImage(sprite.getFrame(frameIndex), origin, sprite.getScaling(), g2d);
	}
	
	/**
	 * Draws an image
	 * @param image the image that is drawn
	 * @param origin The origin of the image
	 * @param scaling The scaling applied to the image
	 * @param g2d The graphics object used for drawing the image
	 */
	public static void drawImage(BufferedImage image, Vector3D origin, Vector3D scaling, 
			Graphics2D g2d)
	{
		AffineTransform lastTransform = g2d.getTransform();
		
		// Moves the sprite according to its origin
		g2d.translate(-origin.getX(), -origin.getY());
		
		// Scales the sprite according to it's status
		g2d.scale(scaling.getX(), scaling.getY());
		
		// Draws the image
		g2d.drawImage(image, 0, 0, null);
		
		g2d.setTransform(lastTransform);
	}
}
