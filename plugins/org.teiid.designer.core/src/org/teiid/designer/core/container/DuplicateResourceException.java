/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.container;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreRuntimeException;


/** 
 * This exception signifies that a Resource could not be loaded into a Container because one
 * already existed (at a different URI).
 * @since 8.0
 */
public class DuplicateResourceException extends ModelerCoreRuntimeException {

    /**
     */
    private static final long serialVersionUID = 1L;
    private Resource duplicateOfModel;
    private IPath duplicateOfModelPath;
    
    /**
     * Construct an instance of DuplicateResourceException.
     * 
     */
    public DuplicateResourceException() {
        super();
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    
    /**
     * Construct an instance of DuplicateResourceException.
     * @param message
     */
    public DuplicateResourceException(final Resource duplicateOfModel, final IPath duplicateOfModelPath, String message) {
        super(message);
        this.duplicateOfModel = duplicateOfModel;
        this.duplicateOfModelPath = duplicateOfModelPath;
    }
    
    
    
    /**
     * Construct an instance of DuplicateResourceException.
     * @param code
     * @param message
     */
    public DuplicateResourceException(int code, String message) {
        super(code, message);
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param e
     */
    public DuplicateResourceException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param e
     * @param message
     */
    public DuplicateResourceException(Throwable e, String message) {
        super(e, message);
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param e
     * @param code
     * @param message
     */
    public DuplicateResourceException(Throwable e, int code, String message) {
        super(e, code, message);
    }
    
    public Resource getDuplicateOfModel() {
        return duplicateOfModel;
    }
    
    public IPath getDuplicateOfModelPath() {
        return duplicateOfModelPath;
    }
    
}
