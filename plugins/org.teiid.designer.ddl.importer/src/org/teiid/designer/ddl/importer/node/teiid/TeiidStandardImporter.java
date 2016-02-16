/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node.teiid;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.I18n;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.ddl.DdlImporterManager;
import org.teiid.designer.ddl.importer.DdlImporterI18n;
import org.teiid.designer.ddl.importer.node.AbstractImporter;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMappingImpl;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalProcedureResultSet;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalSchema;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.modeshape.sequencer.ddl.DdlConstants;
import org.teiid.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.teiid.modeshape.sequencer.ddl.node.AstNode;

public class TeiidStandardImporter  extends TeiidAbstractImporter {

	class Info {

		protected RelationalSchema schema;
		protected String name;

		public Info(AstNode node, RelationalModel model) throws Exception {
			init(node, model);
		}

		protected void init(AstNode node, RelationalModel model) throws Exception {
			schema = null;
			this.name = node.getName();
		}

		protected String removeLeadingTrailingTicks(String name) {
			String resultName = name;
			if(name!=null && name.length()>2 & name.startsWith("`") && name.endsWith("`")) { //$NON-NLS-1$ //$NON-NLS-2$
				resultName = name.substring(1, name.length()-1);
			}
			return resultName;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * @return the schema
		 */
		public RelationalSchema getSchema() {
			return schema;
		}
	}

	protected static final String STRING_TYPENAME = "string"; //$NON-NLS-1$
	
	protected static final String CHAR_TYPENAME = "char"; //$NON-NLS-1$
	
	/**
	 * see <code>org.teiid.core.types.JDBCSQLTypeInfo.java</code> for details
	 * FLOAT, DOUBLE, BIG_DECIMAL all show 20 as the default precision
	 */
	protected static final int DEFAULT_PRECISION = 0; 

	/**
	 * Create new info object
	 *
	 * @param node the AstNode
	 * @param model the RelationalModel
	 *
	 * @return new info object
	 *
	 * @throws Exception
	 */
	protected Info createInfo(AstNode node, RelationalModel model) throws Exception {
		return new Info(node, model);
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
	protected <T extends RelationalReference> T find(Class<T> type, String name, AstNode node,
			RelationalReference parent, Collection<RelationalReference> allModelRefs) throws EntityNotFoundException, CoreException {
		
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

		while (node.getProperty(StandardDdlLexicon.DDL_EXPRESSION) == null) {
			node = node.getParent();
		}

		throw new EntityNotFoundException(I18n.format(DdlImporterI18n.ENTITY_NOT_FOUND_MSG,
				type.getSimpleName(),
				name,
				DdlImporterI18n.MODEL,
				parent == null ? getImporterManager().getModelName() : parent.getName(),
						node.getProperty(StandardDdlLexicon.DDL_START_LINE_NUMBER).toString(),
						node.getProperty(StandardDdlLexicon.DDL_START_COLUMN_NUMBER).toString()));
	}

	/**
	 * @param type type of RelationalReference to find
	 * @param node the AstNode
	 * @param parent the parent reference
	 * @param allModelRefs the collection of all model RelationalReferences
	 * @return RelationalReference which is a match
	 *
	 * @throws EntityNotFoundException
	 * @throws CoreException
	 */
	protected <T extends RelationalReference> T find(Class<T> type, AstNode node, RelationalReference parent, Collection<RelationalReference> allModelRefs) throws EntityNotFoundException, CoreException {
		String nodeName = node.getName();

		return find(type, nodeName, node, parent, allModelRefs);
	}

	/**
	 * Initialize the RelationalReference
	 * @param entity the object
	 * @param node the corresponding AstNode
	 * @param name the name for the object
	 */
	protected void initialize(RelationalReference entity, AstNode node, String name) {
		entity.setName(name);

		// descriptions must wait to be set until container and model type has been set
		if (getImporterManager().optToSetModelEntityDescription()) {
			Object prop = node.getProperty(StandardDdlLexicon.DDL_EXPRESSION);
			if (prop != null) {
				entity.setDescription(prop.toString());
			} else {
				entity.setDescription(""); //$NON-NLS-1$
			}
		}
	}


	/**
	 * Initialize the RelationalReference
	 * @param entity the object
	 * @param node the corresponding AstNode
	 */
	protected void initialize(RelationalReference entity, AstNode node) {
		initialize(entity, node, node.getName());
	}

	/**
	 * Helper method for creating unique FK names
	 * @param currentFKs the List of ForeignKeys currently on the table
	 * @param newFKName the proposed name for the new FK
	 * @return the unique name - generated from the proposed name
	 */
	protected String getUniqueFKName(Collection<RelationalForeignKey> currentFKs, String newFKName) {
		// If current list is empty, no need to check names
		if (currentFKs == null || currentFKs.isEmpty()) return newFKName;

		// Use name validator for unique name generation
		StringNameValidator nameValidator = new StringNameValidator();

		// Add the current FK names to the validator
		for (RelationalForeignKey fk : currentFKs) {
			nameValidator.addExistingName(fk.getName());
		}

		// Make the proposed name unique
		return nameValidator.createValidUniqueName(newFKName);
	}

	/**
	 * Initialize a ForeignKey
	 * @param currentFKs collection of current FKs
	 * @param key a ForeignKey
	 * @param node corresponding AstNode
	 */
	protected void initializeFK(Collection<RelationalForeignKey> currentFKs, RelationalForeignKey key, AstNode node) {
		// Get Name from DDL node
		String fkName = node.getName();
		// Make sure not to add duplicate FK names
		String uniqueName = getUniqueFKName(currentFKs, fkName);

		initialize(key, node, uniqueName);
	}

	/**
	 * Initialize a RelationalTable
	 * @param table the Table to init
	 * @param node corresponding AstNode
	 * @param model the RelationalModel
	 * @return the initialized Table
	 * @throws Exception
	 */
	protected <T extends RelationalTable> T initializeTable(T table, AstNode node, RelationalModel model) throws Exception {
		Info info = createInfo(node, model);
		if (info.getSchema() == null)
			model.addChild(table);
		else
			info.getSchema().getTables().add(table);

		initialize(table, node, info.getName());
		return table;
	}

	/**
	 * Handle a statement OPTION key for Column for DDL
	 *
	 * @param column the ColumngetTeiidDataTypeName
	 * @param columnOptionNode a statementOption node for a column
	 */
	protected void handleColumnOption(RelationalColumn column, AstNode columnOptionNode) {
		// Do nothing
	}

	/**
	 * Handle the OPTION keys that may be set on Column for DDL
	 *
	 * @param column the Column
	 * @param columnNode the column AstNode
	 */
	protected void handleColumnOptions(RelationalColumn column, AstNode columnNode) {
		List<AstNode> children = columnNode.getChildren();
		for(AstNode child: children) {
			if(is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
				handleColumnOption(column,child);
			}
		}
	}

	/**
	 * @param datatype
	 *
	 * @return {@link EObject} represented by the given data type id
	 * @throws Exception
	 */
	protected String getTeiidDataTypeName(String datatype) throws Exception {
		String typeName = ""; //$NON-NLS-1$
		EObject dataType = RelationalTypeMappingImpl.getInstance().getDatatype(datatype);
		if(dataType!=null) {
			typeName = ModelerCore.getWorkspaceDatatypeManager().getName(dataType);
		}
		return typeName;
	}

	/**
	 * Create Column from the provided AstNode within ColumnSet
	 * @param node the provided AstNode
	 * @param table the ColumnSet in which to create the column
	 * @return the column
	 *
	 * @throws Exception 
	 */
	protected RelationalColumn createColumn(AstNode node, RelationalTable table) throws Exception {
		RelationalColumn col = getFactory().createColumn();
		col.setParent(table);
		table.getColumns().add(col);
		initialize(col, node);

		setDataType(node, col);

		return col;
	}
	
	/**
	 * Create Column from the provided AstNode within ColumnSet
	 * @param node the provided AstNode
	 * @param column 
	 *
	 * @throws Exception 
	 */
	protected void setDataType(AstNode node, RelationalColumn column) throws Exception {
		String datatype = node.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
		column.setNativeType(datatype);

		String teiidType = getTeiidDataTypeName(datatype);
		column.setDatatype(teiidType);

		// Datatype length
		Object prop = node.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
		if (prop != null) {
			column.setLength(Integer.parseInt(prop.toString()));
		} else {
			// Length is not provided for type 'string', use the default length specified in preferences...
			//String dtName = ModelerCore.getWorkspaceDatatypeManager().getName(type);
			if(teiidType != null) {
				if( teiidType.equalsIgnoreCase(STRING_TYPENAME)) {
					column.setLength(ModelerCore.getTransformationPreferences().getDefaultStringLength());
				} else if( teiidType.equalsIgnoreCase(CHAR_TYPENAME) ) {
					column.setLength(1);
				}
			}
		}

		prop = node.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
		if (prop != null) {
			column.setPrecision(Integer.parseInt(prop.toString()));
		} else {
			// IF type == FLOAT, BIG_DECIMAL, DECIMAL, then set precision to at least 1
			if( teiidType.equalsIgnoreCase(IDataTypeManagerService.DataTypeName.BIGDECIMAL.name()) ||
					teiidType.equalsIgnoreCase(IDataTypeManagerService.DataTypeName.DECIMAL.name()) ||
					teiidType.equalsIgnoreCase(IDataTypeManagerService.DataTypeName.FLOAT.name()) ||
					teiidType.equalsIgnoreCase(IDataTypeManagerService.DataTypeName.DOUBLE.name())) {
				column.setPrecision(DEFAULT_PRECISION);
			}
		}

		prop = node.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
		if (prop != null)
			column.setScale(Integer.parseInt(prop.toString()));

		prop = node.getProperty(StandardDdlLexicon.NULLABLE);
		if (prop != null)
			column.setNullable(getRelRefNullable(prop.toString())); 

		prop = node.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
		if (prop != null)
			column.setDefaultValue(prop.toString());
	}

	/**
	 * Create ProcedureParameter from the provided AstNode within procedure
	 * @param node the provided AstNode
	 * @param procedure the Procedure in which to create the procedure parameter
	 * @return the procedure parameter
	 *
	 * @throws Exception 
	 */
	protected RelationalParameter createProcedureParameter(AstNode node, RelationalProcedure procedure) throws Exception {
		RelationalParameter prm = getFactory().createParameter();
		procedure.getParameters().add(prm);
		initialize(prm, node);

		String datatype = node.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
		prm.setNativeType(datatype);

		String teiidType = getTeiidDataTypeName(datatype);
		prm.setDatatype(teiidType);

		// Datatype length
		Object prop = node.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
		if (prop != null) {
			prm.setLength(Integer.parseInt(prop.toString()));
		} 
//		else {
//			 Length is not provided for type 'string', use the default length specified in preferences...
//			String dtName = ModelerCore.getWorkspaceDatatypeManager().getName(type);
//			if(teiidType != null && teiidType.equalsIgnoreCase(STRING_TYPENAME)) {
//				prm.setLength(ModelerCore.getTransformationPreferences().getDefaultStringLength());
//			}
//		}

		prop = node.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
		if (prop != null)
			prm.setPrecision(Integer.parseInt(prop.toString()));

		prop = node.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
		if (prop != null)
			prm.setScale(Integer.parseInt(prop.toString()));

		prop = node.getProperty(StandardDdlLexicon.NULLABLE);
		if (prop != null)
			prm.setNullable(getRelRefNullable(prop.toString())); 

		prop = node.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
		if (prop != null)
			prm.setDefaultValue(prop.toString());

		return prm;
	}

	/**
	 * Create a PrimaryKey
	 * @param node the AstNode representing the primary key
	 * @param table the parent Table
	 * @param allRefs the Collection of all RelationalReference objects in the model
	 *
	 * @throws CoreException
	 */
	protected void createPrimaryKey(AstNode node, RelationalTable table, Collection<RelationalReference> allRefs) throws CoreException {
		String type = node.getProperty(StandardDdlLexicon.CONSTRAINT_TYPE).toString();
		if (DdlConstants.PRIMARY_KEY.equals(type)) {
			RelationalPrimaryKey key = getFactory().createPrimaryKey();
			table.setPrimaryKey(key);
			initialize(key, node);

			for (AstNode node1 : node) {
				if (is(node1, StandardDdlLexicon.TYPE_COLUMN_REFERENCE)) {
					try {
						RelationalColumn column = find(RelationalColumn.class, node1, table, allRefs);
						//                        if (column.getNullable() == NullableType.NULLABLE_UNKNOWN_LITERAL
						//                            || column.getNullable() == NullableType.NULLABLE_LITERAL) {
						//                            column.setNullable(NullableType.NO_NULLS_LITERAL);
						//                        }
						key.getColumns().add(column);
					} catch (EntityNotFoundException error) {
						addProgressMessage(error.getMessage());
					}
				}
			}
		}
	}
	
	/**
	 * Create a Constraint
	 * @param node the AstNode representing the constraint
	 * @param table the parent Table
	 * @param allRefs the Collection of all RelationalReference objects in the model
	 *
	 * @throws CoreException
	 */
	protected void createConstraint(AstNode node, RelationalTable table, Collection<RelationalReference> allRefs) throws CoreException {
		String type = node.getProperty(StandardDdlLexicon.CONSTRAINT_TYPE).toString();
		if (DdlConstants.FOREIGN_KEY.equals(type)) {
			RelationalForeignKey key = getFactory().createForeignKey();
			initializeFK(table.getForeignKeys(), key, node);
			table.getForeignKeys().add(key);
			RelationalTable foreignTable = null;
			Set<RelationalColumn> foreignColumns = new HashSet<RelationalColumn>();

			for (AstNode node1 : node) {
				try {
					if (is(node1, StandardDdlLexicon.TYPE_COLUMN_REFERENCE))
						key.getColumns().add(find(RelationalColumn.class, node1, table, allRefs));
					else if (is(node1, StandardDdlLexicon.TYPE_TABLE_REFERENCE))
						foreignTable = find(RelationalTable.class, node1, null, allRefs);
					else if (is(node1, StandardDdlLexicon.TYPE_FK_COLUMN_REFERENCE) && foreignTable != null) {
						foreignColumns.add(find(RelationalColumn.class, node1, foreignTable, allRefs));
					}
				} catch (Exception error) {
					addProgressMessage(error.getMessage());
				}
			}

			if (foreignTable == null)
				return;

			RelationalPrimaryKey primaryKey = foreignTable.getPrimaryKey();
			Collection<RelationalColumn> primaryKeyColumns = primaryKey.getColumns();
			if (foreignColumns.isEmpty()) {
				key.setUniqueKeyName(primaryKey.getName());
				key.setUniqueKeyTableName(foreignTable.getName());
			}
			if (primaryKeyColumns.containsAll(foreignColumns) && primaryKeyColumns.size() == foreignColumns.size()) {
				key.setUniqueKeyName(primaryKey.getName());
				key.setUniqueKeyTableName(foreignTable.getName());
			} else {
				for (Object obj : foreignTable.getUniqueConstraints()) {
					RelationalUniqueConstraint uniqueKey = (RelationalUniqueConstraint)obj;
					Collection<RelationalColumn> uniqueKeyColumns = uniqueKey.getColumns();
					if (uniqueKeyColumns.containsAll(foreignColumns) && uniqueKeyColumns.size() == foreignColumns.size()) {
						key.setUniqueKeyName(uniqueKey.getName());
						key.setUniqueKeyTableName(foreignTable.getName());
						break;
					}
				}
			}

		} else if (DdlConstants.UNIQUE.equals(type)) {
			RelationalUniqueConstraint key = getFactory().createUniqueConstraint();
			table.getUniqueConstraints().add(key);
			initialize(key, node);

			for (AstNode node1 : node) {
				if (! is(node1, StandardDdlLexicon.TYPE_COLUMN_REFERENCE))
					continue;

				try {
					RelationalColumn column = find(RelationalColumn.class, node1, table, allRefs);

					//                    if (column.getNullable() == NullableType.NULLABLE_UNKNOWN_LITERAL || column.getNullable() == NullableType.NULLABLE_LITERAL) {
						//                        column.setNullable(NullableType.NO_NULLS_LITERAL);
						//                    }
					key.getColumns().add(column);
				} catch (Exception error) {
					addProgressMessage(error.getMessage());
				}
			}
		}
	}

	/**
	 * Create a RelationalProcedure
	 * @param procedureNode the AstNode for the procedure
	 * @param model the RelationalModel
	 * @return the RelationalProcedure
	 *
	 * @throws Exception
	 */
	protected RelationalProcedure createProcedure( AstNode procedureNode, RelationalModel model) throws Exception {
		RelationalProcedure procedure = getFactory().createProcedure();
		Info info = createInfo(procedureNode, model);
		if (info.getSchema() == null)
			model.addChild(procedure);
		else {
			info.getSchema().getProcedures().add(procedure);
			procedure.setParent(info.getSchema());
		}

		initialize(procedure, procedureNode, info.getName());
		// TODO: determine how to handle Procedure StatementOption
		// TODO: determine how to handle Procedure Statement

		if (procedureNode.getProperty(StandardDdlLexicon.DATATYPE_NAME) != null) {
			RelationalProcedureResultSet result = getFactory().createProcedureResultSet();
			procedure.setResultSet(result);
			initialize(result, procedureNode);
		}

		return procedure;
	}
	
	/**
	 * Create a RelationalProcedure
	 * @param procedureNode the AstNode for the procedure
	 * @param model the RelationalModel
	 * @return the RelationalProcedure
	 *
	 * @throws Exception
	 */
	protected RelationalProcedure createViewProcedure( AstNode procedureNode, RelationalModel model) throws Exception {
		RelationalProcedure procedure = getFactory().createViewProcedure();
		Info info = createInfo(procedureNode, model);
		if (info.getSchema() == null)
			model.addChild(procedure);
		else {
			info.getSchema().getProcedures().add(procedure);
			procedure.setParent(info.getSchema());
		}

		initialize(procedure, procedureNode, info.getName());
		// TODO: determine how to handle Procedure StatementOption
		// TODO: determine how to handle Procedure Statement

		if (procedureNode.getProperty(StandardDdlLexicon.DATATYPE_NAME) != null) {
			RelationalProcedureResultSet result = getFactory().createProcedureResultSet();
			procedure.setResultSet(result);
			initialize(result, procedureNode);
		}

		return procedure;
	}

	/**
	 * Perform the import
	 * @param rootNode the rootNode of the DDL
	 * @param importManager the import manager which maintains import options
	 * 	 * @param props the custom properties for import
	 * @return the RelationalModel created
	 * @throws Exception
	 */
	@Override
	public RelationalModel importNode(AstNode rootNode, DdlImporterManager importManager, Properties props) throws Exception {

		setImporterManager(importManager);

		// Create a RelationalModel for the imported DDL
		RelationalModel model = getFactory().createModel("ddlImportedModel"); //$NON-NLS-1$

		// Map for holding deferred nodes, which much be created later
		Map<AstNode,RelationalReference> deferredCreateMap = new HashMap<AstNode,RelationalReference>();

		// Create objects from the DDL.  (populated map of deferred nodes)
		for (AstNode node : rootNode) {
			if (is(node, StandardDdlLexicon.TYPE_CREATE_SCHEMA_STATEMENT)) {
				RelationalSchema schema = getFactory().createSchema();
				model.addChild(schema);
				initialize(schema, node);
				for (AstNode node1 : node) {
					Map<AstNode,RelationalReference> deferredMap = createObject(node1, model, schema);
					if(!deferredMap.isEmpty()) {
						deferredCreateMap.putAll(deferredMap);
					}
				}
			} else {
				Map<AstNode,RelationalReference> deferredMap = createObject(node, model, null);
				if(!deferredMap.isEmpty()) {
					deferredCreateMap.putAll(deferredMap);
				}
			}
		}

		// Now process all the 'deferred' nodes.  These are nodes which reference other nodes (which are required to exist first)
		createDeferredObjects(deferredCreateMap,model);

		return model;
	}

	/**
	 * Create RelationalReference objects
	 * @param node the provided AstNode
	 * @param model the RelationalModel being created
	 * @param schema the schema
	 * @return the map of AstNodes which need to be deferred
	 * @throws Exception 
	 */
	protected Map<AstNode,RelationalReference> createObject(AstNode node, RelationalModel model, RelationalSchema schema) throws Exception {
		Map<AstNode,RelationalReference> deferredMap = new HashMap<AstNode,RelationalReference>();

		// -----------------------------------------------------------------------
		// Standard DDL 
		// -----------------------------------------------------------------------
		if (is(node, StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT)) {
			RelationalTable table = initializeTable(getFactory().createBaseTable(), node, model);
			for (AstNode child : node) {
				if (is(child, StandardDdlLexicon.TYPE_COLUMN_DEFINITION))
					createColumn(child, table);
				else if (is(child, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT)) {
					deferredMap.put(child, table);
				}
			}
		} else if (is(node, StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT)) {
			if (getImporterManager().getModelType() != ModelType.VIRTUAL_LITERAL 
					&& getImporterManager().optToCreateModelEntitiesForUnsupportedDdl())

				initializeTable(getFactory().createView(), node, model);

		} else if (is(node, StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT)) {
			deferredMap.put(node, null);
		// Unhandled node - get the mixin type and increment the count
		} else {
			StringBuffer sb = new StringBuffer();
			List<String> mixins = node.getMixins();
			Iterator<String> iter = mixins.iterator();
			while(iter.hasNext()) {
				String mixin = iter.next();
				sb.append(mixin);
				if(iter.hasNext()) sb.append(","); //$NON-NLS-1$
			}
			getImporterManager().getImportMessages().incrementUnhandledNodeType(sb.toString());
		}
		return deferredMap;
	}

	/**
	 * Create deferred objects using the supplied map
	 * @param deferredNodes the map of deferred AstNodes
	 * @param model the RelationalModel being created
	 * @throws Exception 
	 */
	protected void createDeferredObjects(Map<AstNode,RelationalReference> deferredNodes, RelationalModel model) throws Exception {
		Collection<RelationalReference> allRefs = model.getAllReferences();

		// Make first pass to create the PKs
		Set<AstNode> astNodes = deferredNodes.keySet();
		for(AstNode node:astNodes) {
			if (is(node, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT)) {
				RelationalTable table = (RelationalTable)deferredNodes.get(node);
				createPrimaryKey(node, table, allRefs);
			} else if (is(node, StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT)) {
				RelationalTable table = find(RelationalTable.class, node, null, allRefs);
				for (AstNode node1 : node) {
					if (is(node1, StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION)) 
						createPrimaryKey(node1, table, allRefs);
				}
			}
		}

		// Second pass create other constraints
		for(AstNode node:astNodes) {
			if (is(node, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT)) {
				RelationalTable table = (RelationalTable)deferredNodes.get(node);
				createConstraint(node, table, allRefs);
			} else if (is(node, StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT)) {
				RelationalTable table = find(RelationalTable.class, node, null, allRefs);
				for (AstNode node1 : node) {
					if (is(node1, StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION)) 
						createConstraint(node1, table, allRefs);
					else if (is(node1, StandardDdlLexicon.TYPE_ADD_COLUMN_DEFINITION))
						createColumn(node1, table);
				}
			}
		}
	}
	
	/**
	 * Get the RelationalReference nullable string for the provided ast nullable property value
	 * @param astNullableStr
	 * @return RelationalReference nullable string
	 */
	protected String getRelRefNullable(String astNullableStr) {
		String nullableStr = "NULLABLE_UNKNOWN"; //$NON-NLS-1$
		if(astNullableStr!=null) {
			if(astNullableStr.equalsIgnoreCase("null")) { //$NON-NLS-1$
				nullableStr = "NULLABLE"; //$NON-NLS-1$
			} else if(astNullableStr.equalsIgnoreCase("not null")) { //$NON-NLS-1$
				nullableStr = "NO_NULLS"; //$NON-NLS-1$
			}
		}
		return nullableStr;
	}
    
}
