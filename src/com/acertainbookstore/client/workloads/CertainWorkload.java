package com.acertainbookstore.client.workloads;

import java.util.HashSet;
import java.util.Set;

import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;

/**
 * Helper class to generate stockbooks and isbns modelled similar to Random
 * class
 */
public class CertainWorkload {
	private int bookNum = 0;
	private int isbn = 1337;

	public CertainWorkload() {
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
		for(int i=0; i<10; i++){
			int copies = (int)(Math.random()*20+20);
			StockBook book = new ImmutableStockBook(copies*2+i*500+1, "50 shades of NULL vol." + i,
					"Hitlerik Smørhår", (float) 10, copies, 0, 0, 0, true);

			books.add(book);
			bookNum++;
		}
		return books;
	}

}
