/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;


/**
 *
 * @since 8.0
 */
public class ModelerCoreRuntimeException extends RuntimeException {
	
	Throwable child = null;
	
	/** An error code. */
    private String code;
    
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * 
     */
    public ModelerCoreRuntimeException() {
        super();
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param message
     */
    public ModelerCoreRuntimeException(String message) {
        super(message);
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param code
     * @param message
     */
    public ModelerCoreRuntimeException(int code, String message) {
        super(message);
        setCode(Integer.toString(code));
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param e
     */
    public ModelerCoreRuntimeException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param e
     * @param message
     */
    public ModelerCoreRuntimeException(Throwable e, String message) {
        super(message, e);
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param e
     * @param code
     * @param message
     */
    public ModelerCoreRuntimeException(Throwable e, int code, String message) {
        this(code, message);
        child = e;
    }
    
    /**
     * 
     * @return child Throwable
     */
    public Throwable getChild() {
    	return this.child;
    }
    
    /**
     * Get the error code.
     *
     * @return The error code 
     */
    public String getCode() {
        return this.code;
    }
    
    private void setCode( String code ) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (code == null || code.length() == 0 || message.startsWith(code)) {
            return message;
        }
        return code + " " + message; //$NON-NLS-1$
    } 

}
