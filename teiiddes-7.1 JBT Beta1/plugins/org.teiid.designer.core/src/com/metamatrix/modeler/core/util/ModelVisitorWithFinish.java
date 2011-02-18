/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * ModelVisitorWithFinish
 */
public interface ModelVisitorWithFinish extends ModelVisitor {
    
    /**
     * Called after the object and it's contents have all been visited.  Implementing this interface
     * allows the visitor to do additional logic after all children have been visited.
     * @param item the item to visit; never null
     * @return true if the children of <code>item</code> should be visited, or false if they should not.
     * @throws ModelerCoreException if the visit fails for some reason
     */
    public void postVisit( EObject object) throws ModelerCoreException;


}
