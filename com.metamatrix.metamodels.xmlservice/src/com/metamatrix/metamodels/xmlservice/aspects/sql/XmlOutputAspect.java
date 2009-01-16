/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.xmlservice.aspects.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.xmlservice.XmlOutput;
import com.metamatrix.metamodels.xmlservice.XmlResult;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeCheckerAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlResultSetAspect;


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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getColumns(final EObject eObject) {
        ArgCheck.isInstanceOf(XmlOutput.class, eObject);
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumnSetType()
     * @since 4.2
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.PROCEDURE_RESULT;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     * @since 4.2
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.RESULT_SET);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeCheckerAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.3
     */
    public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) {
        ArgCheck.isInstanceOf(XmlOutput.class, eObject); 
        return false;
    }
    
    /**
     * Return the procedure that produces this result set. 
     * @return
     * @since 5.0.2
     */
    public Object getProcedure(EObject eObject) {
        ArgCheck.isInstanceOf(XmlOutput.class, eObject); 
        return ((XmlOutput)eObject).getOperation();
    }
}
