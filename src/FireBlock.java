import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class FireBlock extends Block {
	private Image img;
	private int BURN = 1;

	private int animSpeed = 500;
	private int sprites;
	private int currSprite = 1;
	
	private long lastTime = System.currentTimeMillis();
	
	FireBlock(int row, int column, Image img, EventListener parent) {
		this.row = row;
		this.column = column;
		this.img = img;
		
		sprites = img.getWidth(null)/16;
		
		this.parent = parent;
	}
	
	@Override
	public Image getImage() {
		BufferedImage buff = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buff.createGraphics();
		g.drawImage(img, -16*(currSprite-1), 0, null);

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
			damage.put("damage", BURN);
			
			Event ev = new Event(EventType.TAKE_DAMAGE, damage);
			sender.trigger(ev, this);
		}
		else if(event.type == EventType.STAND) {
			HashMap<String, Object> damage = new HashMap<>();
			damage.put("damage", BURN);
			
			Event ev = new Event(EventType.TAKE_DAMAGE, damage);
			sender.trigger(ev, this);
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