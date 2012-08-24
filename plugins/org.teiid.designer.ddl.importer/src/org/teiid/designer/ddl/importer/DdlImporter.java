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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.graph.JcrLexicon;
import org.modeshape.graph.property.Name;
import org.modeshape.graph.property.Property;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParsers;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.derby.DerbyDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.mysql.MySqlDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.core.I18n;
import org.teiid.core.exception.EmptyArgumentException;
import org.teiid.core.util.FileUtils;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.modeler.util.OperationUtil;
import com.metamatrix.core.modeler.util.OperationUtil.Unreliable;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.util.RelationalTypeMappingImpl;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.MergeProcessor;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.compare.processor.DifferenceProcessorImpl;
import com.metamatrix.modeler.compare.selector.ModelResourceSelector;
import com.metamatrix.modeler.compare.selector.TransientModelSelector;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * 
 */
public class DdlImporter {

    private static final RelationalFactory FACTORY = RelationalFactory.eINSTANCE;

    private final IProject[] projects;

    private IContainer modelFolder;
    private String ddlFileName;
    private String modelName;
    private IFile modelFile;
    private ModelType modelType;
    private DifferenceProcessorImpl chgProcessor;
    private ModelResource model;
    private boolean optToCreateModelEntitiesForUnsupportedDdl;
    private boolean optToSetModelEntityDescription;
    
    // hold on to DDL so that it can be set in the description
    private Map<RelationalEntity, String> descriptionMap = new HashMap<RelationalEntity, String>();

    /**
     * @param projects
     */
    public DdlImporter( final IProject[] projects ) {
        this.projects = projects;
    }

    private void create( final AstNode node,
                         Name mixinType,
                         final List<EObject> roots,
                         final Schema schema,
                         final List<String> messages ) throws CoreException {
        try {
            if (StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT.equals(mixinType)) {
                final BaseTable table = initializeTable(FACTORY.createBaseTable(), node, roots);
                for (final AstNode node1 : node) {
                    mixinType = mixinType(node1);
                    if (StandardDdlLexicon.TYPE_COLUMN_DEFINITION.equals(mixinType)) createColumn(node1, table);
                    else if (StandardDdlLexicon.TYPE_TABLE_CONSTRAINT.equals(mixinType)) createKey(node1, table, roots, messages);
                }
            } else if (StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT.equals(mixinType)) {
                if (modelType != ModelType.VIRTUAL_LITERAL && optToCreateModelEntitiesForUnsupportedDdl) initializeTable(FACTORY.createView(),
                                                                                                                         node,
                                                                                                                         roots);
            } else if (OracleDdlLexicon.TYPE_CREATE_INDEX_STATEMENT.equals(mixinType)
                       || DerbyDdlLexicon.TYPE_CREATE_INDEX_STATEMENT.equals(mixinType)
                       || MySqlDdlLexicon.TYPE_CREATE_INDEX_STATEMENT.equals(mixinType)
                       || PostgresDdlLexicon.TYPE_CREATE_INDEX_STATEMENT.equals(mixinType)) {
                final Index index = FACTORY.createIndex();
                final Info<Index> info = new Info<Index>(index, node, roots);
                if (info.schema == null) roots.add(index);
                else info.schema.getIndexes().add(index);
                initialize(index, node, info.name);
                Property prop = node.getProperty(OracleDdlLexicon.UNIQUE_INDEX);
                if (prop == null) prop = node.getProperty(DerbyDdlLexicon.UNIQUE_INDEX);
                if (prop != null) index.setUnique((Boolean)prop.getFirstValue());
                prop = node.getProperty(OracleDdlLexicon.TABLE_NAME);
                if (prop == null) prop = node.getProperty(DerbyDdlLexicon.TABLE_NAME);
                if (prop != null) {
                    try {
                        final Table table = find(Table.class, prop.getFirstValue().toString(), node, null, roots);
                        for (final AstNode node1 : node) {
                            // Probably need to check for a simple column reference for Oracle
                            if (DerbyDdlLexicon.TYPE_INDEX_COLUMN_REFERENCE.equals(mixinType(node1))) try {
                                index.getColumns().add(find(Column.class, node1, table, roots));
                            } catch (final EntityNotFoundException error) {
                                messages.add(error.getMessage());
                            }
                        }
                    } catch (final EntityNotFoundException error) {
                        messages.add(error.getMessage());
                    }
                }
            } else if (OracleDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT.equals(mixinType)
                       || DerbyDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT.equals(mixinType)
                       || MySqlDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT.equals(mixinType)) try {
                createProcedure(node, roots);
            } catch (final EntityNotFoundException error) {
                messages.add(error.getMessage());
            }
            else if (OracleDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT.equals(mixinType)
                     || DerbyDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT.equals(mixinType)
                     || MySqlDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT.equals(mixinType)
                     || PostgresDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT.equals(mixinType)) try {
                createProcedure(node, roots).setFunction(true);
            } catch (final EntityNotFoundException error) {
                messages.add(error.getMessage());
            }
            else if (StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT.equals(mixinType)) {
                final BaseTable table = find(BaseTable.class, node, schema, roots);
                for (final AstNode node1 : node) {
                    mixinType = mixinType(node1);
                    if (StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION.equals(mixinType)) createKey(node1,
                                                                                                             table,
                                                                                                             roots,
                                                                                                             messages);
                    else if (StandardDdlLexicon.TYPE_ADD_COLUMN_DEFINITION.equals(mixinType)) createColumn(node1, table);
                }
            }
        } catch (final EntityNotFoundException error) {
            messages.add(error.getMessage());
        }
    }

    private void createColumn( final AstNode node,
                               final BaseTable table ) throws CoreException {
        final Column col = FACTORY.createColumn();
        table.getColumns().add(col);
        initialize(col, node);
        final String datatype = node.getProperty(StandardDdlLexicon.DATATYPE_NAME).getFirstValue().toString();
        col.setNativeType(datatype);
        col.setType(RelationalTypeMappingImpl.getInstance().getDatatype(datatype));
        Property prop = node.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
        if (prop != null) col.setLength(((Number)prop.getFirstValue()).intValue());
        prop = node.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
        if (prop != null) col.setPrecision((Integer)prop.getFirstValue());
        prop = node.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
        if (prop != null) col.setScale((Integer)prop.getFirstValue());
        prop = node.getProperty(StandardDdlLexicon.NULLABLE);
        if (prop != null) col.setNullable(prop.getFirstValue().toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$
        prop = node.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
        if (prop != null) col.setDefaultValue(prop.getFirstValue().toString());
    }

    private void createKey( final AstNode node,
                            final BaseTable table,
                            final List<EObject> roots,
                            final List<String> messages ) throws CoreException {
        final String type = node.getProperty(StandardDdlLexicon.CONSTRAINT_TYPE).getFirstValue().toString();
        if (DdlConstants.PRIMARY_KEY.equals(type)) {
            final PrimaryKey key = FACTORY.createPrimaryKey();
            table.setPrimaryKey(key);
            initialize(key, node);
            for (final AstNode node1 : node) {
                if (StandardDdlLexicon.TYPE_COLUMN_REFERENCE.equals(mixinType(node1))) try {
                    key.getColumns().add(find(Column.class, node1, table, roots));
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
                final Name mixinType = mixinType(node1);
                if (StandardDdlLexicon.TYPE_COLUMN_REFERENCE.equals(mixinType)) try {
                    key.getColumns().add(find(Column.class, node1, table, roots));
                } catch (final EntityNotFoundException error) {
                    messages.add(error.getMessage());
                }
                else if (StandardDdlLexicon.TYPE_TABLE_REFERENCE.equals(mixinType)) try {
                    foreignTable = find(BaseTable.class, node1, null, roots);
                } catch (final EntityNotFoundException error) {
                    messages.add(error.getMessage());
                }
                else if (StandardDdlLexicon.TYPE_FK_COLUMN_REFERENCE.equals(mixinType)) {
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
                if (StandardDdlLexicon.TYPE_COLUMN_REFERENCE.equals(mixinType(node1))) try {
                    key.getColumns().add(find(Column.class, node1, table, roots));
                } catch (final EntityNotFoundException error) {
                    messages.add(error.getMessage());
                }
            }
        }
    }

    private Procedure createProcedure( final AstNode node,
                                       final List<EObject> roots ) throws EntityNotFoundException, CoreException {
        final Procedure procedure = FACTORY.createProcedure();
        final Info<Procedure> info = new Info<Procedure>(procedure, node, roots);
        if (info.schema == null) roots.add(procedure);
        else info.schema.getProcedures().add(procedure);
        initialize(procedure, node, info.name);
        if (node.getProperty(StandardDdlLexicon.DATATYPE_NAME) != null) {
            final ProcedureResult result = FACTORY.createProcedureResult();
            procedure.setResult(result);
            initialize(result, node);
        }
        for (final AstNode node1 : node) {
            final Name mixinType = mixinType(node1);
            if (OracleDdlLexicon.TYPE_FUNCTION_PARAMETER.equals(mixinType)
                || DerbyDdlLexicon.TYPE_FUNCTION_PARAMETER.equals(mixinType)) {
                final ProcedureParameter prm = FACTORY.createProcedureParameter();
                procedure.getParameters().add(prm);
                initialize(prm, node1);
                final String datatype = node1.getProperty(StandardDdlLexicon.DATATYPE_NAME).getFirstValue().toString();
                prm.setNativeType(datatype);
                prm.setType(RelationalTypeMappingImpl.getInstance().getDatatype(datatype));
                Property prop = node1.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
                if (prop != null) prm.setLength(((Number)prop.getFirstValue()).intValue());
                prop = node1.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
                if (prop != null) prm.setPrecision((Integer)prop.getFirstValue());
                prop = node1.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
                if (prop != null) prm.setScale((Integer)prop.getFirstValue());
                prop = node1.getProperty(StandardDdlLexicon.NULLABLE);
                if (prop != null) prm.setNullable(prop.getFirstValue().toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$
                prop = node1.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
                if (prop != null) prm.setDefaultValue(prop.getFirstValue().toString());
                prop = node1.getProperty(OracleDdlLexicon.IN_OUT_NO_COPY);
                if (prop != null) {
                    final String direction = prop.getFirstValue().toString();
                    if ("IN".equals(direction)) prm.setDirection(DirectionKind.IN_LITERAL); //$NON-NLS-1$
                    else if ("OUT".equals(direction) || "OUT NOCOPY".equals(direction)) prm.setDirection(DirectionKind.OUT_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
                    else if ("IN OUT".equals(direction) || "IN OUT NOCOPY".equals(direction)) prm.setDirection(DirectionKind.INOUT_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
                }
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
        return find(type, node.getName().getLocalName(), node, parent, roots);
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
                                                      node.getProperty(StandardDdlLexicon.DDL_START_LINE_NUMBER).getFirstValue(),
                                                      node.getProperty(StandardDdlLexicon.DDL_START_COLUMN_NUMBER).getFirstValue()));
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

    void importDdl( final FileReader reader,
                    final List<String> messages,
                    final IProgressMonitor monitor,
                    final int totalWork ) throws IOException, CoreException {
        final int workUnit = totalWork / 3;
        monitor.subTask(DdlImporterI18n.PARSING_DDL_MSG);
        final char[] buf = new char[FileUtils.DEFAULT_BUFFER_SIZE];
        final StringBuilder builder = new StringBuilder();
        for (int charTot = reader.read(buf); charTot >= 0; charTot = reader.read(buf))
            builder.append(buf, 0, charTot);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        final DdlParsers parsers = new DdlParsers();
        final AstNode rootNode = parsers.parse(builder.toString(), ddlFileName);
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(workUnit);
        monitor.subTask(DdlImporterI18n.CREATING_MODEL_MSG);
        // TODO: cleanup old model in case import called multiple times
        model = ModelerCore.create(modelFile);
        final TransientModelSelector endSelector = new TransientModelSelector(model.getEmfResource().getURI());
        final DifferenceProcessorImpl processor = new DifferenceProcessorImpl(new ModelResourceSelector(model), endSelector);
        processor.addEObjectMatcherFactories(ModelerComparePlugin.createEObjectMatcherFactories());
        final List<EObject> roots = endSelector.getRootObjects();
        for (final AstNode node : rootNode) {
            final Name mixinType = mixinType(node);
            if (StandardDdlLexicon.TYPE_CREATE_SCHEMA_STATEMENT.equals(mixinType)) {
                final Schema schema = FACTORY.createSchema();
                roots.add(schema);
                initialize(schema, node);
                for (final AstNode node1 : node) {
                    create(node1, mixinType(node1), roots, schema, messages);
                }
            } else create(node, mixinType, roots, null, messages);
        }
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(workUnit);
        monitor.subTask(DdlImporterI18n.CREATING_CHANGE_REPORT_MSG);
        handleStatus(processor.execute(monitor));
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(workUnit);
        chgProcessor = processor;
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

    private void initialize( final RelationalEntity entity,
                             final AstNode node ) {
        initialize(entity, node, node.getName().getLocalName());
    }

    private void initializeFK( final List<ForeignKey> currentFKs,
                               final ForeignKey key,
                               final AstNode node ) {
        // Get Name from DDL node
        String fkName = node.getName().getLocalName();
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
            final Property prop = node.getProperty(StandardDdlLexicon.DDL_EXPRESSION);
            if (prop != null) {
                this.descriptionMap.put(entity, prop.getFirstValue().toString());
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

    private Name mixinType( final AstNode node ) {
        final Property prop = node.getProperty(JcrLexicon.MIXIN_TYPES);
        for (final Object obj : prop) {
            final Name mixinType = (Name)obj;
            final String uri = mixinType.getNamespaceUri();
            if (StandardDdlLexicon.Namespace.URI.equals(uri) || OracleDdlLexicon.Namespace.URI.equals(uri)
                || DerbyDdlLexicon.Namespace.URI.equals(uri) || MySqlDdlLexicon.Namespace.URI.equals(uri)
                || PostgresDdlLexicon.Namespace.URI.equals(uri)) return mixinType;
        }
        return null; // Not possible
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

            // now set descriptions (model type and container *must* be already set)
            if (optToSetModelEntityDescription) {
                for (Map.Entry<RelationalEntity, String> entry : this.descriptionMap.entrySet()) {
                    ModelerCore.getModelEditor().setDescription(entry.getKey(), entry.getValue());
                }

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
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
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
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
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
            final String name = node.getName().getLocalName();
            final int ndx = name.indexOf('.');
            if (ndx < 0) {
                schema = null;
                this.name = name;
            } else {
                schema = find(Schema.class, name.substring(0, ndx), node, null, roots);
                this.name = name.substring(ndx + 1);
            }
        }
    }
}
