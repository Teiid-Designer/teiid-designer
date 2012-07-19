/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;


import org.eclipse.ui.ISharedImages;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.workers.CopyWorker;


/**
 * The <code>CopyAction</code> class is the action that handles the global copy.
 * @since 8.0
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
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }
    
}
