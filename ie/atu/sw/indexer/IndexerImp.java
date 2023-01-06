package ie.atu.sw.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ie.atu.sw.Formatter;
import ie.atu.sw.indexer.data.DictionaryEntry;
import ie.atu.sw.indexer.data.IndexEntry;

/**
 * This class forms the basis of an API to index a document. This class provides
 * a suite of public methods necessary to configure, build, process and query an
 * index for a given document.
 * 
 * @author Otito Mbelu
 *
 */
public class IndexerImp implements Indexer {
	private String textFile = null;
	private String dictFile = null;
	private String google_1000File = null;
	private String outputFile = null;
	private int linesPerPage = 40;
	private int pageBreak = 1;
	private volatile static AtomicLong progress = new AtomicLong(0);
	private static AtomicLong lines = new AtomicLong(0);
	private Map<String, IndexEntry> words = new ConcurrentSkipListMap<>();
	private List<String> google_1000;
	private Map<String, DictionaryEntry> dictWords = new ConcurrentSkipListMap<>();

	/**
	 * 
	 * An implementation of the {@link Indexer#configureDictionary
	 * configureDictionary} using a {@link ConcurrentSkipListMap
	 * ConcurrentSkipListMap}
	 *
	 * <pre>
	 * *******************
	 * Big-O Running Time*
	 * *******************
	 * </pre>
	 * 
	 * The running time for this method is <code>O(nLog(n))</code> where
	 * <code>n</code> is the number of entries in the dictionary. The underlying
	 * data structure for mapping each word in the dictionary guarantees a running
	 * time of <code>Log(n)</code> for search and insertion.
	 * 
	 * @see Indexer#configureDictionary
	 */
	@Override
	public void configureDictionary() {
		int indent = 1;
		String path = query("Specify Dictionary File", indent);
		if (path != null && !path.isBlank()) {
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				long size = file.length();
				progress.set(0);
				dictFile = path;
				try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
					es.execute(() -> Formatter.printProgress(size, () -> progress.get()));
					try {
						Files.lines(Paths.get(path)).forEach(line -> es.execute(() -> processDictionary(line)));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				Formatter.printError(System.out, "File does not exist", indent);
			}
		} else {
			Formatter.printError(System.out, "Invalid File path specified", indent);
		}

	}

	/**
	 * This method retrieves a URI of a file to output the index from a user.
	 */
	@Override
	public void configureOutputFile() {
		int indent = 1;
		String path = query("Specify Output File", indent);
		if (path != null && !path.isBlank()) {
			outputFile = path;
		} else {
			Formatter.printError(System.out, "Invalid File path specified", indent);
		}

	}

	/**
	 * An implementation of the {@link Indexer#configureCommonWords
	 * configureCommonWords} using an {@link ArrayList ArrayList}
	 * 
	 * <pre>
	 * *******************
	 * Big-O Running Time*
	 * *******************
	 * </pre>
	 * 
	 * The running time for this method is <code>O(n)</code> where <code>n</code> is
	 * the number of words in the document. The ArrayList guarantees that data will
	 * be shoved into the collection at O(1).
	 * 
	 * @see Indexer#configureCommonWords
	 */
	@Override
	public void configureCommonWords() {
		int indent = 1;
		String path = query("Specify Common Words File", indent);
		if (path != null && !path.isBlank()) {
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				long size = file.length();
				progress.set(0);
				google_1000File = path;
				Thread t = Thread.startVirtualThread(() -> Formatter.printProgress(size, () -> progress.get()));
				try {
					this.google_1000 = Files.lines(Paths.get(google_1000File)).map((line) -> {
						int lineSize = line.length() + 1;
						progress.set(progress.get() + lineSize);
						return line.toLowerCase();
					}).collect(Collectors.toList());
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				Formatter.printError(System.out, "File does not exist", indent);
			}
		} else {
			Formatter.printError(System.out, "Invalid File path specified", indent);
		}

	}

	/**
	 * A convenient method for querying and a menu and running the various methods
	 * of the API
	 * 
	 * @param code - Code retrieved from menu operation. The value of the code is
	 *             used internally to determine which command to invoke.
	 */
	public void execute(int code) {
		switch (code) {
		case 1:
			specifyTextFile();
			break;
		case 2:
			configureDictionary();
			break;
		case 3:
			configureCommonWords();
			break;
		case 4:
			configureOutputFile();
			break;
		case 5:
			buildIndex();
			break;
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			printIndex(code - 6);
			break;
		case 12:
			setOptions();
			break;
		case 13:
			printToFile();
		default:
			break;
		}
	}

	/*
	 * Provides a submenu for setting options that controls how output is displayed
	 * to the user. This includes: setting number of lines per page and number of
	 * page to order page break.
	 */
	private void setOptions() {
		int indent = 2;
		String inputStr = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int input = -1;
		String[] subMenus = { "Lines per Page", "Page break", "Back" };
		Formatter.printBoxed(System.out, "OPTIONS", indent, '*', '*', '*', 1);
		IntStream.range(0, subMenus.length).forEach((i) -> {
			System.out.printf("\t\t(%d) %s\n", i + 1, subMenus[i]);
		});
		do {

			System.out.printf("\t\tSelect Option [%d-%d]>", 1, subMenus.length);
			try {
				inputStr = br.readLine().trim();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				input = Integer.parseInt(inputStr);
			} catch (NumberFormatException nfe) {
				input = -1;
				continue;
			}
		} while (input < 1 || input > subMenus.length);
		if (input == 1) {
			try {
				linesPerPage = Integer.parseInt(query(String.format("Lines per Page[%d]>", linesPerPage), indent));
			} catch (NumberFormatException nfe) {
				Formatter.printError(System.out, "Invalid Entry", 2);
			}
		} else if (input == 2) {
			try {
				pageBreak = Integer.parseInt(query(String.format("Page break[%d]>", pageBreak), indent));
			} catch (NumberFormatException nfe) {
				Formatter.printError(System.out, "Invalid Entry", 2);
			}
		}
	}

	/*
	 * This instance variable is used break of the output display loop when a user
	 * enters a key to quit displaying outputs.
	 */
	private volatile boolean contnue = true;
	/*
	 * This instance variable is used to keep track of the number of pages traversed
	 * so far by the display engine.
	 */
	private int pageCount = 0;
	/*
	 * This instance variable is used to keep track of the number of lines traversed
	 * so far by the display engine.
	 */
	private int lineCount = 0;

	@Override
	public void printToFile() {
		if (words.isEmpty()) {
			Formatter.printError(System.out, "Index yet to be built", 1);
			return;
		} else if (outputFile == null) {
			Formatter.printError(System.out, "Output File not specified", 1);
			return;
		}
		try (PrintStream out = new PrintStream(outputFile)) {
			String[] title = { "Word", "Detail" };
			int[] ratio = { 10, 35 };
			progress.set(1);
	//		Thread.startVirtualThread();
			try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
				es.execute(() -> Formatter.printProgress(words.size(), () -> progress.get()));
				es.execute(() -> {
						Formatter.printTabular(out, title, 0, null, ratio, 2, '+', '|', '-');
						words.entrySet().stream().forEach((v) -> {
							progress.incrementAndGet();
							IndexEntry e = v.getValue();
							String[][] rows = e.getAllRows(70);
							Formatter.printTabularFeed(out, rows, ratio, 2, '+', '|', '-');
						});
				});
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see Indexer#printIndex
	 * 
	 * @throws IllegalArgumentException - If parameter out of range
	 */

	@Override
	public void printIndex(int code) {
		if (words.isEmpty()) {
			Formatter.printError(System.out, "Index yet to be built", 2);
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		pageCount = 0;
		lineCount = 0;
		contnue = true;
		Comparator<Entry<String, IndexEntry>> comp = null;
		Predicate<Entry<String, IndexEntry>> filter = null;
		if (code == 0) { // Ascending Order
			comp = (e1, e2) -> e1.getKey().compareTo(e2.getKey());
			filter = (e) -> true;
		} else if (code == 1) { // Descending Order
			comp = (e1, e2) -> -e1.getKey().compareTo(e2.getKey());
			filter = (e) -> true;
		} else if (code == 2) { // Range
			String query = query("Enter range (E.g. 'a-b')>", 2);
			comp = (e1, e2) -> e1.getKey().compareTo(e2.getKey());

			Pattern pattern = Pattern.compile("[\\W]*?([\\w]+)?-(\\w+)?.*");
			Matcher matcher = pattern.matcher(query);
			if (matcher.find()) {
				String start = matcher.group(1);
				String stop = matcher.group(2);
				filter = e -> {
					boolean first = false;
					boolean second = false;
					if (start == null) {
						first = true;
					} else {
						first = e.getKey().compareTo(start) >= 0;
					}

					if (stop == null) {
						second = true;
					} else {
						second = e.getKey().compareTo(stop) <= 0;
					}
					return first && second;
				};
			} else {
				Formatter.printError(System.out, "Invalid Range Entered", 2);
				return;
			}
		} else if (code == 3) { // Top occurrence
			comp = (e1, e2) -> (int) (e2.getValue().getOccurrence() - e1.getValue().getOccurrence());
			filter = (e) -> true;
		} else if (code == 4) { // Least occurrence
			comp = (e1, e2) -> ((int) e1.getValue().getOccurrence() - (int) e2.getValue().getOccurrence());
			filter = (e) -> true;
		} else {
			throw new IllegalArgumentException("Invalid argument supplied");
		}

		String[] title = { "Word", "Detail" };
		int[] ratio = { 10, 35 };
		Formatter.printTabular(System.out, title, 0, null, ratio, 2, '+', '|', '-');
		words.entrySet().stream().filter(filter).sorted(comp).takeWhile((e) -> contnue).forEach((v) -> {
			IndexEntry e = v.getValue();
			String[][] rows = e.getAllRows(70);
			Formatter.printTabularFeed(System.out, rows, ratio, 2, '+', '|', '-');
			lineCount += rows.length;
			if (lineCount / linesPerPage > pageCount + pageBreak - 1) {
				pageCount = lineCount / linesPerPage;
				System.out.print("\t\tPress 'Enter' to continue or Enter any key to exit> ");
				try {
					String s = br.readLine();
					if (s.isBlank()) {
						System.out.print("\r                                                       \r");
					} else {
						contnue = false;
						return;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	/*
	 * A utility method for getting String input from the user.
	 */
	private String query(String message, int indent) {
		Formatter.printBoxed(System.out, message, indent, '+', '|', '-', 1);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		IntStream.range(0, indent).forEach(k -> System.out.print('\t'));
		System.out.print(">");
		String path = null;
		try {
			path = br.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return path;
	}

	@Override
	public void specifyTextFile() {
		int indent = 1;
		String path = query("Specify Text File", indent);
		if (path != null && !path.isBlank()) {
			File file = new File(path);

			if (file.exists() && file.isFile()) {
				textFile = path;
			} else {
				Formatter.printError(System.out, "File does not exist", indent);
			}
		} else {
			Formatter.printError(System.out, "Invalid File path specified", indent);
		}
	}

	/**
	 * An implementation of the {@link Indexer#buildIndex buildIndex} method using
	 * using a {@link ConcurrentSkipListMap ConcurrentSkipListMap}
	 * 
	 * <pre>
	 * *******************
	 * Big-O Running Time*
	 * *******************
	 * </pre>
	 * 
	 * The running time for this method is <code>O(n × m × Log(p))</code> where
	 * <code>n</code> is the number of selected words to be indexed in the document,
	 * <code>m</code> is the number of entries in the common words and
	 * <code>p</code> is the number of entries in the dictionary.
	 * 
	 * @see Indexer#buildIndex
	 */
	@Override
	public void buildIndex() {
		if (textFile == null) {
			Formatter.printError(System.out, "Cannot build Index, Text File not specified", 1);
			return;
		}
		if (dictFile == null) {
			Formatter.printError(System.out, "Cannot build Index, Dictionary not Configured", 1);
			return;
		}
		if (google_1000File == null) {
			Formatter.printError(System.out, "Cannot build Index, Common Words not Configured", 1);
			return;
		}
		words.clear();
		File file = new File(textFile);
		long time = System.currentTimeMillis();
		try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
			progress.set(0);
			es.execute(() -> Formatter.printProgress(file.length(), () -> progress.get()));
			Files.lines(Paths.get(textFile)).forEach(line -> es.execute(() -> process(line, lines.incrementAndGet())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		time = System.currentTimeMillis() - time;
		String[] title = { "Build Time", String.format("%dms", time) };
		int[] ratios = { 10, 10 };
		String[][] vs = { { "File Name", file.getName() }, { "Unique words", "" + words.size() },
				{ "Number of Pages", (lines.get() / 40) + "" } };
		Formatter.printBoxed(System.out, "BUILD SUMMARY", 1, '+', '|', '-', 0);
		Formatter.printTabular(System.out, title, 2, (i) -> vs[i], ratios, 1, '+', '|', '-');
		words.entrySet().stream().sorted((e1, e2) -> {
			return -(e1.getKey().compareTo(e2.getKey()));
		});
	}

	/*
	 * This method processes every line of the document and filters out select
	 * special characters such as found in the start or end of words or isolated
	 * group of special characters.Blank characters are also filtered out.
	 */
	private void process(String line, long lineNumber) {
		progress.set(progress.get() + line.length() + 4);
		String[] words = Arrays.stream(line.trim().split("\\s+")).filter(Predicate.not(String::isBlank))
				.map((w) -> w.replaceAll("^[^a-zA-Z0-9]+", "").replaceAll("[^a-zA-Z0-9]+$", "")).toArray(String[]::new);
		Arrays.stream(words).map(String::toLowerCase).forEach(word -> {
			if (!google_1000.contains(word)) {

				IndexEntry index = this.words.get(word);
				if (index != null) {

				} else {
					index = new IndexEntry(word);
					this.words.put(word, index);
				}
				index.increment();
				long pageNum = getPageNumber(lineNumber);
				index.addPage(pageNum);

				DictionaryEntry de = dictWords.get(word);
				if (de == null) {

				} else {
					index.setDefinitions(de);
				}
			}
		});
	}

	/*
	 * This method process every word in a given line, and maps each word to a
	 * DictionaryEntry object, which is in turn put into a Map containing every word
	 * (Encapsulated as a DictionaryEntry) in the dictionary.
	 * 
	 * @param line
	 */
	private void processDictionary(final String line) {
		progress.set(progress.get() + line.length() + 4);
		String[] parts = line.split(",");
		String subject = parts[0].toLowerCase();
		String others = parts[1];
		boolean isPartOfSpeech = others.matches("^(.+?):.*?");
		String[] myLines = others.split(";\\s(?!--)");
		DictionaryEntry dEntry = new DictionaryEntry(subject, isPartOfSpeech);
		dEntry.setDefinitions(myLines);
		dictWords.put(subject, dEntry);
	}

	/*
	 * A utility method for retrieving the page number given a line number
	 */
	private long getPageNumber(long lineNumber) {
		return lineNumber / linesPerPage;
	}

}
