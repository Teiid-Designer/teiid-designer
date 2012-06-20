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
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.Schema;

/**
 * ProcedurePameterFinder
 */
public class ProcedureParameterFinder extends RelationalEntityFinder {

    /**
     * Construct an instance of UniqueKeyFinder.
     */
    public ProcedureParameterFinder() {
        super();
    }

    /**
     * This method accumulates the {@link ProcedureParameter} instances. The implementation takes as many shortcuts as possible to
     * prevent unnecessarily visiting unrelated objects.
     * 
     * @see org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean visit( final EObject object ) {
        if (object instanceof ProcedureParameter) {
            found((ProcedureParameter)object);
            return false;
        }
        // ProcedureParams are contained by Catalogs, Schemas and Resources
        if (object instanceof Procedure) {
            return true;
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
