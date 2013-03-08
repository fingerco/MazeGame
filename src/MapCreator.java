import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;


public class MapCreator extends JPanel {
	
	private static final long serialVersionUID = 5365881360950287615L;
	
	private static int ROWS = 12;
	private static int COLUMNS = 16;
	
	private static int mx = 0;
	private static int my = 0;
	
	private static Integer[][] blocks;
	
	private static BlockType currBlock = BlockType.WALL;
	
	private static String path;
	private static Image floorImage;
	private static Image playerImage;
	private static Image wallImage;
	private static Image fireImage;
	private static Image treasureImage;
	private static Image heartImage;
	private static Image spiderImage;
	
	JPopupMenu blockMenu = new JPopupMenu("Block Selector");
	final JButton btnChangeBlock;
	
	private static boolean fileChanged = false;
	
	MapCreator() {
		path = getClass().getClassLoader().getResource("").getPath();
		
		loadImages();

		newMap(ROWS, COLUMNS);
		
		setPreferredSize(new Dimension(COLUMNS*32, ROWS*32));
		
		final JFrame frame = new JFrame("Maze Game - Map Creator");
		
		loadBlockMenu();
		frame.add(blockMenu);
		
		frame.addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e) {
				if(fileChanged) {
					int confirm = JOptionPane.showConfirmDialog(null, "Save First?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.YES_NO_CANCEL_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
	                	saveMap();
	                	System.exit(0);
	                }
					else if (confirm == JOptionPane.NO_OPTION) {
	                	System.exit(0);
	                }
                }
				else {
					int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
	                	System.exit(0);
	                }
				}
			}
			public void windowOpened(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		});

		this.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {
				int column =  (e.getX()-e.getX()%32)/32;
				int row =  (e.getY()-e.getY()%32)/32;

				if(row >= ROWS || column >= COLUMNS) return;
				
				if(e.getButton() == MouseEvent.BUTTON1){
					if(currBlock == BlockType.FLOOR) blocks[row][column] = 0;
					else if(currBlock == BlockType.WALL) blocks[row][column] = 1;
					else if(currBlock == BlockType.LAVA) blocks[row][column] = 2;
					else if(currBlock == BlockType.PLAYER) blocks[row][column] = 3;
					else if(currBlock == BlockType.TREASURE) blocks[row][column] = 4;
					else if(currBlock == BlockType.SPIDER) blocks[row][column] = 5;
					else if(currBlock == BlockType.HEART) blocks[row][column] = 6;

					fileChanged = true;
				}
				if(e.getButton() == MouseEvent.BUTTON3){
					blocks[row][column] = 0;
					fileChanged = true;
				}
			}
			public void mouseReleased(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}});
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		container.add(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); 
		
		JButton btnNew = new JButton("New");
		JButton btnSave = new JButton("Save");
		JButton btnLoad = new JButton("Load");
		btnChangeBlock = new JButton("Change Block", new ImageIcon(wallImage));

		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newMap();
				setPreferredSize(new Dimension(COLUMNS*32, ROWS*32));
				setSize(new Dimension(COLUMNS*32, ROWS*32));
				frame.pack();
			}
		});
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {saveMap();}
		});
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMap();
				setPreferredSize(new Dimension(COLUMNS*32, ROWS*32));
				setSize(new Dimension(COLUMNS*32, ROWS*32));
				frame.pack();	
			}
		});
		btnChangeBlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				blockMenu.show(frame, -60, 0);
			}
		});
		
		buttonPanel.add(btnNew);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(btnSave);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(btnLoad);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(btnChangeBlock);
		
		container.add(buttonPanel);
		
		frame.add(container);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setFocusable(true);
		frame.pack();
		frame.setVisible(true);
		
		loop();
	}
	private void loadBlockMenu() {
		JButton btnFloor = new JButton(new ImageIcon(floorImage));
		JButton btnPlayer = new JButton(new ImageIcon(playerImage));
		JButton btnWall = new JButton(new ImageIcon(wallImage));
		JButton btnFire = new JButton(new ImageIcon(fireImage));
		JButton btnTreasue = new JButton(new ImageIcon(treasureImage));
		JButton btnHeart = new JButton(new ImageIcon(heartImage));
		JButton btnSpider = new JButton(new ImageIcon(spiderImage));
		
		btnFloor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {changeBlock(BlockType.FLOOR);}});
		btnPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {changeBlock(BlockType.PLAYER);}});
		btnWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {changeBlock(BlockType.WALL);}});
		btnFire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {changeBlock(BlockType.LAVA);}});
		btnTreasue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {changeBlock(BlockType.TREASURE);}});
		btnHeart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {changeBlock(BlockType.HEART);}});
		btnSpider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {changeBlock(BlockType.SPIDER);}});
		
		blockMenu.add(btnFloor);
		blockMenu.add(btnWall);
		blockMenu.add(btnPlayer);
		blockMenu.add(btnFire);
		blockMenu.add(btnTreasue);
		blockMenu.add(btnHeart);
		blockMenu.add(btnSpider);
	}
	
	private void loadImages() {
		floorImage = new ImageIcon(path+"images/floorImage.png").getImage();
		playerImage = new ImageIcon(path+"images/playerImage.png").getImage();
		wallImage = new ImageIcon(path+"images/wallImage.png").getImage();
		fireImage = new ImageIcon(path+"images/fireImage.png").getImage();
		treasureImage = new ImageIcon(path+"images/treasureImage.png").getImage();
		heartImage = new ImageIcon(path+"images/heartImage.png").getImage();
		spiderImage = new ImageIcon(path+"images/spiderImage.png").getImage();
	}
	
	private void changeBlock(BlockType newType) {
		currBlock = newType;
		
		if(currBlock == BlockType.FLOOR) btnChangeBlock.setIcon(new ImageIcon(floorImage));
		else if(currBlock == BlockType.WALL) btnChangeBlock.setIcon(new ImageIcon(wallImage));
		else if(currBlock == BlockType.LAVA) btnChangeBlock.setIcon(new ImageIcon(fireImage));
		else if(currBlock == BlockType.PLAYER) btnChangeBlock.setIcon(new ImageIcon(playerImage));
		else if(currBlock == BlockType.TREASURE) btnChangeBlock.setIcon(new ImageIcon(treasureImage));
		else if(currBlock == BlockType.HEART) btnChangeBlock.setIcon(new ImageIcon(heartImage));
		else if(currBlock == BlockType.SPIDER) btnChangeBlock.setIcon(new ImageIcon(spiderImage));
	}
	
	private void newMap() {
		String x = JOptionPane.showInputDialog("Width: ");
		if(x == null) return;
		
		String y = JOptionPane.showInputDialog("Height: ");
		if(y == null) return;
		
		newMap(Integer.parseInt(y), Integer.parseInt(x));
	}
	
	private void newMap(int rows, int columns) {

		COLUMNS = columns;
		ROWS = rows;
		
		blocks = new Integer[ROWS][COLUMNS];
		
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j] = 0;
				if(i == 0 || j == 0 || i == blocks.length-1 || j == blocks[i].length-1) blocks[i][j] = 1;
			}
		}
		
		fileChanged = true;
	}
	
	private void saveMap() {
		String name = JOptionPane.showInputDialog("File Name: "); 
		
		if(name == null) return;
		
		try {
			FileWriter stream = new FileWriter(path+"maps/"+name+".cyan");
			BufferedWriter out = new BufferedWriter(stream);
			
			out.write(ROWS+"\n"+COLUMNS+"\n");
			
			for(int i = 0; i < blocks.length; i ++) {
				for(int j = 0; j < blocks[i].length; j ++) {
					out.write(blocks[i][j]+"");
				}
				out.write("\n");
			}
			out.close();
			fileChanged = false;
			JOptionPane.showMessageDialog(null, "Saved!");
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Error saving file.");
		}
	}
	
	private void loadMap() {
		String name = JOptionPane.showInputDialog("Map Name: "); 
		
		try {
			FileInputStream fstream = new FileInputStream(path+"maps/"+name+".cyan");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String line;
			int row = 0;
			while((line = br.readLine()) != null) {
				if(row == 0) ROWS = Integer.parseInt(line);
				else if(row == 1) {
					COLUMNS = Integer.parseInt(line);
					blocks = new Integer[ROWS][COLUMNS];
				}
				else {
					for(int i = 0; i < line.length(); i ++) {
						String c = line.substring(i, i+1);
						blocks[row-2][i] = Integer.parseInt(c);
					}
				}
				row += 1;
			}	
			fileChanged = false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error loading map.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void loop() {
		setBackground(new Color(0, 0, 0, 128));
		while(true) {
			if(!isFocusOwner()) requestFocusInWindow();
			
			try {
				Point newMouse = getMousePosition();
				if(newMouse != null) {
					mx = newMouse.x;
					my = newMouse.y;
				}
			} catch(NullPointerException e) {}
				
			
			repaint();
			
			try {Thread.sleep(25);} catch (InterruptedException e) {}
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				int value = blocks[i][j];
				
				if(value == 0) g2d.drawImage(floorImage, j*32, i*32, null);
				if(value == 1) g2d.drawImage(wallImage, j*32, i*32, null);
				if(value == 2) g2d.drawImage(fireImage, j*32, i*32, null);
				if(value == 3) g2d.drawImage(playerImage, j*32, i*32, null);
				if(value == 4) g2d.drawImage(treasureImage, j*32, i*32, null);
				if(value == 5) g2d.drawImage(spiderImage, j*32, i*32, null);
				if(value == 6) g2d.drawImage(heartImage, j*32, i*32, null);
			}
		}

		int column =  (mx-(mx%32))/32;
		int row =  (my-(my%32))/32;

		if(row >= ROWS || column >= COLUMNS) return;
		
		g2d.setColor(Color.GREEN);
		g2d.drawRect(column*32, row*32, 32, 32);
	}
	
	public static void main(String[] args) {
		new MapCreator();
	}
}
