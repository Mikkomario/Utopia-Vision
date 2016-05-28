package utopia.vision.resource;

import java.awt.image.BufferedImage;

import utopia.genesis.util.Vector3D;

/**
 * A tile is a struct containing information required when drawing a tile in a tile map
 * @author Mikko Hilpinen
 * @since 24.5.2016
 */
public class Tile
{
	// ATTRIBUTES	--------------
	
	private Sprite sprite;
	private int startFrameIndex = 0;
	private double animationSpeed = 0.1;
	
	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new tile
	 * @param sprite The sprite used in this tile
	 * @param size The size of the tile
	 */
	public Tile(Sprite sprite, Vector3D size)
	{
		this.sprite = sprite.withSize(size);
	}

	/**
	 * Creates a new tile
	 * @param sprite The sprite used in this tile
	 * @param size The size of the tile
	 * @param startFrameIndex The frame that is first displayed (default 0)
	 * @param animationSpeed The animation speed used in the tile (default 0.1)
	 */
	public Tile(Sprite sprite, Vector3D size, int startFrameIndex, double animationSpeed)
	{
		this.sprite = sprite.withSize(size);
		this.startFrameIndex = startFrameIndex;
		this.animationSpeed = animationSpeed;
	}
	
	/**
	 * Copies and rescales a tile
	 * @param other another tile
	 * @param size The size of this new tile
	 */
	public Tile(Tile other, Vector3D size)
	{
		this.sprite = other.sprite.withSize(size);
		this.startFrameIndex = other.startFrameIndex;
		this.animationSpeed = other.animationSpeed;
	}
	
	
	// ACCESSORS	-------------
	
	/**
	 * @return The sprite used in this tile
	 */
	public Sprite getSprite()
	{
		return this.sprite;
	}
	
	/**
	 * @return The index of the frame displayed at 0th step
	 */
	public int getStartFrameIndex()
	{
		return this.startFrameIndex;
	}
	
	/**
	 * @return The animation speed used in this tile
	 */
	public double getAnimationSpeed()
	{
		return this.animationSpeed;
	}
	
	/**
	 * @return The size of this tile
	 */
	public Vector3D getSize()
	{
		return getSprite().getSize();
	}
	
	
	// OTHER METHODS	---------
	
	/**
	 * Finds the frame that should be displayed of this tile at a given time
	 * @param t A time frame (in steps)
	 * @return The frame that should be displayed
	 */
	public BufferedImage getFrame(double t)
	{
		return getSprite().getFrame(getStartFrameIndex() + (int) (t * getAnimationSpeed()));
	}
}
