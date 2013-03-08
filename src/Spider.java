import java.awt.Image;
import java.util.HashMap;


public class Spider extends Block {

	private int walkDelay = 300;
	private long lastTickTime = System.currentTimeMillis();
	private boolean left = true;

	private int DAMAGE = 25;
	
	private Image img;

	Spider(int row, int column, Image img, EventListener parent) {
		this.row = row;
		this.column = column;
		this.img = img;
		
		this.parent = parent;
	}
	
	@Override
	public Image getImage() {
		return img;
	}
	
	private void onTick(EventListener sender) throws PreventDefaultException {
		long newTickTime = System.currentTimeMillis();
		
		if(newTickTime-lastTickTime < walkDelay) return;
		
		lastTickTime = newTickTime;
		
		HashMap<String, Object> coordinates = new HashMap<>();
		coordinates.put("row", row);
		
		if(left) coordinates.put("column", column-1);
		else coordinates.put("column", column+1);
		
		Event ev = new Event(EventType.WALK, coordinates);
		
		try {
			sender.trigger(ev, this);
		} catch (PreventDefaultException e) {
			left = !left;
			if(left) coordinates.put("column", column-1);
			else coordinates.put("column", column+1);
			sender.trigger(ev, this);
		}
		if(left) column -= 1;
		else column += 1;
		
		sender.trigger(new Event(EventType.UPDATE_VISION), this);
	}

	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		if(event.type == EventType.WALK) {

			HashMap<String, Object> damage = new HashMap<>();
			damage.put("damage", DAMAGE);
			
			Event ev = new Event(EventType.TAKE_DAMAGE, damage);
			sender.trigger(ev, this);
		}
		else if(event.type == EventType.STAND) {

		}
		else if(event.type == EventType.TICK) {
			onTick(sender);
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
