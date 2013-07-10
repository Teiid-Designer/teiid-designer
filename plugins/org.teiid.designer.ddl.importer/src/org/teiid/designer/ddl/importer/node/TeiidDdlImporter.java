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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlConstants;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ddl.importer.DdlImporterI18n;
import org.teiid.designer.ddl.importer.TeiidDDLConstants;
import org.teiid.designer.relational.model.RelationalAccessPattern;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalForeignKey;
import org.teiid.designer.relational.model.RelationalIndex;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalPrimaryKey;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalProcedureResultSet;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalSchema;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalUniqueConstraint;


/**
 * Teiid DDL node importer
 */
public class TeiidDdlImporter extends StandardImporter {

	private class TeiidInfo extends Info {

		/**
		 * @param node the AstNode
		 * @param model the RelationalModel
		 *
		 * @throws Exception
		 */
		public TeiidInfo(AstNode node, RelationalModel model) throws Exception {
			super(node, model);
		}

		@Override
		protected void init(AstNode node, RelationalModel model) throws Exception {
			String name = node.getName();
			int ndx = name.indexOf('.');
			if (ndx >= 0) {
				this.schema = null;
				this.name = name.substring(ndx + 1);
			} else
				super.init(node, model);
		}
	}

	@Override
	protected TeiidInfo createInfo(AstNode node, RelationalModel model) throws Exception {
		return new TeiidInfo(node, model);
	}

	@Override
	protected String getTeiidDataTypeName(String datatype) throws Exception {
		String resultTypeName = null;

		/*
		 * Get the Datatype for Teiid DDL.
		 * First tries to match the datatype string with a teiid built-in type.
		 * If a built-in type is not found, then attempt to use the relational mapping to find a match.
		 */

		// Look up matching Built-In type
		EObject[] builtInTypes = ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
		String dtName = null;
		for (int i = 0; i < builtInTypes.length; i++) {
			dtName = ModelerCore.getWorkspaceDatatypeManager().getName(builtInTypes[i]);
			if (dtName != null && dtName.equalsIgnoreCase(datatype)) {
				resultTypeName = dtName;
				break;
			}
		}

		// Built In type not found, try mapping from native to built-in
		if(resultTypeName == null) {
			resultTypeName = super.getTeiidDataTypeName(datatype);
		}

		return resultTypeName;
	}

	/**
	 * Creates constraints for Table for Teiid DDL
	 * @param constraintNode the AstNode for the constraint
	 * @param table the RelationalTable object
	 * @param model the RelationalModel
	 * @param allRefs the collection of all RelationalReference objects in the model
	 *
	 * @throws CoreException
	 */
	private void createConstraint(AstNode constraintNode, RelationalTable table, RelationalModel model, Collection<RelationalReference> allRefs) throws CoreException {

		String type = constraintNode.getProperty(TeiidDdlLexicon.Constraint.TYPE).toString();
		boolean primaryKeyConstraint = false;
		boolean uniqueConstraint = false;
		boolean accessPatternConstraint = false;
		boolean foreignKeyConstraint = false;
		boolean indexConstraint = false;
		RelationalReference key = null;

		if (DdlConstants.PRIMARY_KEY.equals(type)) {
			key = getFactory().createPrimaryKey();
			initialize(key, constraintNode);
			table.setPrimaryKey((RelationalPrimaryKey)key);
			primaryKeyConstraint = true;
		} else if (DdlConstants.INDEX.equals(type)) {
			// TODO need to process teiidddl:expression property
			key = getFactory().createIndex();
			initialize(key, constraintNode);
			model.addChild(key);
			indexConstraint = true;
		} else if (DdlConstants.UNIQUE.equals(type)) {
			key = getFactory().createUniqueConstraint();
			initialize(key, constraintNode);
			table.getUniqueConstraints().add((RelationalUniqueConstraint)key);
			uniqueConstraint = true;
		} else if (TeiidDdlConstants.TeiidNonReservedWord.ACCESSPATTERN.toDdl().equals(type)) {
			key = getFactory().createAccessPattern();
			initialize(key, constraintNode);
			table.getAccessPatterns().add((RelationalAccessPattern)key);
			accessPatternConstraint = true;
		} else if (DdlConstants.FOREIGN_KEY.equals(type)) {
			key = getFactory().createForeignKey();
			initializeFK(table.getForeignKeys(), (RelationalForeignKey)key, constraintNode);
			table.getForeignKeys().add((RelationalForeignKey)key);
			foreignKeyConstraint = true;
		} else {
			assert false : "Unexpected constraint type of '" + type + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		// process referenced columns multi-valued property
		Object temp = constraintNode.getProperty(TeiidDdlLexicon.Constraint.REFERENCES);
		List<AstNode> references = (List<AstNode>)temp;

		for (AstNode ref : references) {
			try {
				RelationalColumn col = find(RelationalColumn.class, ref, table, allRefs);
				if(col!=null) {
					if (primaryKeyConstraint) {
						((RelationalPrimaryKey)key).getColumns().add(col);
					} else if (uniqueConstraint) {
						((RelationalUniqueConstraint)key).getColumns().add(col);
					} else if (accessPatternConstraint) {
						((RelationalAccessPattern)key).getColumns().add(col);
					} else if (foreignKeyConstraint) {
						((RelationalForeignKey)key).getColumns().add(col);
					} else if (indexConstraint) {
						((RelationalIndex)key).getColumns().add(col);
					}else {
						assert false : "Unexpected constraint type of '" + type + "'"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			} catch (EntityNotFoundException error) {
				addProgressMessage(error.getMessage());
			}
		}

		// special processing for foreign key
		if (foreignKeyConstraint) {
			RelationalForeignKey foreignKey = (RelationalForeignKey)key;

			AstNode tableRefNode = (AstNode)constraintNode.getProperty(TeiidDdlLexicon.Constraint.TABLE_REFERENCE);
			if(tableRefNode==null) {
				addProgressMessage(DdlImporterI18n.FK_TABLE_REF_NOT_FOUND_MSG+" '"+foreignKey.getName()+"'"); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}

			try {
				RelationalTable tableRef = find(RelationalTable.class, tableRefNode, null, allRefs);
				RelationalPrimaryKey tableRefPrimaryKey = tableRef.getPrimaryKey();
				Collection<RelationalColumn> primaryKeyColumns = tableRef.getColumns();
				// check to see if foreign table columns are referenced
				Object tempRefColumns = constraintNode.getProperty(TeiidDdlLexicon.Constraint.TABLE_REFERENCE_REFERENCES);

				List<AstNode> foreignTableColumnNodes = (tempRefColumns==null) ? Collections.<AstNode>emptyList() : (List<AstNode>)tempRefColumns;
				int numPKColumns = primaryKeyColumns.size();
				int numFKColumns = foreignTableColumnNodes.size();

				//TODO: Can we just set PK object on the FK, instead of the Names???   
				if( foreignTableColumnNodes.isEmpty() ) {
					if(tableRefPrimaryKey!=null) {
						foreignKey.setUniqueKeyName(tableRefPrimaryKey.getName());
						foreignKey.setUniqueKeyTableName(tableRef.getName());
					}
				} else if( numPKColumns == numFKColumns ) {
					//TODO: not sure what this is doing
					for(AstNode fTableColumn : foreignTableColumnNodes) {
						find(RelationalColumn.class, fTableColumn, tableRef, allRefs);
					}
					foreignKey.setUniqueKeyName(tableRefPrimaryKey.getName());
					foreignKey.setUniqueKeyTableName(tableRef.getName());
				} else {
					foreignKey.setUniqueKeyName(tableRefPrimaryKey.getName());
					foreignKey.setUniqueKeyTableName(tableRef.getName());
				}
			} catch (EntityNotFoundException error) {
				addProgressMessage(error.getMessage());
			}
		}
	}

	@Override
	protected RelationalColumn createColumn(AstNode node, RelationalTable table) throws Exception {
		RelationalColumn column = super.createColumn(node, table);

		// Handle Teiid-specific properties and options
		Object prop = node.getProperty(TeiidDdlLexicon.CreateTable.AUTO_INCREMENT);
		if(prop != null)
			column.setAutoIncremented(((Boolean)prop).booleanValue());

		// Find all the Option properties
		List<AstNode> optionNodes = new ArrayList<AstNode>();
		List<AstNode> children = node.getChildren();
		for(AstNode child: children) {
			if(is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
				optionNodes.add(child);
			}
		}

		// process the Column Options
		processOptions(optionNodes,column);

		return column;
	}

	@Override
	protected RelationalProcedure createProcedure(AstNode procedureNode, RelationalModel model) throws Exception {
		RelationalProcedure procedure = super.createProcedure(procedureNode, model);

		List<AstNode> procOptionNodes = new ArrayList<AstNode>();

		for (AstNode child : procedureNode) {
			if (is(child, TeiidDdlLexicon.CreateProcedure.PARAMETER)) {
				createProcedureParameter(child, procedure);
			} else if(is(child, TeiidDdlLexicon.CreateProcedure.RESULT_COLUMNS)) {
				RelationalProcedureResultSet result = getFactory().createProcedureResultSet();
				procedure.setResultSet(result);
				initialize(result, procedureNode);

				for(AstNode resultCol: child) {
					if(resultCol.hasMixin(TeiidDdlLexicon.CreateProcedure.RESULT_COLUMN)) {
						createColumn(resultCol,result);
					}
				}
			} else if(is(child, TeiidDdlLexicon.CreateProcedure.RESULT_DATA_TYPE)) {
				RelationalProcedureResultSet result = getFactory().createProcedureResultSet();
				procedure.setResultSet(result);
				initialize(result, procedureNode);
				createColumn(child,result);
			} else if(is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
				procOptionNodes.add(child);
			}
		}

		// process the Procedure Options
		processOptions(procOptionNodes,procedure);

		return procedure;
	}

	@Override
	protected RelationalParameter createProcedureParameter(AstNode node, RelationalProcedure procedure) throws Exception {
		RelationalParameter prm = super.createProcedureParameter(node, procedure);

		// Handle Teiid-specific properties and options
		Object prop = node.getProperty(TeiidDdlLexicon.CreateProcedure.PARAMETER_TYPE);
		if(prop != null) {
			String direction = prop.toString();
			prm.setDirection(direction);
		}

		// Find all the Option properties
		List<AstNode> optionNodes = new ArrayList<AstNode>();
		List<AstNode> children = node.getChildren();
		for(AstNode child: children) {
			if(is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
				optionNodes.add(child);
			}
		}

		processOptions(optionNodes,prm);

		return prm;
	}

	/**
	 * Create RelationalReference objects
	 * @param node the provided AstNode
	 * @param model the RelationalModel being created
	 * @param schema the schema
	 * @return the map of AstNodes which need to be deferred
	 * @throws Exception 
	 */
	@Override
	protected Map<AstNode,RelationalReference> createObject(AstNode node, RelationalModel model, RelationalSchema schema) throws Exception {
		Map<AstNode,RelationalReference> deferredMap = new HashMap<AstNode,RelationalReference>();

		// -----------------------------------------------------------------------
		// Handle Creation of Teiid Entities
		// -----------------------------------------------------------------------
		if (is(node, TeiidDdlLexicon.CreateTable.TABLE_STATEMENT)
				|| is(node, TeiidDdlLexicon.CreateTable.VIEW_STATEMENT)) {

			RelationalTable baseTable = getFactory().createBaseTable();
			initializeTable(baseTable, node, model);

			List<AstNode> optionNodes = new ArrayList<AstNode>();

			for (AstNode child : node) {
				// Table Elements
				if (is(child, TeiidDdlLexicon.CreateTable.TABLE_ELEMENT)) {
					createColumn(child, baseTable);
					// Statement Options
				} else if (is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
					optionNodes.add(child);
					// Contraints
				} else if (is(child, TeiidDdlLexicon.Constraint.TABLE_ELEMENT)
						|| is(child, TeiidDdlLexicon.Constraint.FOREIGN_KEY_CONSTRAINT)
						|| is(child, TeiidDdlLexicon.Constraint.INDEX_CONSTRAINT)) {
					deferredMap.put(child, baseTable);
				}
			}
			// processes all options for this table
			if(!optionNodes.isEmpty()) {
				processOptions(optionNodes,baseTable);
			}

		} else if (is(node, TeiidDdlLexicon.CreateProcedure.PROCEDURE_STATEMENT)
				|| is(node, TeiidDdlLexicon.CreateProcedure.FUNCTION_STATEMENT)) {
			createProcedure(node, model);

			// Handle Alter Table
		} else if (is(node, TeiidDdlLexicon.AlterOptions.TABLE_STATEMENT)) {
			deferredMap.put(node, null);
		} else if (is(node, TeiidDdlLexicon.AlterOptions.VIEW_STATEMENT)
				|| is(node, TeiidDdlLexicon.AlterOptions.PROCEDURE_STATEMENT)) {
		} else {
			// -----------------------------------------------------------------------
			// All other Non-Teiid DDL 
			// -----------------------------------------------------------------------
			return super.createObject(node, model, schema);
		}
		return deferredMap;

	}

	/**
	 * Create deferred objects using the supplied map
	 * @param deferredNodes the map of deferred AstNodes
	 * @param model the RelationalModel being created
	 * @throws Exception 
	 */
	@Override
	protected void createDeferredObjects(Map<AstNode,RelationalReference> deferredNodes, RelationalModel model) throws Exception {
		Collection<RelationalReference> allRefs = model.getAllReferences();

		// Make first pass to create the PKs
		Set<AstNode> astNodes = deferredNodes.keySet();
		for(AstNode node:astNodes) {
			if (is(node, TeiidDdlLexicon.Constraint.TABLE_ELEMENT)) {
				RelationalTable table = (RelationalTable)deferredNodes.get(node);
				createConstraint(node, table, model, allRefs);
			} 
		}

		// Second pass create FKs, options, others
		for(AstNode node:astNodes) {
			if (is(node, TeiidDdlLexicon.Constraint.FOREIGN_KEY_CONSTRAINT)
					|| is(node, TeiidDdlLexicon.Constraint.INDEX_CONSTRAINT)) {
				RelationalTable table = (RelationalTable)deferredNodes.get(node);
				createConstraint(node, table, model, allRefs);
			} else if (is(node, TeiidDdlLexicon.AlterOptions.TABLE_STATEMENT)) {
				//FIXME: find different way
				RelationalTable table = find(RelationalTable.class, node, null, allRefs);
				List<AstNode> optionNodes = new ArrayList<AstNode>();
				if (table != null) {
					for (AstNode child : node) {
						if (is(child, TeiidDdlLexicon.AlterOptions.OPTIONS_LIST)) {
							List<AstNode> nodeList = child.getChildren();
							for (AstNode listItem : nodeList) {
								if (listItem.hasMixin(StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
									optionNodes.add(listItem);
								}
							}
						}
					}
				}
				// processes all options for this table
				if(!optionNodes.isEmpty()) {
					processOptions(optionNodes,table);
				}
				// Handle Alter View and Procedure
				// TODO: could potentially be combined with alter table block above
			}
		}
	}

	/**
	 * Process the Statement Option AstNodes for the supplied relational entity.
	 * @param optionNodes the list of AstNodes
	 * @param relationalReference the RelationalReference
	 */
	private void processOptions(List<AstNode> optionNodes, RelationalReference relationalReference) {
		// process the standard teiid options.  Recognized Options are removed from the list as they are processed.
		processTeiidStandardOptions(optionNodes,relationalReference);

		// Add the remaining Options as extension properties.
		processTeiidExtensionOptions(optionNodes,relationalReference);
	}

	/**
	 * Process the options that are specific to the provided entity type
	 * @param optionNodes the list of AstNode
	 * @param relationalReference the RelationalReference
	 */
	private void processTeiidStandardOptions(List<AstNode> optionNodes, RelationalReference relationalReference) {
		// process Options common to all Entities
		processTeiidCommonOptions(optionNodes,relationalReference);

		// process Options specific to entity type
		if(relationalReference instanceof RelationalTable) {
			processTeiidTableOptions(optionNodes,(RelationalTable)relationalReference);
		} else if(relationalReference instanceof RelationalColumn) {
			processTeiidColumnOptions(optionNodes,(RelationalColumn)relationalReference);
		} else if(relationalReference instanceof RelationalProcedure) {
			processTeiidProcedureOptions(optionNodes,(RelationalProcedure)relationalReference);
		} 
	}

	/**
	 * Handles statementOption common to all relational entities for Teiid DDL
	 * @param optionNodes the list of statementOption AstNodes
	 * @param entity the RelationalEntity
	 */
	private void processTeiidCommonOptions(List<AstNode> optionNodes, RelationalReference entity) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();
			String optionName = optionNode.getName();
			Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
			if(!CoreStringUtil.isEmpty(optionName)) {
				String optionValueStr = (String)optionValue;
				if(!CoreStringUtil.isEmpty(optionValueStr)) {
					if(optionName.equalsIgnoreCase(TeiidDDLConstants.ANNOTATION)) {
						entity.setDescription(optionValueStr);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.UUID)) {
						// entity.setUUID();
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NAMEINSOURCE)) {
						entity.setNameInSource(optionValueStr);
						nodeIter.remove();
					} 
				}
			}
		}
		return;
	}

	/**
	 * Handle the OPTION keys that may be set on Tables for Teiid DDL
	 * @param optionNodes 
	 * @param table 
	 */
	private void processTeiidTableOptions(List<AstNode> optionNodes, RelationalTable table) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();
			String optionName = optionNode.getName();
			Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
			if(!CoreStringUtil.isEmpty(optionName)) {
				String optionValueStr = (String)optionValue;
				if(!CoreStringUtil.isEmpty(optionValueStr)) {
					if(optionName.equalsIgnoreCase(TeiidDDLConstants.CARDINALITY)) {
						table.setCardinality(Integer.parseInt(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MATERIALIZED)) {
						table.setMaterialized(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MATERIALIZED_TABLE)) {
						//Table mattable = new Table();
						//mattable.setName(value);
						//table.setMaterializedTable(mattable);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.UPDATABLE)) {
						table.setSupportsUpdate(isTrue(optionValueStr));
						nodeIter.remove();
					}
				}
			}
		}
	}

	/**
	 * Handle the OPTION keys that may be set on Procedures for Teiid DDL
	 * @param optionNodes the list of optionNodes for a Procedure
	 * @param procedure the procedure
	 */
	private void processTeiidProcedureOptions(List<AstNode> optionNodes, RelationalProcedure procedure) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();
			String optionName = optionNode.getName();
			Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
			if(!CoreStringUtil.isEmpty(optionName)) {
				String optionValueStr = (String)optionValue;
				// If any function properties are present, the setFuntion boolean is also set
				if(!CoreStringUtil.isEmpty(optionValueStr)) {
					if(optionName.equalsIgnoreCase(TeiidDDLConstants.UPDATECOUNT)) {
						procedure.setUpdateCount(optionValueStr);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CATEGORY)) {
						procedure.setFunctionCategory(optionValueStr);
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.AGGREGATE_PROP)) {
						procedure.setAggregate(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.ALLOWS_DISTINCT_PROP)) {
						procedure.setAllowsDistinct(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.ALLOWS_ORDER_BY_PROP)) {
						procedure.setAllowsOrderBy(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.ANALYTIC_PROP)) {
						procedure.setAnalytic(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.DECOMPOSABLE_PROP)) {
						procedure.setDecomposable(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NON_PREPARED_PROP)) {
						procedure.setNonPrepared(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NULL_ON_NULL_PROP)) {
						procedure.setReturnsNullOnNull(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.USES_DISTINCT_ROWS_PROP)) {
						procedure.setUseDistinctRows(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.VARARGS_PROP)) {
						procedure.setVariableArguments(isTrue(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.DETERMINISM_PROP)) {
						procedure.setDeterministic(isDeterministic(optionValueStr));
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NATIVE_QUERY_PROP)) {
						procedure.setNativeQuery(optionValueStr);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.FUNCTION_CATEGORY_PROP)) {
						procedure.setFunctionCategory(optionValueStr);
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.JAVA_CLASS)) {
						procedure.setJavaClassName(optionValueStr);
						procedure.setFunction(true);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.JAVA_METHOD)) {
						procedure.setJavaMethodName(optionValueStr);
						procedure.setFunction(true);
						nodeIter.remove();
					}
				}
			}
		}
	}

	/**
	 * Handle the OPTION keys that may be set on Columns for Teiid DDL
	 * @param optionNodes 
	 * @param column 
	 */
	private void processTeiidColumnOptions(List<AstNode> optionNodes, RelationalColumn column) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();
			String optionName = optionNode.getName();
			Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
			if(!CoreStringUtil.isEmpty(optionName)) {
				String optionValueStr = (String)optionValue;
				if(!CoreStringUtil.isEmpty(optionValueStr)) {
					if(optionName.equalsIgnoreCase(TeiidDDLConstants.SELECTABLE)) {
						column.setSelectable(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.UPDATABLE)) {
						column.setUpdateable(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CURRENCY)) {
						column.setCurrency(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CASE_SENSITIVE)) {
						column.setCaseSensitive(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.SIGNED)) {
						column.setSigned(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.FIXED_LENGTH)) {
						column.setLengthFixed(isTrue(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.SEARCHABLE)) {
						column.setSearchability(optionValueStr);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MIN_VALUE)) {
						column.setMinimumValue(optionValueStr);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MAX_VALUE)) {
						column.setMaximumValue(optionValueStr);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NATIVE_TYPE)) {
						column.setNativeType(optionValueStr);
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NULL_VALUE_COUNT)) {
						column.setNullValueCount(Integer.parseInt(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.RADIX)) {
						column.setRadix(Integer.parseInt(optionValueStr));
						nodeIter.remove();
					} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CHAR_OCTET_LENGTH)) {
						column.setCharacterOctetLength(Integer.parseInt(optionValueStr));
						nodeIter.remove();
					}
				}
			}
		}
	}

	/**
	 * Get the deterministic boolean from the DETERMINISM option string
	 * @param determinismStr
	 * @return 'true' if deterministic, 'false' if not.
	 */
	private boolean isDeterministic(String determinismStr) {
		if(TeiidDDLConstants.DETERMINISM_OPT_NONDETERMINISTIC.equalsIgnoreCase(determinismStr)) {
			return false;
		} else if(TeiidDDLConstants.DETERMINISM_OPT_COMMAND_DETERMINISTIC.equalsIgnoreCase(determinismStr)) {
			return false;
		} else if(TeiidDDLConstants.DETERMINISM_OPT_SESSION_DETERMINISTIC.equalsIgnoreCase(determinismStr)) {
			return false;
		} else if(TeiidDDLConstants.DETERMINISM_OPT_USER_DETERMINISTIC.equalsIgnoreCase(determinismStr)) {
			return false;
		} else if(TeiidDDLConstants.DETERMINISM_OPT_VDB_DETERMINISTIC.equalsIgnoreCase(determinismStr)) {
			return false;
		} else if(TeiidDDLConstants.DETERMINISM_OPT_DETERMINISTIC.equalsIgnoreCase(determinismStr)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Save the Extension Option name-value info to the importerModel
	 * @param optionNodes the list of statement option AstNodes
	 * @param relationalEntity the relational entity
	 */
	private void processTeiidExtensionOptions(List<AstNode> optionNodes, RelationalReference relationalEntity) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();

			String optionName = optionNode.getName();
			Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
			if(!CoreStringUtil.isEmpty(optionName)) {
				String optionValueStr = (String)optionValue;
				if(!CoreStringUtil.isEmpty(optionValueStr)) {
					relationalEntity.addExtensionProperty(optionName, optionValueStr);
				}
			}
			nodeIter.remove();
		}
	}
	
}
