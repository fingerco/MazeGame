import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class GridStaticBlocks extends GridBlocks {
	private Block blocks[][];
	
	private int blockWidth;
	private int blockHeight;
	
	private int rows;
	private int columns;
	
	GridStaticBlocks() {
		this(16, 16, 10, 10);
	}
	
	GridStaticBlocks(int rows, int columns) {
		this(16, 16, rows, columns);
	}

	GridStaticBlocks(int blockWidth, int blockHeight, int rows, int columns) {
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		
		this.rows = rows;
		this.columns = columns;
		
		blocks = new Block[rows][columns];
	}
	
	@Override
	public void trigger(Event event, EventListener sender) throws PreventDefaultException{
		if(event.type == EventType.WALK) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			Block block = blocks[row][column];
			
			if(block != null) {
				block.trigger(event, sender);
			}
		}
		else if(event.type == EventType.STAND) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			Block block = blocks[row][column];
			
			if(block != null) {
				block.trigger(event, sender);
			}
		}
		else if(event.type == EventType.TICK) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if(blocks[i][j] != null) {
						Block block = blocks[i][j];
						block.trigger(event, sender);
					}
				}
			}
		}
		else if(event.type == EventType.CLEAR_VISION) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if(blocks[i][j] != null && blocks[i][j].state != BlockState.NOT_DISCOVERED) {
						Block block = blocks[i][j];
						block.state = BlockState.DISCOVERED;
					}
				}
			}
		}
		else if(event.type == EventType.TRY_TO_SEE) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			if(row >= 0 && column >= 0 && row < rows && column < columns){
				Block block = blocks[row][column];
				if(block != null) block.trigger(event, sender);
			}
		}
		else if(event.type == EventType.SEE) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			if(row >= 0 && column >= 0 && row < rows && column < columns){
				Block block = blocks[row][column];
				if(block != null) block.trigger(event, sender);
			}
		}
		else if(event.type == EventType.DESTROY_ME) {
			Block block = (Block)event.args.get("block");
			removeBlock(block);
		}
	}

	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(columns*blockWidth, rows*blockHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setBackground(new Color(0,0,0,0));
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if(blocks[i][j] != null) {
					Block block = blocks[i][j];
					if(block.state != BlockState.NOT_DISCOVERED) g.drawImage(block.getImage(), j*blockWidth, i*blockHeight, null);
					
					if(block.state == BlockState.DISCOVERED){
						g.setColor(new Color(0,0,0,128));
						g.fill(new Rectangle(j*blockWidth, i*blockHeight, blockWidth, blockHeight));
					}
					
				}
			}
		}

		return image;
	}
	
	public Block getBlock(int row, int column) {
		return blocks[row][column];
	}
	
	public void addBlock(Block block) {
		blocks[block.getRow()][block.getColumn()] = block;
	}
	
	public void removeBlock(Block block) {
		if(block != null) blocks[block.getRow()][block.getColumn()] = null;
	}
	
}
