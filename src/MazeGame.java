import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
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
	
	private static final int SCREEN_W = 800;
	private static final int SCREEN_H = 600;
	
	private static int paintSleepTime = 25;

	private static Player player;
	
	private static int ROWS = -1;
	private static int COLUMNS = -1;
	
	private static final int BLOCKSIZE = 16;
	private boolean loading = true;
	
	private static HashMap<GridType, GridBlocks> gridLayers = new HashMap<>();
	private static GridBlocks floorGrid;
	private static GridBlocks wallsGrid;
	private static GridBlocks trapsGrid;
	private static GridBlocks playersGrid;
	
	MazeGame() {
		loading = true;
		loadMap("C:\\Users\\ryan.brown\\Desktop\\LearningJava\\MazeGame");
		loading = false;
		
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

		String name = JOptionPane.showInputDialog("Map Name: "); 
		
		Image basicBlockImage = new ImageIcon(path+"\\images\\defaultBlock.png").getImage();
		Image playerBlockImage = new ImageIcon(path+"\\images\\player.png").getImage();
		Image wallBlockImage = new ImageIcon(path+"\\images\\wallBlock.png").getImage();
		Image deathBlockImage = new ImageIcon(path+"\\images\\deathBlock.png").getImage();
		try {
			FileInputStream fstream = new FileInputStream(path+"\\maps\\"+name+".cyan");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line;
			int row = 0;
			while((line = br.readLine()) != null) {
				if(COLUMNS == -1) COLUMNS = line.length();
				row += 1;
			}
			ROWS = row;	
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error loading map.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		floorGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
		wallsGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
		trapsGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
		playersGrid = new GridDynamicBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
		
		try {
			FileInputStream fstream = new FileInputStream(path+"\\maps\\"+name+".cyan");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String line;
			int row = 0;
			while((line = br.readLine()) != null) {
				int length = line.length();
				for(int i = 0; i < length; i ++) {
					String c = line.substring(i, i+1);
					if(c.equals("1")) wallsGrid.addBlock(new WallBlock(row, i, wallBlockImage, this));
					if(c.equals("2")) trapsGrid.addBlock(new DeathBlock(row, i, deathBlockImage, this));
					if(c.equals("3")) player = new Player(row, i, playerBlockImage, this);
				}
				row += 1;
			}	
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error loading map.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				floorGrid.addBlock(new BasicBlock(i, j, basicBlockImage, this));	
			}
		}
		playersGrid.addBlock(player);
		
		gridLayers.put(GridType.FLOOR, floorGrid);
		gridLayers.put(GridType.WALLS, wallsGrid);
		gridLayers.put(GridType.TRAPS, trapsGrid);
		gridLayers.put(GridType.PLAYERS, playersGrid);
	}
	
	private void gameLoop() {
		setBackground(Color.BLACK);
		
		Event ev = new Event(EventType.SPAWN);
		try { player.trigger(ev, MazeGame.this); } catch (PreventDefaultException e1) {}
		
		while(true) {
			if(!isFocusOwner()) requestFocusInWindow();
			
			try {
				gridLayers.get(GridType.PLAYERS).trigger(new Event(EventType.TICK), this);
			} catch (PreventDefaultException e) {}
			
			repaint();
			
			try { Thread.sleep(paintSleepTime);} catch (InterruptedException e) {}
		}
	}
	
	int x = 0;
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D) g;

		if(loading) return;
			
		g2d.drawImage(gridLayers.get(GridType.FLOOR).getImage(), 0, 0, null);
		g2d.drawImage(gridLayers.get(GridType.WALLS).getImage(), 0, 0, null);
		g2d.drawImage(gridLayers.get(GridType.TRAPS).getImage(), 0, 0, null);
		g2d.drawImage(gridLayers.get(GridType.PLAYERS).getImage(), 0, 0, null);
		
		g2d.setColor(Color.RED);
		g2d.fill(new Rectangle2D.Double(0, 560, player.getMaxHP(), 10));
		g2d.setColor(Color.GREEN);
		g2d.fill(new Rectangle2D.Double(0, 560, player.getHP(), 10));
	
	}
	
	@Override
	public void trigger(Event event, EventListener sender)  throws PreventDefaultException{
		for(GridType type : gridLayers.keySet()){ 
			gridLayers.get(type).trigger(event, sender);
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
