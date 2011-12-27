/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

import java.beans.PropertyChangeEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;

/**
 * AbstractFreeEditPart
 */
public abstract class AbstractFreeEditPart extends AbstractDefaultEditPart {

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#layout(boolean)
    **/
    @Override
    public void layout( boolean layoutChildren ) {
        // DO NOTHING
    }

    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    @Override
    public void propertyChange( PropertyChangeEvent evt ) {
        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            createOrUpdateAnchorsLocations(true);
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            createOrUpdateAnchorsLocations(true);
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PROPERTIES)) {
            refreshVisuals();
            // refreshChildren();
            // layout(DiagramEditPart.LAYOUT_CHILDREN);
        }
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#isPrimaryParent()
    **/
    @Override
    public boolean isPrimaryParent() {
        return true;
    }

    public EditPart getEditPart( EObject someModelObject ) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getSelectionHandler()
     */
    public IDiagramSelectionHandler getSelectionHandler() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setSelectionHandler(com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler)
     */
    public void setSelectionHandler( IDiagramSelectionHandler selectionHandler ) {
    }

}
