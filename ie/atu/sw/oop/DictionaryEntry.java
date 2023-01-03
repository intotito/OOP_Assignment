package ie.atu.sw.oop;

import java.util.Arrays;
import java.util.List;

public class DictionaryEntry {
	private String word;
	private String[] definitions;
	private boolean isPartOfSpeech;
	public DictionaryEntry(String word, boolean isPOfS) {
		this.word = word;
		isPartOfSpeech = isPOfS;
	}
	@Override
	public String toString() {
		return "DictionaryEntry [word=" + word + ", definitions=" + Arrays.toString(definitions) + ", isPartOfSpeech="
				+ isPartOfSpeech + "]";
	}
	public void setDefinitions(String[] def) {
		definitions = def;
	}
	
	public String[] getDefinitions() {
		return definitions;
	}
}
