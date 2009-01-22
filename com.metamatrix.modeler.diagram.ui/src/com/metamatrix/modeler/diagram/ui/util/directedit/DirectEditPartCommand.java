/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.directedit;

import org.eclipse.gef.commands.Command;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;

/**
 * EditAttributeCommand
 */
public class DirectEditPartCommand extends Command {

    private String newName, oldName;
    private DirectEditPart directEditPart;

    public DirectEditPartCommand(DirectEditPart part, String s) {
        directEditPart = part;
        if (s != null)
            newName = s;
        else
            newName = ""; //$NON-NLS-1$
    }

    @Override
    public void execute() {
        oldName = directEditPart.getText();
        String undoLabel = DiagramUiConstants.Util.getString("DirectEditPartCommand.undoRenameLabel", oldName); //$NON-NLS-1$
        boolean started = ModelerCore.startTxn(true, true, undoLabel, this);
        boolean succeeded = false;
        try {
            
            directEditPart.setText(newName);
            succeeded = true;
        } finally {
            if (started) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    @Override
    public void undo() {
        String undoLabel = DiagramUiConstants.Util.getString("DirectEditPartCommand.undoRenameLabel", oldName); //$NON-NLS-1$
        boolean started = ModelerCore.startTxn(true, true, undoLabel, this);
        boolean succeeded = false;
        try {
            directEditPart.setText(oldName);
            succeeded = true;
        } finally {
            if (started) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

}
