package ie.atu.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

/**
 * This class provides basic Menu functionalities for traversing the different
 * menus of the application.
 * 
 * @author Otito Mbelu
 *
 */
public class Menu {
	private String[] options = { "Specify Text File", "Configure Dictionary", "Configure Common Words",
			"Specify Output File", "Indexer", "Quit" };

	/**
	 * Creates an instance of a Menu
	 */
	public Menu() {

	}

	/**
	 * Displays the menu
	 * 
	 * @return - A code indicating which menu was selected
	 * @throws IOException - If an I/O Exception occurs
	 */
	public int showMenu() throws IOException {
		int indent = 0;
		int input = -1;
		boolean contnue = false;
		outer: do {
			contnue = false;
			Formatter.printBoxed(System.out, "MAIN MENU", indent, '*', '*', '*', 1);
			IntStream.range(0, options.length).forEach((i) -> {
				System.out.printf("(%d) %s\n", i + 1, options[i]);
			});
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String inputStr = null;
			do {
				System.out.printf("Select Option [%d-%d]>", 1, options.length);
				inputStr = br.readLine().trim();
				try {
					input = Integer.parseInt(inputStr);
				} catch (NumberFormatException nfe) {
					input = -1;
					continue;
				}
			} while (input < 1 || input > options.length);
			if (input == options.length) {
				return Runner.EXIT_CODE;
			} else if (input == 5) {
				boolean proceed = false;
				out: do {
					proceed = false;
					indent++;
					String[] menus = { "Build Index", "View Index", "Options", "Print to File", "Back" };
					Formatter.printBoxed(System.out, "INDEXER", indent, '*', '*', '*', 1);
					IntStream.range(0, menus.length).forEach((i) -> {
						System.out.printf("\t(%d) %s\n", i + 1, menus[i]);
					});
					do {

						System.out.printf("\tSelect Option [%d-%d]>", 1, menus.length);
						inputStr = br.readLine().trim();
						try {
							input = Integer.parseInt(inputStr);
						} catch (NumberFormatException nfe) {
							input = -1;
							continue;
						}
					} while (input < 1 || input > menus.length);
					input += 4;
					if (input == 5) { // Build Index

					} else if (input == 6) { // View Index

					} else if (input == 7) { // Options
						input = 12;
					} else if (input == 8) { // print to file
						input = 13;
					} else if (input == 9) { // Back
						indent--;
						contnue = true;
						continue outer;
					}
					if (input == 6) { // View Index
						indent++;
						String[] subMenus = { "Ascending", "Descending", "Range", "Top Occurrence", "Least Occurrence",
								"Back" };
						Formatter.printBoxed(System.out, "VIEW INDEX", indent, '*', '*', '*', 1);
						IntStream.range(0, subMenus.length).forEach((i) -> {
							System.out.printf("\t\t(%d) %s\n", i + 1, subMenus[i]);
						});
						do {

							System.out.printf("\t\tSelect Option [%d-%d]>", 1, subMenus.length);
							inputStr = br.readLine().trim();
							try {
								input = Integer.parseInt(inputStr);
							} catch (NumberFormatException nfe) {
								input = -1;
								continue;
							}
						} while (input < 1 || input > subMenus.length);
						input += 5;
						if (input == 11) { // Back
							indent -= 2;
							proceed = true;
							continue out;
						}
					}
				} while (proceed);
			}
		} while (contnue);
		// br.close();
		return input;
	}

}
