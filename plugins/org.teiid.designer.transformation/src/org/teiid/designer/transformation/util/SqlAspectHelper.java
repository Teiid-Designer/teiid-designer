/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.transformation.aspects.sql.InputParameterSqlAspect;
import org.teiid.designer.transformation.aspects.sql.InputSetSqlAspect;


/**
 * SqlAspectManager - maintains cache of SqlAspects
 *
 * @since 8.0
 */
public class SqlAspectHelper extends org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper {

    /**
     * Determine if the supplied EObject has a InputParameterSqlAspect
     * @param eObject the EObject
     * @return 'true' if has InputParameterAspect, 'false' if not.
     */
    public static boolean isInputParameter(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof InputParameterSqlAspect )
            return true;

        return false;
    }
    
    /**
     * Determine if the supplied EObject has a InputSetSqlAspect
     * @param eObject the EObject
     * @return 'true' if has InputSetSqlAspect, 'false' if not.
     */
    public static boolean isInputSet(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof InputSetSqlAspect )
            return true;

        return false;
    }

}
