/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * PrimaryKeyAspect
 */
public class PrimaryKeyAspect extends UniqueKeyAspect {
    
    public PrimaryKeyAspect(MetamodelEntity entity) {
        super(entity);   
    }    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.PRIMARY_KEY);
    }

}
