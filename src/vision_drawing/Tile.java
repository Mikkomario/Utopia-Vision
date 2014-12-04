package vision_drawing;

import omega_world.AreaOldVersion;
import vision_sprite.Sprite;

/**
 * Tiles are backgrounds that have certain proportions.
 *
 * @author Mikko Hilpinen.
 * @since 6.7.2013.
 * @see Background
 */
public class Tile extends Background
{
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new tile with the given information
	 *
	 * @param x The x-coordinate of the tile's center
	 * @param y The y-coordinate of the tile's center
	 * @param texture The sprite which is used for drawing the tile
	 * @param width The width of the tile
	 * @param height The height of the tile
	 * @param area The area where the object will reside
	 */
	public Tile(int x, int y, Sprite texture, 
			int width, int height, AreaOldVersion area)
	{
		super(x, y, texture, area);
		// Sets the size of the tile
		setDimensions(width, height);
		// Tiles are drawn a bit above the backgrounds
		setDepth(getDepth() - 5);
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------
	
	@Override
	public int getOriginX()
	{
		// Unlike with background, tiles' origins are always in the middle
		if (getSpriteDrawer() != null)
			return getSpriteDrawer().getSprite().getWidth() / 2;
		else
			return 0;
	}

	@Override
	public int getOriginY()
	{
		if (getSpriteDrawer() != null)
			return getSpriteDrawer().getSprite().getHeight() / 2;
		else
			return 0;
	}
}
