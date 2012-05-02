/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

import static com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants.NAMESPACE_PROVIDER;
import static com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants.SF_Column.PICKLIST_VALUES;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants.SF_Column;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants.SF_Table;
import com.metamatrix.modeler.modelgenerator.salesforce.model.Relationship;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceField;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceObject;
import com.metamatrix.modeler.modelgenerator.salesforce.util.ModelBuildingException;
import com.metamatrix.modeler.modelgenerator.salesforce.util.NameUtil;
import com.sforce.soap.partner.QueryResult;

public class RelationalModelgenerator {

    private IProgressMonitor monitor;

    private final ModelObjectExtensionAssistant assistant;
    
    private final ModelExtensionDefinition definition;
    
    // The relationships to create between salesforce objects
    private List relationships;

    // used to look up tables in order to create the relationships
    private Map tablesByName;

    private SalesforceImportWizardManager wizardManager;

    /**
     * @param salesforceImportWizardManager
     * @param monitor
     */
    public RelationalModelgenerator( SalesforceImportWizardManager wizardManager,
                                     IProgressMonitor monitor ) {
        this.wizardManager = wizardManager;
        this.monitor = monitor;
        relationships = new ArrayList();
        tablesByName = new HashMap();

        // the assistant should be found in the registry
        this.assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                       .getRegistry()
                                                                       .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());

        if (this.assistant == null) {
            throw new IllegalStateException(Messages.getString(I18nUtil.getPropertyPrefix(getClass())
                    + "modelExtensionAssistantNotFound")); //$NON-NLS-1$
        }

        this.definition = this.assistant.getModelExtensionDefinition();

        if (this.definition == null) {
            throw new IllegalStateException(Messages.getString(I18nUtil.getPropertyPrefix(getClass())
                    + "modelExtensionDefinitionNotFound")); //$NON-NLS-1$
        }
    }

    /**
     * Create the relational model from the salesforce metadata
     * 
     * @param resource the file to contain the relational model.
     * @throws ModelBuildingException
     * @throws ModelerCoreException 
     */
    public void createRelationalModel( ModelResource modelResource, Resource resource ) throws Exception {
        // Create the model annotation, the top level object in our of our models and
        // set some of its attributes
        ModelAnnotation annotation = CoreFactory.eINSTANCE.createModelAnnotation();
        annotation.setModelType(ModelType.PHYSICAL_LITERAL);
        annotation.setPrimaryMetamodelUri(RelationalPackage.eINSTANCE.getNsURI());
        resource.getContents().add(annotation);
        
        // Create a schema object in the relational model.
        Schema schema = RelationalFactory.eINSTANCE.createSchema();
        schema.setName(NAMESPACE_PROVIDER.getNamespacePrefix());
        schema.setNameInSource(NAMESPACE_PROVIDER.getNamespacePrefix());
        resource.getContents().add(schema);
        
        // add Salesforce MED
        this.assistant.saveModelExtensionDefinition(modelResource);

        // Loop over the salesforce metadata creating tables and columns
        Object[] objects = wizardManager.getDataModel().getSalesforceObjects();
        for (int i = 0; i < objects.length; i++) {
            SalesforceObject sfo = (SalesforceObject)objects[i];
            if (sfo.isSelected() && !monitor.isCanceled()) {
                monitor.subTask("Creating " + sfo.getName() + " table"); //$NON-NLS-1$ //$NON-NLS-2$
                addTableToModel(sfo, schema);
                monitor.worked(1);
            }
        }

        // Create the relations between the tables. This is done after all
        // the tables are built because you can't put a foreign key into a
        // table that might not have been created yet.
        if (!monitor.isCanceled()) {
            createRelationships();
        }

        if (wizardManager.isGenerateUpdated() && !monitor.isCanceled()) {
            generateGetUpdated(schema);
        }

        if (wizardManager.isGenerateDeleted() && !monitor.isCanceled()) {
            generateGetDeleted(schema);
        }
    }

    /**
     * Create a relational table from salesforce metadata
     * 
     * @param sfo metadata about a salesforce object to be modeled as a table
     * @param schema the relational schema that contains the tables
     * @throws ModelBuildingException
     * @throws ModelerCoreException 
     */
    private void addTableToModel( SalesforceObject sfo,
                                  Schema schema ) throws ModelBuildingException {
        BaseTable newTable = RelationalFactory.eINSTANCE.createBaseTable();

        this.relationships.addAll(sfo.getSelectedRelationships());
        this.tablesByName.put(sfo.getName(), newTable);
        newTable.setSchema(schema);

        if (wizardManager.isSetNameAsLabel() && null != sfo.getLabel()) {
            newTable.setName(NameUtil.normalizeName(sfo.getLabel()));
        } else {
            newTable.setName(NameUtil.normalizeName(sfo.getName()));
        }
        newTable.setNameInSource(sfo.getName());

        if (wizardManager.getSupressCollectCardinalities()) {
            newTable.setCardinality(getCardinality(sfo));
        }

        newTable.setSupportsUpdate(sfo.isUpdateable());

        // update extension property values if different from default value
        try {
            setInitialPropertyValue(newTable, SF_Table.CUSTOM, Boolean.toString(sfo.isCustom()));
            setInitialPropertyValue(newTable, SF_Table.SUPPORTS_CREATE, Boolean.toString(sfo.isCreateable()));
            setInitialPropertyValue(newTable, SF_Table.SUPPORTS_DELETE, Boolean.toString(sfo.isDeleteable()));
            setInitialPropertyValue(newTable, SF_Table.SUPPORTS_MERGE, Boolean.toString(sfo.isMergeable()));
            setInitialPropertyValue(newTable, SF_Table.SUPPORTS_QUERY, Boolean.toString(sfo.isQueryable()));
            setInitialPropertyValue(newTable, SF_Table.SUPPORTS_REPLICATE, Boolean.toString(sfo.isReplicateable()));
            setInitialPropertyValue(newTable, SF_Table.SUPPORTS_RETRIEVE, Boolean.toString(sfo.isRetrieveable()));
            setInitialPropertyValue(newTable, SF_Table.SUPPORTS_SEARCH, Boolean.toString(sfo.isSearchable()));
        } catch (Exception e) {
            throw new ModelBuildingException(e);
        }

        // add columns to the new table
        addColumnsToTable(sfo, newTable);
    }

    private void setInitialPropertyValue(EObject eObject, String simplePropertyId, String expectedValue) throws Exception {
        String propId = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, simplePropertyId);
        String currentValue = this.assistant.getPropertyValue(eObject, propId);

        if (!CoreStringUtil.equals(expectedValue, currentValue)) {
            this.assistant.setPropertyValue(eObject, propId, expectedValue);
        }
    }

    private int getCardinality( SalesforceObject sfo ) {
        int result = 0;
        try {
            if (sfo.isQueryable()) {
                StringBuffer query = new StringBuffer();
                query.append("SELECT COUNT() FROM "); //$NON-NLS-1$
                query.append(sfo.getName());
                QueryResult queryResult;
                queryResult = wizardManager.getConnection().getBinding().query(query.toString());
                result = queryResult.getSize();
            }
        } catch (Throwable t) {
            // Failure here should not stop the model from being created.
            String pattern = Messages.getString("RelationalModelgenerator.error.calculating.cardinalities"); //$NON-NLS-1$
            String message = MessageFormat.format(pattern, new Object[] {sfo.getName()});
            IStatus status = new org.eclipse.core.runtime.Status(IStatus.INFO, Activator.PLUGIN_ID, message);
            Activator.getDefault().getLog().log(status);
        }
        return result;
    }

    /**
     * Add the colums to the relational table
     * 
     * @param sfo metadata about a salesforce object to be modeled as a table
     * @param newTable
     * @throws ModelBuildingException
     * @throws ModelerCoreException 
     */
    private void addColumnsToTable( SalesforceObject sfo,
                                    BaseTable newTable ) throws ModelBuildingException {
        boolean hasUpdateableColumn = false;
        SalesforceField[] fields = sfo.getFields();
        for (int i = 0; i < fields.length; i++) {
            SalesforceField field = fields[i];

            if (!wizardManager.isModelAuditFields() && field.isAuditField()) {
                continue;
            }

            Column column = RelationalFactory.eINSTANCE.createColumn();
            newTable.getColumns().add(column);
            if (wizardManager.isSetNameAsLabel() && null != field.getLabel()) {
                column.setName(NameUtil.normalizeName(field.getLabel()));
            } else {
                column.setName(NameUtil.normalizeName(field.getName()));
            }
            column.setNameInSource(field.getName());
            column.setLength(field.getLength());
            if (field.isUpdateable()) {
                column.setUpdateable(true);
                hasUpdateableColumn = true;
            }
            

            // update extension property values if different from default value
            try {
                setInitialPropertyValue(column, SF_Column.CALCULATED, Boolean.toString(field.isCalculated()));
                setInitialPropertyValue(column, SF_Column.CUSTOM, Boolean.toString(field.isCustom()));
                setInitialPropertyValue(column, SF_Column.DEFAULTED, Boolean.toString(field.isDefaultedOnCreate()));
            } catch (Exception e) {
                throw new ModelBuildingException(e);
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

            if (sfo.isQueryable() && wizardManager.isCollectColumnDistinctValue()) {
                int distinctValues = getDistinctValueCount(sfo.getName(), field.getName());
                column.setDistinctValueCount(distinctValues);
            }
        }

        // A salesforceobject might say it supports updates,
        // but if none of the columns do, then it doesn't
        if (!hasUpdateableColumn) {
            newTable.setSupportsUpdate(false);
        }
    }

    private int getDistinctValueCount( String objectName,
                                       String columnName ) {
        int resultSize = 0;
        try {
            StringBuffer query = new StringBuffer();
            query.append("SELECT "); //$NON-NLS-1$
            query.append(columnName);
            query.append(" FROM "); //$NON-NLS-1$
            query.append(objectName);
            QueryResult queryResult;
            queryResult = wizardManager.getConnection().getBinding().query(query.toString());
            resultSize = queryResult.getSize();
        } catch (Throwable t) {
            // Failure here should not stop the model from being created.
            String pattern = Messages.getString("RelationalModelgenerator.error.calculating.column.distinct"); //$NON-NLS-1$
            String message = MessageFormat.format(pattern, new Object[] {objectName, columnName});
            IStatus status = new org.eclipse.core.runtime.Status(IStatus.INFO, Activator.PLUGIN_ID, message);
            Activator.getDefault().getLog().log(status);
        }
        return resultSize;
    }

    /**
     * @param queryResult
     * @param values
     */
    // private void getUniqueValues( QueryResult queryResult,
    // Set values ) {
    // SObject[] results = queryResult.getRecords();
    // if (null != results) {
    // for (int i = 0; i < results.length; i++) {
    // SObject object = results[i];
    // values.add(object.get_any()[0].getValue());
    // }
    // }
    // }

    private String createPickListString( List<String> allowedValues ) {
        StringBuilder picklistValues = new StringBuilder();
        Iterator iter = allowedValues.iterator();

        while (iter.hasNext()) {
            picklistValues.append((String)iter.next());

            if (iter.hasNext()) {
                picklistValues.append(',');
            }
        }

        return picklistValues.toString();
    }

    private void setColumnType( SalesforceField field,
                                Column column ) throws ModelBuildingException {
        DatatypeManager dtMgr = ModelerCore.getBuiltInTypesManager();

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

                setInitialPropertyValue(column, PICKLIST_VALUES, createPickListString(field.getAllowedValues()));
            } else if (fieldType.equals(SalesforceField.MULTIPICKLIST_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                if (field.isRestrictedPicklist()) {
                    column.setNativeType(SalesforceField.RESTRICTED_MULTISELECT_PICKLIST_TYPE);
                } else {
                    column.setNativeType(SalesforceField.MULTIPICKLIST_TYPE);
                }

                setInitialPropertyValue(column, PICKLIST_VALUES, createPickListString(field.getAllowedValues()));
            } else if (fieldType.equals(SalesforceField.COMBOBOX_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.COMBOBOX_TYPE);
            } else if (fieldType.equals(SalesforceField.REFERENCE_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
                column.setNativeType(SalesforceField.ID_TYPE);
            } else if (fieldType.equals(SalesforceField.BASE64_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.BASE64_BINARY));
                column.setNativeType(SalesforceField.BASE64_TYPE);
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
            } else if (fieldType.equals(SalesforceField.TIME_TYPE)) {
                column.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.TIME));
                column.setNativeType(SalesforceField.TIME_TYPE);
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
        while (iter.hasNext() && !monitor.isCanceled()) {
            monitor.subTask(Messages.getString("RelationalModelgenerator.generating.relationships")); //$NON-NLS-1$
            Relationship relation = (Relationship)iter.next();
            if (!wizardManager.isModelAuditFields() && relation.relatesToAuditField()) {
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
                if (c.getNameInSource().equals(relation.getForeignKeyField())) {
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

    private void generateGetUpdated( Schema schema ) throws ModelBuildingException {
        try {
            DatatypeManager dtMgr = ModelerCore.getBuiltInTypesManager();
            Procedure getUpdatedProc = RelationalFactory.eINSTANCE.createProcedure();
            getUpdatedProc.setSchema(schema);
            getUpdatedProc.setName("GetUpdated"); //$NON-NLS-1$
            getUpdatedProc.setNameInSource("GetUpdated"); //$NON-NLS-1$

            addCommonProcParams(getUpdatedProc, dtMgr);

            addLatestDateCoveredParam(getUpdatedProc, dtMgr);

            addCommonResult(getUpdatedProc, dtMgr);

        } catch (ModelerCoreException e) {
            ModelBuildingException ex = new ModelBuildingException(e);
            throw ex;
        }
    }

    private void generateGetDeleted( Schema schema ) throws ModelBuildingException {
        try {
            DatatypeManager dtMgr = ModelerCore.getBuiltInTypesManager();
            Procedure getUpdatedProc = RelationalFactory.eINSTANCE.createProcedure();
            getUpdatedProc.setSchema(schema);
            getUpdatedProc.setName("GetDeleted"); //$NON-NLS-1$
            getUpdatedProc.setNameInSource("GetDeleted"); //$NON-NLS-1$

            addCommonProcParams(getUpdatedProc, dtMgr);

            ProcedureParameter earliestDateAvailable = RelationalFactory.eINSTANCE.createProcedureParameter();
            getUpdatedProc.getParameters().add(earliestDateAvailable);
            earliestDateAvailable.setName("EarliestDateAvailable"); //$NON-NLS-1$
            earliestDateAvailable.setNameInSource("EarliestDateAvailable"); //$NON-NLS-1$
            earliestDateAvailable.setDirection(DirectionKind.OUT_LITERAL);
            earliestDateAvailable.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DATE_TIME));

            addLatestDateCoveredParam(getUpdatedProc, dtMgr);

            ProcedureResult result = addCommonResult(getUpdatedProc, dtMgr);

            Column deletedDate = RelationalFactory.eINSTANCE.createColumn();
            deletedDate.setName("DeletedDate"); //$NON-NLS-1$
            deletedDate.setNameInSource("DeletedDate"); //$NON-NLS-1$
            deletedDate.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DATE_TIME));
            result.getColumns().add(deletedDate);

        } catch (ModelerCoreException e) {
            ModelBuildingException ex = new ModelBuildingException(e);
            throw ex;
        }
    }

    private void addCommonProcParams( Procedure proc,
                                      DatatypeManager dtMgr ) throws ModelerCoreException {
        ProcedureParameter objectName = RelationalFactory.eINSTANCE.createProcedureParameter();
        proc.getParameters().add(objectName);
        objectName.setName("ObjectName"); //$NON-NLS-1$
        objectName.setNameInSource("ObjectName"); //$NON-NLS-1$
        objectName.setDirection(DirectionKind.IN_LITERAL);
        objectName.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));

        ProcedureParameter startDate = RelationalFactory.eINSTANCE.createProcedureParameter();
        proc.getParameters().add(startDate);
        startDate.setName("StartDate"); //$NON-NLS-1$
        startDate.setNameInSource("StartDate"); //$NON-NLS-1$
        startDate.setDirection(DirectionKind.IN_LITERAL);
        startDate.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.TIMESTAMP));

        ProcedureParameter endDate = RelationalFactory.eINSTANCE.createProcedureParameter();
        proc.getParameters().add(endDate);
        endDate.setName("EndDate"); //$NON-NLS-1$
        endDate.setNameInSource("EndDate"); //$NON-NLS-1$
        endDate.setDirection(DirectionKind.IN_LITERAL);
        endDate.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.TIMESTAMP));
    }

    private void addLatestDateCoveredParam( Procedure proc,
                                            DatatypeManager dtMgr ) throws ModelerCoreException {
        ProcedureParameter latestDateCovered = RelationalFactory.eINSTANCE.createProcedureParameter();
        proc.getParameters().add(latestDateCovered);
        latestDateCovered.setName("LatestDateCovered"); //$NON-NLS-1$
        latestDateCovered.setNameInSource("LatestDateCovered"); //$NON-NLS-1$
        latestDateCovered.setDirection(DirectionKind.OUT_LITERAL);
        latestDateCovered.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.DATE_TIME));
    }

    private ProcedureResult addCommonResult( Procedure proc,
                                             DatatypeManager dtMgr ) throws ModelerCoreException {
        ProcedureResult result = RelationalFactory.eINSTANCE.createProcedureResult();
        result.setProcedure(proc);
        result.setName("Result"); //$NON-NLS-1$
        result.setNameInSource("Result"); //$NON-NLS-1$

        Column id = RelationalFactory.eINSTANCE.createColumn();
        id.setName("ID"); //$NON-NLS-1$
        id.setNameInSource("ID"); //$NON-NLS-1$
        id.setLength(18);
        id.setType(dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
        result.getColumns().add(id);
        return result;
    }

}
