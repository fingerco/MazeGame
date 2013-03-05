import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class GridDynamicBlocks extends GridBlocks {
	private ArrayList<Block> blocks = new ArrayList<>();

	private int blockWidth;
	private int blockHeight;
	
	private int rows;
	private int columns;
	
	GridDynamicBlocks() {
		this(16, 16, 10, 10);
	}
	
	GridDynamicBlocks(int rows, int columns) {
		this(16, 16, rows, columns);
	}

	GridDynamicBlocks(int blockWidth, int blockHeight, int rows, int columns) {
		this.blockHeight = blockHeight;
		this.blockWidth = blockWidth;
		
		this.rows = rows;
		this.columns = columns;

	}
	
	public void addBlock(Block block) {
		blocks.add(block);
	}
	
	public void removeBlock(Block block) {
		blocks.remove(block);
	}
	
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(columns*blockWidth, rows*blockHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setBackground(new Color(0,0,0,0));
		
		for (Block block : blocks) {
			if(block.state == BlockState.VISIBLE) {
				g.drawImage(block.getImage(), block.getColumn()*blockWidth, block.getRow()*blockHeight, null);
			}
		}
		
		return image;
	}

	@Override
	public void trigger(Event event, EventListener sender) throws PreventDefaultException{
		if(event.type == EventType.WALK) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			for (Block block : blocks) {
				if(block.getRow() == row && block.getColumn() == column) {
					block.trigger(event, sender);
				}
			}
		}
		else if(event.type == EventType.STAND) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			for (Block block : blocks) {
				if(block.getRow() == row && block.getColumn() == column) {
					block.trigger(event, sender);
				}
			}
		}
		else if(event.type == EventType.TICK) {
			for (Block block : blocks) {
				block.trigger(event, sender);
			}
		}
		else if(event.type == EventType.CLEAR_VISION) {
			for (Block block : blocks) {
				if(block.state != BlockState.NOT_DISCOVERED) {
					block.state = BlockState.DISCOVERED;
				}
			}
		}
		else if(event.type == EventType.TRY_TO_SEE) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			for (Block block : blocks) {
				if(block.getRow() == row && block.getColumn() == column) {
					block.trigger(event, sender);
				}
			}
		}
		else if(event.type == EventType.SEE) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			for (Block block : blocks) {
				if(block.getRow() == row && block.getColumn() == column) {
					block.trigger(event, sender);
				}
			}
		}
		else if(event.type == EventType.UPDATE_VISION) {
			for (Block block : blocks) {
				block.trigger(event, sender);	
			}
		}
		else if(event.type == EventType.DESTROY_ME) {
			Block block = (Block)event.args.get("block");
			
			removeBlock(block);
		}
		
	}
}
