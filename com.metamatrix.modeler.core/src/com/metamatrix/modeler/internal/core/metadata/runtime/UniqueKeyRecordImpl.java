/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlUniqueKeyAspect;

/**
 * UniqueKeyRecordImpl
 */
public class UniqueKeyRecordImpl extends com.metamatrix.metadata.runtime.impl.UniqueKeyRecordImpl {

    private static final long serialVersionUID = 3860885519616912384L;

	/**
	 * Flags to determine if values have been set.
	 */
	private boolean foreignKeyIDsSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public UniqueKeyRecordImpl(final SqlUniqueKeyAspect sqlAspect, final EObject eObject) {
        super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
        setRecordType(IndexConstants.RECORD_TYPE.UNIQUE_KEY);
        this.eObject = eObject;
	}

	private SqlUniqueKeyAspect getUniqueKeyAspect() {
		return (SqlUniqueKeyAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();			
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.UniqueKeyRecord#getForeignKeyIDs()
     */
    @Override
    public List getForeignKeyIDs() {
		if(eObject != null && !foreignKeyIDsSet) {
			List columns = getUniqueKeyAspect().getForeignKeys((EObject)eObject);
			setForeignKeyIDs(((ModelerMetadataRecordDelegate)this.delegate).getObjectIDs(columns));    		
		}
        return super.getForeignKeyIDs();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param list
     */
    @Override
    public void setForeignKeyIDs(List list) {
        super.setForeignKeyIDs(list);
        this.foreignKeyIDsSet = true;
    }
}
