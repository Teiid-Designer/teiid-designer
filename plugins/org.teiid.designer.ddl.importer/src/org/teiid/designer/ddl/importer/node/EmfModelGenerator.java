/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Catalog;
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
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalConstants.DIRECTION;
import org.teiid.designer.relational.RelationalConstants.NULLABLE;
import org.teiid.designer.relational.RelationalConstants.SEARCHABILITY;
import org.teiid.designer.relational.RelationalConstants.TYPES;
import org.teiid.designer.relational.RelationalPlugin;
import org.teiid.designer.relational.compare.DifferenceReport;
import org.teiid.designer.relational.model.DatatypeProcessor;
import org.teiid.designer.relational.model.RelationalAccessPattern;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalProcedureResultSet;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;
import org.teiid.designer.relational.model.RelationalView;
import org.teiid.designer.relational.model.RelationalViewProcedure;
import org.teiid.designer.relational.model.RelationalViewTable;
import org.teiid.designer.transformation.model.RelationalViewModelFactory;

/**
 * EmfModelGenerator - creates EMF model objects from RelationalReference counterparts.
 */
public class EmfModelGenerator {

    /**
     * RelationalFactory creates the EMF objects
     */
    public static final RelationalFactory FACTORY = RelationalFactory.eINSTANCE;

    @SuppressWarnings("javadoc")
	public static EmfModelGenerator INSTANCE = new EmfModelGenerator();
    public static RelationalViewModelFactory VIEW_MODEL_FACTORY = new RelationalViewModelFactory();

    private DatatypeProcessor datatypeProcessor = new DatatypeProcessor();
    private ModelEditor modelEditor = ModelerCore.getModelEditor();
    
    private List<DeferredPair> pkList = new ArrayList<DeferredPair>();
    private List<DeferredPair> fkList = new ArrayList<DeferredPair>();
    private List<DeferredPair> apList = new ArrayList<DeferredPair>();
    private List<DeferredPair> ucList = new ArrayList<DeferredPair>();
    private List<RelationalIndex> indexList = new ArrayList<RelationalIndex>();
    
    private List<DeferredPair> deferredProcessingList = new ArrayList<DeferredPair>();
    
	private Map<String, Collection<ModelObjectExtensionAssistant>> classNameToMedAssistantsMap = new HashMap<String,Collection<ModelObjectExtensionAssistant>>();

	private Set<String> propsWithNoAssistant = new HashSet<String>();
	private Set<String> metaclassesWithNoAssistant = new HashSet<String>();
	
	/**
	 * EMF Model Generator execute
     * @param diffReport the difference report
     * @param targetModelResource the model resource
     * @param progressMonitor progress monitor
     * @param totalWork the total work units to be done
     * @return the execution status
     * @throws ModelerCoreException if problems building model
     */
	public IStatus execute(DifferenceReport diffReport, ModelResource targetModelResource, IProgressMonitor progressMonitor, int totalWork) throws ModelerCoreException {

        // Clear lists prior to execution
        clearLists();

		int workUnit = totalWork / 5;

		// Add Relational Extension Assistant if necessary
		addRelationalExtensionAssistant(targetModelResource);

		progressMonitor.setTaskName(Messages.emfModelGenerator_generatingModel);

		// 1) Delete Objects from the target Model
		progressMonitor.subTask(Messages.emfModelGenerator_deletingPrimaryObjects);
		List<RelationalReference> objsToDelete = diffReport.getObjectsToDelete().getList();
		for( RelationalReference child : objsToDelete ) {
			// Delete object from targetResource
			if(child.isChecked()) {
				deleteMatchingChild(targetModelResource, child);
			}
		}
		progressMonitor.worked(workUnit);

		// 2) Create Primary Objects in the target Model (Keep Deferred Map)
		progressMonitor.subTask(Messages.emfModelGenerator_creatingPrimaryObjects);
		List<RelationalReference> objsToCreate = diffReport.getObjectsToCreate().getList();
		for( RelationalReference child : objsToCreate ) {
			// Create new objects in targetResource
			if(child.isChecked()) {
				createObject(child, targetModelResource);  
			}
		}
		progressMonitor.worked(workUnit);

		// 3) Update Objects in the target Model (Keep Deferred Map)
		progressMonitor.subTask(Messages.emfModelGenerator_updatingPrimaryObjects);
		List<RelationalReference> objsToUpdate = diffReport.getObjectsToUpdate().getList();
		for( RelationalReference child : objsToUpdate ) {
			if(child.isChecked()) {
				deleteMatchingChild(targetModelResource, child);
				createObject(child, targetModelResource);
			}
		}
		progressMonitor.worked(workUnit);

		// 4) Create the 'Secondary' objects - pks, fks, etc
		progressMonitor.subTask(Messages.emfModelGenerator_creatingSecondaryObjects);

		if(!pkList.isEmpty()) {
			for( DeferredPair item : pkList ) {
				RelationalPrimaryKey pk = (RelationalPrimaryKey)item.getRelationalReference();
				BaseTable table = (BaseTable)item.getEObject();
				createPrimaryKey(pk, table, targetModelResource);  
			}
		}
		if(!fkList.isEmpty()) {
			for( DeferredPair item : fkList ) {
				RelationalForeignKey fk = (RelationalForeignKey)item.getRelationalReference();
				BaseTable table = (BaseTable)item.getEObject();
				createForeignKey(fk, table, targetModelResource);  
			}
		}
		if(!apList.isEmpty()) {
			for( DeferredPair item : apList ) {
				RelationalAccessPattern ap = (RelationalAccessPattern)item.getRelationalReference();
				Table table = (Table)item.getEObject();
				createAccessPattern(ap, table, targetModelResource);  
			}
		}
		if(!ucList.isEmpty()) {
			for( DeferredPair item : ucList ) {
				RelationalUniqueConstraint uc = (RelationalUniqueConstraint)item.getRelationalReference();
				BaseTable table = (BaseTable)item.getEObject();
				createUniqueConstraint(uc, table, targetModelResource);  
			}
		}
		if(!indexList.isEmpty()) {
			for( RelationalIndex index : indexList ) {
				createIndex(index, targetModelResource);  
			}
		}
		progressMonitor.worked(workUnit);

		// 5) Process all 'deferred' operations - Extension properties and descriptions...
		progressMonitor.subTask(Messages.emfModelGenerator_settingExtensionProps);
		if(!deferredProcessingList.isEmpty()) {
			for( DeferredPair item : deferredProcessingList) {
				RelationalReference relRef = item.getRelationalReference();
				EObject eObj = item.getEObject();
				processExtensionProperties(targetModelResource,relRef,eObj);
				setDescription(eObj,relRef.getDescription(),targetModelResource);
			}
		}
		progressMonitor.worked(workUnit);
		
		// Build a return status based on errors found
        MultiStatus multiStatus = new MultiStatus(RelationalPlugin.PLUGIN_ID, IStatus.OK, Messages.emfModelGenerator_modelGenerationSuccess, null);

        if(!this.propsWithNoAssistant.isEmpty()) {
        	StringBuffer sb = new StringBuffer();
        	Iterator iter = this.propsWithNoAssistant.iterator();
        	while(iter.hasNext()) {
        		String prop = (String)iter.next();
        		sb.append(prop);
        		if(iter.hasNext()) sb.append(","); //$NON-NLS-1$
        	}
        	multiStatus.add(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 0, NLS.bind(Messages.emfModelGenerator_warningAssistantForPropertyNotFound, sb.toString()), null));
        } else if(!this.metaclassesWithNoAssistant.isEmpty()) {
        	StringBuffer sb = new StringBuffer();
        	Iterator iter = this.metaclassesWithNoAssistant.iterator();
        	while(iter.hasNext()) {
        		String prop = (String)iter.next();
        		sb.append(prop);
        		if(iter.hasNext()) sb.append(","); //$NON-NLS-1$
        	}
        	multiStatus.add(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 0, NLS.bind(Messages.emfModelGenerator_warningAssistantForMetaclassNotFound, sb.toString()), null));
        } 
        
        // Clear lists on completion
		clearLists();
		
		return multiStatus;
	}
    
	/**
	 * Clear the lists used during execution
	 */
	private void clearLists() {
        pkList.clear();
		fkList.clear();
		apList.clear();
		ucList.clear();
		indexList.clear();
		deferredProcessingList.clear();
		propsWithNoAssistant.clear();
		metaclassesWithNoAssistant.clear();
	}
    
    /**
     * Create an EMF object, using the provided RelationalReference object
     * @param relationalRef the relational model object
     * @param modelResource the model resource
     * @return the new EMF object
     * @throws ModelWorkspaceException if problems building model
     */
    public EObject createObject( RelationalReference relationalRef, ModelResource modelResource) throws ModelWorkspaceException {
        EObject newEObject = null;
        
        switch (relationalRef.getType()) {
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
        	if( relationalRef instanceof RelationalViewTable ) {
        		newEObject = VIEW_MODEL_FACTORY.buildObject(relationalRef, modelResource, new NullProgressMonitor());
        	} else {
	        	newEObject = createBaseTable(relationalRef, modelResource);
	        	modelResource.getEmfResource().getContents().add(newEObject);
        	}
        } break;
        case TYPES.VIEW: {
        	newEObject = createView(relationalRef, modelResource);
        	modelResource.getEmfResource().getContents().add(newEObject);
        } break;
        case TYPES.PROCEDURE: {
                if (relationalRef instanceof RelationalViewProcedure) {
                    newEObject = VIEW_MODEL_FACTORY.buildObject(relationalRef, modelResource, new NullProgressMonitor());
                } else {
                    newEObject = createProcedure(relationalRef, modelResource);
                    modelResource.getEmfResource().getContents().add(newEObject);
                }
        } break;

        case TYPES.INDEX: {
        	newEObject = createIndex(relationalRef, modelResource);
        	modelResource.getEmfResource().getContents().add(newEObject);
        } break;

        case TYPES.UNDEFINED:
        default: {
        	RelationalPlugin.Util.log(IStatus.WARNING, 
        			NLS.bind(Messages.emfModelGenerator_unknown_object_type_0_cannot_be_processed, relationalRef.getName()));
        } break;
        }
        
        return newEObject;
    }
    
    /**
     * Delete the emf object the targetResource that matches the supplied RelationalReference
     * @param targetResource the target model
     * @param ref the relational reference
     * @throws ModelerCoreException
     */
    private void deleteMatchingChild(ModelResource targetResource, RelationalReference ref) throws ModelerCoreException {
        int refType = ref.getType();
        
        Collection<EObject> existingChildren = targetResource.getEmfResource().getContents();
        EObject childToDelete = null;
        for( EObject child : existingChildren ) {
        	String eObjName = this.modelEditor.getName(child);
        	if(refType==RelationalConstants.TYPES.TABLE && child instanceof BaseTable) {
//        		BaseTable tableEObj = ((BaseTable)child);
//        		EObject parent = tableEObj.eContainer();
        		if(CoreStringUtil.equals(eObjName, ref.getName())) {
        			childToDelete = child;
        			break;
        		}
        	} else if(refType==RelationalConstants.TYPES.VIEW && child instanceof View) {
        		if(CoreStringUtil.equals(eObjName, ref.getName())) {
        			childToDelete = child;
        			break;
        		}
        	} else if(refType==RelationalConstants.TYPES.PROCEDURE && child instanceof Procedure) {
        		if(CoreStringUtil.equals(eObjName, ref.getName())) {
        			childToDelete = child;
        			break;
        		}
        	} else if(refType==RelationalConstants.TYPES.INDEX && child instanceof Index) {
        		if(CoreStringUtil.equals(eObjName, ref.getName())) {
        			childToDelete = child;
        			break;
        		}
        	} else if(refType==RelationalConstants.TYPES.SCHEMA && child instanceof Schema) {
        		if(CoreStringUtil.equals(eObjName, ref.getName())) {
        			childToDelete = child;
        			break;
        		}
        	} else if(refType==RelationalConstants.TYPES.CATALOG && child instanceof Catalog) {
        		if(CoreStringUtil.equals(eObjName, ref.getName())) {
        			childToDelete = child;
        			break;
        		}
        	}
        }
        if( childToDelete != null ) {
            this.modelEditor.delete(childToDelete);
        }
    }

    /**
     * Create EMF BaseTable from the supplied RelationalReference
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
        
        // Add Columns
        for( RelationalColumn column : tableRef.getColumns()) {
            createColumn(column, baseTable, modelResource);
        }
        
        // Add Primary Keys
        RelationalPrimaryKey pk = tableRef.getPrimaryKey();
        if( pk != null ) {
        	pkList.add(new DeferredPair(pk,baseTable));
        }
        
        Collection<RelationalUniqueConstraint> ucs = tableRef.getUniqueConstraints();
        for(RelationalUniqueConstraint uc: ucs) {
        	ucList.add(new DeferredPair(uc,baseTable));
        }
        
        for( RelationalAccessPattern ap : tableRef.getAccessPatterns()) {
        	apList.add(new DeferredPair(ap,baseTable));
        }
        
        for( RelationalForeignKey fk : tableRef.getForeignKeys()) {
        	fkList.add(new DeferredPair(fk,baseTable));
        }
        
        for( RelationalIndex index : tableRef.getIndexes() ) {
        	indexList.add(index);
        }
        
        // Save objects to Map, so that Extension Properties can be processed later
        if(!tableRef.getExtensionProperties().isEmpty() || !CoreStringUtil.isEmpty(tableRef.getDescription())) {
        	deferredProcessingList.add(new DeferredPair(tableRef, baseTable));      
        }
        
        return baseTable;
    }
    
    /**
     * Create EMF View from the supplied RelationalReference
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
        
        // Add Columns
        for( RelationalColumn column : viewRef.getColumns()) {
            createColumn(column, view, modelResource);
        }
  
        
        for( RelationalAccessPattern ap : viewRef.getAccessPatterns()) {
        	apList.add(new DeferredPair(ap, view));
        }

        // Add to deferred list, if necessary
        updateDeferredList(viewRef,view);

        return view;
    }

    /**
     * Create EMF Column from the supplied RelationalReference
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
            
            String dTypeName = this.modelEditor.getName(datatype);
            
            int datatypeLength = columnRef.getLength();
            if( datatypeLength == 0 && DatatypeProcessor.DEFAULT_DATATYPE.equalsIgnoreCase(dTypeName) ) {
                columnRef.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
            } else {
            	columnRef.setLength(datatypeLength);
            }
        }
        column.setLength(columnRef.getLength());
        
        // Add to deferred list, if necessary
        updateDeferredList(columnRef,column);

        return column;
    }
    
    /**
     * Create EMF Column from the supplied RelationalReference
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
        String nullableStr = columnRef.getNullable(); 
        column.setNullable(getNullableType(nullableStr));
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
            
            String dTypeName = this.modelEditor.getName(datatype);
            
            int datatypeLength = columnRef.getLength();
            if( datatypeLength == 0 && DatatypeProcessor.DEFAULT_DATATYPE.equalsIgnoreCase(dTypeName) ) {
                columnRef.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
            } else {
            	columnRef.setLength(datatypeLength);
            }
        }
        
        // Add to deferred list, if necessary
        updateDeferredList(columnRef,column);

        return column;
    }

    /**
     * Get the DirectionKind given the string representation
     * @param dirKindStr the string representation
     * @return the DirectionKind object equivalent
     */
    private DirectionKind getDirectionKind(String dirKindStr) {
        if( DIRECTION.IN.equalsIgnoreCase(dirKindStr) ) {
            return DirectionKind.IN_LITERAL;
        }
        if( DIRECTION.IN_OUT.equalsIgnoreCase(dirKindStr) ) {
            return DirectionKind.INOUT_LITERAL;
        }
        if( DIRECTION.OUT.equalsIgnoreCase(dirKindStr) ) {
            return DirectionKind.OUT_LITERAL;
        }
        if( DIRECTION.RETURN.equalsIgnoreCase(dirKindStr) ) {
            return DirectionKind.RETURN_LITERAL;
        }
        
        return DirectionKind.UNKNOWN_LITERAL;
    }

    /**
     * Get the ProcedureUpdateCount given the string representation
     * @param updCountStr the string representation
     * @return the ProcedureUpdateCount object equivalent
     */
    private  ProcedureUpdateCount getUpdateCount(String updCountStr) {
        if( ProcedureUpdateCount.AUTO_LITERAL.getName().equalsIgnoreCase(updCountStr) ) {
            return ProcedureUpdateCount.AUTO_LITERAL;
        }
        if( ProcedureUpdateCount.ONE_LITERAL.getName().equalsIgnoreCase(updCountStr) ) {
            return ProcedureUpdateCount.ONE_LITERAL;
        }
        if( ProcedureUpdateCount.MULTIPLE_LITERAL.getName().equalsIgnoreCase(updCountStr) ) {
            return ProcedureUpdateCount.MULTIPLE_LITERAL;
        }
        if( ProcedureUpdateCount.ZERO_LITERAL.getName().equalsIgnoreCase(updCountStr) ) {
            return ProcedureUpdateCount.ZERO_LITERAL;
        }
        
        return ProcedureUpdateCount.AUTO_LITERAL;
    }

    /**
     * Get the MultiplicityKind given the string representation
     * @param multKindStr the string representation
     * @return the MultiplicityKind object equivalent
     */
    private MultiplicityKind getMultiplictyKind(String multKindStr) {
        if( MultiplicityKind.MANY_LITERAL.getName().equalsIgnoreCase(multKindStr) ) {
            return MultiplicityKind.MANY_LITERAL;
        }
        if( MultiplicityKind.ONE_LITERAL.getName().equalsIgnoreCase(multKindStr) ) {
            return MultiplicityKind.ONE_LITERAL;
        }
        if( MultiplicityKind.ZERO_TO_ONE_LITERAL.getName().equalsIgnoreCase(multKindStr) ) {
            return MultiplicityKind.ZERO_TO_ONE_LITERAL;
        }
        if( MultiplicityKind.ZERO_TO_MANY_LITERAL.getName().equalsIgnoreCase(multKindStr) ) {
            return MultiplicityKind.ZERO_TO_MANY_LITERAL;
        }
        
        return MultiplicityKind.UNSPECIFIED_LITERAL;
    }

    /**
     * Get the NullableType given the string representation
     * @param nullableStr the string representation
     * @return the NullableType object equivalent
     */
    private NullableType getNullableType(String nullableStr) {
        if( NULLABLE.NULLABLE.equalsIgnoreCase(nullableStr) ) {
            return NullableType.NULLABLE_LITERAL;
        }
        
        if( NULLABLE.NO_NULLS.equalsIgnoreCase(nullableStr) ) {
            return NullableType.NO_NULLS_LITERAL;
        }
        
        return NullableType.NULLABLE_UNKNOWN_LITERAL;
    }
    
    /**
     * Get the SearchabilityType given the string representation
     * @param searchableStr the string representation
     * @return the SearchabilityType object equivalent
     */
    private SearchabilityType getSearchabilityType(String searchableStr) {
        if( SEARCHABILITY.UNSEARCHABLE.equalsIgnoreCase(searchableStr) ) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        if( SEARCHABILITY.ALL_EXCEPT_LIKE.equalsIgnoreCase(searchableStr) ) {
            return SearchabilityType.ALL_EXCEPT_LIKE_LITERAL;
        }
        if( SEARCHABILITY.LIKE_ONLY.equalsIgnoreCase(searchableStr) ) {
            return SearchabilityType.LIKE_ONLY_LITERAL;
        }
        
        return SearchabilityType.SEARCHABLE_LITERAL;
    }

    /**
     * Create EMF Index from the supplied RelationalReference
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
        
        // Add the columns in the correct order
        final List indexColumns = index.getColumns();
        for (RelationalColumn relColumn : indexRef.getColumns() ) {
            BaseTable baseTable = getTable(relColumn.getParent().getName(), modelResource);
            
            Column column = getColumn(relColumn.getName(), baseTable);
            if( column != null ) {
                indexColumns.add(column);
            }
        }
        
        // Add to deferred list, if necessary
        updateDeferredList(indexRef,index);

        return index;
    }

    /**
     * Create EMF PrimaryKey from the supplied RelationalReference
     * @param ref the relational pk object
     * @param modelResource the model resource
     * @return the primary key object
     */
    private EObject createPrimaryKey( RelationalReference ref, BaseTable baseTable, ModelResource modelResource) {
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
        
        // Add to deferred list, if necessary
        updateDeferredList(pkRef,primaryKey);
       
        return primaryKey;
    }
    
    /**
     * Create EMF ForeignKey from the supplied RelationalReference
     * @param ref the relational fk object
     * @param baseTable the table parent of the fk
     * @param modelResource the model resource
     * @return the foreign key object
     */
    public EObject createForeignKey( RelationalReference ref, BaseTable baseTable, ModelResource modelResource) {
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
                    String keyName = this.modelEditor.getName((UniqueKey)key);
                    if( keyName.equalsIgnoreCase(ukRefName) ) {
                        foreignKey.setUniqueKey((UniqueKey)key);
                    }
                }
            }
        }
        
        // Add to deferred list, if necessary
        updateDeferredList(fkRef,foreignKey);
        
        return foreignKey;
    }
    
    /**
     * Create EMF AccessPattern from the supplied RelationalReference
     * @param ref the relational AccessPattern object
     * @param baseTable the table parent of the ap
     * @param modelResource the model resource
     * @return the AccessPattern object
     */
    private EObject createAccessPattern( RelationalReference ref, Table baseTable, ModelResource modelResource) {
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
        
        // Add to deferred list, if necessary
        updateDeferredList(apRef,accessPattern);

        return accessPattern;
    }
    
    /**
     * Create EMF UniqueConstraint from the supplied RelationalReference
     * @param ref the relational UniqueConstraint object
     * @param baseTable the table parent of the uc
     * @param modelResource the model resource
     * @return the UniqueConstraint object
     */
    private EObject createUniqueConstraint( RelationalReference ref, BaseTable baseTable, ModelResource modelResource) {
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
        
        // Add to deferred list, if necessary
        updateDeferredList(ucRef,uniqueConstraint);

        return uniqueConstraint;
    }
    
    /**
     * 
     * @param name
     * @param baseTable
     * @return
     */
    private Column getColumn(String name, Table baseTable) {
        for( Object column : baseTable.getColumns()) {
            if( column instanceof Column && ((Column)column).getName().equalsIgnoreCase(name) ) {
                return (Column)column;
            }
        }
        
        return null;
    }
    
    /**
     * 
     * @param tableName
     * @param modelResource
     * @return
     */
    private BaseTable getTable(String tableName, ModelResource modelResource) {
        try {
            for (EObject eObj : modelResource.getEmfResource().getContents() ) {
                String eObjName = this.modelEditor.getName(eObj);
                if( eObj instanceof BaseTable && eObjName != null && eObjName.equalsIgnoreCase(tableName)) {
                    return (BaseTable)eObj;
                }
            }
        } catch (ModelWorkspaceException e) {
            RelationalPlugin.Util.log(IStatus.ERROR, 
            		NLS.bind(Messages.emfModelGenerator_error_finding_table_named, tableName));
        }

        return null;
    }
    
    
    /**
     * Create EMF Procedure from the supplied RelationalReference
     * @param ref the relational Procedure object
     * @param modelResource the model resource
     * @return the Procedure object
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
        

        // Add Columns
        for( RelationalParameter paramRef : procedureRef.getParameters()) {
            createParameter(paramRef, procedure, modelResource);
        }
        
        if( procedureRef.getResultSet() != null ) {
            createResultSet(procedureRef.getResultSet(), procedure, modelResource);
        }
        
        // Add to deferred list, if necessary
        updateDeferredList(procedureRef,procedure);

        return procedure;
    }
    
    /**
     * Create EMF ProcedureParameter from the supplied RelationalReference
     * @param ref the parameter object
     * @param procedure the parent procedure
     * @param modelResource the  model resource
     * @return the ProcedureParameter EObject
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
            String dTypeName = this.modelEditor.getName(datatype);
            
            int datatypeLength = parameterRef.getLength();
            if( datatypeLength == 0 && DatatypeProcessor.DEFAULT_DATATYPE.equalsIgnoreCase(dTypeName) ) {
                parameter.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
            } else {
            	parameter.setLength(datatypeLength);
            }
        }
       
        // Add to deferred list, if necessary
        updateDeferredList(parameterRef,parameter);

        return parameter;
    }
    
    /**
     * Create EMF ProcedureResult from the supplied RelationalReference
     * @param ref the result set object
     * @param procedure the parent procedure
     * @param modelResource the model resource
     * @return the ProcedureResult object
     */
    public EObject createResultSet( RelationalReference ref, Procedure procedure, ModelResource modelResource) {
        CoreArgCheck.isInstanceOf(RelationalProcedureResultSet.class, ref);
        
        RelationalProcedureResultSet resultSetRef = (RelationalProcedureResultSet)ref;
        
        ProcedureResult result = FACTORY.createProcedureResult();
        result.setProcedure(procedure);
        result.setName(resultSetRef.getName());
        result.setNameInSource(resultSetRef.getNameInSource());
        
        // Add Columns
        for( RelationalColumn colRef : resultSetRef.getColumns()) {
            createColumn(colRef, result, modelResource);
        }
        
        // Add to deferred list, if necessary
        updateDeferredList(resultSetRef,result);

        return result;
    }
    
    /**
     * Adds the RelationalReference - EObject pair to the deferred processing list, if there are
     * extension properties or a description to add.  Also RelationalProcedure are deferred to process extensions
     * @param relRef the RelationalReference
     * @param eObj the EObject
     */
    private void updateDeferredList(RelationalReference relRef, EObject eObj) {
        if(!relRef.getExtensionProperties().isEmpty() || !CoreStringUtil.isEmpty(relRef.getDescription()) || relRef instanceof RelationalProcedure) {
        	deferredProcessingList.add(new DeferredPair(relRef, eObj));      
        }
    }

    /**
     * Set the description on the EMF object.  Looks up annotation - if not found then the annotation is created.
     * @param eObject the target object
     * @param description the description
     * @param modelResource the model resource
     */
    public void setDescription( EObject eObject, String description, ModelResource modelResource ) {
        if (description != null && description.trim().length() > 0) {
            try {
                AnnotationContainer annotations = null;
                final Iterator contents = modelResource.getEmfResource().getContents().iterator();
                while (contents.hasNext()) {
                    final Object next = contents.next();
                    if (next instanceof AnnotationContainer) {
                        annotations = (AnnotationContainer)next;
                        break;
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
                	NLS.bind(Messages.emfModelGenerator_error_adding_desciption_to_0, eObject));
            }

        }
    }

    /**
	 * Process the extension properties for a relational entity.  This will apply the necessary med to the model (if needed) and add the 
	 * appropriate extension properties to the model.
	 * @param modelResource the ModelResource
	 * @param relationalEntity the RelationalReference
	 * @param eObject the EObject
	 * 
	 */
	private void processExtensionProperties(ModelResource modelResource, RelationalReference relationalEntity, EObject eObject) {
		// RelationalProcedure has fields which are transferred to extension properties
		if(relationalEntity.getType()==TYPES.PROCEDURE) {
			processProcedureExtensionProperties(modelResource,(RelationalProcedure)relationalEntity,eObject);
		}
		
		Properties extensionProperties = relationalEntity.getExtensionProperties();
		
		Iterator<Object> keyIter = extensionProperties.keySet().iterator();
		while(keyIter.hasNext()) {
			String propName = (String)keyIter.next();
			String propValue = extensionProperties.getProperty(propName);
			
			// Find an extension assistant that can create this extension property (if it exists)
	    	ModelObjectExtensionAssistant assistant = getModelExtensionAssistant(eObject.getClass().getName(),propName);
	    	if(assistant!=null) {
	    		// Ensure that the Model supports the MED
	    		applyMedIfNecessary(modelResource,assistant);
	    		
	    		// Set the property value via the assistant
	    		setPropertyValue(assistant,eObject,propName,propValue);
	    	}
		}
	}
	
    /**
	 * Process the RelationalProcedure, converting values to EMF extension properties
	 * @param modelResource the ModelResource
	 * @param relationalProcedure the RelationalProcedure
	 * @param eObject the EObject
	 * 
	 */
	private void processProcedureExtensionProperties(ModelResource modelResource, RelationalProcedure relationalProcedure, EObject eObject) {
		RelationalModelExtensionAssistant relationalExtensionAssistant = getRelationalExtensionAssistant();
		
		String nativeQuery = relationalProcedure.getNativeQuery();
		if(!CoreStringUtil.isEmpty(nativeQuery)) {
			setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.NATIVE_QUERY,nativeQuery);
		}
		
		boolean isNonPrepared = relationalProcedure.isNonPrepared();
		setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.NON_PREPARED,String.valueOf(isNonPrepared));
		
		// Functions have many additional extension properties
		boolean isFunction = relationalProcedure.isFunction();
		if(isFunction) {
			String functionCategory = relationalProcedure.getFunctionCategory();
			if(!CoreStringUtil.isEmpty(functionCategory)) {
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.FUNCTION_CATEGORY,functionCategory);
			}

			String javaClass = relationalProcedure.getJavaClassName();
			if(!CoreStringUtil.isEmpty(javaClass)) {
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.JAVA_CLASS,javaClass);
			}

			String javaMethod = relationalProcedure.getJavaMethodName();
			if(!CoreStringUtil.isEmpty(javaMethod)) {
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.JAVA_METHOD,javaMethod);
			}
			
			boolean isVariableArgs = relationalProcedure.isVariableArguments();
			setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.VARARGS,String.valueOf(isVariableArgs));
			
			boolean isNullOnNull = relationalProcedure.isReturnsNullOnNull();
			setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.NULL_ON_NULL,String.valueOf(isNullOnNull));
			
			boolean isDeterministic = relationalProcedure.isDeterministic();
			setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.DETERMINISTIC,String.valueOf(isDeterministic));
			
			// Additional Properties for Aggregate 'true'
			boolean isAggregate = relationalProcedure.isAggregate();
			if(isAggregate) {
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.AGGREGATE,String.valueOf(isAggregate));
				
				boolean isAnalytic = relationalProcedure.isAnalytic();
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.ANALYTIC,String.valueOf(isAnalytic));
				
				boolean isAllowsOrderBy = relationalProcedure.isAllowsOrderBy();
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.ALLOWS_ORDER_BY,String.valueOf(isAllowsOrderBy));
				
				boolean isUseDistinctRows = relationalProcedure.isUseDistinctRows();
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.USES_DISTINCT_ROWS,String.valueOf(isUseDistinctRows));
				
				boolean isAllowsDistinct = relationalProcedure.isAllowsDistinct();
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.ALLOWS_DISTINCT,String.valueOf(isAllowsDistinct));
				
				boolean isDecomposable = relationalProcedure.isDecomposable();
				setPropertyValue(relationalExtensionAssistant,eObject,RelationalConstants.PROCEDURE_EXT_PROPERTIES.DECOMPOSABLE,String.valueOf(isDecomposable));
			}

		}
		
	}
	
    /**
	 * Set a property value via the supplied assistant.  the propId should NOT be namespaced
	 * @param assistant the ModelObjectExtensionAssistant
	 * @param eObject the EObject
	 * @param propId the property name
	 * @param propValue the property value
	 * 
	 */
	private void setPropertyValue(ModelObjectExtensionAssistant assistant, EObject eObject, String propId, String propValue) {
		// Check if the propId is already namespaced, otherwise prepend the assistant's namespace prefix
		String namespacedId = null;
		if(propId.indexOf(':') != -1) {
			namespacedId = propId;
		} else {
			namespacedId = assistant.getNamespacePrefix()+':'+propId;
		}

		try {
			assistant.setPropertyValue(eObject, namespacedId, propValue);
		} catch (Exception ex) {
            RelationalPlugin.Util.log(IStatus.ERROR,ex, 
                	NLS.bind(Messages.emfModelGenerator_errorSettingPropertyValue, namespacedId));
		}
	}
	
	/**
	 * If the ModelResource does not support the assistants namespace, apply its MED to the model
	 * @param modelResource the model resource
	 * @param assistant the ModelObjectExtensionAssistant
	 * @throws Exception exception if there's a problem applying the MED
	 */
	private void applyMedIfNecessary(final ModelResource modelResource, ModelObjectExtensionAssistant assistant) {
		if (modelResource != null && !modelResource.isReadOnly()) {
			try{
				if(!assistant.supportsMyNamespace(modelResource)) {
					assistant.saveModelExtensionDefinition(modelResource);
				}
			} catch (Exception e) {
				RelationalPlugin.Util.log(IStatus.ERROR, Messages.emfModelGenerator_errorApplyingMedToModel);
			}
		}
	}

	/**
	 * Get the ModelExtensionAssistant that can handle the supplied property for the specified metaClass.  Currently, this will
	 * get the first valid assistant found (if more than one can handle the property)
	 * @param eObjectClassName the metaclass name
	 * @param propId the property
	 * @return the assistant
	 * 
	 */
	private ModelObjectExtensionAssistant getModelExtensionAssistant( String eObjectClassName, String propId ) {
    	// Get available assistants for the provided className.  If the map has no entry, go to the ExtensionPlugin and populate it first.
    	Collection<ModelObjectExtensionAssistant> assistants = null;
    	if(this.classNameToMedAssistantsMap.containsKey(eObjectClassName)) {
        	assistants = this.classNameToMedAssistantsMap.get(eObjectClassName);
    	} else {
    		Collection<ModelExtensionAssistant> medAssistants = ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistants(eObjectClassName);
    		assistants = new ArrayList<ModelObjectExtensionAssistant>();
    		for(ModelExtensionAssistant medAssistant: medAssistants) {
    			if(medAssistant instanceof ModelObjectExtensionAssistant) {
    				assistants.add((ModelObjectExtensionAssistant)medAssistant);
    			}
    		}
    		this.classNameToMedAssistantsMap.put(eObjectClassName, assistants);
    	}
    	

        // no assistants found that have properties defined for the model object type
        if (assistants.isEmpty()) {
        	String metaclassShortName = getMetaclassShortName(eObjectClassName);
        	metaclassesWithNoAssistant.add(metaclassShortName);
            return null;
        }

        // If property is namespaced, get the assistant with matching namespace
        if(isNamespaced(propId)) {
        	ModelObjectExtensionAssistant matchingAssistant = getAssistantWithNamespace(getExtensionPropertyNamespace(propId),assistants);
        	if(matchingAssistant!=null) return matchingAssistant;
        } else {
        	// find the assistant for the property
        	for (ModelExtensionAssistant assistant : assistants) {
        		// Prepend the assistant namespace to the propertyId, since it doesnt have one
        		String namespacedId = assistant.getNamespacePrefix()+':'+propId;

        		if(hasMatchingPropertyName(assistant.getModelExtensionDefinition(), eObjectClassName, namespacedId)) {
        			return ((assistant instanceof ModelObjectExtensionAssistant) ? (ModelObjectExtensionAssistant)assistant : null);
        		}
        	}
        }
    
    	this.propsWithNoAssistant.add(propId);
        return null;
    }
	
	/**
	 * Get the ModelExtensionAssistant matching the supplied namespace
	 * @param namespace the namespace
	 * @param assistants the list of assistants
	 * @return the assistant which matches the supplied namespace
	 * 
	 */
	private ModelObjectExtensionAssistant getAssistantWithNamespace( String namespace, Collection<ModelObjectExtensionAssistant> assistants ) {
		ModelObjectExtensionAssistant result = null;
		for(ModelObjectExtensionAssistant assistant: assistants) {
			if(assistant.getNamespacePrefix().equalsIgnoreCase(namespace)) {
				result = assistant;
				break;
			}
		}
        return result;
    }
	
    /**
	 * Get the Namespace from the extension property name.  The propertyName may or may not be namespaced.
	 * If it's not a null is returned
	 * @param propName the extension property name, including namespace
	 * @return the namespace, if present.  'null' if not namespaced
	 */
	private String getExtensionPropertyNamespace(String propName) {
		String namespace = null;
		if(!CoreStringUtil.isEmpty(propName)) {
			int index = propName.indexOf(':');
			if(index!=-1) {
				namespace = propName.substring(0,index);
			}
		}
		return namespace;
	}
	
    /**
	 * Determine if the property name has a leading namespace
	 * @param propName the extension property name, including namespace
	 * @return 'true' if a namespace is present, 'false' if not.
	 */
	private boolean isNamespaced(String propName) {
		boolean isNamespaced = false;
		if(!CoreStringUtil.isEmpty(propName)) {
			isNamespaced = propName.indexOf(':') != -1;
		}
		return isNamespaced;
	}

	private String getMetaclassShortName(String metaclass) {
        return AbstractMetaclassNameProvider.getLabel(metaclass);
    }

    /**
	 *  Determine if the ModelExtensionDefinition has a propertyId that matches the supplied property
	 * @param med the ModelExtensionDefinition 
	 * @param metaclassName the extended metaclass name
	 * @param propId the property id
	 * @return 'true' if the med has a matching propertyDefn, 'false' if not
	 */
	private boolean hasMatchingPropertyName(ModelExtensionDefinition med, String metaclassName, String propId) {
		ModelExtensionPropertyDefinition propDefn = med.getPropertyDefinition(metaclassName, propId);
		return propDefn!=null ? true : false;
	}

	/**
	 * Add the RelationalExtension to the supplied ModelResource, if it has not already been added.
	 * @param modelResource
	 */
    private void addRelationalExtensionAssistant(ModelResource modelResource) {
        final RelationalModelExtensionAssistant assistant = getRelationalExtensionAssistant();

        try {
            assistant.applyMedIfNecessary(modelResource.getUnderlyingResource());
        } catch (Exception e) {
        	RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }
    
	/**
	 * Get the RelationalModelExtensionAssistant from the MED registry
	 * @param modelResource
	 */
    private RelationalModelExtensionAssistant getRelationalExtensionAssistant() {
    	final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        final String prefix = RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
        final RelationalModelExtensionAssistant assistant = (RelationalModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
        
        return assistant;
    }
    
	/**
	 * Object for retaining the object pairs for deferred processing
	 */
    class DeferredPair {
    	private RelationalReference relationalRef;
    	private EObject eObj;
    	
    	public DeferredPair(RelationalReference relRef, EObject eObj) {
    		this.relationalRef=relRef;
    		this.eObj=eObj;
    	}
    	
    	RelationalReference getRelationalReference() {
    		return this.relationalRef;
    	}
    	
    	EObject getEObject() {
    		return this.eObj;
    	}
    }
    
}
