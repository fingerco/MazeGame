import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Heart extends Block {

	private Image img;
	
	private int animSpeed = 400;
	private int sprites;
	private int currSprite = 1;
	
	private long lastTime = System.currentTimeMillis();

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
		g.drawImage(img, -32*(currSprite-1), 0, null);

		return buff;
	}
	
	private void onTick(EventListener sender) throws PreventDefaultException {
		long newTime = System.currentTimeMillis();
		
		if(newTime - lastTime < animSpeed) return;
		
		lastTime = newTime;
		currSprite += 1;
		if(currSprite > sprites) currSprite = 1;
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
