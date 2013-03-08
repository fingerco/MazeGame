import java.awt.Image;
import java.util.HashMap;

public class Treasure extends Block {

	private Image img;

	Treasure(int row, int column, Image img, EventListener parent) {
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
			// TBD, right now it heals
			HashMap<String, Object> heal = new HashMap<>();
			heal.put("health", 10);
			
			Event ev = new Event(EventType.HEAL, heal);
			sender.trigger(ev, this);
			
			// Now destroy me
			
			HashMap<String, Object> destory = new HashMap<>();
			destory.put("block", this);
			destory.put("type", GridType.BONUS);

			Event ev2 = new Event(EventType.DESTROY_ME, destory);
			parent.trigger(ev2, this);
		}
		else if(event.type == EventType.STAND) {

		}
		else if(event.type == EventType.TRY_TO_SEE) {

		}
		else if(event.type == EventType.SEE) {
			state = BlockState.VISIBLE; 
		} else {
			super.trigger(event, sender);
		}
	}
}
