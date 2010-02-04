/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;



import org.eclipse.ui.ISharedImages;
import com.metamatrix.modeler.internal.ui.actions.workers.PasteWorker;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * The <code>PasteAction</code> class is the action that handles the global paste.
 * @since 4.0
 */
public final class PasteAction extends ModelObjectAction implements UiConstants {
    
    //============================================================================================================================
	// Constructors

    /**
	 * @since 4.0
	 */    
    public PasteAction() {
        super(UiPlugin.getDefault());
        final ISharedImages imgs = getPlugin().getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
        
        setActionWorker(new PasteWorker(true));
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

}
