/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.Annotation;

/**
 * AnnotationHelper
 */
public interface AnnotationHelper {
    
    /**
     * This method will be used by the RelationalObjectGeneratorImpl to create Annotations for Relational Objects 
     * as they are being created.  Whatever is done in the implementation of this interface, it must 'add'
     * the Annotation to whatever output resource is desired.  The RelationalObjectGenerator class will NOT
     * add the Annotations to the output model.
     * 
     * @param targetObject  The object to attach the Annotation to.
     * @param objectDescription the description for the passed in EObject instance.
     * @return the Annotation that was created.
     * @throws AnnotationHelperException
     */
    public Annotation createAnnotation(EObject targetObject, String objectDescription) throws AnnotationHelperException;

    /**
     * This method will be used by the RelationalObjectGeneratorImpl to set Annotations for Relational Objects.
     * If the annotation does not exist a new one is created, otherwise the value is set on the existing 
     * annotation. 
     * 
     * @param targetObject  The object to attach the Annotation to.
     * @param key The name of the annotation key
     * @param value The value to go with the key.
     * @throws AnnotationHelperException
     */
    public void setAnnotation(EObject targetObject, String key, Object value) throws AnnotationHelperException;

}
