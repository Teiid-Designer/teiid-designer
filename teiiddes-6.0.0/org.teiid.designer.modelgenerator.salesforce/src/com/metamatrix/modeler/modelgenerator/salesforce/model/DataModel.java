/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
