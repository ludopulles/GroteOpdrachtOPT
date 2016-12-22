
package groteopdracht;

import java.util.Random;

public class Constants {

	public static final String SOLUTIONS_DIR = "solutions";
	public static final String RESOURCES_DIR = "resources";
	public static final String[] DAYS = { "ma", "di", "wo", "do", "vr" };

	public static final int MINUTE_CONVERSION = 600;
	public static final boolean DEBUG = true;
	public static final int MATRIX_IDS = 1099;
	public static final int DROP_TIME = MINUTE_CONVERSION * 30;
	public static final int ORDER_IDS = 1178;
	public static final int DUMP_LOCATION = 287;
	public static final Random RANDOM = new Random(); // pi
	
	public static final int MAX_TIME = MINUTE_CONVERSION * 12 * 60;
	public static final int MAX_CAPACITY = 20000 * 5;
	
	public static final int[][] fivePerms = new int[120][5];
	
	public static final double startT = 7.34375;
//	public static final double stopT = 1e-3;
	public static final double alpha = 0.99;
	public static final int Q = 8 * ORDER_IDS;
	
//	public static int CURRENT_MAX_TIME = MAX_TIME;
//	public static int CURRENT_MAX_CAPACITY = MAX_CAPACITY;
//
//	public static void resetConstraints() {
//		setConstraints(MAX_TIME, MAX_CAPACITY);
//	}
//	
//	public static void setConstraints(int maxTime, int maxCapacity) {
//		System.out.println("CONSTRAINTS ARE CHANGED TO " + maxTime + ", " + maxCapacity);
//		CURRENT_MAX_TIME = maxTime;
//		CURRENT_MAX_CAPACITY = maxCapacity;
//	}
	
	static {
		int idx = 0;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (j == i) continue;
				for (int k = 0; k < 5; k++) {
					if (k == j || k == i) continue;
					for (int l = 0; l < 5; l++) {
						if (l == k || l == j || l == i) continue;
						for (int m = 0; m < 5; m++) {
							if (m == l || m == k || m == j || m == i) continue;
							fivePerms[idx++] = new int[] { i, j, k, l, m };
						}
					}
				}
			}
		}
	}
}
