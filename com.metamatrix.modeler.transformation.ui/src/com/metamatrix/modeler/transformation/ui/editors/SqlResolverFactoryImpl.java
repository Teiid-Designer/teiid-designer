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

package com.metamatrix.modeler.transformation.ui.editors;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.ui.sqleditor.SqlResolverFactory;

/**
 * SqlResolverFactoryImpl
 */
public class SqlResolverFactoryImpl implements SqlResolverFactory {
    
    private EObject eObj;
    private QueryMetadataInterface metadata;
    
    public void setCurrentEObject(EObject eObject) {
        this.eObj = eObject;
        this.metadata = null;
    }
    
    public QueryMetadataInterface getQueryMetadata() {
    	if(metadata == null) {
    		metadata = TransformationMetadataFactory.getInstance().getModelerMetadata(this.eObj); 
    	}
        return metadata;
    }

}
