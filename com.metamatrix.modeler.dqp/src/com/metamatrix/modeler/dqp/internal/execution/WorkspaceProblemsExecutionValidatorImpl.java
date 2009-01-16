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

package com.metamatrix.modeler.dqp.internal.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.util.AutoMultiStatus;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * Customization of VdbExecutionValidatorImpl that checks for errors in the workspace instead
 * of in the VirtualDatabase, since the VdbView does not distinguish between the two.
 * @author sjacobs
 *
 */
public class WorkspaceProblemsExecutionValidatorImpl extends VdbExecutionValidatorImpl {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Having no source models is an ERROR when in VDB View product.
     */
    private static final Status STATUS_NO_MODELS_ERROR = new Status(IStatus.ERROR,
                                                                    STATUS_NO_MODELS_WARNING.getPlugin(),
                                                                    STATUS_NO_MODELS_WARNING.getCode(),
                                                                    STATUS_NO_MODELS_WARNING.getMessage(),
                                                                    STATUS_NO_MODELS_WARNING.getException());

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Indicates if the specified <code>IStatus</code> should be accepted and not filtered out.
     * @param theStatus the status being checked
     * @return <code>true</code> if accepted; <code>false</code> otherwise.
     */
    private boolean accept(IStatus theStatus) {
        boolean result = true;
        
        // filter out save required errors
        if (theStatus.getCode() == VdbExecutionValidator.SAVE_REQUIRED_ERROR_CODE) {
            result = false;
        }
        
        return result;
    }
    
    /**
     * Validate the {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase} object. Checks if
     * the there are any error markers in the workspace also warns if there are no physical models in the vdb.
     * Overridden from the base class to check the workspace instead of the VirtualDatabase, which may
     * be out-of-sync in the VdbView. 
     * @param database The VirtualDatabase object to validate.
     * @return The validation status. 
     * @since 4.3
     */
	@Override
    protected IStatus checkForValidationErrors(VirtualDatabase database) {
		Collection modelRefList = database.getModels();
		for ( Iterator iter = modelRefList.iterator() ; iter.hasNext() ; ) {
			ModelReference mr = (ModelReference) iter.next();
            IResource resource = WorkspaceResourceFinderUtil.findIResource(mr.getModelLocation());
            if (resource == null) {
                return OK_STATUS;
            }
            IMarker[] mrkrs = null;
            boolean errorOccurred = false;
            try {
            	mrkrs = resource.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
            } catch (CoreException ex) {
            	errorOccurred = true;
            	DqpPlugin.Util.log(ex);
            }
            
            if ( ! errorOccurred ) {
                final IMarker[] markers = mrkrs;
                
                final boolean startedTxn = ModelerCore.startTxn(false, false, null, null);
                try {
	                for (int ndx = markers.length;  --ndx >= 0;) {
	                    IMarker iMarker = markers[ndx];
                        if( iMarker == null ) {
                            continue;
                        } // endif

	                    // get severity:
	                    final int severity = iMarker.getAttribute(IMarker.SEVERITY, -1);
	                    if (severity == IMarker.SEVERITY_ERROR) {
	                        return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, VDB_VALIDATION_ERROR_CODE, DqpPlugin.Util.getString("VdbExecutionValidator.vdb_validation_errors"), null); //$NON-NLS-1$
	                    }
	                }
	
                } finally {
	                if(startedTxn) {
	                    ModelerCore.commitTxn();
	                }
                }
            	
            }
		}

		return OK_STATUS;
	}

    /**
     * Performs filtering and translation of the specified <code>IStatus</code>. If {@link org.eclipse.core.runtime.MultiStatus},
     * the children are processed.
     * @param theStatus the status being process
     * @return the processed status
     * @since 4.3
     */
    private IStatus processStatus(IStatus theStatus) {
        IStatus result = null;
        List temp = new ArrayList();
        
        // if multistatus check children
        if (theStatus.isMultiStatus()) {
            IStatus kids[] = theStatus.getChildren();
            
            for (int i = 0; i < kids.length; ++i) {
                if (accept(kids[i])) {
                    temp.add(translate(kids[i]));
                }
            }
            
            if (temp.isEmpty()) {
                // all children have been filtered out, return a good single status
                result = OK_STATUS;
            } else {
                // need to create new status with translated children since you
                // can't delete children
                AutoMultiStatus multiStatus = new AutoMultiStatus(OK_STATUS);
                
                for (int size = temp.size(), i = 0; i < size; ++i) {
                    multiStatus.add(translate((IStatus)temp.get(i)));
                }
                
                result = multiStatus;
            }
        } else {
            if (accept(theStatus)) {
                result = translate(theStatus);
            } else {
                result = OK_STATUS;
            }
        }
        
        return result;
    }
    
    /**
     * Translates the properties of the specified <code>IStatus</code>. 
     * @param theStatus the status being translated
     * @return the translated status or the same status if not translated
     * @since 4.3
     */
    private IStatus translate(IStatus theStatus) {
        IStatus result = theStatus;
        
        if (theStatus.getCode() == NO_MODELS_ERROR_CODE) {
            result = STATUS_NO_MODELS_ERROR;
        }
        
        return result;
    }

	/*
	 * Overridden to supress the SYNCH_WARNING_CODE, since synchronization is not an issue with VdbView
	 *  (non-Javadoc)
	 * @see com.metamatrix.modeler.dqp.execution.VdbExecutionValidator#validateVdb(java.lang.String)
	 */
	@Override
    public IStatus validateVdb(String pathToVdb) {
		return processStatus(super.validateVdb(pathToVdb, false));
	}

	/*
	 * Overridden to supress the SYNCH_WARNING_CODE, since synchronization is not an issue with VdbView
	 *  (non-Javadoc)
	 * @see com.metamatrix.modeler.dqp.execution.VdbExecutionValidator#validateVdb(com.metamatrix.vdb.edit.VdbEditingContext)
	 */
	@Override
    public IStatus validateVdb(VdbEditingContext context) {
        return processStatus(super.validateVdb(context, false));
	}
    
}
