import java.awt.Image;
import java.util.HashMap;

public class Heart extends Block {

	private int pie = 314;
	
	private Image img;

	Heart(int row, int column, Image img, EventListener parent) {
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
			HashMap<String, Object> damage = new HashMap<>();
			damage.put("health", 20);
			
			Event ev = new Event(EventType.HEAL, damage);
			sender.trigger(ev, this);
			
			///////////////////////
			
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
		}
	}

}
