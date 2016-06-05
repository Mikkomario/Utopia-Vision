package utopia.vision.generics;

import utopia.flow.generics.DataType;
import utopia.flow.generics.Value;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.Tile;
import utopia.vision.resource.TileMap;

/**
 * These are the data types introduced in the vision project
 * @author Mikko Hilpinen
 * @since 5.6.2016
 */
public enum VisionDataType implements DataType
{
	/**
	 * A sprite represents an animated image
	 * @see Sprite
	 */
	SPRITE,
	/**
	 * A tile is a construct used for drawing a piece of surface
	 * @see Tile
	 */
	TILE,
	/**
	 * Tile maps contain multiple tiles
	 * @see TileMap
	 */
	TILEMAP;
	
	
	// IMPLEMENTED METHODS	----------

	@Override
	public String getName()
	{
		return toString();
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Wraps a sprite into a value
	 * @param sprite a sprite
	 * @return a value containing the sprite
	 */
	public static Value Sprite(Sprite sprite)
	{
		return new Value(sprite, SPRITE);
	}
	
	/**
	 * Casts a value to a sprite
	 * @param value a value
	 * @return The sprite value
	 */
	public static Sprite valueToSprite(Value value)
	{
		return (Sprite) value.parseTo(SPRITE);
	}
	
	/**
	 * Wraps a tile into a value
	 * @param tile a tile
	 * @return A value containing the tile
	 */
	public static Value Tile(Tile tile)
	{
		return new Value(tile, TILE);
	}
	
	/**
	 * Casts a value to a tile
	 * @param value a value
	 * @return The tile value
	 */
	public static Tile valueToTile(Value value)
	{
		return (Tile) value.parseTo(TILE);
	}
	
	/**
	 * Wraps a tile map into a value
	 * @param map a tile map
	 * @return a value containing the map
	 */
	public static Value TileMap(TileMap map)
	{
		return new Value(map, TILEMAP);
	}
	
	/**
	 * Casts a value to a tile map
	 * @param value a value
	 * @return The tile map value
	 */
	public static TileMap valueToTileMap(Value value)
	{
		return (TileMap) value.parseTo(TILEMAP);
	}
}
