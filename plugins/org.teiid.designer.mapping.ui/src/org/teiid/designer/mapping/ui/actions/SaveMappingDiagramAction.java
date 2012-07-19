/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.util.SaveDiagramHelper;

/**
 * SaveDiagramAction
 *
 * @since 8.0
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
