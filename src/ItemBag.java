import java.util.ArrayList;


public class ItemBag extends Bag<Carryable> {
	private ArrayList<Carryable> items[][];
	private int rows;
	private int columns;

	ItemBag(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		
		items = new ArrayList[rows][columns];
		
		for(int i = 0; i < columns; i++) {
			for(int j = 0; j < rows; j++) {
				items[i][j] = new ArrayList<Carryable>();
			}
		}
	}

	@Override
	public void add(Carryable object, int row, int column) {
		items[row][column].add(object);
		
	}

	@Override
	public void remove(Carryable object, int row, int column) {
		items[row][column].remove(object);
	}

	@Override
	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		if(event.type == EventType.LEFT_CLICK_ITEM || event.type == EventType.RIGHT_CLICK_ITEM) {
			int row = (int)event.args.get("row");
			int column = (int)event.args.get("column");
			
			if(items[row][column].isEmpty()) return;
			
			Carryable item = items[row][column].get(0);
			item.trigger(event, sender);
		} else {
			super.trigger(event, sender);
		}
	}

}
