import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;


public class MiniMap implements Runnable {

	private HashMap<GridType, GridBlocks> gridLayers = new HashMap<>();
	
	private int MAP_W = 120;
	private int MAP_H = 120;
	
	private BufferedImage img = new BufferedImage(MAP_W, MAP_H, BufferedImage.TYPE_INT_ARGB);
	
	MiniMap(HashMap<GridType, GridBlocks> gridLayers) {
		this.gridLayers = gridLayers;
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		while(true) { 
			Graphics2D g2d = (Graphics2D) img.getGraphics();
				
			g2d.drawImage(gridLayers.get(GridType.FLOOR).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.WALLS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.TRAPS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.BONUS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.MONSTERS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.PLAYERS).getImage().getScaledInstance(MAP_W, MAP_H, 0), 0, 0, null);
			
			g2d.setColor(Color.RED);
			g2d.drawRect(0, 0, MAP_W-1, MAP_H-1);
		}
	}
	
	public BufferedImage getImage() {
		return img;
	}
}
