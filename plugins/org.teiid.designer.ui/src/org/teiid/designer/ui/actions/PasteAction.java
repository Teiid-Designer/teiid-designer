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
import org.teiid.designer.ui.actions.workers.PasteWorker;


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
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

}
