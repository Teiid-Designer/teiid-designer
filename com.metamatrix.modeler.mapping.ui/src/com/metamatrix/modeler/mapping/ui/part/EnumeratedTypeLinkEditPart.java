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
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;
import com.metamatrix.modeler.mapping.ui.UiConstants;

/**
 * @since 5.0.2
 */
public class EnumeratedTypeLinkEditPart extends NodeConnectionEditPart
                                        implements UiConstants{

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public EnumeratedTypeLinkEditPart() {
        if (Util.isDebugEnabled(com.metamatrix.modeler.transformation.ui.DebugConstants.TX_DIAGRAM_CONNECTIONS) && Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.TRACE)) {
            String msg = getClass().getSimpleName() + ".constructor:model=" + getModel(); //$NON-NLS-1$
            Util.debug(com.metamatrix.modeler.transformation.ui.DebugConstants.TX_DIAGRAM_CONNECTIONS, msg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     * @since 5.0.2
    **/
    @Override
    protected IFigure createFigure() {
		DiagramPolylineConnection connectionFigure = new DiagramPolylineConnection();
		List toolTips = ((NodeConnectionModel)getModel()).getToolTipStrings();

        if (toolTips != null && !toolTips.isEmpty()) {
            connectionFigure.setToolTip(connectionFigure.createToolTip(toolTips));
        }

        if (Util.isDebugEnabled(com.metamatrix.modeler.transformation.ui.DebugConstants.TX_DIAGRAM_CONNECTIONS) &&  Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.TRACE)) {
            String msg = getClass().getSimpleName() + ".createFigure():model=" + getModel(); //$NON-NLS-1$
            Util.debug(com.metamatrix.modeler.transformation.ui.DebugConstants.TX_DIAGRAM_CONNECTIONS, msg);
        }

        connectionFigure.setLineStyle(Graphics.LINE_SOLID);
        connectionFigure.setLineWidth(1);
        connectionFigure.setForegroundColor(ColorConstants.darkBlue);

        return connectionFigure;
    }

}
