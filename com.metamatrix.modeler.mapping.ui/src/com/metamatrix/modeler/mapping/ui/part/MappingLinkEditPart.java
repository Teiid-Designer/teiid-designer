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

package com.metamatrix.modeler.mapping.ui.part;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
//import org.eclipse.draw2d.PolygonDecoration;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;
import com.metamatrix.modeler.transformation.ui.DebugConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;

/**
 * MappingLinkEditPart
 */
public class MappingLinkEditPart extends NodeConnectionEditPart {
    private static final String THIS_CLASS = "MappingLinkEditPart"; //$NON-NLS-1$
    
    public MappingLinkEditPart() {
        super();
        if ( UiConstants.Util.isDebugEnabled(DebugConstants.TX_DIAGRAM_CONNECTIONS) &&  UiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.TRACE)) {
            String message = THIS_CLASS + ".MappingLinkEditPart() CONSTRUCTOR.  Model = " + getModel(); //$NON-NLS-1$
            UiConstants.Util.debug(DebugConstants.TX_DIAGRAM_CONNECTIONS, message);    
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {
		DiagramPolylineConnection connectionFigure = new DiagramPolylineConnection();
        
		List toolTips = ((NodeConnectionModel)getModel()).getToolTipStrings();
		if( toolTips != null && !toolTips.isEmpty() )
			connectionFigure.setToolTip(connectionFigure.createToolTip(toolTips));
			
        if ( UiConstants.Util.isDebugEnabled(DebugConstants.TX_DIAGRAM_CONNECTIONS) &&  UiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.TRACE)) {
            String message = THIS_CLASS + ".createFigure() Model = " + getModel(); //$NON-NLS-1$
            UiConstants.Util.debug(DebugConstants.TX_DIAGRAM_CONNECTIONS, message);    
        }

        connectionFigure.setLineStyle( Graphics.LINE_SOLID );
        int iStandardWidth = connectionFigure.getLineWidth();
        iStandardWidth = 1;
        connectionFigure.setLineWidth( iStandardWidth );
        connectionFigure.setForegroundColor(ColorConstants.darkBlue);
        
        return connectionFigure;
    }

}
