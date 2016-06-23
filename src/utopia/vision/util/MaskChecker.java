package utopia.vision.util;

import java.awt.Color;

import utopia.genesis.util.HelpMath;
import utopia.genesis.util.Vector3D;
import utopia.vision.resource.Sprite;
import utopia.vision.resource.SpriteDrawer;

/**
 * This is a static interface for checking pixel data of a sprite, which can be used in masks, 
 * collision checking, etc.
 * @author Mikko Hilpinen
 * @since 23.6.2016
 */
public class MaskChecker
{
	// CONSTRUCTOR	-----------------
	
	private MaskChecker()
	{
		// Static interface
	}

	
	// OTHER METHODS	-------------
	
	/**
	 * Checks whether the pixel in the sprite has the provided colour. Alpha channel isn't 
	 * taken into account, neither are any filters.
	 * @param drawer A sprite drawer
	 * @param position The checked position
	 * @param colour The colour that is checked against
	 * @return Does the sprite's pixel match the colour
	 */
	public static boolean matchesColour(SpriteDrawer drawer, Vector3D position, Color colour)
	{
		return matchesColour(drawer.getSprite(), position, drawer.getOrigin(), 
				drawer.getFrameIndex(), colour);
	}
	
	/**
	 * Checks whether the pixel in the sprite has the provided colour. Alpha channel isn't 
	 * taken into account
	 * @param sprite a sprite
	 * @param position The checked position
	 * @param origin The origin of the sprite / object
	 * @param frameIndex The index of the used frame
	 * @param colour The colour that is checked against
	 * @return Does the sprite's pixel match the colour
	 */
	public static boolean matchesColour(Sprite sprite, Vector3D position, Vector3D origin, 
			int frameIndex, Color colour)
	{
		int rgb = getRGB(sprite, position, origin, frameIndex);
		if (RGBAColours.red(rgb) != colour.getRed())
			return false;
		if (RGBAColours.green(rgb) != colour.getGreen())
			return false;
		if (RGBAColours.blue(rgb) != colour.getBlue())
			return false;
		
		return true;
	}
	
	/**
	 * Finds the alpha component of a single pixel in a sprite
	 * @param drawer A sprite drawer
	 * @param position The checked position
	 * @return The alpha component at that position. Between 0 and 255.
	 */
	public static int getAlpha(SpriteDrawer drawer, Vector3D position)
	{
		return RGBAColours.alpha(getRGB(drawer, position));
	}
	
	/**
	 * Finds the alpha component of a single pixel in a sprite
	 * @param sprite A sprite
	 * @param position The checked position
	 * @param origin The origin of the sprite / object
	 * @param frameIndex The index of the frame that is checked
	 * @return The alpha component at that position. Between 0 and 255.
	 */
	public static int getAlpha(Sprite sprite, Vector3D position, Vector3D origin, int frameIndex)
	{
		return RGBAColours.alpha(getRGB(sprite, position, origin, frameIndex));
	}
	
	/**
	 * Finds the rgba value of a single pixel in a sprite
	 * @param drawer A sprite drawer
	 * @param position The position of the pixel
	 * @return The rgba value of the pixel in the sprite.
	 */
	public static int getRGB(SpriteDrawer drawer, Vector3D position)
	{
		return getRGB(drawer.getSprite(), position, drawer.getOrigin(), drawer.getFrameIndex());
	}
	
	/**
	 * Finds the rgba value of a single pixel in a sprite
	 * @param sprite a sprite
	 * @param position The position of the pixel
	 * @param origin The origin of the sprite / object
	 * @param frameIndex The index of the used frame
	 * @return The rgba value of the pixel in the sprite.
	 */
	public static int getRGB(Sprite sprite, Vector3D position, Vector3D origin, int frameIndex)
	{
		Vector3D inSpritePosition = position.plus(origin);
		
		// The position must be inside the sprite
		if (!HelpMath.pointIsInRange(position, Vector3D.ZERO, sprite.getSize()))
			return RGBAColours.rgba(0, 0, 0, 0);
		
		// Subtracts the scaling as well
		inSpritePosition = inSpritePosition.dividedBy(sprite.getScaling());
		
		return sprite.getFrame(frameIndex).getRGB(inSpritePosition.getXInt(), inSpritePosition.getYInt());
	}
}
