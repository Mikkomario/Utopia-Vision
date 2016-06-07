package utopia.vision.generics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.Conversion;
import utopia.flow.generics.DataType;
import utopia.flow.generics.Model;
import utopia.flow.generics.Value;
import utopia.flow.generics.ValueParser;
import utopia.flow.generics.Variable;
import utopia.genesis.generics.GenesisDataType;
import utopia.genesis.util.Vector3D;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.Tile;

/**
 * This class handles the parsing of vision originated data types
 * @author Mikko Hilpinen
 * @since 5.6.2016
 */
public class VisionValueParser implements ValueParser
{
	// ATTRIBUTES	---------------
	
	private static VisionValueParser instance = null;
	private List<Conversion> conversions = new ArrayList<>();
	
	
	// CONSTRUCTOR	--------------
	
	private VisionValueParser()
	{
		// TODO Add conversions
	}
	
	/**
	 * @return The static parser instance
	 */
	public static VisionValueParser getInstance()
	{
		if (instance == null)
			instance = new VisionValueParser();
		return instance;
	}
	
	
	// IMPLEMENTED METHODS	------

	@Override
	public Value cast(Value value, DataType to) throws ValueParseException
	{
		// Can cast between model type and vision resource types. Casting from model can 
		// always fail.
		DataType from = value.getType();
		
		if (from.equals(VisionDataType.SPRITE))
		{
			if (to.equals(BasicDataType.MODEL))
			{
				Sprite sprite = VisionDataType.valueToSprite(value);
				Model<Variable> model = Model.createBasicModel();
				model.setAttributeValue("file", Value.String(sprite.getSourceFile().getPath()));
				model.setAttributeValue("length", Value.Integer(sprite.getLength()));
				model.setAttributeValue("origin", GenesisDataType.Vector(sprite.getOrigin()));
				model.setAttributeValue("size", GenesisDataType.Vector(sprite.getSize()));
				model.setAttributeValue("sharpness", Value.Integer(sprite.getSharpness()));
				model.setAttributeValue("luminosity", Value.Double((double) sprite.getLuminosity()));
				
				return Value.Model(model);
			}
		}
		else if (from.equals(VisionDataType.TILE))
		{
			if (to.equals(BasicDataType.MODEL))
			{
				Tile tile = VisionDataType.valueToTile(value);
				Model<Variable> model = Model.createBasicModel();
				model.setAttributeValue("bankName", Value.String(tile.getSpriteBankName()));
				model.setAttributeValue("spriteName", Value.String(tile.getSpriteName()));
				model.setAttributeValue("size", GenesisDataType.Vector(tile.getSize()));
				model.setAttributeValue("animationSpeed", Value.Double(tile.getAnimationSpeed()));
				model.setAttributeValue("startFrameIndex", Value.Integer(tile.getStartFrameIndex()));
				
				return Value.Model(model);
			}
		}
		else if (from.equals(BasicDataType.MODEL))
		{
			if (to.equals(VisionDataType.SPRITE))
			{
				Model<?> model = value.toModel();
				
				if (!model.containsAttribute("file"))
					throw new ValueParseException(value, to); // TODO: Could add an exception message
				
				File sourceFile = new File(model.getAttributeValue("file").toString());
				
				int length = 1;
				if (model.containsAttribute("length"))
					length = model.getAttributeValue("length").toInteger();
				
				Vector3D origin;
				if (model.containsAttribute("origin"))
					origin = GenesisDataType.valueToVector(model.getAttributeValue("origin"));
				else
					origin = Vector3D.ZERO;
				
				Vector3D size = null;
				if (model.containsAttribute("size"))
					size = GenesisDataType.valueToVector(model.getAttributeValue("size"));
				
				int sharpness = 0;
				if (model.containsAttribute("sharpness"))
					sharpness = model.getAttributeValue("sharpness").toInteger();
				
				float luminosity = 0;
				if (model.containsAttribute("luminosity"))
					luminosity = model.getAttributeValue("luminosity").toDouble().floatValue();
				
				try
				{
					return VisionDataType.Sprite(new Sprite(
							sourceFile, length, origin, size, sharpness, luminosity));
				}
				catch (IOException e)
				{
					throw new ValueParseException(value.getObjectValue(), from, to, e);
				}
			}
		}
		
		return null;
	}

	@Override
	public Collection<? extends Conversion> getConversions()
	{
		return this.conversions;
	}
}
