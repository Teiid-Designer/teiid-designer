/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.sql;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.relational.Index;


/**
 * IndexAspect
 */
public class IndexAspect extends RelationalEntityAspect implements SqlColumnSetAspect {

    public IndexAspect(MetamodelEntity entity) {
        super(entity);
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.INDEX);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getColumns(EObject eObject) {
        CoreArgCheck.isInstanceOf(Index.class, eObject); 
        Index index = (Index) eObject;
        return index.getColumns();
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getType()
     */
    @Override
	public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.FOREIGN_KEY;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
