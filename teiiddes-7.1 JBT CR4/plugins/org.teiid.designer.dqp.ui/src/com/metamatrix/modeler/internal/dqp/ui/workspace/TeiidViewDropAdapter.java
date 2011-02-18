/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.PluginDropAdapter;
import org.eclipse.ui.part.ResourceTransfer;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.ui.DeployVdbAction;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;

/**
 * Provides simple DROP capability to the ConnectorsView. performDrop() locates the ModelResource for the selected/dragged
 * IResource and creates a source binding object using the drop target ConnectorBinding.
 * 
 * @since 5.0
 */
public class TeiidViewDropAdapter extends PluginDropAdapter {
    /**
     * The current transfer data, or <code>null</code> if none.
     */
    private TransferData currentTransfer;
    // private TeiidTranslator theTargetBinding;/
    private Server theTargetServer;

    /**
     * @param theViewer
     * @since 5.0
     */
    public TeiidViewDropAdapter( StructuredViewer theViewer ) {
        super(theViewer);
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
     * @since 5.0
     */
    @Override
    public boolean performDrop( Object theData ) {
        if (theData instanceof IResource[]) {
            IResource[] resources = (IResource[])theData;
            ModelResource mr = ModelerCore.getModelWorkspace().findModelResource(resources[0]);

            if (mr != null && ModelIdentifier.isPhysicalModelType(mr)) {
                currentTransfer = null;
                return true;
            } else if (resources[0] instanceof IFile) {
                IFile theFile = (IFile)resources[0];
                String extension = theFile.getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    try {
                        DeployVdbAction.deployVdb(theTargetServer, theFile);

                        // VDB deployedVDB = theTargetServer.getAdmin().deployVdb(theFile);
                        // if (deployedVDB == null) {
                        // MessageDialog.openError(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                        //                                                    DqpUiConstants.UTIL.getString("DeployVdbAction.vdbNotDeployedTitle"), //$NON-NLS-1$
                        //                                                    DqpUiConstants.UTIL.getString("DeployVdbAction.vdbNotDeployedMessage", theFile.getName())); //$NON-NLS-1$
                        // } else if (deployedVDB.getStatus().equals(VDB.Status.INACTIVE)) {
                        // StringBuilder message = new StringBuilder(
                        //                                                                      DqpUiConstants.UTIL.getString("ExecuteVDBAction.vdbNotActiveMessage", deployedVDB.getName())); //$NON-NLS-1$
                        // for (String error : deployedVDB.getValidityErrors()) {
                        //                                message.append("\nERROR:\t").append(error); //$NON-NLS-1$
                        // }
                        // MessageDialog.openWarning(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                        //                                                      DqpUiConstants.UTIL.getString("DeployVdbAction.vdbNotActiveTitle"), //$NON-NLS-1$
                        // message.toString());
                        // }
                    } catch (Exception e) {
                        DqpUiConstants.UTIL.log(IStatus.ERROR,
                                                e,
                                                DqpUiConstants.UTIL.getString("TeiidViewDropAdapter.problemDeployingVdbToServer", //$NON-NLS-1$
                                                                              theFile.getName(),
                                                                              theTargetServer.getTeiidAdminInfo().getURL()));
                    }
                }
            }
        }

        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
     * @since 5.0
     */
    @Override
    public boolean validateDrop( Object theTarget,
                                 int theOperation,
                                 TransferData theTransferType ) {

        currentTransfer = theTransferType;
        if (theTarget instanceof TeiidTranslator && currentTransfer != null
            && ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
            // plugin cannot be loaded without the plugin data
            // theTargetBinding = (TeiidTranslator)theTarget;
            return true;
        } else if (theTarget instanceof Server && currentTransfer != null
                   && ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
            // plugin cannot be loaded without the plugin data
            theTargetServer = (Server)theTarget;
            return true;
        }
        // theTargetBinding = null;
        theTargetServer = null;
        return false;
    }

}
