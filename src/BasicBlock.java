import java.awt.Image;

public class BasicBlock extends Block {
	private Image img;

	BasicBlock(int row, int column, Image img, EventListener parent) {
		this.row = row;
		this.column = column;
		this.img = img;
		
		this.parent = parent;
	}
	
	public void trigger(Event event, EventListener sender) {
		if(event.type == EventType.TRY_TO_SEE) {
			
		}
		else if(event.type == EventType.SEE) {
			state = BlockState.VISIBLE; 
		}
	}

	public Image getImage() {
		return this.img;
	}
}
