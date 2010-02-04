/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.connection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Font;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.connection.AbstractNodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.relationship.ui.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;

/**
 * DiagramAssociation
 */
public class RelationshipLink extends AbstractNodeConnectionModel {
                                                                  
//	private static final String EMPTY_LABEL = " "; //$NON-NLS-1$ 
    
	private DiagramModelNode sourceNode;
	private DiagramModelNode targetNode;

	private LabelModelNode nameLabelNode;
	private LabelModelNode roleLabelNode;
    
	private String sName;
	private String sRole;
    
	private static final int LINK_ORTHOGONAL      = 1;     
	private int ltCurrentLinkType = LINK_ORTHOGONAL;

	
	private boolean useCenterAnchorForTarget = false;
	private boolean useCenterAnchorForSource = false;
    
	private String sRouterStyle;
          
	/**
	 * Construct an instance of TransformationLink
	 * 
	 */
	public RelationshipLink(DiagramModelNode source, DiagramModelNode target ) {
		super();
		setSourceNode(source);
		setTargetNode(target);
		init();
	}
    
	/**
	 * Construct an instance of TransformationLink.
	 * 
	 */
	public RelationshipLink(DiagramModelNode source, DiagramModelNode target, String sName ) {
		super();
        
		this.sName = sName;
		setSourceNode(source);
		setTargetNode(target); 
		init();
	}
	
	/**
	 * Construct an instance of TransformationLink.
	 * 
	 */
	public RelationshipLink(DiagramModelNode source, DiagramModelNode target, String sName, String sRole) {
		super();
        
		this.sName = sName;
		this.sRole = sRole;
		setSourceNode(source);
		setTargetNode(target); 
		init();
	}
    
	private void init() {
        String roleText = getRole();
        if( roleText != null )
            roleLabelNode  = new LabelModelNode( roleText );
        String nameText = getName();
        if( roleText != null )
            nameLabelNode  = new LabelModelNode( nameText );
	}

    
	@Override
    public void updateLabels() {

	}
    

	public void setSource(DiagramModelNode iSource) {
		sourceNode = iSource;
	}

	public void setTarget(DiagramModelNode iTarget) {
		targetNode = iTarget;
	}


	@Override
    public void setName( String sName ) {
		this.sName = sName;   
	}
    
	/**
	 * @param 
	 */
	@Override
    public String getName() {
		return sName;
	}

	@Override
    public void layout() {
//		  System.out.println( "[DiagramAssociation.layout 0 arg ] TOP, node name: " + getName() );  //$NON-NLS-1$                 
	}

	@Override
    public void setRouterStyle( String sRouterStyle ) {
        
		if ( this.sRouterStyle == null || !this.sRouterStyle.equals( sRouterStyle ) ) {
        
			this.sRouterStyle = sRouterStyle;
			// refresh the label layout manager
            
		}            
	}
    
	@Override
    public int getRouterStyle() {
        return DiagramUiConstants.LinkRouter.DIRECT;
	}

    
	/**
	 * @return sourceNode
	 */
	@Override
    public Object getSourceNode() {
		return sourceNode;
	}

	/**
	 * @return targetNode
	 */
	@Override
    public Object getTargetNode() {
		return targetNode;
	}

	/**
	 * @param node
	 */
	@Override
    public void setSourceNode(Object node) {
		sourceNode = (DiagramModelNode)node;
	}

	/**
	 * @param node
	 */
	@Override
    public void setTargetNode(Object node) {
		targetNode = (DiagramModelNode)node;
	}

	/**
	 * @return
	 */
	public LabelModelNode getNameLabel() {
		return nameLabelNode;
	}

	/**
	 * @return
	 */
	public LabelModelNode getRoleLabel() {
		return roleLabelNode;
	}


	/**
	 * @param node
	 */
	public void setName(LabelModelNode node) {
		nameLabelNode = node;
	}

	/**
	 * @param node
	 */
	public void setRoleLabel(LabelModelNode node) {
		roleLabelNode = node;
	}

    
	@Override
    public List getLabelNodes() {
		List currentLabels = new ArrayList();
        if( roleLabelNode == null && getRole() != null ) {
        	roleLabelNode = new LabelModelNode(getRole());
        }
		if(roleLabelNode != null )
			currentLabels.add(roleLabelNode);
        
		if( currentLabels.isEmpty() )
			return Collections.EMPTY_LIST;
		return currentLabels;
	}

	public void clearLabelNodes() {
		roleLabelNode = null;
	}

// =====================================================================
//	Methods to support firing PropertyChangeEvents
// =====================================================================
   transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    

   @Override
public void addPropertyChangeListener(PropertyChangeListener l) {
	   listeners.addPropertyChangeListener(l);
   }

	@Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}
    
	@Override
    public void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	// Used to alert the Edit Part that the children have been modified
	// and a refreshChildren() is needed.
	public void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}
    

// =====================================================================
//	Layout code (rewritten from DCAssocation (modeler 3.1))
// =====================================================================


	public int getLinkType() {
		return ltCurrentLinkType;
	}   

	private void updateFonts( DiagramEditPart adepParentEditPart ) {
		// always run this; it may have changed since the last time layout was called...
		Font fntNew = getLabelFont();
            
		updateFontOnLabelFigure( adepParentEditPart, roleLabelNode, fntNew );                    
	}
          
	private void updateFontOnLabelFigure( DiagramEditPart adepParentEditPart,
										  LabelModelNode lmnLabel,                                 
										  Font fntNew ) {
                                              
                                              
		if( lmnLabel != null ) {
    
			DiagramEditPart dep = (DiagramEditPart)adepParentEditPart.getParent();
                                                       
			DiagramEditPart depLabelled = dep.getEditPart( lmnLabel );
            
			if( depLabelled != null ) {
				DiagramFigure df1 = depLabelled.getDiagramFigure();        
        
				if ( df1 instanceof LabeledRectangleFigure ) {
					((LabeledRectangleFigure)df1).updateForFont( fntNew );   
				}
			}
		}
                      
	}
    
	private Font getLabelFont() {
		return ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
	}


   /**
	* Creates all the labels and icons for the link.
	*/
	@Override
    public void layout( ConnectionAnchor sourceAnchor, 
						ConnectionAnchor targetAnchor,
						DiagramEditPart adepParentEditPart ) {
        
		// This is the hook to go back to model properties and get the Role and Name values
		// layout is called from refreshAssociations() in the Node edit part.

        
		if( roleLabelNode == null ) {
			// If Role was turned from false to true
			// this will update the label "name" and should fire a property change
			// and ultimately change the label figure text.
			updateLabels();
			return;
		}

		updateLabels();
                     
		// update the fonts of each label before any other calcs                    
		updateFonts( adepParentEditPart );   
        
		Point linkCenterPt = new Point(0, 0);
		
		NodeConnectionAnchor relAnchor = getRelationshipNodeAnchor(sourceAnchor, targetAnchor);
		
        if( hasFocusNode() ) {
    		FocusModelNode focusNode = getFocusNode();
    		RelationshipModelNode relNode = getRelationshipNode();
    		
    		int relPtX = relNode.getX() + relAnchor.getOffsetH();
    		int relPtY = relNode.getY() + relAnchor.getOffsetV();
    
    		linkCenterPt.x = (focusNode.getCenterX() + relPtX)/2;
    		linkCenterPt.y = (focusNode.getCenterY() + relPtY)/2;
        } else {
            int relPtX = sourceNode.getX() + relAnchor.getOffsetH();
            int relPtY = sourceNode.getY() + relAnchor.getOffsetV();
    
            linkCenterPt.x = (targetNode.getCenterX() + relPtX)/2;
            linkCenterPt.y = (targetNode.getCenterY() + relPtY)/2;
        }

		int tmpPtX = 0;
		int tmpPtY = 0;
		double startX = 0;
		double startY = 0;
		double strHeight = 0;
		double strWidth = 0;
    
		if( canShowRole() ) {
		   // LOCATE ROLE STRING
		   strHeight = roleLabelNode.getHeight();
		   strWidth = roleLabelNode.getWidth();
		   startX = linkCenterPt.x;
		   startY = linkCenterPt.y;
		   
		   tmpPtX = (int)(startX - (strWidth/2));
		   tmpPtY = (int)(startY - (strHeight/2));

		   roleLabelNode.setPosition(new Point( tmpPtX, tmpPtY ));
	   }
	}
    



	@Override
    public void placeStereotypeAndName(  int iSourceSide,   
										 int iTargetSide, 
										 PointList plConnectionPoints ) {
                                                                        
	}
    
   private boolean canShowRole() {
	   if (roleLabelNode != null && 
		   roleLabelNode.getName().length() > 0 ) {
	       return true;
	   }
       return false;
   }


	@Override
    public String toString() {            
		return new StringBuffer().append(" Relationship:") //$NON-NLS-1$
		.append(" Role = ").append(getRole()) //$NON-NLS-1$
		.append("\n Source Node = ").append(sourceNode.getName()) //$NON-NLS-1$
		.append("\n Target Node = ").append(targetNode.getName()) //$NON-NLS-1$
		.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getTargetDecoratorId() {
		return BinaryAssociation.DECORATOR_NONE;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getSourceDecoratorId() {
		return BinaryAssociation.DECORATOR_NONE;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getLineStyle()
	 */
	@Override
    public int getLineStyle() {
		return BinaryAssociation.LINE_SOLID;
	}
	
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getToolTipString()
	 */
	@Override
    public List getToolTipStrings() {
		List newList = new ArrayList(1);
//		newList.add(this.toString());
		newList.add("Relationship Link"); //$NON-NLS-1$
		newList.add("Role = " + getRole()); //$NON-NLS-1$
		newList.add("Source = " + sourceNode.getName()); //$NON-NLS-1$
		newList.add("Target = "  + targetNode.getName()); //$NON-NLS-1$
		return newList;
	}
	
	public boolean targetUsesCenterAnchor() {
		return useCenterAnchorForTarget;
	}
	
	public boolean sourceUsesCenterAnchor() {
		return useCenterAnchorForSource;
	}
	/**
	 * @param b
	 */
	public void setUseCenterAnchorForTarget(boolean use) {
		useCenterAnchorForTarget = use;
	}
	
	public void setUseCenterAnchorForSource(boolean use) {
		useCenterAnchorForSource = use;
	}

	public String getRole() {
		return sRole;
	}

	public void setRole(String role) {
		sRole = role;
	}
	
	protected FocusModelNode getFocusNode() {
		if( sourceNode instanceof FocusModelNode )
			return (FocusModelNode)sourceNode;
        if( targetNode instanceof FocusModelNode )
            return (FocusModelNode)targetNode;
		return null;
	}
    
    private boolean hasFocusNode() {
        return ( sourceNode instanceof FocusModelNode ) || ( targetNode instanceof FocusModelNode );
    }
	
	protected RelationshipModelNode getRelationshipNode() {
		if( sourceNode instanceof RelationshipModelNode )
			return (RelationshipModelNode)sourceNode;
		
		return (RelationshipModelNode)targetNode;
	}
	
//	private ChopboxAnchor getFocusNodeAnchor(ConnectionAnchor anch1, ConnectionAnchor anch2) {
//		if( anch1 instanceof ChopboxAnchor )
//			return (ChopboxAnchor)anch1;
//		
//		return (ChopboxAnchor)anch2;
//	}
	
	private NodeConnectionAnchor getRelationshipNodeAnchor(ConnectionAnchor anch1, ConnectionAnchor anch2) {
		if( anch1 instanceof NodeConnectionAnchor )
			return (NodeConnectionAnchor)anch1;
		
		return (NodeConnectionAnchor)anch2;
	}
    
}
