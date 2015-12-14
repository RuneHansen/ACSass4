/**
 * 
 */
package com.acertainbookstore.client.workloads;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import com.acertainbookstore.business.Book;
import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.utils.BookStoreException;

/**
 * 
 * Worker represents the workload runner which runs the workloads with
 * parameters using WorkloadConfiguration and then reports the results
 * 
 */
public class Worker implements Callable<WorkerRunResult> {
	private WorkloadConfiguration configuration = null;
	private int numSuccessfulFrequentBookStoreInteraction = 0;
	private int numTotalFrequentBookStoreInteraction = 0;

	public Worker(WorkloadConfiguration config) {
		configuration = config;
	}

	/**
	 * Run the appropriate interaction while trying to maintain the configured
	 * distributions
	 * 
	 * Updates the counts of total runs and successful runs for customer
	 * interaction
	 * 
	 * @param chooseInteraction
	 * @return
	 */
	private boolean runInteraction(float chooseInteraction) {
		try {
			if (chooseInteraction < configuration
					.getPercentRareStockManagerInteraction()) {
				runRareStockManagerInteraction();
			} else if (chooseInteraction < configuration
					.getPercentFrequentStockManagerInteraction()) {
				runFrequentStockManagerInteraction();
			} else {
				numTotalFrequentBookStoreInteraction++;
				runFrequentBookStoreInteraction();
				numSuccessfulFrequentBookStoreInteraction++;
			}
		} catch (BookStoreException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Run the workloads trying to respect the distributions of the interactions
	 * and return result in the end
	 */
	public WorkerRunResult call() throws Exception {
		int count = 1;
		long startTimeInNanoSecs = 0;
		long endTimeInNanoSecs = 0;
		int successfulInteractions = 0;
		long timeForRunsInNanoSecs = 0;

		Random rand = new Random();
		float chooseInteraction;

		// Perform the warmup runs
		while (count++ <= configuration.getWarmUpRuns()) {
			chooseInteraction = rand.nextFloat() * 100f;
			runInteraction(chooseInteraction);
		}

		count = 1;
		numTotalFrequentBookStoreInteraction = 0;
		numSuccessfulFrequentBookStoreInteraction = 0;

		// Perform the actual runs
		startTimeInNanoSecs = System.nanoTime();
		while (count++ <= configuration.getNumActualRuns()) {
			chooseInteraction = rand.nextFloat() * 100f;
			if (runInteraction(chooseInteraction)) {
				successfulInteractions++;
			}
		}
		endTimeInNanoSecs = System.nanoTime();
		timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
		return new WorkerRunResult(successfulInteractions,
				timeForRunsInNanoSecs, configuration.getNumActualRuns(),
				numSuccessfulFrequentBookStoreInteraction,
				numTotalFrequentBookStoreInteraction);
	}

	/**
	 * Runs the new stock acquisition interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runRareStockManagerInteraction() throws BookStoreException {
		List<StockBook> LSB = configuration.getStockManager().getBooks();
		
		
		Set<StockBook> SSB = configuration.getBookSetGenerator().nextSetOfStockBooks(
				configuration.getNumBooksToAdd());
		Set<StockBook> BTA = new HashSet<StockBook>();
		for(StockBook book : LSB) {
			if(!SSB.contains(book.getISBN())) {
				BTA.add(book);
			}
		}
		
		configuration.getStockManager().addBooks(BTA);
		
	}

	/**
	 * Runs the stock replenishment interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runFrequentStockManagerInteraction() throws BookStoreException {
		List<StockBook> LSB = configuration.getStockManager().getBooks();
		
		Collections.sort(LSB, new SBComparator());
		
		Set<BookCopy> SBC = new HashSet<BookCopy>();
		LSB = LSB.subList(0, configuration.getNumBooksWithLeastCopies());
		
		for(StockBook book : LSB) {
			BookCopy tmp = new BookCopy(book.getISBN(), configuration.getNumAddCopies());
			SBC.add(tmp);
		}
		
		configuration.getStockManager().addCopies(SBC);
	}

	public class SBComparator implements Comparator<StockBook> {
		@Override
		public int compare(StockBook x, StockBook y) {		    // TODO: Handle null x or y values
			int startComparison = compare(x.getNumCopies(), y.getNumCopies());
			return startComparison;
		  }

		  private int compare(float a, float b) {
		    return a < b ? 1
		         : a > b ? -1
		         : 0;
		  }
	}
	
	/**
	 * Runs the customer interaction
	 * 
	 * @throws BookStoreException
	 */
	private void runFrequentBookStoreInteraction() throws BookStoreException {
		List<Book> LB = configuration.getBookStore().getEditorPicks(configuration.getNumEditorPicksToGet());
		Set<Integer> SI = new HashSet<Integer>();
		for(Book book : LB) {
			SI.add(book.getISBN());
		}
		SI = configuration.getBookSetGenerator().sampleFromSetOfISBNs(SI, configuration.getNumBookCopiesToBuy());
		Set<BookCopy> SBC = new HashSet<BookCopy>();
		for(Integer i : SI) {
			SBC.add(new BookCopy(i, configuration.getNumBooksToBuy()));
		}
		configuration.getBookStore().buyBooks(SBC);
	}

}
