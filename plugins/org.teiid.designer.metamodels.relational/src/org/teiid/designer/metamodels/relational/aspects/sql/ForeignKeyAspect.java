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
import org.teiid.designer.core.metamodel.aspect.sql.SqlForeignKeyAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.relational.ForeignKey;


/**
 * ForeignKeyAspect
 */
public class ForeignKeyAspect extends RelationalEntityAspect implements SqlForeignKeyAspect {

    public ForeignKeyAspect(MetamodelEntity entity) {
        super(entity);   
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.FOREIGN_KEY);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlForeignKeyAspect#getUniqueKey(org.eclipse.emf.ecore.EObject)
     */
    public Object getUniqueKey(EObject eObject) {
        CoreArgCheck.isInstanceOf(ForeignKey.class, eObject); 
        ForeignKey foreignKey = (ForeignKey) eObject;
        return foreignKey.getUniqueKey();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    public List getColumns(EObject eObject) {
        CoreArgCheck.isInstanceOf(ForeignKey.class, eObject); 
        ForeignKey foreignKey = (ForeignKey) eObject;
        return foreignKey.getColumns();
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getType()
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.FOREIGN_KEY;
    }
    
    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
