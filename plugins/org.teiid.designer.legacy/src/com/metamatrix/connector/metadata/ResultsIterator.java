/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class ResultsIterator implements Iterator {
	
	public interface ResultsProcessor {

		void createRows(Object resultObject, List rows);

	}
	
	private final ResultsProcessor objectQueryProcessor;
	private LinkedList rowBuffer = new LinkedList();
	private Iterator resultsIter;

	public ResultsIterator(ResultsProcessor objectQueryProcessor, Iterator resultsIter) {
		this.objectQueryProcessor = objectQueryProcessor;
		this.resultsIter = resultsIter;
	}

	public boolean hasNext() {
		return rowBuffer.size() > 0 || resultsIter.hasNext();
	}

	public Object next() {
		if (rowBuffer.size() > 0) {
			return rowBuffer.removeFirst();
		}
		if (!resultsIter.hasNext()) {
			throw new NoSuchElementException();
		}
		this.objectQueryProcessor.createRows(resultsIter.next(), rowBuffer);
		return rowBuffer.removeFirst();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}