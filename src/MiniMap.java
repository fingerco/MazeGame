import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

// TBD, add mouse listener
public class MiniMap implements Runnable {

	private HashMap<GridType, GridBlocks> gridLayers = new HashMap<>();
	
	private int MAP_W = 160;
	private int MAP_H = 125;
	
	private int x = 0;
	private int y = 0;

	private BufferedImage img = new BufferedImage(MAP_W, MAP_H, BufferedImage.TYPE_INT_ARGB);
	
	MiniMap(HashMap<GridType, GridBlocks> gridLayers) {
		this(gridLayers, 0, 0);
	}
	
	MiniMap(HashMap<GridType, GridBlocks> gridLayers, int x, int y) {
		this.gridLayers = gridLayers;
		
		this.x = x;
		this.y = y;
		
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		while(true) { 
			BufferedImage buff = new BufferedImage(MAP_W, MAP_H, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) buff.getGraphics();

			
			synchronized (this) {
				g2d.setColor(new Color(0, 0, 0, 128));
				g2d.fill(new Rectangle(0, 0, MAP_W-1, MAP_H-1));
				
				g2d.drawImage(gridLayers.get(GridType.FLOOR).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
				g2d.drawImage(gridLayers.get(GridType.WALLS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
				g2d.drawImage(gridLayers.get(GridType.PLAYERS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			}
			g2d.setColor(Color.BLUE);
			g2d.drawRect(0, 0, MAP_W-1, MAP_H-1);
			
			img = buff;
		}
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
