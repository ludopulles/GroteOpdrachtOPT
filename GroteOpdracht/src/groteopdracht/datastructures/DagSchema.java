
package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import groteopdracht.Constants;

public class DagSchema {

	public List<Route> v1, v2;
	public int t1, t2;

	public DagSchema() {
		this(new ArrayList<>(), new ArrayList<>(), Constants.MAX_TIME, Constants.MAX_TIME);
	}
	
	public DagSchema(DagSchema copy) {
		this(copy.v1, copy.v2, copy.t1, copy.t2);
	}
	
	public DagSchema(List<Route> v1, List<Route> v2, int t1, int t2) {
		this.v1 = new ArrayList<>();
		this.v2 = new ArrayList<>();
		for (Route r : v1) this.v1.add(new Route(r));
		for (Route r : v2) this.v2.add(new Route(r));
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public List<Route> getRoute(int truck) {
		return truck == 0 ? v1 : v2;
	}

	public int getTime(int truck) {
		return truck == 0 ? t1 : t2;
	}

	public void addTime(int truck, int time) {
		if (truck == 0) {
			t1 -= time;
		} else {
			t2 -= time;
		}
	}
	
	public void checkOrders(BitSet orders) {
		for (Route r : v1) r.checkOrders(orders);
		for (Route r : v2) r.checkOrders(orders);
	}
	
	public int checkTime(int truck) {
		int t = 0;
		for (Route r : getRoute(truck)) {
			t += r.checkTime();
		}
		return t;
	}
	
	public InsertIndex bestInsertIndex(int order) {
		InsertIndex bestIndex = new InsertIndex();
		Order cur = Order.orders[order];
		for (int i = 0; i < 2; i++) {
			List<Route> array = getRoute(i);
			int timeLeft = i == 0 ? t1 : t2;
			for (int j = 0; j < array.size(); j++) {
				Route r = array.get(j);
				if (!r.canAdd(order)) continue;
				for (int k = 0, rl = r.length(); k <= rl; k++) {
					int prev = k == 0 ? 0 : r.get(k - 1);
					int next = k == rl ? 0 : r.get(k);
					int increase = cur.timeIncrease(prev, next);
					InsertIndex insert = new InsertIndex(i, j, k, increase);
					if (increase <= timeLeft && bestIndex.compareTo(insert) > 0)
						bestIndex = insert;
				}
			}
			// new route:
			int increase = Constants.DROP_TIME + cur.timeIncrease(0, 0);
			InsertIndex insert = new InsertIndex(i, increase);
			if (increase <= timeLeft && bestIndex.compareTo(insert) > 0)
				bestIndex = insert;
		}
		return bestIndex;
	}

	public boolean canAddRoute(int truck, Route r) {
		int t = truck == 0 ? t1 : t2;
		return r.time <= t;
	}

	public void addRoute(int truck, Route r) {
		if (truck == 0) {
			v1.add(r);
			t1 -= r.time;
		} else {
			v2.add(r);
			t2 -= r.time;
		}
	}

	public void insert(InsertIndex index, int order) {
		if (!index.canAdd) {
			throw new Error("WUUT");
		}
		if (index.newRoute) {
			Route r = new Route();
			r.add(0, order);
			getRoute(index.vNr).add(r);
		} else {
			getRoute(index.vNr).get(index.routeNr).add(index.routeIndex,
					order);
		}
		if (index.vNr == 0) t1 -= index.timeInc;
		else t2 -= index.timeInc;
	}

	public List<Integer> getIds(int truck) {
		ArrayList<Integer> ret = new ArrayList<>();
		for (Route r : getRoute(truck)) {
			ret.addAll(r.route);
			ret.add(0);
		}
		return ret;
	}
	
	public List<Integer> getOrderIds(int truck) {
		ArrayList<Integer> ret = new ArrayList<>();
		for (Route r : getRoute(truck)) {
			for (int id : r.route) {
				ret.add(Order.orders[id].orderID);
			}
			ret.add(Order.orders[0].orderID);
		}
		return ret;
	}

	public int twoOpt(int truck) {
		int diff = 0;
		for (Route r : getRoute(truck)) {
			diff -= r.time;
			r.twoOpt();
			diff += r.time;
		}
		if (truck == 0) t1 += diff;
		else 			t2 += diff;
		return diff;
	}

	public int twoHalfOpt(int truck) {
		int diff = 0;
		for (Route r : getRoute(truck)) {
			diff -= r.time;
			r.twoHalfOpt();
			diff += r.time;
		}
		if (truck == 0) t1 += diff;
		else 			t2 += diff;
		return diff;
	}
	
	public int opts(int truck) {
		int diff = 0;
		for (Route r : getRoute(truck)) {
			diff -= r.time;
			r.opts();
			diff += r.time;
		}
		if (truck == 0) t1 += diff;
		else 			t2 += diff;
		return diff;
	}
	
	public void removeTimes(int[] times, int truck) {
		for (Route r : getRoute(truck)) {
			for (int i = 0, N = r.length(); i < N; i++) {
				int lorder = i == 0 ? 0 : r.route.get(i - 1);
				int rorder = i == N - 1 ? 0 : r.route.get(i + 1);
				int cur = r.route.get(i);
				// the 'increase' in time if we remove this order from the route:
				times[cur] -= Order.orders[cur].timeIncrease(lorder, rorder);
			}
		}
	}

	public int removeAllNegatives(int[] times, int truck) {
		int diff = 0;
		Iterator<Route> it = getRoute(truck).iterator();
		while (it.hasNext()) {
			Route r = it.next();
			for (int idx = 0; idx < r.length(); idx++) {
				if (times[r.route.get(idx)] < 0) {
					diff += r.removeAt(idx);
					idx--;
				}
			}
			if (r.length() == 0) {
				diff += r.time;
				it.remove();
			}
		}
		if (truck == 0) t1 += diff;
		else t2 += diff;
		return diff;
	}

	public Route getRandomRoute(int truck, Random rand) {
		List<Route> routes = getRoute(truck);
		return routes.get(rand.nextInt(routes.size()));
	}

	public boolean routesEmpty(int truck) {
		return getRoute(truck).isEmpty();
	}

	public int removeOrder(int truck, int order) {
		int diff = 0;
		Iterator<Route> it = getRoute(truck).iterator();
		while (it.hasNext()) {
			Route r = it.next();
			for (int idx = 0; idx < r.length(); idx++) {
				if (r.route.get(idx) == order) {
					diff += r.removeAt(idx);
					idx--;
				}
			}
			if (r.length() == 0) {
				diff += r.time;
				it.remove();
			}
		}
		if (truck == 0) t1 += diff;
		else t2 += diff;
		return diff;
	}

}