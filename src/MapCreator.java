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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MapCreator extends JPanel {
	
	private static final long serialVersionUID = 5365881360950287615L;
	
	private static int ROWS;
	private static int COLUMNS;
	
	private static int mx = 0;
	private static int my = 0;
	
	private static Integer[][] blocks;
	
	private static String imageLoc = "C:\\Users\\Ryan\\Desktop\\Java Programs\\MazeGame\\images\\";
	private static Image basicBlockImage = new ImageIcon(imageLoc+"defaultBlock.png").getImage();
	private static Image playerBlockImage = new ImageIcon(imageLoc+"player.png").getImage();
	private static Image wallBlockImage = new ImageIcon(imageLoc+"wallBlock.png").getImage();
	private static Image deathBlockImage = new ImageIcon(imageLoc+"deathBlock.png").getImage();
	
	MapCreator() {
		
		String x = JOptionPane.showInputDialog("Width: ");
		String y = JOptionPane.showInputDialog("Height: ");
		
		COLUMNS = Integer.parseInt(x);
		ROWS = Integer.parseInt(y);
		
		blocks = new Integer[ROWS][COLUMNS];
		
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j] = 0;
				if(i == 0 || j == 0 || i == blocks.length-1 || j == blocks[i].length-1) blocks[i][j] = 1;
			}
		}
		
		setPreferredSize(new Dimension(COLUMNS*16, ROWS*16));
		
		JFrame frame = new JFrame("Maze Game - Map Creator");
		
		frame.addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null, "Save first?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                	save();
                }
                System.exit(0);
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
				int column =  e.getX()-e.getX()%16;
				int row =  e.getY()-e.getY()%16;

				if(e.getButton() == MouseEvent.BUTTON1){
					blocks[row/16][column/16] += 1;
					blocks[row/16][column/16] %= 4;
				}
				if(e.getButton() == MouseEvent.BUTTON3){
					blocks[row/16][column/16] = 0;
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
		
		JPanel pan = new JPanel();
		JButton save = new JButton("Save");
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		pan.add(save);
		container.add(pan);
		
		frame.add(container);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setFocusable(true);
		frame.pack();
		frame.setVisible(true);
		
		loop();
	}
	
	private void save() {
		String name = JOptionPane.showInputDialog("File Name: "); 
		
		try {
			FileWriter stream = new FileWriter("..\\maps\\"+name+".cyan");
			BufferedWriter out = new BufferedWriter(stream);
			
			for(int i = 0; i < blocks.length; i ++) {
				for(int j = 0; j < blocks[i].length; j ++) {
					out.write(blocks[i][j]+"");
				}
				out.write("\n");
			}
			out.close();
			
			JOptionPane.showMessageDialog(null, "Saved!");
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Error saving file.");
		}
	}
	
	private void loop() {
		setBackground(Color.BLACK);
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
				
				if(value == 0) g2d.drawImage(basicBlockImage, j*16, i*16, null);
				if(value == 1) g2d.drawImage(wallBlockImage, j*16, i*16, null);
				if(value == 2) g2d.drawImage(deathBlockImage, j*16, i*16, null);
				if(value == 3) g2d.drawImage(playerBlockImage, j*16, i*16, null);
			}
		}
		
		/*g2d.setColor(new Color(0,0,0,128));
		
		for (int i = 0; i <= ROWS; i++) {
			g2d.drawLine(0, i*16, 16*COLUMNS, i*16);
		}
		for (int i = 0; i <= COLUMNS; i++) {
			g2d.drawLine(i*16, 0, i*16, 16*ROWS);
		}*/

		g2d.setColor(Color.GREEN);
		g2d.drawRect(mx-(mx%16), my-(my%16), 16, 16);
	}
	
	public static void main(String[] args) {
		new MapCreator();
	}
}
