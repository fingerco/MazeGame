import java.awt.Point;
import java.util.ArrayList;

public class Vision {
	public static ArrayList<Point> getLineOfSight(Point start, Point end) {
		ArrayList<Point> result = new ArrayList<>();
		if(start.equals(end)) {
			result.add(start);
			return result;
		}
		
		int x = start.x, 
			y = start.y, 
			endx = end.x, 
			endy = end.y,  
			dx = endx - x, 
			dy = endy - y,
			D = 0,
		    c, 
		    M, 
		    xInc = 1, 
		    yInc = 1;
		
		if (dx < 0) {
			xInc = -1; 
			dx *= -1;
		}
		if (dy < 0) {
			yInc = -1; 
			dy *= -1;
		}
		
		if (dy <= dx)
		{  c = 2 * dx; M = 2 * dy;
			for (;;) {  
				result.add(new Point(x,y));
				if (x == endx) break;
				x += xInc; 
				D += M;
				if (D > dx) {
					y += yInc; 
					D -= c;
				}
			}
		}
		else
		{  c = 2 * dy; M = 2 * dx;
			for (;;) {  
				result.add(new Point(x,y));
				if (y == endy) break;
				y += yInc; 
				D += M;
				if (D > dy) {
					x += xInc; 
					D -= c;
				}
			}
		}
		return result;
	}	
	
	public static ArrayList<Point> pointsInCircle(int col, int row, int r) {
		ArrayList<Point> pointsInCircle = new ArrayList<>();
		for(int x = -r; x <= r; x++) {
			for(int y = -r; y <= r; y++) {
				if(Math.sqrt(x*x+y*y) <= r) {
					pointsInCircle.add(new Point(col+x, row+y));
				}
			}
		}
		return pointsInCircle;
	}
}
