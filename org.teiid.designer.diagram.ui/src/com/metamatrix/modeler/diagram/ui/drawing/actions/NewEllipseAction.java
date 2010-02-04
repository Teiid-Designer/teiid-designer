/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingConstants;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingModelFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * NewEllipseAction
 */
public class NewEllipseAction extends DrawingAction {

    /**
     * Construct an instance of NewEllipseAction.
     * 
     */
    public NewEllipseAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.NEW_ELLIPSE));
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        // Let's get ahold of the current diagram...
        DiagramModelNode diagramNode = editor.getCurrentModel();
        if( diagramNode != null ) {
            DiagramModelNode newNode = DrawingModelFactory.createModelNode(DrawingConstants.TypeId.ELLIPSE, diagramNode);
            if( newNode != null )
                diagramNode.addChild(newNode);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
    }

}
