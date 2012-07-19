/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Schema;

/**
 * SchemaFinder
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
