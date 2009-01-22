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
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;

/**
 * TableFinder
 */
public class TableFinder extends RelationalEntityFinder {

    /**
     * Construct an instance of UniqueKeyFinder.
     */
    public TableFinder() {
        super();
    }

    /**
     * This method accumulates the {@link Table} instances. The implementation takes as many shortcuts as possible to prevent
     * unnecessarily visiting unrelated objects.
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        // Tables are contained by Catalogs, Schemas and Resources
        if (object instanceof Table) {
            found((Table)object);
            return false;
        }
        if (object instanceof Catalog) {
            // catalogs will contain tables
            return true;
        }
        if (object instanceof Schema) {
            // schemas will contain tables
            return true;
        }
        return false;
    }

}
