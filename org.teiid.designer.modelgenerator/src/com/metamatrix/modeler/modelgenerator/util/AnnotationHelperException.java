/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.util;

import com.metamatrix.core.MetaMatrixCoreException;

/**
 * AnnotationHelperExceptoin
 */
public class AnnotationHelperException extends MetaMatrixCoreException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance of AnnotationHelperExceptoin.
     * 
     */
    public AnnotationHelperException() {
        super();
    }

    /**
     * Construct an instance of AnnotationHelperExceptoin.
     * @param message
     */
    public AnnotationHelperException(String message) {
        super(message);
    }

    /**
     * Construct an instance of AnnotationHelperExceptoin.
     * @param e
     */
    public AnnotationHelperException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of AnnotationHelperExceptoin.
     * @param e
     * @param message
     */
    public AnnotationHelperException(Throwable e, String message) {
        super(e, message);
    }
}
