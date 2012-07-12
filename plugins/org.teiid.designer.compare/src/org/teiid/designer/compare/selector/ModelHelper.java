/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.selector;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.core.Annotation;

/**
 * ModelHelper
 */
public interface ModelHelper {
    
    public Annotation getAnnotation( final EObject eObject );
    
}
