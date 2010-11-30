/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.diagram.ui.util.colors.DefaultColorPaletteManager;
import com.metamatrix.modeler.diagram.ui.util.colors.FigureColorPalette;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipTypeModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipColorPaletteManager extends DefaultColorPaletteManager {
	FigureColorPalette focusNodePalette;
	FigureColorPalette nonFocusNodePalette;
	FigureColorPalette relationshipPalette;
	FigureColorPalette relationshipTypePalette;
	FigureColorPalette unknownPalette;
	/**
	 * Construct an instance of MappingColorPaletteManager.
	 * 
	 */
	public RelationshipColorPaletteManager() {
		super();
		init();
	}
	
	private void init() {
		focusNodePalette = new FigureColorPalette();
		focusNodePalette.setColors(
			UiConstants.Colors.FOCUS_NODE_BKGD,
			UiConstants.Colors.FOCUS_NODE_BKGD,
			ColorConstants.black,
			ColorConstants.lightBlue,
			ColorConstants.lightGreen,
			ColorConstants.black
			);
			
		nonFocusNodePalette = new FigureColorPalette();
		nonFocusNodePalette.setColors(
			UiConstants.Colors.NON_FOCUS_NODE_BKGD,
			UiConstants.Colors.NON_FOCUS_NODE_BKGD,
			ColorConstants.black,
			ColorConstants.lightBlue,
			ColorConstants.lightGreen,
			ColorConstants.black
			);
			
		relationshipPalette = new FigureColorPalette();
		relationshipPalette.setColors(
			UiConstants.Colors.RELATIONSHIP_BKGD,
			UiConstants.Colors.RELATIONSHIP_HEADER_BKGD,
			ColorConstants.black,
			ColorConstants.lightBlue,
			ColorConstants.lightGreen,
			ColorConstants.black
			);
			
		relationshipTypePalette = new FigureColorPalette();
		relationshipTypePalette.setColors(
			UiConstants.Colors.RELATIONISHIP_TYPE_BKGD,
			UiConstants.Colors.RELATIONISHIP_TYPE_BKGD,
			ColorConstants.black,
			ColorConstants.lightBlue,
			ColorConstants.lightGreen,
			ColorConstants.black
			);
			
		unknownPalette = relationshipPalette;
	}

    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager#getColorPalette(org.eclipse.emf.ecore.EObject)
	 */
	@Override
    public ColorPalette getColorPalette(Object object) {
		if( object != null ) {
			if( object instanceof FocusModelNode ) {
				return focusNodePalette;
			} else if( object instanceof RelationshipModelNode ) {
				return relationshipPalette;
			} else if( object instanceof RelationshipTypeModelNode) {
				return relationshipTypePalette;
			} else {
                if( object instanceof DiagramModelNode)
			    return super.getColorPalette(((DiagramModelNode)object).getModelObject());
            }
		}
        
		return unknownPalette;
	}

}
