/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;


/** 
 * This interface provides non-ui plugins to listener for changes in resources within the Designer workspace.
 * 
 * Currently Refactor actions for Move, Rename & Delete are wired to notify when commands are completed including Undo/Redo
 * @since 5.0
 */
public interface IRefactorResourceListener {

    void notifyRefactored(RefactorResourceEvent event);
}
