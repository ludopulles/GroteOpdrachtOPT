package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Route {

	public final List<Integer> route;
	public int capLeft, time;

	public Route() {
		this.route = new ArrayList<>();
		this.capLeft = DagSchema.MAX_CAPACITY;
		this.time = Order.DROP_TIME;
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
		// assert(canAdd(order));
		this.capLeft -= Order.orders[order].capacity();
		
		int cur = Order.orders[order].matrixID;
		ListIterator<Integer> li = route.listIterator(index);
		int prev = Order.orders[li.hasPrevious() ? li.previous() : 0].matrixID;
		int next = Order.orders[index == route.size() ? 0 : route.get(index)].matrixID;
		route.add(index, order);
		this.time += Afstanden.tijd[prev][cur];
		this.time += Afstanden.tijd[cur][next];
		this.time -= Afstanden.tijd[prev][next];
		this.time += Order.orders[order].leegTijd;
	}
}
