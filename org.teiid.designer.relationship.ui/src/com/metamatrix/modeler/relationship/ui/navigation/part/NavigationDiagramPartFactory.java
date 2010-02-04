/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingPartFactory;
import com.metamatrix.modeler.diagram.ui.drawing.model.DrawingModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.relationship.NavigationHistory;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.model.DummyFocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.LabelModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationContainerModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationDiagramLink;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationDiagramNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NonFocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.RelationshipModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationDiagramPartFactory extends AbstractDiagramEditPartFactory implements UiConstants {
	private DrawingPartFactory drawingPartFactory;
	private NavigationDiagramFigureFactory figureFactory;
//	private static final String THIS_CLASS = "RelationshipDiagramPartFactory"; //$NON-NLS-1$
//	private static final String diagramTypeId = PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID;

	/**
	 * 
	 */
	public NavigationDiagramPartFactory(NavigationHistory history) {
		super();
	
		if( drawingPartFactory == null )
			drawingPartFactory = new DrawingPartFactory();
	        
		if( figureFactory == null )
			figureFactory = new NavigationDiagramFigureFactory(history);
	}
	/**
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(EditPart, Object)
	**/
	@Override
    public EditPart createEditPart(EditPart iContext, Object iModel) {
		EditPart editPart = null;

		if( iModel instanceof DrawingModelNode ) {
			editPart = drawingPartFactory.createEditPart(iContext, iModel);
		} else if( iModel instanceof NavigationDiagramNode ) {
			editPart = new NavigationDiagramEditPart(figureFactory);
			// We need to tell the figure factory to update it's fwd/back focus cached focus nodes.
			figureFactory.resetHistoryFocusNodes();
		} else if( iModel instanceof RelationshipModelNode ) {
			editPart = new RelationshipNodeEditPart(figureFactory);
		} else if( iModel instanceof DummyFocusModelNode ) {
			editPart = new DummyFocusNodeEditPart(figureFactory);
		} else if( iModel instanceof FocusModelNode ) {
			editPart = new FocusNodeEditPart(figureFactory);
		} else if( iModel instanceof NonFocusModelNode ) {
			editPart = new NonFocusNodeEditPart(figureFactory);
		} else if( iModel instanceof NavigationContainerModelNode ) {
			editPart = new NavigationContainerNodeEditPart(figureFactory);
		}  else if( iModel instanceof LabelModelNode ) {
			editPart = new LabelEditPart((LabelModelNode)iModel);
		} else  if( iModel instanceof NavigationDiagramLink ) {
			editPart = getConnectionEditPart(iModel);
		} else {
			// Here's where we get the notation manager and tell it to create an EditPart
			// for this modelObject.  So it'll come back in whatever "Notation" it desires.
//			NotationPartGenerator generator = DiagramUiPlugin.getDiagramNotationManager().getEditPartGenerator(getNotationId());
//			if( generator != null ) {
//				editPart = generator.createEditPart(iContext, iModel, diagramTypeId);
//			} else {
//				ModelerCore.Util.log( 
//					IStatus.ERROR, 
//					Util.getString(DiagramUiConstants.Errors.PART_GENERATOR_FAILURE) + " Model Object = " + iModel); //$NON-NLS-1$
//			}
		}
	        
		if (editPart != null ) {
	
			if( editPart instanceof NodeConnectionEditPart ) {
				editPart.setModel(iModel);
//				((NodeConnectionEditPart)editPart).setSourceAndTarget(iContext);
			} else 
			if( editPart instanceof NavigationNodeEditPart ){
				editPart.setModel(iModel);
			}
	            
//			if ( Util.isDebugEnabled(DebugConstants.TX_DIAGRAM_EDIT_PARTS) &&  Util.isDebugEnabled(DebugConstants.TRACE) ) { 
//				String message = ".createEditPart() = " + editPart; //$NON-NLS-1$
//				Util.debug(DebugConstants.TX_DIAGRAM_EDIT_PARTS, THIS_CLASS + message);
//			}
		} else {
			ModelerCore.Util.log( 
				IStatus.ERROR, 
				Util.getString(DiagramUiConstants.Errors.EDIT_PART_FAILURE) + " Model Object = " + iModel); //$NON-NLS-1$);
		}
	
		return editPart;
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
	 */
	public NodeConnectionEditPart getConnectionEditPart(Object iModel) {
		return new NavigationLinkEditPart();
	}
	    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getAnchorManager(com.metamatrix.modeler.diagram.ui.part.DiagramEditPart)
	 */
	public AnchorManager getAnchorManager(DiagramEditPart editPart) {
		return null; //new TransformationAnchorManager(editPart);
	}
}
