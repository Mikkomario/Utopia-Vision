package utopia.vision.util;

/**
 * This class works as an interface between rgb values and colours. See also 
 * <a href="http://stackoverflow.com/questions/16502984/adjust-image-saturation-by-color-type">Ryan's original code</a>
 * @author Ryan
 */
public class RGBAColours
{
	/**
	 * Calculates the rgb value of a colour. All component values should be between 0 and 255.
	 * @param red The red component
	 * @param green The green component
	 * @param blue The blue component
	 * @param alpha The alpha component
	 * @return The rgb value
	 */
    public static int rgba(int red, int green, int blue, int alpha)
    {
        int rgba = alpha;
        rgba = (rgba << 8) + red;
        rgba = (rgba << 8) + green;
        rgba = (rgba << 8) + blue;
        return rgba;
    }

    /**
	 * Calculates the rgb value of a colour. All component values should be between 0 and 255.
	 * @param red The red component
	 * @param green The green component
	 * @param blue The blue component
	 * @return The rgb value
	 */
    public static int rgba(int red, int green, int blue)
    {
        int rgba = 255;
        rgba = (rgba << 8) + red;
        rgba = (rgba << 8) + green;
        rgba = (rgba << 8) + blue;
        return rgba;
    }
    
    /**
     * Retrieves the colour components of an rgb value
     * @param color An rgb value
     * @return The colour components of the rgb value. Array contains three elements: 
     * red green and blue (in that order). Each value is between 0 and 255.
     */
    public static int[] getrgba(int color)
    {
        int[] colors = new int[3];
        colors[0] = red(color);
        colors[1] = green(color);
        colors[2] = blue(color);
        return colors;
    }

    /**
     * Retrieves the alpha component of an rgb value
     * @param color an rgb value
     * @return The alpha component of the colour
     */
    public static int alpha(int color)
    {
        return color >> 24 & 0x0FF;
    }

    /**
     * Retrieves the red component of an rgb value
     * @param color an rgb value
     * @return The alpha component of the colour
     */
    public static int red(int color)
    {
        return color >> 16 & 0x0FF;
    }

    /**
     * Retrieves the green component of an rgb value
     * @param color an rgb value
     * @return The alpha component of the colour
     */
    public static int green(int color)
    {
        return color >> 8 & 0x0FF;
    }

    /**
     * Retrieves the blue component of an rgb value
     * @param color an rgb value
     * @return The alpha component of the colour
     */
    public static int blue(int color)
    {
        return color & 0x0FF;
    }
}
