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

package com.metamatrix.modeler.transformation.aspects.sql;

import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingHelper;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;

/**
 * SqlTransformationMappingRootSqlAspect
 */
public abstract class TransformationMappingRootSqlAspect extends AbstractTransformationSqlAspect implements SqlTransformationAspect {

    protected TransformationMappingRootSqlAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * Helper method that returns the MappingHelper that is an instance of the supplied
     * class/interface.  This method checks the nested structure of the supplied MappingHelper,
     * and may be recursive.
     * @param helper
     * @param expected
     * @return the mapping helper that is an instance of the supplied class/interface,
     * or null if no such object could be found
     */
    protected MappingHelper getHelper( final MappingHelper helper, final Class expected ) {
        if ( helper != null ) {
            if ( expected.isInstance(helper) ) {
                return helper;
            }
            // Go through the nested ones and find the transformation ...
            final Iterator iter = helper.getNested().iterator();
            while (iter.hasNext()) {
                final MappingHelper nestedHelper = (MappingHelper)iter.next();
                final MappingHelper result = getHelper(nestedHelper,expected);
                if ( result != null ) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    protected String getParentFullName(EObject eObject) {
        return ModelerCore.getModelEditor().getModelName(eObject);
    }

    @Override
    protected IPath getParentPath(EObject eObject) {
        return new Path(ModelerCore.getModelEditor().getModelName(eObject));
    }


}
