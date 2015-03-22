package vision_drawing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omega_util.SimpleGameObject;
import omega_util.Transformable;
import omega_util.Transformation;
import vision_sprite.SpriteBank;
import exodus_util.ConstructableGameObject;
import flow_recording.Constructable;
import flow_recording.Writable;
import genesis_event.HandlerRelay;
import genesis_util.Vector3D;

/**
 * Tilemaps are constructed from multiple different tiles. They can be used as more varying 
 * backgrounds, for example.
 * 
 * @author Mikko Hilpinen
 * @since 6.12.2014
 */
public class TileMap extends SimpleGameObject implements Transformable, Writable, 
		ConstructableGameObject
{
	// ATTRIBUTES	-----------------------
	
	private String spriteBankName;
	private Tile[][] tiles;
	private Vector3D tileSize;
	private Transformation transformation;
	private HandlerRelay handlers;
	private int depth;
	private TileMapConstruction construction;
	
	
	// CONSTRUCTOR	-----------------------
	
	/**
	 * Creates a new TileMap.
	 * 
	 * @param spriteBankName The name of the SpriteBank that contains the sprites used in this 
	 * map
	 * @param depth The drawing depth of the map
	 * @param tileAmounts How many tiles there are horizontally and vertically
	 * @param tileSize How large each tile should be
	 * @param handlers The handlers that will handle this map
	 */
	public TileMap(String spriteBankName, int depth, Vector3D tileAmounts, Vector3D tileSize, 
			HandlerRelay handlers)
	{
		super(handlers);
		
		// Initializes attributes
		this.spriteBankName = spriteBankName;
		this.depth = depth;
		this.tiles = new Tile[tileAmounts.getFirstInt()][tileAmounts.getSecondInt()];
		this.tileSize = tileSize;
		this.handlers = handlers;
		this.transformation = new Transformation();
	}
	
	/**
	 * Creates a new TileMap that may be constructed in parts
	 * @param handlers The handlers that will handle this map
	 */
	public TileMap(HandlerRelay handlers)
	{
		super(handlers);
		
		// Initializes attributes
		this.handlers = handlers;
		this.construction = new TileMapConstruction();
		this.transformation = new Transformation();
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
		attributes.put("tileAmounts", new Vector3D(this.tiles.length, 
				this.tiles[0].length).toString());
		
		// Adds the basic attributes
		attributes.put("spriteBankName", this.spriteBankName);
		attributes.put("tileSize", this.tileSize.toString());
		attributes.put("depth", this.depth + "");
		
		// Adds the transformation attributes
		attributes.putAll(getTransformation().getAttributes());
		
		// Adds the tiles
		for (int x = 0; x < this.tiles.length; x++)
		{
			for (int y = 0; y < this.tiles[0].length; y++)
			{
				Tile tile = this.tiles[x][y];
				if (tile != null)
					attributes.put("tile" + x + "," + y, tile.toString());
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
	
	@Override
	public String getID()
	{
		if (this.construction != null)
			return this.construction.getID();
		else
			return null;
	}

	@Override
	public void setAttribute(String attributeName, String attributeValue)
	{
		if (this.construction != null)
			this.construction.setAttribute(attributeName, attributeValue);
	}

	@Override
	public void setID(String id)
	{
		if (this.construction != null)
			this.construction.setID(id);
	}

	@Override
	public void setLink(String linkName, ConstructableGameObject target)
	{
		if (this.construction != null)
			this.construction.setLink(linkName, target);
	}
	
	
	// OTHER METHODS	--------------------------
	
	/**
	 * Sets a tile to the given position in the map. The previous tile will be removed.
	 * @param position The position where the tile will be put (x, y) in tiles, starting 
	 * from (0, 0).
	 * @param tileSpriteName The name of the sprite used for drawing the tile
	 * @param imageIndex The image index the tile starts from (default 0)
	 * @param animationSpeed How fast the animation in the tile is (default at 0.1)
	 */
	public void setTile(Vector3D position, String tileSpriteName, int imageIndex, 
			double animationSpeed)
	{
		setTile(position, new Tile(this, this.depth, this.spriteBankName, tileSpriteName, 
				animationSpeed, imageIndex, this.tileSize, this.handlers));
	}
	
	private void setTile(Vector3D position, Tile tile)
	{
		// If there already is a tile at the given position, removes it
		removeTile(position);
		
		// Adds a new tile to the given position
		this.tiles[position.getFirstInt()][position.getSecondInt()] = tile;
		tile.setTrasformation(tile.getOwnTransformation().withPosition(position.times(this.tileSize)));
	}
	
	/**
	 * Removes a tile from the given position
	 * @param position The position where the tile is removed from (x, y) in tiles starting 
	 * from (0, 0)
	 */
	public void removeTile(Vector3D position)
	{
		Tile tile = this.tiles[position.getFirstInt()][position.getSecondInt()];
		
		if (tile != null)
			tile.separate();
	}
	
	/**
	 * TileMapConstructions are used for loading tileMaps from text or xml data. Once 
	 * finished, they can be turned into TileMaps.
	 * 
	 * @author Mikko Hilpinen
	 * @since 6.12.2014
	 */
	private class TileMapConstruction implements Constructable<Constructable<?>>
	{
		// ATTRIBUTES	---------------------
		
		private String id;
		private Map<String, String> tileData;
		private List<String> requiredFeatures;
		
		
		// CONSTRUCTOR	----------------------
		
		/**
		 * Creates a new TileMapConstruction ready to be constructed
		 */
		public TileMapConstruction()
		{
			this.tileData = new HashMap<>();
			this.requiredFeatures = new ArrayList<>();
			
			this.requiredFeatures.add("tileAmounts");
			this.requiredFeatures.add("tileSize");
			this.requiredFeatures.add("depth");
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
			boolean wasBasicAttribute = false;
			
			// Checks if is any of the basic attributes
			switch (attributeName)
			{
				case "spriteBankName": 
					TileMap.this.spriteBankName = attributeValue;
					wasBasicAttribute = true;
					break;
				case "tileSize":
					TileMap.this.tileSize = Vector3D.parseFromString(attributeValue);
					wasBasicAttribute = true;
					break;
				case "tileAmounts": 
					Vector3D tileAmounts = Vector3D.parseFromString(attributeValue);
					TileMap.this.tiles = 
							new Tile[tileAmounts.getFirstInt()][tileAmounts.getSecondInt()];
					wasBasicAttribute = true;
					break;
				case "depth":
					TileMap.this.depth = Integer.parseInt(attributeValue);
					wasBasicAttribute = true;
					break;
			}
			
			if (wasBasicAttribute)
				this.requiredFeatures.remove(attributeName);
			// Otherwise may be a new tile
			else if (attributeName.startsWith("tile"))
				this.tileData.put(attributeName, attributeValue);
			// Otherwise is a transformation update
			else
				setTrasformation(getTransformation().withAttribute(attributeName, 
						attributeValue));
			
			// Once all the basic attributes have been collected, starts the construction
			if (this.requiredFeatures.isEmpty())
			{
				for (String tilePosition : this.tileData.keySet())
				{
					String[] tileArguments = this.tileData.get(tilePosition).split(",");
					Vector3D position = Vector3D.parseFromString(tilePosition.substring(4));
					
					setTile(position, new Tile(TileMap.this, TileMap.this.spriteBankName, 
							TileMap.this.depth, TileMap.this.tileSize, tileArguments, 
							TileMap.this.handlers));
				}
				
				this.tileData.clear();
			}
		}

		@Override
		public void setID(String id)
		{
			this.id = id;
		}

		@Override
		public void setLink(String linkName, Constructable<?> target)
		{
			// Doesn't use links
		}
	}
	
	private static class Tile extends DependentSingleSpriteDrawer<TileMap>
	{
		// ATTRIBUTES	----------------------------
		
		private int startImageIndex;
		private String spriteName;
		
		
		// CONSTRUCTOR	----------------------------
		
		public Tile(TileMap user, int initialDepth, String spriteBankName, String spriteName, 
				double imageSpeed, int imageIndex, Vector3D tileSize, HandlerRelay handlers)
		{
			super(user, initialDepth, SpriteBank.getSprite(spriteBankName, spriteName), 
					handlers);
			
			this.startImageIndex = imageIndex;
			this.spriteName = spriteName;
			getSpriteDrawer().setImageIndex(this.startImageIndex);
			getSpriteDrawer().setImageSpeed(imageSpeed);
			getSpriteDrawer().setOrigin(Vector3D.zeroVector());
			scaleToSize(tileSize);
		}
		
		// Use split(",") to get the tileArguments
		public Tile(TileMap user, String spriteBankName, int depth, Vector3D tileSize, 
				String[] tileArguments, HandlerRelay handlers)
		{
			super(user, depth, SpriteBank.getSprite(spriteBankName, tileArguments[2]), 
					handlers);
			
			this.startImageIndex = Integer.parseInt(tileArguments[0]);
			getSpriteDrawer().setImageIndex(this.startImageIndex);
			this.spriteName = tileArguments[2];
			getSpriteDrawer().setImageSpeed(Double.parseDouble(tileArguments[1]));
			getSpriteDrawer().setOrigin(Vector3D.zeroVector());
			scaleToSize(tileSize);
		}
		
		
		// IMPLEMENTED METHODS	--------------------
		
		@Override
		public String toString()
		{
			/*
			 * attributes.put("tile" + x + "," + y + "ANIM" + 
							this.tiles[x][y].getSpriteDrawer().getImageSpeed(), spriteName);
			 */
			
			return this.startImageIndex + "," + getSpriteDrawer().getImageSpeed() + "," + 
					this.spriteName;
		}
	}
}
