/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.impl;

import com.metamatrix.metamodels.core.Annotation;


/**
 * InternalAnnotationContainer
 */
public interface InternalAnnotationContainer {

    /**
     * @param impl
     * @param oldAnnotatedObject
     */
    void removeAnnotation(Annotation annotation);

    /**
     * @param impl
     * @param newAnnotatedObject
     * @param oldAnnotatedObject
     */
    void addAnnotation(Annotation annotation);

}
