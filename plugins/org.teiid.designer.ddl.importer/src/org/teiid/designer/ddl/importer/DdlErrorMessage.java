/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer;


/**
 * DdlErrorMessage
 *
 * @since 8.2
 */
public class DdlErrorMessage {

    private String message;
    private String parserId;
    private boolean isParseException = false;
	private int lineNumber = -1;
    private int colNumber = -1;
    private int index = -1;

    /**
     * Constructor
     * @param message the message string
     */
    public DdlErrorMessage(String message) {
    	this.message = message;
    }
        
    /**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
    /**
	 * @return the isParseException
	 */
	public boolean isParseException() {
		return this.isParseException;
	}

	/**
	 * @param isParseException the isParseException to set
	 */
	public void setIsParse(boolean isParseException) {
		this.isParseException = isParseException;
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @return the colNumber
	 */
	public int getColNumber() {
		return this.colNumber;
	}

	/**
	 * @param colNumber the colNumber to set
	 */
	public void setColNumber(int colNumber) {
		this.colNumber = colNumber;
	}

	/**
	 * @return the parserId
	 */
	public String getParserId() {
		return parserId;
	}

	/**
	 * @param parserId the parserId to set
	 */
	public void setParserId(String parserId) {
		this.parserId = parserId;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}
