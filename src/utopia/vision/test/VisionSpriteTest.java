package utopia.vision.test;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import utopia.genesis.event.Actor;
import utopia.genesis.event.Drawable;
import utopia.genesis.event.StepHandler;
import utopia.genesis.util.Transformable;
import utopia.genesis.util.Transformation;
import utopia.genesis.util.Vector3D;
import utopia.genesis.video.GamePanel;
import utopia.genesis.video.GameWindow;
import utopia.genesis.video.GamePanel.ScalingPolicy;
import utopia.genesis.video.SplitPanel.ScreenSplit;
import utopia.inception.handling.HandlerRelay;
import utopia.inception.util.SimpleHandled;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.SpriteDrawer;

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
			
			HandlerRelay handlers = new HandlerRelay();
			handlers.addHandler(stepHandler, panel.getDrawer());
			
			// Creates the resources
			Sprite sprite = new Sprite(new File("testData/panic_spell_strip4.png"), 4, null);
			Sprite luminous = sprite.withLuminosity(1.6f);
			
			// Creates the objects
			handlers.add(new SimpleSpriteObject(luminous, resolution.dividedBy(2)));
			
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
			Transformable, Actor
	{
		// ATTRIBUTES	------------
		
		private Transformation transformation;
		private SpriteDrawer drawer;
		
		
		// CONSTUCTOR	------------
		
		public SimpleSpriteObject(Sprite sprite, Vector3D position)
		{
			this.transformation = new Transformation(position);
			this.drawer = new SpriteDrawer(sprite);
		}
		
		
		// IMPLEMENTED METHODS	----
		
		@Override
		public void act(double duration)
		{
			this.drawer.animate(duration);
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
	}
}
