
public abstract class Bag<T> implements EventListener {
	abstract public void add(T object, int row, int column);
	abstract public void remove(T object, int row, int column);
	
	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		
	}
}