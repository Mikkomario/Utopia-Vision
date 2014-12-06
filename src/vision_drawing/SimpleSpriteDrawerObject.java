package vision_drawing;

import vision_sprite.SpriteDrawer;
import exodus_object.SimpleGameObject;
import exodus_util.Transformable;
import exodus_util.Transformation;
import genesis_event.HandlerRelay;

/**
 * SimpleSpriteDrawerObjects can be transformed. They draw themselves using spriteDrawers.
 * 
 * @author Mikko Hilpinen
 * @param <SpriteDrawerType> The type of spriteDrawer used by this object
 * @since 6.12.2014
 */
public class SimpleSpriteDrawerObject<SpriteDrawerType extends SpriteDrawer> extends 
		SimpleGameObject implements Transformable
{
	// ATTRIBUTES	-------------------------
	
	private Transformation transformation;
	private DependentSpriteDrawer<SimpleSpriteDrawerObject<SpriteDrawerType>, 
			SpriteDrawerType> drawer;
	
	
	// CONSTRUCTOR	-------------------------
	
	/**
	 * Creates a new object
	 * @param spriteDrawer The drawer used for drawing the sprite(s)
	 * @param initialDepth The drawing depth used by the object
	 * @param handlers The handlers that will handle this object
	 */
	public SimpleSpriteDrawerObject(SpriteDrawerType spriteDrawer, int initialDepth, 
			HandlerRelay handlers)
	{
		super(handlers);
		
		// Initializes attributes
		this.transformation = new Transformation();
		this.drawer = new DependentSpriteDrawer<>(this, initialDepth, spriteDrawer, handlers);
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
	 * @return The drawer used for drawing this object
	 */
	public DependentSpriteDrawer<SimpleSpriteDrawerObject<SpriteDrawerType>, SpriteDrawerType> getDrawer()
	{
		return this.drawer;
	}
}
