package ie.atu.sw.indexer.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * This class Encapsulate a single entry in the Index. 
 * @author Otito Mbelu
 *
 */
public class IndexEntry {
	private String title;
	private DictionaryEntry definition;
	private List<Long> pages;
	private long count = 0;
	/**
	 * Create a new instance of IndexEntry
	 * @param title - The word defined by this IndexEntry
	 */
	public IndexEntry(String title) {
		this.title = title;
		pages = new ArrayList<Long>();
	}
	/**
	 * Increment the number of occurrence of this entry.
	 */
	public void increment() {
		count++;
	}
	/**
	 * Add a page to the list of pages where this entry occurs. 
	 * If the page already exists it will not be added
	 * @param pagenumber - The page number to add to the list of pages
	 */
	public void addPage(long pagenumber) {
		if(!pages.contains(pagenumber)) {
			pages.add(pagenumber);
		}
	}


	@Override
	public String toString() {
		return "IndexEntry [definition=" + definition + ", pages=" + pages + ", count=" + count + "]";
	}
/**
 * Sets the definition of this word entry
 * @param definition - The DictionaryEntry definition.
 */
	public void setDefinitions(DictionaryEntry definition) {
		this.definition = definition;
	}
	/**
	 * This method is used for formatting each entry to the console.
	 * This method gets all definitions (if present) and pages where the entry occurs
	 * as a separate line. Each line is formatted with the maximum width of the page, supplied as 
	 * a parameter to the method.
	 * @param WIDTH
	 * @return
	 */
	public String[][] getAllRows(int WIDTH){
		List<String> rows = new ArrayList<String>();
		if(this.definition != null) {
			rows.add("Definition");
			for(int i = 0; i < definition.getDefinitions().length; i++) {
				Stream.of(splitLine(WIDTH, definition.getDefinitions()[i])).forEach(rows::add);
			}
		}
		rows.add("Pages");
		Stream.of(splitLine(WIDTH, pages.stream().map(String::valueOf).collect(Collectors.joining(", ")))).forEach(rows::add);
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[][] rr = new String[rows.size()][2];
		for(int i = 0; i < rr.length; i++) {
			rr[i] = new String[] {i == 0 ? title: " ",  rows.get(i)};
		//	System.out.println("truth::::: " + title + "   ::::: " + rows.get(i));
		}
		return rr;
	}
	/*
	 * Split the given string into multiple lines if the width is greater than the 
	 * specified width 'WIDTH'.
	 */
	private String[] splitLine(int WIDTH, String line) {
		String[] words = line.split("\\s+");
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(int i = 0; i < words.length; i++) {
			if(count + words[i].length() + 1> WIDTH) {
				sb.append("\n");
				count = 0;
			}
			sb.append(words[i] + " ");
			count += words[i].length() + 1;
		}
		return sb.toString().split("\\n");
	}
	/**
	 * Get the number of different pages this entry occurs.
	 * @return - Number of pages this entry occurs
	 */
	public long getPageOccurrence(){
		return pages.size();
	}
	/**
	 * Gets the number of occurrence of this entry in the document.
	 * @return - The number of times this entry occurs.
	 */
	public long getOccurrence() {
		return count;
	}
}
