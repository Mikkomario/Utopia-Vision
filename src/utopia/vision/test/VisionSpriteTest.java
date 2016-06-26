package utopia.vision.test;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import utopia.genesis.event.Actor;
import utopia.genesis.event.Drawable;
import utopia.genesis.event.MouseEvent;
import utopia.genesis.event.MouseListener;
import utopia.genesis.event.StepHandler;
import utopia.genesis.util.Transformable;
import utopia.genesis.util.Transformation;
import utopia.genesis.util.Vector3D;
import utopia.genesis.video.GamePanel;
import utopia.genesis.video.GameWindow;
import utopia.genesis.video.PanelMouseListenerHandler;
import utopia.genesis.video.GamePanel.ScalingPolicy;
import utopia.genesis.video.SplitPanel.ScreenSplit;
import utopia.inception.event.EventSelector;
import utopia.inception.event.StrictEventSelector;
import utopia.inception.handling.HandlerRelay;
import utopia.inception.util.SimpleHandled;
import utopia.vision.event.AnimationEvent;
import utopia.vision.event.AnimationEventListener;
import utopia.vision.filter.FunctionFilter;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.SpriteDrawer;
import utopia.vision.util.DependentSpriteObject;
import utopia.vision.util.MaskChecker;

/**
 * This test tests the various sprite drawing functions
 * @author Mikko Hilpinen
 * @since 12.6.2016
 */
class VisionSpriteTest
{
	// MAIN METHOD	----------------
	
	public static void main(String[] args)
	{
		try
		{
			// Sets up the environment
			Vector3D resolution = new Vector3D(800, 500);
			
			StepHandler stepHandler = new StepHandler(120, 20);
			GameWindow window = new GameWindow(resolution.toDimension(), "Vision Test", false, 
					false, ScreenSplit.HORIZONTAL);
			
			GamePanel panel = new GamePanel(resolution, ScalingPolicy.PROJECT, 120);
			window.addGamePanel(panel);
			
			PanelMouseListenerHandler mouseHandler = new PanelMouseListenerHandler(panel, true);
			stepHandler.add(mouseHandler);
			
			HandlerRelay handlers = new HandlerRelay();
			handlers.addHandler(stepHandler, panel.getDrawer(), mouseHandler);
			
			// Creates the resources
			Sprite sprite = new Sprite(new File("testData/panic_spell_strip4.png"), 4, null, null, 8);
			//Sprite luminous = sprite.withLuminosity(1.6f);
			
			// Creates the objects
			SimpleSpriteObject independent = new SimpleSpriteObject(sprite, resolution.dividedBy(2));
			
			SpriteDrawer dependentDrawer = new SpriteDrawer(sprite);
			//dependentDrawer.applyFilter(new LuminosityFilter(1f, 1f, 2f));
			//dependentDrawer.applyFilter(new SharpnessFilter(false));
			//dependentDrawer.applyFilter(new FunctionFilter(new FunctionFilter.InvertFunction(), 
			//		null, null));
			//dependentDrawer.applyFilter(new HSBFilter(-0.5, 0, new ColourTarget(Color.WHITE, 400, false)));
			dependentDrawer.applyFilter(new FunctionFilter(new FunctionFilter.TresholdFunction(4)));
			DependentSpriteObject<?> dependent = new DependentSpriteObject<>(independent, 
					dependentDrawer, 5);
			//dependent.setAlpha(0.5f);
			dependent.transform(Transformation.scalingTransformation(1.5));
			
			handlers.add(independent, dependent, new MaskTest(independent));
					
			// Starts the program
			stepHandler.start();
		}
		catch (IOException e)
		{
			System.err.println("Resource initialisation failed");
			e.printStackTrace();
		}
	}
	
	
	// NESTED CLASSES	------------
	
	private static class SimpleSpriteObject extends SimpleHandled implements Drawable, 
			Transformable, Actor, AnimationEventListener
	{
		// ATTRIBUTES	------------
		
		private Transformation transformation;
		private SpriteDrawer drawer;
		private StrictEventSelector<AnimationEvent, AnimationEvent.Feature> selector = 
				new StrictEventSelector<>();
		//private Sprite originalSprite, reversedSprite;
		private double phase = 0;
		
		
		// CONSTUCTOR	------------
		
		public SimpleSpriteObject(Sprite sprite, Vector3D position)
		{
			this.transformation = new Transformation(position);
			this.drawer = new SpriteDrawer(sprite);
			//this.drawer.setAnimationDuration(StepHandler.millisToSteps(1000));
			//this.originalSprite = sprite;
			//this.reversedSprite = sprite.reverse();
			this.selector.addUnacceptableFeature(AnimationEvent.EventType.FRAME_CHANGED);
			
			this.drawer.getAnimationListenerHandler().add(this);
		}
		
		
		// IMPLEMENTED METHODS	----
		
		@Override
		public void act(double duration)
		{
			this.drawer.animate(duration);
			
			this.phase = (this.phase + duration * 0.0001) % 1;
			setTrasformation(getTransformation().withScaling(0.5 + Math.sin(this.phase * Math.PI)));
		}

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
		public void drawSelf(Graphics2D g2d)
		{
			AffineTransform lastTransform = g2d.getTransform();
			getTransformation().transform(g2d);
			getDrawer().drawSprite(g2d);
			g2d.setTransform(lastTransform);
		}

		@Override
		public int getDepth()
		{
			return 0;
		}
		
		
		// ACCESSORS	--------------
		
		public SpriteDrawer getDrawer()
		{
			return this.drawer;
		}


		@Override
		public EventSelector<AnimationEvent> getAnimationEventSelector()
		{
			return this.selector;
		}

		@Override
		public void onAnimationEvent(AnimationEvent event)
		{
			System.out.println(event.getType());
			/*
			if (event.getType() == EventType.ANIMATION_COMPLETED)
			{
				// TODO: Reversing looks wonky
				if (this.drawer.getSprite() == this.originalSprite)
					this.drawer.setSprite(this.reversedSprite, false);
				else
					this.drawer.setSprite(this.originalSprite, false);
				this.drawer.setFrameIndex(1);
			}
			*/
		}
	}
	
	private static class MaskTest extends SimpleHandled implements MouseListener
	{
		// ATTRIBUTES	------------------
		
		private SimpleSpriteObject target;
		private EventSelector<MouseEvent> selector;
		
		
		// CONSTRUCTOR	------------------
		
		public MaskTest(SimpleSpriteObject target)
		{
			this.target = target;
			this.selector = MouseEvent.createMouseMoveSelector();
		}
		
		
		// IMPLEMENTED METHODS	----------
		
		@Override
		public void onMouseEvent(MouseEvent event)
		{
			// If over a sprite, generates an event
			Vector3D relativePosition = 
					this.target.getTransformation().inverseTransform(event.getPosition());
			if (MaskChecker.getAlpha(this.target.getDrawer(), relativePosition) > 10)
				System.out.println("Mouse over sprite");
		}

		@Override
		public EventSelector<MouseEvent> getMouseEventSelector()
		{
			return this.selector;
		}

		@Override
		public boolean isInAreaOfInterest(Vector3D position)
		{
			// Has no specific area of interest
			return false;
		}
	}
}
