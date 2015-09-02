/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ModelExtensionAssistantAggregator;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.RelationalPlugin;

/**
 * Factory for creating RelationalReference objects
 * This will create objects 'from scratch', or will create reference objects from EMF objects
 *
 * @since 8.2
 */
public class RelationalReferenceFactory implements RelationalConstants {

	public static RelationalReferenceFactory INSTANCE = new RelationalReferenceFactory();
    
	private ModelExtensionAssistantAggregator medAggregator = ExtensionPlugin.getInstance().getModelExtensionAssistantAggregator();
	private ModelEditor modelEditor = ModelerCore.getModelEditor();
        
    /**
     * Create the RelationalModel object representation of an EMF Model
     * @param modelResource the ModelResource
     * @return the RelationalModel object representation
     * @throws Exception the exception
     */
    public RelationalModel createRelationalModel(ModelResource modelResource) throws Exception {
    	String modelName = modelEditor.getModelName(modelResource);
    	RelationalModel relationalModel = new RelationalModel(modelName);
    	
    	// Some objects deferred since they reference other objects which must be created first
    	Map<EObject,RelationalReference> deferredCreateMap = new HashMap<EObject,RelationalReference>();
    	
    	// Process the primary objects
    	List<EObject> rootEObjs = modelResource.getAllRootEObjects();
    	for(EObject eObj : rootEObjs) {
    		Map<EObject,RelationalReference> deferredObjMap = createObject(eObj,null,relationalModel);
    		if(!deferredObjMap.isEmpty()) {
    			deferredCreateMap.putAll(deferredObjMap);
    		}
    	}
    	
		// Now process all the 'deferred' objects.  These are objects which reference other objects (which are required to exist first)
		createDeferredObjects(deferredCreateMap,relationalModel);

    	return relationalModel;
    }
    
	/**
	 * Create RelationalReference objects
	 * @param node the provided EObject
	 * @param model the RelationalModel being created
	 * @param schema the schema
	 * @return the map of EObjects which need to be deferred
	 * @throws Exception 
	 */
	protected Map<EObject,RelationalReference> createObject(EObject eObj, RelationalReference parent, RelationalModel model) throws Exception {
		Map<EObject,RelationalReference> deferredMap = new HashMap<EObject,RelationalReference>();

		// -----------------------------------------------------------------------
		// Standard DDL 
		// -----------------------------------------------------------------------
    	if(eObj instanceof BaseTable) {
    		BaseTable tableEObj = (BaseTable)eObj;
    		// Create BaseTable with its columns and ext properties
			RelationalTable table = createBaseTable(tableEObj);
			
			// FKs, PK, AP are deferred
	    	List<ForeignKey> fks = tableEObj.getForeignKeys();
	    	for(ForeignKey fk : fks) {
	    		deferredMap.put(fk, table);
	    	}
	    	
	    	List<AccessPattern> aps = tableEObj.getAccessPatterns();
	    	for(AccessPattern ap : aps) {
	    		deferredMap.put(ap, table);
	    	}
	    	
	    	PrimaryKey pk = tableEObj.getPrimaryKey();
	    	if(pk!=null) {
	    		deferredMap.put(pk, table);
	    	}
	    	if(parent!=null) {
	    		table.setParent(parent);
	    	} else {
	    		model.addChild(table);
	    	}
     	} else if(eObj instanceof Procedure) {
    		Procedure procEObj = (Procedure)eObj;
    		// Create Procedure with its children and ext properties
			RelationalProcedure proc = createProcedure(procEObj);
	    	if(parent!=null) {
	    		proc.setParent(parent);
	    	} else {
	    		model.addChild(proc);
	    	}
    	} else if(eObj instanceof Index) {
    		Index indexEObj = (Index)eObj;
    		deferredMap.put(indexEObj, null);
    	} else if(eObj instanceof View) {
    		View viewEObj = (View)eObj;
    		// Create View with its children
			RelationalView view = createView(viewEObj);
			
	    	List<AccessPattern> aps = viewEObj.getAccessPatterns();
	    	for(AccessPattern ap : aps) {
	    		deferredMap.put(ap, view);
	    	}
	    	
	    	if(parent!=null) {
	    		view.setParent(parent);
	    	} else {
	    		model.addChild(view);
	    	}
    	} else if(eObj instanceof Catalog) {
//    		resultObj = createCatalog(((Catalog)eObj));
    	}
		return deferredMap;
	}

	/**
	 * Create deferred objects using the supplied map
	 * @param deferredObjects the map of deferred EObjects
	 * @param model the RelationalModel being created
	 * @throws Exception 
	 */
	protected void createDeferredObjects(Map<EObject,RelationalReference> deferredObjects, RelationalModel model) throws Exception {
		Collection<RelationalReference> allRefs = model.getAllReferences();

		Set<EObject> eObjs = deferredObjects.keySet();
		for(EObject eObj : eObjs) {
			if(eObj instanceof PrimaryKey) {
				RelationalTable table = (RelationalTable)deferredObjects.get(eObj);
				RelationalPrimaryKey pk = createPrimaryKey((PrimaryKey)eObj, table, allRefs);
				table.setPrimaryKey(pk);
			} else if(eObj instanceof ForeignKey) {
				RelationalTable table = (RelationalTable)deferredObjects.get(eObj);
				RelationalForeignKey fk = createForeignKey((ForeignKey)eObj, table, allRefs);
				table.addForeignKey(fk);
			} else if(eObj instanceof AccessPattern) {
				RelationalTable table = (RelationalTable)deferredObjects.get(eObj);
				RelationalAccessPattern ap = createAccessPattern((AccessPattern)eObj, table, allRefs);
				table.addAccessPattern(ap);
			} else if(eObj instanceof Index) {
				RelationalIndex index = createIndex((Index)eObj, allRefs);
				model.addChild(index);
			}
		}
	}

	/**
     * Create a Relational Table from EObject, including columns and ext properties
     * @param tableEObj the EMF object
     * @return the new RelationalTable
     */
    public RelationalTable createBaseTable(BaseTable tableEObj) {
    	RelationalTable table = new RelationalTable();
    	table.setName(tableEObj.getName());
    	table.setNameInSource(tableEObj.getNameInSource());
    	setDescription(table,tableEObj);
    	table.setMaterialized(tableEObj.isMaterialized());
    	table.setCardinality(tableEObj.getCardinality());
    	table.setSupportsUpdate(tableEObj.isSupportsUpdate());
    	table.setSystem(tableEObj.isSystem());

    	List<Column> columns = tableEObj.getColumns();
    	for(Column column : columns) {
    		RelationalColumn relColumn = createColumn(column);
    		if(relColumn!=null) table.addColumn(relColumn);
    	}
    	
    	// Set the extension properties	
    	setExtensionProperties(table,tableEObj);
    	
    	return table;
    }
    
    /**
     * Set Description on the RelationalReference by transferring it from the EObject.
     * @param relationalRef the RelationalReference
     * @param eObject the source EObject
     */
    private void setDescription(RelationalReference relationalRef, EObject eObject) {
        try {
			// Set Description
			String desc = modelEditor.getDescription(eObject);
			relationalRef.setDescription(desc);
		} catch (ModelerCoreException ex) {
        	RelationalPlugin.Util.log(IStatus.ERROR, 
        			NLS.bind(Messages.relationalRefFactory_errorSettingDescription, relationalRef.getName()));
			relationalRef.setDescription(null);
		}
    }

    /**
     * Get the extension properties from the supplied EObject and set them on the supplied RelationalReference object
     * @param relationalRef the target RelationalReference
     * @param eObj the source EObject
     */
    private void setExtensionProperties(RelationalReference relationalRef, EObject eObj) {
    	Properties extProps = null;
    	try {
			extProps = this.medAggregator.getPropertyValues(eObj);
		} catch (Exception ex) {
        	RelationalPlugin.Util.log(IStatus.ERROR, 
        			NLS.bind(Messages.relationalRefFactory_errorGettingEmfExtProps, relationalRef.getName()));
		}
    	
    	relationalRef.setExtensionProperties(extProps);
    }
    
    /**
     * Create a RelationalIndex from an EMF Index
     * @param indexEObj the EMF index
     * @param allRefs list of all refs in the RelationalModel
     * @return the new RelationalIndex object
     */
    public RelationalIndex createIndex(Index indexEObj, Collection<RelationalReference> allRefs) {
    	RelationalIndex index = new RelationalIndex();
    	
    	index.setName(indexEObj.getName());
    	index.setNameInSource(indexEObj.getNameInSource());
    	setDescription(index,indexEObj);
   	
    	index.setFilterCondition(indexEObj.getFilterCondition());
    	
    	List<Column> columns = indexEObj.getColumns();
    	for(Column column : columns) {
			RelationalColumn relColumn = find(RelationalColumn.class, column, null, allRefs);
			if(relColumn==null) {
	        	RelationalPlugin.Util.log(IStatus.WARNING, 
	        			NLS.bind(Messages.relationalRefFactory_columnNotFound_forIndexCreate, column.getName()));
			} else {
				relColumn.setParent(index);
				index.addColumn(relColumn);
			}
    	}

    	setExtensionProperties(index,indexEObj);

    	return index;
    }
    
    /**
     * Create a RelationalView from an EMF View
     * @param viewEObj the EMF view
     * @return the new RelationalView object
     */
    public RelationalView createView(View viewEObj) {
    	RelationalView view = new RelationalView();
    	view.setName(viewEObj.getName());
    	view.setNameInSource(viewEObj.getNameInSource());
    	setDescription(view,viewEObj);
    	view.setMaterialized(viewEObj.isMaterialized());
    	view.setCardinality(viewEObj.getCardinality());
    	view.setSupportsUpdate(viewEObj.isSupportsUpdate());
    	view.setSystem(viewEObj.isSystem());
    	
    	List<Column> columns = viewEObj.getColumns();
    	for(Column column : columns) {
    		RelationalColumn relColumn = createColumn(column);
    		view.addColumn(relColumn);
    	}
    	
    	setExtensionProperties(view,viewEObj);

    	return view;
    }
    
    /**
     * Create a RelationalColumn from an EMF Column
     * @param columnEObj the EMF Column
     * @return the new RelationalColumn object
     */
    public RelationalColumn createColumn(Column columnEObj) {
    	RelationalColumn relCol = new RelationalColumn();
    	relCol.setName(columnEObj.getName());
    	relCol.setNameInSource(columnEObj.getNameInSource());
    	setDescription(relCol,columnEObj);
    	relCol.setDefaultValue(columnEObj.getDefaultValue());
    	relCol.setCharacterSetName(columnEObj.getCharacterSetName());
    	relCol.setDistinctValueCount(columnEObj.getDistinctValueCount());
    	relCol.setLength(columnEObj.getLength());
    	relCol.setNativeType(columnEObj.getNativeType());
    	relCol.setMaximumValue(columnEObj.getMaximumValue());
    	relCol.setMinimumValue(columnEObj.getMinimumValue());
    	relCol.setPrecision(columnEObj.getPrecision());
    	relCol.setRadix(columnEObj.getRadix());
    	relCol.setScale(columnEObj.getScale());
    	relCol.setAutoIncremented(columnEObj.isAutoIncremented());
    	relCol.setCaseSensitive(columnEObj.isCaseSensitive());
    	relCol.setCurrency(columnEObj.isCurrency());
    	relCol.setLengthFixed(columnEObj.isFixedLength());
    	relCol.setSelectable(columnEObj.isSelectable());
    	relCol.setSigned(columnEObj.isSigned());
    	relCol.setUpdateable(columnEObj.isUpdateable());

    	NullableType nullType  = columnEObj.getNullable();
    	relCol.setNullable(nullType.getLiteral());
    	
    	SearchabilityType searchabilityType  = columnEObj.getSearchability();
    	relCol.setSearchability(searchabilityType.getLiteral());

        String dTypeName = modelEditor.getName(columnEObj.getType());
    	relCol.setDatatype(dTypeName);
    	    	
    	setExtensionProperties(relCol,columnEObj);
    	
    	return relCol;
    }
    
    /**
     * Create a RelationalForeignKey from an EMF ForeignKey
     * @param fk the EMF ForeignKey
     * @param table the parent table
     * @param allRefs all RelationalReferences in the model
     * @return the new RelationalForeignKey object
     */
    public RelationalForeignKey createForeignKey(ForeignKey fk, RelationalTable table, Collection<RelationalReference> allRefs) {
    	RelationalForeignKey relFK = new RelationalForeignKey();
    	relFK.setName(fk.getName());
    	relFK.setNameInSource(fk.getNameInSource());
    	setDescription(relFK,fk);
   	
    	List<Column> columns = fk.getColumns();
    	for(Column column : columns) {
			RelationalColumn relColumn = find(RelationalColumn.class, column, table, allRefs);
			if(relColumn==null) {
	        	RelationalPlugin.Util.log(IStatus.WARNING, 
	        			NLS.bind(Messages.relationalRefFactory_columnNotFound_forFKCreate, column.getName()));
			} else {
				relFK.addColumn(relColumn);
			}
    	}
    	
    	UniqueKey uniqueKey = fk.getUniqueKey();
    	if(uniqueKey!=null) {
    		String uniqueKeyName = uniqueKey.getName();
    		if(uniqueKeyName!=null) relFK.setUniqueKeyName(uniqueKeyName);
    		BaseTable uniqueKeyTable = uniqueKey.getTable();
    		if(uniqueKeyTable!=null) {
    			String uniqueKeyTableName = uniqueKeyTable.getName();
    			if(uniqueKeyTableName!=null) {
    				relFK.setUniqueKeyTableName(uniqueKeyTableName);
    			}
    		}
    	}
    	
    	setExtensionProperties(relFK,fk);

    	return relFK;
    }
    
    /**
     * Create a RelationalPrimaryKey from an EMF PrimaryKey
     * @param pk the EMF PrimaryKey
     * @param table the parent table
     * @param allRefs all RelationalReferences in the model
     * @return the new RelationalPrimaryKey object
     */
    public RelationalPrimaryKey createPrimaryKey(PrimaryKey pk, RelationalTable table, Collection<RelationalReference> allRefs) {
    	RelationalPrimaryKey relPK = new RelationalPrimaryKey();
    	relPK.setName(pk.getName());
    	relPK.setNameInSource(pk.getNameInSource());
    	setDescription(relPK,pk);
    	
    	List<Column> columns = pk.getColumns();
    	for(Column column : columns) {
			RelationalColumn relColumn = find(RelationalColumn.class, column, table, allRefs);
			if(relColumn==null) {
	        	RelationalPlugin.Util.log(IStatus.WARNING, 
	        			NLS.bind(Messages.relationalRefFactory_columnNotFound_forPKCreate, column.getName()));
			} else {
				relPK.addColumn(relColumn);
			}
    	}
    	
    	setExtensionProperties(relPK,pk);

    	return relPK;
    }

    /**
     * Create a RelationalAccessPattern from an EMF AccessPattern
     * @param ap the EMF AccessPattern
     * @param table the parent table
     * @param allRefs all RelationalReferences in the model
     * @return the new RelationalAccessPattern object
     */
    public RelationalAccessPattern createAccessPattern(AccessPattern ap, RelationalTable table, Collection<RelationalReference> allRefs) {
    	RelationalAccessPattern relAP = new RelationalAccessPattern();
    	relAP.setName(ap.getName());
    	relAP.setNameInSource(ap.getNameInSource());
    	setDescription(relAP,ap);
    	
    	List<Column> columns = ap.getColumns();
    	for(Column column : columns) {
			RelationalColumn relColumn = find(RelationalColumn.class, column, table, allRefs);
			if(relColumn==null) {
	        	RelationalPlugin.Util.log(IStatus.WARNING, 
	        			NLS.bind(Messages.relationalRefFactory_columnNotFound_forAPCreate, column.getName()));
			} else {
				relAP.addColumn(relColumn);
			}
    	}
    	
    	setExtensionProperties(relAP,ap);
    	
    	return relAP;
    }

    /**
     * Create a RelationalProcedure from an EMF Procedure
     * @param procEObj the EMF Procedure
     * @return the new RelationalProcedure object
     */
    public RelationalProcedure createProcedure(Procedure procEObj) {
    	RelationalProcedure proc = new RelationalProcedure();
    	proc.setName(procEObj.getName());
    	proc.setNameInSource(procEObj.getNameInSource());
    	setDescription(proc,procEObj);
    	
    	ProcedureResult procResult = procEObj.getResult();
    	if (procResult != null) {
    	    RelationalProcedureResultSet relProcResult = createProcedureResultSet(procResult);
    	    if(relProcResult!=null) {
    	        proc.setResultSet(relProcResult);
    	    }
    	}

    	List<ProcedureParameter> params = procEObj.getParameters();
    	for(ProcedureParameter param : params) {
    		RelationalParameter relProcParam = createProcedureParameter(param);
    		proc.addParameter(relProcParam);
    	}
    	
    	setExtensionProperties(proc,procEObj);

    	return proc;
    }
    
    /**
     * Create a RelationalProcedureResultSet from an EMF ProcedureResult
     * @param procResultSetEObj the EMF ProcedureResult
     * @return the new RelationalProcedureResultSet object
     */
    public RelationalProcedureResultSet createProcedureResultSet(ProcedureResult procResultSetEObj) {
    	RelationalProcedureResultSet relResultSet = new RelationalProcedureResultSet();
    	relResultSet.setName(procResultSetEObj.getName());
    	relResultSet.setNameInSource(procResultSetEObj.getNameInSource());
    	setDescription(relResultSet,procResultSetEObj);
    	    	
    	List<Column> columns = procResultSetEObj.getColumns();
    	for(Column column : columns) {
    		RelationalColumn relColumn = createColumn(column);
    		if(relColumn!=null) relResultSet.addColumn(relColumn);
    	}

    	setExtensionProperties(relResultSet,procResultSetEObj);

    	return relResultSet;
    }
    
    /**
     * Create a RelationalParameter from an EMF ProcedureParameter
     * @param procParamEObj the EMF ProcedureParameter
     * @return the new RelationalParameter object
     */
    public RelationalParameter createProcedureParameter(ProcedureParameter procParamEObj) {
    	RelationalParameter relParam = new RelationalParameter();
    	relParam.setName(procParamEObj.getName());
    	relParam.setNameInSource(procParamEObj.getNameInSource());
    	setDescription(relParam,procParamEObj);
   	
    	relParam.setDirection(getDirectionKindStr(procParamEObj.getDirection()));
    	
    	relParam.setDefaultValue(procParamEObj.getDefaultValue());
    	relParam.setLength(procParamEObj.getLength());
    	relParam.setNativeType(procParamEObj.getNativeType());
    	relParam.setNullable(getNullableTypeStr(procParamEObj.getNullable()));
    	relParam.setPrecision(procParamEObj.getPrecision());
    	relParam.setRadix(procParamEObj.getRadix());
    	relParam.setScale(procParamEObj.getScale());
    	    	
        String dTypeName = ModelerCore.getModelEditor().getName(procParamEObj.getType());
        relParam.setDatatype(dTypeName);
    	    	
    	setExtensionProperties(relParam,procParamEObj);

    	return relParam;
    }

    private String getDirectionKindStr(DirectionKind dir) {
    	if(dir==DirectionKind.IN_LITERAL) {
    		return DIRECTION.IN;
    	} else if(dir==DirectionKind.INOUT_LITERAL) {
    		return DIRECTION.IN_OUT;
    	} else if(dir==DirectionKind.OUT_LITERAL) {
    		return DIRECTION.OUT;
    	} else if(dir==DirectionKind.RETURN_LITERAL) {
    		return DIRECTION.RETURN;
    	}
    	return DIRECTION.UNKNOWN;
    }
    
    private String getNullableTypeStr(NullableType nType) {
    	if(nType==NullableType.NULLABLE_LITERAL) {
    		return NULLABLE.NULLABLE;
    	} else if(nType==NullableType.NO_NULLS_LITERAL) {
    		return NULLABLE.NO_NULLS;
    	}
    	return NULLABLE.NULLABLE_UNKNOWN;
    }
    
	/**
	 * @param type type of RelationalReference to find
	 * @param name the node name
	 * @param node the AstNode
	 * @param parent the parent reference
	 * @param allModelRefs the collection of all model RelationalReferences
	 *
	 * @return RelationalReference which is a match
	 *
	 * @throws EntityNotFoundException
	 * @throws CoreException
	 */
	protected <T extends RelationalReference> T find(Class<T> type, String name, EObject node,
			RelationalReference parent, Collection<RelationalReference> allModelRefs) {
		
		// Look through all refs list for a matching object
		for ( RelationalReference obj : allModelRefs) {
			if (type.isInstance(obj)) {
				T relEntity = (T)obj;
				if (relEntity.getName().equalsIgnoreCase(name)) {
					RelationalReference relParent = relEntity.getParent();
					if(parent!=null) {
						if(relParent.getName().equalsIgnoreCase(parent.getName())) {
							return relEntity;
						}
					} else {
						return relEntity;
					}
				}
			}
		}
		return null;
//		throw new Exception();
//		throw new EntityNotFoundException(I18n.format(DdlImporterI18n.ENTITY_NOT_FOUND_MSG,
//				type.getSimpleName(),
//				name,
//				DdlImporterI18n.MODEL,
//				parent == null ? getImporterManager().getModelName() : parent.getName(),
//						node.getProperty(StandardDdlLexicon.DDL_START_LINE_NUMBER).toString(),
//						node.getProperty(StandardDdlLexicon.DDL_START_COLUMN_NUMBER).toString()));
	}

	/**
	 * @param type type of RelationalReference to find
	 * @param eObj the EObject
	 * @param parent the parent reference
	 * @param allModelRefs the collection of all model RelationalReferences
	 * @return RelationalReference which is a match
	 *
	 * @throws EntityNotFoundException
	 * @throws CoreException
	 */
	protected <T extends RelationalReference> T find(Class<T> type, EObject eObj, RelationalReference parent, Collection<RelationalReference> allModelRefs) {
		return find(type, this.modelEditor.getName(eObj), eObj, parent, allModelRefs);
	}
	
    /**
     * Create a Relational Model
     * @param modelName the name of the model
     * @return the new object
     */
    public RelationalModel createModel(String modelName) {
    	return new RelationalModel(modelName);
    }

    /**
     * Create a RelationalSchema
     * @return the new object
     */
    public RelationalSchema createSchema( ) {
    	return new RelationalSchema();
    }

    /**
     * Create a RelationalTable
     * @return the new object
     */
    public RelationalTable createBaseTable( ) {
    	return new RelationalTable();
    }
    
    /**
     * Create a Relational Table
     * @param name the name of the object
     * @return the new object
     */
    public RelationalTable createBaseTable(String name) {
    	return new RelationalTable(name);
    }

    /**
     * Create a RelationalView
     * @return the new object
     */
    public RelationalView createView( ) {
    	return new RelationalView();
    }

    /**
     * Create a RelationalView
     * @param name the name of the object
     * @return the new object
     */
    public RelationalView createView(String name) {
    	return new RelationalView(name);
    }
    
    /**
     * Create a RelationalColumn
     * @return the new object
     */
    public RelationalColumn createColumn( ) {
    	return new RelationalColumn();
    }

    /**
     * Create a RelationalAccessPattern
     * @return the new object
     */
    public RelationalAccessPattern createAccessPattern( ) {
    	return new RelationalAccessPattern();
    }
    
    /**
     * Create a RelationalForeignKey
     * @return the new object
     */
    public RelationalForeignKey createForeignKey( ) {
    	return new RelationalForeignKey();
    }
    
    /**
     * Create a RelationalIndex
     * @return the new object
     */
    public RelationalIndex createIndex( ) {
    	return new RelationalIndex();
    }
    
    /**
     * Create a RelationalParameter
     * @return the new object
     */
    public RelationalParameter createParameter( ) {
    	return new RelationalParameter();
    }
    
    /**
     * Create a Relational PrimaryKey
     * @return the new object
     */
    public RelationalPrimaryKey createPrimaryKey( ) {
    	return new RelationalPrimaryKey();
    }
    
    /**
     * Create a RelationalProcedure
     * @return the new object
     */
    public RelationalProcedure createProcedure( ) {
    	return new RelationalProcedure();
    }
    
    /**
     * Create a RelationalProcedureResultSet
     * @return the new object
     */
    public RelationalProcedureResultSet createProcedureResultSet( ) {
    	return new RelationalProcedureResultSet();
    }
    
    /**
     * Create a RelationalUniqueConstraint
     * @return the new object
     */
    public RelationalUniqueConstraint createUniqueConstraint( ) {
    	return new RelationalUniqueConstraint();
    }
    
    /**
     * Create a RelationalTable
     * @return the new object
     */
    public RelationalViewTable createViewTable( ) {
    	return new RelationalViewTable();
    }
    
    /**
     * Create a RelationalTable
     * @return the new object
     */
    public RelationalViewProcedure createViewProcedure( ) {
    	return new RelationalViewProcedure();
    }
        
}
