import java.util.ArrayList;


public abstract class Bag<T> implements EventListener {
	private boolean open = false;
	
	abstract public void add(Item item, int row, int column);
	abstract public void remove(Item item, int row, int column);
	
	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		
	}
	
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	abstract public int getRows();
	abstract public int getColumns();
	abstract public ArrayList<Item>[][] getItems();
}