/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice.aspects.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeCheckerAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlResultSetAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.xmlservice.XmlOutput;
import org.teiid.designer.metamodels.xmlservice.XmlResult;



/** 
 * XmlOutputAspect
 */
public class XmlOutputAspect extends XmlServiceComponentAspect implements SqlResultSetAspect, SqlDatatypeCheckerAspect {
    
    /** 
     * XmlOutputAspect
     * @param entity
     * @since 4.2
     */
    protected XmlOutputAspect(final MetamodelEntity entity) {
        super(entity);
    }
    

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getColumns(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlOutput.class, eObject);
        final XmlOutput output = (XmlOutput)eObject;
        XmlResult result = output.getResult();
        if(result != null) {
            List columns = new ArrayList(1);
            columns.add(result);
            return columns;
        }
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumnSetType()
     * @since 4.2
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.PROCEDURE_RESULT;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     * @since 4.2
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.RESULT_SET);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeCheckerAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.3
     */
    public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) {
        CoreArgCheck.isInstanceOf(XmlOutput.class, eObject); 
        return false;
    }
    
    /**
     * Return the procedure that produces this result set. 
     * @return
     * @since 5.0.2
     */
    public Object getProcedure(EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlOutput.class, eObject); 
        return ((XmlOutput)eObject).getOperation();
    }
}
