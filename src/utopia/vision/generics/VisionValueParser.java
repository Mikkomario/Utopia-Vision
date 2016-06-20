package utopia.vision.generics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.Conversion;
import utopia.flow.generics.ConversionReliability;
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
		// Can convert between model and sprite / tile
		// Conversions to models always succeed, but a model may not always represent a correct 
		// entity when converting other way
		this.conversions.add(new Conversion(VisionDataType.SPRITE, BasicDataType.MODEL, 
				ConversionReliability.PERFECT));
		this.conversions.add(new Conversion(VisionDataType.TILE, BasicDataType.MODEL, 
				ConversionReliability.PERFECT));
		
		this.conversions.add(new Conversion(BasicDataType.MODEL, VisionDataType.SPRITE, 
				ConversionReliability.DANGEROUS));
		this.conversions.add(new Conversion(BasicDataType.MODEL, VisionDataType.TILE, 
				ConversionReliability.DANGEROUS));
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
				model.setAttributeValue("animationSpeed", Value.Double(sprite.getDefaultAnimationSpeed()));
				
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
				model.setAttributeValue("startFrameIndex", Value.Integer(tile.getStartFrameIndex()));
				model.setAttributeValue("animated", Value.Boolean(tile.isAnimated()));
				
				return Value.Model(model);
			}
		}
		else if (from.equals(BasicDataType.MODEL))
		{
			if (to.equals(VisionDataType.SPRITE))
			{
				Model<?> model = value.toModel();
				checkThatModelContains(value, to, model, "file");
				
				File sourceFile = new File(model.getAttributeValue("file").toString());
				
				int length = getInteger(model, "length", 1);
				Vector3D origin = getVector(model, "origin", Vector3D.ZERO);
				Vector3D size = getVector(model, "size", null);
				double animationSpeed = getDouble(model, "animationSpeed", 0.1);
				
				try
				{
					return VisionDataType.Sprite(new Sprite(sourceFile, length, origin, size, 
							animationSpeed));
				}
				catch (IOException e)
				{
					throw new ValueParseException(value.getObjectValue(), from, to, e);
				}
			}
			else if (to.equals(VisionDataType.TILE))
			{
				Model<?> model = value.toModel();
				checkThatModelContains(value, to, model, "bankName", "spriteName", "size");
				
				String bankName = model.getAttributeValue("bankName").toString();
				String spriteName = model.getAttributeValue("spriteName").toString();
				Vector3D size = GenesisDataType.valueToVector(model.getAttributeValue("size"));
				
				int startFrameIndex = getInteger(model, "startFrameIndex", 0);
				boolean animated = getValue(model, "animated", Value.Boolean(true)).toBoolean();
				
				return VisionDataType.Tile(new Tile(bankName, spriteName, size, 
						startFrameIndex, animated));
			}
		}
		
		return null;
	}

	@Override
	public Collection<? extends Conversion> getConversions()
	{
		return this.conversions;
	}
	
	
	// OTHER METHODS	------------
	
	private static void checkThatModelContains(Value from, DataType to, 
			Model<? extends Variable> model, String... requiredAttributes)
	{
		for (String attName : requiredAttributes)
		{
			if (!model.containsAttribute(attName))
				throw new ValueParseException(from, to);
		}
	}
	
	private static Value getValue(Model<? extends Variable> model, String attName, Value defaultValue)
	{
		if (model.containsAttribute(attName))
			return model.getAttributeValue(attName);
		else
			return defaultValue;
	}
	
	private static Vector3D getVector(Model<? extends Variable> model, String attName, Vector3D defaultValue)
	{
		return GenesisDataType.valueToVector(getValue(model, attName, GenesisDataType.Vector(defaultValue)));
	}
	
	private static Integer getInteger(Model<? extends Variable> model, String attName, Integer defaultValue)
	{
		return getValue(model, attName, Value.Integer(defaultValue)).toInteger();
	}
	
	private static Double getDouble(Model<? extends Variable> model, String attName, Double defaultValue)
	{
		return getValue(model, attName, Value.Double(defaultValue)).toDouble();
	}
}
