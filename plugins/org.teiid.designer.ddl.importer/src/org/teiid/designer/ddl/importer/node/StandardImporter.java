/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer.node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.core.designer.I18n;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.ddl.DdlImporterModel;
import org.teiid.designer.ddl.DdlImporterModel.EntityDescriptions;
import org.teiid.designer.ddl.DdlImporterModel.EntityDescriptions.DescriptionOperation;
import org.teiid.designer.ddl.importer.DdlImporterI18n;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ColumnSet;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMappingImpl;

/**
 * Node importer for standard DDL
 */
public class StandardImporter extends AbstractImporter {

    class Info {

        protected Schema schema;
        protected String name;

        public Info(AstNode node, List<EObject> roots) throws Exception {
            init(node, roots);
        }

        protected void init(AstNode node, List<EObject> roots) throws Exception {
            String name = node.getName();
            int ndx = name.indexOf('.');
            if (ndx < 0) {
                schema = null;
                this.name = name;
            } else {
                schema = find(Schema.class, name.substring(0, ndx), node, null, roots);
                this.name = name.substring(ndx + 1);
            }
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
        public Schema getSchema() {
            return schema;
        }
    }

    private static final String STRING_TYPENAME = "string"; //$NON-NLS-1$

    /**
     * Create new info object
     *
     * @param node
     * @param roots
     *
     * @return new info object
     *
     * @throws Exception
     */
    protected Info createInfo(AstNode node, List<EObject> roots) throws Exception {
        return new Info(node, roots);
    }

    /**
     * @param type
     * @param name
     * @param node
     * @param parent
     * @param roots
     *
     * @return entity matching parameters
     *
     * @throws EntityNotFoundException
     * @throws CoreException
     */
    protected <T extends RelationalEntity> T find(Class<T> type, String name, AstNode node,
                                                                                          RelationalEntity parent, List<EObject> roots) throws EntityNotFoundException, CoreException {
        for (EObject obj : parent == null ? roots : parent.eContents()) {
            if (type.isInstance(obj)) {
                T entity = (T)obj;
                if (entity.getName().equalsIgnoreCase(name))
                    return entity;
            } else if (parent == null && obj instanceof Schema) {
                try {
                    return find(type, name, node, (Schema)obj, roots);
                } catch (EntityNotFoundException ignored) {
                }
            }
        }

        while (node.getProperty(StandardDdlLexicon.DDL_EXPRESSION) == null) {
            node = node.getParent();
        }

        String parentType = null;
        if (parent == null)
            parentType = DdlImporterI18n.MODEL;
        else for (Class<?> parentInterface : parent.getClass().getInterfaces()) {
            if (RelationalEntity.class.isAssignableFrom(parentInterface))
                parentType = parentInterface.getSimpleName();
        }

        throw new EntityNotFoundException(I18n.format(DdlImporterI18n.ENTITY_NOT_FOUND_MSG,
                                                      type.getSimpleName(),
                                                      name,
                                                      parentType,
                                                      parent == null ? getImporterModel().getModelName() : parent.getName(),
                                                      node.getProperty(StandardDdlLexicon.DDL_START_LINE_NUMBER).toString(),
                                                      node.getProperty(StandardDdlLexicon.DDL_START_COLUMN_NUMBER).toString()));
    }

    /**
     * @param type
     * @param node
     * @param parent
     * @param roots
     * @return entity matching parameters
     *
     * @throws EntityNotFoundException
     * @throws CoreException
     */
    protected <T extends RelationalEntity> T find(Class<T> type, AstNode node, RelationalEntity parent,
                                                                                          List<EObject> roots) throws EntityNotFoundException, CoreException {
        return find(type, node.getName(), node, parent, roots);
    }

    /**
     * 
     * @param entity
     * @param node
     * @param name
     */
    protected void initialize(RelationalEntity entity, AstNode node, String name) {
        entity.setName(name);
        entity.setNameInSource(name);

        // descriptions must wait to be set until container and model type has been set
        if (getImporterModel().optToSetModelEntityDescription()) {
            Object prop = node.getProperty(StandardDdlLexicon.DDL_EXPRESSION);
            if (prop != null) {
                getImporterModel().addDescription(entity, prop.toString(), DescriptionOperation.PREPEND);
            }
        }
    }


    /**
     * @param entity
     * @param node
     */
    protected void initialize(RelationalEntity entity, AstNode node) {
        initialize(entity, node, node.getName());
    }

    /**
     * Helper method for creating unique FK names
     * @param currentFKs the List of ForeignKeys currently on the table
     * @param newFKName the proposed name for the new FK
     * @return the unique name - generated from the proposed name
     */
    protected String getUniqueFKName(List<ForeignKey> currentFKs, String newFKName) {
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

    /**
     * @param currentFKs
     * @param key
     * @param node
     */
    protected void initializeFK(List<ForeignKey> currentFKs, ForeignKey key, AstNode node) {
        // Get Name from DDL node
        String fkName = node.getName();
        // Make sure not to add duplicate FK names
        String uniqueName = getUniqueFKName(currentFKs, fkName);

        initialize(key, node, uniqueName);
    }

    /**
     * @param table
     * @param node
     * @param roots
     *
     * @return initialised table
     *
     * @throws Exception
     */
    protected <T extends Table> T initializeTable(T table, AstNode node, List<EObject> roots) throws Exception {
        Info info = createInfo(node, roots);
        if (info.getSchema() == null)
            roots.add(table);
        else
            info.getSchema().getTables().add(table);

        initialize(table, node, info.getName());
        return table;
    }

    /**
     * Handle a statement OPTION key for Column for DDL
     *
     * @param column the Column
     * @param columnOptionNode a statementOption node for a column
     */
    protected void handleColumnOption(Column column, AstNode columnOptionNode) {
        // Do nothing
    }

    /**
     * Handle the OPTION keys that may be set on Column for DDL
     *
     * @param column the Column
     * @param columnNode the column AstNode
     */
    protected void handleColumnOptions(Column column, AstNode columnNode) {
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
    protected EObject getDataType(String datatype) throws Exception {
        return RelationalTypeMappingImpl.getInstance().getDatatype(datatype);
    }

    /**
     * Create Column from the provided AstNode within ColumnSet
     * @param node the provided AstNode
     * @param table the ColumnSet in which to create the column
     * @return the column
     *
     * @throws Exception 
     */
    protected Column createColumn(AstNode node, ColumnSet table) throws Exception {
        Column col = getFactory().createColumn();
        table.getColumns().add(col);
        initialize(col, node);

        String datatype = node.getProperty(StandardDdlLexicon.DATATYPE_NAME).toString();
        col.setNativeType(datatype);
        
        EObject type = getDataType(datatype);
        col.setType(type);

        // Datatype length
        Object prop = node.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
        if (prop != null) {
            col.setLength(Integer.parseInt(prop.toString()));
        } else {
            // Length is not provided for type 'string', use the default length specified in preferences...
            String dtName = ModelerCore.getWorkspaceDatatypeManager().getName(type);
            if(dtName != null && dtName.equalsIgnoreCase(STRING_TYPENAME)) {
                col.setLength(ModelerCore.getTransformationPreferences().getDefaultStringLength());
            }
        }

        prop = node.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
        if (prop != null)
            col.setPrecision(Integer.parseInt(prop.toString()));

        prop = node.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
        if (prop != null)
            col.setScale(Integer.parseInt(prop.toString()));

        prop = node.getProperty(StandardDdlLexicon.NULLABLE);
        if (prop != null)
            col.setNullable(prop.toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$

        prop = node.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
        if (prop != null)
            col.setDefaultValue(prop.toString());

        return col;
    }

    /**
     * Create primary key
     *
     * @param node
     * @param table
     * @param roots
     *
     * @throws CoreException
     */
    protected void createKey(AstNode node, BaseTable table, List<EObject> roots) throws CoreException {
        String type = node.getProperty(StandardDdlLexicon.CONSTRAINT_TYPE).toString();
        if (DdlConstants.PRIMARY_KEY.equals(type)) {
            PrimaryKey key = getFactory().createPrimaryKey();
            table.setPrimaryKey(key);
            initialize(key, node);

            for (AstNode node1 : node) {
                if (is(node1, StandardDdlLexicon.TYPE_COLUMN_REFERENCE)) {
                    try {
                        Column column = find(Column.class, node1, table, roots);

                        if (column.getNullable() == NullableType.NULLABLE_UNKNOWN_LITERAL
                            || column.getNullable() == NullableType.NULLABLE_LITERAL) {
                            column.setNullable(NullableType.NO_NULLS_LITERAL);
                        }
                        key.getColumns().add(column);
                    } catch (EntityNotFoundException error) {
                        addProgressMessage(error.getMessage());
                    }
                }
            }
        } else if (DdlConstants.FOREIGN_KEY.equals(type)) {
            ForeignKey key = getFactory().createForeignKey();
            initializeFK(table.getForeignKeys(), key, node);
            table.getForeignKeys().add(key);
            BaseTable foreignTable = null;
            Set<Column> foreignColumns = new HashSet<Column>();

            for (AstNode node1 : node) {
                try {
                    if (is(node1, StandardDdlLexicon.TYPE_COLUMN_REFERENCE))
                        key.getColumns().add(find(Column.class, node1, table, roots));
                    else if (is(node1, StandardDdlLexicon.TYPE_TABLE_REFERENCE))
                        foreignTable = find(BaseTable.class, node1, null, roots);
                    else if (is(node1, StandardDdlLexicon.TYPE_FK_COLUMN_REFERENCE) && foreignTable != null) {
                        foreignColumns.add(find(Column.class, node1, foreignTable, roots));
                    }
                } catch (Exception error) {
                    addProgressMessage(error.getMessage());
                }
            }

            if (foreignTable == null)
                return;

            PrimaryKey primaryKey = foreignTable.getPrimaryKey();
            List<Column> primaryKeyColumns = primaryKey.getColumns();
            if (foreignColumns.isEmpty())
                key.setUniqueKey(primaryKey);

            if (primaryKeyColumns.containsAll(foreignColumns) && primaryKeyColumns.size() == foreignColumns.size())
                key.setUniqueKey(primaryKey);
            else {
                for (Object obj : foreignTable.getUniqueConstraints()) {
                    UniqueConstraint uniqueKey = (UniqueConstraint)obj;
                    List<Column> uniqueKeyColumns = uniqueKey.getColumns();
                    if (uniqueKeyColumns.containsAll(foreignColumns) && uniqueKeyColumns.size() == foreignColumns.size()) {
                        key.setUniqueKey(uniqueKey);
                        break;
                    }
                }
            }

        } else if (DdlConstants.UNIQUE.equals(type)) {
            UniqueConstraint key = getFactory().createUniqueConstraint();
            table.getUniqueConstraints().add(key);
            initialize(key, node);

            for (AstNode node1 : node) {
                if (! is(node1, StandardDdlLexicon.TYPE_COLUMN_REFERENCE))
                    continue;

                try {
                    Column column = find(Column.class, node1, table, roots);

                    if (column.getNullable() == NullableType.NULLABLE_UNKNOWN_LITERAL || column.getNullable() == NullableType.NULLABLE_LITERAL) {
                        column.setNullable(NullableType.NO_NULLS_LITERAL);
                    }
                    key.getColumns().add(column);
                } catch (Exception error) {
                    addProgressMessage(error.getMessage());
                }
            }
        }
    }

    /**
     * Create a procedure
     *
     * @param procedureNode
     * @param roots
     *
     * @return a procedure
     *
     * @throws Exception
     */
    protected Procedure createProcedure( AstNode procedureNode, List<EObject> roots) throws Exception {
        Procedure procedure = getFactory().createProcedure();
        Info info = createInfo(procedureNode, roots);
        if (info.getSchema() == null)
            roots.add(procedure);
        else
            info.getSchema().getProcedures().add(procedure);

        initialize(procedure, procedureNode, info.getName());
        // TODO: determine how to handle Procedure StatementOption
        // TODO: determine how to handle Procedure Statement

        if (procedureNode.getProperty(StandardDdlLexicon.DATATYPE_NAME) != null) {
            ProcedureResult result = getFactory().createProcedureResult();
            procedure.setResult(result);
            initialize(result, procedureNode);
        }

        return procedure;
    }

    /**
     * Create a Model Entity, using the provided AstNode
     * @param node the provided AstNode
     * @param roots the current model roots
     * @param schema the schema
     * @throws Exception 
     */
    protected void create(AstNode node, List<EObject> roots, Schema schema) throws Exception {
        // -----------------------------------------------------------------------
        // Standard DDL 
        // -----------------------------------------------------------------------
        if (is(node, StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT)) {
            BaseTable table = initializeTable(getFactory().createBaseTable(), node, roots);
            for (AstNode child : node) {
                if (is(child, StandardDdlLexicon.TYPE_COLUMN_DEFINITION))
                    createColumn(child, table);
                else if (is(child, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT))
                    createKey(child, table, roots);
            }
        } else if (is(node, StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT)) {
            if (getImporterModel().getModelType() != ModelType.VIRTUAL_LITERAL 
                        && getImporterModel().optToCreateModelEntitiesForUnsupportedDdl())

                initializeTable(getFactory().createView(), node, roots);

        } else if (is(node, StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT)) {
            BaseTable table = find(BaseTable.class, node, schema, roots);
            for (AstNode node1 : node) {
                if (is(node1, StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION)) 
                    createKey(node1, table, roots);
                else if (is(node1, StandardDdlLexicon.TYPE_ADD_COLUMN_DEFINITION))
                    createColumn(node1, table);
            }
        }
    }

    @Override
    public void importNode(DdlImporterModel importerModel, AstNode rootNode) throws Exception {
        // FIXME not sure I like this at the moment
        setImporterModel(importerModel);

        List<EObject> roots = importerModel.getEndingSelector().getRootObjects();

        for (AstNode node : rootNode) {
            if (is(node, StandardDdlLexicon.TYPE_CREATE_SCHEMA_STATEMENT)) {
                Schema schema = importerModel.getFactory().createSchema();
                roots.add(schema);
                initialize(schema, node);
                for (AstNode node1 : node) {
                    create(node1, roots, schema);
                }
            } else create(node, roots, null);
        }
    }
    
    @Override
    public void importFinalize() throws Exception {
        // If user chose to use DDL as description, now set those descriptions (model type and container *must* be already set)
        if (getImporterModel().optToSetModelEntityDescription()) {
            for (EntityDescriptions pair : getImporterModel().getEntityDescriptions()) {
                ModelerCore.getModelEditor().setDescription(pair.getEntity(), pair.getPreferredDescription());
            }
        }
    }
}
