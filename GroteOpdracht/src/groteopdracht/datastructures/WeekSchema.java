package groteopdracht.datastructures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class WeekSchema {

	private DagSchema[] weekschema = new DagSchema[5];

	public WeekSchema() {
		for (int i = 0; i < 5; i++) weekschema[i] = new DagSchema();
	}
	
	public WeekSchema(DagSchema ma, DagSchema tu, DagSchema we, DagSchema th, DagSchema fr) {
		weekschema[0] = ma;
		weekschema[1] = tu;
		weekschema[2] = we;
		weekschema[3] = th;
		weekschema[4] = fr;
	}
	
	public void printSolutionFormat(BufferedWriter w) throws IOException {
		for (int i = 0; i < weekschema.length; i++) {
			int dag = i + 1;
			weekschema[i].debugTime("Dag " + i);
			for (int j = 0; j < 2; j++) {
				List<Integer> arr = weekschema[i].exportRoute(j);
				int sequence = 1;
				for (int k : arr) {
					w.write((j + 1) + "; " + dag + "; " + sequence++ + "; " + Order.orders[k].orderID);
					w.newLine();
				}
			}
		}
	}
	
	/*
	public boolean isValid() {
		// de dagschemas moeten individueel kunnen.
		for (DagSchema ds : weekschema) {
			if(!ds.isValid()) return false;
		}

		//en verder moet de frequentie kloppen.
		for (int i = 1; i < Order.orders.length; i++) {
			int times_collected = 0;
			for(DagSchema ds : weekschema) {
				if(ds.getOrders()[i]) {
					times_collected += 1;
				}
			}
			if (times_collected == 0) {
				continue;
			}
			
			
			if (times_collected != Order.orders[i].frequentie) {
				System.out.println("order " + Order.orders[i].orderID + " collected " + times_collected + " times, expected " + Order.orders[i].frequentie);
				return false;
			}
			
			System.out.println("Order " + Order.orders[i].orderID);
			System.out.println(weekschema[0].getOrders()[i]);
			System.out.println(weekschema[1].getOrders()[i]);
			System.out.println(weekschema[2].getOrders()[i]);
			System.out.println(weekschema[3].getOrders()[i]);
			System.out.println(weekschema[4].getOrders()[i]);
			
			switch(Order.orders[i].frequentie) {
			case 1:
				// kunnen we negeren, want 1 keer ophalen dan sowieso in patroon
				break;
			case 2:
				if (! (    (weekschema[0].getOrders()[i] && weekschema[3].getOrders()[i]) 
						|| (weekschema[1].getOrders()[i] && weekschema[4].getOrders()[i]) ) ) {
					return false;
				}
				break;
			case 3:
				if (!(weekschema[0].getOrders()[i] && weekschema[2].getOrders()[i] && weekschema[4].getOrders()[i])) {
					return false;
				}
				break;
			case 4:
				// er zijn maar 5 mogelijkheden, en deze mogen altijd.
				break;
			case 5:
				// er is maar 1 mogelijkheid, en we tellen per dag, dus kan altijd.
				break;
			default: 
				//zou niet moeten gebeuren.
			}
		}
		
		return true;
	}	
	*/
	
	public static void main(String[] args) {
		/*
		//even testen
		DagSchema ma = new DagSchema();
		DagSchema tu = new DagSchema();
		DagSchema we = new DagSchema();
		DagSchema th = new DagSchema();
		DagSchema fr = new DagSchema();
		
		ma.add(1, 931); //order 931 moet twee keer per week
		ma.add(1, 0);
		ma.add(2, 0);
		tu.add(1, 0);
		tu.add(2, 0);
		we.add(1, 0);
		we.add(2, 0);
		th.add(1, 0);
		th.add(2, 931);
		th.add(2, 0);
		fr.add(1, 0);
		fr.add(2, 0);

		System.out.println(ma.isValid());
		System.out.println(tu.isValid());
		System.out.println(we.isValid());
		System.out.println(th.isValid());
		System.out.println(fr.isValid());
		
		WeekSchema ws = new WeekSchema(ma, tu, we, th, fr);
		ws.printSolutionFormat();
		*/
		
	}

	public InsertIndex bestInsertIndex(int day, int order) {
		return weekschema[day].bestInsertIndex(order);
	}

	public void insert(int day, InsertIndex index, int order) {
		weekschema[day].insert(index, order);
	}
}
