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

/** 
 * @since 4.2
 */
public class DeleteBendpointCommand extends BendpointCommand {
    private Bendpoint bendpoint;

    @Override
    public void execute() {
        bendpoint = (Bendpoint)getConnectionModel().getBendpoints().get(getIndex());
        getConnectionModel().removeBendpoint(getIndex());
        super.execute();
    }

    @Override
    public void undo() {
        super.undo();
        getConnectionModel().insertBendpoint(getIndex(), bendpoint);
    }

}
