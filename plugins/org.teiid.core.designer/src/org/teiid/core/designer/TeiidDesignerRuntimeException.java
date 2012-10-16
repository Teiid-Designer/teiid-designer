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
public class TeiidDesignerRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public TeiidDesignerRuntimeException() {
        super();
    }

    /**
     * @param message
     */
    public TeiidDesignerRuntimeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TeiidDesignerRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public TeiidDesignerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
