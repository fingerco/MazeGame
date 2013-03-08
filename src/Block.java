import java.awt.Image;

abstract public class Block implements EventListener {
	protected int column;
	protected int row;
	
	protected BlockState state = BlockState.NOT_DISCOVERED;
	
	protected EventListener parent;
	abstract public Image getImage();
	
	public int getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}

	
	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		
	}
}
