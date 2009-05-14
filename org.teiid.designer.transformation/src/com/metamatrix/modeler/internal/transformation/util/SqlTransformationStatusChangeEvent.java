/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
