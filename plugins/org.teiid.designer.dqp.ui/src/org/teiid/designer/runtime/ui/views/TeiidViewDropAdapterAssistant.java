/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.part.ResourceTransfer;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.actions.DeployVdbAction;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;

/**
 * @since 8.0
 */
public class TeiidViewDropAdapterAssistant extends CommonDropAdapterAssistant {

    /**
     * The current transfer data, or <code>null</code> if none.
     */
    private TransferData currentTransfer;
    
    private TeiidServer theTargetServer;
    
    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {
        currentTransfer = transferType;
        if (target instanceof TeiidTranslator && currentTransfer != null
            && ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
            // plugin cannot be loaded without the plugin data
            return Status.OK_STATUS;
        } else if (target instanceof TeiidServer && currentTransfer != null
                   && ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
            // plugin cannot be loaded without the plugin data
            theTargetServer = (TeiidServer) target;
            return Status.OK_STATUS;
        }
        
        theTargetServer = null;
        return Status.CANCEL_STATUS;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
        Object theData = aDropTargetEvent.data;
        if (! (theData instanceof IResource[]))
            return Status.CANCEL_STATUS;
        
        IResource[] resources = (IResource[])theData;
        for( IResource resource : resources ) {
            ModelResource mr = ModelerCore.getModelWorkspace().findModelResource(resource);
    
            if (mr != null && ModelIdentifier.isPhysicalModelType(mr)) {
                currentTransfer = null;
                return Status.OK_STATUS;
            } else if (resource instanceof IFile) {
                IFile theFile = (IFile)resource;
                String extension = theFile.getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    try {
                        DeployVdbAction.deployVdb(theTargetServer, theFile);
                    } catch (Exception e) {
                        DqpUiConstants.UTIL.log(IStatus.ERROR,
                                                e,
                                                DqpUiConstants.UTIL.getString(  "TeiidViewDropAdapter.problemDeployingVdbToServer", //$NON-NLS-1$
                                                                                theFile.getName(),
                                                                                theTargetServer));
                    }
                }
            }
        }
        
        return Status.CANCEL_STATUS;
    }

}
