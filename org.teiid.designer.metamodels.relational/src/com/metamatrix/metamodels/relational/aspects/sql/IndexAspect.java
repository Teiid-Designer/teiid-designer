/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;

/**
 * IndexAspect
 */
public class IndexAspect extends RelationalEntityAspect implements SqlColumnSetAspect {

    public IndexAspect(MetamodelEntity entity) {
        super(entity);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.INDEX);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    public List getColumns(EObject eObject) {
        ArgCheck.isInstanceOf(Index.class, eObject); 
        Index index = (Index) eObject;
        return index.getColumns();
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getType()
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.FOREIGN_KEY;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
