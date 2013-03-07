import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class MiniMap implements Runnable {

	private HashMap<GridType, GridBlocks> gridLayers = new HashMap<>();
	
	private int MAP_W = 160;
	private int MAP_H = 125;
	
	private BufferedImage img = new BufferedImage(MAP_W, MAP_H, BufferedImage.TYPE_INT_ARGB);
	
	MiniMap(HashMap<GridType, GridBlocks> gridLayers) {
		this.gridLayers = gridLayers;
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		while(true) { 
			BufferedImage buff = new BufferedImage(MAP_W, MAP_H, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) buff.getGraphics();
			
			g2d.drawImage(gridLayers.get(GridType.FLOOR).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.WALLS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.PLAYERS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			
			g2d.setColor(Color.BLUE);
			g2d.drawRect(0, 0, MAP_W-1, MAP_H-1);
			
			Graphics2D g2d_final = (Graphics2D) img.getGraphics();
			g2d_final.drawImage(buff, 0, 0, null);
		}
	}
	
	public BufferedImage getImage() {
		return img;
	}
}
