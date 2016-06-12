package utopia.vision.util;

import java.awt.Graphics2D;

import utopia.genesis.event.Actor;
import utopia.genesis.util.DependentDrawer;
import utopia.genesis.util.Transformable;
import utopia.genesis.util.Transformation;
import utopia.genesis.util.Vector3D;
import utopia.inception.handling.Handled;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.SpriteDrawer;

/**
 * This dependent object can be used for drawing sprites so that another object's state and 
 * transformations are taken into account.
 * @author Mikko Hilpinen
 * @since 12.6.2016
 * @param <T> The type of object using this drawer
 */
public class DependentSpriteObject<T extends Handled & Transformable> extends 
		DependentDrawer<T> implements Actor
{
	// ATTRIBUTES	--------------
	
	private SpriteDrawer drawer;
	
	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new sprite object
	 * @param user The object this object depends from
	 * @param drawer The drawer this object uses to draw itself
	 * @param initialDepth The initial drawing depth the object uses
	 */
	public DependentSpriteObject(T user, SpriteDrawer drawer, int initialDepth)
	{
		super(user, Vector3D.ZERO, initialDepth);
		this.drawer = drawer;
	}
	
	
	// IMPLEMENTED METHODS	------

	@Override
	public void act(double duration)
	{
		if (getDrawer() != null)
			getDrawer().animate(duration);
	}

	@Override
	protected void drawSelfBasic(Graphics2D g2d)
	{
		if (getDrawer() != null)
			getDrawer().drawSprite(g2d, getDrawer().getFrameIndex(), Vector3D.ZERO);
	}
	
	/**
	 * @param origin The origin that will be used. Null will be interpreted as the sprite's 
	 * default origin.
	 */
	@Override
	public void setOrigin(Vector3D origin)
	{
		if (getDrawer() != null)
			getDrawer().setOrigin(origin);
	}
	
	@Override
	public Vector3D getOrigin()
	{
		if (getDrawer() == null)
			return Vector3D.ZERO;
		else
			return getDrawer().getOrigin();
	}
	
	
	// ACCESSORS	----------------
	
	/**
	 * @return The sprite drawer this object uses.
	 */
	public SpriteDrawer getDrawer()
	{
		return this.drawer;
	}
	
	/**
	 * Changes the sprite drawer used by this object
	 * @param drawer The sprite drawer used by this object
	 */
	public void setDrawer(SpriteDrawer drawer)
	{
		this.drawer = drawer;
	}
	
	
	// OTHER METHODS	------------
	
	/**
	 * @return The sprite currently used by this object
	 */
	public Sprite getSprite()
	{
		if (getDrawer() == null)
			return null;
		else
			return getDrawer().getSprite();
	}
	
	/**
	 * @return The size of the object. Does not include scaling.
	 */
	public Vector3D getSize()
	{
		Sprite sprite = getSprite();
		if (sprite == null)
			return Vector3D.ZERO;
		else
			return sprite.getSize();
	}
	
	/**
	 * Sets the object's independent scaling so that it fills the provided area (master 
	 * object's scaling may change that, however). If the object's original size is 0, the 
	 * scaling has no effect
	 * @param size The new size of the object
	 */
	public void scaleToSize(Vector3D size)
	{
		Vector3D originalSize = getSize();
		if (!originalSize.equals(Vector3D.ZERO))
			transform(Transformation.scalingTransformation(size.dividedBy(originalSize)));
	}
}
