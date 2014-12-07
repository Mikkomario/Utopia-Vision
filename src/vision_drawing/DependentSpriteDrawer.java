package vision_drawing;

import java.awt.Graphics2D;

import vision_sprite.SpriteDrawer;
import exodus_object.GameObject;
import exodus_util.Transformable;
import genesis_event.HandlerRelay;
import genesis_util.StateOperator;
import genesis_util.Vector2D;

/**
 * This class is a dependent drawer that uses sprites / spriteDrawers in the drawing process.
 * 
 * @author Mikko Hilpinen
 * @since 5.12.2014
 * @param <T> The type of object using this drawer
 * @param <SpriteDrawerType> The type of SpriteDrawer this drawer uses
 */
public class DependentSpriteDrawer<T extends Transformable & GameObject, 
		SpriteDrawerType extends SpriteDrawer> extends AbstractDependentDrawer<T>
{
	// ATTRIBUTES	-------------------------
	
	private SpriteDrawerType spriteDrawer;
	
	
	// CONSTRUCTOR	-------------------------
	
	/**
	 * Creates a new drawer
	 * @param user The user that uses this drawer
	 * @param initialDepth The depth the drawer uses initially
	 * @param spriteDrawer The spriteDrawer used for drawing the sprite(s) (optional, 
	 * can be added later with setSpriteDrawer())
	 * @param handlers The handlers that will handle this drawer
	 * @param isVisibleOperator The stateOperator that defines the visibility of this drawer
	 */
	public DependentSpriteDrawer(T user, int initialDepth, SpriteDrawerType spriteDrawer, 
			HandlerRelay handlers, StateOperator isVisibleOperator)
	{
		super(user, initialDepth, handlers, isVisibleOperator);
		
		// Initializes attributes
		this.spriteDrawer = spriteDrawer;
	}
	
	/**
	 * Creates a new drawer
	 * @param user The user that uses this drawer
	 * @param initialDepth The depth the drawer uses initially
	 * @param spriteDrawer The spriteDrawer used for drawing the sprite(s)
	 * @param handlers The handlers that will handle this drawer
	 */
	public DependentSpriteDrawer(T user, int initialDepth, SpriteDrawerType spriteDrawer, 
			HandlerRelay handlers)
	{
		super(user, initialDepth, handlers);
		
		// Initializes attributes
		this.spriteDrawer = spriteDrawer;
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
	public void scaleToSize(Vector2D dimensions)
	{
		//    W2 = S * W1
		// -> S = W2 / W1
		setTrasformation(getOwnTransformation().withScaling(dimensions.dividedBy(
				getSpriteDrawer().getSprite().getDimensions())));
	}
}
