/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * SqlAnnotationAspect is used to get the differrent properties on entity annotation for runtime metadata.
 */
public interface SqlAnnotationAspect extends SqlAspect {
    
    /**
     * Return the description
     * @param eObject The target <code>EObject</code>  
     * @return description
     */
    String getDescription(EObject eObject);
    
    /**
     * Return the keywords
     * @param eObject The target <code>EObject</code>  
     * @return keywords
     */
    List getKeywords(EObject eObject);

    /**
     * Return the tags
     * @param eObject The target <code>EObject</code>  
     * @return tags
     */
    Map getTags(EObject eObject);

    /**
     * Get the ObjectID of the annotated object
     * @param eObject The <code>EObject</code> whose annotated object ID is obtained 
     * @return ObjectID of the annotated eobject.
     */
    Object getObjectID(EObject eObject);
    
    /**
     * Get the URI of the annotated object
     * @param eObject The <code>EObject</code> whose annotated EObject's URI is obtained 
     * @return URI of the annotated eobject.
     */
    URI getURI(EObject eObject);
    
    /**
     * Get the metaclass URI of the annotated object
     * @param eObject The <code>EObject</code> whose annotated EObject's metaclass URI is obtained 
     * @return Metaclass URI of the annotated eobject.
     */
    URI getMetaclassURI(EObject eObject);
}
