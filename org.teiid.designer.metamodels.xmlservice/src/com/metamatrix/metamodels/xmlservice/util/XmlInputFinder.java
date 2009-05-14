/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlOperation;

/**
 * RelationshipFinder.java
 */
public class XmlInputFinder extends XmlServiceComponentFinder {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( EObject object ) {
        if (object instanceof XmlInput) {
            // collect the Relationship
            found((XmlInput)object);
            return false;
        }

        // XmlInputs are contained by XmlOperation
        if (object instanceof XmlOperation) {
            return true;
        }

        return false;
    }

}
