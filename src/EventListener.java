
public interface EventListener {
	void trigger(Event event, EventListener sender) throws PreventDefaultException;
}
