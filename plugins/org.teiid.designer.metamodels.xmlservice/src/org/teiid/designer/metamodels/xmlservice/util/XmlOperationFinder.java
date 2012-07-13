/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.xmlservice.XmlOperation;

/**
 * RelationshipFinder.java
 */
public class XmlOperationFinder extends XmlServiceComponentFinder {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean visit( EObject object ) {
        if (object instanceof XmlOperation) {
            // collect the Relationship
            found((XmlOperation)object);
        }
        return false;
    }

}
