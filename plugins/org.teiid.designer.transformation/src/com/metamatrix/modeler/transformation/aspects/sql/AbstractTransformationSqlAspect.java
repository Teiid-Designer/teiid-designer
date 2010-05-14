/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.sql;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;

/**
 * AbstractTransformationSqlAspect
 */
public abstract class AbstractTransformationSqlAspect extends AbstractMetamodelAspect implements SqlAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;

    public static final String FULL_NAME_DELIMITER = "."; //$NON-NLS-1$

    protected AbstractTransformationSqlAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(final EObject eObject) {
        final String parentFullName = getParentFullName(eObject);
        if ( parentFullName != null ) {
            final String name = getName(eObject);
            return parentFullName + FULL_NAME_DELIMITER + name;
        }
        return getName(eObject);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public IPath getPath(final EObject eObject) {
        final IPath parentPath = getParentPath(eObject);
        final String name = getName(eObject);
        if ( parentPath != null ) {
            return parentPath.append(name);
        }
        return new Path(name);
    }

    protected String getParentFullName(EObject eObject) {
        final EObject parent = eObject.eContainer();
        if ( parent != null ) {
            final SqlAspect parentAspect = AspectManager.getSqlAspect(parent);
            if ( parentAspect != null ) {
                final String parentFullName = parentAspect.getFullName(parent);
                return parentFullName;
            }
        }
        return null;
    }

    protected IPath getParentPath(EObject eObject) {
        final EObject parent = eObject.eContainer();
        if ( parent != null ) {
            final SqlAspect parentAspect = AspectManager.getSqlAspect(parent);
            if ( parentAspect != null ) {
                final IPath parentPath = parentAspect.getPath(parent);
                return parentPath;
            }
        }
        return null;
    }

    protected String getModelName(EObject eObject) {
        IPath parentPath = getParentPath(eObject);
        return parentPath.segment(0);
    }
}
