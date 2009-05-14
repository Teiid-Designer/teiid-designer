/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.part;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.HiliteDndNodeSelectionEditPolicy;


/** This class provides customized hilighting and drag and drop methodology for Relationship edit
 * parts.  In particular, RelationshipNodeEditPart.
 * This class extends the standard HiliteDndNodeSelectionEditPolicy.
 * @since 4.2
 */
public class HiliteRelationshipNodeSelectionPolicy extends HiliteDndNodeSelectionEditPolicy {

    public HiliteRelationshipNodeSelectionPolicy() {
        super();
    }

    /** 
     * Overrides the super.showTargetFeedback() because it needs to ask a different question.
     * (i.e. not canCreateAssociation())
     * @see org.eclipse.gef.EditPolicy#showTargetFeedback(org.eclipse.gef.Request)
     * @since 4.2
     */
    @Override
    public void showTargetFeedback(Request request) {
        if (request.getType().equals(RequestConstants.REQ_MOVE)
            || request.getType().equals(RequestConstants.REQ_ADD)) {
            if( canAcceptAddRequest() ) {
                showHighlight(true);
            }
        }
    }
    
    /*
     * Internal method provides ability to check if the hovered-over edit part is
     * really a relationship object.
     */
    private boolean canAcceptAddRequest() {
        boolean canCreate = false;
        if( !isDiagramReadOnly()  ) {
            DiagramEditPart thisEditPart = (DiagramEditPart)getHost();
            if( thisEditPart instanceof RelationshipNodeEditPart &&
                thisEditPart.getModel() != null && 
                thisEditPart.getModel() instanceof DiagramModelNode) {
                DiagramModelNode nextDMN = (DiagramModelNode)thisEditPart.getModel();
                if( nextDMN.getModelObject() != null ) {
                    if( nextDMN.getModelObject() instanceof Relationship )
                        canCreate = true;
                }

            }
        }
        return canCreate;
    }
    
    @Override
    public boolean understandsRequest(Request request) {
        return false;
    }
    
    @Override
    public Command getCommand(Request request) {
        return null;
    }
}
