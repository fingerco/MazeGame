import java.awt.Image;

public class WallBlock extends Block {
	private Image img;

	WallBlock(int row, int column, Image img, EventListener parent) {
		this.row = row;
		this.column = column;
		this.img = img;
		
		this.parent = parent;
	}
	
	@Override
	public Image getImage() {
		return img;
	}

	@Override
	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		if(event.type == EventType.WALK) {
			throw new PreventDefaultException();
		}
		else if(event.type == EventType.TRY_TO_SEE) {
			throw new PreventDefaultException();
		}
		else if(event.type == EventType.SEE) {
			state = BlockState.VISIBLE; 
		} else {
			super.trigger(event, sender);
		}
	}
}
