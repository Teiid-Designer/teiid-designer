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

package com.metamatrix.modeler.internal.transformation.util;

import java.util.EventObject;

import org.eclipse.emf.ecore.EObject;

/**
 * The <code>SqlTransformationStatusChangeEvent</code> class is the event that the
 * is fired to SqlMappingRootCache listeners whenever the state of a transformation
 * has changed.
 */
public class SqlTransformationStatusChangeEvent extends EventObject {

    /**
     */
    private static final long serialVersionUID = 1L;
    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////
    // The mappingRoot that has changed
    private EObject transMappingRoot = null;
    private boolean overwriteDirty;

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////

    public SqlTransformationStatusChangeEvent(EObject transMappingRoot,Object source) {
        this(transMappingRoot,source,false);
    }
    
    /**
     * @param object
     * @param source
     * @param userOverride
     */
    public SqlTransformationStatusChangeEvent(EObject transMappingRoot, Object source, boolean overwriteDirty) {
        super(source);
        this.transMappingRoot = transMappingRoot;
        this.overwriteDirty = overwriteDirty;
    }

    /**
     * Get the MappingRoot that has changed.
     * @return the mapping root
     */
    public EObject getMappingRoot() {
        return this.transMappingRoot;
    }

    public boolean isOverwriteDirty() {
        return overwriteDirty;
    }
}
