/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.vdb.edit.VdbContextValidator;
import com.metamatrix.vdb.edit.VdbContextValidatorResult;


/** 
 * An implementation of VdbContextValidator that performs no validation
 * and returns an empty VdbContextValidatorResult
 * @since 5.0
 */
public class NullVdbContextValidator implements VdbContextValidator {
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /** 
     * @since 5.0
     */
    public NullVdbContextValidator() {
        super();
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.vdb.edit.VdbContextValidator#validate(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.emf.ecore.resource.Resource[])
     * @since 5.0
     */
    public VdbContextValidatorResult validate(final IProgressMonitor monitor, final Resource[] models) {
        ArgCheck.isNotNull(models);
        return new VdbContextValidatorResultImpl();
    }

}
