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

package com.metamatrix.modeler.diagram.ui.util;

import org.eclipse.gef.GraphicalEditPart;

import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;

public class DiagramConnectionEndpointEditPolicy
	extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy {

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
