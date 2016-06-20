package utopia.vision.resource;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * This filter increases / decreases the luminosity of a sprite
 * @author Mikko Hilpinen
 * @since 20.6.2016
 */
public class LuminosityFilter implements ImageFilter
{
	// ATTRIBUTES	---------------
	
	private static final float[] OFFSETS = {0, 0, 0, 0};
	private RescaleOp operation;
	
	
	// CONSTRUCTOR	---------------
	
	/**
	 * Creates a new filter
	 * @param scale How much the luminosity is scaled, > 0. Value of 1 doesn't alter the 
	 * image in any way, values larger than 1 increase the luminosity, values lower than 1 
	 * decrease the luminosity.
	 */
	public LuminosityFilter(float scale)
	{
		this.operation = new RescaleOp(new float[]{scale, scale, scale, 1f}, OFFSETS, null);
	}
	
	/**
	 * Creates a new filter where scaling is added individually for each colour channel 
	 * (but not alpha channel)
	 * @param redScale The scaling applied to red colour
	 * @param greeScale The scaling applied to green colour
	 * @param blueScale The scaling applied to blue colour
	 */
	public LuminosityFilter(float redScale, float greeScale, float blueScale)
	{
		this.operation = new RescaleOp(new float[] {redScale, greeScale, blueScale, 1f}, OFFSETS, null);
	}
	
	
	// IMPLEMENTED METHODS	-------

	@Override
	public BufferedImage filter(BufferedImage image)
	{
		return this.operation.filter(image, null);
	}
}
