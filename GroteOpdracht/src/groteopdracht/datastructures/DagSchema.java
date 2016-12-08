package groteopdracht.datastructures;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import groteopdracht.Main;

/*
 * Aannames: we splitsen een order niet, dus als we het ophalen, halen we gelijk alles op.
 * verder zorgen we ervoor dat we geen dingen dubbel bezoeken, want twee keer legen heeft geen nut (behalve order 0)
 */
public class DagSchema {

	public final static int MAX_TIME = Main.MINUTE_CONVERSION * 12 * 60 - 1;
	public final static int MAX_CAPACITY = 20000 * 5;

	private ArrayList<Route> v1, v2;
//	private int t1, t2;
	
	/** 
	 * bijhouden welke orders we hebben,
	 * 0 staat hier ook in, voor makkelijk indexeren, maar is niet relevant.
	 */
	private BitSet isCollected = new BitSet(Order.ORDERS_IDS);
	
	public DagSchema() {
		v1 = new ArrayList<>();
		v2 = new ArrayList<>();
	}

	public InsertIndex bestInsertIndex(int order) {
		if (isCollected.get(order)) return new InsertIndex();

		InsertIndex bestIndex = new InsertIndex();
		int mID = Order.orders[order].matrixID;
		int dumpTime = Order.orders[order].leegTijd;
		
		for (int i = 0; i < 2; i++) {
			ArrayList<Route> arr = i == 0 ? v1 : v2;
			int tTime = 0;
			for (int j = 0; j < arr.size(); j++) {
				tTime += arr.get(j).time;
			}
			for (int j = 0; j < arr.size(); j++) {
				Route r = arr.get(j);
				if (!r.canAdd(order)) continue;
				for (int k = 0, rl = r.length(); k <= rl; k++) {
					int lneighbour = Order.orders[k == 0 ? 0 : r.get(k - 1)].matrixID;
					int rneighbour = Order.orders[k == rl ? 0 : r.get(k)].matrixID;
					// lneighbour -> k -> rneighbour
					int timeL = Afstanden.tijd[lneighbour][mID];
					int timeR = Afstanden.tijd[mID][rneighbour];
					int timeOld = Afstanden.tijd[lneighbour][rneighbour];
					int deltaTime = timeL + timeR - timeOld + dumpTime;
					if (tTime + deltaTime <= DagSchema.MAX_TIME) {
						// dan mag het.
						InsertIndex insert = new InsertIndex(i, j, k, deltaTime);
						if (bestIndex.compareTo(insert) > 0) bestIndex = insert;
					}
				}
			}
			
			// nieuwe route:
			int newRouteTime = Order.DROP_TIME + Afstanden.tijd[Order.orders[0].matrixID][mID] + Afstanden.tijd[mID][Order.orders[0].matrixID] + dumpTime;
			InsertIndex insert = new InsertIndex(i, newRouteTime);
			// System.out.println(bestIndex.timeInc + " VS " + insert.timeInc + " GIVES " + bestIndex.compareTo(insert));
			if (tTime + newRouteTime <= DagSchema.MAX_TIME && bestIndex.compareTo(insert) > 0) {
				bestIndex = insert;
			}
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
			(index.vNr == 0 ? v1 : v2).get(index.routeNr).add(index.routeIndex, order);
		}
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
		for (Route r : v1) {
			int calcTime = Order.DROP_TIME;
			int curI = Order.orders[0].matrixID;
			for (int orderNR : r.route) {
				int nxtI = Order.orders[orderNR].matrixID;
				calcTime += Afstanden.tijd[curI][nxtI];
				calcTime += Order.orders[orderNR].leegTijd;
				
				System.out.println(curI + " -> " + nxtI + ": " + Afstanden.tijd[curI][nxtI]);
				System.out.println("EMPTYING " + orderNR + " IN " + Order.orders[orderNR].leegTijd);
				
				curI = nxtI;
			}
			calcTime += Afstanden.tijd[curI][Order.orders[0].matrixID];
			System.out.println(curI + " -> " + 0 + ": " + Afstanden.tijd[curI][Order.orders[0].matrixID]);
			System.out.println(extraInfo + " TIME: " + r.time + " VS " + calcTime);
		}
	}
}