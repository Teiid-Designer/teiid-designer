/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.Schema;

/**
 * TableFinder
 */
public class BaseTableColumnFinder extends RelationalEntityFinder {

    /**
     * Construct an instance of UniqueKeyFinder.
     */
    public BaseTableColumnFinder() {
        super();
    }

    /**
     * This method accumulates the {@link Column} instances. The implementation takes as many shortcuts as possible to prevent
     * unnecessarily visiting unrelated objects.
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof BaseTable) {
            final ColumnSet columnSet = (ColumnSet)object;
            found(columnSet.getColumns());
            return false;
        }
        if (object instanceof Catalog) {
            return true;
        }
        if (object instanceof Schema) {
            return true;
        }
        return false;
    }

}
