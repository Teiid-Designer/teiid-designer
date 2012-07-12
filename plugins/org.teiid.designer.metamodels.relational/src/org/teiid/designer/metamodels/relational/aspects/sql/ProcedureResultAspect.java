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
import org.teiid.designer.core.metamodel.aspect.sql.SqlResultSetAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.relational.ProcedureResult;


/**
 * ProcedureResultAspect
 */
public class ProcedureResultAspect extends RelationalEntityAspect implements SqlResultSetAspect {

    public ProcedureResultAspect(MetamodelEntity entity) {
        super(entity);   
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    public List getColumns(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureResult.class, eObject);
        ProcedureResult procResult = (ProcedureResult) eObject;
        return procResult.getColumns();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getType()
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.PROCEDURE_RESULT;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.RESULT_SET);
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /**
     * Return the procedure that produces this result set. 
     * @return
     * @since 5.0.2
     */
    public Object getProcedure(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureResult.class, eObject);
        return ((ProcedureResult)eObject).getProcedure();
    }
}
