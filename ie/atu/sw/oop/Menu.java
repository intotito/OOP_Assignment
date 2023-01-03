package ie.atu.sw.oop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

public class Menu {
	private String[] options = {
			"Specify Text File",
			"Configure Dictionary",
			"Configure Common Words",
			"Specify Output File",
			"Execute",
			"Quit"
	};
	public Menu() {
		
	}
	
	public int showMenu() throws IOException {
		Formatter.printBoxed("MAIN MENU", 0, '*', '*', '*', 1);
		IntStream.range(0,  options.length).forEach((i) -> {
			System.out.printf("(%d) %s\n", i + 1, options[i]);
		});
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int input = -1;
		String inputStr = null;
		do {
			System.out.printf("Select Option [%d-%d]>", 1, options.length);
			inputStr = br.readLine().trim();
			try {
				input = Integer.parseInt(inputStr);
			} catch(NumberFormatException nfe) {
				input = -1;
				continue;
			}
		} while(input < 1 || input > options.length);
		if(input == options.length) {
			return Runner.EXIT_CODE;
		} else if (input == 5) { 
			
		}
	//	br.close();
		return input;
	}
	
}
