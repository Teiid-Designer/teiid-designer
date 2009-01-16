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

package com.metamatrix.modeler.core.util;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;

/**
 * This DisabledCommandParameter is a wrapper for a {@link CommandParameter} that signals that the
 * command parameter should be disabled.
 */
public class DisabledCommand implements Command {
    
    private final Command command;

    /**
     * Construct an instance of DisabledCommandParameter.
     * @param owner
     */
    public DisabledCommand(final Command disabled) {
        this.command = disabled;
    }
    
    public Command getDisabledCommand() {
        return this.command;
    }

    /**
     * @see org.eclipse.emf.common.command.Command#canExecute()
     */
    public boolean canExecute() {
        return false;
    }

    /**
     * @see org.eclipse.emf.common.command.Command#execute()
     */
    public void execute() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#canUndo()
     */
    public boolean canUndo() {
        return false;
    }

    /**
     * @see org.eclipse.emf.common.command.Command#undo()
     */
    public void undo() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#redo()
     */
    public void redo() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getResult()
     */
    public Collection getResult() {
        return command.getResult();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getAffectedObjects()
     */
    public Collection getAffectedObjects() {
        return command.getAffectedObjects();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getLabel()
     */
    public String getLabel() {
        return command.getLabel();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getDescription()
     */
    public String getDescription() {
        return command.getDescription();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#dispose()
     */
    public void dispose() {
        command.dispose();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#chain(org.eclipse.emf.common.command.Command)
     */
    public Command chain(Command command) {
        return command.chain(command);
    }
}
