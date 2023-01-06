package ie.atu.sw;

import ie.atu.sw.indexer.IndexerImp;

public class Runner {
	public volatile static long progress = 0;
	public static int EXIT_CODE = -1;

	public static void main(String[] args) throws Exception {
		// You should put the following code into a menu or Menu class
		System.out.println(ConsoleColour.CYAN);
		System.out.println("************************************************************");
		System.out.println("*       ATU - Dept. Computer Science & Applied Physics     *");
		System.out.println("*                                                          *");
		System.out.println("*           Virtual Threaded Text IndexerImp               *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");
		/*
		 * System.out.println("(1) Specify Text File");
		 * System.out.println("(2) Configure Dictionary");
		 * System.out.println("(3) Configure Common Words");
		 * System.out.println("(4) Specify Output File");
		 * System.out.println("(5) Execute"); System.out.println("(6) Quit");
		 * 
		 * //Output a menu of options and solicit text from the user
		 * System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
		 * System.out.print("Select Option [1-4]>");
		 */ System.out.println();

//		Formatter.printProgress(100, () -> progress);
		IndexerImp parser = new IndexerImp();
		Menu menu = new Menu();
		int code = 0;
		do {
			code = menu.showMenu();
			parser.execute(code);
		} while (code != EXIT_CODE);
	}

}