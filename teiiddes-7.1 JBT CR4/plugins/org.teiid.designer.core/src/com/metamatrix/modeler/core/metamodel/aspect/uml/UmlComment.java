/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.uml;

import org.eclipse.emf.ecore.EObject;

/**
 * UmlComment - A comment is a textual annotation that can be attached to a set of elements.
 */
public interface UmlComment extends UmlDiagramAspect {
    
    /**
     * Return the owner of the comment.
     * @param eObject
     * @return
     */
    EObject getOwner(Object eObject);
    
    /**
     * Return the string that is the comment.
     * @param eObject
     * @return
     */
    String getText(Object eObject);

}
