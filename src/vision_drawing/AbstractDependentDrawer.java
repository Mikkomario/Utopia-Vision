package vision_drawing;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import genesis_event.Drawable;
import genesis_event.Handled;
import genesis_event.HandlerRelay;
import genesis_util.ConnectedHandled;
import genesis_util.Transformable;
import genesis_util.Transformation;

/**
 * An object can use a drawer to draw stuff on screen according to its transformation(s).
 * 
 * @author Mikko Hilpinen
 * @since 5.12.2014
 * @param <T> The type of object that uses this drawer
 */
public abstract class AbstractDependentDrawer<T extends Transformable & Handled> extends 
		ConnectedHandled<T> implements Drawable, Transformable
{
	// TODO: Move to genesis
	
	// ATTRIBUTES	------------------------------
	
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
		// TODO: Only set this if alpha != 1
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
		
		AffineTransform lastTransform = g2d.getTransform();
		g2d.transform(getTransformation().toAffineTransform());
		
		drawSelfBasic(g2d);
		
		g2d.setTransform(lastTransform);
		
		// TODO: Return the alpha to its original value
	}

	@Override
	public int getDepth()
	{
		return this.depth;
	}

	/**
	 * Changes the drawer's depth
	 * @param depth The drawer's new drawing depth
	 */
	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	@Override
	public Transformation getTransformation()
	{
		// TODO: Return own transformation here, create a separate method for combined transformation
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
			setAlpha(0); // TODO: Just use this.alpha =
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
	public void addToOwnTransformation(Transformation t) // TODO: Rename to transform
	{
		setTrasformation(getOwnTransformation().plus(t));
	}
}
