package utopia.vision.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import utopia.genesis.event.Actor;
import utopia.genesis.event.Drawable;
import utopia.genesis.util.DepthConstants;
import utopia.genesis.util.Transformable;
import utopia.genesis.util.Transformation;
import utopia.genesis.util.Vector3D;
import utopia.inception.util.SimpleHandled;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.SpriteDrawer;

/**
 * This is a simple class that implements basic sprite drawing and animation, combined with 
 * transformations
 * @author Mikko Hilpinen
 * @since 22.6.2016
 */
public class SimpleSpriteObject extends SimpleHandled
		implements Drawable, Actor, Transformable
{
	// ATTRIBUTES	--------------------
	
	private Transformation transformation;
	private int depth;
	private SpriteDrawer drawer;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new object
	 * @param transformation The initial transformation of the object
	 * @param drawer The sprite drawer the object will use
	 * @param initialDepth The initial drawing depth of the object
	 */
	public SimpleSpriteObject(Transformation transformation, SpriteDrawer drawer, int initialDepth)
	{
		this.transformation = transformation;
		this.depth = initialDepth;
		this.drawer = drawer;
	}
	
	/**
	 * Creates a new object
	 * @param position The position of the object
	 * @param sprite The sprite used by the object
	 * @param initialDepth The initial drawing depth of the object
	 */
	public SimpleSpriteObject(Vector3D position, Sprite sprite, int initialDepth)
	{
		this.transformation = new Transformation(position);
		this.drawer = new SpriteDrawer(sprite);
		this.depth = initialDepth;
	}
	
	
	// IMPLEMENTED METHODS	------------

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
	public void act(double duration)
	{
		// Animates the drawer
		getDrawer().animate(duration);
	}

	@Override
	public void drawSelf(Graphics2D g2d)
	{
		// Applies transformations and draws the sprite
		AffineTransform lastTransform = getTransformation().transform(g2d);
		getDrawer().drawSprite(g2d);
		g2d.setTransform(lastTransform);
	}

	@Override
	public int getDepth()
	{
		return this.depth;
	}

	
	// ACCESSORS	----------------
	
	/**
	 * @return The sprite drawer used by the object
	 */
	public SpriteDrawer getDrawer()
	{
		return this.drawer;
	}
	
	/**
	 * Changes the sprite drawer used by the object
	 * @param drawer The new sprite drawer to be used
	 */
	public void setDrawer(SpriteDrawer drawer)
	{
		this.drawer = drawer;
	}
	
	/**
	 * Changes the drawing depth of the object
	 * @param drawingDepth The new drawing depth of the object
	 * @see DepthConstants
	 */
	public void setDepth(int drawingDepth)
	{
		this.depth = drawingDepth;
	}
	
	
	// OTHER METHODS	-------------
	
	/**
	 * Transforms the object. The transformation is applied over the object's current 
	 * trasformation
	 * @param transformation The transformation applied to the object
	 */
	public void transform(Transformation transformation)
	{
		Transformable.transform(this, transformation);
	}
	
	/**
	 * Moves the object
	 * @param movement How much the object is moved
	 */
	public void move(Vector3D movement)
	{
		transform(Transformation.transitionTransformation(movement));
	}
	
	/**
	 * Rotates the object
	 * @param rotateAngle How much the object is rotated, in angles counter-clockwise
	 */
	public void rotate(double rotateAngle)
	{
		transform(Transformation.rotationTransformation(rotateAngle));
	}
	
	/**
	 * Changes the object's position
	 * @param position The new position
	 */
	public void setPosition(Vector3D position)
	{
		setTrasformation(getTransformation().withPosition(position));
	}
	
	/**
	 * Changes the object's rotation angle
	 * @param angle The new angle the object is rotated into, in degrees counter-clockwise
	 */
	public void setAngle(double angle)
	{
		setTrasformation(getTransformation().withAngle(angle));
	}
	
	/**
	 * @return The object's current position
	 */
	public Vector3D getPosition()
	{
		return getTransformation().getPosition();
	}
	
	/**
	 * @return The object's current rotation (in degrees)
	 */
	public double getAngle()
	{
		return getTransformation().getAngle();
	}
}
