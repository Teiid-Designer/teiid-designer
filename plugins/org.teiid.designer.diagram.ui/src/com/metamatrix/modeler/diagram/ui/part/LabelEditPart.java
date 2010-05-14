/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

//import org.eclipse.draw2d.ColorConstants;
import java.beans.PropertyChangeEvent;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.figure.AssociationLabelFigure;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * LabelEditPart
 */
public class LabelEditPart extends AbstractFreeEditPart {
    private static int labelCount = 0;
    /**
     * Construct an instance of LabelEditPart.
     * 
     */
    public LabelEditPart() {
        super();
    }
    
    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {

        Point posn = new Point(10, 100 + labelCount*5);
        Figure newFigure = new AssociationLabelFigure(((DiagramModelNode) getModel()).getName(), false, null);
//        NoteFigure noteFigure = new NoteFigure((Composite)getRoot().getViewer().getControl());
//        noteFigure.setText("SOME NOTE FIGURE WITH LOTS OF TEXT HERE.");
//        IFigure newFigure = noteFigure.getViewport().getContents();
//        newFigure.setBackgroundColor(ColorConstants.cyan);
//        newFigure.setBorder(new LineBorder(2));
        newFigure.setLocation(posn);
        ((DiagramModelNode) getModel()).setPosition(posn);
        ((DiagramModelNode) getModel()).setSize(newFigure.getSize());
        labelCount ++;
        
        return newFigure;
    }
    
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     * You need to tell how children nodes will be layed out...
    **/
    @Override
    protected void createEditPolicies() {
        setSelectablePart(false);
    }
    
    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String prop = evt.getPropertyName();
        
        if( prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
            refreshName();
            ((LabeledRectangleFigure)getFigure()).resize();
            ((DiagramModelNode)getModel()).setSize(getFigure().getSize());
//            refreshVisuals();
        }
    }
    
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
    **/
    @Override
    protected void refreshVisuals() {
        Point loc = ((DiagramModelNode) getModel()).getPosition();
        Dimension size = ((DiagramModelNode) getModel()).getSize();
        Rectangle r = new Rectangle(loc, size);

        ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
        getFigure().repaint();
    }
    
    
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	@Override
    public DragTracker getDragTracker(Request request) {
		// We wanted to remove selection capability to the Labeled objects in the diagram.
		// This was the only way I could figure out how to do it.  Don't let it play
		// in selection...
		return null;
	}

}
