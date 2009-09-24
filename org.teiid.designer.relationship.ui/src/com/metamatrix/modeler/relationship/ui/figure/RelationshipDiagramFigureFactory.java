/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.drawing.model.DrawingModelNode;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.connection.RelationshipLink;
import com.metamatrix.modeler.relationship.ui.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipDiagramNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipFolderModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipTypeModelNode;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipDiagramFigureFactory  extends AbstractDiagramFigureFactory implements UiConstants {
	    
	/**
	 * Construct an instance of UmlFigureFactory.
	 * 
	 */
	public RelationshipDiagramFigureFactory() {
		super();
	}
	    
	
	@Override
    public Figure createFigure(Object modelObject) {
	
		Figure newFigure = null;
		ColorPalette colorPalette = null;
		switch( getModelTypeID(modelObject) ) {
	            
			case RelationshipModelTypes.DIAGRAM: {
				newFigure = new FreeformLayer();
				newFigure.setLayoutManager(new FreeformLayout());
				// Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
				newFigure.setOpaque(true);
				RGB currentBkgdColor = 
					PreferenceConverter.getColor(
						DiagramUiPlugin.getDefault().getPreferenceStore(),
						PluginConstants.Prefs.Appearance.RELATIONSHIP_BKGD_COLOR);
				newFigure.setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
			} break;
	            
			case RelationshipModelTypes.RELATIONSHIP: {
				RelationshipModelNode node = (RelationshipModelNode)modelObject;

				String nodeName = node.getName();
				Image relIcon = null;
				Image sourceIcon = null;
				Image targetIcon = null;
				
				if( node.getModelObject() != null ) {
					colorPalette = getColorPalette(node);
					relIcon = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getImage(node.getModelObject());
					if( node.getSourceRole() != null )
						sourceIcon = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getImage(node.getSourceRole());
					if( node.getTargetRole() != null )
						targetIcon = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getImage(node.getTargetRole());
				}
				if( nodeName == null || nodeName.equalsIgnoreCase("null")) { //$NON-NLS-1$
					nodeName = ModelerCore.getModelEditor().getName(node.getModelObject());
				}
//					nodeName = "UNKNOWN_NODE"; //$NON-NLS-1$
				newFigure = new RelationshipNodeFigure(
					nodeName,
					relIcon,
					node.getStereotype(), 
					node.getSourceRoleName(),
					sourceIcon,
					node.getTargetRoleName(),
					targetIcon, 
					colorPalette);
				((DiagramFigure)newFigure).setDiagramModelNode(node);
				
			} break;
			
			case RelationshipModelTypes.TYPE: {
				RelationshipTypeModelNode node = (RelationshipTypeModelNode)modelObject;

				String nodeName = node.getName();
				Image icon = null;
				if( node.getModelObject() != null ) {
					colorPalette = getColorPalette(node);
					icon = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getImage(node.getModelObject());
				}
				if( nodeName == null || nodeName.equalsIgnoreCase("null")) { //$NON-NLS-1$
					nodeName = ModelerCore.getModelEditor().getName(node.getModelObject());
				}
				String roleString = node.getRoleString(); //"{Role 1} {Role2}"; 
//					nodeName = "UNKNOWN_NODE"; //$NON-NLS-1$
				newFigure = new RelationshipTypeFigure("<<Relationship Type>>", nodeName, roleString, icon, colorPalette); //$NON-NLS-1$
				
			} break;
			
			case RelationshipModelTypes.FOCUS_NODE: {
				FocusModelNode node = (FocusModelNode)modelObject;
				
				Image icon = null;
				if( node.getModelObject() != null ) {
					colorPalette = getColorPalette(node);
					icon = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getImage(node.getModelObject());
				}

				newFigure = new FocusNodeFigure(icon, colorPalette);
				
			} break;
			
			case RelationshipModelTypes.FOLDER: {
				RelationshipFolderModelNode node = (RelationshipFolderModelNode)modelObject;
				
				Image icon = null;
				if( node.getModelObject() != null ) {
					colorPalette = getColorPalette(node);
					icon = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getImage(node.getModelObject());
				}

				newFigure = new RelationshipFolderFigure(
						"<<Folder>>", //$NON-NLS-1$
						node.getName(),
						node.getPath(),
						icon, colorPalette);
				
			} break;
	            
			// Delegate to the UML figure factory to make figures for all other model types.
			default: {
			} break;
	            
		}
	
		return newFigure;
	}
	
	private int getModelTypeID(Object iModel) {
		int typeID = RelationshipModelTypes.OTHER;
		
		if( iModel instanceof DrawingModelNode ) {
			typeID = RelationshipModelTypes.DRAWING;
		} else if( iModel instanceof RelationshipDiagramNode ) {
			typeID = RelationshipModelTypes.DIAGRAM;
		} else if( iModel instanceof RelationshipModelNode ) {
			typeID = RelationshipModelTypes.RELATIONSHIP;
		} else if( iModel instanceof RelationshipTypeModelNode ) {
			typeID = RelationshipModelTypes.TYPE;
		} else if( iModel instanceof LabelModelNode ) {
			typeID = RelationshipModelTypes.LABEL;
		} else if( iModel instanceof FocusModelNode ) {
			typeID = RelationshipModelTypes.FOCUS_NODE;
		} else if( iModel instanceof RelationshipLink ) {
			typeID = RelationshipModelTypes.LINK;
		} else if( iModel instanceof RelationshipFolderModelNode ) {
			typeID = RelationshipModelTypes.FOLDER;
		}
		
		return typeID;
	}
	    
	    
	private ColorPalette getColorPalette(DiagramModelNode node) {
		return DiagramUiPlugin.
					getDiagramTypeManager().getDiagram(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID).
							getColorPaletteManager().getColorPalette(node);
	}
}
