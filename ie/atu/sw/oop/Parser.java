package ie.atu.sw.oop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Parser {
	private static AtomicLong lines = new AtomicLong(0);
	Map<String, IndexEntry> words = new ConcurrentSkipListMap<>();
	List<String> google_1000;
	Map<String, DictionaryEntry> dictWords = new ConcurrentSkipListMap<>();

	public void configureDictionary(String path) {
		try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
			try {
				Files.lines(Paths.get(path)).forEach(line -> es.execute(() -> processDictionary(line)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void configureCommonWords() {

	}

	public void specifyTextFile(String path) {
		try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
			try {
				Files.lines(Paths.get(path)).forEach(line -> es.execute(() -> process(line, lines.incrementAndGet())));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Parser(String path, String google_1000, String dictionary) {
//		String dataPath = "./data_src/";
//		Path[] filesPath = Stream.of(new File(dataPath).listFiles()).filter(Predicate.not(File::isDirectory)).map(File::toPath).toArray(Path[]::new);
//		Arrays.stream(filesPath).forEach(p -> {
		try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {

			try {
				Files.lines(Paths.get(dictionary)).forEach(this::processDictionary);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				this.google_1000 = Files.lines(Paths.get(google_1000)).map(String::toLowerCase)
						.collect(Collectors.toList());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Files.lines(Paths.get(path)).forEach(line -> es.execute(() -> process(line, lines.incrementAndGet())));

		} catch (IOException e) {
			e.printStackTrace();
		}
//		});

		// this.google_1000.forEach(System.out::println);

		words.entrySet().stream().sorted((e1, e2) -> {
			return -(e1.getKey().compareTo(e2.getKey()));
		}).forEach((v) -> {
			System.out.printf("Key: %s, value: %s\n", v.getKey(), v.getValue());
		});

		/*
		 * words.forEach((i, v) -> { System.out.printf("key: %s, value: %s\n", i,
		 * v.toString()); });
		 */

		String dictPath = "./dictionary.csv";

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

	private void processDictionary(String line) {
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
