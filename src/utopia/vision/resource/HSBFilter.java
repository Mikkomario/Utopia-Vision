package utopia.vision.resource;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * HSV filters can be used for altering image hue and saturation. Some parts of the code 
 * reference 
 * <a href="http://stackoverflow.com/questions/16502984/adjust-image-saturation-by-color-type">Ryan's code</a>
 * @author Mikko Hilpinen
 * @since 21.6.2016
 */
public class HSBFilter implements ImageFilter
{
	// ATTRIBUTES	----------------
	
	private double hueAdjustment;
	private double saturationAdjustment;
	private ColourTarget target = null;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new filter
	 * @param hueAdjustment How much the hue is adjusted
	 * @param saturationAdjustment How much the saturation is adjusted
	 */
	public HSBFilter(double hueAdjustment, double saturationAdjustment)
	{
		this.hueAdjustment = hueAdjustment;
		this.saturationAdjustment = saturationAdjustment;
	}
	
	/**
	 * Creates a new filter
	 * @param hueAdjustment How much the hue is adjusted
	 * @param saturationAdjustment How much the saturation is adjusted
	 * @param target The colour range targeted by the filter
	 */
	public HSBFilter(double hueAdjustment, double saturationAdjustment, ColourTarget target)
	{
		this.hueAdjustment = hueAdjustment;
		this.saturationAdjustment = saturationAdjustment;
		this.target = target;
	}
	
	
	// IMPLEMENTED METHODS	--------

	@Override
	public BufferedImage filter(BufferedImage image)
	{
		// Reads the image into a pixel array
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        
        // Alters the pixels
		for(int i = 0; i < pixels.length; i++)
		{
            int pixel = pixels[i];
            int red = Colors.red(pixel);
            int green = Colors.green(pixel);
            int blue = Colors.blue(pixel);
            int alpha = Colors.alpha(pixel);

            double effectModifier = 1;
            if (this.target != null)
            	effectModifier = this.target.getEffectModifier(red, green, blue);
            
            if (effectModifier > 0)
            {
	            float[] hsb = new float[3];
	            Color.RGBtoHSB(red, green, blue, hsb);
	            
	            
	            if (this.hueAdjustment != 0)
	            {
	            	hsb[0] += this.hueAdjustment * effectModifier;
	            	if (hsb[0] > 1)
	            		hsb[0] -= 1;
	            	else if (hsb[0] < 0)
	            		hsb[0] += 1;
	            }
	            if (this.saturationAdjustment != 0)
	            {
		            hsb[1] += this.saturationAdjustment * effectModifier;
		            if(hsb[1] > 1)
		                hsb[1] = 1;
		            else if(hsb[1] < 0)
		                hsb[1] = 0;
	            }
	
	            int newpixel = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
	            red = Colors.red(newpixel);
	            green = Colors.green(newpixel);
	            blue = Colors.blue(newpixel);
	            
	            pixels[i] = Colors.rgba(red, green, blue, alpha);
            }
            else
            	pixels[i] = pixel;
            
        }
		
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), 
				BufferedImage.TYPE_INT_ARGB);
		newImage.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		return newImage;
	}

	
	// NESTED CLASSES	-------------
	
	/**
	 * Colour targets are used when targeting certain colour ranges
	 * @author Mikko Hilpinen
	 * @since 21.6.2016
	 */
	public static class ColourTarget
	{
		// ATTRIBUTES	-------------
		
		private int red, green, blue, range;
		private boolean inclusive;
		
		
		// CONSTRUCTOR	-------------
		
		/**
		 * Creates a new colour target
		 * @param colour The targeted colour
		 * @param range The included range
		 * @param inclusive Should the colour be included (true) or excluded (false). 
		 * If excluded, all other colours will be targeted.
		 */
		public ColourTarget(Color colour, int range, boolean inclusive)
		{
			this.red = colour.getRed();
			this.green = colour.getGreen();
			this.blue = colour.getBlue();
			this.range = range;
			this.inclusive = inclusive;
		}
		
		
		// OTHER METHODS	----------
		
		/**
		 * Calculates the effect strength that should be applied to a colour. If inclusive, 
		 * full effect will be applied to the targeted colour. If exclusive, full effect will 
		 * be applied to colour outside the targeted range.
		 * @param red The red component of a colour
		 * @param green The green component of a colour
		 * @param blue The blue component of a colour
		 * @return The effect modifier for that colour [0, 1]
		 */
		public double getEffectModifier(int red, int green, int blue)
		{
			// The effect is stronger the closer the colour is
			int difference = getDifference(red, green, blue);
			if (difference > this.range)
				return 0;
			
			// Uses a sin function for the effectiveness
			// (sin(2 * (x * 1.6) - 1.6)  + 1) / 2
			double x = difference / (double) this.range;
			double modifier = 0.5 * (Math.sin(x * Math.PI - Math.PI / 2) + 1);
			if (this.inclusive)
				modifier = 1 - modifier;
			
			return modifier;
		}
		
		private int getDifference(int red, int green, int blue)
		{
			return Math.abs(this.red - red) + Math.abs(this.green - green) + 
					Math.abs(this.blue - blue);
		}
	}
	
	/**
	 * @author Ryan
	 */
	private static class Colors
	{
	    public static int rgba(int red, int green, int blue, int alpha)
	    {
	        int rgba = alpha;
	        rgba = (rgba << 8) + red;
	        rgba = (rgba << 8) + green;
	        rgba = (rgba << 8) + blue;
	        return rgba;
	    }

	    /*
	    public static int rgba(int red, int green, int blue)
	    {
	        int rgba = 255;
	        rgba = (rgba << 8) + red;
	        rgba = (rgba << 8) + green;
	        rgba = (rgba << 8) + blue;
	        return rgba;
	    }
	    
	    public static int[] getrgba(int color)
	    {
	        int[] colors = new int[3];
	        colors[0] = Colors.red(color);
	        colors[1] = Colors.green(color);
	        colors[2] = Colors.blue(color);
	        return colors;
	    }
	    */

	    public static int alpha(int color)
	    {
	        return color >> 24 & 0x0FF;
	    }

	    public static int red(int color)
	    {
	        return color >> 16 & 0x0FF;
	    }

	    public static int green(int color)
	    {
	        return color >> 8 & 0x0FF;
	    }

	    public static int blue(int color)
	    {
	        return color & 0x0FF;
	    }
	}
}
