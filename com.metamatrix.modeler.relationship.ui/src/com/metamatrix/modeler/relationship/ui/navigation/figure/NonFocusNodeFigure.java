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

package com.metamatrix.modeler.relationship.ui.navigation.figure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.diagram.ui.util.ToolTipUtil;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NonFocusNodeFigure extends AbstractNavigationNodeFigure {
	/**
	 * 
	 */
	public NonFocusNodeFigure(NavigationModelNode modelNode, Image image, String toolTip) {
		super(modelNode, image);
		setBackgroundColor(UiConstants.Colors.NON_FOCUS_NODE_BKGD);
		setToolTip(toolTip);
	}
	
	@Override
    protected boolean useLocalCoordinates(){
		return true;
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#layoutFigure()
	 */
	@Override
    public void layoutFigure() {
		setSizeToMinimum();
	}

	@Override
    public void setToolTip(String toolTip) {
		super.setToolTip(createToolTip(toolTip));
	}
	
	@Override
    protected IFigure createToolTip(String toolTipString) {
		return ToolTipUtil.createToolTip(toolTipString);
	}

}
