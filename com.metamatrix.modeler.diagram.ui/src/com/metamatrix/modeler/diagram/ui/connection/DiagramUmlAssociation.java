/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.connection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
//import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.diagram.ui.model.AssociationLabelModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramLinkAdapter;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;

/**
 * DiagramAssociation
 */
public class DiagramUmlAssociation implements NodeConnectionModel {
    private DiagramLink diagramLink;
	private BinaryAssociation bAssociation;
	private DiagramModelNode sourceNode;
	private DiagramModelNode targetNode;
    private boolean showRoles           = true;
    private boolean showMultiplicity    = true;
    private boolean showName            = true;

	private List sourceLabelNodes;
	private List targetLabelNodes;

	private LabelModelNode sourceRoleName;
	private LabelModelNode targetRoleName;
	private LabelModelNode sourceMultiplicity;
	private LabelModelNode targetMultiplicity;
	private LabelModelNode name;
	private LabelModelNode stereotype;
	private static Font currentLabelFont = ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);

	private String sName;
	//    private static final String DEFAULT_NAME_ID = "DiagramNames.DefaultAssociationName"; //$NON-NLS-1$ 

	private static final String STEREOTYPE_BEGIN = "<<"; //$NON-NLS-1$ 
	private static final String STEREOTYPE_END = ">>"; //$NON-NLS-1$ 

//	private static final int LINK_ORTHOGONAL = 1;
//	private int ltCurrentLinkType = LINK_ORTHOGONAL;

	private static final int DELTA_X = 6;
	private static final int DELTA_Y = 4;

	private static final int ABOVE_THE_LINE = 1;
	private static final int BELOW_THE_LINE = 2;
	private static final int LEFT_OF_SEGMENT = 1;
	private static final int CENTERED_ON_SEGMENT = 2;
	private static final int RIGHT_OF_SEGMENT = 3;
	private static final int CENTERED_ON_RIGHT_ENDPOINT = 4;
	private static final int CENTERED_ON_LEFT_ENDPOINT = 5;

	private String sRouterStyle;
//    private String oldRouterStyle;
    
    protected List bendpoints = new ArrayList();
	/**
	 * Construct an instance of DiagramAssociation.
	 * 
	 */
	public DiagramUmlAssociation(
		DiagramModelNode source,
		DiagramModelNode target,
		BinaryAssociation bAss) {
		super();
		bAssociation = bAss;
		//        setName( Util.getString( DEFAULT_NAME_ID ) );
		setSourceNode(source);
		setTargetNode(target);
		init();
	}

	/**
	 * Construct an instance of DiagramAssociation.
	 * 
	 */
	public DiagramUmlAssociation(
		DiagramModelNode sourceNode,
		DiagramModelNode targetNode,
		BinaryAssociation bAss,
		String sName) {
		super();

		bAssociation = bAss;
		this.sName = sName;
		setSourceNode(sourceNode);
		setTargetNode(targetNode);
		init();
	}

	private void init() {
		if (bAssociation.getRoleName(BinaryAssociation.SOURCE_END) != null) {
			sourceRoleName =
				new AssociationLabelModelNode(
					bAssociation.getRoleName(BinaryAssociation.SOURCE_END),
					AssociationLabelModelNode.SOURCE_ROLE_NAME);
		}

		if (bAssociation.getRoleName(BinaryAssociation.TARGET_END) != null) {
			targetRoleName =
				new AssociationLabelModelNode(
					bAssociation.getRoleName(BinaryAssociation.TARGET_END),
					AssociationLabelModelNode.TARGET_ROLE_NAME);
		}

		if (bAssociation.getMultiplicity(BinaryAssociation.SOURCE_END) != null) {
			sourceMultiplicity =
				new AssociationLabelModelNode(
					bAssociation.getMultiplicity(BinaryAssociation.SOURCE_END),
					AssociationLabelModelNode.SOURCE_MULTIPLICITY);
		}
		if (bAssociation.getMultiplicity(BinaryAssociation.TARGET_END) != null) {
			targetMultiplicity =
				new AssociationLabelModelNode(
					bAssociation.getMultiplicity(BinaryAssociation.TARGET_END),
					AssociationLabelModelNode.TARGET_MULTIPLICITY);
		}

		if (sName != null)
			name = new AssociationLabelModelNode(sName, AssociationLabelModelNode.NAME);

		//        stereotype = new AssociationLabelModelNode( getFormattedStereoType(), 
		//                                                    AssociationLabelModelNode.STEREOTYPE ); 
		//		bAssociation.getEndCount();
		//getUmlAspect().getNavigability()
		createAdditionalLabelNodes();

        // look for DiagramLink in diagram
        Diagram diagram = getDiagram();
        if( diagram != null ) {
            DiagramLink dLink = DiagramUiUtilities.findDiagramLink(diagram, getModelObject());
            // If exists, retrieve bendpoints.
            if( dLink != null ) {
                List positions = DiagramLinkAdapter.getBendpoints(dLink);
                createBendpoints(positions);
            }
        }
	}
	private void createBendpoints(List positions) {
        int nPts = positions.size();
        
        for( int i=0; i<nPts; i++ ) {
            AbsoluteBendpoint ab = new AbsoluteBendpoint((Point)positions.get(i));
            getBendpoints().add(i, ab);
        }
    }
    
	protected void createAdditionalLabelNodes() {
		// add source properties first
		
		String[] sourceStrings = bAssociation.getProperties(BinaryAssociation.SOURCE_END);
		if( sourceStrings != null && sourceStrings.length > 0 ) {
			sourceLabelNodes = new ArrayList(sourceStrings.length);
			for(int i=0; i<sourceStrings.length; i++ ) {
				LabelModelNode newLabel =
					new AssociationLabelModelNode(sourceStrings[i], AssociationLabelModelNode.GENERAL);
				sourceLabelNodes.add(newLabel);
			}
		}
		
		String[] targetStrings = bAssociation.getProperties(BinaryAssociation.TARGET_END);
		if( targetStrings != null && targetStrings.length > 0 ) {
			targetLabelNodes = new ArrayList(targetStrings.length);
			for(int i=0; i<targetStrings.length; i++ ) {
				LabelModelNode newLabel =
					new AssociationLabelModelNode(targetStrings[i], AssociationLabelModelNode.GENERAL);
				targetLabelNodes.add(newLabel);
			}
		}
	}

	public void updateLabels() {
		if (sourceRoleName != null)
			sourceRoleName.setName(bAssociation.getRoleName(BinaryAssociation.SOURCE_END));
		if (targetRoleName != null)
			targetRoleName.setName(bAssociation.getRoleName(BinaryAssociation.TARGET_END));
		if (sourceMultiplicity != null)
			sourceMultiplicity.setName(bAssociation.getMultiplicity(BinaryAssociation.SOURCE_END));
		if (targetMultiplicity != null)
			targetMultiplicity.setName(bAssociation.getMultiplicity(BinaryAssociation.TARGET_END));

		// BMLtodo: how do we handle updates including adding new nodes???
		
		if (sourceLabelNodes != null && !sourceLabelNodes.isEmpty()) {
//			String[] sourceStrings = bAssociation.getProperties(BinaryAssociation.SOURCE_END);
//			if( sourceStrings != null && sourceStrings.length > 0 ) {
//				sourceLabelNodes = new ArrayList(sourceStrings.length);
//				for(int i=0; i<sourceStrings.length; i++ ) {
//					LabelModelNode newLabel =
//						new AssociationLabelModelNode(sourceStrings[i], AssociationLabelModelNode.GENERAL);
//					sourceLabelNodes.add(newLabel);
//				}
//			}
			//        	Iterator iter = sourceLabelNodes.iterator();
			//        	LabelModelNode nextNode = null;
			//        	while( iter.hasNext() ) {
			//        		nextNode = (LabelModelNode)iter.next();
			//        		nextNode.setName();
			//        	}
		}

		if (targetLabelNodes != null && !targetLabelNodes.isEmpty()) {
//			String[] targetStrings = bAssociation.getProperties(BinaryAssociation.TARGET_END);
//			if( targetStrings != null && targetStrings.length > 0 ) {
//				targetLabelNodes = new ArrayList(targetStrings.length);
//				for(int i=0; i<targetStrings.length; i++ ) {
//					LabelModelNode newLabel =
//						new AssociationLabelModelNode(targetStrings[i], AssociationLabelModelNode.GENERAL);
//					targetLabelNodes.add(newLabel);
//				}
//			}
			//			Iterator iter = targetLabelNodes();
			//			LabelModelNode nextNode = null;
			//			while( iter.hasNext() ) {
			//				nextNode = (LabelModelNode)iter.next();
			//				nextNode.setName();
			//			}
		}
	}

	public void setName(String sName) {
		this.sName = sName;
	}

	/**
	 * @param 
	 */
	public String getName() {
		return sName;
	}

	public void layout() {
		//        System.out.println( "[DiagramAssociation.layout 0 arg ] TOP, node name: " + getName() );  //$NON-NLS-1$                 
	}
    
    public boolean routerStyleChanged() {
//        if( sRouterStyle != null ) {
//            if( oldRouterStyle == null )
//                return true;
//            else if( !oldRouterStyle.equals(sRouterStyle))
//                return true;
//        } else {
//            if( oldRouterStyle != null )
//                return true;
//        }
        
        return false;
    }
    
    public boolean changedRouterFromOtoM(String oldStyle, String newStyle) {
        if( oldStyle != null && newStyle != null ) {
            if( newStyle.equalsIgnoreCase(DiagramLinkType.MANUAL_LITERAL.getName()) &&
                oldStyle.equalsIgnoreCase(DiagramLinkType.ORTHOGONAL_LITERAL.getName()) ) {
                return true;
            }
        }
        
        return false;
    }

	public void setRouterStyle(String newStyle) {

		if (this.sRouterStyle == null || !this.sRouterStyle.equals(newStyle)) {
            
			this.sRouterStyle = newStyle;
			// refresh the label layout manager
//            if( getDiagramLink() != null )
//                DiagramLinkAdapter.setType(getDiagramLink(), DiagramLinkType.get(sRouterStyle).getValue());
		}
	}
    

    public void setRouterStyle(int iRouterStyle) {
//        // refresh the label layout manager
//        if( getDiagramLink() != null )
//            DiagramLinkAdapter.setType(getDiagramLink(), iRouterStyle);
//        else {
//            setDiagramLink(DiagramUiUtilities.createDiagramLink(getModelObject(), getDiagram(), iRouterStyle));
//        }
            
    }

	public int getRouterStyle() {
//        if( getDiagramLink() != null ) {
//            return DiagramLinkAdapter.getType(getDiagramLink());
//        }
        
        return DiagramEditorUtil.getCurrentDiagramRouterStyle();
//		return DiagramUiUtilities.getCurrentRouterStyleID(sRouterStyle);
	}

	public String getFormattedStereoType() {
		return STEREOTYPE_BEGIN + bAssociation.getStereotype() + STEREOTYPE_END;
	}

	/**
	 * @return sourceNode
	 */
	public Object getSourceNode() {
		return sourceNode;
	}

	/**
	 * @return targetNode
	 */
	public Object getTargetNode() {
		return targetNode;
	}

	/**
	 * @param node
	 */
	public void setSourceNode(Object node) {
		sourceNode = (DiagramModelNode)node;
	}

	/**
	 * @param node
	 */
	public void setTargetNode(Object node) {
		targetNode = (DiagramModelNode)node;
	}

	/**
	 * @return
	 */
	public LabelModelNode getNameLabel() {
		return name;
	}

	/**
	 * @return
	 */
	public LabelModelNode getSourceMultiplicityLabel() {
		return sourceMultiplicity;
	}

	/**
	 * @return
	 */
	public LabelModelNode getSourceRoleNameLabel() {
		return sourceRoleName;
	}

	/**
	 * @return
	 */
	public LabelModelNode getStereotypeLabel() {
		return stereotype;
	}

	/**
	 * @return
	 */
	public LabelModelNode getTargetMultiplicityLabel() {
		return targetMultiplicity;
	}

	/**
	 * @return
	 */
	public LabelModelNode getTargetRoleNameLabel() {
		return targetRoleName;
	}

	/**
	 * @param node
	 */
	public void setName(LabelModelNode node) {
		name = node;
	}

	/**
	 * @param node
	 */
	public void setSourceMultiplicity(LabelModelNode node) {
		sourceMultiplicity = node;
	}

	/**
	 * @param node
	 */
	public void setSourceRoleName(LabelModelNode node) {
		sourceRoleName = node;
	}

	/**
	 * @param node
	 */
	public void setStereotype(LabelModelNode node) {
		stereotype = node;
	}

	/**
	 * @param node
	 */
	public void setTargetMultiplicity(LabelModelNode node) {
		targetMultiplicity = node;
	}

	/**
	 * @param node
	 */
	public void setTargetRoleName(LabelModelNode node) {
		targetRoleName = node;
	}

	public List getLabelNodes() {
		List currentLabels = new ArrayList();

		if (sourceRoleName != null && showRoles)
			currentLabels.add(sourceRoleName);
		if (targetRoleName != null && showRoles)
			currentLabels.add(targetRoleName);
		if (sourceMultiplicity != null && showMultiplicity)
			currentLabels.add(sourceMultiplicity);
		if (targetMultiplicity != null && showMultiplicity)
			currentLabels.add(targetMultiplicity);
		if (name != null && showName )
			currentLabels.add(name);
		if (stereotype != null)
			currentLabels.add(stereotype);

		if (sourceLabelNodes != null && !sourceLabelNodes.isEmpty())
			currentLabels.addAll(sourceLabelNodes);

		if (targetLabelNodes != null && !targetLabelNodes.isEmpty())
			currentLabels.addAll(targetLabelNodes);

		if (currentLabels.isEmpty())
			return Collections.EMPTY_LIST;
		return currentLabels;
	}

	// =====================================================================
	//  Methods to support firing PropertyChangeEvents
	// =====================================================================
	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	public void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	// Used to alert the Edit Part that the children have been modified
	// and a refreshChildren() is needed.
	public void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}

	// =====================================================================
	//  Layout code (rewritten from DCAssocation (modeler 3.1))
	// =====================================================================

	public int getLinkType() {
		return DiagramEditorUtil.getCurrentDiagramRouterStyle();
	}

	private void updateFonts(DiagramEditPart adepParentEditPart) {
		// always run this; it may have changed since the last time layout was called...
		Font newFont = ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
		
		if( ScaledFontManager.fontsAreDifferent(newFont, currentLabelFont) ) {
			currentLabelFont = newFont;
			updateFontOnLabelFigure(adepParentEditPart, sourceRoleName, currentLabelFont);
			updateFontOnLabelFigure(adepParentEditPart, targetRoleName, currentLabelFont);
			updateFontOnLabelFigure(adepParentEditPart, sourceMultiplicity, currentLabelFont);
			updateFontOnLabelFigure(adepParentEditPart, targetMultiplicity, currentLabelFont);
			updateFontOnLabelFigure(adepParentEditPart, name, currentLabelFont);
			updateFontOnLabelFigure(adepParentEditPart, stereotype, currentLabelFont);
			if (sourceLabelNodes != null && !sourceLabelNodes.isEmpty()) {
				Iterator iter = sourceLabelNodes.iterator();
				LabelModelNode nextNode = null;
				while (iter.hasNext()) {
					nextNode = (LabelModelNode)iter.next();
					updateFontOnLabelFigure(adepParentEditPart, nextNode, currentLabelFont);
				}
			}
			if (targetLabelNodes != null && !targetLabelNodes.isEmpty()) {
				Iterator iter = targetLabelNodes.iterator();
				LabelModelNode nextNode = null;
				while (iter.hasNext()) {
					nextNode = (LabelModelNode)iter.next();
					updateFontOnLabelFigure(adepParentEditPart, nextNode, currentLabelFont);
				}
			}
		}
	}

	private void updateFontOnLabelFigure(
		DiagramEditPart adepParentEditPart,
		LabelModelNode lmnLabel,
		Font fntNew) {

		DiagramEditPart dep = (DiagramEditPart)adepParentEditPart.getParent();

		if (dep != null) {
			DiagramEditPart depLabelled = dep.getEditPart(lmnLabel);
			if (depLabelled != null) {
				DiagramFigure df1 = depLabelled.getDiagramFigure();

				if (df1 != null && df1 instanceof LabeledRectangleFigure) {
					((LabeledRectangleFigure)df1).updateForFont(fntNew);
					depLabelled.updateModelSize();
				}
			}
		}

	}

//	private Font getLabelFont() {
//		return ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
//		int iCurrGeneralFontSize = ScaledFontManager.getSize();
//
//		//        System.out.println(" -->> [DiagramAssociation.getLabelFont()] ScaledFontManager  " + DiagramUiUtilities.getFontString( ScaledFontManager.getFont())); //$NON-NLS-1$
//		int iNewLabelFontSize = 0;

//		if (ScaledFontManager.canDecrease(iCurrGeneralFontSize - 2)) {
//			// we can decrease by 3
//			iNewLabelFontSize = iCurrGeneralFontSize - 3;
//		} else 
//		if (ScaledFontManager.canDecrease(iCurrGeneralFontSize - 1)) {
//			// we can decrease by 2
//			iNewLabelFontSize = iCurrGeneralFontSize - 2;
//		} else 
//		if (ScaledFontManager.canDecrease(iCurrGeneralFontSize)) {
//			// we can only decrease by 1
//			iNewLabelFontSize = iCurrGeneralFontSize - 1;
//		} else {
//			// no room to decrease at all
//			iNewLabelFontSize = iCurrGeneralFontSize;
//		}

		// construct the new font
//		Font fnt = 
//			new Font(
//				null,
//				ScaledFontManager.getName(),
//				iNewLabelFontSize,
//				ScaledFontManager.getStyle());
//
//		return fnt;
//	}

	/**
	 * Creates all the labels and icons for the link.
	 */
	public void layout(
		ConnectionAnchor sourceAnchor,
		ConnectionAnchor targetAnchor,
		DiagramEditPart adepParentEditPart) {

		// Check here!!! because we don't want to process generic connection anchors...
		if (!(sourceAnchor instanceof NodeConnectionAnchor)
			|| !(targetAnchor instanceof NodeConnectionAnchor))
			return;

		// update the fonts of each label before any other calcs                    
		updateFonts(adepParentEditPart);

		NodeConnectionAnchor ncaSourceAnchor = (NodeConnectionAnchor)sourceAnchor;
		NodeConnectionAnchor ncaTargetAnchor = (NodeConnectionAnchor)targetAnchor;

		Point pSourceAnchor = null;
		Point pTargetAnchor = null;

		int sourceX = ncaSourceAnchor.getOffsetH() + ((DiagramModelNode)getSourceNode()).getX();
		int sourceY = ncaSourceAnchor.getOffsetV() + ((DiagramModelNode)getSourceNode()).getY();

		int targetX = ncaTargetAnchor.getOffsetH() + ((DiagramModelNode)getTargetNode()).getX();
		int targetY = ncaTargetAnchor.getOffsetV() + ((DiagramModelNode)getTargetNode()).getY();

		pSourceAnchor = new Point(sourceX, sourceY);
		pTargetAnchor = new Point(targetX, targetY);
		double deltaX = DELTA_X;
		double deltaY = DELTA_Y;

		int sourceSide = 0;
		int targetSide = 0;

		// Get End Points (Intersections)
		Point sourceStartPt = pSourceAnchor;
		Point targetEndPt = pTargetAnchor;
		sourceSide = ncaSourceAnchor.getDirection();
		targetSide = ncaTargetAnchor.getDirection();

		double sourceAngleInRadians = getSourceAngleInRadians(sourceSide);
		double targetAngleInRadians = getTargetAngleInRadians(targetSide);

		int tmpPtX = 0;
		int tmpPtY = 0;
		double startX = 0;
		double startY = 0;
		double strHeight = 0;
		double strWidth = 0;
		double tmpAngle = 0;

		if (canShowSourceMultiplicity() ) {
			// LOCATE SOURCE MULTIPLICITY STRING
			strHeight = sourceMultiplicity.getHeight();
			strWidth = sourceMultiplicity.getWidth();
			startX = sourceStartPt.x;
			startY = sourceStartPt.y;
			if (getLinkType() == DiagramUiConstants.LinkRouter.ORTHOGONAL) {
				tmpAngle = Math.toDegrees(sourceAngleInRadians);
			} else
				tmpAngle = 360.0 - Math.toDegrees(sourceAngleInRadians);

			switch (sourceSide) {
				case AnchorManager.NORTH : {
					if (tmpAngle >= 60 && tmpAngle <= 120) {
						// Baseline.  Place at upper left of intersection.
						tmpPtX = (int) (startX - deltaX - strWidth);
						tmpPtY = (int) (startY - deltaY - strHeight);
					} else if (tmpAngle < 60) {
						// Baseline.  Place at X of intersection left justified.
						tmpPtX = (int) (startX - strWidth);
						tmpPtY = (int) (startY - deltaY - strHeight);
					} else {
						// Baseline.  Place at X of intersection right justified.
						tmpPtX = (int) (startX);
						tmpPtY = (int) (startY - deltaY - strHeight);
					}
				} break;

				case AnchorManager.SOUTH : {
					if (tmpAngle >= 240 && tmpAngle <= 300) {
						// Baseline.  Place at lower left of intersection.
						tmpPtX = (int) (startX - deltaX - strWidth);
						tmpPtY = (int) (startY + deltaY);
					} else if (tmpAngle < 240) {
						// Baseline.  Place at X of intersection left right justified.
						tmpPtX = (int) (startX - strWidth);
						tmpPtY = (int) (startY + deltaY);
					} else {
						// Baseline.  Place at X of intersection right justified.
						tmpPtX = (int) (startX);
						tmpPtY = (int) (startY + deltaY);
					}
				} break;

				case AnchorManager.EAST : {
					if (tmpAngle <= 20 || tmpAngle >= 340) {
						// Baseline.  Place at upper right of intersection.
						tmpPtX = (int) (startX + deltaX);
						tmpPtY = (int) (startY - deltaY - strHeight);
					} else if (tmpAngle > 20 && tmpAngle <= 91) {
						// Baseline.  Place at Y of intersection left justified.
						tmpPtX = (int) (startX + deltaX);
						tmpPtY = (int) (startY);
					} else {
						// Baseline.  Place at Y of intersection right justified.
						tmpPtX = (int) (startX + deltaX);
						tmpPtY = (int) (startY - strHeight);
					}
				}
				break;

				case AnchorManager.WEST : {
					if (tmpAngle <= 200 && tmpAngle >= 160) {
						// Baseline.  Place at upper left of intersection.
						tmpPtX = (int) (startX - deltaX - strWidth);
						tmpPtY = (int) (startY - deltaY - strHeight);
					} else if (tmpAngle > 89 && tmpAngle < 160) {
						// Baseline.  Place at Y of intersection left justified.
						tmpPtX = (int) (startX - deltaX - strWidth);
						tmpPtY = (int) (startY - strHeight);
					} else {
						// Baseline.  Place at Y of intersection right justified.
						tmpPtX = (int) (startX - deltaX - strWidth);
						tmpPtY = (int) (startY - strHeight);
					}
				} break;

				default :
					break;
			}
//			sourceMultiplicity.setPosition(new Point(tmpPtX, tmpPtY));
			
			switch (sourceSide) {
				case AnchorManager.NORTH: {
				
					if (sourceLabelNodes != null && !sourceLabelNodes.isEmpty()) {
						Iterator iter = sourceLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX + sourceMultiplicity.getWidth() - nextNode.getWidth(), tmpPtY));
							tmpPtY -= nextNode.getHeight();
						}
					}
				} break;
				
				case AnchorManager.SOUTH: {
				
					if (sourceLabelNodes != null && !sourceLabelNodes.isEmpty()) {
						Iterator iter = sourceLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX + sourceMultiplicity.getWidth() - nextNode.getWidth(), tmpPtY));
							tmpPtY += nextNode.getHeight();
						}
					}
				} break;
				
				case AnchorManager.WEST: {
				
					if (sourceLabelNodes != null && !sourceLabelNodes.isEmpty()) {
						Iterator iter = sourceLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX - nextNode.getWidth() + sourceMultiplicity.getWidth(), tmpPtY));
							tmpPtY -= nextNode.getHeight();
						}
					}
				} break;
				
				case AnchorManager.EAST: {
				
					if (sourceLabelNodes != null && !sourceLabelNodes.isEmpty()) {
						Iterator iter = sourceLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX, tmpPtY));
							tmpPtY -= nextNode.getHeight();
						}
					}
				} break;
			}

            sourceMultiplicity.setPosition(new Point(tmpPtX, tmpPtY));
        }
        if (canShowTargetMultiplicity() ) {
			// LOCATE TARGET MULTIPLICITY STRING
			strHeight = targetMultiplicity.getHeight();
			strWidth = targetMultiplicity.getWidth();
			startX = targetEndPt.x;
			startY = targetEndPt.y;
			if (getLinkType() == DiagramUiConstants.LinkRouter.ORTHOGONAL) {
				tmpAngle = Math.toDegrees(targetAngleInRadians + Math.PI);
			} else
				tmpAngle = 360.0 - Math.toDegrees(targetAngleInRadians + Math.PI);

			if (tmpAngle < 0)
				tmpAngle += 360;
			else if (tmpAngle > 360)
				tmpAngle -= 360;

			switch (targetSide) {
				case AnchorManager.NORTH :
					{
						if (tmpAngle >= 60 && tmpAngle <= 120) {
							// Baseline.  Place at upper left of intersection.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY - deltaY - strHeight);
						} else if (tmpAngle < 60) {
							// Baseline.  Place at X of intersection left justified.
							tmpPtX = (int) (startX - strWidth);
							tmpPtY = (int) (startY - deltaY - strHeight);
						} else {
							// Baseline.  Place at X of intersection right justified.
							tmpPtX = (int) (startX);
							tmpPtY = (int) (startY - deltaY - strHeight);
						}
					}
					break;

				case AnchorManager.SOUTH :
					{
						if (tmpAngle >= 240 && tmpAngle <= 300) {
							// Baseline.  Place at lower left of intersection.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY + deltaY);
						} else if (tmpAngle < 240) {
							// Baseline.  Place at X of intersection left right justified.
							tmpPtX = (int) (startX - strWidth);
							tmpPtY = (int) (startY + deltaY);
						} else {
							// Baseline.  Place at X of intersection right justified.
							tmpPtX = (int) (startX);
							tmpPtY = (int) (startY + deltaY);
						}
					}
					break;

				case AnchorManager.EAST :
					{
						if (tmpAngle <= 20 || tmpAngle >= 340) {
							// Baseline.  Place at upper right of intersection.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY - deltaY - strHeight);
						} else if (tmpAngle > 20 && tmpAngle <= 91) {
							// Baseline.  Place at Y of intersection left justified.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY);
						} else {
							// Baseline.  Place at Y of intersection right justified.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY - strHeight);
						}
					}
					break;

				case AnchorManager.WEST :
					{
						if (tmpAngle <= 200 && tmpAngle >= 160) {
							// Baseline.  Place at upper left of intersection.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY - deltaY - strHeight);
						} else if (tmpAngle > 89 && tmpAngle < 160) {
							// Baseline.  Place at Y of intersection left justified.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY);
						} else {
							// Baseline.  Place at Y of intersection right justified.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY - strHeight);
						}
					}
					break;

				default :
					break;
			}
			
			switch (targetSide) {
				case AnchorManager.NORTH: {
				
					if (targetLabelNodes != null && !targetLabelNodes.isEmpty()) {
						Iterator iter = targetLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX + targetMultiplicity.getWidth() - nextNode.getWidth(), tmpPtY));
							tmpPtY -= nextNode.getHeight();
						}
					}
				} break;
				
				case AnchorManager.SOUTH: {
				
					if (targetLabelNodes != null && !targetLabelNodes.isEmpty()) {
						Iterator iter = targetLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX + targetMultiplicity.getWidth() - nextNode.getWidth(), tmpPtY));
							tmpPtY += nextNode.getHeight();
						}
					}
				} break;
				
				case AnchorManager.WEST: {
				
					if (targetLabelNodes != null && !targetLabelNodes.isEmpty()) {
						Iterator iter = targetLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX - nextNode.getWidth() + targetMultiplicity.getWidth(), tmpPtY));
							tmpPtY -= nextNode.getHeight();
						}
					}
				} break;
				
				case AnchorManager.EAST: {
				
					if (targetLabelNodes != null && !targetLabelNodes.isEmpty()) {
						Iterator iter = targetLabelNodes.iterator();
						LabelModelNode nextNode = null;
						while( iter.hasNext() ) {
							nextNode = (LabelModelNode)iter.next();
							nextNode.setPosition(new Point(tmpPtX, tmpPtY));
							tmpPtY -= nextNode.getHeight();
						}
					}
				} break;
			}
			targetMultiplicity.setPosition(new Point(tmpPtX, tmpPtY));
		}

		if (canShowSourceRole() ) {
			// LOCATE SOURCE ROLE STRING
			strHeight = sourceRoleName.getHeight();
			strWidth = sourceRoleName.getWidth();
			startX = sourceStartPt.x;
			startY = sourceStartPt.y;
			if (getLinkType() == DiagramUiConstants.LinkRouter.ORTHOGONAL) {
				tmpAngle = Math.toDegrees(sourceAngleInRadians);
			} else
				tmpAngle = 360.0 - Math.toDegrees(sourceAngleInRadians);

			switch (sourceSide) {
				case AnchorManager.NORTH :
					{
						if (tmpAngle >= 60 && tmpAngle <= 120) {
							// Baseline.  Place at upper left of intersection.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY - deltaY - strHeight);
						} else if (tmpAngle < 60) {
							// Baseline.  Place at X of intersection left justified.
							tmpPtX = (int) (startX - strWidth);
							tmpPtY = (int) (startY - deltaY - strHeight * 2);
						} else {
							// Baseline.  Place at X of intersection right justified.
							tmpPtX = (int) (startX);
							tmpPtY = (int) (startY - deltaY - strHeight * 2);
						}
					}
					break;

				case AnchorManager.SOUTH :
					{
						if (tmpAngle >= 240 && tmpAngle <= 300) {
							// Baseline.  Place at lower left of intersection.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY + deltaY);
						} else if (tmpAngle < 240) {
							// Baseline.  Place at X of intersection left right justified.
							tmpPtX = (int) (startX);
							tmpPtY = (int) (startY + deltaY + strHeight);
						} else {
							// Baseline.  Place at X of intersection right justified.
							tmpPtX = (int) (startX - strWidth);
							tmpPtY = (int) (startY + deltaY + strHeight);
						}
					}
					break;

				case AnchorManager.EAST :
					{
						if (tmpAngle <= 20 || tmpAngle >= 340) {
							// Baseline.  Place at upper right of intersection.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY + deltaY);
						} else if (tmpAngle > 20 && tmpAngle <= 91) {
							// Baseline.  Place at Y of intersection left justified.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY + strHeight);
						} else {
							// Baseline.  Place at Y of intersection right justified.
							tmpPtX = (int) (startX + deltaX);
							tmpPtY = (int) (startY - strHeight * 2);
						}
					}
					break;

				case AnchorManager.WEST :
					{
						if (tmpAngle <= 200 && tmpAngle >= 160) {
							// Baseline.  Place at upper left of intersection.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY + deltaY);
						} else if (tmpAngle > 89 && tmpAngle < 160) {
							// Baseline.  Place at Y of intersection left justified.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY + strHeight);
						} else {
							// Baseline.  Place at Y of intersection right justified.
							tmpPtX = (int) (startX - deltaX - strWidth);
							tmpPtY = (int) (startY - strHeight * 2);
						}
					}
					break;

				default :
					break;
			}

			sourceRoleName.setPosition(new Point(tmpPtX, tmpPtY));

		}
        if( canShowTargetRole() ) {

            // LOCATE TARGET ROLE STRING
            strHeight = targetRoleName.getHeight();
            strWidth = targetRoleName.getWidth();
            startX = targetEndPt.x;
            startY = targetEndPt.y;
            if (getLinkType() == DiagramUiConstants.LinkRouter.ORTHOGONAL) {
                tmpAngle = Math.toDegrees(targetAngleInRadians + Math.PI);
            } else
                tmpAngle = 360.0 - Math.toDegrees(targetAngleInRadians + Math.PI);
            if (tmpAngle < 0)
                tmpAngle += 360;
            else if (tmpAngle > 360)
                tmpAngle -= 360;

            switch (targetSide) {
                case AnchorManager.NORTH :
                    {
                        if (tmpAngle >= 60 && tmpAngle <= 120) {
                            // Baseline.  Place at upper left of intersection.
                            tmpPtX = (int) (startX + deltaX);
                            tmpPtY = (int) (startY - deltaY - strHeight);
                        } else if (tmpAngle < 60) {
                            // Baseline.  Place at X of intersection left justified.
                            tmpPtX = (int) (startX - strWidth);
                            tmpPtY = (int) (startY - deltaY - strHeight * 2);
                        } else {
                            // Baseline.  Place at X of intersection right justified.
                            tmpPtX = (int) (startX);
                            tmpPtY = (int) (startY - deltaY - strHeight * 2);
                        }
                    }
                    break;

                case AnchorManager.SOUTH :
                    {
                        if (tmpAngle >= 240 && tmpAngle <= 300) {
                            // Baseline.  Place at lower left of intersection.
                            tmpPtX = (int) (startX + deltaX);
                            tmpPtY = (int) (startY + deltaY);
                        } else if (tmpAngle < 240) {
                            // Baseline.  Place at X of intersection left right justified.
                            tmpPtX = (int) (startX);
                            tmpPtY = (int) (startY + deltaY + strHeight);
                        } else {
                            // Baseline.  Place at X of intersection right justified.
                            tmpPtX = (int) (startX - strWidth);
                            tmpPtY = (int) (startY + deltaY + strHeight);
                        }
                    }
                    break;

                case AnchorManager.EAST :
                    {
                        if (tmpAngle <= 20 || tmpAngle >= 340) {
                            // Baseline.  Place at upper right of intersection.
                            tmpPtX = (int) (startX + deltaX);
                            tmpPtY = (int) (startY + deltaY);
                        } else if (tmpAngle > 20 && tmpAngle <= 91) {
                            // Baseline.  Place at Y of intersection left justified.
                            tmpPtX = (int) (startX + deltaX);
                            tmpPtY = (int) (startY + strHeight);
                        } else {
                            // Baseline.  Place at Y of intersection right justified.
                            tmpPtX = (int) (startX + deltaX);
                            tmpPtY = (int) (startY - strHeight * 2);
                        }
                    }
                    break;

                case AnchorManager.WEST :
                    {
                        if (tmpAngle <= 200 && tmpAngle >= 160) {
                            // Baseline.  Place at upper left of intersection.
                            tmpPtX = (int) (startX - deltaX - strWidth);
                            tmpPtY = (int) (startY + deltaY);
                        } else if (tmpAngle > 89 && tmpAngle < 160) {
                            // Baseline.  Place at Y of intersection left justified.
                            tmpPtX = (int) (startX - deltaX - strWidth);
                            tmpPtY = (int) (startY + strHeight);
                        } else {
                            // Baseline.  Place at Y of intersection right justified.
                            tmpPtX = (int) (startX - deltaX - strWidth);
                            tmpPtY = (int) (startY - strHeight * 2);
                        }
                    }
                    break;

                default :
                    break;
            }
            targetRoleName.setPosition(new Point(tmpPtX, tmpPtY));
        }

	}

	public void placeStereotypeAndName(
		int iSourceSide,
		int iTargetSide,
		PointList plConnectionPoints) {
		// jh note: the router style info will replace the 'LINK_ORTHOGONAL' constant.

		if (!canShowStereotype() && !canShowName()) {
			return;
		}

		int iPointsInConnection = plConnectionPoints.size();
		Point ptFirstSegPoint = null;
		Point ptLastSegPoint = null;

		//System.out.println("[DiagramAssociation.placeStereotypeAndName] TOP: plConnectionPoints, first point: " + plConnectionPoints.getFirstPoint() ); //$NON-NLS-1$
		//System.out.println("[DiagramAssociation.placeStereotypeAndName] TOP: plConnectionPoints, mid point: " + plConnectionPoints.getMidpoint() ); //$NON-NLS-1$
		//System.out.println("[DiagramAssociation.placeStereotypeAndName] TOP: plConnectionPoints, last point: " + plConnectionPoints.getLastPoint() ); //$NON-NLS-1$
		switch (iPointsInConnection) {
			case 2 :
				{
					//System.out.println("[DiagramAssociation.placeStereotypeAndName] Handling case: 2 points " ); //$NON-NLS-1$
					// if vertical (North/South), and straight (2 points), requires special handling:
					if (isVerticalConnection(iSourceSide, iTargetSide)) {
						Point ptMidPoint = plConnectionPoints.getMidpoint();

						// calc position for stereotype
						int iStereotypeX = ptMidPoint.x + DELTA_X;
						int iStereotypeY = ptMidPoint.y;

						// calc position for name
						int iNameX = iStereotypeX;
						int iNameY = iStereotypeY + name.getHeight() + 7;
						if (name != null)
							name.setPosition(new Point(iNameX, iNameY));
						if (stereotype != null)
							stereotype.setPosition(new Point(iStereotypeX, iStereotypeY));

					} else {
						// must be horizontal, so handle in the standard way.  The segment to attach
						//  the label to is the whole connection, in the 2 point case.
						ptFirstSegPoint = plConnectionPoints.getFirstPoint();
						ptLastSegPoint = plConnectionPoints.getMidpoint();

						if (name != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								name,
								ABOVE_THE_LINE,
								CENTERED_ON_SEGMENT);
						if (stereotype != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								stereotype,
								BELOW_THE_LINE,
								CENTERED_ON_SEGMENT);
					}
				}
				break;

			case 3 :
				{
					//System.out.println("[DiagramAssociation.placeStereotypeAndName] Handling case: 3 points " ); //$NON-NLS-1$
					// determine the line segment to use: 
					//       for 3, the rule is: "find the 2 points that have the same Y but different Xs"

					int iLeftCenterRightPosition = 0;

					if (plConnectionPoints.getFirstPoint().y
						== plConnectionPoints.getMidpoint().y) {
						if (plConnectionPoints.getFirstPoint().x
							< plConnectionPoints.getMidpoint().x) {
							ptFirstSegPoint = plConnectionPoints.getFirstPoint();
							ptLastSegPoint = plConnectionPoints.getMidpoint();
							iLeftCenterRightPosition = RIGHT_OF_SEGMENT;
						} else if (
							plConnectionPoints.getMidpoint().x
								< plConnectionPoints.getFirstPoint().x) {
							ptFirstSegPoint = plConnectionPoints.getMidpoint();
							ptLastSegPoint = plConnectionPoints.getFirstPoint();
							iLeftCenterRightPosition = LEFT_OF_SEGMENT;
						}
						if (name != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								name,
								ABOVE_THE_LINE,
								iLeftCenterRightPosition);
						if (stereotype != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								stereotype,
								BELOW_THE_LINE,
								iLeftCenterRightPosition);
					} else if (
						plConnectionPoints.getLastPoint().y
							== plConnectionPoints.getMidpoint().y) {
						if (plConnectionPoints.getLastPoint().x
							< plConnectionPoints.getMidpoint().x) {
							ptFirstSegPoint = plConnectionPoints.getLastPoint();
							ptLastSegPoint = plConnectionPoints.getMidpoint();
							iLeftCenterRightPosition = RIGHT_OF_SEGMENT;
						} else if (
							plConnectionPoints.getMidpoint().x
								< plConnectionPoints.getLastPoint().x) {
							ptFirstSegPoint = plConnectionPoints.getMidpoint();
							ptLastSegPoint = plConnectionPoints.getLastPoint();
							iLeftCenterRightPosition = LEFT_OF_SEGMENT;
						}
						if (name != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								name,
								ABOVE_THE_LINE,
								iLeftCenterRightPosition);
						if (stereotype != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								stereotype,
								BELOW_THE_LINE,
								iLeftCenterRightPosition);
					}
				}
				break;

			case 4 :
				{
					//System.out.println("[DiagramAssociation.placeStereotypeAndName] Handling case: 4 points " ); //$NON-NLS-1$
					// if vertical (North/South), and bent (4 points), segment to use will be
					//   the 2nd --- 3rd points in the collection
					if (isVerticalConnection(iSourceSide, iTargetSide)) {
						ptFirstSegPoint = plConnectionPoints.getPoint(1);
						ptLastSegPoint = plConnectionPoints.getPoint(2);

						setNewPositionForLabelOnSegment(
							ptFirstSegPoint,
							ptLastSegPoint,
							name,
							ABOVE_THE_LINE,
							CENTERED_ON_SEGMENT);
						setNewPositionForLabelOnSegment(
							ptFirstSegPoint,
							ptLastSegPoint,
							stereotype,
							BELOW_THE_LINE,
							CENTERED_ON_SEGMENT);

					} else {
						// must be horizontal, so handle in the standard way.  The segment to attach
						//  the label to is (for now) the 1st segment.  We may need to do something more
						//  refined like always place it by the source end.
						ptFirstSegPoint = plConnectionPoints.getPoint(0);
						ptLastSegPoint = plConnectionPoints.getPoint(1);

						if (name != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								name,
								ABOVE_THE_LINE,
								CENTERED_ON_RIGHT_ENDPOINT);
						if (stereotype != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								stereotype,
								BELOW_THE_LINE,
								CENTERED_ON_RIGHT_ENDPOINT);
					}
				}
				break;

			case 5 :
				{
					//System.out.println("[DiagramAssociation.placeStereotypeAndName] Handling case: 5 points " ); //$NON-NLS-1$
					// This works as a 3 point, if you just ignore the first and last (use 2, 3 and 4 [indices: 1, 2 and 3)
					// determine the line segment to use: 
					//       for 3, the rule is: "find the 2 points that have the same Y but different Xs"

					int iLeftCenterRightPosition = 0;

					if (plConnectionPoints.getPoint(1).y == plConnectionPoints.getPoint(2).y) {
						if (plConnectionPoints.getPoint(1).x < plConnectionPoints.getPoint(2).x) {
							ptFirstSegPoint = plConnectionPoints.getPoint(1);
							ptLastSegPoint = plConnectionPoints.getPoint(2);
							iLeftCenterRightPosition = RIGHT_OF_SEGMENT;
						} else if (
							plConnectionPoints.getPoint(2).x < plConnectionPoints.getPoint(1).x) {
							ptFirstSegPoint = plConnectionPoints.getPoint(2);
							ptLastSegPoint = plConnectionPoints.getPoint(1);
							iLeftCenterRightPosition = LEFT_OF_SEGMENT;
						}

						if (name != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								name,
								ABOVE_THE_LINE,
								iLeftCenterRightPosition);
						if (stereotype != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								stereotype,
								BELOW_THE_LINE,
								iLeftCenterRightPosition);
					} else if (
						plConnectionPoints.getPoint(3).y == plConnectionPoints.getPoint(2).y) {
						if (plConnectionPoints.getPoint(3).x < plConnectionPoints.getPoint(2).x) {
							ptFirstSegPoint = plConnectionPoints.getPoint(3);
							ptLastSegPoint = plConnectionPoints.getPoint(2);
							iLeftCenterRightPosition = RIGHT_OF_SEGMENT;
						} else if (
							plConnectionPoints.getPoint(2).x < plConnectionPoints.getPoint(3).x) {
							ptFirstSegPoint = plConnectionPoints.getPoint(2);
							ptLastSegPoint = plConnectionPoints.getPoint(3);
							iLeftCenterRightPosition = LEFT_OF_SEGMENT;
						}

						if (name != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								name,
								ABOVE_THE_LINE,
								iLeftCenterRightPosition);
						if (stereotype != null)
							setNewPositionForLabelOnSegment(
								ptFirstSegPoint,
								ptLastSegPoint,
								stereotype,
								BELOW_THE_LINE,
								iLeftCenterRightPosition);
					}
				}
				break;

			default :
				break;

		}
		//System.out.println("[DiagramAssociation.placeStereotypeAndName] BOT: Just set 'name' position to: " + name.getPosition().toString() ); //$NON-NLS-1$

	}

	private boolean isVerticalConnection(int iSourceSide, int iTargetSide) {

		if ((iSourceSide == AnchorManager.NORTH && iTargetSide == AnchorManager.SOUTH)
			|| (iTargetSide == AnchorManager.NORTH && iSourceSide == AnchorManager.SOUTH)) {
			return true;
		}
		return false;
	}

	private void setNewPositionForLabelOnSegment(
		Point ptSegmentStart,
		Point ptSegmentEnd,
		LabelModelNode lmnLabel,
		int iAboveBelowPosition,
		int iLeftCenterRightPosition) {
		Point ptResult = null;
		int strHeight = lmnLabel.getHeight();
		int strWidth = lmnLabel.getWidth();
		int iY = 0;
		int iX = 0;

		// calc X for name
		switch (iLeftCenterRightPosition) {
			case LEFT_OF_SEGMENT :
				{
					iX = ptSegmentStart.x - strWidth - DELTA_X;
				}
				break;

			case CENTERED_ON_SEGMENT :
				{
					int iMiddleOfSegX = ptSegmentEnd.x - ptSegmentStart.x;
					iX = iMiddleOfSegX - (strWidth / 2);
					if (iX < ptSegmentStart.x) {
						// if centering pushes it BEFORE the start, just left-justify it:
						iX = ptSegmentStart.x + DELTA_X;
					}
				}
				break;

			case RIGHT_OF_SEGMENT :
				{
					iX = ptSegmentEnd.x + DELTA_X;
				}
				break;

			case CENTERED_ON_RIGHT_ENDPOINT :
				{
					iX = ptSegmentEnd.x - (strWidth / 2);
				}
				break;

			case CENTERED_ON_LEFT_ENDPOINT :
				{
					iX = ptSegmentStart.x - (strWidth / 2);
				}
				break;

			default :
				break;
		}

		// calc Y for name
		if (iAboveBelowPosition == ABOVE_THE_LINE) {
			iY = ptSegmentStart.y - DELTA_Y - strHeight;
		} else {
			iY = ptSegmentStart.y + DELTA_Y;
		}

		ptResult = new Point(iX, iY);

		lmnLabel.setPosition(ptResult);
	}

	private double getSourceAngleInRadians(int iSourceSide) {
		// Code for Source End Object
		double sourceAngleInRadians = 0.0;

		// calc angle           
		if (getLinkType() == DiagramUiConstants.LinkRouter.ORTHOGONAL) {
			if (iSourceSide == AnchorManager.WEST)
				sourceAngleInRadians = Math.PI;
			else if (iSourceSide == AnchorManager.EAST)
				sourceAngleInRadians = 0;
			else if (iSourceSide == AnchorManager.NORTH)
				sourceAngleInRadians = Math.PI / 2;
			else
				sourceAngleInRadians = 3 * Math.PI / 2;

		} else {
			//  jhTODO: implement this later:               
			//                if(  sourceStartPt.getDistance( sourceEndPt ) > 0.001 ) {
			//                    if( Math.abs(sourceEndPt.y - sourceStartPt.y) < 0.001 ) {
			//                        if( sourceEndPt.x > sourceStartPt.x )
			//                            sourceAngleInRadians = 0.0;
			//                        else sourceAngleInRadians = Math.PI;
			//                    } else if( Math.abs(sourceEndPt.x - sourceStartPt.x) < 0.001 ) {
			//                        if( sourceEndPt.y > sourceStartPt.y )
			//                            sourceAngleInRadians = Math.PI/2;
			//                        else sourceAngleInRadians = 3*Math.PI/2;
			//                    } else {
			//                        sourceAngleInRadians = Math.atan( (sourceEndPt.y - sourceStartPt.y)/(sourceEndPt.x - sourceStartPt.x));
			//    
			//                        if( sourceAngleInRadians < 0.0 && (sourceEndPt.y > sourceStartPt.y) )
			//                            sourceAngleInRadians = Math.PI + sourceAngleInRadians;
			//                        else if( sourceAngleInRadians > 0.0 && (sourceEndPt.x < sourceStartPt.x) )
			//                            sourceAngleInRadians = Math.PI + sourceAngleInRadians;
			//    
			//                        if( targetAngleInRadians < 0 && sourceAngleInRadians > -90 )
			//                            sourceAngleInRadians = 2*Math.PI + sourceAngleInRadians;
			//                    }
			//                }
			//    
			//    
			//                // Code for Source End Object
			//    
			//                if( targetStartPt.getDistance( targetEndPt ) > 0.001 ) {
			//                    if( Math.abs(targetEndPt.y - targetStartPt.y) < 0.001 ) {
			//                        if( targetEndPt.x > targetStartPt.x )
			//                            targetAngleInRadians = 0.0;
			//                        else targetAngleInRadians = Math.PI;
			//                    } else if( Math.abs(targetEndPt.x - targetStartPt.x) < 0.001 ) {
			//                        if( targetEndPt.y > targetStartPt.y )
			//                            targetAngleInRadians = Math.PI/2;
			//                        else targetAngleInRadians = 3*Math.PI/2;
			//                    } else {
			//                        targetAngleInRadians = Math.atan( (targetEndPt.y - targetStartPt.y)/(targetEndPt.x - targetStartPt.x));
			//    
			//                        if( targetAngleInRadians < 0.0 && (targetEndPt.y > targetStartPt.y) )
			//                            targetAngleInRadians = Math.PI + targetAngleInRadians;
			//                        else if( targetAngleInRadians > 0.0 && (targetEndPt.x < targetStartPt.x) )
			//                            targetAngleInRadians = Math.PI + targetAngleInRadians;
			//    
			//                        if( targetAngleInRadians < 0 && targetAngleInRadians > -90 )
			//                            targetAngleInRadians = 2*Math.PI + targetAngleInRadians;
			//                    }
			//                }
		}

		return sourceAngleInRadians;
	}

	private double getTargetAngleInRadians(int iTargetSide) {

		double targetAngleInRadians = 0.0;

		// calc angle           
		if (getLinkType() == DiagramUiConstants.LinkRouter.ORTHOGONAL) {

			if (iTargetSide == AnchorManager.WEST)
				targetAngleInRadians = 0;
			else if (iTargetSide == AnchorManager.EAST)
				targetAngleInRadians = Math.PI;
			else if (iTargetSide == AnchorManager.NORTH)
				targetAngleInRadians = 3 * Math.PI / 2;
			else
				targetAngleInRadians = Math.PI / 2;

		} else {
			//  jhTODO: implement this later:               
			//                if(  sourceStartPt.getDistance( sourceEndPt ) > 0.001 ) {
			//                    if( Math.abs(sourceEndPt.y - sourceStartPt.y) < 0.001 ) {
			//                        if( sourceEndPt.x > sourceStartPt.x )
			//                            sourceAngleInRadians = 0.0;
			//                        else sourceAngleInRadians = Math.PI;
			//                    } else if( Math.abs(sourceEndPt.x - sourceStartPt.x) < 0.001 ) {
			//                        if( sourceEndPt.y > sourceStartPt.y )
			//                            sourceAngleInRadians = Math.PI/2;
			//                        else sourceAngleInRadians = 3*Math.PI/2;
			//                    } else {
			//                        sourceAngleInRadians = Math.atan( (sourceEndPt.y - sourceStartPt.y)/(sourceEndPt.x - sourceStartPt.x));
			//    
			//                        if( sourceAngleInRadians < 0.0 && (sourceEndPt.y > sourceStartPt.y) )
			//                            sourceAngleInRadians = Math.PI + sourceAngleInRadians;
			//                        else if( sourceAngleInRadians > 0.0 && (sourceEndPt.x < sourceStartPt.x) )
			//                            sourceAngleInRadians = Math.PI + sourceAngleInRadians;
			//    
			//                        if( targetAngleInRadians < 0 && sourceAngleInRadians > -90 )
			//                            sourceAngleInRadians = 2*Math.PI + sourceAngleInRadians;
			//                    }
			//                }
			//    
			//    
			//                // Code for Source End Object
			//    
			//                if( targetStartPt.getDistance( targetEndPt ) > 0.001 ) {
			//                    if( Math.abs(targetEndPt.y - targetStartPt.y) < 0.001 ) {
			//                        if( targetEndPt.x > targetStartPt.x )
			//                            targetAngleInRadians = 0.0;
			//                        else targetAngleInRadians = Math.PI;
			//                    } else if( Math.abs(targetEndPt.x - targetStartPt.x) < 0.001 ) {
			//                        if( targetEndPt.y > targetStartPt.y )
			//                            targetAngleInRadians = Math.PI/2;
			//                        else targetAngleInRadians = 3*Math.PI/2;
			//                    } else {
			//                        targetAngleInRadians = Math.atan( (targetEndPt.y - targetStartPt.y)/(targetEndPt.x - targetStartPt.x));
			//    
			//                        if( targetAngleInRadians < 0.0 && (targetEndPt.y > targetStartPt.y) )
			//                            targetAngleInRadians = Math.PI + targetAngleInRadians;
			//                        else if( targetAngleInRadians > 0.0 && (targetEndPt.x < targetStartPt.x) )
			//                            targetAngleInRadians = Math.PI + targetAngleInRadians;
			//    
			//                        if( targetAngleInRadians < 0 && targetAngleInRadians > -90 )
			//                            targetAngleInRadians = 2*Math.PI + targetAngleInRadians;
			//                    }
			//                }
		}
		return targetAngleInRadians;
	}

	private boolean canShowName() {
		if (this.sName != null && this.sName.length() > 0 && name != null) {
			return true;
		}
		return false;
	}

	private boolean canShowStereotype() {
		if (getStereotypeLabel() != null
			&& getStereotypeLabel().getName() != null
			&& getStereotypeLabel().getName().length() > 0
			&& stereotype != null) {
			return true;
		}
		return false;
	}

	private boolean canShowTargetMultiplicity() {

		if (targetMultiplicity != null
			&& targetMultiplicity.getName() != null
			&& targetMultiplicity.getName().length() > 0) {
			return true;
		}
		return false;
	}

	private boolean canShowSourceMultiplicity() {
		if (sourceMultiplicity != null
			&& sourceMultiplicity.getName() != null
			&& sourceMultiplicity.getName().length() > 0) {
			return true;
		}
		return false;
	}

	private boolean canShowTargetRole() {
		if (targetRoleName != null
			&& targetRoleName.getName() != null
			&& targetRoleName.getName().length() > 0) {
			return true;
		}
		return false;
	}

	private boolean canShowSourceRole() {
		if (sourceRoleName != null
			&& sourceRoleName.getName() != null
			&& sourceRoleName.getName().length() > 0) {
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	public int getSourceDecoratorId() {
		if (getBAssociation().getAggregation(BinaryAssociation.SOURCE_END)
			== BinaryAssociation.AGGREGATION_COMPOSITE)
			return BinaryAssociation.DECORATOR_DIAMOND_FILLED;
		else if (
			getBAssociation().getAggregation(BinaryAssociation.SOURCE_END)
				== BinaryAssociation.AGGREGATION_SHARED)
			return BinaryAssociation.DECORATOR_DIAMOND_OPEN;
		else if (
			getBAssociation().getNavigability(BinaryAssociation.SOURCE_END)
				== BinaryAssociation.NAVIGABILITY_NONE)
			return BinaryAssociation.DECORATOR_NON_NAVIGABLE;
		else if (
			getBAssociation().getNavigability(BinaryAssociation.SOURCE_END)
				== BinaryAssociation.NAVIGABILITY_NAVIGABLE)
			return BinaryAssociation.DECORATOR_ARROW_OPEN;

		return BinaryAssociation.DECORATOR_NONE;
	}

	public int getTargetDecoratorId() {
		if (getBAssociation().getAggregation(BinaryAssociation.TARGET_END)
			== BinaryAssociation.AGGREGATION_COMPOSITE)
			return BinaryAssociation.DECORATOR_DIAMOND_FILLED;
		else if (
			getBAssociation().getAggregation(BinaryAssociation.TARGET_END)
				== BinaryAssociation.AGGREGATION_SHARED)
			return BinaryAssociation.DECORATOR_DIAMOND_OPEN;
		else if (
			getBAssociation().getNavigability(BinaryAssociation.TARGET_END)
				== BinaryAssociation.NAVIGABILITY_NONE)
			return BinaryAssociation.DECORATOR_NON_NAVIGABLE;
		else if (
			getBAssociation().getNavigability(BinaryAssociation.TARGET_END)
				== BinaryAssociation.NAVIGABILITY_NAVIGABLE)
			return BinaryAssociation.DECORATOR_ARROW_OPEN;

		return BinaryAssociation.DECORATOR_NONE;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getLineStyle()
	 */
	public int getLineStyle() {
		return BinaryAssociation.LINE_SOLID;
	}

	/**
	 * @return
	 */
	public BinaryAssociation getBAssociation() {
		return bAssociation;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getModelObject()
	 */
	public EObject getModelObject() {
		return getBAssociation().getReference();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getToolTipString()
	 */
	public List getToolTipStrings() {
		if (getBAssociation().getReference() != null
			&& getBAssociation().getRelationshipAspect() != null
			&& getBAssociation().getRelationshipAspect() instanceof UmlAssociation) {
			List stringList = new ArrayList(3);

			String toolTip = "UmlAssociation"; //$NON-NLS-1$
			UmlAssociation theAspect = (UmlAssociation)getBAssociation().getRelationshipAspect();
			String tempS = theAspect.getToolTip(getBAssociation().getReference());
			if (tempS != null)
				toolTip = tempS;
			stringList.add(toolTip);
			//			stringList.add(" Source = " + sourceNode.getName()); //$NON-NLS-1$
			//			stringList.add(" Target = " + targetNode.getName()); //$NON-NLS-1$
			return stringList;
		}
		return Collections.EMPTY_LIST;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object someOtherObject) {
		if (someOtherObject instanceof DiagramUmlAssociation) {
			BinaryAssociation thisBass = getBAssociation();
			BinaryAssociation otherBass =
				((DiagramUmlAssociation)someOtherObject).getBAssociation();
			if (thisBass != null && otherBass != null)
				return thisBass.equals(otherBass);
		}
		return false;
	}
    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getBendpoints()
     * @since 4.2
     */
    public List getBendpoints() {
        return bendpoints;
    }
    /**
     *  
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#insertBendpoint(int, org.eclipse.draw2d.Bendpoint)
     * @since 4.2
     */
    public void insertBendpoint(int index, Bendpoint point) {
        getBendpoints().add(index, point);
        if( getDiagramLink() != null )
            DiagramLinkAdapter.addBendpoint(getDiagramLink(), index, point.getLocation());
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.BENDPOINT, null, null);
    }
    /**
     *  
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#removeBendpoint(int)
     * @since 4.2
     */
    public void removeBendpoint(int index) {
        getBendpoints().remove(index);
        if( getDiagramLink() != null )
            DiagramLinkAdapter.removeBendpoint(getDiagramLink(), index);
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.BENDPOINT, null, null);
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#removeBendpoint(int)
     * @since 4.2
     */
    public void clearBendpoints() {
        getBendpoints().clear();
        if( getDiagramLink() != null )
            DiagramLinkAdapter.clearBendpoints(getDiagramLink());
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.BENDPOINT, null, null);
    }
    /**
     *  
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#setBendpoint(int, org.eclipse.draw2d.Bendpoint)
     * @since 4.2
     */
    public void setBendpoint(int index, Bendpoint bPoint) {
        getBendpoints().set(index, bPoint);
        if( getDiagramLink() != null )
            DiagramLinkAdapter.setBendpoint(getDiagramLink(), index, bPoint.getLocation());
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.BENDPOINT, null, null);
    }
    /**
     *      * 
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#setBendpoints(java.util.Vector)
     * @since 4.2
     */ 
    public void setBendpoints(Vector newPoints) {
        List newBendpoints = new ArrayList(newPoints.size());
        Object nextObj = null;
        Iterator iter = newPoints.iterator();
        
        while( iter.hasNext() ) {
            nextObj = iter.next();
            if( nextObj instanceof Bendpoint) {
                newBendpoints.add(nextObj);
            } else if( nextObj instanceof Point) {
                newBendpoints.add(new LinkBendpoint((Point)nextObj));
            }
        }
        
        bendpoints = new ArrayList(newBendpoints);
        if( getDiagramLink() != null) {
            List points = new ArrayList(newBendpoints.size());
            for( int i=0; i<newBendpoints.size(); i++ )
                points.add(i, ((Bendpoint)newBendpoints.get(i)).getLocation());
            DiagramLinkAdapter.setBendpoints(getDiagramLink(), points);
        }
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.BENDPOINT, null, null);
    }
    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDiagramLink()
     * @since 4.2
     */
    public DiagramLink getDiagramLink() {
        if( !getBendpoints().isEmpty() && diagramLink == null ) {
            diagramLink = DiagramUiUtilities.findDiagramLink(getDiagram(), getModelObject());
            if( diagramLink == null )
                diagramLink = DiagramUiUtilities.createDiagramLink(getModelObject(), getDiagram());
        }
        return diagramLink;
    }
    
    public Diagram getDiagram() {
        DiagramModelNode parentDiagramNode = sourceNode.getParent();
        if( parentDiagramNode != null && parentDiagramNode.getModelObject() instanceof Diagram )
            return (Diagram)parentDiagramNode.getModelObject();
        
        return null;
    }
    
    /**
     *  
     * @param link
     * @since 4.2
     */
    public void setDiagramLink(DiagramLink link) {
        diagramLink = link;
    }
    
    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#refreshBendPoints()
     * @since 4.2
     */
    public void refreshBendPoints() {
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.BENDPOINT, null, null);
    }

    
    /** 
     * @param theShowMultiplicity The showMultiplicity to set.
     * @since 5.0
     */
    public void setShowMultiplicity(boolean theShowMultiplicity) {
        this.showMultiplicity = theShowMultiplicity;
    }

    
    /** 
     * @param theShowName The showName to set.
     * @since 5.0
     */
    public void setShowName(boolean theShowName) {
        this.showName = theShowName;
    }

    
    /** 
     * @param theShowRoles The showRoles to set.
     * @since 5.0
     */
    public void setShowRoles(boolean theShowRoles) {
        this.showRoles = theShowRoles;
    }
}
