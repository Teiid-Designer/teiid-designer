/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

/**
 *
 */
public class TeiidExecutionException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int code;

	/**
	 * 
	 */
	public TeiidExecutionException(int code) {
		this.code = code;
	}
	
	/**
	 * 
	 */
	public TeiidExecutionException() {
	}

	/**
	 * @param message
	 */
	public TeiidExecutionException(int code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * @param cause
	 */
	public TeiidExecutionException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TeiidExecutionException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}

}
