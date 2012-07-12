/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.core.impl;

import org.teiid.designer.metamodels.core.Annotation;


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
