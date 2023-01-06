package ie.atu.sw.indexer.data;

import java.util.Arrays;

/**
 * This class encapsulates a single entry in the Dictionary
 * @author Otito Mbelu
 *
 */
public class DictionaryEntry {
	private String word;
	private String[] definitions;
	private boolean isPartOfSpeech;
	/**
	 * Creates a new instance of dictionary.
	 * @param word - The word in the dictionary
	 * @param isPOfS - Determines if the word is a 'Part of Speech' or just a definition.
	 */
	public DictionaryEntry(String word, boolean isPOfS) {
		this.word = word;
		isPartOfSpeech = isPOfS;
	}
	@Override
	public String toString() {
		return "DictionaryEntry [word=" + word + ", definitions=" + Arrays.toString(definitions) + ", isPartOfSpeech="
				+ isPartOfSpeech + "]";
	}
	/**
	 * Set the definitions for this Entry
	 * @param def - Array of definitions for this word
	 */
	public void setDefinitions(String[] def) {
		definitions = def;
	}
	/**
	 * Get the various definitions for this word
	 * @return - The definitions
	 */
	public String[] getDefinitions() {
		return definitions;
	}
	/**
	 * Get the word that this class encapsulates.
	 * @return - The word that this class represents in the Dictionary
	 */
	public String getWord() {
		return word;
	}
}
