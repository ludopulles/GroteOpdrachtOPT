package checker.ui;

import java.awt.Color;
import java.util.ArrayList;

public class ColorGenerator {
	private static final ArrayList<Color> colors = new ArrayList<Color>();

	private static double factor = 1.0D;

	public static Color getColor(int i) {
		while (colors.size() <= i) {
			createNextBatch();
		}

		return (Color) colors.get(i);
	}

	static final int[][] colorLoop = { { 255, 0, 0 }, { 0, 255, 0 }, { 0, 0, 255 }, { 255, 255, 0 }, { 0, 255, 255 },
			{ 255, 0, 255 }, { 255, 200, 0 }, { 200, 255, 0 }, { 0, 255, 200 }, { 0, 200, 255 }, { 255, 0, 200 },
			{ 200, 0, 255 }, { 255, 122, 122 }, { 122, 255, 122 }, { 122, 122, 255 }, { 255, 255, 122 },
			{ 122, 255, 255 }, { 255, 122, 255 } };

	public static void createNextBatch() {
		int[][] arrayOfInt;

		int j = (arrayOfInt = colorLoop).length;
		for (int i = 0; i < j; i++) {
			int[] c = arrayOfInt[i];

			colors.add(new Color((int) (factor * c[0]), (int) (factor * c[1]), (int) (factor * c[2])));
		}
		factor *= 0.8D;
	}
}
