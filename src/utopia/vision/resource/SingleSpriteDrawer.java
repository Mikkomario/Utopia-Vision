package utopia.vision.resource;

import utopia.genesis.util.Vector3D;
import utopia.vision.event.AnimationEvent.EventType;

/**
 * This drawer is meant to be used in situations where the drawn sprite rarely changes
 * @author Mikko Hilpinen. 
 * @since 16.1.2014
 */
public class SingleSpriteDrawer extends SpriteDrawer
{
	// ATTRIBUTES	--------------------
	
	private Sprite sprite;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new drawer. The used origin is the sprite's default origin
	 * @param sprite The sprite that the drawer will draw / animate
	 */
	public SingleSpriteDrawer(Sprite sprite)
	{
		this.sprite = sprite;
	}
	
	/**
	 * Creates a new drawer
	 * @param sprite The sprite that the drawer will draw / animate
	 * @param origin The origin used when drawing the sprite (relative)
	 */
	public SingleSpriteDrawer(Sprite sprite, Vector3D origin)
	{
		super(origin);
		this.sprite = sprite;
	}
	
	
	// IMPLEMENTED METHODS	--------------

	@Override
	public Sprite getSprite()
	{
		return this.sprite;
	}
	
	
	// OTHER METHODS	-----------------
	
	/**
	 * Changes the sprite drawn
	 * @param sprite The sprite that will be drawn from now on
	 * @param resetAnimation Should the animation be reset as well (back to the first frame 
	 * of the new sprite)
	 */
	public void setSprite(Sprite sprite, boolean resetAnimation)
	{
		if (!this.sprite.equals(sprite))
		{
			this.sprite = sprite;
			generateAnimationEvent(EventType.SPRITE_CHANGED);
			if (resetAnimation)
				resetAnimation();
		}
	}
}
