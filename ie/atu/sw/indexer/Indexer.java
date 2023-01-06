package ie.atu.sw.indexer;

/**
 * <pre>
 * Indexer API.
 * </pre>
 * 
 * This interface provides various methods for configuring, building and
 * displaying an index for a given document.
 * 
 * @author Otito Mbelu
 *
 */
public interface Indexer {
	public static final int PRINT_ASCENDING = 0, PRINT_DESCENDING = 1, PRINT_BY_RANGE = 2, PRINT_BY_TOP_OCCURRENCE = 3,
			PRINT_BY_LEAST_OCCURRENCE = 4;

	/**
	 * This method Configures the indexer with a dictionary to provide definition
	 * for entries on the index. This method will query the user for the URI of the
	 * dictionary which is expected in a CSV (Comma Separated Value) format. The URI
	 * supplied will be verified before attempting to parse and any error
	 * encountered will be displayed to the user. The Dictionary is parsed using a
	 * single Virtual Thread per line. The indexer will not build if the Dictionary
	 * is not configured.
	 * 
	 */
	void configureDictionary();

	/**
	 * This methods configures the file to output the built index. This method the
	 * query the user for the URI of the file to write to. The Indexer will not
	 * output any file if the output file is not configured.
	 */
	void configureOutputFile();

	/**
	 * This method retrieves and loads common words that would be ignored during the
	 * index building. This method will query the user for the URI of the text file
	 * to load the words from, the file is expected in a text format with each word
	 * separated by a white space. The Indexer will not build if the common words
	 * are not configured.
	 */
	void configureCommonWords();

	/**
	 * This method gets the URI of the document to be indexed from the user. The
	 * method will query the user to retrieve the document, and will verify that the
	 * file supplied does indeed exist. The Indexer will not build if a textfile is
	 * not specified.
	 */
	void specifyTextFile();

	/**
	 * This method generates the index for a given text file. The index is generated
	 * by going through the file and parsing each word on the file, then filtering
	 * out common words as specified by {@link Indexer#getCommonWords
	 * getCommonWords}, then adding the definition of the word if specified in the
	 * dictionary.
	 */
	void buildIndex();

	/**
	 * This method provides the mechanism for displaying the built index to the
	 * user. The index can be sorted in different order and filtered with different
	 * criteria.
	 * 
	 * @param code - This parameter determines how the index will be printed, the
	 *             following are valid values:
	 * 
	 *<pre>
	 *{@link Indexer#PRINT_ASCENDING PRINT_ASCENDING} - Order the index in ascending order
	 *{@link Indexer#PRINT_DESCENDING PRINT_DESCENDING} - Order the index in descending order 
	 *{@link Indexer#PRINT_RANGE PRINT_RANGE} - Offers a rich set of search
	 *	options. Indices could be ordered by specifying a starting and stopping bounds. E.g. 
	 *	using <code>'a-c'</code> will order all indices starting from letter 'a' to the last
	 *	word starting with 'b' just before c. The inner bound is inclusive while the outer 
	 *	bound is exclusive. Also the inner or outer bounds could be excluded all together 
	 *	respectively i.e <code>'-b'</code> and <code>'a-'</code> are both valid.
	 *{@link Indexer#PRINT_TOP_OCCURRENCE PRINT_TOP_OCCURRENCE} - Order the index by number of top occurrences
	 *{@link Indexer#PRINT_LEAST_OCCURRENCE PRINT_LEAST_OCCURRENCE} - Order the index by the least number occurrences
	 *</pre>
	 * 
	 * @throws UncheckedException - if <code>code</code> specified is out of range
	 */
	void printIndex(int code);

}