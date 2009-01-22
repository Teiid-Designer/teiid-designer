/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
