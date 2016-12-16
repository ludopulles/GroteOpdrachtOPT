package checker.validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import groteopdracht.Constants;

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
			String file = Constants.RESOURCES_DIR + "/AfstandenMatrix.txt";
			BufferedReader br = new BufferedReader(new FileReader(file));
			defaultMatrix = ProblemReader.reader.readDistances(br);
		}
		return defaultMatrix;
	}

	public static TreeMap<Integer, Order> getDefaultOrders() throws IOException {
		if (defaultOrders == null) {
			String file = Constants.RESOURCES_DIR + "/Orderbestand.txt";
			BufferedReader br = new BufferedReader(new FileReader(file));
			defaultOrders = ProblemReader.reader.readOrders(br);
		}
		return defaultOrders;
	}
}
