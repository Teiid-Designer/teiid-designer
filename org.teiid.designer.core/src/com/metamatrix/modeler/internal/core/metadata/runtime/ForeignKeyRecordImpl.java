/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlForeignKeyAspect;

/**
 * ForeignKeyRecordImpl
 */
public class ForeignKeyRecordImpl extends com.metamatrix.metadata.runtime.impl.ForeignKeyRecordImpl {

    private static final long serialVersionUID = 8403148788957708630L;

	/**
	 * Flags to determine if values have been set.
	 */
	private boolean uniqueKeyIDSet;


    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ForeignKeyRecordImpl(final SqlForeignKeyAspect sqlAspect, final EObject eObject) {
        super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
        setRecordType(IndexConstants.RECORD_TYPE.FOREIGN_KEY);
        this.eObject = eObject;
	}

	private SqlForeignKeyAspect getForeignKeyAspect() {
		return (SqlForeignKeyAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();			
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ForeignKeyRecord#getPrimaryKeyID()
     */
    @Override
    public Object getUniqueKeyID() {
    	if(eObject != null && !uniqueKeyIDSet) {
			EObject primaryKey = (EObject) getForeignKeyAspect().getUniqueKey((EObject)eObject);
			setUniqueKeyID(((ModelerMetadataRecordDelegate)this.delegate).getObjectID(primaryKey));    		
    	}
        return super.getUniqueKeyID();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param object
     */
    @Override
    public void setUniqueKeyID(Object keyID) {
        super.setUniqueKeyID(keyID);
		uniqueKeyIDSet = true;
    }    
}
