/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
