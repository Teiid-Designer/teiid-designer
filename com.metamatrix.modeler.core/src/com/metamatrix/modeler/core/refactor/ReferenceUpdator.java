/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;


/**
 * ReferenceUpdator
 * @since 4.2
 */
public interface ReferenceUpdator {

    /**
     * Update the EObject by resetting references by looking up oldToNewObjects map, the
     * references may be eObjects or values derived from eObjects in the map.
     * @param eObject The EObject to update
     * @param oldToNewObjects The Map of old to new objects, this info is needed to update
     * the EObject
     * @since 4.2
     */
    void updateEObject(final EObject eObject, final Map oldToNewObjects);
}
