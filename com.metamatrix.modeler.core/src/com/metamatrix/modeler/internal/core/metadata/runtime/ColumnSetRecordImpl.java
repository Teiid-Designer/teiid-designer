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

import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;

/**
 * ColumnSetRecordImpl
 */
public class ColumnSetRecordImpl extends com.metamatrix.metadata.runtime.impl.ColumnSetRecordImpl {
    
    private static final long serialVersionUID = -2311057386266603525L;

	/**
	 * Flags to determine if values have been set.
	 */
	private boolean columnIDsSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ColumnSetRecordImpl(final SqlColumnSetAspect sqlAspect, final EObject eObject, final char recordType) {
		super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
        setRecordType(recordType);
        this.eObject = eObject;
	}

	private SqlColumnSetAspect getColumnSetAspect() {
		return (SqlColumnSetAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();			
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================    

    /** 
     * @see com.metamatrix.modeler.core.metadata.runtime.ColumnSetRecord#getColumnIDs()
     */
    @Override
    public List getColumnIDs() {
    	if((EObject)super.eObject != null && !columnIDsSet) {
			List columns = getColumnSetAspect().getColumns((EObject)super.eObject);
			setColumnIDs(((ModelerMetadataRecordDelegate)this.delegate).getObjectIDs(columns));    		
    	}
        return super.getColumnIDs();
    }

    /**
     * @param list
     */
    @Override
    public void setColumnIDs(List list) {
    	super.setColumnIDs(list);
		columnIDsSet = true;        
    }
    
}
