import java.awt.Image;

import javax.swing.ImageIcon;


public class Sword extends Item implements Carryable{

	@Override
	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		if(event.type == EventType.RIGHT_CLICK_ITEM) {
			System.out.println("USED!");
			sender.trigger(new Event(EventType.DESTROY_ME), this);
		}
		
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return new ImageIcon(getClass().getClassLoader().getResource("").getPath()+"images/sword.png").getImage();
	}

}
