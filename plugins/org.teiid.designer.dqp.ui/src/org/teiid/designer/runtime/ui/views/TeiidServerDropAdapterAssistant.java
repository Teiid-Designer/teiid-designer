/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.actions.DeployVdbAction;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;

/**
 * @since 8.0
 */
public class TeiidServerDropAdapterAssistant extends CommonDropAdapterAssistant {

    /**
     * The current transfer data, or <code>null</code> if none.
     */
    private TransferData currentTransfer;
    
    private ITeiidServer theTargetServer;
    
    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {
        currentTransfer = transferType;

        if (RuntimeAssistant.adapt(target, ITeiidTranslator.class) != null && currentTransfer != null
            && isSupportedType(currentTransfer)) {
            // plugin cannot be loaded without the plugin data
            return Status.OK_STATUS;
        } else if (RuntimeAssistant.adapt(target, ITeiidServer.class) != null && currentTransfer != null
                   && isSupportedType(currentTransfer)) {
            // plugin cannot be loaded without the plugin data
            theTargetServer = RuntimeAssistant.adapt(target, ITeiidServer.class);
            return Status.OK_STATUS;
        }
        
        theTargetServer = null;
        return Status.CANCEL_STATUS;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
        Object theData = aDropTargetEvent.data;

        if (theData instanceof ISelection) {
            List<Object> selectedObjects = SelectionUtilities.getSelectedObjects((ISelection) theData);
            for (Object o : selectedObjects) {
                if (o instanceof IResource) {
                    IStatus status = handleResource((IResource) o);
                    if (status != Status.OK_STATUS)
                        return status;
                }
            }
        }
        else if (theData instanceof IResource[]) {
            IResource[] resources = (IResource[])theData;
            for( IResource resource : resources ) {
                IStatus status = handleResource(resource);
                if (status != Status.OK_STATUS)
                    return status;
            }
        }
        else {
            return Status.CANCEL_STATUS;
        }
        
        return Status.OK_STATUS;
    }
    
    private IStatus handleResource(IResource resource) {
        ModelResource mr = ModelerCore.getModelWorkspace().findModelResource(resource);
    
        if (mr != null && ModelIdentifier.isPhysicalModelType(mr)) {
            currentTransfer = null;
            return Status.OK_STATUS;
        } else if (resource instanceof IFile) {
            IFile theFile = (IFile)resource;
                
            if ("vdb".equals(theFile.getFileExtension())) { //$NON-NLS-1$
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
        
        return Status.CANCEL_STATUS;
    }

}
