/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;


import org.eclipse.ui.ISharedImages;

import com.metamatrix.modeler.internal.ui.actions.workers.CopyWorker;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * The <code>CopyAction</code> class is the action that handles the global copy.
 * @since 4.0
 */
public class CopyAction extends ModelObjectAction implements UiConstants {
    //============================================================================================================================
    // Constants
    
    //============================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */    
    public CopyAction() {
        super(UiPlugin.getDefault());
        final ISharedImages imgs = getPlugin().getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
        
        setActionWorker(new CopyWorker(true));
        
    }

    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }
    
}
