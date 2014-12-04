package vision_drawing;

import genesis_graphic.DepthConstants;

import java.awt.Graphics2D;
import java.util.ArrayList;

import omega_world.AreaOldVersion;
import vision_sprite.SingleSpriteDrawer;
import vision_sprite.Sprite;

/**
 * Background is a simple surface that can be drawn under other objects
 *
 * @author Mikko Hilpinen.
 *         Created 1.7.2013.
 */
public class Background extends DrawnObject
{	
	// ATTRIBUTES	-------------------------------------------------------
	
	private SingleSpriteDrawer texturedrawer;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * Creates a new background to the given coordinates and adds it to the 
	 * given handlers.
	 *
	 * @param x The backround's top left x-coordinate
	 * @param y The backgound's top left y-coordinate
	 * @param area The area where the object will reside 
	 * (optional, for animated backgrounds)
	 * @param sprite The sprite that will be used to draw the background
	 */
	public Background(int x, int y, Sprite sprite, AreaOldVersion area)
	{
		super(x, y, DepthConstants.BOTTOM, area);

		// Initializes attributes
		this.texturedrawer = new SingleSpriteDrawer(sprite, 
				area.getActorHandler(), this);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------

	@Override
	public int getOriginX()
	{
		// Background's origin is usually in the left top corner
		return 0;
	}

	@Override
	public int getOriginY()
	{
		return 0;
	}

	@Override
	public void drawSelfBasic(Graphics2D g2d)
	{
		// Draws the sprite
		if (this.texturedrawer != null)
			this.texturedrawer.drawSprite(g2d, 0, 0);
	}
	
	
	// GETTERS & SETTERS	----------------------------------------------
	
	/**
	 * @return The spritedrawer used to drawing the texture of the background
	 */
	public SingleSpriteDrawer getSpriteDrawer()
	{
		return this.texturedrawer;
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Changes the background's width and height
	 *
	 * @param width The new width of the background
	 * @param height The new height of the background
	 */
	public void setDimensions(int width, int height)
	{
		// Calculates the scaling
		double xscale = width / (double) this.texturedrawer.getSprite().getWidth();
		double yscale = height / (double) this.texturedrawer.getSprite().getHeight();
		
		// Scales the object
		scale(xscale, yscale);
	}
	
	/**
	 * Creates a list containing multiple backgrounds forming a larger surface.
	 *
	 * @param minx The left-top x-coordinate of the surface
	 * @param miny The left-top y-coordinate of the surface
	 * @param width The width of the surface (in pixels)
	 * @param height The height of the surface (in pixels)
	 * @param approximate Are the backgrounds scaled so that they all fit into the area. 
	 * If false, the backgrounds may be placed partly outside the given area.
	 * @param texture The sprite used to draw the background(s)
	 * @param area The area where the background will reside
	 * 
	 * @return A list containing all backgrounds used to create the surface
	 */
	public static ArrayList<Background> getRepeatedBackground(
			int minx, int miny, int width, int height, boolean approximate, 
			Sprite texture, AreaOldVersion area)
	{
		// Calculates the number of backgrounds used horizontally and vertically
		int backbasewidth = texture.getWidth();
		int backbaseheight = texture.getHeight();
		int horbacks = 1 + width / backbasewidth;
		int verbacks = 1 + height / backbaseheight;
		
		double xscale = 1;
		double yscale = 1;
		
		// If approximation is on, scales the backgrounds a bit to fit the area
		if (approximate)
		{
			xscale = width / (horbacks * backbasewidth);
			yscale = height / (verbacks * backbaseheight);
		}
		
		// Initializes the returned list
		ArrayList<Background> backs = new ArrayList<Background>();
		
		// Creates all the backgrounds and adds them to the list
		for (int iy = 0; iy < verbacks; iy++)
		{
			for (int ix = 0; ix < horbacks; ix++)
			{
				Background newback = new Background(
						minx + (int) (ix * backbasewidth * xscale) + 
						(int) (xscale * backbasewidth/2), 
						miny + (int) (iy * backbaseheight * yscale) + 
						(int) (yscale * backbaseheight/2), 
						texture, area);
				newback.scale(xscale, yscale);
				backs.add(newback);
			}
		}
		
		// Returns the complete list
		return backs;
	}
}
