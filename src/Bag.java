import java.util.ArrayList;


public abstract class Bag {
	private ArrayList<Item> items[][];
	private int rows;
	private int columns;
	

	Bag(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		
		items = new ArrayList[rows][columns];
		
		for(int i = 0; i < columns; i++) {
			for(int j = 0; j < rows; j++) {
				items[i][j] = new ArrayList<Item>();
			}
		}
	}
	
	
	public void addItem(Item item, int row, int column) {
		items[row][column].add(item);
	}

	public void removeItem(Item item, int row, int column) {
		items[row][column].remove(0);
	}
}
