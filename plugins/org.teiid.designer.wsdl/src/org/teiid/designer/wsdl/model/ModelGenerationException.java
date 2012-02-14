/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl.model;

public class ModelGenerationException extends Exception {

	public static final long serialVersionUID = 1L;
	
	public ModelGenerationException() {
		super();
	}
	
	public ModelGenerationException(Exception ex) {
		super(ex);
	}
}
