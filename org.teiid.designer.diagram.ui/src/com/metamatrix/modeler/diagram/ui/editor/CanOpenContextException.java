/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CanOpenContextException extends Exception {
	/**
     */
    private static final long serialVersionUID = 1L;
    private String message = null;
	public CanOpenContextException( String message ) {
		this.message = message;
	}

	@Override
    public String getMessage() {
		return message;
	}
}
