package ie.atu.sw.oop;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Formatter {
	/**
	 * This utility method emulates a Progress Meter that keeps track of a
	 * background process progress. The method executes on a Virtual Thread
	 * 
	 * <pre>
	 * *******************
	 * Big-O Running Time*
	 * *******************
	 * </pre>
	 * 
	 * This method running time is quadratic i.e. <code>O(n²)</code>. It executes a
	 * nested loop.
	 * 
	 * @param total    - The maximum value the background process is expected to
	 *                 reach to indicate it has finished execution.
	 * @param supplier - A {@link Supplier Supplier} Functional Interface that
	 *                 supplies the current progress of the background process. This
	 *                 argument can be supplied in form of a lambda expression or a
	 *                 method reference.
	 */
	public static void printProgress(final long total, Supplier<Long> supplier) {
//		Thread.startVirtualThread(() -> {
		long METER_SIZE = 50;
		char done = '█'; // Change to whatever you like.
		char todo = '░'; // Change to whatever you like.
		long index = 0;
		long progress = 0;
		while (index < total) {
			index = supplier.get();
			System.out.print("\r[");
			progress = (index * 100) / total;
			long pgs = progress;
			LongStream.range(0, METER_SIZE)
					.forEach(i -> System.out.printf("%c", i > pgs * METER_SIZE / 100 ? todo : done));

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.printf("] %d%%  ", progress);
		}
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\r                                                                       \r");
	//	System.out.println("I am finished");
//		});
	}

	/**
	 * This method displays the supplied String in a boxed format
	 * 
	 * <pre>
	 * *******************
	 * Big-O Running Time*
	 * *******************
	 * </pre>
	 * 
	 * This method's running time is <code>O(n)</code> where n is the number of
	 * characters. This value was deducted after evaluating running times of the
	 * outer for loop and the two nested for and stream respectively which is
	 * <code>= O(3×n) ≈ O(n)</code>
	 * 
	 * @param string  - The String to be displayed
	 * @param indent  - The indentation of the display
	 * @param corners - Corners characters for the box
	 * @param vEdge   - Vertical edge characters for the box
	 * @param hEdge   - Horizontal edge characters for the box
	 * @param skip    - Number of spaces to skip while printing the enclosing box
	 */
	public static void printBoxed(String string, int indent, char corners, char vEdge, char hEdge, int skip) {
		final int width = string.length() + (((string.length() + 1) % 2) + 4);
		for (int i = 0; i < 3; i++) {
			System.out.print('\n');
			IntStream.range(0, indent).forEach(k -> System.out.print('\t'));
			for (int j = 0; j < width; j++) {
				if (i == 0 || i == 2) {
					System.out.printf("%c",
							(skip == 0 || j % (skip + 1) == 0 || j == width - 1)
									? ((j == 0 || j == (width - 1)) ? corners : hEdge)
									: '\0');
					// System.out.printf("%c", (j == 0 || j == (width - 1)) ? corners : hEdge);
				} else {
					System.out.printf("%c %s%" + (width - 2 - string.length()) + "c", vEdge, string, vEdge);
					break;
				}
			}
		}
		System.out.println();
	}

	/**
	 * A variant of the
	 * {@link Formatter#printBoxed(String, int, char, char, char, int) printBoxed}
	 * method with a title text.
	 * 
	 * @see Formatter#printBoxed(String, int, char, char, char, int)
	 * @param title   - The title to display with the string
	 * @param string  - The String to be displayed
	 * @param indent  - The indentation of the display
	 * @param corners - Corners characters for the table
	 * @param vEdge   - Vertical edge characters for the table
	 * @param hEdge   - Horizontal edge characters for the table
	 * @param skip    - Number of spaces to skip while printing the enclosing box
	 */
	public static void printBoxedTitled(String title, String string, int indent, char corners, char vEdge, char hEdge,
			int skip) {
		final int width = string.length() + (((string.length() + 1) % 2) + 3);
		final int width1 = title.length() + (((title.length() + 0) % 2) + 3);
		for (int i = 0; i < 3; i++) {
			System.out.print('\n');
			IntStream.range(0, indent).forEach(k -> System.out.print('\t'));
			for (int j = 0; j < width + width1; j++) {
				if (i == 0 || i == 2) {
					System.out.printf("%c",
							(skip == 0 || j % (skip + 1) == 0)
									? ((j == 0 || j == (width1 - 1) || (j == width + width1 - 1)) ? corners : hEdge)
									: '\0');
				} else {
					System.out.printf(
							"%c %s%" + (width1 - 2 - title.length()) + "c %s%" + (width - 1 - string.length()) + "c",
							vEdge, title, vEdge, string, vEdge);
					break;
				}
			}
		}
	}

	/**
	 * A specialized variant of the
	 * {@link Formatter#printBoxedTitled(String, String, int, char, char, char, int)
	 * printBoxedTitled} method that displays an Error message with default title
	 * and corner and edge characters
	 * 
	 * @param msg    - Error message to be displayed
	 * @param indent - The indentation of the display
	 */
	public static void printError(String msg, int indent) {
		printBoxedTitled("Error", msg, indent, '*', '*', '*', 1);
	}
	
	/**
	 * This method prints supplied values in tabular form
	 * 
	 * @param title          - An array containing titles for the columns
	 * @param numberOfValues - Number of rows in the table
	 * @param supplier       - A Function functional interface that gets Each row of
	 *                       the table given an index
	 * @param ratios         - The width of various columns of the table
	 * @param indent         - Indent to start displaying the feed
	 * @param corners        - Corners characters for the table
	 * @param vEdge          - Vertical edge characters for the table
	 * @param hEdge          - Horizontal edge characters for the table
	 */
	public static void printTabular(String[] title, int numberOfValues, Function<Integer, String[]> supplier,
			int[] ratios, int indent, char corners, char vEdge, char hEdge) {
		final int rows = numberOfValues * 2 + 3;
		final int cols = title.length;
		final int unitWidth = 2;
		final int[] WIDTHS = IntStream.range(0, title.length).map(index -> {
			return ratios[index] * unitWidth;
		}).toArray();
//		final int WIDTH = IntStream.of(WIDTHS).sum() + title.length + 1;
		for (int i = 0; i < rows; i++) {
			IntStream.range(0, indent).forEach(index -> System.out.print('\t'));
			System.out.printf("%c", i % 2 == 0 ? corners : vEdge);
			String[] values = (i % 2 == 0 || i == 1) ? null : supplier.apply((i - 2) / 2);
			for (int j = 0; j < cols; j++) {
				if (i % 2 == 0) {
					IntStream.range(0, WIDTHS[j]).forEach(index -> System.out.printf("%c", hEdge));
					System.out.printf("%c", corners);
				} else {
					String s = (i == 1) ? title[j] : values[j].trim();
					s = s.length() > WIDTHS[j] ? s.substring(0, WIDTHS[j] - 5) + "..." : s;
					System.out.printf("%s%" + (WIDTHS[j] - s.length() + 1) + "c", s, vEdge);
				}
			}

			System.out.print('\n');
		}

	}
	/**
	 * Prints values at the bottom of a table by replacing the current line.
	 * 
	 * @param entry   - Array of values to be output on the console
	 * @param ratios  - The width of various columns of the table
	 * @param indent  - Indent to start displaying the feed
	 * @param corners - Corners characters for the table
	 * @param vEdge   - Vertical edge characters for the table
	 * @param hEdge   - Horizontal edge characters for the table
	 */
	public static void printTabularFeed(String[][] entry, int[] ratios, int indent, char corners, char vEdge,
			char hEdge) {
		final int rows = entry.length + 1;
		final int cols = entry[0].length;
		final int unitWidth = 2;
		final int[] WIDTHS = IntStream.range(0, entry[0].length).map(index -> {
			return ratios[index] * unitWidth;
		}).toArray();
		for (int i = 0; i < rows; i++) {
			IntStream.range(0, indent).forEach(index -> System.out.print('\t'));
			System.out.printf("%c", (i == (rows - 1)) ? corners : vEdge);

			for (int j = 0; j < cols; j++) {
				if (i == (rows - 1)) {
					IntStream.range(0, WIDTHS[j]).forEach(index -> System.out.printf("%c", hEdge));
					System.out.printf("%c", corners);
				} else {
					String s = entry[i][j];
					s = s.length() > WIDTHS[j] ? s.substring(0, WIDTHS[j] - 5) + "..." : s;
					System.out.printf("%s%" + (WIDTHS[j] - s.length() + 1) + "c", s, vEdge);
				}
			}

			System.out.print('\n');
		}

	}	
}
