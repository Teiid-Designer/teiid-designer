/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigureFactory;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextException;
import com.metamatrix.modeler.relationship.NavigationHistory;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.NavigatorLabelProvider;
import com.metamatrix.modeler.relationship.ui.navigation.model.DummyFocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationContainerModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationDiagramNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NonFocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.RelationshipModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationDiagramFigureFactory  extends AbstractDiagramFigureFactory implements UiConstants {
	private static final int NAVIGATION_DIAGRAM = 0;
	private static final int RELATIONSHIP_NODE = 1;
	private static final int FOCUS_NODE = 2;
	private static final int NON_FOCUS_NODE = 3;
	private static final int CONTAINER_NODE = 4;
	private static final int DUMMY_FOCUS_NODE = 5;
	    
    private ILabelProvider labelProvider;
    private NavigationHistory navHistory;
    private NavigationNode currentBackFocusNode;
	private NavigationNode currentNextFocusNode;
	/**
	 * Construct an instance of UmlFigureFactory.
	 * 
	 */
	public NavigationDiagramFigureFactory(NavigationHistory history) {
		super();
        labelProvider = new NavigatorLabelProvider();
        this.navHistory = history;
        
	}
	
	
	@Override
    public Figure createFigure(Object modelObject) {
	
		Figure newFigure = null;
//		ColorPalette colorPalette = null;
		switch( getObjectType(modelObject) ) {
	            
			case NAVIGATION_DIAGRAM: {
				newFigure = new NavigationDiagramFigure();
//				
//				newFigure = new FreeformLayer();
//				newFigure.setLayoutManager(new FreeformLayout());
//				// Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
//				newFigure.setOpaque(true);
//				RGB currentBkgdColor = 
//					PreferenceConverter.getColor(
//						DiagramUiPlugin.getDefault().getPreferenceStore(),
//						PluginConstants.Prefs.Appearance.RELATIONSHIP_BKGD_COLOR);
//				newFigure.setBackgroundColor(new Color(null, currentBkgdColor));
//				System.out.println(" FIGURE FACTORY >>> Created NAVIGATION_DIAGRAM");
			} break;
	            
			case RELATIONSHIP_NODE: {
				RelationshipModelNode node = (RelationshipModelNode)modelObject;

				String nodeName = node.getName();
				
				if( nodeName == null || nodeName.equalsIgnoreCase("null")) //$NON-NLS-1$
					nodeName = "UNKNOWN_NODE"; //$NON-NLS-1$
				newFigure = new RelationshipNodeFigure(node);
				
			} break;
			
			case NON_FOCUS_NODE: {
				NonFocusModelNode node = (NonFocusModelNode)modelObject;

				String nodeName = node.getName();
				Image icon = null;
				if( node.getModelObject() != null ) {
//					colorPalette = getColorPalette(node);
					icon = labelProvider.getImage(node.getModelObject());
				}
				if( nodeName == null || nodeName.equalsIgnoreCase("null")) //$NON-NLS-1$
					nodeName = "UNKNOWN_NODE"; //$NON-NLS-1$
				newFigure = new NonFocusNodeFigure(node, icon, node.getToolTip());
				if( node.getModelObject() != null ) {
					NavigationNode navNode = (NavigationNode)node.getModelObject();
					if( currentBackFocusNode != null && navNode.getModelObjectUri().equals(currentBackFocusNode.getModelObjectUri())) {
						newFigure.setBackgroundColor(UiConstants.Colors.LAST_FOCUS_NODE_BKGD);
					} else if(currentNextFocusNode != null && navNode.getModelObjectUri().equals(currentNextFocusNode.getModelObjectUri())) {
						newFigure.setBackgroundColor(UiConstants.Colors.NEXT_FOCUS_NODE_BKGD);
					}
				}
//				System.out.println(" FIGURE FACTORY >>> Created NON_FOCUS_NODE");
			} break;
			
			case FOCUS_NODE: {
				FocusModelNode node = (FocusModelNode)modelObject;

				String nodeName = node.getName();
				Image icon = null;
				if( node.getModelObject() != null ) {
//					colorPalette = getColorPalette(node);
					icon = labelProvider.getImage(node.getModelObject());
				}
				if( nodeName == null || nodeName.equalsIgnoreCase("null")) //$NON-NLS-1$
					nodeName = "UNKNOWN_NODE"; //$NON-NLS-1$
				newFigure = new FocusNodeFigure(node, icon, node.getToolTip());
//				System.out.println(" FIGURE FACTORY >>> Created FOCUS_NODE");
			} break;
			
			case DUMMY_FOCUS_NODE: {
				newFigure = new DummyFocusNodeFigure((NavigationModelNode)modelObject);
				newFigure.setSize(21, 21);
			} break;
			
			case CONTAINER_NODE: {
				NavigationContainerModelNode node = (NavigationContainerModelNode)modelObject;

				newFigure = new NavigationContainerNodeFigure(node, node.getToolTip());
//				System.out.println(" FIGURE FACTORY >>> Created CONTAINER_NODE");
			} break;
	            
			// Delegate to the UML figure factory to make figures for all other model types.
			default: {
			} break;
	            
		}
	
		return newFigure;
	}
	    
	protected int getObjectType( Object modelObject ) {
		int objectType = -1;
	        
		if( modelObject != null ) {
			if (modelObject instanceof NavigationDiagramNode) {
				objectType = NAVIGATION_DIAGRAM;
			} else if (modelObject instanceof DummyFocusModelNode) {
				objectType = DUMMY_FOCUS_NODE;
			} else if (modelObject instanceof FocusModelNode) {
				objectType = FOCUS_NODE;
			} else if (modelObject instanceof RelationshipModelNode) {
				objectType = RELATIONSHIP_NODE;
			} else if (modelObject instanceof NonFocusModelNode) {
				objectType = NON_FOCUS_NODE;
			} else if (modelObject instanceof NavigationContainerModelNode) {
				objectType = CONTAINER_NODE;
			}
		}
		return objectType;
	}
	    
//	private ColorPalette getColorPalette(AbstractNavigationModelNode node) {
////		return DiagramUiPlugin.
////					getDiagramTypeManager().getDiagram(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID).
////							getColorPaletteManager().getColorPalette(node.getModelObject());
//		return null;
//	}

	public void resetHistoryFocusNodes() {
		setBackFocusNode();
		setNextFocusNode();
	}

	private void setBackFocusNode() {
		currentBackFocusNode = null;
		if ( navHistory != null ) {
			NavigationContext nc = null;
			try {
				nc = navHistory.peakAtPrevious();
			} catch (NavigationContextException e) {
				UiConstants.Util.log(e);
			} finally {
				if(nc != null ) {
					currentBackFocusNode = nc.getFocusNode();
				}
			}
		}
	}
	
	private void setNextFocusNode() {
		currentNextFocusNode = null;
		if ( navHistory != null ) {
			NavigationContext nc = null;
			try {
				nc = navHistory.peakAtNext();
			} catch (NavigationContextException e) {
				UiConstants.Util.log(e);
			} finally {
				if(nc != null ) {
					currentNextFocusNode = nc.getFocusNode();
				}
			}
		}
	}
}
