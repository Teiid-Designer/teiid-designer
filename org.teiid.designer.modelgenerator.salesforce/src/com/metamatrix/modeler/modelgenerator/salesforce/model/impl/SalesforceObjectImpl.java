/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.metamatrix.modeler.modelgenerator.salesforce.model.DataModel;
import com.metamatrix.modeler.modelgenerator.salesforce.model.Relationship;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceField;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceObject;
import com.sforce.soap.partner.ChildRelationship;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;

public class SalesforceObjectImpl implements SalesforceObject {
	
	public static final String FIELD_LABEL = "label"; //$NON-NLS-1$
	public static final String FIELD_NAME = "name"; //$NON-NLS-1$
	public static final String FIELD_TYPE = "type"; //$NON-NLS-1$
	public static final String FIELD_SORTABLE = "sortable"; //$NON-NLS-1$
	
	public DataModel dataModel;
	public DescribeSObjectResult objectMetadata;
	private boolean selected;
	
	public SalesforceObjectImpl() {}

	public void setObjectMetadata(DescribeSObjectResult objectMetadata, DataModel dataModel) {
		this.objectMetadata = objectMetadata;
		this.dataModel = dataModel;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.salesforce.model.impl.SalesforceObject#isQueryable()
	 */
	public boolean isQueryable() {
		return objectMetadata.isQueryable();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.salesforce.model.impl.SalesforceObject#getVisibleName()
	 */
	public String getLabel() {
		return objectMetadata.getLabel();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.salesforce.model.impl.SalesforceObject#getFieldCount()
	 */
	public int getFieldCount() {
		int result = 0;
		if(null != objectMetadata && null != objectMetadata.getFields()) {
			result =  objectMetadata.getFields().length;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.modelgenerator.salesforce.model.impl.SalesforceObject#getFields()
	 */
	public SalesforceField[] getFields() {
		SalesforceField[] result;
		if(null != objectMetadata.getFields()) {
			result = getFields(objectMetadata);
		} else {
			result = new SalesforceField[0];
		}
		return result;
	}

	private SalesforceField[] getFields(DescribeSObjectResult objectMetadata) {
		Object[] fields = objectMetadata.getFields(); 
		SalesforceField[] result = new SalesforceField[fields.length];
		
		for (int i = 0; i < fields.length; i++) {
			result[i] = new SalesforceFieldImpl((Field)fields[i]);
		}
		
		return result;
	}

	public String getName() {
		return objectMetadata.getName();
	}

	public List getSelectedRelationships() {
		return getRelationships(false);
	}

	public List getAllRelationships() {
		return getRelationships(true);
	}
	
	private List getRelationships(boolean includeUnselected) {
		List result;
		ChildRelationship[] children = objectMetadata.getChildRelationships();
		if(children != null && children.length != 0) {
			result = new ArrayList();
			for (int i = 0; i < children.length; i++) {
				ChildRelationship childRelation = children[i];
				String childTable = childRelation.getChildSObject();
				if(includeUnselected || dataModel.getSalesforceObject(childTable).isSelected()) {
					Relationship newRelation = new RelationshipImpl();
					newRelation.setParentTable(objectMetadata.getName());
					newRelation.setChildTable(childRelation.getChildSObject());
					newRelation.setForeignKeyField(childRelation.getField());
					newRelation.setCascadeDelete(childRelation.isCascadeDelete());
					result.add(newRelation);
				}
			}
		} else {
			result = new ArrayList();
		}
		return result;
	}
	
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean checked) {
		selected = checked;
		if(checked ) {
			dataModel.incrementSelectionCount();
		} else {
			dataModel.decrementSelectionCount();
		}
	}

	public boolean isCreateable() {
		return objectMetadata.isCreateable();
	}

	public boolean isUpdateable() {
		return objectMetadata.isUpdateable();
	}

	public boolean isDeleteable() {
		return objectMetadata.isDeletable();
	}
	
	public boolean isSearchable() {
		return objectMetadata.isSearchable();
	}
	
	public boolean isCustom() {
		return objectMetadata.isCustom();
	}
	
	public boolean isMergeable() {
		return objectMetadata.isMergeable();
	}
	
	public boolean isReplicateable() {
		return objectMetadata.isReplicateable();
	}
	
	public boolean isRetrieveable() {
		return objectMetadata.isRetrieveable();
	}
}
