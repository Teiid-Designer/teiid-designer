/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model;

import java.util.List;

public interface SalesforceObject {

	public abstract boolean isQueryable();

	public abstract String getVisibleName();
	
	public abstract String getName();

	public abstract int getFieldCount();

	public abstract SalesforceField[] getFields();

	public abstract void setSelected(boolean checked);
	
	public abstract boolean isSelected();

	public abstract List getSelectedRelationships();

	public abstract List getAllRelationships();

	public boolean isCreateable();

	public boolean isUpdateable();

	public boolean isDeleteable();

	public boolean isSearchable();

	public boolean isReplicateable();

	public boolean isRetrieveable();

}
