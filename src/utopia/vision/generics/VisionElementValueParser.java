package utopia.vision.generics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utopia.flow.generics.DataType;
import utopia.flow.generics.Value;
import utopia.flow.io.ElementValueParser;
import utopia.flow.structure.Element;
import utopia.flow.structure.Node;
import utopia.flow.structure.Pair;
import utopia.flow.structure.TreeNode;
import utopia.genesis.generics.GenesisDataType;
import utopia.genesis.util.Vector3D;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.Tile;
import utopia.vision.resource.TileMap;

/**
 * This class handles the element parsing of vision-originated data types
 * @author Mikko Hilpinen
 * @since 15.6.2016
 */
public class VisionElementValueParser implements ElementValueParser
{
	// IMPLEMENTED METHODS	-----------

	@Override
	public DataType[] getParsedTypes()
	{
		return VisionDataType.values();
	}

	@Override
	public TreeNode<Element> writeValue(Value value) throws ElementValueParsingFailedException
	{
		DataType from = value.getType();
		
		if (from.equals(VisionDataType.SPRITE))
		{
			Sprite sprite = VisionDataType.valueToSprite(value);
			
			TreeNode<Element> root = new TreeNode<>(new Element("sprite"));
			addChildElement(root, "file", Value.String(sprite.getSourceFile().getPath()));
			addChildElement(root, "length", Value.Integer(sprite.getLength()));
			addChildElement(root, "origin", GenesisDataType.Vector(sprite.getOrigin()));
			addChildElement(root, "size", GenesisDataType.Vector(sprite.getSize()));
			addChildElement(root, "animationSpeed", Value.Double(sprite.getDefaultAnimationSpeed()));
			
			return root;
		}
		else if (from.equals(VisionDataType.TILE))
		{
			Tile tile = VisionDataType.valueToTile(value);
			
			TreeNode<Element> root = new TreeNode<>(new Element("tile"));
			addChildElement(root, "bankName", Value.String(tile.getSpriteBankName()));
			addChildElement(root, "spriteName", Value.String(tile.getSpriteName()));
			addChildElement(root, "size", GenesisDataType.Vector(tile.getSize()));
			addChildElement(root, "startFrameIndex", Value.Integer(tile.getStartFrameIndex()));
			addChildElement(root, "animated", Value.Boolean(tile.isAnimated()));
			
			return root;
		}
		else if (from.equals(VisionDataType.TILEMAP))
		{
			TileMap map = VisionDataType.valueToTileMap(value);
			
			TreeNode<Element> root = new TreeNode<>(new Element("tileMap"));
			addChildElement(root, "origin", GenesisDataType.Vector(map.getOrigin()));
			// Each tile is added under a separate node, paired with a position
			for (Pair<Vector3D, Tile> tile : map.getTiles())
			{
				TreeNode<Element> node = new TreeNode<>(new Element("tileData"));
				addChildElement(node, "position", GenesisDataType.Vector(tile.getFirst()));
				addChildElement(node, "tile", VisionDataType.Tile(tile.getSecond()));
				root.addChild(node);
			}
			
			return root;
		}
		else
			throw new ElementValueParsingFailedException("Unsupported data type " + from);
	}

	@Override
	public Value readValue(TreeNode<Element> element, DataType targetType)
			throws ElementValueParsingFailedException
	{
		if (targetType.equals(VisionDataType.SPRITE))
		{
			String fileName = null;
			int length = 1;
			Vector3D origin = null;
			Vector3D size = null;
			double animationSpeed = 0.1;
			
			for (Element child : Node.getNodeContent(element.getChildren()))
			{
				switch (child.getName().toLowerCase())
				{
					case "file": fileName = child.getContent().toString(); break;
					case "length": length = child.getContent().toInteger(); break;
					case "origin": origin = GenesisDataType.valueToVector(child.getContent()); break;
					case "size": size = GenesisDataType.valueToVector(child.getContent()); break;
					case "animationspeed": animationSpeed = child.getContent().toDouble(); break;
				}
			}
			
			// File name is required
			if (fileName == null)
				throw new ElementValueParsingFailedException(
						"Element 'file' required under a sprite element");
			
			try
			{
				return VisionDataType.Sprite(new Sprite(new File(fileName), length, origin, size, 
						animationSpeed));
			}
			catch (IOException e)
			{
				throw new ElementValueParsingFailedException("Failed to create a sprite", e);
			}
		}
		else if (targetType.equals(VisionDataType.TILE))
		{
			String bankName = null;
			String spriteName = null;
			Vector3D size = null;
			int startFrameIndex = 0;
			boolean animated = true;
			
			for (Element child : Node.getNodeContent(element.getChildren()))
			{
				switch (child.getName().toLowerCase())
				{
					case "bankname": bankName = child.getContent().toString(); break;
					case "spritename": spriteName = child.getContent().toString(); break;
					case "size": size = GenesisDataType.valueToVector(child.getContent()); break;
					case "startframeindex": startFrameIndex = child.getContent().toInteger(); break;
					case "animated": animated = child.getContent().toBoolean(); break;
				}
			}
			
			// Resource names + size are required
			if (bankName == null || spriteName == null || size == null)
				throw new ElementValueParsingFailedException(
						"Elements bankName, spriteName and size are required under a tile element");
			
			return VisionDataType.Tile(new Tile(bankName, spriteName, size, startFrameIndex, 
					animated));
		}
		else if (targetType.equals(VisionDataType.TILEMAP))
		{
			List<Pair<Vector3D, Tile>> tiles = new ArrayList<>();
			Vector3D origin = Vector3D.ZERO;
			
			for (TreeNode<Element> child : element.getChildren())
			{
				if (child.getContent().getName().equalsIgnoreCase("origin"))
					origin = GenesisDataType.valueToVector(child.getContent().getContent());
				else
				{
					Vector3D position = null;
					Tile tile = null;
					
					for (Element childData : Node.getNodeContent(child.getChildren()))
					{
						if (childData.getName().equalsIgnoreCase("position"))
							position = GenesisDataType.valueToVector(childData.getContent());
						else if (childData.getName().equalsIgnoreCase("tile"))
							tile = VisionDataType.valueToTile(childData.getContent());
					}
					
					// Both tile and position are required
					if (position == null || tile == null)
						throw new ElementValueParsingFailedException(
								"Position and tile elements required under a tileData element");
					
					tiles.add(new Pair<>(position, tile));
				}
			}
			
			return VisionDataType.TileMap(new TileMap(tiles, origin));
		}
		else
			throw new ElementValueParsingFailedException("Unsupported target type " + targetType);
	}
	
	
	// OTHER METHODS	------------
	
	private static void addChildElement(TreeNode<Element> parent, String name, Value contents)
	{
		parent.addChild(new TreeNode<>(new Element(name, contents)));
	}
}
