package groteopdracht.datastructures;

public class InsertIndex implements Comparable<InsertIndex> {

	public final boolean canAdd, newRoute;
	public final int vNr, routeNr, routeIndex, timeInc;
	
	public InsertIndex() {
		this.canAdd = false;
		this.newRoute = false;
		this.vNr = this.routeNr = this.routeIndex = 0;
		this.timeInc = Integer.MAX_VALUE;
	}
	
	public InsertIndex(int vNr, int routeNr, int routeIndex, int timeInc) {
		this.canAdd = true;
		this.newRoute = false;
		this.vNr = vNr;
		this.routeNr = routeNr;
		this.routeIndex = routeIndex;
		this.timeInc = timeInc;
	}

	public InsertIndex(int vNr, int timeInc) {
		this.canAdd = true;
		this.newRoute = true;
		this.vNr = vNr;
		this.routeNr = this.routeIndex = 0;
		this.timeInc = timeInc;
	}

	@Override
	public int compareTo(InsertIndex o) {
		return Integer.compare(this.timeInc, o.timeInc);
	}
}
