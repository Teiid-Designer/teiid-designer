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

package com.metamatrix.modeler.core.metamodel.aspect.uml;

import java.util.Collection;

/**
 * UmlClassifier
 */
public interface UmlClassifier extends UmlDiagramAspect {
    //Show Mask Constants    
    public final static int SIGNATURE_NAME = 1;
    public final static int SIGNATURE_STEROTYPE = 2;
    
    /**
     * Return the collection of relationships for the given Classifier
     * @param eObject
     * @return
     */
    Collection getRelationships(Object eObject);
    
    /**
     * Return the collection of supertypes for the given Classifier
     * @param eObject
     * @return the collection of supertypes
     */
    Collection getSupertypes(Object eObject);
    
    /**
     * If true, the Classifier does not provide a complete declaration and can 
     * typically not be instantiated. An abstract classifier is intended to be 
     * used by other classifiers e.g. as the target of general metarelationships 
     * or generalization relationships. Default value is false.
     * @param eObject
     * @return
     */
    boolean isAbstract(Object eObject);
    
}
