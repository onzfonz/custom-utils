package utilities;

import java.awt.Point;

public class GraphicUtils {

	public GraphicUtils() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * Method: projectPolarPoint(point, r, thetaDegrees)
	 * ------------------------------------------ Taken a theta in degrees,
	 * provide the point of where something would end up given an distance and
	 * that theta and return it as a point.
	 */
	public static Point projectPolarPoint(Point orig, double r, double theta) {
		double x = r * Math.sin(Math.toRadians(theta));
		double y = r * Math.cos(Math.toRadians(theta));
		Point p = new Point();
		p.setLocation(orig.getX() + x, orig.getY() + y);
		return p;
	}

}
