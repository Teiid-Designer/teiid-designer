/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.sql;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.metamodels.transformation.MappingClassSet;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * SqlTransformationMappingRootSqlAspect
 */
public class MappingClassSetSqlAspect extends AbstractTransformationSqlAspect {

    protected MappingClassSetSqlAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        return TransformationPlugin.Util.getString("MappingClassSetSqlAspect.MappingClasses_sql_name"); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
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
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
