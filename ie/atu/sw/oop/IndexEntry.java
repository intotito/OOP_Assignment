package ie.atu.sw.oop;

import java.util.List;
import java.util.ArrayList;
public class IndexEntry {
	private DictionaryEntry definition;
	private List<Long> pages;
	private long count = 0;
	
	public IndexEntry() {
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
	
	
}
