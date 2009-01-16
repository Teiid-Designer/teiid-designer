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
			UiConstants.Colors.SELECTION,
			UiConstants.Colors.HILITE,
			UiConstants.Colors.OUTLINE
			);
			
		nonFocusNodePalette = new FigureColorPalette();
		nonFocusNodePalette.setColors(
			UiConstants.Colors.NON_FOCUS_NODE_BKGD,
			UiConstants.Colors.NON_FOCUS_NODE_BKGD,
			ColorConstants.black,
			UiConstants.Colors.SELECTION,
			UiConstants.Colors.HILITE,
			UiConstants.Colors.OUTLINE
			);
			
		relationshipPalette = new FigureColorPalette();
		relationshipPalette.setColors(
			UiConstants.Colors.RELATIONSHIP_BKGD,
			UiConstants.Colors.RELATIONSHIP_HEADER_BKGD,
			ColorConstants.black,
			UiConstants.Colors.SELECTION,
			UiConstants.Colors.HILITE,
			UiConstants.Colors.OUTLINE
			);
			
		relationshipTypePalette = new FigureColorPalette();
		relationshipTypePalette.setColors(
			UiConstants.Colors.RELATIONISHIP_TYPE_BKGD,
			UiConstants.Colors.RELATIONISHIP_TYPE_BKGD,
			ColorConstants.black,
			UiConstants.Colors.SELECTION,
			UiConstants.Colors.HILITE,
			UiConstants.Colors.OUTLINE
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
