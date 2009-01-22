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
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;

/**
 * @
 */
public class ColumnNameFinder extends AbstractNameFinder {

    public ColumnNameFinder( final String elementName,
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
            if (sqlAspect instanceof SqlModelAspect || sqlAspect instanceof SqlTableAspect) {
                // If the fullname of the parentAspect is not in the parent
                // path of the entity fullname then do not check the contents
                // of the parent
                if (fullName != null && !isParent(fullName.toUpperCase())) {
                    return false;
                }
            } else if (sqlAspect instanceof SqlColumnAspect) {
                // Check the ColumnAspect against the element fullname to match
                if (fullName != null && foundMatch(fullName.toUpperCase(), eObject)) {
                    return false;
                }
            }
        }
        return true;
    }
}
