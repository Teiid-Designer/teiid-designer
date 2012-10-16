/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.core.designer;

/**
 * @since 8.0
 */
public class TeiidDesignerException extends Exception {

    private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor
     */
    public TeiidDesignerException() {
        super();
    }
    
    /**
     * @param message
     */
    public TeiidDesignerException(String message) {
        super(message);
    }

    /**
     * @param childException
     */
    public TeiidDesignerException(Throwable childException) {
        super(childException);
    }

    /**
     * @param childException
     * @param message
     */
    public TeiidDesignerException(Throwable childException, String message) {
        super(message, childException);
    }
}
