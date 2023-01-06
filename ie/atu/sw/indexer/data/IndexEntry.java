package ie.atu.sw.indexer.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class IndexEntry {
	private String title;
	private DictionaryEntry definition;
	private List<Long> pages;
	private long count = 0;
	
	public IndexEntry(String title) {
		this.title = title;
		pages = new ArrayList<Long>();
	}
	
	public void increment() {
		count++;
	}
	
	public void addPage(long pagenumber) {
		if(!pages.contains(pagenumber)) {
			pages.add(pagenumber);
		}
	}


	@Override
	public String toString() {
		return "IndexEntry [definition=" + definition + ", pages=" + pages + ", count=" + count + "]";
	}

	public void setDefinitions(DictionaryEntry definition) {
		this.definition = definition;
	}
	
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
	
	public String[] splitLine(int WIDTH, String line) {
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
	
	public long getPageOccurrence(){
		return pages.size();
	}
	public long getOccurrence() {
		return count;
	}
}
