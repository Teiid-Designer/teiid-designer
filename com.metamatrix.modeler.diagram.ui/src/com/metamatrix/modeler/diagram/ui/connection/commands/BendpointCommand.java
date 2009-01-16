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

package com.metamatrix.modeler.diagram.ui.connection.commands;

import org.eclipse.gef.commands.Command;

import org.eclipse.draw2d.geometry.*;

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
