package groteopdracht.datastructures;

public class InsertIndex implements Comparable<InsertIndex> {

	public final boolean canAdd, newRoute;
	public final int vNr, routeNr, routeIndex, timeInc;
	
	public InsertIndex() {
		this(false, false, 0, 0, 0, Integer.MAX_VALUE);
	}
	
	public InsertIndex(int vNr, int routeNr, int routeIndex, int timeInc) {
		this(true, false, vNr, routeNr, routeIndex, timeInc);
	}

	public InsertIndex(int vNr, int timeInc) {
		this(true, true, vNr, 0, 0, timeInc);
	}
	
	private InsertIndex(boolean canAdd, boolean newRoute, int vNr, int routeNr, int routeIndex, int timeInc) {
		this.canAdd = canAdd;
		this.newRoute = newRoute;
		this.vNr = vNr;
		this.routeNr = routeNr;
		this.routeIndex = routeIndex;
		this.timeInc = timeInc;
	}

	@Override
	public int compareTo(InsertIndex o) {
		return Integer.compare(this.timeInc, o.timeInc);
	}
}
