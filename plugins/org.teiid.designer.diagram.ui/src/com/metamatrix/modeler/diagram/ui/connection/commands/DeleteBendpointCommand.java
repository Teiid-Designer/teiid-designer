/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
