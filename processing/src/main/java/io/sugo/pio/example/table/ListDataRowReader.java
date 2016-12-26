package io.sugo.pio.example.table;

import java.util.Iterator;


/**
 * Iterates over a list of DataRows. Actually a misnomer because this class does not use a list but
 * an iterator over an arbitrary collection.
 * 
 * @author Ingo Mierswa Exp $
 */
public class ListDataRowReader implements DataRowReader {

	private Iterator<DataRow> iterator;

	public ListDataRowReader(Iterator<DataRow> i) {
		this.iterator = i;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public DataRow next() {
		return iterator.next();
	}

	/**
	 * Will throw a new {@link UnsupportedOperationException} since {@link DataRowReader} does not
	 * have to implement remove.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("The method 'remove' is not supported by DataRowReaders!");
	}
}
