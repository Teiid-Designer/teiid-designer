/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.relationship.RelationshipType;

/**
 * RelationshipTypeFinder.java
 */
public class RelationshipTypeFinder extends RelationshipEntityFinder {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( EObject object ) {
        if (object instanceof RelationshipType) {
            // collect the Relationship
            found((RelationshipType)object);
        }
        return false;
    }

}
