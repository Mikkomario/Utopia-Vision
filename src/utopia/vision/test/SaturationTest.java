package utopia.vision.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Ryan
 * @see <a href="http://stackoverflow.com/questions/16502984/adjust-image-saturation-by-color-type">Original code</a>
 */
public class SaturationTest extends JFrame{

	private static final long serialVersionUID = 3441110630223140653L;
	public Layer layer;

    public SaturationTest(){
    try{
            this.setSize(800, 600);
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            layer = new Layer();
            layer.setImage("http://www.prikol.ru/wp-content/gallery/october-2012/datacenter-01.jpg");
            this.add(layer);
            this.revalidate();
            Timer timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    applySat();
                }
            }, 500);
        }catch(IOException ex){
            Logger.getLogger(SaturationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void applySat(){
        Saturation sat = new Saturation();
        sat.setAmount(-100);
        BufferedImage img = this.layer.getImage();
        int[] pixels = new int[img.getWidth() * img.getHeight()];
        this.layer.getImage().getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
        sat.setPixels(pixels);
        pixels = sat.filter("yellow");
        img.setRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
        this.layer.setImage(img);
    }
    public static void main(String[] args){
        new SaturationTest();
    }
}


class Layer extends JPanel{

    protected BufferedImage image;

    public BufferedImage getImage(){
        return this.image;
    }

    public void setImage(BufferedImage image){
        this.image = image;
        this.repaint();
    }

    public void setImage(String filename) throws IOException{
        this.image = ImageIO.read(new URL(filename));
        this.setSize(this.image.getWidth(), this.image.getHeight());
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(this.image, 0, 0, this.image.getWidth(), this.image.getHeight(), Color.black, null);
    }
}



class Saturation{

    protected volatile int[] pixels;
    protected int adjustmentAmount = 0;

    public void setPixels(int[] pixels){
        this.pixels = pixels;
    }

    public void setAmount(int amount){
        this.adjustmentAmount = amount;
    }

    public int[] filter(String keep){
        for(int i = 0; i < this.pixels.length; i++){
            int pixel = this.pixels[i];
            int red = Colors.red(pixel);
            int green = Colors.green(pixel);
            int blue = Colors.blue(pixel);

            // TODO: May use an if here to only affect certain colours
            float[] hsv = new float[3];
            Color.RGBtoHSB(red, green, blue, hsv);
            hsv[1] += (float)(this.adjustmentAmount * 0.01);
            if(hsv[1] > 1){
                hsv[1] = 1;
            }else if(hsv[1] < 0){
                hsv[1] = 0;
            }

            // TODO: Colour switching is also a thing
            int newpixel = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
            red = Colors.red(newpixel);
            green = Colors.green(newpixel);
            blue = Colors.blue(newpixel);
            
            this.pixels[i] = Colors.rgba(red, green, blue);
        }
        return this.pixels;
    }
}


class Colors{

    public static int rgba(int red, int green, int blue, Integer alpha){
        int rgba = alpha;
        rgba = (rgba << 8) + red;
        rgba = (rgba << 8) + green;
        rgba = (rgba << 8) + blue;
        return rgba;
    }

    public static int rgba(int red, int green, int blue){
        int rgba = 255;
        rgba = (rgba << 8) + red;
        rgba = (rgba << 8) + green;
        rgba = (rgba << 8) + blue;
        return rgba;
    }
    public static int[] getrgba(int color){
        int[] colors = new int[3];
        colors[0] = Colors.red(color);
        colors[1] = Colors.green(color);
        colors[2] = Colors.blue(color);
        return colors;
    }

    public static int alpha(int color){
        return color >> 24 & 0x0FF;
    }

    public static int red(int color){
        return color >> 16 & 0x0FF;
    }

    public static int green(int color){
        return color >> 8 & 0x0FF;
    }

    public static int blue(int color){
        return color & 0x0FF;
    }
}