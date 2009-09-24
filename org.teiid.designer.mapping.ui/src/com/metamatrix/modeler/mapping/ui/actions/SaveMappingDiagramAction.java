/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.util.SaveDiagramHelper;

/**
 * SaveDiagramAction
 */
public class SaveMappingDiagramAction extends MappingAction {
    private DiagramEditor editor;

    /**
     * Construct an instance of SaveDiagramAction.
     * 
     */
    public SaveMappingDiagramAction(DiagramEditor editor) {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.SAVE_DIAGRAM));
        this.editor = editor;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);

        setEnabled(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        // Get current DiagramEditor
        if( editor != null ) {
            SaveDiagramHelper helper = new SaveDiagramHelper();
            helper.saveDiagramToFile(editor);
        }

    }
}
