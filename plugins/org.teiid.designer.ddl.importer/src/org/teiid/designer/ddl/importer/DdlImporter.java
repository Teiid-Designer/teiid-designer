/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParsers;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.derby.DerbyDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.mysql.MySqlDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlConstants;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.I18n;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.exception.EmptyArgumentException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.designer.compare.DifferenceProcessor;
import org.teiid.designer.compare.DifferenceReport;
import org.teiid.designer.compare.MergeProcessor;
import org.teiid.designer.compare.ModelerComparePlugin;
import org.teiid.designer.compare.processor.DifferenceProcessorImpl;
import org.teiid.designer.compare.selector.ModelResourceSelector;
import org.teiid.designer.compare.selector.ModelSelector;
import org.teiid.designer.compare.selector.TransientModelSelector;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ColumnSet;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.ProcedureUpdateCount;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMappingImpl;

/**
 * DdlImporter parses the provided DDL and generates a model containing the parsed entities
 *
 * @since 8.0
 */
public class DdlImporter {

    private static final RelationalFactory FACTORY = RelationalFactory.eINSTANCE;
    private static final String STRING_TYPENAME = "string"; //$NON-NLS-1$

    private final IProject[] projects;

    private IContainer modelFolder;
    private String ddlFileName;
    private String modelName;
    private IFile modelFile;
    private ModelType modelType;
    private DifferenceProcessor chgProcessor;
    private ModelResource model;
    private boolean optToCreateModelEntitiesForUnsupportedDdl;
    private boolean optToSetModelEntityDescription;
    boolean isTeiidDdl = false;
    
    // hold on to DDL so that it can be set in the description
    private Map<RelationalEntity, String> descriptionMap = new HashMap<RelationalEntity, String>();
    private Map<RelationalEntity, String> teiidAnnotationMap = new HashMap<RelationalEntity, String>();

    /**
     * DdlImporter contructor
     * @param projects the list of open workspace projects
     */
    public DdlImporter( final IProject[] projects ) {
        this.projects = projects;
    }

    /*
     * Create a Model Entity, using the provided AstNode
     * @param node the provided AstNode
     * @param roots the current model roots
     * @param schema the schema
     * @param messages the list of messages generated during generation
     */
    private void create( final AstNode node,
                         final List<EObject> roots,
                         final Schema schema,
                         final List<String> messages ) throws CoreException {
    	isTeiidDdl = false;
    	try {
        	// -----------------------------------------------------------------------
        	// Handle Creation of Teiid Entities
        	// -----------------------------------------------------------------------
        	if (node.hasMixin(TeiidDdlLexicon.CreateTable.TABLE_STATEMENT)
        		|| node.hasMixin(TeiidDdlLexicon.CreateProcedure.PROCEDURE_STATEMENT)
        		|| node.hasMixin(TeiidDdlLexicon.CreateProcedure.FUNCTION_STATEMENT)
        		|| node.hasMixin(TeiidDdlLexicon.AlterOptions.TABLE_STATEMENT)
        		|| node.hasMixin(TeiidDdlLexicon.AlterOptions.VIEW_STATEMENT)
        		|| node.hasMixin(TeiidDdlLexicon.AlterOptions.PROCEDURE_STATEMENT)
        		|| node.hasMixin(TeiidDdlLexicon.CreateTable.VIEW_STATEMENT)) {
        		
        		isTeiidDdl = true;
        		
        		createTeiidEntity(node,roots,messages);
        		
    		// -----------------------------------------------------------------------
    		// All other Non-Teiid DDL 
    		// -----------------------------------------------------------------------
        	} else if (node.hasMixin(StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT)) {
        		isTeiidDdl = false;
        		
        		final BaseTable table = initializeTable(FACTORY.createBaseTable(), node, roots);
        		for (final AstNode child : node) {
        			if (child.hasMixin(StandardDdlLexicon.TYPE_COLUMN_DEFINITION)) createColumn(child, table);
        			else if (child.hasMixin(StandardDdlLexicon.TYPE_TABLE_CONSTRAINT)) createKey(child, table, roots, messages);
        		}
        	} else if (node.hasMixin(StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT)) {
        		isTeiidDdl = false;
        		if (modelType != ModelType.VIRTUAL_LITERAL && optToCreateModelEntitiesForUnsupportedDdl) initializeTable(FACTORY.createView(),
        				node,
        				roots);
            } else if (node.hasMixin(OracleDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)
                       || node.hasMixin(DerbyDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)
                       || node.hasMixin(MySqlDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)
                       || node.hasMixin(PostgresDdlLexicon.TYPE_CREATE_INDEX_STATEMENT)) {
        		isTeiidDdl = false;
                final Index index = FACTORY.createIndex();
                final Info<Index> info = new Info<Index>(index, node, roots);
                if (info.schema == null) roots.add(index);
                else info.schema.getIndexes().add(index);
                initialize(index, node, info.name);
                Object prop = node.getProperty(OracleDdlLexicon.UNIQUE_INDEX);
                if (prop == null) prop = node.getProperty(DerbyDdlLexicon.UNIQUE_INDEX);
                if (prop != null) index.setUnique((Boolean)prop);
                prop = node.getProperty(OracleDdlLexicon.TABLE_NAME);
                if (prop == null) prop = node.getProperty(DerbyDdlLexicon.TABLE_NAME);
                if (prop != null) {
                    try {
                        final Table table = find(Table.class, prop.toString(), node, null, roots);
                        for (final AstNode node1 : node) {
                            // Probably need to check for a simple column reference for Oracle
                            if (node1.hasMixin(DerbyDdlLexicon.TYPE_INDEX_COLUMN_REFERENCE)) try {
                                index.getColumns().add(find(Column.class, node1, table, roots));
                            } catch (final EntityNotFoundException error) {
                                messages.add(error.getMessage());
                            }
                        }
                    } catch (final EntityNotFoundException error) {
                        messages.add(error.getMessage());
                    }
                }
            } else if (node.hasMixin(OracleDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT)
                       || node.hasMixin(DerbyDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT)
                       || node.hasMixin(MySqlDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT)) try {
           		isTeiidDdl = false;
                createProcedure(node, roots);
            } catch (final EntityNotFoundException error) {
                messages.add(error.getMessage());
            }
            else if (node.hasMixin(OracleDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)
                     || node.hasMixin(DerbyDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)
                     || node.hasMixin(MySqlDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)
                     || node.hasMixin(PostgresDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT)) try {
                isTeiidDdl = false;
                createProcedure(node, roots).setFunction(true);
            } catch (final EntityNotFoundException error) {
                messages.add(error.getMessage());
            }
            else if (node.hasMixin(StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT)) {
        		isTeiidDdl = false;
                final BaseTable table = find(BaseTable.class, node, schema, roots);
                for (final AstNode node1 : node) {
                    if (node1.hasMixin(StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION)) createKey(node1,
                                                                                                             table,
                                                                                                             roots,
                                                                                                             messages);
                    else if (node1.hasMixin(StandardDdlLexicon.TYPE_ADD_COLUMN_DEFINITION)) createColumn(node1, table);
                }
            }
        } catch (final EntityNotFoundException error) {
            messages.add(error.getMessage());
        }
    }

    /**
     * Creates Entity from Teiid DDL nodes
	 * @param node the provided AstNode
	 * @param roots the current model roots
	 * @param messages the generated messages
	 * @throws CoreException
     * @throws EntityNotFoundException 
	 */
	private void createTeiidEntity(AstNode node, List<EObject> roots, List<String> messages) throws CoreException,EntityNotFoundException {
		// TODO need to handle SchemaElement (foreign or virtual) - DO we need to set model type to physical or virtual
		
    	if (node.hasMixin(TeiidDdlLexicon.CreateTable.TABLE_STATEMENT)
        		|| node.hasMixin(TeiidDdlLexicon.CreateTable.VIEW_STATEMENT) ) {
    		
            final BaseTable table = initializeTable(FACTORY.createBaseTable(), node, roots);
            
            for (final AstNode child : node) {
            	// Table Elements
                if (child.hasMixin(TeiidDdlLexicon.CreateTable.TABLE_ELEMENT)) {
                	createColumn(child, table);
                // Contraints
                } else if(child.hasMixin(TeiidDdlLexicon.Constraint.TABLE_ELEMENT)
                		               || child.hasMixin(TeiidDdlLexicon.Constraint.FOREIGN_KEY_CONSTRAINT)
                	                   || child.hasMixin(TeiidDdlLexicon.Constraint.INDEX_CONSTRAINT)) {
                	                    	 
                	createTeiidConstraint(child,table,roots,messages); 
                // Statement Options
                } else if(child.hasMixin(StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
                	handleTeiidTableOption(table,child);
                }
            }
    	} else if (node.hasMixin(TeiidDdlLexicon.CreateProcedure.PROCEDURE_STATEMENT)
    			  || node.hasMixin(TeiidDdlLexicon.CreateProcedure.FUNCTION_STATEMENT)) {
    		createProcedure(node,roots);
    		
        // Handle Alter Table
    	} else if (  node.hasMixin(TeiidDdlLexicon.AlterOptions.TABLE_STATEMENT) ) {
    		final BaseTable table = find(BaseTable.class, node, null, roots);
    		if(table!=null) {
    			for (final AstNode child : node) {
    				if (child.hasMixin(TeiidDdlLexicon.AlterOptions.OPTIONS_LIST)) {
    					List<AstNode> nodeList = child.getChildren();
    					for(AstNode listItem: nodeList) {
    						if(listItem.hasMixin(StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
    							handleTeiidTableOption(table,listItem);
    						}
    					}
    				}
    			}
    		}
        // Handle Alter View and Procedure
        // TODO: could potentially be combined with alter table block above
    	} else if (  node.hasMixin(TeiidDdlLexicon.AlterOptions.VIEW_STATEMENT)
  			  || node.hasMixin(TeiidDdlLexicon.AlterOptions.PROCEDURE_STATEMENT) ) {
    	} else {
    		throw new IllegalStateException();
    	}		
	}
	
	/*
	 * Handle the OPTION keys that may be set on Tables for Teiid DDL
	 */
	private void handleTeiidTableOption(BaseTable table, AstNode node) {
		boolean wasCommonOption = handleTeiidCommonOption(table,node);
		if(wasCommonOption) return;
		
        // TODO: handle 'generic' statement options
    	String optionName = node.getName();
        Object optionValue = node.getProperty(StandardDdlLexicon.VALUE);
        if(!CoreStringUtil.isEmpty(optionName)) {
        	String optionValueStr = (String)optionValue;
        	if(!CoreStringUtil.isEmpty(optionValueStr)) {
            	if(optionName.equalsIgnoreCase(TeiidDDLConstants.CARDINALITY)) {
            		table.setCardinality(Integer.parseInt(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MATERIALIZED)) {
        			table.setMaterialized(isTrue(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MATERIALIZED_TABLE)) {
            		//Table mattable = new Table();
            		//mattable.setName(value);
            		//table.setMaterializedTable(mattable);
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.UPDATABLE)) {
        			table.setSupportsUpdate(isTrue(optionValueStr));
            	}
        	}
        }
	}
	
	/*
	 * Handle the OPTION keys that may be set on ProcedureParameters for Teiid DDL
	 * @param procParam the ProcedureParameter
	 * @param paramNode the procedure parameter AstNode
	 */
	private void handleTeiidProcParamOptions(ProcedureParameter procParam, AstNode paramNode) {
		List<AstNode> children = paramNode.getChildren();
		for(AstNode child: children) {
			if(child.hasMixin(StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
				handleTeiidProcParamOption(procParam,child);
			}
		}
	}
	
	/*
	 * Handle the OPTION keys that may be set on a ProcedureParameter for Teiid DDL
	 * @param procParam the ProcedureParameter
	 * @param procParamOptionNode a statementOption node for the procedure parameter
	 */
	private void handleTeiidProcParamOption(ProcedureParameter procParam, AstNode procParamOptionNode) {
		boolean wasCommonOption = handleTeiidCommonOption(procParam,procParamOptionNode);
		if(wasCommonOption) return;
	}
	
	/*
	 * Handle the OPTION keys that may be set on Column for Teiid DDL
	 * @param column the Column
	 * @param columnNode the column AstNode
	 */
	private void handleTeiidColumnOptions(Column column, AstNode columnNode) {
		List<AstNode> children = columnNode.getChildren();
		for(AstNode child: children) {
			if(child.hasMixin(StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
				handleTeiidColumnOption(column,child);
			}
		}
	}
	
	/*
	 * Handle a statement OPTION key for Column for Teiid DDL
	 * @param column the Column
	 * @param columnOptionNode a statementOption node for a column
	 */
	private void handleTeiidColumnOption(Column column, AstNode columnOptionNode) {
		boolean wasCommonOption = handleTeiidCommonOption(column,columnOptionNode);
		if(wasCommonOption) return;
		
    	String optionName = columnOptionNode.getName();
        Object optionValue = columnOptionNode.getProperty(StandardDdlLexicon.VALUE);
        if(!CoreStringUtil.isEmpty(optionName)) {
        	String optionValueStr = (String)optionValue;
        	if(!CoreStringUtil.isEmpty(optionValueStr)) {
            	if(optionName.equalsIgnoreCase(TeiidDDLConstants.SELECTABLE)) {
            		column.setSelectable(isTrue(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.UPDATABLE)) {
            		column.setUpdateable(isTrue(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CURRENCY)) {
            		column.setCurrency(isTrue(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CASE_SENSITIVE)) {
            		column.setCaseSensitive(isTrue(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.SIGNED)) {
            		column.setSigned(isTrue(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.FIXED_LENGTH)) {
            		column.setFixedLength(isTrue(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.SEARCHABLE)) {
            		column.setSearchability(SearchabilityType.get(optionValueStr.toUpperCase()));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MIN_VALUE)) {
            		column.setMinimumValue(optionValueStr);
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.MAX_VALUE)) {
            		column.setMaximumValue(optionValueStr);
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NATIVE_TYPE)) {
            		column.setNativeType(optionValueStr);
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NULL_VALUE_COUNT)) {
            		column.setNullValueCount(Integer.parseInt(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.DISTINCT_VALUES)) {
            		//column.setDistinctValueCount(value);
            	}
        	}
        }
	}

	/*
	 * Handle a statementOption for a Procedure for Teiid DDL
	 * @param proc the Procedure
	 * @param procOptionNode a statementOption for a procedure
	 */
	private void handleTeiidProcedureOption(Procedure proc, AstNode procOptionNode) {
		boolean wasCommonOption = handleTeiidCommonOption(proc,procOptionNode);
		if(wasCommonOption) return;
		
    	String optionName = procOptionNode.getName();
        Object optionValue = procOptionNode.getProperty(StandardDdlLexicon.VALUE);
        if(!CoreStringUtil.isEmpty(optionName)) {
        	String optionValueStr = (String)optionValue;
        	if(!CoreStringUtil.isEmpty(optionValueStr)) {
            	if(optionName.equalsIgnoreCase(TeiidDDLConstants.UPDATECOUNT)) {
            		proc.setUpdateCount(getUpdateCount(optionValueStr));
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.CATEGORY)) {
            		proc.setFunction(true);
            	}
        	}
        }
	}
	
	/*
	 * Get ProcedureUpdateCount object for the provided string value
	 * @param value the string value
	 * @return the ProcedureUpdateCount object corresponding to the provided string
	 */
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
	
    /*
     * Gets boolean value for the provided text string
     * @param text a text string
     * @return 'true' if provided string is "true", otherwise 'false'
     */
	private static boolean isTrue(final String text) {
        return Boolean.valueOf(text);
    }    

	/*
	 * Creates constraints for Table for Teiid DDL
	 * @param contraintNode the AstNode for the constraint
	 * @param table the BaseTable object
	 * @param roots the current model roots
	 * @param message the list of messages
	 */
	private void createTeiidConstraint(final AstNode constraintNode,
			final BaseTable table,
			final List<EObject> roots,
			final List<String> messages) throws CoreException {

		final String type = constraintNode.getProperty(TeiidDdlLexicon.Constraint.TYPE).toString();

		boolean primaryKeyConstraint = false;
		boolean uniqueConstraint = false;
		boolean accessPatternConstraint = false;
		boolean foreignKeyConstraint = false;
		boolean indexConstraint = false;
		RelationalEntity key = null;

		if (DdlConstants.PRIMARY_KEY.equals(type)) {
			key = FACTORY.createPrimaryKey();
			initialize(key, constraintNode);
			table.setPrimaryKey((PrimaryKey)key);
			primaryKeyConstraint = true;
		} else if (DdlConstants.INDEX.equals(type)) {
			// TODO need to process teiidddl:expression property
			key = FACTORY.createIndex();
			initialize(key, constraintNode);
			roots.add(key);
			indexConstraint = true;
		} else if (DdlConstants.UNIQUE.equals(type)) {
			key = FACTORY.createUniqueConstraint();
			initialize(key, constraintNode);
			table.getUniqueConstraints().add(key);
			uniqueConstraint = true;
		} else if (TeiidDdlConstants.TeiidNonReservedWord.ACCESSPATTERN.toDdl().equals(type)) {
			key = FACTORY.createAccessPattern();
			initialize(key, constraintNode);
			table.getAccessPatterns().add(key);
			accessPatternConstraint = true;
		} else if (DdlConstants.FOREIGN_KEY.equals(type)) {
			key = FACTORY.createForeignKey();
			initializeFK(table.getForeignKeys(), (ForeignKey)key, constraintNode);
			table.getForeignKeys().add(key);
			foreignKeyConstraint = true;
		} else {
			assert false : "Unexpected constraint type of '" + type + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		// process referenced columns multi-valued property
		final Object temp = constraintNode.getProperty(TeiidDdlLexicon.Constraint.REFERENCES);
		final List<AstNode> references = (List<AstNode>)temp;

		for (final AstNode ref : references) {
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
			} catch (final EntityNotFoundException error) {
				messages.add(error.getMessage());
			}
		}

		// special processing for foreign key
		if (foreignKeyConstraint) {
			final ForeignKey foreignKey = (ForeignKey)key;

			// must have a table reference
			final AstNode tableRefNode = (AstNode)constraintNode.getProperty(TeiidDdlLexicon.Constraint.TABLE_REFERENCE);

			try {
				final BaseTable tableRef = find(BaseTable.class, tableRefNode, null, roots);
				final PrimaryKey tableRefPrimaryKey = tableRef.getPrimaryKey();
				final List<Column> primaryKeyColumns = tableRef.getColumns();
				// check to see if foreign table columns are referenced
				final Object tempRefColumns = constraintNode.getProperty(TeiidDdlLexicon.Constraint.TABLE_REFERENCE_REFERENCES);

				final List<AstNode> foreignTableColumnNodes = (tempRefColumns==null) ? Collections.<AstNode>emptyList() : (List<AstNode>)tempRefColumns;
				int numPKColumns = primaryKeyColumns.size();
				int numFKColumns = foreignTableColumnNodes.size();
				
				if( foreignTableColumnNodes.isEmpty() ) {
					foreignKey.setUniqueKey(tableRefPrimaryKey);
				} else if( numPKColumns == numFKColumns ) {
					for(AstNode fTableColumn : foreignTableColumnNodes) {
						find(Column.class, fTableColumn, tableRef, roots);
					}
					foreignKey.setUniqueKey(tableRefPrimaryKey);
					//					} else {
						//						for (final Object obj : foreignTable.getUniqueConstraints()) {
					//							final UniqueConstraint uniqueKey = (UniqueConstraint)obj;
					//							final List<Column> uniqueKeyColumns = uniqueKey.getColumns();
					//
					//							if (uniqueKeyColumns.containsAll(foreignColumns) && uniqueKeyColumns.size() == foreignColumns.size()) {
					//								key.setUniqueKey(uniqueKey);
					//								break;
					//							}
					//						}
				} else {
					foreignKey.setUniqueKey(tableRefPrimaryKey);
				}
			} catch (final EntityNotFoundException error) {
				messages.add(error.getMessage());
			}
		}
	}
	
	/*
	 * Create Column from the provided AstNode within ColumnSet
	 * @param node the provided AstNode
	 * @param table the ColumnSet in which to create the column
	 */
	private void createColumn( final AstNode node,
                               final ColumnSet table) throws CoreException {
        final Column col = FACTORY.createColumn();
        table.getColumns().add(col);
        initialize(col, node);
        
        final String datatype = node.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
        col.setNativeType(datatype);
        
        EObject type = null;
        if(!isTeiidDdl) {
        	type = RelationalTypeMappingImpl.getInstance().getDatatype(datatype);
        } else {
        	type = getTeiidDatatype(datatype);
        }
    	col.setType(type);
    	
        Object prop = node.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
        // Datatype length
        if (prop != null) {
        	col.setLength(Integer.parseInt(prop.toString()));
        // If length is not provided for type 'string', use the default length specified in preferences...
        } else {
        	String dtName = ModelerCore.getWorkspaceDatatypeManager().getName(type);
        	if(dtName!=null && dtName.equalsIgnoreCase(STRING_TYPENAME)) {
        		col.setLength(ModelerCore.getTransformationPreferences().getDefaultStringLength());
        	}
        }
        prop = node.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
        if (prop != null) col.setPrecision(Integer.parseInt(prop.toString()));
        prop = node.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
        if (prop != null) col.setScale(Integer.parseInt(prop.toString()));
        prop = node.getProperty(StandardDdlLexicon.NULLABLE);
        if (prop != null) col.setNullable(prop.toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$
        prop = node.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
        if (prop != null) col.setDefaultValue(prop.toString());
        
        // Handle Teiid-specific properties and options
        if(isTeiidDdl) {
        	prop = node.getProperty(TeiidDdlLexicon.CreateTable.AUTO_INCREMENT);
        	if(prop!=null) col.setAutoIncremented(isTrue(prop.toString()));
        	handleTeiidColumnOptions(col,node);
        }
    }
	
	/*
	 * Handles statementOption common to all relational entities for Teiid DDL
	 * @param entity the RelationalEntity
	 * @param node the statementOption AstNode
	 * @return 'true' if the provided OPTION was a 'common' option, 'false' if not.
	 */
	private boolean handleTeiidCommonOption(RelationalEntity entity, AstNode optionNode) {
		boolean wasCommonOption = false;
		
    	String optionName = optionNode.getName();
        Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
        if(!CoreStringUtil.isEmpty(optionName)) {
        	String optionValueStr = (String)optionValue;
        	if(!CoreStringUtil.isEmpty(optionValueStr)) {
            	if(optionName.equalsIgnoreCase(TeiidDDLConstants.ANNOTATION)) {
            		this.teiidAnnotationMap.put(entity, optionValueStr);
            		wasCommonOption = true;
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.UUID)) {
            		// entity.setUUID();
            		wasCommonOption = true;
            	} else if(optionName.equalsIgnoreCase(TeiidDDLConstants.NAMEINSOURCE)) {
            		entity.setNameInSource(optionValueStr);
            		wasCommonOption = true;
            	} 
        	}
        }
        return wasCommonOption;
	}
	
	/*
	 * Get the Datatype for Teiid DDL.  First tries to match the datatype string with a teiid built-in type.
	 * If a built-in type is not found, then attempt to use the relational mapping to find a match.
	 * @param datatype the datatype string
	 * @param the matching EObject datatype
	 */
    private EObject getTeiidDatatype(String datatype) throws ModelerCoreException {
    	EObject resultType = null;
    	
    	// Look up matching Built-In type
        Object[] builtInTypes = ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
        String dtName = null;
        for( int i=0; i<builtInTypes.length; i++ ) {
            dtName = ModelerCore.getWorkspaceDatatypeManager().getName((EObject)builtInTypes[i]);
            if( dtName != null && dtName.equalsIgnoreCase(datatype)) {
                resultType = (EObject)builtInTypes[i];
                break;
            }
        }
        
        // Built In type not found, try mapping from native to built-in
        if(resultType==null) {
        	resultType = RelationalTypeMappingImpl.getInstance().getDatatype(datatype);
        }
                
        return resultType;
    }

    private void createKey( final AstNode node,
                            final BaseTable table,
                            final List<EObject> roots,
                            final List<String> messages ) throws CoreException {
        final String type = node.getProperty(StandardDdlLexicon.CONSTRAINT_TYPE).toString();
        if (DdlConstants.PRIMARY_KEY.equals(type)) {
            final PrimaryKey key = FACTORY.createPrimaryKey();
            table.setPrimaryKey(key);
            initialize(key, node);
            for (final AstNode node1 : node) {
                if (node1.hasMixin(StandardDdlLexicon.TYPE_COLUMN_REFERENCE)) try {
                	Column column = find(Column.class, node1, table, roots);
                	
                	if( column.getNullable() == NullableType.NULLABLE_UNKNOWN_LITERAL || column.getNullable() == NullableType.NULLABLE_LITERAL ) {
                		column.setNullable(NullableType.NO_NULLS_LITERAL);
                	}
                    key.getColumns().add(column);
                } catch (final EntityNotFoundException error) {
                    messages.add(error.getMessage());
                }
            }
        } else if (DdlConstants.FOREIGN_KEY.equals(type)) {
            final ForeignKey key = FACTORY.createForeignKey();
            initializeFK(table.getForeignKeys(), key, node);
            table.getForeignKeys().add(key);
            BaseTable foreignTable = null;
            final Set<Column> foreignColumns = new HashSet<Column>();
            for (final AstNode node1 : node) {
                if (node1.hasMixin(StandardDdlLexicon.TYPE_COLUMN_REFERENCE)) try {
                    key.getColumns().add(find(Column.class, node1, table, roots));
                } catch (final EntityNotFoundException error) {
                    messages.add(error.getMessage());
                }
                else if (node1.hasMixin(StandardDdlLexicon.TYPE_TABLE_REFERENCE)) try {
                    foreignTable = find(BaseTable.class, node1, null, roots);
                } catch (final EntityNotFoundException error) {
                    messages.add(error.getMessage());
                }
                else if (node1.hasMixin(StandardDdlLexicon.TYPE_FK_COLUMN_REFERENCE)) {
                    if (foreignTable != null) try {
                        foreignColumns.add(find(Column.class, node1, foreignTable, roots));
                    } catch (final EntityNotFoundException error) {
                        messages.add(error.getMessage());
                    }
                }
            }
            if (foreignTable != null) {
                final PrimaryKey primaryKey = foreignTable.getPrimaryKey();
                final List<Column> primaryKeyColumns = primaryKey.getColumns();
                if (foreignColumns.isEmpty()) key.setUniqueKey(primaryKey);
                if (primaryKeyColumns.containsAll(foreignColumns) && primaryKeyColumns.size() == foreignColumns.size()) key.setUniqueKey(primaryKey);
                else for (final Object obj : foreignTable.getUniqueConstraints()) {
                    final UniqueConstraint uniqueKey = (UniqueConstraint)obj;
                    final List<Column> uniqueKeyColumns = uniqueKey.getColumns();
                    if (uniqueKeyColumns.containsAll(foreignColumns) && uniqueKeyColumns.size() == foreignColumns.size()) {
                        key.setUniqueKey(uniqueKey);
                        break;
                    }
                }
            }
        } else if (DdlConstants.UNIQUE.equals(type)) {
            final UniqueConstraint key = FACTORY.createUniqueConstraint();
            table.getUniqueConstraints().add(key);
            initialize(key, node);
            for (final AstNode node1 : node) {
                if (node1.hasMixin(StandardDdlLexicon.TYPE_COLUMN_REFERENCE)) try {
                	Column column = find(Column.class, node1, table, roots);
                	
                	if( column.getNullable() == NullableType.NULLABLE_UNKNOWN_LITERAL || column.getNullable() == NullableType.NULLABLE_LITERAL ) {
                		column.setNullable(NullableType.NO_NULLS_LITERAL);
                	}
                    key.getColumns().add(column);
                } catch (final EntityNotFoundException error) {
                    messages.add(error.getMessage());
                }
            }
        }
    }

    private Procedure createProcedure( final AstNode procedureNode,
                                       final List<EObject> roots) throws EntityNotFoundException, CoreException {
        final Procedure procedure = FACTORY.createProcedure();
        final Info<Procedure> info = new Info<Procedure>(procedure, procedureNode, roots);
        if (info.schema == null) roots.add(procedure);
        else info.schema.getProcedures().add(procedure);
        initialize(procedure, procedureNode, info.name);
		// TODO: determine how to handle Procedure StatementOption
        // TODO: determine how to handle Procedure Statement

        if (procedureNode.getProperty(StandardDdlLexicon.DATATYPE_NAME) != null) {
            final ProcedureResult result = FACTORY.createProcedureResult();
            procedure.setResult(result);
            initialize(result, procedureNode);
        }
        for (final AstNode child : procedureNode) {
            if (child.hasMixin(OracleDdlLexicon.TYPE_FUNCTION_PARAMETER)
                || child.hasMixin(DerbyDdlLexicon.TYPE_FUNCTION_PARAMETER)
                || child.hasMixin(TeiidDdlLexicon.CreateProcedure.PARAMETER)) {
                final ProcedureParameter prm = FACTORY.createProcedureParameter();
                procedure.getParameters().add(prm);
                initialize(prm, child);
                final String datatype = child.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
                prm.setNativeType(datatype);

                EObject type = null;
                if(!isTeiidDdl) {
                	type = RelationalTypeMappingImpl.getInstance().getDatatype(datatype);
                } else {
                	type = getTeiidDatatype(datatype);
                }
                prm.setType(type);
                Object prop = child.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
                if (prop != null) prm.setLength(Integer.parseInt(prop.toString()));
                prop = child.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
                if (prop != null) prm.setPrecision(Integer.parseInt(prop.toString()));
                prop = child.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
                if (prop != null) prm.setScale(Integer.parseInt(prop.toString()));
                prop = child.getProperty(StandardDdlLexicon.NULLABLE);
                if (prop != null) prm.setNullable(prop.toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$
                prop = child.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
                if (prop != null) prm.setDefaultValue(prop.toString());
                prop = child.getProperty(OracleDdlLexicon.IN_OUT_NO_COPY);
                if (prop != null) {
                    final String direction = prop.toString();
                    if ("IN".equals(direction)) prm.setDirection(DirectionKind.IN_LITERAL); //$NON-NLS-1$
                    else if ("OUT".equals(direction) || "OUT NOCOPY".equals(direction)) prm.setDirection(DirectionKind.OUT_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
                    else if ("IN OUT".equals(direction) || "IN OUT NOCOPY".equals(direction)) prm.setDirection(DirectionKind.INOUT_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                	prop = child.getProperty(TeiidDdlLexicon.CreateProcedure.PARAMETER_TYPE);
                	// TODO - Determine how to handle 'VARIADIC'
                	if(prop!=null) {
                		final String direction = prop.toString();
                		if ("IN".equals(direction)) prm.setDirection(DirectionKind.IN_LITERAL); //$NON-NLS-1$
                		else if ("OUT".equals(direction) ) prm.setDirection(DirectionKind.OUT_LITERAL); //$NON-NLS-1$ 
                		else if ("INOUT".equals(direction) ) prm.setDirection(DirectionKind.INOUT_LITERAL); //$NON-NLS-1$ 
                	}
                }
                handleTeiidProcParamOptions(prm,child);
                // TODO: Determine how to handle teiidddl:result, ddl:defaultOption, ddl:statementOption
            } else if(child.hasMixin(TeiidDdlLexicon.CreateProcedure.RESULT_COLUMNS)) {
            	// TODO: determine how to handle Table flag property
            	final ProcedureResult result = FACTORY.createProcedureResult();
            	procedure.setResult(result);
            	initialize(result, procedureNode);
            	
            	for(AstNode resultCol: child) {
            		if(resultCol.hasMixin(TeiidDdlLexicon.CreateProcedure.RESULT_COLUMN)) {
                    	createColumn(resultCol,result);
            		}
            	}
            } else if(child.hasMixin(TeiidDdlLexicon.CreateProcedure.RESULT_DATA_TYPE)) {
            	final ProcedureResult result = FACTORY.createProcedureResult();
            	procedure.setResult(result);
            	initialize(result, procedureNode);
            	createColumn(child,result);
            } else if(child.hasMixin(StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
            	handleTeiidProcedureOption(procedure,child);
            }
        }
        return procedure;
    }

    /**
     * @return ddlFileName
     */
    public String ddlFileName() {
        return ddlFileName;
    }

    private <T extends RelationalEntity> T find( final Class<T> type,
                                                 final AstNode node,
                                                 final RelationalEntity parent,
                                                 final List<EObject> roots ) throws EntityNotFoundException, CoreException {
        return find(type, node.getName(), node, parent, roots);
    }

    <T extends RelationalEntity> T find( final Class<T> type,
                                         final String name,
                                         AstNode node,
                                         final RelationalEntity parent,
                                         final List<EObject> roots ) throws EntityNotFoundException, CoreException {
        for (final EObject obj : parent == null ? roots : parent.eContents()) {
            if (type.isInstance(obj)) {
                final T entity = (T)obj;
                if (entity.getName().equalsIgnoreCase(name)) return entity;
            } else if (parent == null && obj instanceof Schema) try {
                return find(type, name, node, (Schema)obj, roots);
            } catch (final EntityNotFoundException ignored) {
            }
        }
        // Throw EntityNotFoundException
        while (node.getProperty(StandardDdlLexicon.DDL_EXPRESSION) == null) {
            node = node.getParent();
        }
        String parentType = null;
        if (parent == null) parentType = DdlImporterI18n.MODEL;
        else for (final Class<?> parentInterface : parent.getClass().getInterfaces()) {
            if (RelationalEntity.class.isAssignableFrom(parentInterface)) parentType = parentInterface.getSimpleName();
        }
        throw new EntityNotFoundException(I18n.format(DdlImporterI18n.ENTITY_NOT_FOUND_MSG,
                                                      type.getSimpleName(),
                                                      name,
                                                      parentType,
                                                      parent == null ? modelName : parent.getName(),
                                                      node.getProperty(StandardDdlLexicon.DDL_START_LINE_NUMBER).toString(),
                                                      node.getProperty(StandardDdlLexicon.DDL_START_COLUMN_NUMBER).toString()));
    }

    /**
     * @return The difference report for the {@link #importDdl(List, IProgressMonitor, int) imported} model
     */
    public DifferenceReport getChangeReport() {
        return chgProcessor == null ? null : chgProcessor.getDifferenceReport();
    }

    private void handleStatus( final IStatus status ) {
        if (!status.isOK()) {
            if (status.getException() != null) throw CoreModelerPlugin.toRuntimeException(status.getException());
            throw new RuntimeException(status.getMessage());
        }
    }

    /**
     * @param messages
     * @param monitor
     * @param totalWork
     */
    public void importDdl( final List<String> messages,
                           final IProgressMonitor monitor,
                           final int totalWork ) {
        if (chgProcessor != null) return;
        OperationUtil.perform(new Unreliable() {

            private FileReader reader = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (reader != null) reader.close();
            }

            @Override
            public void tryToDo() throws Exception {
                reader = new FileReader(ddlFileName());
                importDdl(reader, messages, monitor, totalWork);
            }
        });
    }

    void importDdl( final FileReader reader,
                    final List<String> messages,
                    final IProgressMonitor monitor,
                    final int totalWork ) throws IOException, CoreException {

        final int workUnit = totalWork / 3;
        
        // ------------------------------------------------------------------------------
        // Parse the DDL from the file
        // ------------------------------------------------------------------------------
        monitor.subTask(DdlImporterI18n.PARSING_DDL_MSG);
        // Read the file contents
        final char[] buf = new char[FileUtils.DEFAULT_BUFFER_SIZE];
        final StringBuilder builder = new StringBuilder();
        for (int charTot = reader.read(buf); charTot >= 0; charTot = reader.read(buf))
            builder.append(buf, 0, charTot);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        final String ddlString = builder.toString();
        
        // Parse the DDL
        final DdlParsers parsers = new DdlParsers();
        final AstNode rootNode = parsers.parse(ddlString, ddlFileName);
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(workUnit);
        
        // ------------------------------------------------------------------------------
        // Set up DifferenceProcessor
        //   - startingSelector -- existing model
        //   - endingSelector   -- generated from parsed ddl nodes 
        // ------------------------------------------------------------------------------
        monitor.subTask(DdlImporterI18n.CREATING_MODEL_MSG);
        model = ModelerCore.create(modelFile);
        final ModelResourceSelector startingSelector = new ModelResourceSelector(model);
        final URI mdlUri = URI.createFileURI(model.getPath().toFile().getAbsolutePath());
        final ModelAnnotation mdlAnnotation = model.getModelAnnotation();
        
        final TransientModelSelector endingSelector = new TransientModelSelector(mdlUri,mdlAnnotation);
        
        final DifferenceProcessor diffProcessor = createDifferenceProcessor(startingSelector,endingSelector);
        final List<EObject> roots = endingSelector.getRootObjects();
        for (final AstNode node : rootNode) {
            if (node.hasMixin(StandardDdlLexicon.TYPE_CREATE_SCHEMA_STATEMENT)) {
                final Schema schema = FACTORY.createSchema();
                roots.add(schema);
                initialize(schema, node);
                for (final AstNode node1 : node) {
                    create(node1, roots, schema, messages);
                }
            } else create(node, roots, null, messages);
        }
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(workUnit);
        
        
        // ------------------------------------------------------------------------------
        // Execute generates the differenceReport
        // ------------------------------------------------------------------------------
        monitor.subTask(DdlImporterI18n.CREATING_CHANGE_REPORT_MSG);
        handleStatus(diffProcessor.execute(monitor));
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(workUnit);
        
        chgProcessor = diffProcessor;
    }
    
    /*
     * Create DifferenceProcessor using starting and ending ModelSelector
     * @param startingSelector the starting selector
     * @param endingSelector the ending selector
     * @return the difference processor
     */
    private DifferenceProcessor createDifferenceProcessor( final ModelSelector startingSelector,
                                                           final ModelSelector endingSelector ) {
        CoreArgCheck.isNotNull(startingSelector);
        CoreArgCheck.isNotNull(endingSelector);
        final DifferenceProcessor processor = new DifferenceProcessorImpl(startingSelector, endingSelector);
        processor.addEObjectMatcherFactories(ModelerComparePlugin.createEObjectMatcherFactories());
        return processor;
    }

    private void initialize( final RelationalEntity entity,
                             final AstNode node ) {
        initialize(entity, node, node.getName());
    }

    private void initializeFK( final List<ForeignKey> currentFKs,
                               final ForeignKey key,
                               final AstNode node ) {
        // Get Name from DDL node
        String fkName = node.getName();
        // Make sure not to add duplicate FK names
        String uniqueName = getUniqueFKName(currentFKs, fkName);

        initialize(key, node, uniqueName);
    }

    /*
     * Helper method for creating unique FK names
     * @param currentFKs the List of ForeignKeys currently on the table
     * @param newFKName the proposed name for the new FK
     * @return the unique name - generated from the proposed name
     */
    private String getUniqueFKName( List<ForeignKey> currentFKs,
                                    String newFKName ) {
        // If current list is empty, no need to check names
        if (currentFKs == null || currentFKs.isEmpty()) return newFKName;

        // Use name validator for unique name generation
        StringNameValidator nameValidator = new StringNameValidator();

        // Add the current FK names to the validator
        for (ForeignKey fk : currentFKs) {
            nameValidator.addExistingName(fk.getName());
        }

        // Make the proposed name unique
        return nameValidator.createValidUniqueName(newFKName);
    }

    private void initialize( final RelationalEntity entity,
                             final AstNode node,
                             final String name ) {
        entity.setName(name);
        entity.setNameInSource(name);

        // descriptions must wait to be set until container and model type has been set
        if (optToSetModelEntityDescription) {
            final Object prop = node.getProperty(StandardDdlLexicon.DDL_EXPRESSION);
            if (prop != null) {
                this.descriptionMap.put(entity, prop.toString());
            }
        }
    }

    private <T extends Table> T initializeTable( final T table,
                                                 final AstNode node,
                                                 final List<EObject> roots ) throws EntityNotFoundException, CoreException {
        final Info<T> info = new Info<T>(table, node, roots);
        if (info.schema == null) roots.add(table);
        else info.schema.getTables().add(table);
        initialize(table, node, info.name);
        return table;
    }

    /**
     * @return model
     */
    public ModelResource model() {
        return model;
    }

    /**
     * @return modelFile
     */
    public IFile modelFile() {
        return modelFile;
    }

    /**
     * @return modelFolder
     */
    public IContainer modelFolder() {
        return modelFolder;
    }

    /**
     * @return modelType
     */
    public ModelType modelType() {
        return modelType;
    }

    /**
     * @param monitor
     * @param totalWork
     */
    public void save( final IProgressMonitor monitor,
                      final int totalWork ) {
        monitor.subTask(DdlImporterI18n.SAVING_MODEL_MSG);
        try {
            if (!model.exists()) {
                final ModelAnnotation modelAnnotation = model.getModelAnnotation();
                modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
                modelAnnotation.setModelType(modelType);
            }
            final MergeProcessor mergeProcessor = ModelerComparePlugin.createMergeProcessor(chgProcessor,
                                                                                            ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes(),
                                                                                            true);
            handleStatus(mergeProcessor.execute(monitor));
            model.save(monitor, false);

            // If Teiid DDL, use descriptionMap created from teiid annotations
            boolean setDescriptions = false;
            if(isTeiidDdl) {
                for (Map.Entry<RelationalEntity, String> entry : this.teiidAnnotationMap.entrySet()) {
                    ModelerCore.getModelEditor().setDescription(entry.getKey(), entry.getValue());
                    setDescriptions = true;
                }
            }
            
            // If user chose to use DDL as description, now set those descriptions (model type and container *must* be already set)
            if (optToSetModelEntityDescription) {
                for (Map.Entry<RelationalEntity, String> entry : this.descriptionMap.entrySet()) {
                    ModelerCore.getModelEditor().setDescription(entry.getKey(), entry.getValue());
                    setDescriptions = true;
                }
             } 
            
            if(setDescriptions) {
                // save again
                model.save(monitor, false);
            }
            
        } catch (final Exception error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
        monitor.worked(totalWork);
        monitor.done();
    }

    /**
     * @param ddlFileName
     */
    public void setDdlFileName( String ddlFileName ) {
        this.ddlFileName = null;
        chgProcessor = null;
        if (ddlFileName == null) throw new EmptyArgumentException("ddlFileName"); //$NON-NLS-1$
        ddlFileName = ddlFileName.trim();
        if (ddlFileName.isEmpty()) throw new EmptyArgumentException("ddlFileName"); //$NON-NLS-1$
        final File file = new File(ddlFileName);
        if (!file.exists() || file.isDirectory()) throw new IllegalArgumentException(DdlImporterI18n.DDL_FILE_NOT_FOUND_MSG);
        this.ddlFileName = ddlFileName;
    }

    /**
     * @param modelFolder
     */
    public void setModelFolder( final IContainer modelFolder ) {
        this.modelFolder = modelFolder;
        chgProcessor = null;
    }

    /**
     * @param modelFolderName
     */
    public void setModelFolder( String modelFolderName ) {
        modelFolder = null;
        chgProcessor = null;
        if (modelFolderName == null) throw new EmptyArgumentException("modelFolderName"); //$NON-NLS-1$
        modelFolderName = modelFolderName.trim();
        final IPath modelFolderPath = Path.fromPortableString(modelFolderName).makeAbsolute();
        if (modelFolderName.isEmpty() || modelFolderPath.segmentCount() == 0) throw new EmptyArgumentException("modelFolderName"); //$NON-NLS-1$
        // Verify project is valid
        final String projectName = modelFolderPath.segment(0);
        IWorkspace workspace = ModelerCore.getWorkspace();
        final IWorkspaceRoot root = workspace.getRoot();
        if (root.findMember(projectName) != null) {
            boolean found = false;
            for (final IProject project : projects)
                if (projectName.equals(project.getName())) {
                    found = true;
                    break;
                }
            if (!found) throw new IllegalArgumentException(DdlImporterI18n.MODEL_FOLDER_IN_NON_MODEL_PROJECT_MSG);
        }
        // Verify folder is valid
        if (!workspace.validatePath(modelFolderPath.toString(), IResource.PROJECT | IResource.FOLDER).isOK()) throw new IllegalArgumentException(
                                                                                                                                                 DdlImporterI18n.INVALID_MODEL_FOLDER_MSG);
        final IResource resource = root.findMember(modelFolderPath);
        // Verify final segment in folder is not a file
        if (resource instanceof IFile) throw new IllegalArgumentException(DdlImporterI18n.MODEL_FOLDER_IS_FILE_MSG);
        if (resource == null) {
            if (modelFolderPath.segmentCount() == 1) modelFolder = root.getProject(projectName);
            else modelFolder = root.getFolder(modelFolderPath);
        } else modelFolder = (IContainer)resource;
    }

    /**
     * @param modelName
     */
    public void setModelName( String modelName ) {
        this.modelName = null;
        modelFile = null;
        chgProcessor = null;
        if (modelName == null) throw new EmptyArgumentException("modelName"); //$NON-NLS-1$
        modelName = modelName.trim();
        if (modelName.isEmpty()) throw new EmptyArgumentException("modelName"); //$NON-NLS-1$
        // Verify name is valid
        final IWorkspace workspace = ModelerCore.getWorkspace();
        if (!workspace.validateName(modelName, IResource.FILE).isOK()) throw new IllegalArgumentException(
                                                                                                          DdlImporterI18n.INVALID_MODEL_NAME_MSG);
        if (modelFolder != null) {
            final IWorkspaceRoot root = workspace.getRoot();
            IPath modelPath = modelFolder.getFullPath().append(modelName);
            if (!modelName.endsWith(ModelerCore.MODEL_FILE_EXTENSION)) modelPath = modelPath.addFileExtension(ModelerCore.MODEL_FILE_EXTENSION.substring(1));
            if (modelFolder.exists()) {
                // Verify name is not a folder
                final IResource resource = root.findMember(modelPath);
                if (resource instanceof IContainer) throw new IllegalArgumentException(DdlImporterI18n.MODEL_NAME_IS_FOLDER_MSG);
                if (resource == null) modelFile = root.getFile(modelPath);
                else {
                    // Verify name is not a non-model file
                    if (!ModelUtil.isModelFile(resource)) throw new IllegalArgumentException(
                                                                                             DdlImporterI18n.MODEL_NAME_IS_NON_MODEL_FILE_MSG);
                    // Verify name is not a non-relational model
                    if (!RelationalPackage.eNS_URI.equals(ModelUtil.getXmiHeader(resource).getPrimaryMetamodelURI())) throw new IllegalArgumentException(
                                                                                                                                                         DdlImporterI18n.MODEL_NAME_IS_NON_RELATIONAL_MODEL_MSG);
                    modelFile = (IFile)resource;
                }
            } else modelFile = root.getFile(modelPath);
        }
        this.modelName = new Path(modelName).removeFileExtension().lastSegment();
    }

    /**
     * @param modelType Sets modelType to the specified value.
     */
    public void setModelType( final ModelType modelType ) {
        this.modelType = modelType;
        chgProcessor = null;
    }

    /**
     * @param optToCreateModelEntitiesForUnsupportedDdl
     */
    public void setOptToCreateModelEntitiesForUnsupportedDdl( final boolean optToCreateModelEntitiesForUnsupportedDdl ) {
        this.optToCreateModelEntitiesForUnsupportedDdl = optToCreateModelEntitiesForUnsupportedDdl;
        chgProcessor = null;
    }

    /**
     * @param optToSetModelEntityDescription
     */
    public void setOptToSetModelEntityDescription( final boolean optToSetModelEntityDescription ) {
        this.optToSetModelEntityDescription = optToSetModelEntityDescription;
        chgProcessor = null;
    }

    /**
     * 
     */
    public void undoImport() {
        chgProcessor = null;
    }

    private class EntityNotFoundException extends Exception {

        private static final long serialVersionUID = 1L;

        EntityNotFoundException( final String message ) {
            super(message);
        }
    }

    private class Info<T extends RelationalEntity> {

        final Schema schema;
        final String name;

        Info( final T entity,
              final AstNode node,
              final List<EObject> roots ) throws EntityNotFoundException, CoreException {
            final String name = node.getName();
            final int ndx = name.indexOf('.');
            if (ndx < 0) {
                schema = null;
                this.name = name;
            } else if (isTeiidDdl) {
            	schema = null;
                this.name = name.substring(ndx + 1);
            } else {
                schema = find(Schema.class, name.substring(0, ndx), node, null, roots);
                this.name = name.substring(ndx + 1);
            }
        }
    }
}