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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.modeshape.graph.JcrLexicon;
import org.modeshape.graph.property.Name;
import org.modeshape.graph.property.Property;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParsers;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
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
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.View;
import com.metamatrix.metamodels.relational.util.RelationalTypeMappingImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * 
 */
public class DdlImporter {

    private final IProject[] projects;

    private IContainer modelFolder;
    private String ddlFileName;
    private IFile modelFile;
    private ModelType modelType;

    private transient String modelName;
    private transient ModelResource model;

    /**
     * @param projects
     */
    public DdlImporter( final IProject[] projects ) {
        this.projects = projects;
    }

    private <T extends RelationalEntity> T create( final T entity,
                                                   final AstNode node ) {
        return create(entity, node.getName().getLocalName());
    }

    private <T extends RelationalEntity> T create( final T entity,
                                                   final String name ) {
        entity.setName(name);
        entity.setNameInSource(name);
        return entity;
    }

    private void createBaseTable( final AstNode node,
                                  final List<EObject> parentContainer,
                                  final List<String> messages ) throws Exception {
        // Create table
        final BaseTable table = createTable(RelationalFactory.eINSTANCE.createBaseTable(), node, messages);
        if (table == null) return;
        if (table.getSchema() == null) parentContainer.add(table);
        // Create columns
        for (final AstNode node1 : node) {
            final Name mixinType = mixinType(node1);
            if (StandardDdlLexicon.TYPE_COLUMN_DEFINITION.equals(mixinType)) {
                final Column col = create(RelationalFactory.eINSTANCE.createColumn(), node1);
                table.getColumns().add(col);
                final String datatype = node1.getProperty(StandardDdlLexicon.DATATYPE_NAME).getFirstValue().toString();
                col.setNativeType(datatype);
                col.setType(RelationalTypeMappingImpl.getInstance().getDatatype(datatype));
                Property prop = node1.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
                if (prop != null) col.setLength(((Number)prop.getFirstValue()).intValue());
                prop = node1.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
                if (prop != null) col.setPrecision((Integer)prop.getFirstValue());
                prop = node1.getProperty(StandardDdlLexicon.DATATYPE_SCALE);
                if (prop != null) col.setScale((Integer)prop.getFirstValue());
                prop = node1.getProperty(StandardDdlLexicon.NULLABLE);
                if (prop != null) col.setNullable(prop.getFirstValue().toString().equals("NULL") ? NullableType.NULLABLE_LITERAL : NullableType.NO_NULLS_LITERAL); //$NON-NLS-1$
                prop = node1.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
                if (prop != null) col.setDefaultValue(prop.getFirstValue().toString());
            } else if (StandardDdlLexicon.TYPE_TABLE_CONSTRAINT.equals(mixinType)) createKey(node1, table, messages);
        }
    }

    private void createKey( final AstNode node,
                            final BaseTable table,
                            final List<String> messages ) throws Exception {
        final String type = node.getProperty(StandardDdlLexicon.CONSTRAINT_TYPE).getFirstValue().toString();
        if (DdlConstants.PRIMARY_KEY.equals(type)) {
            final PrimaryKey key = create(RelationalFactory.eINSTANCE.createPrimaryKey(), node);
            table.setPrimaryKey(key);
            for (final AstNode node1 : node) {
                final Name mixinType = mixinType(node1);
                if (StandardDdlLexicon.TYPE_COLUMN_REFERENCE.equals(mixinType)) {
                    final Column col = find(Column.class, node1, table, messages);
                    if (col != null) key.getColumns().add(col);
                }
            }
        } else if (DdlConstants.FOREIGN_KEY.equals(type)) {
            final ForeignKey key = create(RelationalFactory.eINSTANCE.createForeignKey(), node);
            table.getForeignKeys().add(key);
            BaseTable foreignTable = null;
            final Set<Column> foreignColumns = new HashSet<Column>();
            for (final AstNode node1 : node) {
                final Name mixinType = mixinType(node1);
                if (StandardDdlLexicon.TYPE_COLUMN_REFERENCE.equals(mixinType)) {
                    // Create column reference
                    final Column col = find(Column.class, node1, table, messages);
                    if (col != null) key.getColumns().add(col);
                } else if (StandardDdlLexicon.TYPE_TABLE_REFERENCE.equals(mixinType)) foreignTable = find(BaseTable.class,
                                                                                                          node1,
                                                                                                          messages);
                else if (foreignTable != null && StandardDdlLexicon.TYPE_FK_COLUMN_REFERENCE.equals(mixinType)) {
                    final Column col = find(Column.class, node1, foreignTable, messages);
                    if (col != null) foreignColumns.add(col);
                }
            }
            if (foreignTable != null) {
                final PrimaryKey primaryKey = foreignTable.getPrimaryKey();
                final List<Column> primaryKeyColumns = primaryKey.getColumns();
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
            final UniqueConstraint key = create(RelationalFactory.eINSTANCE.createUniqueConstraint(), node);
            table.getUniqueConstraints().add(key);
            for (final AstNode node1 : node) {
                final Name mixinType = mixinType(node1);
                if (StandardDdlLexicon.TYPE_COLUMN_REFERENCE.equals(mixinType)) {
                    final Column col = find(Column.class, node1, table, messages);
                    if (col != null) key.getColumns().add(col);
                }
            }
        }
    }

    private <T extends Table> T createTable( final T entity,
                                             final AstNode node,
                                             final List<String> messages ) throws Exception {
        final String name = node.getName().getLocalName();
        final int ndx = name.indexOf('.');
        if (ndx < 0) return create(entity, name);
        final Schema schema = find(Schema.class, name.substring(0, ndx), node, messages);
        if (schema == null) return null;
        final T table = create(entity, name.substring(ndx + 1));
        schema.getTables().add(table);
        return table;
    }

    private void createView( final AstNode node,
                             final List<EObject> parentContainer,
                             final List<String> messages ) throws Exception {
        // Create table
        final View view = createTable(RelationalFactory.eINSTANCE.createView(), node, messages);
        if (view == null) return;
        if (view.getSchema() == null) parentContainer.add(view);
    }

    /**
     * @return ddlFileName
     */
    public String ddlFileName() {
        return ddlFileName;
    }

    private <T extends RelationalEntity> T find( final Class<T> type,
                                                 final AstNode node,
                                                 final List<String> messages ) throws Exception {
        return find(type, node, null, messages);
    }

    private <T extends RelationalEntity> T find( final Class<T> type,
                                                 final AstNode node,
                                                 final RelationalEntity parent,
                                                 final List<String> messages ) throws Exception {
        return find(type, node.getName().getLocalName(), node, parent, messages);
    }

    private <T extends RelationalEntity> T find( final Class<T> type,
                                                 final String name,
                                                 final AstNode node,
                                                 final List<String> messages ) throws Exception {
        return find(type, name, node, null, messages);
    }

    private <T extends RelationalEntity> T find( final Class<T> type,
                                                 final String name,
                                                 AstNode node,
                                                 final RelationalEntity parent,
                                                 final List<String> messages ) throws Exception {
        for (final EObject obj : parent == null ? model.getEmfResource().getContents() : parent.eContents()) {
            if (obj instanceof RelationalEntity) {
                final RelationalEntity entity = (RelationalEntity)obj;
                if (entity.getName().equals(name)) {
                    if (!type.isInstance(obj)) break;
                    return type.cast(entity);
                }
            }
        }
        Property lineProp = node.getProperty(StandardDdlLexicon.DDL_START_LINE_NUMBER);
        while (lineProp == null) {
            node = node.getParent();
            lineProp = node.getProperty(StandardDdlLexicon.DDL_START_LINE_NUMBER);
        }
        String parentType = null;
        if (parent == null) parentType = DdlImporterI18n.MODEL;
        else for (final Class parentInterface : parent.getClass().getInterfaces()) {
            if (RelationalEntity.class.isAssignableFrom(parentInterface)) parentType = parentInterface.getSimpleName();
        }
        messages.add(I18n.format(DdlImporterI18n.ENTITY_NOT_FOUND_MSG,
                                 type.getSimpleName(),
                                 name,
                                 parentType,
                                 parent == null ? modelName : parent.getName(),
                                 lineProp.getFirstValue(),
                                 node.getProperty(StandardDdlLexicon.DDL_START_COLUMN_NUMBER).getFirstValue()));
        return null;
    }

    /**
     * @return modelType
     */
    public ModelType getModelType() {
        return modelType;
    }

    void importDdl( final FileReader reader,
                    final List<String> messages,
                    final IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(DdlImporterI18n.IMPORTING_DDL_MSG, 3);
        monitor.subTask(DdlImporterI18n.PARSING_DDL_MSG);
        final char[] buf = new char[FileUtils.DEFAULT_BUFFER_SIZE];
        final StringBuilder builder = new StringBuilder();
        for (int charTot = reader.read(buf); charTot >= 0; charTot = reader.read(buf))
            builder.append(buf, 0, charTot);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        final DdlParsers parsers = new DdlParsers();
        final AstNode rootNode = parsers.parse(builder.toString(), ddlFileName);
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(1);
        monitor.subTask(DdlImporterI18n.CREATING_MODEL_MSG);
        // TODO: Don't remove model if update
        if (model != null) model.getEmfResource().delete(null);
        model = ModelerCore.create(model());
        final ModelAnnotation modelAnnotation = model.getModelAnnotation();
        modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
        modelAnnotation.setModelType(modelType);
        final List<EObject> modelContents = model.getEmfResource().getContents();
        for (final AstNode node : rootNode) {
            Name mixinType = mixinType(node);
            if (StandardDdlLexicon.TYPE_CREATE_SCHEMA_STATEMENT.equals(mixinType)) {
                final Schema schema = create(RelationalFactory.eINSTANCE.createSchema(), node);
                for (final AstNode node1 : node) {
                    mixinType = mixinType(node1);
                    if (StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT.equals(mixinType)) createBaseTable(node1,
                                                                                                          schema.getTables(),
                                                                                                          messages);
                    else if (StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT.equals(mixinType)) createView(node1,
                                                                                                         schema.getTables(),
                                                                                                         messages);
                    else if (StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT.equals(mixinType)) {
                        final BaseTable table = find(BaseTable.class, node1, schema, messages);
                        if (table == null) continue;
                        for (final AstNode node2 : node1) {
                            mixinType = mixinType(node2);
                            if (StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION.equals(mixinType)) createKey(node2,
                                                                                                                     table,
                                                                                                                     messages);
                        }
                    }
                }
            } else if (StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT.equals(mixinType)) createBaseTable(node,
                                                                                                         modelContents,
                                                                                                         messages);
            else if (StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT.equals(mixinType)) createView(node, modelContents, messages);
            // else if (StandardDdlLexicon..equals(mixinType)) createView(node, modelContents, messages);
            else if (StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT.equals(mixinType)) {
                final BaseTable table = find(BaseTable.class, node, messages);
                if (table == null) continue;
                for (final AstNode node1 : node) {
                    mixinType = mixinType(node1);
                    if (StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION.equals(mixinType)) createKey(node1, table, messages);
                }
            }
        }
        ModelBuildUtil.rebuildImports(model.getEmfResource(), false);
        if (monitor.isCanceled()) throw new OperationCanceledException();
        monitor.worked(1);
        if (!messages.isEmpty()) return;
        saveInternal(monitor);
    }

    /**
     * @param messages
     * @param monitor
     */
    public void importDdl( final List<String> messages,
                           final IProgressMonitor monitor ) {
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
                importDdl(reader, messages, monitor);
            }
        });
    }

    private Name mixinType( final AstNode node ) {
        final Property prop = node.getProperty(JcrLexicon.MIXIN_TYPES);
        for (final Object obj : prop) {
            final Name mixinType = (Name)obj;
            if (StandardDdlLexicon.Namespace.URI.equals(mixinType.getNamespaceUri())) return mixinType;
        }
        return null; // Not possible
    }

    /**
     * @return model
     */
    public IFile model() {
        return modelFile;
    }

    /**
     * @return modelFolder
     */
    public IContainer modelFolder() {
        return modelFolder;
    }

    /**
     * @param monitor
     */
    public void save( final IProgressMonitor monitor ) {
        monitor.beginTask(DdlImporterI18n.IMPORTING_DDL_MSG, 1);
        saveInternal(monitor);
    }

    /**
     * @param monitor
     */
    void saveInternal( final IProgressMonitor monitor ) {
        monitor.subTask(DdlImporterI18n.SAVING_MODEL_MSG);
        try {
            model.save(monitor, false);
        } catch (final Exception error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
        monitor.worked(1);
        monitor.done();
    }

    /**
     * @param ddlFileName
     */
    public void setDdlFileName( String ddlFileName ) {
        this.ddlFileName = null;
        if (ddlFileName == null) throw new EmptyArgumentException("ddlFileName"); //$NON-NLS-1$
        ddlFileName = ddlFileName.trim();
        if (ddlFileName.isEmpty()) throw new EmptyArgumentException("ddlFileName"); //$NON-NLS-1$
        final File file = new File(ddlFileName);
        if (!file.exists() || file.isDirectory()) throw new IllegalArgumentException(DdlImporterI18n.DDL_FILE_NOT_FOUND_MSG);
        this.ddlFileName = ddlFileName;
    }

    /**
     * @param modelName
     */
    public void setModel( String modelName ) {
        modelFile = null;
        this.modelName = null;
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
                // Verify name is not a model file
                if (resource != null && !ModelUtil.isModelFile(modelPath)) throw new IllegalArgumentException(
                                                                                                              DdlImporterI18n.MODEL_NAME_IS_NON_MODEL_FILE_MSG);
                if (resource == null) modelFile = root.getFile(modelPath);
                else modelFile = (IFile)resource;
            } else modelFile = root.getFile(modelPath);
            this.modelName = modelFile.getFullPath().removeFileExtension().lastSegment();
        }
    }

    /**
     * @param modelFolder
     */
    public void setModelFolder( final IContainer modelFolder ) {
        this.modelFolder = modelFolder;
    }

    /**
     * @param modelFolderName
     */
    public void setModelFolder( String modelFolderName ) {
        modelFolder = null;
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
     * @param modelType Sets modelType to the specified value.
     */
    public void setModelType( final ModelType modelType ) {
        this.modelType = modelType;
    }
}
