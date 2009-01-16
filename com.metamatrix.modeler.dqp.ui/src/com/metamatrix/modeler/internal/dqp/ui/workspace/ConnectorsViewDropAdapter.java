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

package com.metamatrix.modeler.internal.dqp.ui.workspace;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.PluginDropAdapter;
import org.eclipse.ui.part.ResourceTransfer;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;


/** 
 * Provides simple DROP capability to the ConnectorsView.
 * 
 * performDrop() locates the ModelResource for the selected/dragged IResource and creates a source binding object using the drop
 * target ConnectorBinding.
 * @since 5.0
 */
public class ConnectorsViewDropAdapter extends PluginDropAdapter {
    /**
     * The current transfer data, or <code>null</code> if none.
     */
    private TransferData currentTransfer;
    private ConnectorBinding theTargetBinding;
    
    /** 
     * @param theViewer
     * @since 5.0
     */
    public ConnectorsViewDropAdapter(StructuredViewer theViewer) {
        super(theViewer);
    }

    /**
     *  
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
     * @since 5.0
     */
    @Override
    public boolean performDrop(Object theData) {
        //System.out.println("ConnectorsViewDropAdapter.performDrop()  theData = " + theData.getClass().toString());
        if( theData instanceof IResource[] ) {
            IResource[] resources = (IResource[])theData;

            ModelResource mr = ModelerCore.getModelWorkspace().findModelResource(resources[0]);
            if( mr != null && ModelIdentifier.isPhysicalModelType(mr)) {
                DqpPlugin.getWorkspaceConfig().createSourceBinding(mr, theTargetBinding);
                theTargetBinding = null;
                currentTransfer = null;
                return true;
            }
        }
        return false;
    }

    /**
     *  
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
     * @since 5.0
     */
    @Override
    public boolean validateDrop(Object theTarget,
                                int theOperation,
                                TransferData theTransferType) {

       currentTransfer = theTransferType;
       if (theTarget instanceof ConnectorBinding && currentTransfer != null && ResourceTransfer.getInstance().isSupportedType(currentTransfer)) {
           //plugin cannot be loaded without the plugin data
           theTargetBinding = (ConnectorBinding)theTarget;
           return true;
       }
       theTargetBinding = null;
       return false;
    }

}
