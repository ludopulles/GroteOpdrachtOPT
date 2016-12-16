package checker.validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

public final class DefaultProblem {
	private static TreeMap<Integer, Order> defaultOrders;
	private static int[][] defaultMatrix;
	private static ProblemData defaultProblem;

	public static ProblemData getDefaultProblem() {
		if (defaultProblem == null) {
			try {
				defaultProblem = new ProblemData(getDefaultOrders(), getDefaultMatrix());
			} catch (IOException e) {
				throw new Error(e);
			}
		}
		return defaultProblem;
	}

	public static int[][] getDefaultMatrix() throws IOException {
		if (defaultMatrix == null) {
			BufferedReader br = new BufferedReader(new FileReader("resources/AfstandenMatrix.txt"));
			defaultMatrix = ProblemReader.reader.readDistances(br);
		}
		return defaultMatrix;
	}

	public static TreeMap<Integer, Order> getDefaultOrders() throws IOException {
		if (defaultOrders == null) {
			BufferedReader br = new BufferedReader(new FileReader("resources/Orderbestand.txt"));
			defaultOrders = ProblemReader.reader.readOrders(br);
		}
		return defaultOrders;
	}
}
