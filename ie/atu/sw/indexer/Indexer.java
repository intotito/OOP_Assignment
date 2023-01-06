package ie.atu.sw.indexer;

public interface Indexer {

	/**
	 * *
	 * 
	 * This method Configures the parser with a dictionary provided as a URI to a
	 * CSV file This method is responsible for retrieving and verifying the URI of
	 * the dictionary from the user and parsing the file. It will display an error
	 * message to the user when an exception occurs. The CSV file is parsed using a
	 * single Virtual Thread per line
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
	 */
	void configureDictionary();

	/**
	 * This method retrieves a URI of a file to output the index from a user.
	 */
	void configureOutputFile();

	/**
	 * 
	 * This method configures the Parsing Engine with common words which are not
	 * deemed worthy of listing in an index. These common words are expected from a
	 * text file with each word separated by white space.
	 * 
	 * <pre>
	 * *******************
	 * Big-O Running Time*
	 * *******************
	 * </pre>
	 * 
	 * The running time for this method is <code>O(n)</code> where <code>n</code> is
	 * the number of words in the document.
	 */
	void configureCommonWords();

	void specifyTextFile();

	void buildIndex();

}