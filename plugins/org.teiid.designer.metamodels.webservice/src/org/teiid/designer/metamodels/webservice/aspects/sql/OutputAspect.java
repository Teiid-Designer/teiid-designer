/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeCheckerAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlResultSetAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.webservice.Output;
import org.teiid.designer.metamodels.webservice.SampleMessages;
import org.teiid.designer.metamodels.webservice.WebServicePackage;



/** 
 * OutputAspect
 *
 * @since 8.0
 */
public class OutputAspect extends WebServiceComponentAspect implements SqlResultSetAspect, SqlDatatypeCheckerAspect {

    /** 
     * OutputAspect
     * @param entity
     * @since 4.2
     */
    protected OutputAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getColumns(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Output.class, eObject);
        final Output output = (Output)eObject;
        SampleMessages sampleMsgs = output.getSamples();
        if(sampleMsgs != null) {
	        List columns = new ArrayList(1);
	        columns.add(sampleMsgs);
	        return columns;
        }
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumnSetType()
     * @since 4.2
     */
    @Override
	public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.PROCEDURE_RESULT;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     * @since 4.2
     */
    @Override
	public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.RESULT_SET);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeCheckerAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.3
     */
    @Override
	public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) {
        CoreArgCheck.isInstanceOf(Output.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case WebServicePackage.OUTPUT__CONTENT_SIMPLE_TYPE:
                    return true;
            }
        }
        return false;
    }

    /**
     * Return the procedure that produces this result set. 
     * @return
     * @since 5.0.2
     */
    @Override
	public Object getProcedure(EObject eObject) {
        CoreArgCheck.isInstanceOf(Output.class, eObject); 
        return ((Output)eObject).getOperation();
    }
}
