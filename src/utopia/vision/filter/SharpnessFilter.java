package utopia.vision.filter;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * This filter modifies the image sharpness, either increasing or decreasing it
 * @author Mikko Hilpinen
 * @since 20.6.2016
 */
public class SharpnessFilter implements ImageFilter
{
	// ATTRIBUTES	-----------------
	
	private static final float[] SHARPEN = new float[]
	{
	     0.0f, -1.0f, 0.0f,
	    -1.0f, 5.0f, -1.0f,
	     0.0f, -1.0f, 0.0f
	};
	private static final float[] BLUR = new float[]
	{
		0.05f, 0.15f, 0.05f,
		0.15f, 0.2f, 0.15f, 
		0.05f, 0.15f, 0.05f
	};
	
	private boolean isSharpen = true;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new filter that sharpens the image
	 */
	public SharpnessFilter()
	{
		// Simple constructor
	}
	
	/**
	 * Creates a new filter that sharpens or blurs the image
	 * @param sharpen Whether the filter should sharpen (true) or blur (false) the image
	 */
	public SharpnessFilter(boolean sharpen)
	{
		this.isSharpen = sharpen;
	}
	
	
	// IMPLEMENTED METHODS	-------

	@Override
	public BufferedImage filter(BufferedImage image)
	{
		return convolve(image, this.isSharpen ? SHARPEN : BLUR, 3, 3);
	}
	
	
	// ACCESSORS	---------------
	
	/**
	 * @return Whether this filter sharpens (true) or blurs (false) the targeted image
	 */
	public boolean isSharpen()
	{
		return this.isSharpen;
	}

	
	// OTHER METHODS	-----------
	
	private static BufferedImage convolve(BufferedImage image, float[] data, int kernelWidth, 
			int kernelHeight)
	{
		// Creates the operation
		ConvolveOp op = new ConvolveOp(new Kernel(kernelWidth, kernelHeight, data), 
				ConvolveOp.EDGE_NO_OP, null);
		return op.filter(image, null);
	}
}
