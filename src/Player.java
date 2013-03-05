import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;


public class Player extends Block implements EventListener{

	private double HP = 100;
	private double maxHP = 100;
	private int maxVision = 4;
	
	private Image img;
	
	Player(int row, int column, Image img, EventListener parent) {
		this.row = row;
		this.column = column;
		this.img = img;
		
		this.parent = parent;
		this.state = BlockState.VISIBLE;
	}
	
	public double getHP() {
		return HP;
	}

	public double getMaxHP() {
		return maxHP;
	}

	public Image getImage() {
		return img;
	}

	private void onTick(EventListener sender) throws PreventDefaultException {
		HashMap<String, Object> coordinates = new HashMap<>();
		coordinates.put("row", row);
		coordinates.put("column", column);
		
		Event ev = new Event(EventType.STAND, coordinates);
		sender.trigger(ev, this);
	}
	
	private void updateVision(EventListener sender) {	
		try {sender.trigger(new Event(EventType.CLEAR_VISION), this);} catch (PreventDefaultException e1) {}
		
		ArrayList<Point> pointsToCheck = Vision.pointsInCircle(column, row, maxVision);

		for (Point point : pointsToCheck) {
			ArrayList<Point> lineOfPoints = Vision.getLineOfSight(new Point(column, row), point);
			int lastPoint = lineOfPoints.size()-1;
			for (Point currPoint : lineOfPoints) {
				HashMap<String, Object> coordinates = new HashMap<>();
				coordinates.put("row", currPoint.y);
				coordinates.put("column", currPoint.x);
				
				Event ev;
				if(lineOfPoints.indexOf(currPoint) != lastPoint) ev = new Event(EventType.TRY_TO_SEE, coordinates);
				else ev = new Event(EventType.SEE, coordinates);

				try { sender.trigger(ev, this); } catch(PreventDefaultException e) { break; }
			}
		}
	}
	
	@Override
	public void trigger(Event event, EventListener sender) throws PreventDefaultException {
		if(event.type == EventType.MOVE_LEFT) {
			HashMap<String, Object> coordinates = new HashMap<>();
			coordinates.put("row", row);
			coordinates.put("column", column-1);
			
			Event ev = new Event(EventType.WALK, coordinates);
			
			sender.trigger(ev, this);
			column -= 1;
			
			updateVision(sender);
		}
		else if(event.type == EventType.MOVE_RIGHT) {
			HashMap<String, Object> coordinates = new HashMap<>();
			coordinates.put("row", row);
			coordinates.put("column", column+1);
			
			Event ev = new Event(EventType.WALK, coordinates);
			
			sender.trigger(ev, this);
			column += 1;
			
			updateVision(sender);
		}
		else if(event.type == EventType.MOVE_UP) {
			HashMap<String, Object> coordinates = new HashMap<>();
			coordinates.put("row", row-1);
			coordinates.put("column", column);
			
			Event ev = new Event(EventType.WALK, coordinates);
			
			sender.trigger(ev, this);
			row -= 1;
			
			updateVision(sender);
		}
		else if(event.type == EventType.MOVE_DOWN) {
			HashMap<String, Object> coordinates = new HashMap<>();
			coordinates.put("row", row+1);
			coordinates.put("column", column);
			
			Event ev = new Event(EventType.WALK, coordinates);
			
			sender.trigger(ev, this);
			row += 1;
			
			updateVision(sender);
		}
		else if(event.type == EventType.TAKE_DAMAGE) {
			HP -= (int)event.args.get("damage");
		} 
		else if(event.type == EventType.TICK) {
			onTick(sender);
		}
		else if(event.type == EventType.TRY_TO_SEE) {

		}
		else if(event.type == EventType.SEE) {
			state = BlockState.VISIBLE; 
		}
		else if(event.type == EventType.SPAWN) {
			updateVision(sender);
		}

	}
}
