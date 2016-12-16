
package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.List;
import groteopdracht.Constants;

public class DagSchema {

	private ArrayList<Route> v1, v2;
	private int t1, t2;

	public DagSchema() {
		v1 = new ArrayList<>();
		v2 = new ArrayList<>();
		t1 = t2 = Constants.MAX_TIME;
	}
	
	public DagSchema(DagSchema copy) {
		this();
		for (Route r : copy.v1) v1.add(new Route(r));
		for (Route r : copy.v2) v2.add(new Route(r));
		this.t1 = copy.t1;
		this.t2 = copy.t2;
	}

	public int getTime(int vNr) {
		return vNr == 0 ? t1 : t2;
	}

	public InsertIndex bestInsertIndex(int order) {
		InsertIndex bestIndex = new InsertIndex();
		Order cur = Order.orders[order];
		for (int i = 0; i < 2; i++) {
			ArrayList<Route> array = i == 0 ? v1 : v2;
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

	public boolean canAddRoute(int vNr, Route r) {
		int t = vNr == 0 ? t1 : t2;
		return r.time <= t;
	}

	public void addRoute(int vNr, Route r) {
		if (vNr == 0) {
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
			(index.vNr == 0 ? v1 : v2).add(r);
		} else {
			(index.vNr == 0 ? v1 : v2).get(index.routeNr).add(index.routeIndex,
					order);
		}
		if (index.vNr == 0) t1 -= index.timeInc;
		else t2 -= index.timeInc;
	}

	public List<Integer> getIds(int vNr) {
		ArrayList<Integer> ret = new ArrayList<>();
		for (Route r : (vNr == 0 ? v1 : v2)) {
			ret.addAll(r.route);
			ret.add(0);
		}
		return ret;
	}
	
	public List<Integer> getOrderIds(int vNr) {
		ArrayList<Integer> ret = new ArrayList<>();
		for (Route r : (vNr == 0 ? v1 : v2)) {
			for (int id : r.route) {
				ret.add(Order.orders[id].orderID);
			}
			ret.add(Order.orders[0].orderID);
		}
		return ret;
	}

	public void debugTime(String extraInfo) {
		for (int v = 0; v < 2; v++) {
			for (Route r : (v == 0 ? v1 : v2)) {
				int calcTime = Constants.DROP_TIME;
				int curI = Constants.DUMP_LOCATION;
				for (int orderNR : r.route) {
					int nxtI = Order.orders[orderNR].matrixID;
					calcTime += Afstanden.tijd[curI][nxtI];
					calcTime += Order.orders[orderNR].emptyTime;
					curI = nxtI;
				}
				calcTime += Afstanden.tijd[curI][Constants.DUMP_LOCATION];
				System.out.println(extraInfo + " v = " + v + ", t = " + r.time
						+ ", vs t = " + calcTime);
			}
		}
	}

	public int twoOpt(int vNr) {
		int diff = 0;
		if (vNr == 0) {
			for (Route r : v1) {
				diff -= r.time;
				r.twoOpt();
				diff += r.time;
			}
			t1 += diff;
		} else {
			for (Route r : v2) {
				diff -= r.time;
				r.twoOpt();
				diff += r.time;
			}
			t2 += diff;
		}
		return diff;
	}

	public int twoHalfOpt(int vNr) {
		int diff = 0;
		if (vNr == 0) {
			for (Route r : v1) {
				diff -= r.time;
				r.twoHalfOpt();
				diff += r.time;
			}
			t1 += diff;
		} else {
			for (Route r : v2) {
				diff -= r.time;
				r.twoHalfOpt();
				diff += r.time;
			}
			t2 += diff;
		}
		return diff;
	}
	
	public int opts(int vNr) {
		int diff = 0;
		if (vNr == 0) {
			for (Route r : v1) {
				diff -= r.time;
				r.opts();
				diff += r.time;
			}
			t1 += diff;
		} else {
			for (Route r : v2) {
				diff -= r.time;
				r.opts();
				diff += r.time;
			}
			t2 += diff;
		}
		return diff;
	}
}