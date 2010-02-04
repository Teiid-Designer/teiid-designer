/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.modelextension;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.ObjectExtension;
import com.metamatrix.modeler.modelgenerator.salesforce.util.ModelBuildingException;

public class ExtensionManager {

	static final String MODEL_FILE_NAME = "SalesforceExtensions.xmi"; //$NON-NLS-1$
	public static final String PACKAGE_NAME = "salesforce"; //$NON-NLS-1$
	static final String PACKAGE_PREFIX = "sf"; //$NON-NLS-1$
	static final String PACKAGE_NS_URI = "http://www.metamatrix.com/metamodels/Salesforce"; //$NON-NLS-1$
	static final String SALESFORCE_TABLE = "Salesforce Table"; //$NON-NLS-1$
	static final String SALESFORCE_COLUMN = "Salesforce Column"; //$NON-NLS-1$

	static final String TABLE_SUPPORTS_CREATE = "Supports Create"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_DELETE = "Supports Delete"; //$NON-NLS-1$
	static final String TABLE_CUSTOM = "Custom"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_LOOKUP = "Supports ID Lookup"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_MERGE = "Supports Merge"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_QUERY = "Supports Query"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_REPLICATE = "Supports Replicate"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_RETRIEVE = "Supports Retrieve"; //$NON-NLS-1$
	static final String TABLE_SUPPORTS_SEARCH = "Supports Search"; //$NON-NLS-1$
	
	static final String COLUMN_DEFAULTED = "Defaulted on Create"; //$NON-NLS-1$
	static final String COLUMN_CUSTOM = "Custom"; //$NON-NLS-1$
	static final String COLUMN_CALCULATED = "Calculated"; //$NON-NLS-1$
	static final String COLUMN_PICKLIST_VALUES = "Picklist Values"; //$NON-NLS-1$
	
	
	
	private XPackage salesforcePackage;
	private XClass salesforceTableXClass;
	private XClass salesforceColumnXClass;

	private XAttribute supportsSearchTableAttribute;
	private XAttribute supportsRetrieveTableAttribute;
	private XAttribute supportsReplicateTableAttribute;
	private XAttribute supportsQueryTableAttribute;
	private XAttribute supportsMergeTableAttribute;
	private XAttribute supportsidLookupTableAttribute;
	private XAttribute customTableAttribute;
	private XAttribute supportsDeleteTableAttribute;
	private XAttribute supportsCreateTableAttribute;
	
	private XAttribute calculatedColumnAttribute;
	private XAttribute customColumnAttribute;
	private XAttribute defaultedColumnAttribute;
	private XAttribute picklistValuesColumnAttribute;
	
	public XPackage getSalesforcePackage() {
		return salesforcePackage;
	}
	
	public void loadModelExtensions(IContainer targetModelLocation, IProgressMonitor monitor) throws ModelBuildingException {

		Path modelPath = new Path(MODEL_FILE_NAME);
		IFile iFile = targetModelLocation.getFile(modelPath);
		if(!iFile.exists()) {
			createModelExtensions(iFile, targetModelLocation, monitor);
		} else {
			loadModelExtension(targetModelLocation);
		}

	}
	
	/////////////////////////////
	// Create a new instance of the  model extension
	/////////////////////////////

	private void createModelExtensions(IFile file, IContainer targetModelLocation, IProgressMonitor monitor) throws ModelBuildingException {
		ModelResource modelExtension = ModelerCore.create(file);

		ModelAnnotation annotation = CoreFactory.eINSTANCE.createModelAnnotation();
		annotation.setPrimaryMetamodelUri(RelationalPackage.eINSTANCE.getNsURI());
		annotation.setModelType(ModelType.EXTENSION_LITERAL);
		
		ExtensionPackage xPackage = ExtensionPackage.eINSTANCE;
		ExtensionFactory xFactory = xPackage.getExtensionFactory();

		salesforcePackage = xFactory.createXPackage();
		salesforcePackage.setName(PACKAGE_NAME);
		salesforcePackage.setNsPrefix(PACKAGE_PREFIX);
		salesforcePackage.setNsURI(PACKAGE_NS_URI);
						
		try {
			modelExtension.getEmfResource().getContents().add(annotation);
			modelExtension.getEmfResource().getContents().add(salesforcePackage);
		} catch (ModelWorkspaceException e1) {
			ModelBuildingException mbe = new ModelBuildingException();
			mbe.initCause(e1);
			throw mbe;
		}
		
		salesforceTableXClass = xFactory.createXClass();
		salesforceTableXClass.setExtendedClass(RelationalPackage.eINSTANCE.getBaseTable());
		salesforceTableXClass.setName(SALESFORCE_TABLE);
		salesforcePackage.getEClassifiers().add(salesforceTableXClass);  // this works
		createTableExtensions(xFactory, salesforceTableXClass);
		
		salesforceColumnXClass = xFactory.createXClass();
		salesforceColumnXClass.setExtendedClass(RelationalPackage.eINSTANCE.getColumn());
		salesforceColumnXClass.setName(SALESFORCE_COLUMN);
		salesforcePackage.getEClassifiers().add(salesforceColumnXClass);  // this works
		createColumnExtensions(xFactory, salesforceColumnXClass);
		
		try {
			modelExtension.save(monitor, false);
		} catch (ModelWorkspaceException e) {
			ModelBuildingException mbe = new ModelBuildingException();
			mbe.initCause(e);
			throw mbe;
		}
	}

	private void createTableExtensions(ExtensionFactory xFactory, XClass salesforceTable) {
		supportsCreateTableAttribute = xFactory.createXAttribute();
		supportsCreateTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_CREATE);
		supportsCreateTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsCreateTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsCreateTableAttribute);

		supportsDeleteTableAttribute = xFactory.createXAttribute();
		supportsDeleteTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_DELETE);
		supportsDeleteTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsDeleteTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsDeleteTableAttribute);
		
		customTableAttribute = xFactory.createXAttribute();
		customTableAttribute.setName(ExtensionManager.TABLE_CUSTOM);
		customTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		customTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(customTableAttribute);
		
		supportsidLookupTableAttribute = xFactory.createXAttribute();
		supportsidLookupTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_LOOKUP);
		supportsidLookupTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsidLookupTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsidLookupTableAttribute);
		
		supportsMergeTableAttribute = xFactory.createXAttribute();
		supportsMergeTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_MERGE);
		supportsMergeTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsMergeTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsMergeTableAttribute);
		
		supportsQueryTableAttribute = xFactory.createXAttribute();
		supportsQueryTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_QUERY);
		supportsQueryTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsQueryTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsQueryTableAttribute);
		
		supportsReplicateTableAttribute = xFactory.createXAttribute();
		supportsReplicateTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_REPLICATE);
		supportsReplicateTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsReplicateTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsReplicateTableAttribute);
		
		supportsRetrieveTableAttribute = xFactory.createXAttribute();
		supportsRetrieveTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_RETRIEVE);
		supportsRetrieveTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsRetrieveTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsRetrieveTableAttribute);
		
		supportsSearchTableAttribute = xFactory.createXAttribute();
		supportsSearchTableAttribute.setName(ExtensionManager.TABLE_SUPPORTS_SEARCH);
		supportsSearchTableAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		supportsSearchTableAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceTable.getEStructuralFeatures().add(supportsSearchTableAttribute);
		}
	
	private void createColumnExtensions(ExtensionFactory xFactory, XClass salesforceColumn) {
		defaultedColumnAttribute = xFactory.createXAttribute();
		defaultedColumnAttribute.setName(ExtensionManager.COLUMN_DEFAULTED);
		defaultedColumnAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		defaultedColumnAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceColumn.getEStructuralFeatures().add(defaultedColumnAttribute);
		
		calculatedColumnAttribute = xFactory.createXAttribute();
		calculatedColumnAttribute.setName(ExtensionManager.COLUMN_CALCULATED);
		calculatedColumnAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		calculatedColumnAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceColumn.getEStructuralFeatures().add(calculatedColumnAttribute);
		
		customColumnAttribute = xFactory.createXAttribute();
		customColumnAttribute.setName(ExtensionManager.COLUMN_CUSTOM);
		customColumnAttribute.setDefaultValueLiteral(Boolean.FALSE.toString());
		customColumnAttribute.setEType(EcorePackage.eINSTANCE.getEBoolean());
		salesforceColumn.getEStructuralFeatures().add(customColumnAttribute);
		
		picklistValuesColumnAttribute = xFactory.createXAttribute();
		picklistValuesColumnAttribute.setName(ExtensionManager.COLUMN_PICKLIST_VALUES);
		picklistValuesColumnAttribute.setEType(EcorePackage.eINSTANCE.getEString());
		salesforceColumn.getEStructuralFeatures().add(picklistValuesColumnAttribute);
	}

	/////////////////////////////
	// Load an existing model extesion
	/////////////////////////////
	
	private void loadModelExtension(IContainer targetModelLocation) throws ModelBuildingException {

		Container cntr;
		try {
			cntr = com.metamatrix.modeler.core.ModelerCore.getModelContainer();
		} catch (CoreException e) {
			ModelBuildingException mbe = new ModelBuildingException();
			mbe.initCause(e);
			throw mbe;
		}
		
		cntr.getPackageRegistry().put(ExtensionPackage.eNS_URI, ExtensionPackage.eINSTANCE);
		
		IFile extensionFile = targetModelLocation.getProject().getFile(new Path(MODEL_FILE_NAME));        
        String extPath = extensionFile.getRawLocation().toOSString();
        URI fileURI = URI.createFileURI(extPath);
        Resource xPkg = cntr.getResource(fileURI, true);
        EList resources = xPkg.getContents();
        salesforcePackage = null;
        for(Iterator resIter = resources.iterator(); resIter.hasNext();) {
			Object next = resIter.next();
			if(next instanceof XPackage) salesforcePackage = (XPackage) next;
		}

        if(null == salesforcePackage) throw new RuntimeException(Messages.getString("ExtensionManager.package.null.after.load")); //$NON-NLS-1$
        
		salesforceTableXClass = salesforcePackage.findXClass(RelationalPackage.eINSTANCE.getBaseTable());
		EList attributes = salesforceTableXClass.getEAllAttributes();
		
		XAttribute attribute;
		Iterator iter = attributes.iterator();
		while(iter.hasNext()) {
			attribute = (XAttribute) iter.next();
			if(attribute.getName().equals(TABLE_SUPPORTS_CREATE)) {
				supportsCreateTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_SUPPORTS_DELETE)) {
				supportsDeleteTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_CUSTOM)) {
				customTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_SUPPORTS_LOOKUP)) {
				supportsidLookupTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_SUPPORTS_MERGE)) {
				supportsMergeTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_SUPPORTS_QUERY)) {
				supportsQueryTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_SUPPORTS_REPLICATE)) {
				supportsReplicateTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_SUPPORTS_RETRIEVE)) {
				supportsRetrieveTableAttribute = attribute;
			} else if(attribute.getName().equals(TABLE_SUPPORTS_SEARCH)) {
				supportsSearchTableAttribute = attribute;
			}
		}
		
		salesforceColumnXClass = salesforcePackage.findXClass(RelationalPackage.eINSTANCE.getColumn());
		attributes = salesforceColumnXClass.getEAllAttributes();
		iter = attributes.iterator();
		while(iter.hasNext()) {
			attribute = (XAttribute) iter.next();
			if(attribute.getName().equals(ExtensionManager.COLUMN_CALCULATED)) {
				calculatedColumnAttribute = attribute;
			} else if(attribute.getName().equals(ExtensionManager.COLUMN_CUSTOM)) {
				customColumnAttribute = attribute;
			} else if(attribute.getName().equals(ExtensionManager.COLUMN_DEFAULTED)) {
				defaultedColumnAttribute = attribute;
			} else if(attribute.getName().equals(ExtensionManager.COLUMN_PICKLIST_VALUES)) {
				picklistValuesColumnAttribute = attribute;
			}
		}
	}
	
	/////////////////////////////
	// Set the value of the objects defined by the model extension
	/////////////////////////////
	
	
	/////
	// Table Attributes
	////
	
	public void setTableQueryable(BaseTable table, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(table, salesforceTableXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(supportsQueryTableAttribute, bool);
	}

	public void setTableDeletable(BaseTable table, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(table, salesforceTableXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(supportsDeleteTableAttribute, bool);		
	}
	
	public void setTableCreatable(BaseTable table, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(table, salesforceTableXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(supportsCreateTableAttribute, bool);		
	}
	
	public void setTableSearchable(BaseTable table, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(table, salesforceTableXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(supportsSearchTableAttribute, bool);		
	}
	
	public void setTableReplicate(BaseTable table, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(table, salesforceTableXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(supportsReplicateTableAttribute, bool);		
	}
	
	public void setTableRetrieve(BaseTable table, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(table, salesforceTableXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(supportsRetrieveTableAttribute, bool);		
	}

	/////
	// Column Attributes
	////
	
	/**
	 * Set the value of the Picklist Values column attribute.
	 * @param table
	 * @param className
	 */
	public void setAllowedColumnValues(Column column, List allowedValues) {
		StringBuffer picklistValues = new StringBuffer();
		Iterator iter = allowedValues.iterator();
		while (iter.hasNext()) {
			picklistValues.append((String)iter.next());
			if(iter.hasNext()) {
				picklistValues.append(',');
			}	
		}
		ObjectExtension extension = new ObjectExtension(column, salesforceColumnXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(picklistValuesColumnAttribute, picklistValues.toString());
	}

	public void setColumnCustom(Column column, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(column, salesforceColumnXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(customColumnAttribute, bool);
	}

	public void setColumnCalculated(Column column, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(column, salesforceColumnXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(calculatedColumnAttribute, bool);
	}

	public void setColumnDefaultedOnCreate(Column column, Boolean bool) {
		ObjectExtension extension = new ObjectExtension(column, salesforceColumnXClass, ModelerCore.getModelEditor());
		extension.eDynamicSet(defaultedColumnAttribute, bool);
	}


}
