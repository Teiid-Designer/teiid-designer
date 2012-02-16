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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalPlugin;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.MultiplicityKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.View;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * Class provides building EMF Relational Metamodel objects from Relational Model objects
 */
public class RelationalModelFactory implements RelationalConstants {
    public static final String RELATIONAL_PACKAGE_URI = RelationalPackage.eNS_URI;
    public static final RelationalFactory FACTORY = RelationalFactory.eINSTANCE;
    
    private DatatypeProcessor datatypeProcessor;
    
    private Map<RelationalForeignKey, BaseTable> fkTableMap = new HashMap<RelationalForeignKey, BaseTable>();
    private Collection<RelationalIndex> indexes = new ArrayList<RelationalIndex>();

    public RelationalModelFactory() {
        super();
        this.datatypeProcessor = new DatatypeProcessor();
    }
    
    public void build(ModelResource modelResource, RelationalModel model, IProgressMonitor progressMonitor) {

        try {
            RelationalModelFactory builder = new RelationalModelFactory();
            
            builder.buildFullModel(model, modelResource, progressMonitor);
            
            modelResource.save(new NullProgressMonitor(), true);
        } catch (ModelerCoreException e) {
            RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }
    
    
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
            } break;
            case TYPES.VIEW: {
                EObject view = createView(obj, modelResource);
                modelResource.getEmfResource().getContents().add(view);
            } break;
            case TYPES.PROCEDURE: {
                EObject procedure = createProcedure(obj, modelResource);
                modelResource.getEmfResource().getContents().add(procedure);
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
//                        dType, 
//                        columnRef.getLength(),
//                        columnRef.getPrecision(), 
//                        columnRef.getScale(), 
//                        new ArrayList());
        if( datatype != null ) {
            column.setType(datatype);
            
            String dTypeName = ModelerCore.getModelEditor().getName(datatype);
            
            int paramLength = columnRef.getLength();
            if( paramLength == 0 && DatatypeProcessor.DEFAULT_DATATYPE.equalsIgnoreCase(dTypeName) ) {
                columnRef.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
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
    
    
    public EObject createProcedure( final RelationalReference ref, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalProcedure.class, ref);

        RelationalProcedure procedureRef = (RelationalProcedure)ref;
        // Create and Set Name
        Procedure procedure = FACTORY.createProcedure();
        procedure.setName(procedureRef.getName());
        procedure.setNameInSource(procedureRef.getNameInSource());
        
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
//                                                           dType, 
//                                                           parameterRef.getLength(),
//                                                           parameterRef.getPrecision(), 
//                                                           parameterRef.getScale(), 
//                                                           new ArrayList());
        if( datatype != null ) {
            parameter.setType(datatype);
            String dTypeName = ModelerCore.getModelEditor().getName(datatype);
            
            int paramLength = parameterRef.getLength();
            if( paramLength == 0 && DatatypeProcessor.DEFAULT_DATATYPE.equalsIgnoreCase(dTypeName) ) {
                parameter.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
            }
        }
       
        
        return parameter;
    }
    
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
    
    public EObject createIndex( RelationalReference ref, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalIndex.class, ref);
        
        RelationalIndex indexRef = (RelationalIndex)ref;
        
        Index index = FACTORY.createIndex();
        index.setName(indexRef.getName());
        index.setNameInSource(index.getNameInSource());
        
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
}
