package utopia.vision.resource;

import utopia.genesis.util.Vector3D;

/**
 * A tile is a struct containing information required when drawing a tile in a tile map
 * @author Mikko Hilpinen
 * @since 24.5.2016
 */
public class Tile
{
	// ATTRIBUTES	--------------
	
	private String bankName, spriteName;
	private Vector3D size;
	private int startFrameIndex = 0;
	private double animationSpeed = 0.1;
	
	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new tile
	 * @param spriteBankName The name of the bank that contains the sprite used in this tile
	 * @param spriteName The name of the sprite used in this tile
	 * @param size The size of the tile
	 */
	public Tile(String spriteBankName, String spriteName, Vector3D size)
	{
		this.bankName = spriteBankName;
		this.spriteName = spriteName;
		this.size = size;
	}

	/**
	 * Creates a new tile
	 * @param spriteBankName The name of the bank that contains the sprite used in this tile
	 * @param spriteName The name of the sprite used in this tile
	 * @param size The size of the tile
	 * @param startFrameIndex The frame that is first displayed (default 0)
	 * @param animationSpeed The animation speed used in the tile (default 0.1)
	 */
	public Tile(String spriteBankName, String spriteName, Vector3D size, int startFrameIndex, 
			double animationSpeed)
	{
		this.bankName = spriteBankName;
		this.spriteName = spriteName;
		this.size = size;
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
		this.bankName = other.bankName;
		this.spriteName = other.spriteName;
		this.size = size;
		this.startFrameIndex = other.startFrameIndex;
		this.animationSpeed = other.animationSpeed;
	}
	
	
	// ACCESSORS	-------------
	
	/**
	 * @return The name of the bank that contains the sprite used in this tile
	 */
	public String getSpriteBankName()
	{
		return this.bankName;
	}
	
	/**
	 * @return The name of the sprite used in this tile
	 */
	public String getSpriteName()
	{
		return this.spriteName;
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
		return this.size;
	}
}
