/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;

public class BendpointCommand extends Command {

    protected int index;
    protected Point location;
    protected NodeConnectionModel connectionModel;
    private Dimension d1, d2;

    protected Dimension getFirstRelativeDimension() {
        return d1;
    }

    protected Dimension getSecondRelativeDimension() {
        return d2;
    }

    protected int getIndex() {
        return index;
    }

    protected Point getLocation() {
        return location;
    }

    protected NodeConnectionModel getConnectionModel() {
        return connectionModel;
    }

    @Override
    public void redo() {
        execute();
    }

    public void setRelativeDimensions(Dimension dim1,
                                      Dimension dim2) {
        d1 = dim1;
        d2 = dim2;
    }

    public void setIndex(int i) {
        index = i;
    }

    public void setLocation(Point p) {
        location = p;
    }

    public void setConnectionModel(NodeConnectionModel connection) {
        connectionModel = connection;
    }

}
