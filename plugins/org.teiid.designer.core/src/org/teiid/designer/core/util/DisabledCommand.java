/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.util;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.CommandParameter;

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
    @Override
	public boolean canExecute() {
        return false;
    }

    /**
     * @see org.eclipse.emf.common.command.Command#execute()
     */
    @Override
	public void execute() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#canUndo()
     */
    @Override
	public boolean canUndo() {
        return false;
    }

    /**
     * @see org.eclipse.emf.common.command.Command#undo()
     */
    @Override
	public void undo() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#redo()
     */
    @Override
	public void redo() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getResult()
     */
    @Override
	public Collection getResult() {
        return command.getResult();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getAffectedObjects()
     */
    @Override
	public Collection getAffectedObjects() {
        return command.getAffectedObjects();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getLabel()
     */
    @Override
	public String getLabel() {
        return command.getLabel();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#getDescription()
     */
    @Override
	public String getDescription() {
        return command.getDescription();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#dispose()
     */
    @Override
	public void dispose() {
        command.dispose();
    }

    /**
     * @see org.eclipse.emf.common.command.Command#chain(org.eclipse.emf.common.command.Command)
     */
    @Override
	public Command chain(Command command) {
        return command.chain(command);
    }
}
