package utopia.vision.test;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ColorModelTest extends JLabel
	implements MouseMotionListener {

	private BufferedImage image = createImage();
	
	public ColorModelTest() {
		setIcon(new ImageIcon(image));
		
		addMouseMotionListener(this);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		// here we want to change the color model
		// to do that we create a new image using a new color model
		// nb. there is no need to change the source pixels
		
		image = new BufferedImage(createColorModel(e.getX()), 
				image.getRaster(), false, null);
		setIcon(new ImageIcon(image));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	private static BufferedImage createImage() {

		int width = 200;
		int height = 200;
		
		// Generate the source pixels for our image
		// Lets just keep it to a simple blank image for now
		
		byte[] pixels = new byte[width * height];
		DataBuffer dataBuffer = new DataBufferByte(pixels, width*height, 0);
		SampleModel sampleModel = new SinglePixelPackedSampleModel(
        	DataBuffer.TYPE_BYTE, width, height, new int[] {(byte)0xf});
		WritableRaster raster = Raster.createWritableRaster(
			sampleModel, dataBuffer, null);

		return new BufferedImage(createColorModel(0), raster, false, null);
	}

	private static ColorModel createColorModel(int n) {

		// Create a simple color model with all values mapping to
		// a single shade of gray
		// nb. this could be improved by reusing the byte arrays
		
		byte[] r = new byte[16];
		byte[] g = new byte[16];
		byte[] b = new byte[16];

		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) n;
			g[i] = (byte) n;
			b[i] = (byte) n;
		}
		return new IndexColorModel(4, 16, r, g, b);
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new ColorModelTest());
		frame.pack();
		frame.setVisible(true);
	}
}