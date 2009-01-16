/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
