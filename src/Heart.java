import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Heart extends Block {

	private Image img;
	
	private int animSpeed = 400;
	private int sprites;
	private int currSprite = 0;
	
	private long lastTickTime = System.currentTimeMillis();

	Heart(int row, int column, Image img, EventListener parent) {
		this.row = row;
		this.column = column;
		this.img = img;
		
		sprites = img.getWidth(null)/32;
		
		this.parent = parent;
	}
	
	@Override
	public Image getImage() {
		BufferedImage buff = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = buff.createGraphics();
		g.drawImage(img, -32*currSprite, 0, null);

		return buff;
	}
	
	private void onTick(EventListener sender) throws PreventDefaultException {
		long newTickTime = System.currentTimeMillis();
		
		if(newTickTime - lastTickTime < animSpeed) return;
		
		lastTickTime = newTickTime;
		currSprite += 1;
		currSprite %= sprites;
	}

	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		if(event.type == EventType.WALK) {
			HashMap<String, Object> damage = new HashMap<>();
			damage.put("health", 20);
			
			Event ev = new Event(EventType.HEAL, damage);
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
		else if(event.type == EventType.TICK) {
			onTick(sender);
		}
		else if(event.type == EventType.TRY_TO_SEE) {

		}
		else if(event.type == EventType.SEE) {
			state = BlockState.VISIBLE; 
		}
	}
}
