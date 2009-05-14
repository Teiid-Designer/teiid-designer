/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection.commands;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Point;

import com.metamatrix.modeler.diagram.ui.connection.LinkBendpoint;


/** 
 * @since 4.2
 */
public class MoveBendpointCommand extends BendpointCommand 
{
    private Point bpPoint;
    private Bendpoint oldBendpoint;

    public MoveBendpointCommand(Point point) {
        super();
        bpPoint = point;
    }
    
    @Override
    public void execute() {
        LinkBendpoint bp = new LinkBendpoint(bpPoint);
//        bp.setRelativeDimensions(getFirstRelativeDimension(), 
//                        getSecondRelativeDimension());
        setOldBendpoint((Bendpoint)getConnectionModel().getBendpoints().get(getIndex()));
        getConnectionModel().setBendpoint(getIndex(), bp);
        super.execute();
    }

    protected Bendpoint getOldBendpoint() {
        return oldBendpoint;
    }

    public void setOldBendpoint(Bendpoint bp) {
        oldBendpoint = bp;
    }

    @Override
    public void undo() {
        super.undo();
        getConnectionModel().setBendpoint(getIndex(), getOldBendpoint());
    }
}
