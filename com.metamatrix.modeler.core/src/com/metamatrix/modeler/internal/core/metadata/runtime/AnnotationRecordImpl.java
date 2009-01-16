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

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;

/**
 * AnnotationRecordImpl
 */
public class AnnotationRecordImpl extends com.metamatrix.metadata.runtime.impl.AnnotationRecordImpl {

	private static final long serialVersionUID = 1615791785370971740L;

	/**
	 * Flags to determine if values have been set.
	 */
	private boolean descriptionSet;

    public AnnotationRecordImpl(final SqlAnnotationAspect sqlAspect, final EObject eObject) {
        super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		setRecordType(IndexConstants.RECORD_TYPE.ANNOTATION);
		this.eObject = eObject;
	}

	private SqlAnnotationAspect getAnnotationAspect() {
		return (SqlAnnotationAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.AnnotationRecord#getDescription()
     */
    @Override
    public String getDescription() {
		if(this.eObject != null && !descriptionSet) {
			setDescription(getAnnotationAspect().getDescription((EObject)this.eObject));
		}
        return super.getDescription();
    }

    /**
     * @param string
     */
    @Override
    public void setDescription(final String string) {
		descriptionSet = true;
		super.setDescription(string);
    }

}
