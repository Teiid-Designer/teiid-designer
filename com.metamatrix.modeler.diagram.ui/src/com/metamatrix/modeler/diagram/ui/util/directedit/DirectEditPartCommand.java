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
