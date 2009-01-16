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

package com.metamatrix.modeler.modelgenerator.salesforce.model;


import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection;

/**
 * 
 * Contains the metadata about the Salesforce Objects.
 *
 */
public interface DataModel {

	/**
	 * Gathers the metadata from the Salesforce system.
	 * @param conn The connection the the salesforce instance
	 * @throws Exception
	 */
	public abstract void load(SalesforceConnection conn, IProgressMonitor monitor) throws Exception;

	/**
	 * Returns the list of objects in the Salesforce system.
	 * @return
	 */
	public abstract Object[] getSalesforceObjects();

	public abstract SalesforceObject getSalesforceObject(String name);

	public abstract boolean hasSelectedObjects();

	public abstract void incrementSelectionCount();

	public abstract void decrementSelectionCount();

}
