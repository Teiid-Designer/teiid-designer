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
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.UniqueKey;

/**
 * KeyFinder
 */
public class KeyFinder extends RelationalEntityFinder {

    /**
     * Construct an instance of KeyFinder.
     */
    public KeyFinder() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof UniqueKey) {
            found((UniqueKey)object);
            return false;
        }
        if (object instanceof ForeignKey) {
            found((ForeignKey)object);
            return false;
        }
        // BaseTables contain keys ...
        if (object instanceof Catalog) {
            return true;
        }
        if (object instanceof Schema) {
            return true;
        }
        if (object instanceof BaseTable) {
            return true;
        }
        return false;
    }

}
