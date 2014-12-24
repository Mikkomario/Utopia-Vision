package vision_drawing;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import omega_util.DependentGameObject;
import omega_util.GameObject;
import omega_util.Transformable;
import omega_util.Transformation;
import genesis_event.Drawable;
import genesis_event.HandlerRelay;
import genesis_util.StateOperator;

/**
 * An object can use a drawer to draw stuff on screen according to its transformation(s).
 * 
 * @author Mikko Hilpinen
 * @since 5.12.2014
 * @param <T> The type of object that uses this drawer
 */
public abstract class AbstractDependentDrawer<T extends Transformable & GameObject> extends 
		DependentGameObject<T> implements Drawable, Transformable
{
	// ATTRIBUTES	------------------------------
	
	private StateOperator isVisibleOperator;
	private Transformation ownTransformation;
	private int depth;
	private float alpha;
	
	
	// CONSTRUCTOR	-----------------------------
	
	/**
	 * Creates a new drawer. The drawer's visibility will depend from the user's activity.
	 * @param user The user that will use the drawer. The drawer's activity and visibility 
	 * will be tied to that of the user.
	 * @param initialDepth How deep the drawer draws stuff
	 * @param handlers The handlers that will handle the drawer.
	 */
	public AbstractDependentDrawer(T user, int initialDepth, HandlerRelay handlers)
	{
		super(user, handlers);
		
		// Initializes attributes
		this.isVisibleOperator = null;
		this.ownTransformation = new Transformation();
		this.depth = initialDepth;
		this.alpha = 1;
	}
	
	/**
	 * Creates a new drawer. The drawer's visibility can be handled individually.
	 * @param user The user that will use the drawer. The drawer's activity 
	 * will be tied to that of the user.
	 * @param initialDepth How deep the drawer draws stuff
	 * @param handlers The handlers that will handle the drawer.
	 * @param isVisibleOperator The operator that defines the drawer's visibility.
	 */
	public AbstractDependentDrawer(T user, int initialDepth, HandlerRelay handlers, 
			StateOperator isVisibleOperator)
	{
		super(user, handlers);
		
		// Initializes attributes
		this.isVisibleOperator = isVisibleOperator;
		this.ownTransformation = new Transformation();
		this.depth = initialDepth;
		this.alpha = 1;
	}
	
	
	// ABSTRACT METHODS	----------------------------
	
	/**
	 * Draws the stuff this drawer is supposed to draw. The transformations have already been 
	 * applied at this point so the subclass shouldn't apply them again.
	 * 
	 * @param g2d The graphics object that does the actual drawing
	 */
	protected abstract void drawSelfBasic(Graphics2D g2d);
	
	
	// IMPLEMENTED METHODS	------------------------

	@Override
	public void drawSelf(Graphics2D g2d)
	{
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
		
		AffineTransform lastTransform = g2d.getTransform();
		g2d.transform(getTransformation().toAffineTransform());
		
		drawSelfBasic(g2d);
		
		g2d.setTransform(lastTransform);
	}

	@Override
	public int getDepth()
	{
		return this.depth;
	}

	@Override
	public StateOperator getIsVisibleStateOperator()
	{
		if (this.isVisibleOperator != null)
			return this.isVisibleOperator;
		else
			return getMaster().getIsActiveStateOperator();
	}

	@Override
	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	@Override
	public Transformation getTransformation()
	{
		return getMaster().getTransformation().transform(getOwnTransformation());
	}

	@Override
	public void setTrasformation(Transformation t)
	{
		this.ownTransformation = t;
	}
	
	
	// GETTERS & SETTERS	--------------------------
	
	/**
	 * @return The opacity / alpha value of the object [0, 1]
	 */
	public float getAlpha()
	{
		return this.alpha;
	}
	
	/**
	 * Changes the object's opacity / alpha
	 * @param alpha The new alpha / opacity value of the object [0, 1]
	 */
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
		
		if (getAlpha() < 0)
			setAlpha(0);
		if (getAlpha() > 1)
			setAlpha(1);
	}
	
	/**
	 * @return This object's independent transformation, the one applied on top of the 
	 * master object's transformation
	 */
	public Transformation getOwnTransformation()
	{
		return this.ownTransformation;
	}
	
	/**
	 * Modifies the drawer's own transformation according to the given transformation
	 * @param t How the drawer's transformation is transformed
	 */
	public void addToOwnTransformation(Transformation t)
	{
		setTrasformation(getOwnTransformation().plus(t));
	}
}
