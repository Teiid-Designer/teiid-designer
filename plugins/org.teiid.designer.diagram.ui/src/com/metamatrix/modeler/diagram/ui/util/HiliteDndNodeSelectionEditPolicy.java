/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.SetAssociationCommand;
import com.metamatrix.modeler.diagram.ui.part.AbstractDefaultEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.DropEditPart;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * HiliteDndNodeSelectionEditPolicy
 */

/**
 * @author blafond To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and
 *         Comments
 */
public class HiliteDndNodeSelectionEditPolicy extends DiagramNodeSelectionEditPolicy {
    protected boolean hilited = false;

    private boolean canCreateAssociation() {
        boolean canCreate = false;
        if (!isDiagramReadOnly() && isPackageDiagram()) {
            final List selectedEObjects = getSelectedEObjects();
            if (selectedEObjects.size() > 1) try {
                final Collection descriptors = ModelerCore.getModelEditor().getNewAssociationDescriptors(selectedEObjects);
                if (descriptors.size() == 1) {
                    final AssociationDescriptor theDescriptor = (AssociationDescriptor)descriptors.iterator().next();
                    if (theDescriptor != null) printDescriptor(theDescriptor);
                    if (theDescriptor.isAmbiguous()) DiagramUiConstants.Util.log(IStatus.INFO,
                                                                                 "HiliteDndNodeSelectionEditPolicy.canCreateAssociation() The Association Descriptor is AMBIGUOUS"); //$NON-NLS-1$

                    if (theDescriptor.isComplete()) canCreate = true;
                } else {
                    // System.out.println(" -->> HDndNSP.canCreateAssociation() # Descriptors = " + descriptors.size());
                    final Iterator iter = descriptors.iterator();
                    AssociationDescriptor theDescriptor = null;
                    while (iter.hasNext()) {
                        theDescriptor = (AssociationDescriptor)iter.next();
                        if (theDescriptor.isAmbiguous()) DiagramUiConstants.Util.log(IStatus.INFO,
                                                                                     "HiliteDndNodeSelectionEditPolicy.canCreateAssociation() The Association Descriptor is AMBIGUOUS"); //$NON-NLS-1$

                        if (theDescriptor.isComplete()) canCreate = true;

                        if (canCreate) break;
                    }
                }
            } catch (final ModelerCoreException theException) {
                DiagramUiConstants.Util.log(IStatus.ERROR,
                                            "HiliteDndNodeSelectionEditPolicy.canCreateAssociation() ERROR getting New Association Descriptors"); //$NON-NLS-1$
            }
        }
        // System.out.println(" ---------->> HDndNSP.canCreateAssociation() = " + canCreate + " for Host = "+ getHost());
        return canCreate;
    }

    @Override
    public void eraseTargetFeedback( final Request request ) {
        if (request != null && request.getType().equals(RequestConstants.REQ_ADD)) showHighlight(false);
    }

    @Override
    public Command getCommand( final Request request ) {
        final DiagramEditPart thisEditPart = (DiagramEditPart)getHost();
        if (thisEditPart.getModelObject() instanceof Diagram) return null;
        final List eObjs = getSelectedEObjects();
        if (eObjs.size() > 1 && request.getType().equals(RequestConstants.REQ_MOVE)
            || request.getType().equals(RequestConstants.REQ_ADD)) {
            final SetAssociationCommand newCommand = new SetAssociationCommand(getSelectedEObjects());
            return newCommand;
        }
        return null;
    }

    private List getSelectedEditParts() {

        if (getViewer() != null) return getViewer().getSelectedEditParts();

        return Collections.EMPTY_LIST;
    }

    private List getSelectedEObjects() {
        final Iterator iter = getSelectedEditParts().iterator();
        final List selectedEObjects = new ArrayList();
        DiagramEditPart nextEP = null;
        DiagramModelNode nextDMN = null;
        while (iter.hasNext()) {
            nextEP = (DiagramEditPart)iter.next();
            if (nextEP.getModel() != null && nextEP.getModel() instanceof DiagramModelNode) {
                nextDMN = (DiagramModelNode)nextEP.getModel();
                if (nextDMN.getModelObject() != null && !selectedEObjects.contains(nextDMN.getModelObject())) // System.out.println(" -->> HDndNSP.getSelectedEObjects()  added: "+
                                                                                                              // ModelerCore.getModelEditor().getName(nextDMN.getModelObject()));
                selectedEObjects.add(nextDMN.getModelObject());
            }
        }

        final DiagramEditPart thisEditPart = (DiagramEditPart)getHost();
        if (thisEditPart.getModel() != null && thisEditPart.getModel() instanceof DiagramModelNode) {
            nextDMN = (DiagramModelNode)thisEditPart.getModel();
            if (nextDMN.getModelObject() != null && !selectedEObjects.contains(nextDMN.getModelObject())) // System.out.println(" -->> HDndNSP.getSelectedEObjects()  added: "+
                                                                                                          // ModelerCore.getModelEditor().getName(nextDMN.getModelObject()));
            selectedEObjects.add(nextDMN.getModelObject());

        }

        return selectedEObjects;
    }

    @Override
    public EditPart getTargetEditPart( final Request request ) {
        if (request != null && request.getType() != null) return request.getType().equals(RequestConstants.REQ_ADD) ? getHost() : null;

        return null;
    }

    private DiagramViewer getViewer() {
        final EditPart ep = getHost();

        if (ep != null && ep instanceof AbstractDefaultEditPart) {
            final AbstractDefaultEditPart adep = (AbstractDefaultEditPart)ep;

            if (adep.isValidViewer()) if (adep.getViewer() instanceof DiagramViewer) return (DiagramViewer)adep.getViewer();
        }
        return null;
    }

    protected boolean isDiagramReadOnly() {
        if (getViewer() != null) {
            final Diagram diagram = getViewer().getEditor().getDiagram();

            return ModelObjectUtilities.isReadOnly(diagram);
        }

        return true;
    }

    private boolean isPackageDiagram() {
        boolean result = false;

        if (getViewer() != null) {

            final Diagram diagram = getViewer().getEditor().getDiagram();
            if (diagram != null && diagram.getType() != null && diagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID)) result = true;
        }
        return result;
    }

    private void printDescriptor( final AssociationDescriptor theDescriptor ) {
        // System.out.println("  ------------------- AssociationDescriptor ------------------------");
        // System.out.println("  desciptor = " + theDescriptor);
        // System.out.println("  desciptor.isAmbiguous()" + theDescriptor.isAmbiguous());
        // System.out.println("  desciptor.isComplete()" + theDescriptor.isComplete());
        // AssociationDescriptor[] dArray = theDescriptor.getChildren();
        // AssociationDescriptor nextD = null;
        // for(int i=0; i<dArray.length; i++ ) {
        // System.out.println("        NESTED desciptor = " + dArray[i]);
        // System.out.println("        NESTED desciptor.isAmbiguous()" + dArray[i].isAmbiguous());
        // System.out.println("        NESTED desciptor.isComplete()" + dArray[i].isComplete());
        // }
        // System.out.println("  ------------------- AssociationDescriptor ------------------------");
    }

    protected void showHighlight( final boolean hilite ) {
        final DiagramEditPart selectedEditPart = (DiagramEditPart)getHost();
        if (selectedEditPart instanceof DropEditPart) ((DropEditPart)selectedEditPart).hilite(hilite);
        else if (hilite && !hilited) {
            hilited = true;
            selectedEditPart.hiliteBackground(ColorConstants.lightGreen);

        } else if (!hilite && hilited) {
            selectedEditPart.hiliteBackground(null);
            hilited = false;
        }
    }

    @Override
    public void showTargetFeedback( final Request request ) {
        if (request.getType().equals(RequestConstants.REQ_MOVE) || request.getType().equals(RequestConstants.REQ_ADD)) // DiagramEditPart
                                                                                                                       // selectedEditPart
                                                                                                                       // =
                                                                                                                       // (DiagramEditPart)getHost();
        // if( selectedEditPart.shouldHiliteBackground(getSelectedEditParts()) )
        if (canCreateAssociation()) showHighlight(true);

    }

    @Override
    public boolean understandsRequest( final Request request ) {
        return false;
    }
}
