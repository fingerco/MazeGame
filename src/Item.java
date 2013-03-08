import java.awt.Image;


public abstract class Item implements Carryable {
	private Image img;
	
	public Image getImage(){
		return img;
	}
}
