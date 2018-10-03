import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class JapCharac {

	String drawing;
	String romaji;
	
	public JapCharac (String uni, String sound) {
		drawing= uni;
		romaji= sound;
	}
	
	public BufferedImage dispInfo (int size) {
		BufferedImage out= new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g= out.getGraphics();
		g.setColor(new Color(255, 102, 102));
		g.fillRect(0, 0, size, size);
		g.drawString(drawing, 0, 0);
		
		return out;
	}
	
	public String toString () {
		return drawing+" "+romaji;
	}
	
}