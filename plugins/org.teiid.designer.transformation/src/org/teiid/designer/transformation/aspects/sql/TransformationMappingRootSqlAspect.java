/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.sql;

import java.util.Iterator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingHelper;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect;


/**
 * SqlTransformationMappingRootSqlAspect
 *
 * @since 8.0
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
