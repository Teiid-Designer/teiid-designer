/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.Schema;

/**
 * LogicalRelationshipEndFinder
 */
public class LogicalRelationshipEndFinder extends RelationalEntityFinder {

    /**
     * Construct an instance of LogicalRelationshipEndFinder.
     */
    public LogicalRelationshipEndFinder() {
        super();
    }

    /**
     * This method accumulates the {@link LogicalRelationship} instances. The implementation takes as many shortcuts as possible
     * to prevent unnecessarily visiting unrelated objects.
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof LogicalRelationshipEnd) {
            found((LogicalRelationshipEnd)object);
            return false;
        }

        // LogicalRelationship are contained by Catalog and Schema and Resources
        if (object instanceof LogicalRelationship) {
            return true;
        }
        if (object instanceof Catalog) {
            return true; // may be schemas below the catalog
        }
        if (object instanceof Schema) {
            // schema may contain relationship
            return true;
        }
        return false;
    }

}
