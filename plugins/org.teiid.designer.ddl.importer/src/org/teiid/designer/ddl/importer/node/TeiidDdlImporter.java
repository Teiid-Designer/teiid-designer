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
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlConstants;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ddl.DdlImporterModel.EntityDescriptions;
import org.teiid.designer.ddl.DdlImporterModel.EntityDescriptions.DescriptionOperation;
import org.teiid.designer.ddl.DdlImporterModel.EntityExtensionProperties;
import org.teiid.designer.ddl.importer.DdlImporterI18n;
import org.teiid.designer.ddl.importer.DdlImporterPlugin;
import org.teiid.designer.ddl.importer.TeiidDDLConstants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ColumnSet;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.ProcedureUpdateCount;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.UniqueConstraint;


/**
 * Teiid DDL node importer
 */
public class TeiidDdlImporter extends StandardImporter {

	private Map<String, Collection<ModelObjectExtensionAssistant>> classNameToMedAssistantsMap = new HashMap<String,Collection<ModelObjectExtensionAssistant>>();

	private class TeiidInfo extends Info {

        /**
         * @param node
         * @param roots
         *
         * @throws Exception
         */
        public TeiidInfo(AstNode node, List<EObject> roots) throws Exception {
            super(node, roots);
        }

        @Override
        protected void init(AstNode node, List<EObject> roots) throws Exception {
            String name = node.getName();
            int ndx = name.indexOf('.');
            if (ndx >= 0) {
                this.schema = null;
                this.name = name.substring(ndx + 1);
            } else
                super.init(node, roots);
        }
    }
    
    @Override
    protected TeiidInfo createInfo(AstNode node, List<EObject> roots) throws Exception {
        return new TeiidInfo(node, roots);
    }

    @Override
    protected EObject getDataType(String datatype) throws Exception {
        EObject resultType = null;

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
                resultType = builtInTypes[i];
                break;
            }
        }

        // Built In type not found, try mapping from native to built-in
        if(resultType == null) {
            resultType = super.getDataType(datatype);
        }

        return resultType;
    }

    /**
     * Creates constraints for Table for Teiid DDL
     * @param constraintNode the AstNode for the constraint
     * @param table the BaseTable object
     * @param roots the current model roots
     *
     * @throws CoreException
     */
    private void createConstraint(AstNode constraintNode, BaseTable table, List<EObject> roots) throws CoreException {

        String type = constraintNode.getProperty(TeiidDdlLexicon.Constraint.TYPE).toString();
        boolean primaryKeyConstraint = false;
        boolean uniqueConstraint = false;
        boolean accessPatternConstraint = false;
        boolean foreignKeyConstraint = false;
        boolean indexConstraint = false;
        RelationalEntity key = null;

        if (DdlConstants.PRIMARY_KEY.equals(type)) {
            key = getFactory().createPrimaryKey();
            initialize(key, constraintNode);
            table.setPrimaryKey((PrimaryKey)key);
            primaryKeyConstraint = true;
        } else if (DdlConstants.INDEX.equals(type)) {
            // TODO need to process teiidddl:expression property
            key = getFactory().createIndex();
            initialize(key, constraintNode);
            roots.add(key);
            indexConstraint = true;
        } else if (DdlConstants.UNIQUE.equals(type)) {
            key = getFactory().createUniqueConstraint();
            initialize(key, constraintNode);
            table.getUniqueConstraints().add(key);
            uniqueConstraint = true;
        } else if (TeiidDdlConstants.TeiidNonReservedWord.ACCESSPATTERN.toDdl().equals(type)) {
            key = getFactory().createAccessPattern();
            initialize(key, constraintNode);
            table.getAccessPatterns().add(key);
            accessPatternConstraint = true;
        } else if (DdlConstants.FOREIGN_KEY.equals(type)) {
            key = getFactory().createForeignKey();
            initializeFK(table.getForeignKeys(), (ForeignKey)key, constraintNode);
            table.getForeignKeys().add(key);
            foreignKeyConstraint = true;
        } else {
            assert false : "Unexpected constraint type of '" + type + "'"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        // process referenced columns multi-valued property
        Object temp = constraintNode.getProperty(TeiidDdlLexicon.Constraint.REFERENCES);
        List<AstNode> references = (List<AstNode>)temp;

        for (AstNode ref : references) {
            try {
                if (primaryKeyConstraint) {
                    ((PrimaryKey)key).getColumns().add(find(Column.class, ref, table, roots));
                } else if (uniqueConstraint) {
                    ((UniqueConstraint)key).getColumns().add(find(Column.class, ref, table, roots));
                } else if (accessPatternConstraint) {
                    ((AccessPattern)key).getColumns().add(find(Column.class, ref, table, roots));
                } else if (foreignKeyConstraint) {
                    ((ForeignKey)key).getColumns().add(find(Column.class, ref, table, roots));
                } else if (indexConstraint) {
                    ((Index)key).getColumns().add(find(Column.class, ref, table, roots));
                }else {
                    assert false : "Unexpected constraint type of '" + type + "'"; //$NON-NLS-1$ //$NON-NLS-2$
                }
            } catch (EntityNotFoundException error) {
                addProgressMessage(error.getMessage());
            }
        }

        // special processing for foreign key
        if (foreignKeyConstraint) {
            ForeignKey foreignKey = (ForeignKey)key;

            // must have a table reference
            AstNode tableRefNode = (AstNode)constraintNode.getProperty(TeiidDdlLexicon.Constraint.TABLE_REFERENCE);
            if(tableRefNode==null) {
                addProgressMessage(DdlImporterI18n.FK_TABLE_REF_NOT_FOUND_MSG+" '"+foreignKey.getName()+"'"); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            try {
                BaseTable tableRef = find(BaseTable.class, tableRefNode, null, roots);
                PrimaryKey tableRefPrimaryKey = tableRef.getPrimaryKey();
                List<Column> primaryKeyColumns = tableRef.getColumns();
                // check to see if foreign table columns are referenced
                Object tempRefColumns = constraintNode.getProperty(TeiidDdlLexicon.Constraint.TABLE_REFERENCE_REFERENCES);

                List<AstNode> foreignTableColumnNodes = (tempRefColumns==null) ? Collections.<AstNode>emptyList() : (List<AstNode>)tempRefColumns;
                int numPKColumns = primaryKeyColumns.size();
                int numFKColumns = foreignTableColumnNodes.size();
                
                if( foreignTableColumnNodes.isEmpty() ) {
                    foreignKey.setUniqueKey(tableRefPrimaryKey);
                } else if( numPKColumns == numFKColumns ) {
                    for(AstNode fTableColumn : foreignTableColumnNodes) {
                        find(Column.class, fTableColumn, tableRef, roots);
                    }
                    foreignKey.setUniqueKey(tableRefPrimaryKey);
                } else {
                    foreignKey.setUniqueKey(tableRefPrimaryKey);
                }
            } catch (EntityNotFoundException error) {
                addProgressMessage(error.getMessage());
            }
        }
    }

    /**
     * Get ProcedureUpdateCount object for the provided string value
     *
     * @param value the string value
     * @return the ProcedureUpdateCount object corresponding to the provided string
     */
    private  ProcedureUpdateCount getUpdateCount(String value) {
        if( ProcedureUpdateCount.AUTO_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.AUTO_LITERAL;
        } else if( ProcedureUpdateCount.ONE_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.ONE_LITERAL;
        } else if( ProcedureUpdateCount.MULTIPLE_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.MULTIPLE_LITERAL;
        } else if( ProcedureUpdateCount.ZERO_LITERAL.getName().equalsIgnoreCase(value) ) {
            return ProcedureUpdateCount.ZERO_LITERAL;
        }

        return ProcedureUpdateCount.AUTO_LITERAL;
    }

    @Override
    protected Column createColumn(AstNode node, ColumnSet table) throws Exception {
        Column column = super.createColumn(node, table);

        // Handle Teiid-specific properties and options
        Object prop = node.getProperty(TeiidDdlLexicon.CreateTable.AUTO_INCREMENT);
        if(prop != null)
            column.setAutoIncremented(isTrue(prop.toString()));

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
    protected BaseTable createBaseTable(AstNode tableNode, List<EObject> roots) throws Exception {
    	BaseTable table = super.createBaseTable(tableNode, roots);
    	
		List<AstNode> optionNodes = new ArrayList<AstNode>();
		
        for (AstNode child : tableNode) {
            // Table Elements
            if (is(child, TeiidDdlLexicon.CreateTable.TABLE_ELEMENT)) {
                createColumn(child, table);
            // Contraints
            } else if (is(child, TeiidDdlLexicon.Constraint.TABLE_ELEMENT)
                       || is(child, TeiidDdlLexicon.Constraint.FOREIGN_KEY_CONSTRAINT)
                       || is(child, TeiidDdlLexicon.Constraint.INDEX_CONSTRAINT)) {

                createConstraint(child, table, roots);
            // Statement Options
            } else if (is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
            	optionNodes.add(child);
            }
        }
        // processes all options for this table
        if(!optionNodes.isEmpty()) {
        	processOptions(optionNodes,table);
        }
        
        return table;
    }

    @Override
    protected Procedure createProcedure(AstNode procedureNode, List<EObject> roots) throws Exception {
        Procedure procedure = super.createProcedure(procedureNode, roots);

		List<AstNode> procOptionNodes = new ArrayList<AstNode>();
		
        for (AstNode child : procedureNode) {
            if (is(child, TeiidDdlLexicon.CreateProcedure.PARAMETER)) {
                createProcedureParameter(child, procedure);

                // TODO: Determine how to handle teiidddl:result, ddl:defaultOption, ddl:statementOption
            } else if(is(child, TeiidDdlLexicon.CreateProcedure.RESULT_COLUMNS)) {
                // TODO: determine how to handle Table flag property
                ProcedureResult result = getFactory().createProcedureResult();
                procedure.setResult(result);
                initialize(result, procedureNode);
                
                for(AstNode resultCol: child) {
                    if(resultCol.hasMixin(TeiidDdlLexicon.CreateProcedure.RESULT_COLUMN)) {
                        createColumn(resultCol,result);
                    }
                }
            } else if(is(child, TeiidDdlLexicon.CreateProcedure.RESULT_DATA_TYPE)) {
                ProcedureResult result = getFactory().createProcedureResult();
                procedure.setResult(result);
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
    protected ProcedureParameter createProcedureParameter(AstNode node, Procedure procedure) throws Exception {
        ProcedureParameter prm = super.createProcedureParameter(node, procedure);

        // Handle Teiid-specific properties and options
        Object prop = node.getProperty(TeiidDdlLexicon.CreateProcedure.PARAMETER_TYPE);
        // TODO - Determine how to handle 'VARIADIC'
        if(prop != null) {
            String direction = prop.toString();
            if (DirectionKind.IN_LITERAL.getName().equals(direction))
                prm.setDirection(DirectionKind.IN_LITERAL);
            else if (DirectionKind.OUT_LITERAL.getName().equals(direction) )
                prm.setDirection(DirectionKind.OUT_LITERAL);
            else if (DirectionKind.INOUT_LITERAL.getName().equals(direction)) 
            prm.setDirection(DirectionKind.INOUT_LITERAL);
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

    @Override
    protected void create(AstNode node, List<EObject> roots, Schema schema) throws Exception {

        // -----------------------------------------------------------------------
        // Handle Creation of Teiid Entities
        // -----------------------------------------------------------------------
        if (is(node, TeiidDdlLexicon.CreateTable.TABLE_STATEMENT)
            || is(node, TeiidDdlLexicon.CreateTable.VIEW_STATEMENT)) {

            createBaseTable(node, roots);
            
        } else if (is(node, TeiidDdlLexicon.CreateProcedure.PROCEDURE_STATEMENT)
                   || is(node, TeiidDdlLexicon.CreateProcedure.FUNCTION_STATEMENT)) {
            createProcedure(node, roots);

        // Handle Alter Table
        } else if (is(node, TeiidDdlLexicon.AlterOptions.TABLE_STATEMENT)) {
            BaseTable table = find(BaseTable.class, node, null, roots);
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
        } else if (is(node, TeiidDdlLexicon.AlterOptions.VIEW_STATEMENT)
                   || is(node, TeiidDdlLexicon.AlterOptions.PROCEDURE_STATEMENT)) {
        } else {
            // -----------------------------------------------------------------------
            // All other Non-Teiid DDL 
            // -----------------------------------------------------------------------
            super.create(node, roots, schema);
        }
    }

	/**
	 * Process the Statement Option AstNodes for the supplied relational entity.
	 * @param optionNodes the list of AstNodes
	 * @param relationalEntity the RelationalEntity
	 */
	private void processOptions(List<AstNode> optionNodes, RelationalEntity relationalEntity) {
		// process the standard teiid options
		processTeiidStandardOptions(optionNodes,relationalEntity);
		
		// save Extension Options - they are created in 'importFinalize' after model is created.
		saveTeiidExtensionOptions(optionNodes,relationalEntity);
	}

	/**
	 * Process the options that are specific to the provided entity type
	 * @param optionNodes the list of AstNode
	 * @param entity the relational entity
	 */
	private void processTeiidStandardOptions(List<AstNode> optionNodes, RelationalEntity entity) {
		// process Options common to all Entities
		processTeiidCommonOptions(optionNodes,entity);
		
		// process Options specific to entity type
		if(entity instanceof BaseTable) {
			processTeiidTableOptions(optionNodes,(BaseTable)entity);
		} else if(entity instanceof Column) {
			processTeiidColumnOptions(optionNodes,(Column)entity);
		} else if(entity instanceof Procedure) {
			processTeiidProcedureOptions(optionNodes,(Procedure)entity);
		} else if(entity instanceof ProcedureParameter) {
			processTeiidProcedureParameterOptions(optionNodes,(ProcedureParameter)entity);
		}
		//ETC
	}
	
	/**
	 * Handles statementOption common to all relational entities for Teiid DDL
	 * @param optionNodes the list of statementOption AstNodes
	 * @param entity the RelationalEntity
	 */
	private void processTeiidCommonOptions(List<AstNode> optionNodes, RelationalEntity entity) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();
	    	String optionName = optionNode.getName();
	        Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
	        if(!CoreStringUtil.isEmpty(optionName)) {
	        	String optionValueStr = (String)optionValue;
	        	if(!CoreStringUtil.isEmpty(optionValueStr)) {
	            	if(optionName.equalsIgnoreCase(TeiidDDLConstants.ANNOTATION)) {
	                    getImporterModel().addDescription(entity, optionValueStr, DescriptionOperation.PREPEND);
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
	private void processTeiidTableOptions(List<AstNode> optionNodes, BaseTable table) {
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
	private void processTeiidProcedureOptions(List<AstNode> optionNodes, Procedure procedure) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();
	    	String optionName = optionNode.getName();
	        Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
	        if(!CoreStringUtil.isEmpty(optionName)) {
	        	String optionValueStr = (String)optionValue;
	        	if(!CoreStringUtil.isEmpty(optionValueStr)) {
	        		if(optionName.equalsIgnoreCase(TeiidDDLConstants.UPDATECOUNT)) {
	        			procedure.setUpdateCount(getUpdateCount(optionValueStr));
	        		} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CATEGORY)) {
	        			procedure.setFunction(true);
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
	private void processTeiidColumnOptions(List<AstNode> optionNodes, Column column) {
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
	        			column.setFixedLength(isTrue(optionValueStr));
	            		nodeIter.remove();
	        		} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.SEARCHABLE)) {
	        			column.setSearchability(SearchabilityType.get(optionValueStr.toUpperCase()));
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
	        		} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.DISTINCT_VALUES)) {
	        			//column.setDistinctValueCount(value);
	            		nodeIter.remove();
	        		}
	        	}
	        }
		}
	}
	
	 /**
     * Handle the OPTION keys that may be set on ProcedureParameters for Teiid DDL
     * @param optionNodes the list of statementOptions for a procedure parameter
     * @param procParam the ProcedureParameter
     */
	private void processTeiidProcedureParameterOptions(List<AstNode> optionNodes, ProcedureParameter procParam) {
//		Iterator<AstNode> nodeIter = optionNodes.iterator();
//		while(nodeIter.hasNext()) {
//			AstNode optionNode = nodeIter.next();
//	    	String optionName = optionNode.getName();
//	        Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
//	        if(!CoreStringUtil.isEmpty(optionName)) {
//	        	String optionValueStr = (String)optionValue;
//	        	if(!CoreStringUtil.isEmpty(optionValueStr)) {
//
//	        	}
//	        }
//		}
	}

	/**
	 * Save the Extension Option name-value info to the importerModel
	 * @param optionNodes the list of statement option AstNodes
	 * @param relationalEntity the relational entity
	 */
	private void saveTeiidExtensionOptions(List<AstNode> optionNodes, RelationalEntity relationalEntity) {
		Iterator<AstNode> nodeIter = optionNodes.iterator();
		while(nodeIter.hasNext()) {
			AstNode optionNode = nodeIter.next();
			
			String optionName = optionNode.getName();
		    Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
		    if(!CoreStringUtil.isEmpty(optionName)) {
		    	String optionValueStr = (String)optionValue;
		    	if(!CoreStringUtil.isEmpty(optionValueStr)) {
		    		getImporterModel().addExtensionProperty(relationalEntity, optionName, optionValueStr);
		    	}
		    }
		    nodeIter.remove();
		}
	}
	
	/**
	 * Process the extension properties for a relational entity.  This will apply the necessary med to the model (if needed) and add the 
	 * appropriate extension properties to the model.
	 * @param modelResource the ModelResource
	 * @param relationalEntity the RelationalEntity
	 * @param extensionProperties the extension property info
	 * 
	 */
	private void processExtensionProperties(ModelResource modelResource, RelationalEntity relationalEntity, Properties extensionProperties) {
		
		Iterator<Object> keyIter = extensionProperties.keySet().iterator();
		while(keyIter.hasNext()) {
			String propName = (String)keyIter.next();
			String propValue = extensionProperties.getProperty(propName);
			
			// Find an extension assistant that can create this extension property (if it exists)
	    	ModelObjectExtensionAssistant assistant = getModelExtensionAssistant(relationalEntity.getClass().getName(),propName);
	    	if(assistant!=null) {
	    		// Ensure that the Model supports the MED
	    		try {
	    			applyMedIfNecessary(modelResource,assistant);
	    		} catch (Exception e) {
	    			DdlImporterPlugin.UTIL.log(IStatus.ERROR,e,DdlImporterI18n.ERROR_APPLYING_MED_TO_MODEL);
	    		}
	    		String namespacedId = null;
	    		try {
	    			namespacedId = assistant.getNamespacePrefix()+':'+propName;
					assistant.setPropertyValue(relationalEntity, namespacedId, propValue);
				} catch (Exception ex) {
	    			DdlImporterPlugin.UTIL.log(IStatus.ERROR,ex,DdlImporterI18n.ERROR_SETTING_PROPERTY_VALUE+namespacedId);
				}
	    	}
		}
	}
	
	/**
	 * If the ModelResource does not support the assistants namespace, apply its MED to the model
	 * @param modelResource the model resource
	 * @param assistant the ModelObjectExtensionAssistant
	 * @throws Exception exception if there's a problem applying the MED
	 */
	private void applyMedIfNecessary(final ModelResource modelResource, ModelObjectExtensionAssistant assistant) throws Exception {
		if (modelResource != null && !modelResource.isReadOnly()) {
			if(!assistant.supportsMyNamespace(modelResource)) {
				assistant.saveModelExtensionDefinition(modelResource);
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
    		DdlImporterPlugin.UTIL.log(IStatus.WARNING,DdlImporterI18n.WARNING_ASSISTANT_FOR_METACLASS_NOT_FOUND+eObjectClassName);
            return null;
        }

        // find the assistant for the property
        for (ModelExtensionAssistant assistant : assistants) {
        	// Prepend the assistant namespace to the propertyId, since it doesnt have one
        	String namespacedId = assistant.getNamespacePrefix()+':'+propId;

        	if(hasMatchingPropertyName(assistant.getModelExtensionDefinition(), eObjectClassName, namespacedId)) {
                return ((assistant instanceof ModelObjectExtensionAssistant) ? (ModelObjectExtensionAssistant)assistant : null);
            }
        }
    
		DdlImporterPlugin.UTIL.log(IStatus.WARNING,DdlImporterI18n.WARNING_ASSISTANT_FOR_PROPERTY_NOT_FOUND+propId);
        return null;
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
        
	@Override
    public void importFinalize() throws Exception {
        /*
         * The descriptions are added from to the model's descriptions
         * from the teiid annotations.
         */
        for (EntityDescriptions pair : getImporterModel().getEntityDescriptions()) {
            ModelerCore.getModelEditor().setDescription(pair.getEntity(), pair.getPreferredDescription());
        }
        
		// Clear Map of extended objectClass to available MED Assistants
		this.classNameToMedAssistantsMap.clear();
		
        /*
         * Create the Extension Properties using the extension properties saved previously on the importer model
         */
		ModelResource modelResource = getImporterModel().getModel();
        for (EntityExtensionProperties pair : getImporterModel().getEntityExtensionProperties()) {
        	processExtensionProperties(modelResource,pair.getEntity(),pair.getProperties());
        }
        
    }
}
