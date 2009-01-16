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

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * SqlTransformationMappingRootSqlAspect
 */
public class MappingClassSetSqlAspect extends AbstractTransformationSqlAspect {

    protected MappingClassSetSqlAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        return TransformationPlugin.Util.getString("MappingClassSetSqlAspect.MappingClasses_sql_name"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        return null;
    }
    
    @Override
    protected String getParentFullName(EObject eObject) {
        final EObject target = ((MappingClassSet)eObject).getTarget();
        if ( target != null ) {
            final SqlAspect targetSqlAspect = AspectManager.getSqlAspect(target);
            if ( targetSqlAspect != null ) {
                return targetSqlAspect.getFullName(target);
            }
        }
        return null;
    }

    @Override
    protected IPath getParentPath(EObject eObject) {
        final EObject target = ((MappingClassSet)eObject).getTarget();
        if ( target != null ) {
            final SqlAspect targetSqlAspect = AspectManager.getSqlAspect(target);
            if ( targetSqlAspect != null ) {
                return targetSqlAspect.getPath(target);
            }
        }
        return null;
    }


    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
