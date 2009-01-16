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
