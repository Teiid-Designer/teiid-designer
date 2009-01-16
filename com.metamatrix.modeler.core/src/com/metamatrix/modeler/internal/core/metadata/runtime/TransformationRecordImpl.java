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

package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationInfo;

/**
 * TransformationRecordImpl
 */
public class TransformationRecordImpl extends com.metamatrix.metadata.runtime.impl.TransformationRecordImpl {

    private static final long serialVersionUID = 767732723989088385L;

    private SqlTransformationInfo info;
	
	/**
	 * Flags to determine if values have been set.
	 */
	private boolean transformedObjectIDSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public TransformationRecordImpl(final SqlTransformationAspect sqlAspect, final EObject eObject, final String type) {
		super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		// set the transformation type
		setTransformationType(type);
		this.eObject = eObject;
    }

	private SqlTransformationAspect getTransformationAspect() {
		return (SqlTransformationAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();
	}

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getTransformation()
     */
    @Override
    public String getTransformation() {
		if(super.eObject != null) {
            return getTransformationInfo().getSqlTransform();
		}
        return super.getTransformation();
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getBindings()
     */
    @Override
    public List getBindings() {
		if(super.eObject != null) {
			return getTransformationInfo().getBindings();
		}
        return super.getBindings();
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getSchemaPaths()
     */
    @Override
    public List getSchemaPaths() {
		if(super.eObject != null) {
			return getTransformationInfo().getSchemaPaths();
		}
        return super.getSchemaPaths();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getTransformedObjectID()
     */
    @Override
    public Object getTransformedObjectID() {
		if(eObject != null && !transformedObjectIDSet) {
			EObject transformedObj = (EObject) getTransformationAspect().getTransformedObject((EObject)eObject);
			setTransformedObjectID(((ModelerMetadataRecordDelegate)this.delegate).getObjectID(transformedObj));
		}
        return super.getTransformedObjectID();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param object
     */
    @Override
    public void setTransformedObjectID(final Object object) {
        super.setTransformedObjectID(object);
		transformedObjectIDSet = true;
    }

	private SqlTransformationInfo getTransformationInfo() {
		if(info == null) {
			info = getTransformationAspect().getTransformationInfo((EObject)eObject, null, getTransformationType());
		}
		return info;
	}

}
