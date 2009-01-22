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

/**
 * SchemaFinder
 */
public class SchemaFinder extends RelationalEntityFinder {

    /**
     * Construct an instance of SchemaFinder.
     */
    public SchemaFinder() {
        super();
    }

    /**
     * This method accumulates the {@link Schema} instances. The implementation takes as many shortcuts as possible to prevent
     * unnecessarily visiting unrelated objects.
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof Catalog) {
            // continue since catalogs contain schemas
            return true;
        }
        if (object instanceof Schema) {
            found((Schema)object);
            // No need to continue ...
            return false;
        }
        return false;
    }

}
