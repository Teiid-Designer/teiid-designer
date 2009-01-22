/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search.commands;

import org.eclipse.core.runtime.IStatus;

/**
 * Command when executed help look up relationships between entities.
 */
public interface SearchCommand {
	
	/**
	 * Return true if the command has all the arguments it needs for execution.
	 * @return true if the command is ready for execution, else false.
	 */
	boolean canExecute();
	
	/**
	 * Execute the command and return a status indication, if the execution
	 * was successful.
	 * @return IStatus giving the sucess of command execution
	 */
	IStatus execute();
}
