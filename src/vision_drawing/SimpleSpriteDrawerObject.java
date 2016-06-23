package vision_drawing;

import vision_sprite.MultiSpriteDrawer;
import vision_sprite.SingleSpriteDrawer;
import vision_sprite.Sprite;
import vision_sprite.SpriteDrawer;
import genesis_event.HandlerRelay;
import genesis_util.SimpleHandled;
import genesis_util.Transformable;
import genesis_util.Transformation;
import utopia.vision.util.SimpleSpriteObject;

/**
 * SimpleSpriteDrawerObjects can be transformed. They draw themselves using spriteDrawers.
 * 
 * @author Mikko Hilpinen
 * @param <SpriteDrawerType> The type of spriteDrawer used by this object
 * @since 6.12.2014
 * @deprecated Replaced with {@link SimpleSpriteObject}
 */
public class SimpleSpriteDrawerObject<SpriteDrawerType extends SpriteDrawer> extends 
		SimpleHandled implements Transformable
{
	// ATTRIBUTES	-------------------------
	
	private Transformation transformation;
	private DependentSpriteDrawer<SimpleSpriteDrawerObject<SpriteDrawerType>, 
			SpriteDrawerType> drawer;
	
	
	// CONSTRUCTOR	-------------------------
	
	/**
	 * Creates a new object. The spriteDrawer has to be set separately, since it will probably 
	 * use this object as the master object.
	 * @param initialDepth The drawing depth used by the object
	 * @param handlers The handlers that will handle this object
	 * @see #setSpriteDrawer(SpriteDrawer)
	 */
	public SimpleSpriteDrawerObject(int initialDepth, HandlerRelay handlers)
	{
		super(handlers);
		
		// TODO: No sprite drawer in the constructor?
		
		// Initializes attributes
		this.transformation = new Transformation();
		this.drawer = new DependentSpriteDrawer<>(this, initialDepth, null, handlers);
	}
	
	
	// IMPLEMENTED METHODS	--------------------------

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
	
	
	// GETTERS & SETTERS	--------------------------
	
	/**
	 * @return The dependent drawer used for drawing this object
	 */
	public DependentSpriteDrawer<SimpleSpriteDrawerObject<SpriteDrawerType>, SpriteDrawerType> getDrawer()
	{
		return this.drawer;
	}
	
	/**
	 * @return The spriteDrawer used for drawing the object
	 */
	public SpriteDrawerType getSpriteDrawer()
	{
		return getDrawer().getSpriteDrawer();
	}
	
	
	// OTHER METHODS	--------------------------
	
	/**
	 * Modifies the object's transformation
	 * @param t The transformation applied to the object
	 */
	public void addTransformation(Transformation t)
	{
		setTrasformation(getTransformation().plus(t));
	}
	
	/**
	 * Changes the spriteDrawer used by this object
	 * @param spriteDrawer The spriteDrawer that will be used by this object
	 */
	public void setSpriteDrawer(SpriteDrawerType spriteDrawer)
	{
		getDrawer().setSpriteDrawer(spriteDrawer);
	}
	
	/**
	 * Creates a new sprite drawer object that uses a single sprite
	 * @param initialDepth The drawing depth the object initially has
	 * @param sprite The sprite the object uses to draw itself
	 * @param handlers The handlers that will handle the object
	 * @return The object that was created
	 */
	public static SimpleSpriteDrawerObject<SingleSpriteDrawer> createSingleSpriteDrawerObject(
			int initialDepth, Sprite sprite, HandlerRelay handlers)
	{
		SimpleSpriteDrawerObject<SingleSpriteDrawer> drawer = new SimpleSpriteDrawerObject<>(
				initialDepth, handlers);
		drawer.setSpriteDrawer(new SingleSpriteDrawer(sprite, drawer, handlers));
		
		return drawer;
	}
	
	/**
	 * Creates a new sprite drawer object that uses multiple sprites
	 * @param initialDepth The drawing depth the object initially has
	 * @param sprites The sprites the object uses to draw itself
	 * @param handlers The handlers that will handle the object
	 * @return The object that was created
	 */
	public static SimpleSpriteDrawerObject<MultiSpriteDrawer> createMultiSpriteDrawerObject(
			int initialDepth, Sprite[] sprites, HandlerRelay handlers)
	{
		SimpleSpriteDrawerObject<MultiSpriteDrawer> drawer = new SimpleSpriteDrawerObject<>(
				initialDepth, handlers);
		drawer.setSpriteDrawer(new MultiSpriteDrawer(sprites, drawer, handlers));
		
		return drawer;
	}
}
