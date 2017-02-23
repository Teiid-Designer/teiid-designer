/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;



import org.eclipse.swt.graphics.Image;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.workers.CloneWorker;
import org.teiid.designer.ui.editors.ModelEditorManager;


/**
 * The <code>CopyAction</code> class is the action that handles the global copy.
 * @since 8.0
 */
public class CloneAction extends ModelObjectAction {

    //============================================================================================================================
    // Constants
    private CloneWorker worker;

    //============================================================================================================================
    // Fields

    
    //============================================================================================================================
    // Constructors
    
    public CloneAction() {
        super(UiPlugin.getDefault());
        
        worker = new CloneWorker(true);
        setActionWorker(worker);
    }
    
    public CloneAction(Image image, Image disabledImage) {
        this();
        
        setImage(image);
        setDisabledImage(disabledImage);

    }
    
    
    //============================================================================================================================
    // Methods
        
    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        if( requiresEditorForRun() ) {
            if( worker.getFocusedObject() != null ) { 
                worker.setEditorIsOpening(true);
                worker.setTempSelection(getSelection());
                if( !ModelEditorManager.isOpen(worker.getFocusedObject()) )
                    ModelEditorManager.open(worker.getFocusedObject(), true);
            } else if( worker.getModelResource() != null ) {
                worker.setEditorIsOpening(true);
                worker.setTempSelection(getSelection());
                ModelEditorManager.activate(worker.getModelResource(), true);
            }
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
}
