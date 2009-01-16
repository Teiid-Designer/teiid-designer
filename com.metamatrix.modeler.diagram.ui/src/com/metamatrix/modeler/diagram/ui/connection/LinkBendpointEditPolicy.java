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

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.BendpointRequest;

import com.metamatrix.modeler.diagram.ui.connection.commands.BendpointCommand;
import com.metamatrix.modeler.diagram.ui.connection.commands.CreateBendpointCommand;
import com.metamatrix.modeler.diagram.ui.connection.commands.DeleteBendpointCommand;
import com.metamatrix.modeler.diagram.ui.connection.commands.MoveBendpointCommand;

public class LinkBendpointEditPolicy extends org.eclipse.gef.editpolicies.BendpointEditPolicy {

    @Override
    protected Command getCreateBendpointCommand(BendpointRequest request) {
        Point p = request.getLocation();
        CreateBendpointCommand com = new CreateBendpointCommand(p);
        Connection conn = getConnection();

        conn.translateToRelative(p);

        com.setLocation(p);
        Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
        Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

        conn.translateToRelative(ref1);
        conn.translateToRelative(ref2);

//        com.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
        com.setConnectionModel((NodeConnectionModel)request.getSource().getModel());
        com.setIndex(request.getIndex());
        return com;
    }

    @Override
    protected Command getMoveBendpointCommand(BendpointRequest request) {
        Point p = request.getLocation();
        MoveBendpointCommand com = new MoveBendpointCommand(p);
        Connection conn = getConnection();

        conn.translateToRelative(p);

        com.setLocation(p);

        Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
        Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

        conn.translateToRelative(ref1);
        conn.translateToRelative(ref2);

//        com.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
        com.setConnectionModel((NodeConnectionModel)request.getSource().getModel());
        com.setIndex(request.getIndex());
        return com;
    }

    @Override
    protected Command getDeleteBendpointCommand(BendpointRequest request) {
        BendpointCommand com = new DeleteBendpointCommand();
        Point p = request.getLocation();
        com.setLocation(p);
        com.setConnectionModel((NodeConnectionModel)request.getSource().getModel());
        com.setIndex(request.getIndex());
        return com;
    }

}
