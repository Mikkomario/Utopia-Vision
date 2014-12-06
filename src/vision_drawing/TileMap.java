package vision_drawing;

import java.util.HashMap;
import java.util.Map;

import vision_sprite.SingleSpriteDrawer;
import vision_sprite.SpriteBank;
import exodus_object.ConstructableGameObject;
import exodus_object.SimpleGameObject;
import exodus_util.Transformable;
import exodus_util.Transformation;
import flow_recording.Constructable;
import flow_recording.Writable;
import genesis_event.HandlerRelay;
import genesis_util.Vector2D;

/**
 * Tilemaps are constructed from multiple different tiles. They can be used as more varying 
 * backgrounds, for example.
 * 
 * @author Mikko Hilpinen
 * @since 6.12.2014
 */
public class TileMap extends SimpleGameObject implements Transformable, Writable
{
	// ATTRIBUTES	-----------------------
	
	private String spriteBankName;
	private DependentSpriteDrawer<?, ?>[][] tiles;
	private Vector2D tileSize;
	private Transformation transformation;
	private HandlerRelay handlers;
	private int depth;
	
	private String[][] spriteNames;
	
	
	// CONSTRUCTOR	-----------------------
	
	/**
	 * Creates a new TileMap
	 * 
	 * @param spriteBankName The name of the SpriteBank that contains the sprites used in this 
	 * map
	 * @param depth The drawing depth of the map
	 * @param tileAmounts How many tiles there are horizontally and vertically
	 * @param tileSize How large each tile should be
	 * @param handlers The handlers that will handle this map
	 */
	public TileMap(String spriteBankName, int depth, Vector2D tileAmounts, Vector2D tileSize, 
			HandlerRelay handlers)
	{
		super(handlers);
		
		// Initializes attributes
		this.spriteBankName = spriteBankName;
		this.depth = depth;
		this.tiles = new DependentSpriteDrawer<?, ?>
				[tileAmounts.getFirstInt()][tileAmounts.getSecondInt()];
		this.spriteNames = new String[tileAmounts.getFirstInt()][tileAmounts.getSecondInt()];
		this.tileSize = tileSize;
		this.handlers = handlers;
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------

	@Override
	public Transformation getTransformation()
	{
		return this.transformation;
	}

	@Override
	public void setTrasformation(Transformation t)
	{
		this.transformation = t;
	}
	
	@Override
	public Map<String, String> getAttributes()
	{
		Map<String, String> attributes = new HashMap<>();
		
		// Adds the dimensions
		attributes.put("tileAmounts", new Vector2D(this.tiles.length, this.tiles[0].length).toString());
		
		// Adds the basic attributes
		attributes.put("spriteBankName", this.spriteBankName);
		attributes.put("tileSize", this.tileSize.toString());
		attributes.put("depth", this.depth + "");
		
		// Adds the transformation attributes
		attributes.putAll(getTransformation().getAttributes());
		
		// Adds the tiles
		for (int x = 0; x < this.spriteNames.length; x++)
		{
			for (int y = 0; y < this.spriteNames[0].length; y++)
			{
				String spriteName = this.spriteNames[x][y];
				if (spriteName != null)
					attributes.put("tile" + x + "," + y + "ANIM" + 
							this.tiles[x][y].getSpriteDrawer().getImageSpeed(), spriteName);
			}
		}
		
		return attributes;
	}

	@Override
	public Map<String, Writable> getLinks()
	{
		// Doesn't use links
		return new HashMap<String, Writable>();
	}
	
	
	// OTHER METHODS	--------------------------
	
	/**
	 * Sets a tile to the given position in the map. The previous tile will be removed.
	 * @param position The position where the tile will be put (x, y) in tiles, starting 
	 * from (0, 0).
	 * @param tileSpriteName The name of the sprite used for drawing the tile
	 * @param animationSpeed How fast the animation in the tile is (default at 0.1)
	 */
	public void setTile(Vector2D position, String tileSpriteName, double animationSpeed)
	{
		// If there already is a tile at the given position, removes it
		removeTile(position);
		
		// Adds a new tile to the given position
		DependentSpriteDrawer<TileMap, SingleSpriteDrawer> newTile = 
				new DependentSpriteDrawer<>(this, this.depth, 
				new SingleSpriteDrawer(SpriteBank.getSprite(
				this.spriteBankName, tileSpriteName), this, this.handlers), this.handlers);
		
		this.tiles[position.getFirstInt()][position.getSecondInt()] = newTile;
		this.spriteNames[position.getFirstInt()][position.getSecondInt()] = tileSpriteName;
		newTile.scaleToSize(this.tileSize);
		newTile.getSpriteDrawer().setImageSpeed(animationSpeed);
	}
	
	/**
	 * Removes a tile from the given position
	 * @param position The position where the tile is removed from (x, y) in tiles starting 
	 * from (0, 0)
	 */
	public void removeTile(Vector2D position)
	{
		DependentSpriteDrawer<?, ?> tile = 
				this.tiles[position.getFirstInt()][position.getSecondInt()];
		
		if (tile != null)
		{
			tile.separate();
			this.spriteNames[position.getFirstInt()][position.getSecondInt()] = null;
		}
	}
	
	/**
	 * TileMapConstructions are used for loading tileMaps from text or xml data. Once 
	 * finished, they can be turned into TileMaps.
	 * 
	 * @author Mikko Hilpinen
	 * @since 6.12.2014
	 */
	public static class TileMapConstruction implements Constructable<ConstructableGameObject>
	{
		// ATTRIBUTES	---------------------
		
		private String id, spriteBankName;
		private Vector2D tileSize, tileAmounts;
		private Transformation transformation;
		private int depth;
		private Map<String, String> tileNames;
		
		private HandlerRelay handlers;
		
		
		// CONSTRUCTOR	----------------------
		
		/**
		 * Creates a new TileMapConstruction ready to be constructed
		 * @param handlers The handlers that will handle the tileMap created from this 
		 * construction
		 */
		public TileMapConstruction(HandlerRelay handlers)
		{
			this.handlers = handlers;
			this.tileNames = new HashMap<>();
			this.transformation = new Transformation();
		}
		
		
		// IMPLEMENTED METHODS	----------------
		
		@Override
		public String getID()
		{
			return this.id;
		}

		@Override
		public void setAttribute(String attributeName, String attributeValue)
		{
			// Checks if is any of the basic attributes
			switch (attributeName)
			{
				case "spriteBankName": this.spriteBankName = attributeValue; return;
				case "tileSize": this.tileSize = Vector2D.parseFromString(attributeValue); return;
				case "tileAmounts": this.tileAmounts = Vector2D.parseFromString(attributeValue); return;
				case "depth": this.depth = Integer.parseInt(attributeValue);
			}
			// Otherwise may be a new tile
			if (attributeName.startsWith("tile"))
				this.tileNames.put(attributeName, attributeValue);
			// Otherwise is a transformation update
			else
				this.transformation = this.transformation.withAttribute(attributeName, 
						attributeValue);
		}

		@Override
		public void setID(String id)
		{
			this.id = id;
		}

		@Override
		public void setLink(String linkName, ConstructableGameObject target)
		{
			// Doesn't use links
		}
		
		
		// OTHER METHODS	----------------------
		
		/**
		 * @return A tileMap based on this construction
		 */
		public TileMap toTileMap()
		{
			TileMap map = new TileMap(this.spriteBankName, this.depth, this.tileAmounts, 
					this.tileSize, this.handlers);
			
			for (String tile : this.tileNames.keySet())
			{
				String spriteName = this.tileNames.get(tile);
				String[] arguments = tile.split("ANIM");
				Vector2D position = Vector2D.parseFromString(arguments[0].substring(4));
				map.setTile(position, spriteName, Double.parseDouble(arguments[1]));
			}
			
			return map;
		}
	}
}
