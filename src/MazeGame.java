import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MazeGame extends JPanel implements EventListener{

	private static final long serialVersionUID = 3896314035336100692L;
	
	private static int SCREEN_W = 800;
	private static int SCREEN_H = 600;
	
	private static Player player;
	private static int ROWS = -1;
	private static int COLUMNS = -1;
	
	private static final int BLOCKSIZE = 32;
	private boolean loading = true;
	
	private static HashMap<GridType, GridBlocks> gridLayers = new HashMap<>();
	private static GridBlocks floorGrid;
	private static GridBlocks wallsGrid;
	private static GridBlocks trapsGrid;
	private static GridBlocks bonusGrid;
	private static GridBlocks monsterGrid;
	private static GridBlocks playersGrid;
	
	private static Image floorImage;
	private static Image playerImage;
	private static Image wallImage;
	private static Image fireImage;
	private static Image treasureImage;
	private static Image heartImage;
	private static Image spiderImage;
	
	MazeGame() {
		loading = true;
		loadMap(getClass().getClassLoader().getResource("").getPath());
		loading = false;
		
		SCREEN_W = COLUMNS*BLOCKSIZE;
		SCREEN_H = ROWS*BLOCKSIZE+20;
		setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
		
		JFrame frame = new JFrame("Maze Game");
		
		this.addKeyListener(new KeyHandler());
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setFocusable(true);
		frame.pack();
		frame.setVisible(true);
		
		gameLoop();
	}
	
	private void loadMap(String path) {
	
		floorImage = new ImageIcon(path+"images/floorImage.png").getImage();
		playerImage = new ImageIcon(path+"images/playerImage.png").getImage();
		wallImage = new ImageIcon(path+"images/wallImage.png").getImage();
		fireImage = new ImageIcon(path+"images/fireImageSheet.png").getImage();
		treasureImage = new ImageIcon(path+"images/treasureImage.png").getImage();
		heartImage = new ImageIcon(path+"images/heartImageSheet.png").getImage();
		spiderImage = new ImageIcon(path+"images/spiderImage.png").getImage();
		
		String name = JOptionPane.showInputDialog("Map Name: "); 
		
		try {
			FileInputStream fstream = new FileInputStream(path+"maps/"+name+".cyan");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String line;
			int row = 0;
			while((line = br.readLine()) != null) {
				if(row == 0) {
					ROWS = Integer.parseInt(line);
				}
				else if(row == 1) {
					COLUMNS = Integer.parseInt(line);

					floorGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
					wallsGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
					trapsGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
					bonusGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
					monsterGrid = new GridDynamicBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
					playersGrid = new GridDynamicBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
				}
				else {
					for(int i = 0; i < line.length(); i ++) {
						String c = line.substring(i, i+1);
						if(c.equals("1")) wallsGrid.addBlock(new WallBlock(row-2, i, wallImage, this));
						if(c.equals("2")) trapsGrid.addBlock(new Lava(row-2, i, fireImage, this));
						if(c.equals("3")) player = new Player(row-2, i, playerImage, this);
						if(c.equals("4")) bonusGrid.addBlock(new Treasure(row-2, i, treasureImage, this));
						if(c.equals("5")) monsterGrid.addBlock(new Spider(row-2, i, spiderImage, this));
						if(c.equals("6")) bonusGrid.addBlock(new Heart(row-2, i, heartImage, this));
					}
				}
				row += 1;
			}	
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error loading map.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				floorGrid.addBlock(new BasicBlock(i, j, floorImage, this));	
			}
		}
		playersGrid.addBlock(player);
		
		gridLayers.put(GridType.FLOOR, floorGrid);
		gridLayers.put(GridType.WALLS, wallsGrid);
		gridLayers.put(GridType.TRAPS, trapsGrid);
		gridLayers.put(GridType.BONUS, bonusGrid);
		gridLayers.put(GridType.MONSTERS, monsterGrid);
		gridLayers.put(GridType.PLAYERS, playersGrid);
	}
	
	private void gameLoop() {
		setBackground(Color.BLACK);
		
		Event ev = new Event(EventType.SPAWN);
		try { player.trigger(ev, MazeGame.this); } catch (PreventDefaultException e1) {}
		
		while(true) {

			if(!isFocusOwner()) requestFocusInWindow();
			
		    synchronized(this){

				try {
					gridLayers.get(GridType.TRAPS).trigger(new Event(EventType.TICK), this);
					gridLayers.get(GridType.BONUS).trigger(new Event(EventType.TICK), this);
					gridLayers.get(GridType.PLAYERS).trigger(new Event(EventType.TICK), this);
					gridLayers.get(GridType.MONSTERS).trigger(new Event(EventType.TICK), this);
				} catch (PreventDefaultException e) {}
			    
			    
				repaint();
		    }
		}
	}
	
	int x = 0;
	public void paint(Graphics g) {
		BufferedImage image = new BufferedImage(SCREEN_W, SCREEN_H, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		super.paint(g2d);

		if(loading) return;

	    synchronized(this){
			g2d.drawImage(gridLayers.get(GridType.FLOOR).getImage(), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.WALLS).getImage(), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.TRAPS).getImage(), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.BONUS).getImage(), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.MONSTERS).getImage(), 0, 0, null);
			g2d.drawImage(gridLayers.get(GridType.PLAYERS).getImage(), 0, 0, null);
	    }
		g2d.setColor(Color.GRAY);
		g2d.drawLine(0, SCREEN_H-20, SCREEN_W, SCREEN_H-20);
		
		g2d.drawImage(heartImage, 2, SCREEN_H-18, null);
		
		g2d.setColor(Color.RED);
		g2d.fill(new Rectangle2D.Double(20, SCREEN_H-15, 100, 12));
		g2d.setColor(Color.GREEN);
		g2d.fill(new Rectangle2D.Double(20, SCREEN_H-15, 100*(player.getHP()/player.getMaxHP()), 12));
		
		g2d.setColor(Color.BLACK);
		g2d.drawString((int)player.getHP()+"/"+(int)player.getMaxHP(), 45, SCREEN_H-5);
		
		Graphics2D g2d_final = (Graphics2D) g;
		g2d_final.drawImage(image, 0, 0, null);
	}
	
	@Override
	public void trigger(Event event, EventListener sender)  throws PreventDefaultException{
		for(GridType type : gridLayers.keySet()){ 
			if(event.type == EventType.DESTROY_ME) {
				if(event.args.get("type") == type) gridLayers.get(type).trigger(event, sender);
			}
			else gridLayers.get(type).trigger(event, sender);
		}
	}
	
	private class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			try {
				if(e.getKeyCode() == KeyEvent.VK_LEFT) {
					Event ev = new Event(EventType.MOVE_LEFT);
					player.trigger(ev, MazeGame.this);
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
					Event ev = new Event(EventType.MOVE_RIGHT);
					player.trigger(ev, MazeGame.this);
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) {
					Event ev = new Event(EventType.MOVE_UP);
					player.trigger(ev, MazeGame.this);
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					Event ev = new Event(EventType.MOVE_DOWN);
					player.trigger(ev, MazeGame.this);
				} 
			} catch (PreventDefaultException ex) {
			}

		}

		public void keyReleased(KeyEvent e) {
			
			if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				
			}
		}
	}
	
	public static void main(String[] args) {
		new MazeGame();
	}
}
