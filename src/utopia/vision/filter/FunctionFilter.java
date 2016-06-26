package utopia.vision.filter;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;

/**
 * A function filter applies a function over each pixel in an image, increasing or decreasing 
 * the different channel values. This can be used for increasing contrast or inverting images, 
 * for example
 * @author Mikko Hilpinen
 * @since 20.6.2016
 */
public class FunctionFilter implements ImageFilter
{
	// ATTRIBUTES	----------------
	
	private LookupOp operation;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new filter. The provided function will be applied to all colours but not alpha.
	 * @param function A function affecting the image colours
	 */
	public FunctionFilter(Function function)
	{
		this.operation = createOperation(function, function, function, null);
	}
	
	/**
	 * Creates a new filter
	 * @param redFunction The function applied to the red colour. Null if the colour shouldn't be affected.
	 * @param greenFunction The function applied to green colour. Null if the colour shouldn't be affected.
	 * @param blueFunction The function applied to blue colour. Null if the colour shouldn't be affected.
	 */
	public FunctionFilter(Function redFunction, Function greenFunction, Function blueFunction)
	{
		this.operation = createOperation(redFunction, greenFunction, blueFunction, null);
	}
	
	/**
	 * Creates a new filter
	 * @param redFunction The function applied to the red colour. Null if the colour shouldn't be affected.
	 * @param greenFunction The function applied to green colour. Null if the colour shouldn't be affected.
	 * @param blueFunction The function applied to blue colour. Null if the colour shouldn't be affected.
	 * @param alphaFunction The function applied to alpha channel. Null if the alpha shouldn't be affected.
	 */
	public FunctionFilter(Function redFunction, Function greenFunction, Function blueFunction, 
			Function alphaFunction)
	{
		this.operation = createOperation(redFunction, greenFunction, blueFunction, 
				alphaFunction);
	}
	
	/**
	 * @return A filter that inverts colours
	 */
	public static FunctionFilter invert()
	{
		return new FunctionFilter(new InvertFunction());
	}
	
	/**
	 * @return A filter that increases contrast
	 */
	public static FunctionFilter increaseContrast()
	{
		return new FunctionFilter(new SFunction());
	}
	
	
	// IMPLEMENTED METHODS	--------

	@Override
	public BufferedImage filter(BufferedImage image)
	{
		BufferedImage destinationImage = new BufferedImage(image.getWidth(), image.getHeight(), 
				BufferedImage.TYPE_INT_ARGB);
		return this.operation.filter(image, destinationImage);
	}
	
	
	// OTHER METHODS	------------
	
	private static LookupOp createOperation(Function rFunction, Function gFunction, 
			Function bFunction, Function alphaFunction)
	{
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];
		short[] alpha = new short[256];
		
		for (short i = 0; i < 256; i++)
		{
			red[i] = getFunctionValue(rFunction, i);
			green[i] = getFunctionValue(gFunction, i);
			blue[i] = getFunctionValue(bFunction, i);
			alpha[i] = getFunctionValue(alphaFunction, i);
		}
		
		short[][] data = new short[][] {red, green, blue, alpha};
		
		return new LookupOp(new ShortLookupTable(0, data), null);
	}
	
	private static short getFunctionValue(Function f, short i)
	{
		if (f == null)
			return i;
		
		short value = f.getValue(i);
		if (value < 0)
			return 0;
		else if (value > 255)
			return 255;
		else
			return value;
	}
	
	
	// INTERFACES	----------------
	
	/**
	 * Functions may be used when generating filters
	 * @author Mikko Hilpinen
	 * @since 20.6.2016
	 */
	public static interface Function
	{
		/**
		 * Returns the function value
		 * @param i An index that runs from 0 to 255
		 * @return The value of the function. Usable values are between 0 and 255.
		 */
		public short getValue(short i);
	}
	
	/**
	 * This function inverts the colour value
	 * @author Mikko Hilpinen
	 * @since 20.6.2016
	 */
	public static class InvertFunction implements Function
	{
		@Override
		public short getValue(short i)
		{
			return (short) (255 - i);
		}
	}
	
	/**
	 * This function decreases small values and increases large ones. May be used for 
	 * altering contrast, for example
	 * @author Mikko Hilpinen
	 * @since 20.6.2016
	 */
	public static class SFunction implements Function
	{
		@Override
		public short getValue(short i)
		{
			double x = i * 2 / 255.0 - 1; // [-1, 1]
			double y = Math.cbrt(x);
			double range = 255 / 2.0;
			
			return (short) (range + range * y);
		}
	}
	
	/**
	 * This function limits values to certain tresholds
	 * @author Mikko Hilpinen
	 * @since 26.6.2016
	 */
	public static class TresholdFunction implements Function
	{
		// ATTRIBUTES	---------------
		
		private short[] tresholds;
		
		
		// CONSTRUCTOR	---------------
		
		/**
		 * Creates a new function
		 * @param colourAmount The amount of different colours (tresholds) to be used
		 */
		public TresholdFunction(int colourAmount)
		{
			this.tresholds = new short[colourAmount];
			for (short i = 0; i < colourAmount; i++)
			{
				this.tresholds[i] = (short) (i * 255 / (colourAmount - 1));
			}
		}
		
		
		// IMPLEMENTED METHODS	------
		
		@Override
		public short getValue(short i)
		{
			int index = i * this.tresholds.length / 256;
			return this.tresholds[index];
		}
	}
}
