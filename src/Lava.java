import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Lava extends Block {
	private Image img;
	private int BURN = 1;
	private boolean readyToBurn = true;
	private int burnCD = 100;
	private long lastBurn = 0;
	
	private int animSpeed = 500;
	private int sprites;
	private int currSprite = 0;
	
	private long lastTickTime = System.currentTimeMillis();
	
	Lava(int row, int column, Image img, EventListener parent) {
		this.row = row;
		this.column = column;
		this.img = img;
		
		sprites = img.getWidth(null)/32;
		
		this.parent = parent;
	}
	
	@Override
	public Image getImage() {
		BufferedImage buff = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buff.createGraphics();
		g.drawImage(img, -32*currSprite, 0, null);

		return buff;
	}
	
	private void onTick(EventListener sender) throws PreventDefaultException {
		long newTime = System.currentTimeMillis();
		readyToBurn = false;
		
		if(newTime - lastBurn > burnCD) {
			readyToBurn = true;
			lastBurn = newTime;
		}
		if(newTime - lastTickTime < animSpeed) return;
		
		lastTickTime = newTime;
		currSprite += 1;
		currSprite %= sprites;
	}

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
			if(readyToBurn) sender.trigger(ev, this);
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