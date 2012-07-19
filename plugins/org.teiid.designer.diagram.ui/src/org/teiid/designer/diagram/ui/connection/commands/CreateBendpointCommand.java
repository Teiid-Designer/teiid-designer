/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.connection.commands;

import org.eclipse.draw2d.geometry.Point;
import org.teiid.designer.diagram.ui.connection.LinkBendpoint;

/**
 * @since 8.0
 */
public class CreateBendpointCommand extends BendpointCommand {
    private Point bpPoint;
    
    public CreateBendpointCommand(Point point) {
        super();
        bpPoint = point;
    }
    
    @Override
    public void execute() {
        LinkBendpoint wbp = new LinkBendpoint(bpPoint);
//        wbp.setRelativeDimensions(getFirstRelativeDimension(), getSecondRelativeDimension());
        getConnectionModel().insertBendpoint(getIndex(), wbp);
        super.execute();
    }

    @Override
    public void undo() {
        super.undo();
        getConnectionModel().removeBendpoint(getIndex());
    }
}
