package vision_test;

import java.io.BufferedWriter;
import java.util.Random;

import exodus_object.ConstructableGameObject;
import exodus_util.Transformation;
import exodus_world.Area;
import exodus_world.AreaBank;
import exodus_world.AreaHandlerConstructor;
import exodus_world.AreaObjectConstructorProvider;
import flow_io.FileOutputAccessor;
import flow_recording.AbstractConstructor;
import flow_recording.TextObjectWriter;
import genesis_event.ActorHandler;
import genesis_event.DrawableHandler;
import genesis_event.HandlerRelay;
import genesis_util.DepthConstants;
import genesis_util.Vector2D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;
import arc_bank.GamePhaseBank;
import arc_resource.ResourceActivator;
import vision_drawing.TileMap;
import vision_drawing.TileMapConstructor;
import vision_sprite.SpriteBank;

/**
 * This class tests some new functionalities introduced in this module
 * 
 * @author Mikko Hilpinen
 * @since 7.12.2014
 */
public class VisionTest
{
	// TODO: Make tileMaps resources?
	
	// CONSTRUCTOR	---------------
	
	private VisionTest()
	{
		// The constructor is hidden since the interface is static
	}

	
	// MAIN METHOD	---------------
	
	/**
	 * Starts the test
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		// Initializes resources (other than areas)
		SpriteBank.initializeSpriteResources("testing/sprites.txt");
		GamePhaseBank.initializeGamePhaseResources("testing/gamePhases.txt", "default");
		
		// The phase1 is required for the construction
		ResourceActivator.startPhase(GamePhaseBank.getGamePhase("phase1"), true);
		
		// creates and writes a tilemap
		System.out.println("Creates a randomly generated tilemap");
		
		TileMap map = new TileMap("test", DepthConstants.BOTTOM, new Vector2D(5, 4), 
				new Vector2D(100, 100), new HandlerRelay());
		map.setTrasformation(Transformation.transitionTransformation(new Vector2D(32, 32)));
		
		String[] spriteNames = {"belt", "mark", "close"};
		Random random = new Random();
		
		for (int x = 0; x < 5; x ++)
		{
			for (int y = 0; y < 4; y++)
			{
				map.setTile(new Vector2D(x, y), 
						spriteNames[random.nextInt(spriteNames.length)], 0, 
						random.nextDouble() * 0.3);
			}
		}
		
		// TODO: DependentTransformations don't work like that. Fix in exodus or here
		map.setTrasformation(map.getTransformation().withPosition(new Vector2D(100, 100)));
		map.setTrasformation(map.getTransformation().plus(Transformation.rotationTransformation(5)));
		
		System.out.println("Writes the map");
		
		BufferedWriter fileWriter = FileOutputAccessor.openFile("testing/area1.txt");
		TextObjectWriter objectWriter = new TextObjectWriter();
		objectWriter.writeInstruction("tiles", fileWriter);
		objectWriter.writeInto(map, fileWriter);
		FileOutputAccessor.closeWriter(fileWriter);
		
		// Creates the window and the superHandlers
		GameWindow window = new GameWindow(new Vector2D(600, 500), "VisionTest", true, 
				120, 20);
		GamePanel panel = window.getMainPanel().addGamePanel();
		HandlerRelay superHandlers = new HandlerRelay();
		superHandlers.addHandler(new ActorHandler(false, window.getHandlerRelay()));
		superHandlers.addHandler(new DrawableHandler(false, false, 0, 1, panel.getDrawer()));
		
		System.out.println("Reconstructs the map");
		
		// Initializes area resources
		AreaBank.initializeAreaResources("testing/areas.txt", 
				new TestHandlerConstructor(superHandlers), new TestObjectConstructorProvider());
		AreaBank.activateAreaBank("test");
		
		// Creates a test object as well
		Area area = AreaBank.getArea("test", "area1");
		/*
		SimpleSingleSpriteDrawerObject testObject = new SimpleSingleSpriteDrawerObject(0, 
				SpriteBank.getSprite("test", "close"), area.getHandlers());
		testObject.setTrasformation(Transformation.transitionTransformation(new Vector2D(100, 100)));
		*/
		// Starts the first ares
		area.start(false);
	}
	
	
	// SUBCLASSES	-----------------------
	
	private static class TestHandlerConstructor implements AreaHandlerConstructor
	{
		// ATTRIBUTES	-------------------
		
		private HandlerRelay superHandlers;
		
		
		// CONSTRUCTOR	-------------------
		
		public TestHandlerConstructor(HandlerRelay superHandlers)
		{
			this.superHandlers = superHandlers;
		}
		
		
		// IMPLEMENTED METHODS	--------------
		
		@Override
		public HandlerRelay constructRelay(String areaName)
		{
			HandlerRelay handlers = new HandlerRelay();
			
			handlers.addHandler(new DrawableHandler(false, true, 0, 1, this.superHandlers));
			handlers.addHandler(new ActorHandler(false, this.superHandlers));
			
			return handlers;
		}
	}
	
	private static class TestObjectConstructorProvider implements 
			AreaObjectConstructorProvider<ConstructableGameObject>
	{
		@Override
		public AbstractConstructor<ConstructableGameObject> getConstructor(
				Area targetArea)
		{
			return new TileMapConstructor(targetArea.getHandlers());
		}
	}
}
