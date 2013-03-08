import java.util.HashMap;

public class Event {
	EventType type;
	HashMap<String, Object> args = new HashMap<>();
	
	Event(EventType type) {
		this(type, new HashMap<String, Object>());
	}
	
	Event(EventType type, HashMap<String, Object> args) {
		this.type = type;
		this.args = args;
	}
	
	public void setArgs(HashMap<String, Object> args) {
		this.args = args;
	}
}
