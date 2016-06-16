package utopia.vision.test;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import utopia.arc.io.XmlFileBankRecorder;
import utopia.arc.resource.Bank;
import utopia.arc.resource.BankBank;
import utopia.arc.resource.BankRecorder;
import utopia.arc.resource.BankRecorder.RecordingFailedException;
import utopia.flow.structure.Pair;
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
import utopia.vision.generics.VisionDataType;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.Tile;
import utopia.vision.resource.TileMap;
import utopia.vision.resource.TileMapDrawer;

/**
 * This class tests the basic functions of TileMap and TileMapDrawer
 * @author Mikko Hilpinen
 * @since 16.6.2016
 */
class VisionMapTest
{
	// ATTRIBUTES	---------------
	
	private static final Path RESOURCE_DIRECTORY = Paths.get("testData"); 
	
	
	// MAIN METHOD	----------------
	
	public static void main(String[] args)
	{
		try
		{
			VisionDataType.initialise();
			
			// Creates the sprites
			createSprites();
			
			// Creates the tilemap(s) next
			createTileMaps();
			
			// Reads the resources
			BankRecorder recorder = new XmlFileBankRecorder(RESOURCE_DIRECTORY);
			BankBank<Sprite> sprites = new BankBank<>(VisionDataType.SPRITE, recorder, true);
			BankBank<TileMap> tileMaps = new BankBank<>(VisionDataType.TILEMAP, recorder, true);
			
			sprites.initialiseAll();
			tileMaps.initialiseAll();
			
			// Sets up the environment
			Vector3D resolution = new Vector3D(800, 500);
			
			StepHandler stepHandler = new StepHandler(120, 20);
			GameWindow window = new GameWindow(resolution.toDimension(), "Test", false, 
					false, ScreenSplit.HORIZONTAL);
			GamePanel panel = new GamePanel(resolution, ScalingPolicy.PROJECT, 120);
			window.addGamePanel(panel);
			
			HandlerRelay handlers = new HandlerRelay();
			handlers.addHandler(stepHandler, panel.getDrawer());
			
			// Creates the tilemap drawer object
			handlers.add(new SimpleTileMapObject(resolution.dividedBy(2), 32, 
					tileMaps.get("default", "test"), sprites));
			
			// Starts the game
			stepHandler.start();
		}
		catch (Exception e)
		{
			System.err.println("Failed");
			e.printStackTrace();
		}
	}
	
	
	// OTHER METHODS	-----------
	
	private static void createSprites() throws IOException, RecordingFailedException
	{
		Bank<Sprite> sprites = new Bank<>("default", VisionDataType.SPRITE, 
				new XmlFileBankRecorder(RESOURCE_DIRECTORY));
		sprites.put("bookMark", new Sprite(RESOURCE_DIRECTORY.resolve("bookmarks_strip5.png").toFile(), 
				5, null));
		sprites.put("close", new Sprite(RESOURCE_DIRECTORY.resolve("closebutton_strip2.png").toFile(), 
				2, Vector3D.ZERO));
		
		sprites.save();
	}
	
	private static void createTileMaps() throws RecordingFailedException
	{
		List<Pair<Vector3D, Tile>> tiles = new ArrayList<>();
		tiles.add(new Pair<>(Vector3D.ZERO, new Tile("default", "bookMark", 
				new Vector3D(64, 96), 0, false)));
		tiles.add(new Pair<>(new Vector3D(64), new Tile("default", "close", new Vector3D(96, 96))));
		tiles.add(new Pair<>(new Vector3D(64 + 96), new Tile("default", "bookMark", 
				new Vector3D(64, 96), 3, false)));
		
		TileMap map = new TileMap(tiles, new Vector3D(64 + 96 / 2, 96 / 2));
		
		Bank<TileMap> maps = new Bank<>("default", VisionDataType.TILEMAP, 
				new XmlFileBankRecorder(RESOURCE_DIRECTORY));
		maps.put("test", map);
		maps.save();
	}
	
	
	// NESTED CLASSES	----------------
	
	private static class SimpleTileMapObject extends SimpleHandled implements Drawable, Actor, Transformable
	{
		// ATTRIBUTES	----------------
		
		private Transformation transformation;
		private TileMapDrawer drawer;
		
		
		// CONSTRUCTOR	----------------
		
		public SimpleTileMapObject(Vector3D position, double rotation, TileMap map, 
				BankBank<Sprite> sprites)
		{
			this.transformation = new Transformation(position, Vector3D.IDENTITY, 
					new Vector3D(0.5, 0), rotation);
			this.drawer = new TileMapDrawer(map, sprites);
		}
		
		
		// IMPLEMENTED METHODS	--------

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
			this.drawer.animate(duration);
		}

		@Override
		public void drawSelf(Graphics2D g2d)
		{
			AffineTransform lastTransform = getTransformation().transform(g2d);
			this.drawer.drawMap(g2d);
			g2d.setTransform(lastTransform);
		}

		@Override
		public int getDepth()
		{
			return 0;
		}
	}
}
