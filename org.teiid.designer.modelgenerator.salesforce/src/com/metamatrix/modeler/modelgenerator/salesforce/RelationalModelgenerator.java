/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection;
import com.metamatrix.modeler.modelgenerator.salesforce.model.DataModel;
import com.metamatrix.modeler.modelgenerator.salesforce.model.Relationship;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceField;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceObject;
import com.metamatrix.modeler.modelgenerator.salesforce.modelextension.ExtensionManager;
import com.metamatrix.modeler.modelgenerator.salesforce.util.ModelBuildingException;
import com.metamatrix.modeler.modelgenerator.salesforce.util.NameUtil;
import com.sforce.soap.partner.QueryResult;

public class RelationalModelgenerator {

    // The folder to create the relational model in
    private IContainer targetModelLocation;

    private IProgressMonitor monitor;

    // The wrapper around the salesforce extension model
    private ExtensionManager exManager;

    // The metadata from the salesforce instance
    private DataModel salesforcemetadata;

    // The relationships to create between salesforce objects
    private List relationships;

    // used to look up tables in order to create the relationships
    private Map tablesByName;

    // determines if we model the common attributes of all salesforce objects
    private boolean supressAuditFields;

    private SalesforceConnection connection;

    /**
     * @param targetModelLocation the folder that contains the relational model
     * @param monitor the progress monitor shown during model creation
     * @param connection
     * @param supressAuditFields determines if the audit fields are modeled
     */
    public RelationalModelgenerator( IContainer targetModelLocation,
                                     IProgressMonitor monitor,
                                     DataModel model,
                                     SalesforceConnection connection,
                                     boolean supressAuditFields ) {
        if (null == targetModelLocation || null == monitor) {
            throw new AssertionError(Messages.getString("RelationalModelgenerator.null.creation.param")); //$NON-NLS-1$
        }
        this.targetModelLocation = targetModelLocation;
        this.monitor = monitor;
        this.salesforcemetadata = model;
        this.connection = connection;
        this.supressAuditFields = supressAuditFields;
        relationships = new ArrayList();
        tablesByName = new HashMap();

    }

    /**
     * Create the relational model from the salesforce metadata
     * 
     * @param resource the file to contain the relational model.
     * @throws ModelBuildingException
     */
    public void createRelationalModel( Resource resource ) throws ModelBuildingException {
        // Get the salesforce extension model and create it in the target directory
        // if it is not already there.
        exManager = new ExtensionManager();
        exManager.loadModelExtensions(targetModelLocation, monitor);

        // Create the model annotation, the top level object in our of our models and
        // set some of its attributes
        ModelAnnotation annotation = CoreFactory.eINSTANCE.createModelAnnotation();
        annotation.setModelType(ModelType.PHYSICAL_LITERAL);
        annotation.setPrimaryMetamodelUri(RelationalPackage.eINSTANCE.getNsURI());
        annotation.setExtensionPackage(exManager.getSalesforcePackage());
        resource.getContents().add(annotation);

        // Create a schema object in the relational model.
        Schema schema = RelationalFactory.eINSTANCE.createSchema();
        schema.setName(ExtensionManager.PACKAGE_NAME);
        schema.setNameInSource(ExtensionManager.PACKAGE_NAME);
        resource.getContents().add(schema);

        // Loop over the salesforce metadata creating tables and columns
        Object[] objects = salesforcemetadata.getSalesforceObjects();
        for (int i = 0; i < objects.length; i++) {
            SalesforceObject sfo = (SalesforceObject)objects[i];
            if (sfo.isSelected()) {
                monitor.subTask("Creating " + sfo.getName() + " table"); //$NON-NLS-1$ //$NON-NLS-2$
                addTableToModel(sfo, schema);
                monitor.worked(1);
            }
        }

        // Create the relations between the tables. This is done after all
        // the tables are built because you can't put a foreign key into a
        // table that might not have been created yet.
        createRelationships();
    }

    /**
     * Create a relational table from salesforce metadata
     * 
     * @param sfo metadata about a salesforce object to be modeled as a table
     * @param schema the relational schema that contains the tables
     * @throws ModelBuildingException
     */
    private void addTableToModel( SalesforceObject sfo,
                                  Schema schema ) throws ModelBuildingException {
        BaseTable newTable = RelationalFactory.eINSTANCE.createBaseTable();

        this.relationships.addAll(sfo.getSelectedRelationships());
        this.tablesByName.put(sfo.getName(), newTable);
        newTable.setSchema(schema);
        newTable.setName(NameUtil.normalizeName(sfo.getName()));
        newTable.setNameInSource(sfo.getName());

        newTable.setCardinality(getCardinality(sfo));

        newTable.setSupportsUpdate(sfo.isUpdateable());

        // Extensions
        if (sfo.isQueryable()) {
            exManager.setTableQueryable(newTable, Boolean.TRUE);
        }
        if (sfo.isDeleteable()) {
            exManager.setTableDeletable(newTable, Boolean.TRUE);
        }
        if (sfo.isCreateable()) {
            exManager.setTableCreatable(newTable, Boolean.TRUE);
        }
        if (sfo.isSearchable()) {
            exManager.setTableSearchable(newTable, Boolean.TRUE);
        }
        if (sfo.isReplicateable()) {
            exManager.setTableReplicate(newTable, Boolean.TRUE);
        }
        if (sfo.isRetrieveable()) {
            exManager.setTableRetrieve(newTable, Boolean.TRUE);
        }

        addColumnsToTable(sfo, newTable);
    }

    private int getCardinality( SalesforceObject sfo ) {
        int result = 0;
        if (sfo.isQueryable()) {
            StringBuffer query = new StringBuffer();
            query.append("SELECT COUNT() FROM "); //$NON-NLS-1$
            query.append(sfo.getName());
            QueryResult queryResult;
            try {
                queryResult = connection.getBinding().query(query.toString());
                /*} catch (RemoteException e) {
                	ModelBuildingException ce = new ModelBuildingException(e.getCause());
                	throw ce;
                */} catch (Exception e) {
                // throw new ModelBuildingException(e);
                result = -1;
                return result;
            }
            result = queryResult.getSize();
        }
        return result;
    }

    /**
     * Add the colums to the relational table
     * 
     * @param sfo metadata about a salesforce object to be modeled as a table
     * @param newTable
     * @throws ModelBuildingException
     */
    private void addColumnsToTable( SalesforceObject sfo,
                                    BaseTable newTable ) throws ModelBuildingException {
        boolean hasUpdateableColumn = false;
        SalesforceField[] fields = sfo.getFields();
        for (int i = 0; i < fields.length; i++) {
            SalesforceField field = fields[i];

            if (supressAuditFields && field.isAuditField()) {
                continue;
            }

            Column column = RelationalFactory.eINSTANCE.createColumn();
            newTable.getColumns().add(column);
            column.setName(NameUtil.normalizeName(field.getName()));
            column.setNameInSource(field.getName());
            column.setLength(field.getLength());
            if (field.isUpdateable()) {
                column.setUpdateable(true);
                hasUpdateableColumn = true;
            }

            if (field.isCustom()) {
                exManager.setColumnCustom(column, Boolean.TRUE);
            }
            if (field.isCalculated()) {
                exManager.setColumnCalculated(column, Boolean.TRUE);
            }
            if (field.isDefaultedOnCreate()) {
                exManager.setColumnDefaultedOnCreate(column, Boolean.TRUE);
            }
            setColumnType(field, column);

            if (field.isPrimaryKey()) {
                column.setNullable(com.metamatrix.metamodels.relational.NullableType.NO_NULLS_LITERAL);
                column.setDefaultValue("Generated upon creation"); //$NON-NLS-1$
                PrimaryKey pKey = RelationalFactory.eINSTANCE.createPrimaryKey();
                newTable.setPrimaryKey(pKey);
                pKey.getColumns().add(column);
                pKey.setName(NameUtil.normalizeName(field.getName()) + "_PK"); //$NON-NLS-1$
            }
        }

        // A salesforceobject might support updates,
        // but if none of the columns do, then it doesn't
        if (!hasUpdateableColumn) {
            newTable.setSupportsUpdate(false);
        }
    }

    private void setColumnType( SalesforceField field,
                                Column column ) throws ModelBuildingException {
        DatatypeManager dtMgr = ModelerCore.getBuiltInTypesManager();
        if (null == dtMgr) {
            return;
        }
        String fieldType = field.getType();
        try {

            if (fieldType.equals(SalesforceField.STRING_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.STRING_TYPE);
            } else if (fieldType.equals(SalesforceField.PICKLIST_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                if (field.isRestrictedPicklist()) {
                    column.setNativeType(SalesforceField.RESTRICTED_PICKLIST_TYPE);
                } else {
                    column.setNativeType(SalesforceField.PICKLIST_TYPE);
                }
                exManager.setAllowedColumnValues(column, field.getAllowedValues());
            } else if (fieldType.equals(SalesforceField.MULTIPICKLIST_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                if (field.isRestrictedPicklist()) {
                    column.setNativeType(SalesforceField.RESTRICTED_MULTISELECT_PICKLIST_TYPE);
                } else {
                    column.setNativeType(SalesforceField.MULTIPICKLIST_TYPE);
                }
                exManager.setAllowedColumnValues(column, field.getAllowedValues());
            } else if (fieldType.equals(SalesforceField.COMBOBOX_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.COMBOBOX_TYPE);
            } else if (fieldType.equals(SalesforceField.REFERENCE_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.ID_TYPE);
            } else if (fieldType.equals(SalesforceField.BASE64_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.BASE64_BINARY));
                column.setNativeType(SalesforceField.BASE64_TYPE);
                // field.getByteLength();
            } else if (fieldType.equals(SalesforceField.BOOLEAN_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.BOOLEAN));
                column.setNativeType(SalesforceField.BOOLEAN_TYPE);
            } else if (fieldType.equals(SalesforceField.CURRENCY_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DOUBLE));
                column.setNativeType(SalesforceField.CURRENCY_TYPE);
                column.setCurrency(true);
                column.setScale(field.getScale());
                column.setPrecision(field.getPrecision());
            } else if (fieldType.equals(SalesforceField.TEXTAREA_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.TEXTAREA_TYPE);
                column.setLength(field.getLength());
                column.setSearchability(SearchabilityType.UNSEARCHABLE_LITERAL);
            } else if (fieldType.equals(SalesforceField.INT_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.INT));
                column.setNativeType(SalesforceField.INT_TYPE);
                column.setPrecision(field.getDigits());
            } else if (fieldType.equals(SalesforceField.DOUBLE_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DOUBLE));
                column.setNativeType(SalesforceField.DOUBLE_TYPE);
                column.setPrecision(field.getPrecision());
                column.setScale(field.getScale());
            } else if (fieldType.equals(SalesforceField.PERCENT_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DOUBLE));
                column.setNativeType(SalesforceField.PERCENT_TYPE);
                column.setPrecision(field.getPrecision());
                column.setScale(field.getScale());
            } else if (fieldType.equals(SalesforceField.PHONE_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.PHONE_TYPE);
            } else if (fieldType.equals(SalesforceField.ID_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.ID_TYPE);
            } else if (fieldType.equals(SalesforceField.DATE_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DATE));
                column.setNativeType(SalesforceField.DATE_TYPE);
            } else if (fieldType.equals(SalesforceField.DATETIME_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DATE_TIME));
                column.setNativeType(SalesforceField.DATETIME_TYPE);
            } else if (fieldType.equals(SalesforceField.URL_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.URL_TYPE);
            } else if (fieldType.equals(SalesforceField.EMAIL_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.EMAIL_TYPE);
            } else if (fieldType.equals(SalesforceField.ANYTYPE_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.ANYTYPE_TYPE);
            }
        } catch (ModelerCoreException e) {
            ModelBuildingException ex = new ModelBuildingException(e);
            throw ex;
        } catch (Throwable e) {
            ModelBuildingException ex = new ModelBuildingException(e);
            throw ex;
        }
    }

    /**
     * Create the primary-foreign key relationships between the tables.
     */
    private void createRelationships() {
        Iterator iter = relationships.iterator();
        while (iter.hasNext()) {
            Relationship relation = (Relationship)iter.next();
            if (supressAuditFields && relation.relatesToAuditField()) {
                continue;
            }

            ForeignKey fKey = RelationalFactory.eINSTANCE.createForeignKey();

            // Get the parent table
            BaseTable parent = (BaseTable)tablesByName.get(relation.getParentTable());
            PrimaryKey pKey = parent.getPrimaryKey();
            if (null == pKey) {
                throw new RuntimeException("ERROR !!primary key column not found!!"); //$NON-NLS-1$
            }
            // Set the foreign key's primary key
            fKey.setUniqueKey(pKey);

            // Get the child table.
            BaseTable child = (BaseTable)tablesByName.get(relation.getChildTable());

            // Add the foreign key to the child table
            child.getForeignKeys().add(fKey);
            relation.getForeignKeyField();

            // Find the foreign key column.
            List columns = child.getColumns();
            Iterator colIter = columns.iterator();
            Column col = null;
            while (colIter.hasNext()) {
                Column c = (Column)colIter.next();
                if (c.getName().equals(relation.getForeignKeyField())) {
                    col = c;
                    break;
                }
            }
            if (null == col) throw new RuntimeException(
                                                        "ERROR !!foreign key column not found!! " + child.getName() + relation.getForeignKeyField()); //$NON-NLS-1$

            // set the name and the acutual column.
            fKey.setName("FK_" + parent.getName() + "_" + col.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            fKey.getColumns().add(col);
        }
    }
}
