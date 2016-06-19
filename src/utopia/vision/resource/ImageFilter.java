package utopia.vision.resource;

import java.awt.image.BufferedImage;

/**
 * Image filters can be applied to images in order to change them somehow
 * @author Mikko Hilpinen
 * @since 18.6.2016
 */
public interface ImageFilter
{
	/**
	 * Applies the filter on an image. The original image should be modified but a new image 
	 * should be produced instead.
	 * @param image An image the filter is applied over
	 * @return The filtered image
	 */
	public BufferedImage filter(BufferedImage image);
}
