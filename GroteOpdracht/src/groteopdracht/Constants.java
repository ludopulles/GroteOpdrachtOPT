
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
	
	public static int MAX_TIME = MINUTE_CONVERSION * 12 * 60;
	public static int MAX_CAPACITY = 20000 * 5;
	
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
}
