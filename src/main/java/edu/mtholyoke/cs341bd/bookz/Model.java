package edu.mtholyoke.cs341bd.bookz;

import java.io.IOException;
import java.util.*;

public class Model {
	Map<String, GutenbergBook> library;
	Map<String, ArrayList<String>> titleTerms;
	List<GutenbergBook> resultBooks;
	HashSet<String> flaggedBooks = new HashSet<String>(); 
	char firstChar; 

	public Model() throws IOException {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);
		// do the hard work:
		DataImport.loadJSONBooks(library);
		initTitleTerms(library);
	}
	
	public void setFirstChar(char firstChar) {
		this.firstChar = firstChar;
	}
	
	public char getFirstChar() {
		return this.firstChar;
	}
	
	public void setFlaggedBooks(HashSet<String> flaggedBooks) {
		this.flaggedBooks = flaggedBooks;
	}
	
	public HashSet<String> getFlaggedBooks() {
		return this.flaggedBooks;
	}
	
	public void setResultBooks (List<GutenbergBook> resultBooks) {
		this.resultBooks = resultBooks;
	}
	public List<GutenbergBook> getResultBooks() {
		return this.resultBooks;
	}

	/**
	 * Initializes a map from terms found in titles and authors to the book ids they are contained in
	 *
	 * @param library
	 */
	private void initTitleTerms(Map<String, GutenbergBook> library) {
		titleTerms = new HashMap<>();
		for (Map.Entry<String, GutenbergBook> entry : library.entrySet()) {
			String title = entry.getValue().title;
			String author = entry.getValue().creator;
			ArrayList<String> terms = new ArrayList(Arrays.asList(title.split(" ")));
			if (author != null)
				terms.addAll(Arrays.asList(author.split(" ")));
			for (String term : terms) {
				if (!titleTerms.containsKey(term)) {
					titleTerms.put(term.toLowerCase(), new ArrayList<>());
				}
				ArrayList<String> ids = titleTerms.get(term.toLowerCase());
				ids.add(entry.getKey());
				titleTerms.put(term, ids);
			}
		}
	}
	public GutenbergBook getBook(String id) {
		return library.get(id);
	}

	public List<GutenbergBook> getBooksStartingWith(char firstChar) {
		// TODO, maybe it makes sense to not compute these every time.
		char query = Character.toUpperCase(firstChar);
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) {
			char first = Character.toUpperCase(book.title.charAt(0));
			if(first == query) {
				matches.add(book);
			}
		}
		return matches;
	}

	/**
	 * Returns a hashet of all the books that match the search query
	 * @param query
	 * @return
	 */
	public HashSet<GutenbergBook> getSearchResults(String query) {
		String[] queryTerms = query.split(" ");
		ArrayList<String> results = new ArrayList<>();
		HashSet<GutenbergBook> retrieve = new HashSet<>();
		for (String term : queryTerms) {
			ArrayList<String> containedIn = titleTerms.get(term.toLowerCase());
			if (titleTerms.containsKey(term.toLowerCase())) {
				results.addAll(containedIn);
			}
		}
		for (String book : results) {
			GutenbergBook gBook = library.get(book);
			retrieve.add(gBook);
		}
		return retrieve;
	}

	public List<GutenbergBook> getRandomBooks(int count) {
		return ReservoirSampler.take(count, library.values());
	}
}
