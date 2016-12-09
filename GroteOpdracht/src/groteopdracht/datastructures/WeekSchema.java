
package groteopdracht.datastructures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class WeekSchema {

	private DagSchema[] weekschema = new DagSchema[5];

	public WeekSchema() {
		for (int i = 0; i < 5; i++) {
			weekschema[i] = new DagSchema();
		}
	}

	public WeekSchema(DagSchema ma, DagSchema tu, DagSchema we, DagSchema th,
			DagSchema fr) {
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
					w.write((j + 1) + "; " + dag + "; " + sequence++ + "; "
							+ Order.orders[k].orderID);
					w.newLine();
				}
			}
		}
	}

	public InsertIndex bestInsertIndex(int day, int order) {
		return weekschema[day].bestInsertIndex(order);
	}

	public void insert(int day, InsertIndex index, int order) {
		weekschema[day].insert(index, order);
	}
}
