package vision_drawing;

import java.awt.Graphics2D;

import vision_sprite.MultiSpriteDrawer;
import vision_sprite.SingleSpriteDrawer;
import vision_sprite.Sprite;
import vision_sprite.SpriteDrawer;
import genesis_event.Handled;
import genesis_event.HandlerRelay;
import genesis_util.Transformable;
import genesis_util.Vector3D;

/**
 * This class is a dependent drawer that uses sprites / spriteDrawers in the drawing process.
 * 
 * @author Mikko Hilpinen
 * @since 5.12.2014
 * @param <T> The type of object using this drawer
 * @param <SpriteDrawerType> The type of SpriteDrawer this drawer uses
 */
public class DependentSpriteDrawer<T extends Transformable & Handled, 
		SpriteDrawerType extends SpriteDrawer> extends AbstractDependentDrawer<T>
{
	// ATTRIBUTES	-------------------------
	
	private SpriteDrawerType spriteDrawer;
	
	
	// CONSTRUCTOR	-------------------------
	
	/**
	 * Creates a new drawer
	 * @param user The user that uses this drawer
	 * @param initialDepth The depth the drawer uses initially
	 * @param spriteDrawer The spriteDrawer used for drawing the sprite(s). The drawer will 
	 * be connected to this drawer.
	 * @param handlers The handlers that will handle this drawer
	 */
	public DependentSpriteDrawer(T user, int initialDepth, SpriteDrawerType spriteDrawer, 
			HandlerRelay handlers)
	{
		super(user, initialDepth, handlers);
		
		// Initializes attributes
		this.spriteDrawer = spriteDrawer;
		
		if (this.spriteDrawer != null) // TODO: Never use this on a constructor like this
			this.spriteDrawer.setMaster(this);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------

	@Override
	protected void drawSelfBasic(Graphics2D g2d)
	{
		if (this.spriteDrawer != null)
			this.spriteDrawer.drawSprite(g2d);
	}
	
	
	// GETTERS & SETTERS	----------------------------
	
	/**
	 * @return The spriteDrawer this drawer uses
	 */
	public SpriteDrawerType getSpriteDrawer()
	{
		return this.spriteDrawer;
	}
	
	/**
	 * Changes the spriteDrawer this drawer uses
	 * @param drawer The new SpriteDrawer this drawer will use
	 */
	public void setSpriteDrawer(SpriteDrawerType drawer)
	{
		// Kills the previous drawer
		if (getSpriteDrawer() != null)
			getSpriteDrawer().separate();
		this.spriteDrawer = drawer;
	}
	
	
	// OTHER METHODS	-----------------------
	
	/**
	 * Changes the size of the drawn sprite to the given dimensions.
	 * @param dimensions The dimensions given to the drawing
	 */
	public void scaleToSize(Vector3D dimensions)
	{
		//    W2 = S * W1
		// -> S = W2 / W1
		setTrasformation(getOwnTransformation().withScaling(dimensions.dividedBy(
				getSpriteDrawer().getSprite().getDimensions())));
	}
	
	/**
	 * Creates a new drawer that uses a single sprite drawer
	 * @param user The object that uses the drawer
	 * @param initialDepth The drawing depth used by the drawer
	 * @param sprite The sprite used by the drawer
	 * @param handlers The handlers that will handle the drawer
	 * @return The drawer that was created
	 */
	// TODO: Remove these methods
	public static <UserType extends Handled & Transformable> DependentSpriteDrawer<UserType, 
			SingleSpriteDrawer> createSingleSpriteDrawer(UserType user, int initialDepth, 
			Sprite sprite, HandlerRelay handlers)
	{
		DependentSpriteDrawer<UserType, SingleSpriteDrawer> drawer = 
				new DependentSpriteDrawer<UserType, SingleSpriteDrawer>(user, initialDepth, 
				null, handlers);
		// TODO: This is why you don't add the master to the constructor
		drawer.setSpriteDrawer(new SingleSpriteDrawer(sprite, drawer, handlers));
		
		return drawer;
	}
	
	/**
	 * Creates a new drawer that uses a single sprite drawer
	 * @param user The object that uses the drawer
	 * @param initialDepth The drawing depth used by the drawer
	 * @param sprites The sprites used by the drawer
	 * @param handlers The handlers that will handle the drawer
	 * @return The drawer that was created
	 */
	public static <UserType extends Handled & Transformable> DependentSpriteDrawer<UserType, 
			MultiSpriteDrawer> createMultiSpriteDrawer(UserType user, int initialDepth, 
			Sprite[] sprites, HandlerRelay handlers)
	{
		DependentSpriteDrawer<UserType, MultiSpriteDrawer> drawer = 
				new DependentSpriteDrawer<UserType, MultiSpriteDrawer>(user, initialDepth, 
				null, handlers);
		drawer.setSpriteDrawer(new MultiSpriteDrawer(sprites, drawer, handlers));
		
		return drawer;
	}
}
