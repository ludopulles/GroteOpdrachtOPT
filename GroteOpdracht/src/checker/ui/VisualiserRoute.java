package checker.ui;

import java.awt.Color;
import java.awt.Graphics;

import checker.validator.Order;
import checker.validator.Solution;

public class VisualiserRoute {
	final int truck;
	final int day;
	final int route;
	final int routeStartId;
	final Color color;
	public boolean visible = true;

	public VisualiserRoute(int truck, int day, int route, int routeStartId, Color color) {
		this.truck = truck;
		this.day = day;
		this.route = route;
		this.routeStartId = routeStartId;
		this.color = color;

		this.visible = true;
	}

	public void DrawRoute(Solution s, Graphics g, long xOff, long yOff, double xF, double yF) {
		Order[] route = s.solution[this.truck][this.day];

		int i = this.routeStartId;

		Order dispose = s.problem.getOrder(0);
		Order oPrev = dispose;

		Order oNext = route[i];

		g.setColor(this.color);
		while ((oNext != null) && (oNext.id != 0)) {

			int x1 = (int) (xF * (oPrev.xCoord + xOff));
			int y1 = (int) (yF * (oPrev.yCoord + yOff));
			int x2 = (int) (xF * (oNext.xCoord + xOff));
			int y2 = (int) (yF * (oNext.yCoord + yOff));

			g.drawLine(x1, y1, x2, y2);

			oPrev = oNext;
			i++;
			oNext = route[i];
		}

		int x1 = (int) (xF * (oPrev.xCoord + xOff));
		int y1 = (int) (yF * (oPrev.yCoord + yOff));
		int x2 = (int) (xF * (dispose.xCoord + xOff));
		int y2 = (int) (yF * (dispose.yCoord + yOff));

		g.drawLine(x1, y1, x2, y2);
	}

	public String toString() {
		return Order.getDayShort(this.day) + "-Truck " + (this.truck + 1) + "-Route " + this.route;
	}
}
