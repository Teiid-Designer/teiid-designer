/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.execution;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import com.metamatrix.core.util.AutoMultiStatus;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * Customization of VdbExecutionValidatorImpl that checks for errors in the workspace instead of in the VirtualDatabase, since the
 * VdbView does not distinguish between the two.
 * 
 * @author sjacobs
 */
public class WorkspaceProblemsExecutionValidatorImpl extends VdbExecutionValidatorImpl {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Having no source models is an ERROR when in VDB View product.
     */
    private static final Status STATUS_NO_MODELS_ERROR = new Status(IStatus.ERROR, STATUS_NO_MODELS_WARNING.getPlugin(),
                                                                    STATUS_NO_MODELS_WARNING.getCode(),
                                                                    STATUS_NO_MODELS_WARNING.getMessage(),
                                                                    STATUS_NO_MODELS_WARNING.getException());

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates if the specified <code>IStatus</code> should be accepted and not filtered out.
     * 
     * @param theStatus the status being checked
     * @return <code>true</code> if accepted; <code>false</code> otherwise.
     */
    private boolean accept( IStatus theStatus ) {
        boolean result = true;

        // filter out save required errors
        if (theStatus.getCode() == VdbExecutionValidator.SAVE_REQUIRED_ERROR_CODE) {
            result = false;
        }

        return result;
    }

    /**
     * Validate the {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase} object. Checks if the there are any error markers in
     * the workspace also warns if there are no physical models in the vdb. Overridden from the base class to check the workspace
     * instead of the VirtualDatabase, which may be out-of-sync in the VdbView.
     * 
     * @param database The VirtualDatabase object to validate.
     * @return The validation status.
     * @since 4.3
     */
    @Override
    protected IStatus checkForValidationErrors( Vdb vdb ) {

        for (VdbModelEntry entry : vdb.getModelEntries()) {
            IResource resource = WorkspaceResourceFinderUtil.findIResourceByPath(entry.getName());
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

            if (!errorOccurred) {
                final IMarker[] markers = mrkrs;

                final boolean startedTxn = ModelerCore.startTxn(false, false, null, null);
                try {
                    for (int ndx = markers.length; --ndx >= 0;) {
                        IMarker iMarker = markers[ndx];
                        if (iMarker == null) {
                            continue;
                        } // endif

                        // get severity:
                        final int severity = iMarker.getAttribute(IMarker.SEVERITY, -1);
                        if (severity == IMarker.SEVERITY_ERROR) {
                            return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, VDB_VALIDATION_ERROR_CODE,
                                              DqpPlugin.Util.getString("VdbExecutionValidator.vdb_validation_errors"), null); //$NON-NLS-1$
                        }
                    }

                } finally {
                    if (startedTxn) {
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
     * 
     * @param theStatus the status being process
     * @return the processed status
     * @since 4.3
     */
    private IStatus processStatus( IStatus theStatus ) {
        IStatus result = null;
        List<IStatus> temp = new ArrayList<IStatus>();

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
                    multiStatus.add(translate(temp.get(i)));
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
     * 
     * @param theStatus the status being translated
     * @return the translated status or the same status if not translated
     * @since 4.3
     */
    private IStatus translate( IStatus theStatus ) {
        IStatus result = theStatus;

        if (theStatus.getCode() == NO_MODELS_ERROR_CODE) {
            result = STATUS_NO_MODELS_ERROR;
        }

        return result;
    }

    /*
     * Overridden to supress the SYNCH_WARNING_CODE, since synchronization is not an issue with VdbView
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.dqp.execution.VdbExecutionValidator#validateVdb(com.metamatrix.vdb.edit.VdbEditingContext)
     */
    @Override
    public IStatus validateVdb( Vdb vdb ) {
        return processStatus(super.validateVdb(vdb, false));
    }

}
