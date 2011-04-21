/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
