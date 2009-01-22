/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * @author blafond To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and
 *         Comments
 */
public class DiagramNodeSelectionEditPolicy extends SelectionEditPolicy {
    private boolean activating = false;

    /**
	 * 
	 */
    public DiagramNodeSelectionEditPolicy() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#hideSelection()
     */
    @Override
    protected void hideSelection() {
        // TODO Auto-generated method stub
        if (getHost().getViewer() instanceof DiagramViewer && getHost().getViewer().getSelectedEditParts().size() == 1
            && !isActivating()) {
            ((DiagramViewer)getHost().getViewer()).getSelectionHandler().hiliteDependencies(null);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#showSelection()
     */
    @Override
    protected void showSelection() {
        // TODO Auto-generated method stub
        EditPart targetEditPart = getHost();
        if (!(targetEditPart instanceof NodeConnectionEditPart) && targetEditPart.getViewer() instanceof DiagramViewer) {
            ((DiagramViewer)targetEditPart.getViewer()).getSelectionHandler().clearConnectionHilites();
        }

        if (targetEditPart instanceof DiagramEditPart) {
            DiagramEditPart selectedPart = (DiagramEditPart)targetEditPart;
            if (!selectedPart.isSelectablePart()) {
                selectedPart.getViewer().deselect(selectedPart);
                DiagramEditPart parentPart = selectedPart.getPrimaryParent();
                selectedPart.getViewer().select(parentPart);
            }
        }
        // String debugMessage = " +++----- >>>  DiagramNodeSelctionPolicy.showSelection(editPart):  EditPart = " +
        // targetEditPart;
        // System.out.println(debugMessage);

        if (getHost().getViewer() instanceof DiagramViewer && getHost().getViewer().getSelectedEditParts().size() == 1) {
            if (targetEditPart instanceof DiagramEditPart) {
                DiagramEditPart selectedPart = (DiagramEditPart)targetEditPart;
                Object diagramNodeModelObject = selectedPart.getModel();
                if (diagramNodeModelObject != null && diagramNodeModelObject instanceof DiagramModelNode) {
                    Object actualModelObject = ((DiagramModelNode)diagramNodeModelObject).getModelObject();
                    if (actualModelObject != null && !isActivating()) ((DiagramViewer)getHost().getViewer()).getSelectionHandler().hiliteDependencies(actualModelObject);
                }
            } else if (targetEditPart instanceof NodeConnectionEditPart && !isActivating()) {
                ((DiagramViewer)getHost().getViewer()).getSelectionHandler().hiliteDependencies(targetEditPart);
            }
        }
    }

    @Override
    public void activate() {
        this.activating = true;
        super.activate();
        this.activating = false;
    }

    public boolean isActivating() {
        return this.activating;
    }
}
