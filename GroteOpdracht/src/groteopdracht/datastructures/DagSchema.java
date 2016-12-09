
package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import groteopdracht.Constants;

/*
 * Aannames: we splitsen een order niet, dus als we het ophalen, halen we gelijk alles op.
 * verder zorgen we ervoor dat we geen dingen dubbel bezoeken, want twee keer legen heeft geen nut (behalve order 0)
 */
public class DagSchema {

	private ArrayList<Route> v1, v2;
	private int t1, t2;
	/**
	 * bijhouden welke orders we hebben, 0 staat hier ook in, voor makkelijk
	 * indexeren, maar is niet relevant.
	 */
	private BitSet isCollected = new BitSet(Constants.ORDERS_IDS);

	public DagSchema() {
		v1 = new ArrayList<>();
		v2 = new ArrayList<>();
		t1 = t2 = Constants.MAX_TIME;
	}

	public InsertIndex bestInsertIndex(int order) {
		InsertIndex bestIndex = new InsertIndex();
		if (isCollected.get(order)) return bestIndex;
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

	public List<Integer> exportRoute(int j) {
		ArrayList<Integer> ret = new ArrayList<>();
		for (Route r : (j == 0 ? v1 : v2)) {
			ret.addAll(r.route);
			ret.add(0);
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
					calcTime += Order.orders[orderNR].leegTijd;
					curI = nxtI;
				}
				calcTime += Afstanden.tijd[curI][Constants.DUMP_LOCATION];
				System.out.println(extraInfo + " v = " + v + ", t = " + r.time
						+ ", vs t = " + calcTime);
			}
		}
	}
}