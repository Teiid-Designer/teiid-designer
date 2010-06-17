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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.PluginDropAdapter;
import org.eclipse.ui.part.ResourceTransfer;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.Server;
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
//    private TeiidTranslator theTargetBinding;/
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
//                SourceConnectionBindingsManager sourceBindingsMgr = this.theTargetBinding.getType().getAdmin().getSourceConnectionBindingsManager();
//                sourceBindingsMgr.createSourceConnectionBinding(mr, theTargetBinding);
//                theTargetBinding = null;
                currentTransfer = null;
                return true;
            } else if (resources[0] instanceof IFile) {
                IFile theFile = (IFile)resources[0];
                String extension = theFile.getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    boolean doIt = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                    		DqpUiConstants.UTIL.getString("TeiidViewDropAdapter.deployVdb.title"), //$NON-NLS-1$
                    		DqpUiConstants.UTIL.getString("TeiidViewDropAdapter.Do_you_wish_to_deploy_VDB_{0}_to_Teiid_server_{1}", //$NON-NLS-1$
                    				theFile.getName(), theTargetServer.getUrl()));
                    if( doIt ) {
	                    try {
	                        theTargetServer.getAdmin().deployVdb(theFile);
	                    } catch (Exception e) {
	                        DqpUiConstants.UTIL.log(IStatus.ERROR,
	                                                e,
	                                                DqpUiConstants.UTIL.getString("TeiidViewDropAdapter.problemDeployingVdbToServer", //$NON-NLS-1$
	                                                                              theFile.getName(),
	                                                                              theTargetServer.getUrl()));
	                    }
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
//            theTargetBinding = (TeiidTranslator)theTarget;
            return true;
        } else if (theTarget instanceof Server && currentTransfer != null
                   && ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
            // plugin cannot be loaded without the plugin data
            theTargetServer = (Server)theTarget;
            return true;
        }
//        theTargetBinding = null;
        theTargetServer = null;
        return false;
    }

}
