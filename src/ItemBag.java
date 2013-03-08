import java.util.ArrayList;


public class ItemBag extends Bag<Item> {
	private ArrayList<Item> items[][];
	private int rows;
	private int columns;

	ItemBag(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		
		items = new ArrayList[rows][columns];
		
		for(int i = 0; i < columns; i++) {
			for(int j = 0; j < rows; j++) {
				items[i][j] = new ArrayList<Item>();
			}
		}
	}

	@Override
	public void add(Item object, int row, int column) {
		items[row][column].add(object);
		
	}

	@Override
	public void remove(Item object, int row, int column) {
		items[row][column].remove(object);
		
	}

}
