/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.util.List;

import org.eclipse.emf.ecore.EReference;

/**
 * This interface represents a factory for {@link EObjectMatcher} instances.  Extensions of the
 * {@link ModelerCore.EXTENSION_POINT.MODEL_OBJECT_RESOLVER#UNIQUE_ID
 */
public interface EObjectMatcherFactory {
    
    /**
     * Create the {@link EObjectMatcher} instances that should be used to find matches between
     * root-level objects in models.  Many implementations might return the same
     * matchers for all EReferences in a metaclass, and many implementations might return the same
     * matchers for all EReferences in a metamodel.  This method should return an empty list
     * if this factory has no matchers for root-level objects.
     * @return the list of EObjectMatcher instances; may not be null, but may be empty
     */
    List createEObjectMatchersForRoots();
    
    /**
     * Create the {@link EObjectMatcher} instances that should be used to find matches between
     * values of the supplied {@link EReference}.  Many implementations might return the same
     * matchers for all EReferences in a metaclass, and many implementations might return the same
     * matchers for all EReferences in a metamodel.  This method should return an empty list
     * if this factory has no matchers for the supplied feature.
     * @param reference the reference; never null
     * @return the list of EObjectMatcher instances; may not be null, but may be empty
     */
    List createEObjectMatchers( final EReference reference );

}
