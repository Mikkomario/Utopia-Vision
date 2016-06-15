package utopia.vision.resource;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import utopia.arc.resource.BankBank;
import utopia.flow.structure.Pair;
import utopia.genesis.util.Vector3D;

/**
 * This class is able to handle the drawing of a tile map. It also offers an interface for 
 * animation.
 * @author Mikko Hilpinen
 * @since 15.6.2016
 */
public class TileMapDrawer
{
	// ATTRIBUTES	----------------
	
	private List<SpriteDrawer> drawers = new ArrayList<>();
	private Vector3D forcedOrigin = null;
	private TileMap map;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new tile map drawer
	 * @param map The map that is drawn
	 * @param spriteResources The sprite resources used for finding the correct sprites for 
	 * each tile
	 */
	public TileMapDrawer(TileMap map, BankBank<Sprite> spriteResources)
	{
		this.map = map;
		createDrawers(spriteResources);
	}

	/**
	 * Creates a new tile map drawer
	 * @param map The map that is drawn
	 * @param spriteResources The sprite resources used for finding the correct sprites for 
	 * each tile
	 * @param forcedOrigin The origin the drawer will use when it draws the map. Use null for 
	 * the map's default origin
	 */
	public TileMapDrawer(TileMap map, BankBank<Sprite> spriteResources, Vector3D forcedOrigin)
	{
		this.map = map;
		this.forcedOrigin = forcedOrigin;
		createDrawers(spriteResources);
	}
	
	
	// ACCESSORS	-----------------
	
	/**
	 * @return The tile map used by this drawer
	 */
	public TileMap getTileMap()
	{
		return this.map;
	}
	
	/**
	 * Changes the tile map used by this drawer
	 * @param map The new map to be used
	 * @param spriteResources The sprite resources used for finding correct tile sprites
	 */
	public void setTileMap(TileMap map, BankBank<Sprite> spriteResources)
	{
		this.map = map;
		createDrawers(spriteResources);
	}
	
	/**
	 * @return The origin used when the drawer draws a tile map
	 */
	public Vector3D getOrigin()
	{
		if (this.forcedOrigin == null)
		{
			if (this.map == null)
				return Vector3D.ZERO;
			else
				return this.map.getOrigin();
		}
		else
			return this.forcedOrigin;
	}
	
	/**
	 * Changes the origin used when drawing the map
	 * @param origin The new origin that is used. Use null for the map's default origin.
	 */
	public void setOrigin(Vector3D origin)
	{
		Vector3D previousOrigin = getOrigin();
		this.forcedOrigin = origin;
		
		Vector3D translation = getOrigin().minus(previousOrigin);
		// Translates each drawer
		for (SpriteDrawer drawer : this.drawers)
		{
			drawer.setOrigin(drawer.getOrigin().plus(translation));
		}
	}
	
	
	// OTHER METHODS	-------------
	
	/**
	 * Draws the tile map
	 * @param g2d The graphics object used for doing the actual drawing
	 */
	public void drawMap(Graphics2D g2d)
	{
		for (SpriteDrawer drawer : this.drawers)
		{
			drawer.drawSprite(g2d);
		}
	}
	
	/**
	 * Animates the tiles in this map
	 * @param duration The duration of the passed animation in steps
	 */
	public void animate(double duration)
	{
		for (SpriteDrawer drawer : this.drawers)
		{
			drawer.animate(duration);
		}
	}
	
	private void createDrawers(BankBank<Sprite> spriteBanks)
	{
		// Creates a sprite drawer for each tile
		this.drawers.clear();
		if (this.map != null)
		{
			for (Pair<Vector3D, Tile> tile : this.map.getTiles())
			{
				SpriteDrawer drawer = new SpriteDrawer(spriteBanks.get(
						tile.getSecond().getSpriteBankName(), tile.getSecond().getSpriteName()), 
						getOrigin().minus(tile.getFirst()));
				
				drawer.setFrameIndex(tile.getSecond().getStartFrameIndex());
				if (!tile.getSecond().isAnimated())
					drawer.setAnimationSpeed(0);
			}
		}
	}
}
