/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.salesforce.model.impl;


import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.modelgenerator.salesforce.connection.SalesforceConnection;
import org.teiid.designer.modelgenerator.salesforce.model.DataModel;
import org.teiid.designer.modelgenerator.salesforce.model.SalesforceObject;


public class MockDataModel implements DataModel {

	@Override
	public void decrementSelectionCount() {
		// TODO Auto-generated method stub

	}

	@Override
	public SalesforceObject getSalesforceObject(String name) {
		SalesforceObjectImpl result = new SalesforceObjectImpl();
		result.setObjectMetadata(null, new MockDataModel());
		result.setSelected(true);
		return result;
	}

	@Override
	public Object[] getSalesforceObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSelectedObjects() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void incrementSelectionCount() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(SalesforceConnection conn, IProgressMonitor monitor) throws Exception {
		// TODO Auto-generated method stub

	}

}
