/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.Annotation;

/**
 * ModelObjectAnnotations
 */
public interface ModelObjectAnnotations  extends ModelWorkspaceItem {
    /**
     * Create a new annotation and add it to this resource. 
     * @param annotatedObject the object to be annotated; may not be null
     * @return the new Annotation object
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    Annotation createNewAnnotation( EObject annotatedObject ) throws ModelWorkspaceException;

    /**
     * Get the annotation object associated with the supplied model object.
     * Annotations are created using the {@link #createNewTransformation(EObject)} method.
     * @param annotatedObject the annotatedObject object; may be null, meaning find all annotations that have no annotatedObject
     * @return the {@link Annotation} instance for the supplied object; null if there is no current
     * annotation
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    Annotation getAnnotation( EObject annotatedObject ) throws ModelWorkspaceException;

//    /**
//     * Get all the annotation objects known by this resource.
//     * Annotations are created using the {@link #createNewAnnotation(EObject)} method.
//     * @return the {@link Annotation} instances for this resource; never null, but possibly empty
//     * @throws ModelWorkspaceException if this element does not exist or if an
//     *      exception occurs while accessing its corresponding resource
//     */
//    List getAnnotations() throws ModelWorkspaceException;

    /**
     * Remove the specified annotation from this resource. 
     * @param annotation the annotation; may not be null
     * @return true if the annotation was deleted from this resource, or false if it was not
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    boolean delete( Annotation annotation ) throws ModelWorkspaceException;

}
