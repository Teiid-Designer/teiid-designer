/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;

/**
 * @
 */
public class DatatypeNameFinder extends AbstractNameFinder {

    public DatatypeNameFinder( final String elementName,
                               final boolean isPartialname ) {
        super(elementName, isPartialname);
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean visit( final EObject eObject ) {
        if (!super.visit(eObject)) {
            return false;
        }
        final SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);
        if (sqlAspect != null) {
            final String fullName = sqlAspect.getFullName(eObject);
            if (sqlAspect instanceof SqlDatatypeAspect) {
                // Check the ModelAspect against the model fullname to match
                if (fullName != null && foundMatch(fullName.toUpperCase(), eObject)) {
                    return false;
                }
            }
        }
        return true;
    }

}
