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
