/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * A ModelRefactorCommand is the interface that all refactoring commands have to implement
 */
public interface ModelRefactorCommand {

    /**
     *  Returns whether the comamad is valid to <code>execute</code>.
     * This <b>must</b> be called before calling <code>execute</code>.
     * @return an <code>IStatus</code> indicating whether the comamad is valid to <code>execute</code>.
     */
    IStatus canExecute();

    /**
     * Performs the command activity required for the effect.
     * The effect of calling <code>execute</code> when <code>canExecute</code> returns <code>false</code>, 
     * or when <code>canExecute</code> hasn't been called, is undefined.
     */
    IStatus execute(final IProgressMonitor monitor);

    /**
     * Returns whether the command can be undone.
     * The result of calling this before <code>execute</code> is well defined,
     * but the result of calling this before calling <code>canExecute</code> is undefined, i.e.,
     * a command that returns <code>false</code> for <code>canExecute</code> may return <code>true</code> for canUndo, 
     * even though that is a contradiction.
     * @return whether the command can be undone.
     */
    boolean canUndo();

    /**
     * Returns whether the command can be redone.
     * The result of calling this before <code>execute</code> is well defined,
     * but the result of calling this before calling <code>canExecute</code> is undefined, i.e.,
     * a command that returns <code>false</code> for <code>canExecute</code> may return <code>true</code> for canRedo, 
     * even though that is a contradiction.
     * @return whether the command can be redone.
     */
    boolean canRedo();

    /**
     * Performs the command activity required to <code>undo</code> the effects of a preceding <code>execute</code> (or <code>redo</code>).
     * The effect, if any, of calling <code>undo</code> before <code>execute</code> or <code>redo</code> have been called, 
     * or when canUndo returns <code>false</code>, is undefined.
     */
    void undo();

    /**
     * Performs the command activity required to <code>redo</code> the effect after undoing the effect.
     * The effect, if any, of calling <code>redo</code> before <code>undo</code> is called is undefined.
     * Note that if you implement <code>redo</code> to call <code>execute</code> 
     * then any derived class will be restricted by that decision also.
     */
    void redo();

    /**
     * Returns a collection of things which this command wishes to present as it's result.
     * The result of calling this before an <code>execute</code> or <code>redo</code>, or after an <code>undo</code>, is undefined.
     * @return a collection of things which this command wishes to present as it's result.
     */
    Collection getResult();

    /**
     * Returns the collection of things which this command wishes to present as the objects affected by the command.
     * Typically should could be used as the selection that should be highlighted to best illustrate the effect of the command.
     * The result of calling this before an <code>execute</code>, <code>redo</code>, or <code>undo</code> is undefined.
     * The result may be different after an <code>undo</code> than it is after an <code>execute</code> or <code>redo</code>,
     * but the result should be the same (equivalent) after either an <code>execute</code> or <code>redo</code>.
     * @return the collection of things which this command wishes to present as the objects affected by the command.
     */
    Collection getAffectedObjects();

    /**
     * Returns a string suitable to represent the label that identifies this command.
     * @return a string suitable to represent the label that identifies this command.
     */
    String getLabel();

    /**
     * Returns a string suitable to help describe the effect of this command.
     * @return a string suitable to help describe the effect of this command.
     */
    String getDescription();

    /**
     * Returns a <code>Collection</code> of <code>IStatus</code> errors and warnings that caused this 
     * command to return <code>IStatus.ERROR</code> or <code>IStatus.WARNING</code> after <code>execute</code>.
     * @return
     */
    Collection getPostExecuteMessages();

}
