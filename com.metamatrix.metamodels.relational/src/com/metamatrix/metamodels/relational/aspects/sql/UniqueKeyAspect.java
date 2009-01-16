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

package com.metamatrix.metamodels.relational.aspects.sql;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlUniqueKeyAspect;

/**
 * UniqueKeyAspect
 */
public class UniqueKeyAspect extends RelationalEntityAspect implements SqlUniqueKeyAspect {
    
    public UniqueKeyAspect(MetamodelEntity entity) {
        super(entity);   
    }    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.UNIQUE_KEY || recordType == IndexConstants.RECORD_TYPE.PRIMARY_KEY);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    public List getColumns(EObject eObject) {
        ArgCheck.isInstanceOf(UniqueKey.class, eObject); 
        UniqueKey uniqueKey = (UniqueKey) eObject;        
        return uniqueKey.getColumns();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlUniqueKeyAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    public List getForeignKeys(EObject eObject) {
        ArgCheck.isInstanceOf(UniqueKey.class, eObject); 
        UniqueKey uniqueKey = (UniqueKey) eObject;
        return uniqueKey.getForeignKeys();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getType()
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.UNIQUE_KEY;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
