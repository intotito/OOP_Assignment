package ie.atu.sw.oop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Parser {
	private String textFile = null;
	private String dictFile = null;
	private String google_1000File = null;
	private String outputFile = null;
	private volatile static AtomicLong progress = new AtomicLong(0);
	private static AtomicLong lines = new AtomicLong(0);
	Map<String, IndexEntry> words = new ConcurrentSkipListMap<>();
	List<String> google_1000;
	Map<String, DictionaryEntry> dictWords = new ConcurrentSkipListMap<>();

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
				Formatter.printError("File does not exist", indent);
			}
		} else {
			Formatter.printError("Invalid File path specified", indent);
		}

	}

	public void configureOutputFile() {
		int indent = 1;
		String path = query("Specify Output File", indent);
		if (path != null && !path.isBlank()) {
			outputFile = path;
		} else {
			Formatter.printError("Invalid File path specified", indent);
		}

	}

	public void configureCommonWords() {
		int indent = 1;
		String path = query("Specify Common Words File", indent);
		if (path != null && !path.isBlank()) {
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				long size = file.length();
				// System.out.println("Size of file: " + size);
				progress.set(0);
				google_1000File = path;
				// try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
				Thread t = Thread.startVirtualThread(() -> Formatter.printProgress(size, () -> progress.get()));
				// es.execute(() -> {
				try {
					this.google_1000 = Files.lines(Paths.get(google_1000File)).map((line) -> {
						int lineSize = line.length() + 1;
						progress.set(progress.get() + lineSize);
						return line.toLowerCase();
					}).collect(Collectors.toList());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// });

				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// }
			} else {
				Formatter.printError("File does not exist", indent);
			}
		} else {
			Formatter.printError("Invalid File path specified", indent);
		}

	}

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
		default:
			break;
		}
	}

	private String query(String message, int indent) {
		Formatter.printBoxed(message, indent, '+', '|', '-', 1);
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

	public void specifyTextFile() {
		int indent = 1;
		String path = query("Specify Text File", indent);
		if (path != null && !path.isBlank()) {
			File file = new File(path);

			if (file.exists() && file.isFile()) {
				textFile = path;
				/*
				 * try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) { try
				 * { Files.lines(Paths.get(path)) .forEach(line -> es.execute(() ->
				 * process(line, lines.incrementAndGet()))); } catch (IOException e) {
				 * e.printStackTrace(); } }
				 */
			} else {
				Formatter.printError("File does not exist", indent);
			}
		} else {
			Formatter.printError("Invalid File path specified", indent);
		}
	}

	public Parser() {

	}

	public void buildIndex() {
		if(textFile == null) {
			Formatter.printError("Cannot build Index, Text File not specified", 1);
			return;
		}
		if(dictFile == null) {
			Formatter.printError("Cannot build Index, Dictionary not Configured", 1);
			return;
		}
		if(google_1000File == null) {
			Formatter.printError("Cannot build Index, Common Words not Configured", 1);
			return;
		}
		try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
			Files.lines(Paths.get(textFile)).forEach(line -> es.execute(() -> process(line, lines.incrementAndGet())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		words.entrySet().stream().sorted((e1, e2) -> {
			return -(e1.getKey().compareTo(e2.getKey()));
		}).forEach((v) -> {
			System.out.printf("Key: %s, value: %s\n", v.getKey(), v.getValue());
		});
	}

	private void process(String line, long lineNumber) {
//		System.out.println(line);
		String[] words = line.trim().replaceAll("[^a-zA-Z0-9\\s+]", "").split("\\s+");
		Arrays.stream(words).map(String::toLowerCase).forEach(word -> {
			// System.out.println("\t"+word);
			if (!google_1000.contains(word)) {

				IndexEntry index = this.words.get(word);
				if (index != null) {

				} else {
					index = new IndexEntry();
					this.words.put(word, index);
					// search dictionary and add definition
				}
				index.increment();
				long pageNum = getPageNumber(lineNumber);
				index.addPage(pageNum);

				DictionaryEntry de = dictWords.get(word);
				if (de == null) {

				} else {
					index.setDefinitions(de);
				}

			} else {
				// System.out.println("XXXXXXXX _---- " + word);
			}

		});
	}

	private void processDictionary(final String line) {
		progress.set(progress.get() + line.length() + 3);
		String[] parts = line.split(",");
		String subject = parts[0].toLowerCase();
		String others = parts[1];
		boolean isPartOfSpeech = others.matches("^(.+?):.*?");
		String[] myLines = others.split(";\\s(?!--)");
		DictionaryEntry dEntry = new DictionaryEntry(subject, isPartOfSpeech);
		dEntry.setDefinitions(myLines);
		dictWords.put(subject, dEntry);
	}

	private long getPageNumber(long lineNumber) {
		return lineNumber / 40;
	}

	private void parse(String str) {

	}
}
