/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.vdb.edit.VdbContextValidatorResult;
import com.metamatrix.vdb.edit.VdbEditPlugin;


/** 
 * @since 5.0
 */
public class VdbContextValidatorResultImpl implements VdbContextValidatorResult {
    
    private static final IStatus[] EMPTY_STATUS_ARRAY = new IStatus[0];
    private final Map uriToProblemMap;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /** 
     * @since 5.0
     */
    public VdbContextValidatorResultImpl() {
        this.uriToProblemMap = new HashMap();
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.vdb.edit.VdbContextValidatorResult#getProblems(org.eclipse.emf.ecore.resource.Resource)
     * @since 5.0
     */
    public IStatus[] getProblems(final Resource model) {
        if (this.uriToProblemMap.containsKey(model.getURI())) {
            List problems = (List)this.uriToProblemMap.get(model.getURI());
            return (IStatus[])problems.toArray(new IStatus[problems.size()]);
        }
        return EMPTY_STATUS_ARRAY;
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    public void addProblem(final Resource eResource,
                           final int severity,
                           final String msg,
                           final Throwable t) {
        List problems = null;
        if (!this.uriToProblemMap.containsKey(eResource.getURI())) {
            problems = new ArrayList();
            this.uriToProblemMap.put(eResource.getURI(), problems);
        } else {
            problems = (List)this.uriToProblemMap.get(eResource.getURI());
        }
        problems.add(new Status(severity, VdbEditPlugin.PLUGIN_ID, RESOURCE_VALIDATION_ERROR_CODE, msg, t));
    }
}
