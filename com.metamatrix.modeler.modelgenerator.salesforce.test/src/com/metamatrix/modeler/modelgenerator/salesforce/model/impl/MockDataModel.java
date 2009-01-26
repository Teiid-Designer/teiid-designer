/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;


import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection;
import com.metamatrix.modeler.modelgenerator.salesforce.model.DataModel;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceObject;

public class MockDataModel implements DataModel {

	public void decrementSelectionCount() {
		// TODO Auto-generated method stub

	}

	public SalesforceObject getSalesforceObject(String name) {
		SalesforceObjectImpl result = new SalesforceObjectImpl();
		result.setObjectMetadata(null, new MockDataModel());
		result.setSelected(true);
		return result;
	}

	public Object[] getSalesforceObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasSelectedObjects() {
		// TODO Auto-generated method stub
		return false;
	}

	public void incrementSelectionCount() {
		// TODO Auto-generated method stub

	}

	public void load(SalesforceConnection conn, IProgressMonitor monitor) throws Exception {
		// TODO Auto-generated method stub

	}

}
