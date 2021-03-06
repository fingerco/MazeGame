import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MazeGame extends JPanel implements EventListener {

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
	// private static GridBlocks itemGrid;
	private static GridBlocks monsterGrid;
	private static GridBlocks playersGrid;
	
	private static Image floorImage;
	private static Image playerImage;
	private static Image wallImage;
	private static Image fireImage;
	private static Image treasureImage;
	private static Image heartIcon;
	private static Image heartImage;
	private static Image spiderImage;
	private static Image bagPanelImage;
	private static int bagSize;
	
	private static MiniMap minimap;
	private static boolean minimapEnabled = true;
	
	MazeGame() {
		
		loading = true;
		loadMap(getClass().getClassLoader().getResource("").getPath());
		loading = false;
		
		setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
		setSize(new Dimension(SCREEN_W, SCREEN_H));
		//setBounds(0, 0, SCREEN_W, SCREEN_H);

		JFrame frame = new JFrame("Maze Game");

		this.addKeyListener(new KeyHandler());
		this.addMouseListener(new MouseHandler());

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
		heartIcon = new ImageIcon(path+"images/heartImage.png").getImage();
		spiderImage = new ImageIcon(path+"images/spiderImage.png").getImage();
		
		bagPanelImage = new ImageIcon(path+"images/bagPanel.png").getImage();
		bagSize = bagPanelImage.getWidth(null);
		
		String name = JOptionPane.showInputDialog("Map Name: "); 
		if(name == null) System.exit(1);
		
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
					// itemGrid = new GridStaticBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
					monsterGrid = new GridDynamicBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
					playersGrid = new GridDynamicBlocks(BLOCKSIZE, BLOCKSIZE, ROWS, COLUMNS);
				}
				else {
					for(int i = 0; i < line.length(); i ++) {
						String c = line.substring(i, i+1);
						if(c.equals("1")) wallsGrid.addBlock(new WallBlock(row-2, i, wallImage, this));
						else if(c.equals("2")) trapsGrid.addBlock(new Lava(row-2, i, fireImage, this));
						else if(c.equals("3")) player = new Player(row-2, i, playerImage, this);
						else if(c.equals("4")) bonusGrid.addBlock(new Treasure(row-2, i, treasureImage, this));
						else if(c.equals("5")) monsterGrid.addBlock(new Spider(row-2, i, spiderImage, this));
						else if(c.equals("6")) bonusGrid.addBlock(new Heart(row-2, i, heartImage, this));
					}
				}
				row += 1;
			}	
		} catch (IOException | NumberFormatException  e) {
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
		// gridLayers.put(GridType.ITEMS, itemGrid);
		gridLayers.put(GridType.MONSTERS, monsterGrid);
		gridLayers.put(GridType.PLAYERS, playersGrid);
		
		minimap = new MiniMap(gridLayers, SCREEN_W-180, 20);
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
	    	int xOffset = getOffset(player.getColumn(), 13,COLUMNS);
	    	int yOffset = getOffset(player.getRow(), 9, ROWS);
	    	
			g2d.drawImage(gridLayers.get(GridType.FLOOR).getImage(), -xOffset, -yOffset, null);
			g2d.drawImage(gridLayers.get(GridType.WALLS).getImage(), -xOffset, -yOffset, null);
			g2d.drawImage(gridLayers.get(GridType.TRAPS).getImage(), -xOffset, -yOffset, null);
			g2d.drawImage(gridLayers.get(GridType.BONUS).getImage(), -xOffset, -yOffset, null);
			// g2d.drawImage(gridLayers.get(GridType.ITEMS).getImage(), -xOffset, -yOffset, null);
			g2d.drawImage(gridLayers.get(GridType.MONSTERS).getImage(), -xOffset, -yOffset, null);
			g2d.drawImage(gridLayers.get(GridType.PLAYERS).getImage(), -xOffset, -yOffset, null);
	    }
	    
		g2d.setColor(Color.GRAY);
		g2d.drawLine(0, SCREEN_H-40, SCREEN_W, SCREEN_H-40);

		g2d.drawImage(heartIcon, 4, SCREEN_H-36, null);
		
		g2d.setColor(Color.RED);
		g2d.fill(new Rectangle2D.Double(40, SCREEN_H-30, 100, 20));
		g2d.setColor(Color.GREEN);
		g2d.fill(new Rectangle2D.Double(40, SCREEN_H-30, 100*(player.getHP()/player.getMaxHP()), 20));
		
		g2d.setColor(Color.BLACK);
		g2d.drawString((int)player.getHP()+"/"+(int)player.getMaxHP(), 65, SCREEN_H-15);
		
		if(minimapEnabled) g2d.drawImage(minimap.getImage(), minimap.getX(), minimap.getY(), null);

		ArrayList<Bag> bags = player.getBags();
		for (int i = 0; i < bags.size(); i ++) {
			Bag<?> bag = bags.get(i);
			int x = 700-i*bagSize;
			if (bag.isOpen()) {
				int rows = bag.getRows();
				int columns = bag.getColumns();
				
				g2d.drawImage(bagPanelImage, x, 500, null);
				g2d.setColor(new Color(0,0,0,128));
				g2d.fill(new Rectangle(x, 500, bagSize, bagSize));
				
				g2d.setColor(new Color(0,0,255,128));
				g2d.fill(new Rectangle(x-columns*bagSize+bagSize, 500-rows*bagSize, columns*bagSize, rows*bagSize));
				
				ArrayList<Item>[][] items = bag.getItems();
				for (int row = 0; row < items.length; row++) {
					for (int column = 0; column < items[row].length; column++) {
						ArrayList<Item> item = items[row][column];
					
						g2d.setColor(new Color(row*25,column*25,row*column*15, 128));
						g2d.fill(new Rectangle(x-column*64, 500-row*64-64, bagSize, bagSize));
						
						if(item.size() > 0) {
							g2d.drawImage(item.get(0).getImage(), x-column*64, 500-row*64-64, null);
							if(item.size() >1) {
								g2d.setColor(Color.WHITE);
								g2d.drawString(item.size()+"", x-column*64, 500-row*64);
							}
						}
					}
				}

			}
			else {
				g2d.drawImage(bagPanelImage, x, 500, null);
			}
		}
		
		Graphics2D g2d_final = (Graphics2D) g;
		g2d_final.drawImage(image, 0, 0, null);
	}
	
	private int getOffset(int location, int tolerance, int max) {
		int offset = location;

    	if(offset <= tolerance) offset = 0;
    	else if(offset > max-tolerance) offset = max-tolerance*2+1;
    	else offset -= tolerance;

    	return offset*BLOCKSIZE;
	}
	
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
				else if(e.getKeyCode() == KeyEvent.VK_M) {
					minimapEnabled = !minimapEnabled;
				} 
			} catch (PreventDefaultException ex) {
			}

		}

		public void keyReleased(KeyEvent e) {
			
			if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				
			}
		}
	}
	
	
	
	private class MouseHandler extends MouseAdapter {
		private boolean mouseOnBag(int x, int y) {
			int bags = player.getBags().size();
			
			int top = 500;
			int bot = top+bagSize;

			int right = 700+bagSize;
			int left = right-bags*bagSize;
			
			return (x >= left && x <= right && y <= bot && y >= top);
		}
		private boolean mouseOnItem(int x, int y, int bagNum, int rows, int columns) {

			int bot = 500;
			int top = bot-rows*bagSize;
			
			int right = 700+bagSize-bagNum*bagSize;
			int left = right-columns*bagSize;
			
			return (x >= left && x <= right && y <= bot && y >= top);
		}
		
		// e.getX(), e.getY(), e.getButton() (1 left, 2 middle, 3 right)
		public void mousePressed(MouseEvent e) {
			if(mouseOnBag(e.getX(), e.getY())) {
				int bagNum = (700+bagSize-e.getX())/bagSize;
				
				player.clickBag(bagNum);
			}
			if(e.getButton() == MouseEvent.BUTTON3){
				for (Bag bag : player.getBags()) {
					if(bag.isOpen()) {
						if(mouseOnItem(e.getX(), e.getY(), player.getBags().indexOf(bag), bag.getRows(), bag.getColumns())) {
							int row = (500-e.getY())/bagSize;
							int column = (700+bagSize-player.getBags().indexOf(bag)*bagSize-e.getX())/bagSize;
							
							ArrayList<Item>[][] item = bag.getItems();
							if(!item[row][column].isEmpty()) {
								Event ev = new Event(EventType.RIGHT_CLICK_ITEM);
								try {
									item[row][column].get(0).trigger(ev, player);
								} catch (PreventDefaultException e1) {}
							}
						}
						break;
					}
				}
			}
			
			
			//try {
				//Stuff
			System.out.println("MOUSE PRESSED => Button: "+e.getButton()+" X:"+e.getX()+" Y:"+e.getY());
			//} catch (PreventDefaultException ex) {}

		}
		public void mouseReleased(MouseEvent e) {
			//try {
				// Stuff
			System.out.println("MOUSE RELEASED => Button: "+e.getButton()+" X:"+e.getX()+" Y:"+e.getY());
			//} catch (PreventDefaultException ex) {}

		}

	}
	
	public static void main(String[] args) {
		new MazeGame();
	}
}
