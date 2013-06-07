/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlConstants;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ddl.DdlImporterModel.EntityDescriptions;
import org.teiid.designer.ddl.DdlImporterModel.EntityDescriptions.DescriptionOperation;
import org.teiid.designer.ddl.importer.DdlImporterI18n;
import org.teiid.designer.ddl.importer.TeiidDDLConstants;
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
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.UniqueConstraint;


/**
 * Teiid DDL node importer
 */
public class TeiidDdlImporter extends StandardImporter {

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
     * Handles statementOption common to all relational entities for Teiid DDL
     * @param entity the RelationalEntity
     * @param optionNode the statementOption AstNode
     * @return 'true' if the provided OPTION was a 'common' option, 'false' if not.
     */
    private boolean handleCommonOption(RelationalEntity entity, AstNode optionNode) {
        boolean wasCommonOption = false;
        
        String optionName = optionNode.getName();
        Object optionValue = optionNode.getProperty(StandardDdlLexicon.VALUE);
        if(!CoreStringUtil.isEmpty(optionName)) {
            String optionValueStr = (String)optionValue;
            if(!CoreStringUtil.isEmpty(optionValueStr)) {
                if(optionName.equalsIgnoreCase(TeiidDDLConstants.ANNOTATION)) {
                    getImporterModel().addDescription(entity, optionValueStr, DescriptionOperation.PREPEND);
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

    /**
     * Handle the OPTION keys that may be set on Tables for Teiid DDL
     * @param table
     * @param node 
     */
    private void handleTableOption(BaseTable table, AstNode node) {
        boolean wasCommonOption = handleCommonOption(table,node);
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

    /**
     * Handle the OPTION keys that may be set on ProcedureParameters for Teiid DDL
     * @param procParam the ProcedureParameter
     * @param paramNode the procedure parameter AstNode
     */
    private void handleProcParamOptions(ProcedureParameter procParam, AstNode paramNode) {
        List<AstNode> children = paramNode.getChildren();
        for(AstNode child: children) {
            if(is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
                handleProcParamOption(procParam,child);
            }
        }
    }

    /**
     * Handle the OPTION keys that may be set on a ProcedureParameter for Teiid DDL
     * @param procParam the ProcedureParameter
     * @param procParamOptionNode a statementOption node for the procedure parameter
     */
    private void handleProcParamOption(ProcedureParameter procParam, AstNode procParamOptionNode) {
        boolean wasCommonOption = handleCommonOption(procParam,procParamOptionNode);
        if(wasCommonOption) return;
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

    /**
     * Handle a statementOption for a Procedure for Teiid DDL
     * @param proc the Procedure
     * @param procOptionNode a statementOption for a procedure
     */
    private void handleProcedureOption(Procedure proc, AstNode procOptionNode) {
        boolean wasCommonOption = handleCommonOption(proc,procOptionNode);
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

    @Override
    protected void handleColumnOption(Column column, AstNode columnOptionNode) {
        boolean wasCommonOption = handleCommonOption(column,columnOptionNode);
        if(wasCommonOption)
            return;
        
        String optionName = columnOptionNode.getName();
        Object optionValue = columnOptionNode.getProperty(StandardDdlLexicon.VALUE);

        if(CoreStringUtil.isEmpty(optionName))
            return;

        String optionValueStr = (String)optionValue;
        if(CoreStringUtil.isEmpty(optionValueStr))
            return;

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

    @Override
    protected void handleColumnOptions(Column column, AstNode columnNode) {
        List<AstNode> children = columnNode.getChildren();
        for(AstNode child: children) {
            if(is(child, StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
                handleColumnOption(column,child);
            }
        }
    }

    @Override
    protected Column createColumn(AstNode node, ColumnSet table) throws Exception {
        Column column = super.createColumn(node, table);

        // Handle Teiid-specific properties and options
        Object prop = node.getProperty(TeiidDdlLexicon.CreateTable.AUTO_INCREMENT);
        if(prop != null)
            column.setAutoIncremented(isTrue(prop.toString()));

        handleColumnOptions(column,node);
        return column;
    }

    @Override
    protected Procedure createProcedure(AstNode procedureNode, List<EObject> roots) throws Exception {
        Procedure procedure = super.createProcedure(procedureNode, roots);

        for (AstNode child : procedureNode) {
            if (is(child, TeiidDdlLexicon.CreateProcedure.PARAMETER)) {
                ProcedureParameter prm = getFactory().createProcedureParameter();
                procedure.getParameters().add(prm);
                initialize(prm, child);
                String datatype = child.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
                prm.setNativeType(datatype);

                EObject type = getDataType(datatype);
                prm.setType(type);

                Object prop = child.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
                if (prop != null)
                    prm.setLength(Integer.parseInt(prop.toString()));

                prop = child.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
                if (prop != null)
                    prm.setPrecision(Integer.parseInt(prop.toString()));

                prop = child.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
                if (prop != null)
                    prm.setScale(Integer.parseInt(prop.toString()));

                prop = child.getProperty(StandardDdlLexicon.NULLABLE);
                if (prop != null)
                    prm.setNullable(prop.toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$

                prop = child.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
                if (prop != null)
                    prm.setDefaultValue(prop.toString());

                prop = child.getProperty(TeiidDdlLexicon.CreateProcedure.PARAMETER_TYPE);
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

                handleProcParamOptions(prm,child);

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
                handleProcedureOption(procedure,child);
            }
        }
        
        return procedure;
    }

    @Override
    protected void create(AstNode node, List<EObject> roots, Schema schema) throws Exception {

        // -----------------------------------------------------------------------
        // Handle Creation of Teiid Entities
        // -----------------------------------------------------------------------
        if (is(node, TeiidDdlLexicon.CreateTable.TABLE_STATEMENT)
            || is(node, TeiidDdlLexicon.CreateTable.VIEW_STATEMENT)) {

            BaseTable table = initializeTable(getFactory().createBaseTable(), node, roots);

            for (AstNode child : node) {
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
                    handleTableOption(table, child);
                }
            }
        } else if (is(node, TeiidDdlLexicon.CreateProcedure.PROCEDURE_STATEMENT)
                   || is(node, TeiidDdlLexicon.CreateProcedure.FUNCTION_STATEMENT)) {
            createProcedure(node, roots);

            // Handle Alter Table
        } else if (is(node, TeiidDdlLexicon.AlterOptions.TABLE_STATEMENT)) {
            BaseTable table = find(BaseTable.class, node, null, roots);
            if (table != null) {
                for (AstNode child : node) {
                    if (is(child, TeiidDdlLexicon.AlterOptions.OPTIONS_LIST)) {
                        List<AstNode> nodeList = child.getChildren();
                        for (AstNode listItem : nodeList) {
                            if (listItem.hasMixin(StandardDdlLexicon.TYPE_STATEMENT_OPTION)) {
                                handleTableOption(table, listItem);
                            }
                        }
                    }
                }
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

    @Override
    public void importFinalize() throws Exception {
        /*
         * The descriptions are added from to the model's descriptions
         * from the teiid annotations.
         */
        for (EntityDescriptions pair : getImporterModel().getEntityDescriptions()) {
            ModelerCore.getModelEditor().setDescription(pair.getEntity(), pair.getPreferredDescription());
        }
    }
}
