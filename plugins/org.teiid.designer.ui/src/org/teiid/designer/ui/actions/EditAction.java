/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditorManager;


/**
 * The <code>EditAction</code> class is the action that handles editing model objects.
 * @since 4.0
 */
public class EditAction extends ModelObjectAction {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public EditAction() {
        super(UiPlugin.getDefault());
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        determineEnablement();
    }

    @Override
    protected void doRun() {
        ModelEditorManager.edit((EObject) super.getSelectedObject());
        determineEnablement();
    }
    
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;

        if (SelectionUtilities.isSingleSelection(getSelection()) && canLegallyEditResource()) {
            EObject o = SelectionUtilities.getSelectedEObject(getSelection());
            enable = ModelEditorManager.canEdit(o);
        }

        setEnabled(enable);
    }
    
    public void setEnabledOnce() {
        setEnabled(true);
    }
    
    /* (non-Javadoc)
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
}
