import java.awt.Image;
import java.util.HashMap;

public class DeathBlock extends Block {
	private Image img;

	DeathBlock(int row, int column, Image img, EventListener parent) {
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
			damage.put("damage", 15);
			
			Event ev = new Event(EventType.TAKE_DAMAGE, damage);
			sender.trigger(ev, this);
		}
		else if(event.type == EventType.STAND) {
			HashMap<String, Object> damage = new HashMap<>();
			damage.put("damage", 1);
			
			Event ev = new Event(EventType.TAKE_DAMAGE, damage);
			sender.trigger(ev, this);
		}
		else if(event.type == EventType.TRY_TO_SEE) {

		}
		else if(event.type == EventType.SEE) {
			state = BlockState.VISIBLE; 
		}
	}
}