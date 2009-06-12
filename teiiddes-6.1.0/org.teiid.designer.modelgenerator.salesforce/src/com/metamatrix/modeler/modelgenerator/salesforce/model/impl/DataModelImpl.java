/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection;
import com.metamatrix.modeler.modelgenerator.salesforce.model.DataModel;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceObject;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.SoapBindingStub;

public class DataModelImpl implements DataModel {
	
	Integer maxBatchSize;
	String encoding;
	private Map<String, SalesforceObject> salesforceObjects = new TreeMap<String, SalesforceObject>();
	private int selectedCount = 0;
	
	public DataModelImpl() {
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.salesforce.model.impl.DataModel#load(com.metamatrix.modeler.modelgenerator.salesforce.connection.Connection)
	 */
	public void load(SalesforceConnection conn, IProgressMonitor monitor) throws Exception {
		monitor.beginTask(Messages.getString("DataModelImpl.gathering.metadata"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		SoapBindingStub binding = conn.getBinding();
		DescribeGlobalResult describeGlobalResult;
		try {
			describeGlobalResult = binding.describeGlobal();
		} catch (RemoteException e) {
			Exception ce = new Exception(e.getCause().getMessage());
			ce.initCause(e.getCause());
			throw ce;
		}
		
		String[] types = describeGlobalResult.getTypes();
		int i = 0;
		while(i < types.length) {
			int arrayLength = i + 100 > types.length ? types.length - i : 100;
			String[] typesBatch = new String[arrayLength];
			for (int j = 0; j < arrayLength; j++) {
				typesBatch[j] = types[i + j];
			}
			
			DescribeSObjectResult[] describeSObjectResults;
			try {
				describeSObjectResults = binding.describeSObjects(typesBatch);
			} catch (RemoteException e) {
				Exception ce = new Exception(e.getCause().getMessage());
				ce.initCause(e.getCause());
				throw ce;
			}
			
			for (int x = 0; x < describeSObjectResults.length; x++) {
				DescribeSObjectResult describeSObjectResult = describeSObjectResults[x];
				SalesforceObjectImpl object = new SalesforceObjectImpl();
				monitor.subTask(Messages.getString("DataModelImpl.gathering.metadata.table") + describeSObjectResult.getLabel()); //$NON-NLS-1$
				object.setObjectMetadata(describeSObjectResult, this);
				addSalesforceObject(typesBatch[x], object);
				monitor.worked(1);
			}
			i = i + arrayLength;
		}
	}
	
	public Integer getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(Integer maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.salesforce.model.impl.DataModel#addDataObject(java.lang.String, com.metamatrix.modeler.modelgenerator.salesforce.model.impl.DataModelObject)
	 */
	public void addSalesforceObject(String objectName, SalesforceObject sObject) throws Exception {
		Object result = salesforceObjects.put(objectName, sObject);
		if(null != result) {
			throw new Exception(Messages.getString("DataModelImpl.duplicate.found") + objectName); //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.salesforce.model.impl.DataModel#getDataObjects()
	 */
	public Object[] getSalesforceObjects() {
		return salesforceObjects.values().toArray();
	}
	
	public SalesforceObject getSalesforceObject(String name) {
		return salesforceObjects.get(name);
	}

	public boolean hasSelectedObjects() {
		return selectedCount  > 0;
	}

	public void incrementSelectionCount() {
		++selectedCount;
	}

	public void decrementSelectionCount() {
		--selectedCount;
	}

}
