/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.MultiplicityKind;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.ProcedureUpdateCount;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalPlugin;

/**
 * Class provides building EMF Relational Metamodel objects from Relational Model objects
 *
 * @since 8.0
 */
public class RelationalModelFactory implements RelationalConstants {
    /**
     * 
     */
    public static final String RELATIONAL_PACKAGE_URI = RelationalPackage.eNS_URI;
    /**
     * 
     */
    public static final RelationalFactory FACTORY = RelationalFactory.eINSTANCE;
    
    /**
     * 
     */
    public static RelationalModelFactory INSTANCE = new RelationalModelFactory();
    
    private DatatypeProcessor datatypeProcessor;
    
    private Map<RelationalForeignKey, BaseTable> fkTableMap = new HashMap<RelationalForeignKey, BaseTable>();
    private Collection<RelationalIndex> indexes = new ArrayList<RelationalIndex>();

    /**
     * 
     */
    public RelationalModelFactory() {
        super();
        this.datatypeProcessor = new DatatypeProcessor();
    }
    
    private RelationalModelExtensionAssistant getExtensionAssistant() {
    	final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        final String prefix = RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
        final RelationalModelExtensionAssistant assistant = (RelationalModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
        
        return assistant;
    }
    
    /**
     * Creates a relational model given a <code>IPath</code> location and a model name
     * 
     * @param location the container location of the model
     * @param modelName the model name
     * @return the ModelResource
     * @throws ModelWorkspaceException if error creating model
     */
    public ModelResource createRelationalModel( IPath location, String modelName) throws ModelWorkspaceException {
    	final ModelResource resrc = ModelerCore.createModelResource(location, modelName);
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
        ModelerCore.getModelEditor().getAllContainers(resrc.getEmfResource());
        
        return resrc;
    }
    
    /**
     * Creates a relational model given a <code>IContainer</code> location (Project or Folder) and a model name
     * 
     * @param container the resource container
     * @param modelName the model name
     * @return the model resource
     * @throws ModelWorkspaceException if problems creating model
     */
    public ModelResource createRelationalModel( IContainer container, String modelName) throws ModelWorkspaceException {
        IProject project = container.getProject();
        IPath relativeModelPath = container.getFullPath().removeFirstSegments(1).append(modelName);
        final IFile modelFile = project.getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
        ModelerCore.getModelEditor().getAllContainers(resrc.getEmfResource());
        
        return resrc;
    }
    
    
    /**
     * @param modelResource the model resource
     * @param model the relational model structure
     * @param progressMonitor progress monitor
     */
    public void build(ModelResource modelResource, RelationalModel model, IProgressMonitor progressMonitor) {

        try {
            RelationalModelFactory builder = new RelationalModelFactory();

            final RelationalModelExtensionAssistant assistant = getExtensionAssistant();

            try {
                assistant.applyMedIfNecessary(modelResource.getUnderlyingResource());
            } catch (Exception e) {
            	RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
            }
            
            builder.buildFullModel(model, modelResource, progressMonitor);
            
            modelResource.save(new NullProgressMonitor(), true);
        } catch (ModelerCoreException e) {
            RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }
    
    /**
     * @param model the relational model structure
     * @param modelResource the model resource
     * @param progressMonitor progress monitor
     * @throws ModelerCoreException if problems building model
     */
    public void buildFullModel(RelationalModel model, ModelResource modelResource, IProgressMonitor progressMonitor) throws ModelerCoreException {
        
        progressMonitor.setTaskName(Messages.relationalModelFactory_creatingModelChildren);
        for( RelationalReference child : model.getChildren() ) {
            int processType = child.getProcessType();
            
            switch(processType) {
                case RelationalReference.IGNORE : {
                    // Do nothing
                } break;
                case RelationalReference.CREATE_ANYWAY : {
                     buildObject(child, modelResource, progressMonitor);     
                } break;
                case RelationalReference.CREATE_UNIQUE_NAME : {
                    // Currently NOT implemented
                } break;
                case RelationalReference.REPLACE : {
                    deleteChildWithName(modelResource, child, progressMonitor);
                    buildObject(child, modelResource, progressMonitor);
                } break;
            }
            progressMonitor.worked(1);
        }
        
        progressMonitor.setTaskName(Messages.relationalModelFactory_creatingForeigneKeys);
        progressMonitor.worked(1);
        for( RelationalForeignKey fkRef : fkTableMap.keySet()) {
            createForeignKey(fkRef, fkTableMap.get(fkRef), modelResource);
        }
        
        progressMonitor.setTaskName(Messages.relationalModelFactory_creatingIndexes);
        progressMonitor.worked(1);
        for( RelationalIndex indexRef : indexes ) {
            EObject index = createIndex(indexRef, modelResource);
            modelResource.getEmfResource().getContents().add(index);
        }
    }
    
    private void deleteChildWithName(ModelResource targetResource, RelationalReference ref, IProgressMonitor progressMonitor) throws ModelerCoreException {
        progressMonitor.setTaskName(NLS.bind(Messages.relationalModelFactory_replacingModelObject, ref.getName()));
        
        Collection<EObject> existingChildren = targetResource.getEmfResource().getContents();
        EObject childToDelete = null;
        for( EObject child : existingChildren ) {
            String name = ModelerCore.getModelEditor().getName(child);
            
            if( name != null && name.equalsIgnoreCase(ref.getName()) ) {
                childToDelete = child;
                break;
            }
        }
        if( childToDelete != null ) {
            ModelerCore.getModelEditor().delete(childToDelete);
        }
    }
    
    /**
     * @param obj the relational model object
     * @param modelResource the model resource
     * @param progressMonitor progress monitor
     * @return the new model object
     * @throws ModelWorkspaceException if problems building model
     */
    public EObject buildObject( RelationalReference obj, ModelResource modelResource, IProgressMonitor progressMonitor) throws ModelWorkspaceException {
        EObject newEObject = null;
        
        progressMonitor.setTaskName(NLS.bind(Messages.relationalModelFactory_creatingModelChild, obj.getName()));
        switch (obj.getType()) {
            case TYPES.MODEL: {
                // NOOP. Shouldn't get here
            } break;
            case TYPES.SCHEMA: {
             // NOOP. Shouldn't get here
            } break;
            case TYPES.CATALOG: {
             // NOOP. Shouldn't get here
            } break;
            case TYPES.TABLE: {
                EObject baseTable = createBaseTable(obj, modelResource);
                modelResource.getEmfResource().getContents().add(baseTable);
                applyTableExtensionProperties((RelationalTable)obj, (BaseTable)baseTable, false);
                
                // In the case of the new object wizards, users can create Indexes while creating a table
                // So just walk these and add them to the model too
                for( RelationalIndex index : ((RelationalTable)obj).getIndexes() ) {
                	EObject newIndex = createIndex(index, modelResource);
                	modelResource.getEmfResource().getContents().add(newIndex);
                }
            } break;
            case TYPES.VIEW: {
                EObject view = createView(obj, modelResource);
                modelResource.getEmfResource().getContents().add(view);
            } break;
            case TYPES.PROCEDURE: {
                EObject procedure = createProcedure(obj, modelResource);
                modelResource.getEmfResource().getContents().add(procedure);
                applyProcedureExtensionProperties((RelationalProcedure)obj,(Procedure) procedure);
            } break;
            case TYPES.INDEX: {
                indexes.add((RelationalIndex)obj);
            } break;
            
            case TYPES.UNDEFINED:
            default: {
                RelationalPlugin.Util.log(IStatus.WARNING, 
                		NLS.bind(Messages.relationalModelFactory_unknown_object_type_0_cannot_be_processed, obj.getName()));
            } break;
        }
        
        return newEObject;
    }
    
    
    /**
     * @param ref the relational model object
     * @param modelResource the model resource
     * @return the new object
     */
    public EObject createBaseTable( final RelationalReference ref, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalTable.class, ref);

        RelationalTable tableRef = (RelationalTable)ref;
        // Create and Set Name
        BaseTable baseTable = FACTORY.createBaseTable();
        baseTable.setName(tableRef.getName());
        baseTable.setSupportsUpdate(tableRef.getSupportsUpdate());
        baseTable.setMaterialized(tableRef.isMaterialized());
        baseTable.setNameInSource(tableRef.getNameInSource());
        baseTable.setSystem(tableRef.isSystem());
        baseTable.setCardinality(tableRef.getCardinality());
        
        // Set Description
        if( tableRef.getDescription() != null ) {
            createAnnotation(baseTable, tableRef.getDescription(), modelResource);
        }
        
        // Add Columns
        for( RelationalColumn column : tableRef.getColumns()) {
            createColumn(column, baseTable, modelResource);
        }
        
        // Add Primary Keys
        // Add Columns
        RelationalPrimaryKey pk = tableRef.getPrimaryKey();
        if( pk != null ) {
            createPrimaryKey(pk, baseTable, modelResource);
        }
        
        RelationalUniqueConstraint uc = tableRef.getUniqueContraint();
        if( uc != null){
            createUniqueConstraint(uc, baseTable, modelResource);
        }
        
        for( RelationalAccessPattern ap : tableRef.getAccessPatterns()) {
            createAccessPattern(ap, baseTable, modelResource);
        }
        
        for( RelationalForeignKey fk : tableRef.getForeignKeys()) {
            fkTableMap.put(fk, baseTable);
            //createForeignKey(fk, baseTable, modelResource);
        }
        
        return baseTable;
    }
    
    /**
     * @param ref the relational model object
     * @param modelResource the model resource
     * @return the new object
     */
    public EObject createView( final RelationalReference ref, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalView.class, ref);

        RelationalView viewRef = (RelationalView)ref;
        // Create and Set Name
        View view = FACTORY.createView();
        view.setName(viewRef.getName());
        view.setSupportsUpdate(viewRef.getSupportsUpdate());
        view.setMaterialized(viewRef.isMaterialized());
        view.setNameInSource(viewRef.getNameInSource());
        view.setSystem(viewRef.isSystem());
        
        // Set Description
        if( viewRef.getDescription() != null ) {
            createAnnotation(view, viewRef.getDescription(), modelResource);
        }
        
        // Add Columns
        for( RelationalColumn column : viewRef.getColumns()) {
            createColumn(column, view, modelResource);
        }
  
        
        for( RelationalAccessPattern uc : viewRef.getAccessPatterns()) {
            createAccessPattern(uc, view, modelResource);
        }

        return view;
    }
    
    /**
     * @param ref the relational model object
     * @param baseTable the table
     * @param modelResource the model resource
     * @return the new object
     */
    public EObject createColumn( RelationalReference ref, Table baseTable, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalColumn.class, ref);
        
        RelationalColumn columnRef = (RelationalColumn)ref;
        
        Column column = FACTORY.createColumn();
        column.setOwner(baseTable);
        column.setName(columnRef.getName());
        column.setNameInSource(columnRef.getNameInSource());
        column.setAutoIncremented(columnRef.isAutoIncremented());
        column.setCaseSensitive(columnRef.isCaseSensitive());
        column.setCharacterSetName(columnRef.getCharacterSetName());
        column.setCollationName(columnRef.getCollationName());
        column.setCurrency(columnRef.isCurrency());
        column.setDefaultValue(columnRef.getDefaultValue());
        column.setDistinctValueCount(columnRef.getDistinctValueCount());
        column.setFixedLength(columnRef.isLengthFixed());
        column.setFormat(columnRef.getFormat());
        column.setLength(columnRef.getLength());
        column.setMaximumValue(columnRef.getMaximumValue());
        column.setMinimumValue(columnRef.getMinimumValue());
        column.setNativeType(columnRef.getNativeType());
        column.setNullable(getNullableType(columnRef.getNullable()));
        column.setNullValueCount(columnRef.getNullValueCount());
        column.setPrecision(columnRef.getPrecision());
        column.setRadix(columnRef.getRadix());
        column.setScale(columnRef.getScale());
        column.setSearchability(getSearchabilityType(columnRef.getSearchability()));
        column.setSelectable(columnRef.isSelectable());
        column.setSigned(columnRef.isSigned());
        column.setUpdateable(columnRef.isUpdateable());
        EObject datatype = this.datatypeProcessor.findDatatype(columnRef.getDatatype());
//                        columnRef.getDatatype(), 
//                        columnRef.getLength(),
//                        columnRef.getPrecision(), 
//                        columnRef.getScale(), 
//                        new ArrayList());
        if( datatype != null ) {
            column.setType(datatype);
        }
        
        // Set Description
        if( columnRef.getDescription() != null ) {
            createAnnotation(column, columnRef.getDescription(), modelResource);
        }
        
        return column;
    }
    
    /**
     * @param ref the relational object
     * @param procedureResult the procedure result set
     * @param modelResource the model resource
     * @return the new object
     */
    public EObject createColumn( RelationalReference ref, ProcedureResult procedureResult, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalColumn.class, ref);
        
        RelationalColumn columnRef = (RelationalColumn)ref;
        
        Column column = FACTORY.createColumn();
        column.setOwner(procedureResult);
        column.setName(columnRef.getName());
        column.setNameInSource(columnRef.getNameInSource());
        column.setAutoIncremented(columnRef.isAutoIncremented());
        column.setCaseSensitive(columnRef.isCaseSensitive());
        column.setCharacterSetName(columnRef.getCharacterSetName());
        column.setCollationName(columnRef.getCollationName());
        column.setCurrency(columnRef.isCurrency());
        column.setDefaultValue(columnRef.getDefaultValue());
        column.setDistinctValueCount(columnRef.getDistinctValueCount());
        column.setFixedLength(columnRef.isLengthFixed());
        column.setFormat(columnRef.getFormat());
        column.setLength(columnRef.getLength());
        column.setMaximumValue(columnRef.getMaximumValue());
        column.setMinimumValue(columnRef.getMinimumValue());
        column.setNativeType(columnRef.getNativeType());
        column.setNullable(getNullableType(columnRef.getNullable()));
        column.setNullValueCount(columnRef.getNullValueCount());
        column.setPrecision(columnRef.getPrecision());
        column.setRadix(columnRef.getRadix());
        column.setScale(columnRef.getScale());
        column.setSearchability(getSearchabilityType(columnRef.getSearchability()));
        column.setSelectable(columnRef.isSelectable());
        column.setSigned(columnRef.isSigned());
        column.setUpdateable(columnRef.isUpdateable());
        
        String dType = columnRef.getDatatype();
        if( dType == null || dType.length() == 0) {
            dType = DatatypeProcessor.DEFAULT_DATATYPE;

        }
        EObject datatype = this.datatypeProcessor.findDatatype(dType);

        if( datatype != null ) {
            column.setType(datatype);
            
            String dTypeName = ModelerCore.getModelEditor().getName(datatype);
            
            int datatypeLength = columnRef.getLength();
            if( datatypeLength == 0 && DatatypeProcessor.DEFAULT_DATATYPE.equalsIgnoreCase(dTypeName) ) {
                columnRef.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
            } else {
            	columnRef.setLength(datatypeLength);
            }
        }
        
        // Set Description
        if( columnRef.getDescription() != null ) {
            createAnnotation(column, columnRef.getDescription(), modelResource);
        }
        
        return column;
    }
    
    private NullableType getNullableType(String value) {
        if( NULLABLE.NULLABLE.equalsIgnoreCase(value) ) {
            return NullableType.NULLABLE_LITERAL;
        }
        
        if( NULLABLE.NO_NULLS.equalsIgnoreCase(value) ) {
            return NullableType.NO_NULLS_LITERAL;
        }
        
        return NullableType.NULLABLE_UNKNOWN_LITERAL;
    }
    
    private SearchabilityType getSearchabilityType(String value) {
        if( SEARCHABILITY.UNSEARCHABLE.equalsIgnoreCase(value) ) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        if( SEARCHABILITY.ALL_EXCEPT_LIKE.equalsIgnoreCase(value) ) {
            return SearchabilityType.ALL_EXCEPT_LIKE_LITERAL;
        }
        if( SEARCHABILITY.LIKE_ONLY.equalsIgnoreCase(value) ) {
            return SearchabilityType.LIKE_ONLY_LITERAL;
        }
        
        return SearchabilityType.SEARCHABLE_LITERAL;
    }
    
    private void createPrimaryKey( RelationalReference ref, BaseTable baseTable, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalPrimaryKey.class, ref);
        
        RelationalPrimaryKey pkRef = (RelationalPrimaryKey)ref;
        
        // Create the primary key
        final PrimaryKey primaryKey = FACTORY.createPrimaryKey();
        // Set the reference to the table ..
        primaryKey.setTable(baseTable);
        primaryKey.setName(pkRef.getName());
        primaryKey.setNameInSource(pkRef.getNameInSource());
        
        // Add the columns in the correct order
        final List keyColumns = primaryKey.getColumns();
        for (RelationalColumn relColumn : pkRef.getColumns() ) {
            Column column = getColumn(relColumn.getName(), baseTable);
            if( column != null ) {
                keyColumns.add(column);
            }
        }
        
    }
    
    private void createForeignKey( RelationalReference ref, BaseTable baseTable, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalForeignKey.class, ref);
        
        RelationalForeignKey fkRef = (RelationalForeignKey)ref;
        
        // Create the primary key
        final ForeignKey foreignKey = FACTORY.createForeignKey();
        // Set the reference to the table ..
        foreignKey.setTable(baseTable);
        foreignKey.setName(fkRef.getName());
        foreignKey.setNameInSource(fkRef.getNameInSource());
        foreignKey.setForeignKeyMultiplicity(getMultiplictyKind(fkRef.getForeignKeyMultiplicity()));
        foreignKey.setPrimaryKeyMultiplicity(getMultiplictyKind(fkRef.getPrimaryKeyMultiplicity()));
        
        // Add the columns in the correct order
        final List keyColumns = foreignKey.getColumns();
        for (RelationalColumn relColumn : fkRef.getColumns() ) {
            Column column = getColumn(relColumn.getName(), baseTable);
            if( column != null ) {
                keyColumns.add(column);
            }
        }
        
        BaseTable fkTable = getTable(fkRef.getUniqueKeyTableName(), modelResource);
        String ukRefName = fkRef.getUniqueKeyName();
        
        if( fkTable != null && ukRefName != null ) {
            if( fkTable.getPrimaryKey() != null && fkTable.getPrimaryKey().getName().equalsIgnoreCase(ukRefName)) {
                foreignKey.setUniqueKey(fkTable.getPrimaryKey());
            } else if( fkTable.getUniqueConstraints().isEmpty() ) {
                for( Object key : fkTable.getUniqueConstraints()) {
                    String keyName = ModelerCore.getModelEditor().getName((UniqueKey)key);
                    if( keyName.equalsIgnoreCase(ukRefName) ) {
                        foreignKey.setUniqueKey((UniqueKey)key);
                    }
                }
            }
        }
        
    }
    
    private void createAccessPattern( RelationalReference ref, Table baseTable, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalAccessPattern.class, ref);
        
        RelationalAccessPattern apRef = (RelationalAccessPattern)ref;
        
        // Create the primary key
        final AccessPattern accessPattern = FACTORY.createAccessPattern();
        // Set the reference to the table ..
        accessPattern.setTable(baseTable);
        accessPattern.setName(apRef.getName());
        accessPattern.setNameInSource(apRef.getNameInSource());
        
        // Add the columns in the correct order
        final List keyColumns = accessPattern.getColumns();
        for (RelationalColumn relColumn : apRef.getColumns() ) {
            Column column = getColumn(relColumn.getName(), baseTable);
            if( column != null ) {
                keyColumns.add(column);
            }
        }
        
    }
    
    private void createUniqueConstraint( RelationalReference ref, BaseTable baseTable, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalUniqueConstraint.class, ref);
        
        RelationalUniqueConstraint ucRef = (RelationalUniqueConstraint)ref;
        
        // Create the primary key
        final UniqueConstraint uniqueConstraint = FACTORY.createUniqueConstraint();
        // Set the reference to the table ..
        uniqueConstraint.setTable(baseTable);
        uniqueConstraint.setName(ucRef.getName());
        uniqueConstraint.setNameInSource(ucRef.getNameInSource());
        
        // Add the columns in the correct order
        final List keyColumns = uniqueConstraint.getColumns();
        for (RelationalColumn relColumn : ucRef.getColumns() ) {
            Column column = getColumn(relColumn.getName(), baseTable);
            if( column != null ) {
                keyColumns.add(column);
            }
        }
        
    }
    
    private Column getColumn(String name, Table baseTable) {
        for( Object column : baseTable.getColumns()) {
            if( column instanceof Column && ((Column)column).getName().equalsIgnoreCase(name) ) {
                return (Column)column;
            }
        }
        
        return null;
    }
    
    private BaseTable getTable(String tableName, ModelResource modelResource) {
        try {
            for (EObject eObj : modelResource.getEmfResource().getContents() ) {
                String eObjName = ModelerCore.getModelEditor().getName(eObj);
                if( eObj instanceof BaseTable && eObjName != null && eObjName.equalsIgnoreCase(tableName)) {
                    return (BaseTable)eObj;
                }
            }
        } catch (ModelWorkspaceException e) {
            RelationalPlugin.Util.log(IStatus.ERROR, 
            		NLS.bind(Messages.relationalModelFactory_error_finding_table_named, tableName));
        }

        return null;
    }
    
    
    /**
     * @param ref the procedure object
     * @param modelResource the  model resource
     * @return the object
     */
    public EObject createProcedure( final RelationalReference ref, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalProcedure.class, ref);

        RelationalProcedure procedureRef = (RelationalProcedure)ref;
        // Create and Set Name
        Procedure procedure = FACTORY.createProcedure();
        procedure.setName(procedureRef.getName());
        procedure.setNameInSource(procedureRef.getNameInSource());
        procedure.setFunction(procedureRef.isFunction());
        procedure.setUpdateCount(getUpdateCount(procedureRef.getUpdateCount()));
        

        
        // Set Description
        if( procedureRef.getDescription() != null ) {
            createAnnotation(procedure, procedureRef.getDescription(), modelResource);
        }
        
        // Add Columns
        for( RelationalParameter paramRef : procedureRef.getParameters()) {
            createParameter(paramRef, procedure, modelResource);
        }
        
        if( procedureRef.getResultSet() != null ) {
            createResultSet(procedureRef.getResultSet(), procedure, modelResource);
            
        }
        
        return procedure;
    }
    
    /**
     * @param ref the parameter object
     * @param procedure the parent procedure
     * @param modelResource the  model resource
     * @return the object
     */
    public EObject createParameter( RelationalReference ref, Procedure procedure, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalParameter.class, ref);
        
        RelationalParameter parameterRef = (RelationalParameter)ref;
        
        ProcedureParameter parameter = FACTORY.createProcedureParameter();
        parameter.setProcedure(procedure);
        parameter.setName(parameterRef.getName());
        parameter.setNameInSource(parameterRef.getNameInSource());
        parameter.setDefaultValue(parameterRef.getDefaultValue());
        parameter.setDirection(getDirectionKind(parameterRef.getDirection()));
        parameter.setNativeType(parameterRef.getNativeType());
        parameter.setNullable(getNullableType(parameterRef.getNullable()));
        parameter.setPrecision(parameterRef.getPrecision());
        parameter.setRadix(parameterRef.getRadix());
        parameter.setScale(parameterRef.getScale());
        String dType = parameterRef.getDatatype();
        if( dType == null || dType.length() == 0) {
            dType = DatatypeProcessor.DEFAULT_DATATYPE;

        }
        EObject datatype = this.datatypeProcessor.findDatatype(dType);

        if( datatype != null ) {
            parameter.setType(datatype);
            String dTypeName = ModelerCore.getModelEditor().getName(datatype);
            
            int datatypeLength = parameterRef.getLength();
            if( datatypeLength == 0 && DatatypeProcessor.DEFAULT_DATATYPE.equalsIgnoreCase(dTypeName) ) {
                parameter.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
            } else {
            	parameter.setLength(datatypeLength);
            }
        }
       
        
        return parameter;
    }
    
    protected void applyTableExtensionProperties(RelationalTable tableRef, BaseTable baseTable, boolean isVirtual) {
    	if( isVirtual ) return;
    	
        // Set Extension Properties here
        final RelationalModelExtensionAssistant assistant = getExtensionAssistant();
        if( assistant != null ) {
        	try {
				assistant.setPropertyValue(baseTable, 
						BASE_TABLE_EXT_PROPERTIES.NATIVE_QUERY, 
						tableRef.getNativeQuery() );

			} catch (Exception ex) {
				RelationalPlugin.Util.log(IStatus.ERROR, ex, 
	                	NLS.bind(Messages.relationalModelFactory_error_setting_extension_props_on_0, tableRef.getName()));
			}
        }
    }
    
    protected void applyProcedureExtensionProperties(RelationalProcedure procedureRef, Procedure procedure) {
        // Set Extension Properties here
        final RelationalModelExtensionAssistant assistant = getExtensionAssistant();
        if( assistant != null ) {
        	try {
        	    final Collection<ModelExtensionPropertyDefinition> extProps = assistant.getPropertyDefinitions(procedure);

                for (final ModelExtensionPropertyDefinition propDefn : extProps) {
                    final String id = propDefn.getId();

                    if (PROCEDURE_EXT_PROPERTIES.NON_PREPARED.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.NON_PREPARED,
                                                   Boolean.toString(procedureRef.isNonPrepared()));
                    } else if (PROCEDURE_EXT_PROPERTIES.NATIVE_QUERY.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.NATIVE_QUERY,
                                                   procedureRef.getNativeQuery());
                    } else if (PROCEDURE_EXT_PROPERTIES.DETERMINISTIC.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.DETERMINISTIC,
                                                   Boolean.toString(procedureRef.isDeterministic()));
                    } else if (PROCEDURE_EXT_PROPERTIES.NULL_ON_NULL.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.NULL_ON_NULL,
                                                   Boolean.toString(procedureRef.isReturnsNullOnNull()));
                    } else if (PROCEDURE_EXT_PROPERTIES.VARARGS.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.VARARGS,
                                                   Boolean.toString(procedureRef.isVariableArguments()));
                    } else if (PROCEDURE_EXT_PROPERTIES.AGGREGATE.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.AGGREGATE,
                                                   Boolean.toString(procedureRef.isAggregate()));
                    } else if (PROCEDURE_EXT_PROPERTIES.ALLOWS_DISTINCT.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.ALLOWS_DISTINCT,
                                                   Boolean.toString(procedureRef.isAllowsDistinct()));
                    } else if (PROCEDURE_EXT_PROPERTIES.ALLOWS_ORDER_BY.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.ALLOWS_ORDER_BY,
                                                   Boolean.toString(procedureRef.isAllowsOrderBy()));
                    } else if (PROCEDURE_EXT_PROPERTIES.ANALYTIC.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.ANALYTIC,
                                                   Boolean.toString(procedureRef.isAnalytic()));
                    } else if (PROCEDURE_EXT_PROPERTIES.DECOMPOSABLE.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.DECOMPOSABLE,
                                                   Boolean.toString(procedureRef.isDecomposable()));
                    } else if (PROCEDURE_EXT_PROPERTIES.JAVA_CLASS.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.JAVA_CLASS,
                                                   procedureRef.getJavaClassName());
                    } else if (PROCEDURE_EXT_PROPERTIES.USES_DISTINCT_ROWS.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.USES_DISTINCT_ROWS,
                                                   Boolean.toString(procedureRef.isUseDistinctRows()));
                    } else if (PROCEDURE_EXT_PROPERTIES.JAVA_METHOD.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.JAVA_METHOD,
                                                   procedureRef.getJavaMethodName());
                    } else if (PROCEDURE_EXT_PROPERTIES.UDF_JAR_PATH.equals(id)) {
                        assistant.setPropertyValue(procedure, PROCEDURE_EXT_PROPERTIES.UDF_JAR_PATH, procedureRef.getUdfJarPath());
                    } else if (PROCEDURE_EXT_PROPERTIES.FUNCTION_CATEGORY.equals(id)) {
                        assistant.setPropertyValue(procedure,
                                                   PROCEDURE_EXT_PROPERTIES.FUNCTION_CATEGORY,
                                                   procedureRef.getFunctionCategory());
                    }
                }
			} catch (Exception ex) {
				RelationalPlugin.Util.log(IStatus.ERROR, ex, 
	                	NLS.bind(Messages.relationalModelFactory_error_setting_extension_props_on_0, procedureRef.getName()));
			}
        }
    }
    
    /**
     * @param ref the result set object
     * @param procedure the parent procedure
     * @param modelResource the model resource
     * @return the object
     */
    public EObject createResultSet( RelationalReference ref, Procedure procedure, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalProcedureResultSet.class, ref);
        
        RelationalProcedureResultSet resultSetRef = (RelationalProcedureResultSet)ref;
        
        ProcedureResult result = FACTORY.createProcedureResult();
        result.setProcedure(procedure);
        result.setName(resultSetRef.getName());
        result.setNameInSource(resultSetRef.getNameInSource());
        
        // Set Description
        if( resultSetRef.getDescription() != null ) {
            createAnnotation(result, resultSetRef.getDescription(), modelResource);
        }
        
        // Add Columns
        for( RelationalColumn colRef : resultSetRef.getColumns()) {
            createColumn(colRef, result, modelResource);
        }
        
        return result;
    }
    
    /**
     * @param ref the relational index object
     * @param modelResource the model resource
     * @return the index object
     */
    public EObject createIndex( RelationalReference ref, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalIndex.class, ref);
        
        RelationalIndex indexRef = (RelationalIndex)ref;
        
        Index index = FACTORY.createIndex();
        index.setName(indexRef.getName());
        index.setNameInSource(index.getNameInSource());
        index.setFilterCondition(indexRef.getFilterCondition());
        index.setAutoUpdate(indexRef.isAutoUpdate());
        index.setNullable(indexRef.isNullable());
        index.setUnique(indexRef.isUnique());
        
        // Set Description
        if( indexRef.getDescription() != null ) {
            createAnnotation(index, indexRef.getDescription(), modelResource);
        }
        
        // Add the columns in the correct order
        final List indexColumns = index.getColumns();
        for (RelationalColumn relColumn : indexRef.getColumns() ) {
            BaseTable baseTable = getTable(relColumn.getParent().getName(), modelResource);
            
            Column column = getColumn(relColumn.getName(), baseTable);
            if( column != null ) {
                indexColumns.add(column);
            }
        }
        
        return index;
    }
    
    private DirectionKind getDirectionKind(String value) {
        if( DIRECTION.IN.equalsIgnoreCase(value) ) {
            return DirectionKind.IN_LITERAL;
        }
        if( DIRECTION.IN_OUT.equalsIgnoreCase(value) ) {
            return DirectionKind.INOUT_LITERAL;
        }
        if( DIRECTION.OUT.equalsIgnoreCase(value) ) {
            return DirectionKind.OUT_LITERAL;
        }
        if( DIRECTION.RETURN.equalsIgnoreCase(value) ) {
            return DirectionKind.RETURN_LITERAL;
        }
        
        return DirectionKind.UNKNOWN_LITERAL;
    }
    
    private  ProcedureUpdateCount getUpdateCount(String value) {
        if( ProcedureUpdateCount.AUTO_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.AUTO_LITERAL;
        }
        if( ProcedureUpdateCount.ONE_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.ONE_LITERAL;
        }
        if( ProcedureUpdateCount.MULTIPLE_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.MULTIPLE_LITERAL;
        }
        if( ProcedureUpdateCount.ZERO_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.ZERO_LITERAL;
        }
        
        return ProcedureUpdateCount.AUTO_LITERAL;
    }
    
    private MultiplicityKind getMultiplictyKind(String value) {
        if( MultiplicityKind.MANY_LITERAL.getName().equalsIgnoreCase(value) ) {
            return MultiplicityKind.MANY_LITERAL;
        }
        if( MultiplicityKind.ONE_LITERAL.getName().equalsIgnoreCase(value) ) {
            return MultiplicityKind.ONE_LITERAL;
        }
        if( MultiplicityKind.ZERO_TO_ONE_LITERAL.getName().equalsIgnoreCase(value) ) {
            return MultiplicityKind.ZERO_TO_ONE_LITERAL;
        }
        if( MultiplicityKind.ZERO_TO_MANY_LITERAL.getName().equalsIgnoreCase(value) ) {
            return MultiplicityKind.ZERO_TO_MANY_LITERAL;
        }
        
        return MultiplicityKind.UNSPECIFIED_LITERAL;
    }
    
    /**
     * @param eObject the target object
     * @param description the description
     * @param modelResource the model resource
     */
    public void createAnnotation( EObject eObject, String description, ModelResource modelResource ) {
        if (description != null && description.trim().length() > 0) {
            try {
                AnnotationContainer annotations = null;
                final Iterator contents = modelResource.getEmfResource().getContents().iterator();
                while (contents.hasNext()) {
                    final Object next = contents.next();
                    if (next instanceof AnnotationContainer) {
                        annotations = (AnnotationContainer)next;
                    }
                } // while

                if (annotations == null) {
                    annotations = CoreFactory.eINSTANCE.createAnnotationContainer();
                    modelResource.getEmfResource().getContents().add(annotations);
                }

                Annotation annotation = annotations.findAnnotation(eObject);
                if (annotation == null) {
                    annotation = CoreFactory.eINSTANCE.createAnnotation();
                    annotations.getAnnotations().add(annotation);
                    annotation.setAnnotatedObject(eObject);
                }

                annotation.setDescription(description);
            } catch (ModelWorkspaceException e) {
                RelationalPlugin.Util.log(IStatus.ERROR, 
                	NLS.bind(Messages.relationalModelFactory_error_adding_desciption_to_0, eObject));
            }

        }
    }
    
    /**
     * @param eObject the model object whose relational reference is being requested (cannot be <code>null</code>)
     * @return the relational reference or <code>null</code> if not found
     */
    public RelationalReference getRelationalObject(EObject eObject) {
    	
    	if( eObject instanceof BaseTable) {
    		BaseTable theTable = (BaseTable)eObject;
    		
    		String name = ModelerCore.getModelEditor().getName(eObject);
    		
    		RelationalTable relTable = new RelationalTable(name);
    		
    		relTable.setSupportsUpdate(theTable.isSupportsUpdate());
    		relTable.setMaterialized(theTable.isMaterialized());
    		relTable.setNameInSource(theTable.getNameInSource());
    		relTable.setSystem(theTable.isSystem());
    		relTable.setCardinality(theTable.getCardinality());
    		
    		final EList<EObject> tableColumns = theTable.getColumns();
            for( EObject column : tableColumns) {
            	relTable.addColumn((RelationalColumn)getRelationalObject(column));
            }
    		
    		transferDescription(theTable, relTable);
    		
    		return relTable;
    	} else if( eObject instanceof View ) {
    		
    	} else if( eObject instanceof Procedure ) {
    		
    	} else if( eObject instanceof Index ) {
    		Index theIndex = (Index)eObject;
    		
    		String name = ModelerCore.getModelEditor().getName(eObject);
    		
    		RelationalIndex relIndex = new RelationalIndex(name);
    		relIndex.setNameInSource(theIndex.getNameInSource());
    		relIndex.setFilterCondition(theIndex.getFilterCondition());
    		relIndex.setAutoUpdate(theIndex.isAutoUpdate());
    		relIndex.setNullable(theIndex.isNullable());
    		relIndex.setUnique(theIndex.isUnique());
            
    		transferDescription(theIndex, relIndex);
            
            // Add the columns in the correct order
            final EList<EObject> indexColumns = theIndex.getColumns();
            for (EObject column : indexColumns ) {
                relIndex.addColumn((RelationalColumn)getRelationalObject(column));
            }
    		
    		return relIndex;
    	} else if( eObject instanceof Column) {
    		Column theColumn = (Column)eObject;
    		
        	String name = ModelerCore.getModelEditor().getName(theColumn);
        	RelationalColumn relColumn = new RelationalColumn(name);
            
        	transferDescription(theColumn, relColumn);
        	
        	return relColumn;
    	}
		return null;
    }
    
    private void transferDescription(EObject eObject, RelationalReference relationalRef) {
        try {
			// Set Description
			String desc = ModelerCore.getModelEditor().getDescription(eObject);
			if( desc != null ) {
				relationalRef.setDescription(desc);
			}
		} catch (ModelerCoreException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
    }
}
