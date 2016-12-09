package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import groteopdracht.Constants;

public class Route {

	public final List<Integer> route;
	public int capLeft, time;

	public Route() {
		this.route = new ArrayList<>();
		this.capLeft = Constants.MAX_CAPACITY;
		this.time = Constants.DROP_TIME;
	}

	public int length() {
		return this.route.size();
	}
	
	public int get(int index) {
		return this.route.get(index);
	}
	
	public boolean canAdd(int order) {
		return this.capLeft >= Order.orders[order].capacity();
	}

	public void add(int index, int order) {
		// Assert: canAdd(order)

		ListIterator<Integer> li = route.listIterator(index);
		int prev = li.hasPrevious() ? li.previous() : 0;
		int next = index == route.size() ? 0 : route.get(index);
		
		this.capLeft -= Order.orders[order].capacity();
		this.time += Order.orders[order].timeIncrease(prev, next);
		this.route.add(index, order);
	}
}
