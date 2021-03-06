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
	private boolean unchanged;
	private BufferedImage cached_image;
	
	GridStaticBlocks() {
		this(32, 32, 16, 16);
	}
	
	GridStaticBlocks(int rows, int columns) {
		this(32, 32, rows, columns);
	}

	GridStaticBlocks(int blockWidth, int blockHeight, int rows, int columns) {
		this.unchanged = false;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		
		this.rows = rows;
		this.columns = columns;
		
		blocks = new Block[rows][columns];
	}
	
	public void addBlock(Block block) {
		this.unchanged = false;
		blocks[block.getRow()][block.getColumn()] = block;
	}
	
	public void removeBlock(Block block) {
		this.unchanged = false;
		if(block != null) blocks[block.getRow()][block.getColumn()] = null;
	}
	
	public void trigger(Event event, EventListener sender) throws PreventDefaultException{
		this.unchanged = false;
		
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
		if(this.unchanged) return this.cached_image;

		BufferedImage image = new BufferedImage(columns*blockWidth, rows*blockHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
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

		this.cached_image = image;
		this.unchanged = true;
		return image;
	}
}
