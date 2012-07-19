/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.connection;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.teiid.designer.diagram.ui.figure.DiagramPolylineConnection;

/**
 * LinkEndpointEditPolicy
 *
 * @since 8.0
 */
public class LinkEndpointEditPolicy extends ConnectionEndpointEditPolicy {

    @Override
    protected void addSelectionHandles() {
        super.addSelectionHandles();
        getConnectionFigure().hilite(false);
        getConnectionFigure().setLineWidth(3);
    }

    protected DiagramPolylineConnection getConnectionFigure() {
        return (DiagramPolylineConnection) ((GraphicalEditPart)getHost()).getFigure();
    }

    @Override
    protected void removeSelectionHandles() {
        super.removeSelectionHandles();
        getConnectionFigure().setLineWidth(1);
    }

}
