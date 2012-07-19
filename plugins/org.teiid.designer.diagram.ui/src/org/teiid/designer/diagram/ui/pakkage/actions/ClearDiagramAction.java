/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.pakkage.actions;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.DiagramAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;


/**
 * ClearTransformation
 *
 * @since 8.0
 */
public class ClearDiagramAction extends DiagramAction {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ClearDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.CLEAR_DIAGRAM));

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
        boolean enable = false;
        List sourceEObjects = null;
        if (SelectionUtilities.isSingleSelection(theSelection)) {
            sourceEObjects = new ArrayList(1);
            Object o = SelectionUtilities.getSelectedObject(theSelection);
            sourceEObjects.add(o);
        } else if (SelectionUtilities.isMultiSelection(theSelection)) {
            sourceEObjects = SelectionUtilities.getSelectedEObjects(theSelection);
        }
        enable = true;
        
        setEnabled(enable);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        System.out.println(" --->> [ClearDiagramAction.run()] !!!"); //$NON-NLS-1$
        // super.getSelectedObject()
    }
}
