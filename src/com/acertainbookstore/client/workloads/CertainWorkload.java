package com.acertainbookstore.client.workloads;

import java.util.HashSet;
import java.util.Set;

import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;

/**
 * Helper class to generate stockbooks and isbns modelled similar to Random
 * class
 */
public class BookSetGenerator {
	private int bookNum = 0;
	private int isbn = 1337;

	public BookSetGenerator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns num randomly selected isbns from the input set
	 * 
	 * @param num
	 * @return
	 */
	public Set<Integer> sampleFromSetOfISBNs(Set<Integer> isbns, int num) {
		if(isbns == null || isbns.size() == 0 || num == 0){
			return null;
		}

		Set<Integer> rand_isbns = new HashSet<Integer>();
		Integer a[] = new Integer[isbns.size()];
		isbns.toArray(a);

		for(int i=0; i<num; i++){
			int idx = (int)(Math.random()*isbns.size());
			rand_isbns.add(a[idx]);
		}
		return rand_isbns;
	}

	/**
	 * Return num stock books. For now return an ImmutableStockBook
	 * 
	 * @param num
	 * @return
	 */
	public Set<StockBook> nextSetOfStockBooks(int num) {
		Set<StockBook> books = new HashSet<StockBook>();
		for(int i=0; i<num; i++){
			int copies = (int)(Math.random()*20);
			boolean ep = copies > 10;
			StockBook book = new ImmutableStockBook(isbn + bookNum, "Introduction to Eduroam vol. " + bookNum,
					"Hitlerik Smørhår", (float) 10, copies+10, 0, 0, 0, ep);
			books.add(book);
			bookNum++;
		}
		return books;
	}

}
