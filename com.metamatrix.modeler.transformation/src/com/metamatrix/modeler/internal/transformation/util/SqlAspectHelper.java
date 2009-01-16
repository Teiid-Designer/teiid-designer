/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.transformation.util;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.transformation.aspects.sql.InputParameterSqlAspect;
import com.metamatrix.modeler.transformation.aspects.sql.InputSetSqlAspect;

/**
 * SqlAspectManager - maintains cache of SqlAspects
 */
public class SqlAspectHelper extends com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper {

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
