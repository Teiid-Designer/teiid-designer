/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ddl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.ConcurrentModelVisitorProcessor;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.ddl.DdlOptions;
import com.metamatrix.modeler.ddl.DdlPlugin;

/**
 * IntermediateFormat
 */
public class IntermediateFormat {

    public static final String FALSE_VALUE = DdlPlugin.Util.getString("IntermediateFormat.falseValue"); //$NON-NLS-1$
    public static final String TRUE_VALUE = DdlPlugin.Util.getString("IntermediateFormat.trueValue"); //$NON-NLS-1$

    public static final String DATE_FORMAT = DdlPlugin.Util.getString("IntermediateFormat.DateFormat"); //$NON-NLS-1$
    public static final String TIME_FORMAT = DdlPlugin.Util.getString("IntermediateFormat.TimeFormat"); //$NON-NLS-1$

    private static final DateFormat DATE_FORMATTER;
    private static final DateFormat TIME_FORMATTER;

    static {
        TIME_FORMATTER = new SimpleDateFormat(TIME_FORMAT);
        TIME_FORMATTER.setLenient(false);
        DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
        DATE_FORMATTER.setLenient(false);
    }

    public static class ModelTypes {
        public static final String VIRTUAL = "Virtual"; //$NON-NLS-1$
        public static final String PHYSICAL = "Physical"; //$NON-NLS-1$
        public static final String UNKNOWN = "Unknown"; //$NON-NLS-1$
    }

    public static class Xml {
        public static class Comment {
            public static final String TAG = "comment"; //$NON-NLS-1$
        }

        public static class DDL {
            public static final String TAG = "ddl"; //$NON-NLS-1$
        }

        public static class Model {
            public static final String TAG = "model"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String GENERATE_COMMENTS = "generateComments"; //$NON-NLS-1$
                public static final String GENERATE_DROPS = "generateDrops"; //$NON-NLS-1$
                public static final String EXPORT_TOOL = "exportTool"; //$NON-NLS-1$
                public static final String EXPORT_VERSION = "exportToolVersion"; //$NON-NLS-1$
                public static final String EXPORT_DATE = "exportDate"; //$NON-NLS-1$
                public static final String EXPORT_TIME = "exportTime"; //$NON-NLS-1$
                public static final String MODEL_FILENAME = "modelFilename"; //$NON-NLS-1$
                public static final String MODEL_TYPE = "modelType"; //$NON-NLS-1$
                public static final String METAMODEL = "metaModel"; //$NON-NLS-1$
                public static final String METAMODEL_URL = "metaModelURL"; //$NON-NLS-1$
            }
        }

        public static class Schema {
            public static final String TAG = "schema"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String PATH_IN_MODEL = "pathInModel"; //$NON-NLS-1$
            }
        }

        public static class Table {
            public static final String TAG = "table"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String DESCRIPTION = "description"; //$NON-NLS-1$
                public static final String PATH_IN_MODEL = "pathInModel"; //$NON-NLS-1$
            }
        }

        public static class Column {
            public static final String TAG = "column"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String ALIAS = "alias"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String DESCRIPTION = "description"; //$NON-NLS-1$
                public static final String PATH_IN_MODEL = "pathInModel"; //$NON-NLS-1$
                public static final String TYPE = "type"; //$NON-NLS-1$
                public static final String DEFAULT_VALUE = "initialValue"; //$NON-NLS-1$
                public static final String LENGTH = "length"; //$NON-NLS-1$
                public static final String PRECISION = "precision"; //$NON-NLS-1$
                public static final String SCALE = "scale"; //$NON-NLS-1$
                public static final String IS_FIXED_LENGTH = "isLengthFixed"; //$NON-NLS-1$
                public static final String IS_NULLABLE = "isNullable"; //$NON-NLS-1$
                public static final String SUPPORTS_SELECT = "supportsSelect"; //$NON-NLS-1$
                public static final String SUPPORTS_UPDATE = "supportsUpdate"; //$NON-NLS-1$
                public static final String IS_CASE_SENSITIVE = "isCaseSensitive"; //$NON-NLS-1$
                public static final String IS_SIGNED = "isSigned"; //$NON-NLS-1$
                public static final String IS_CURRENCY = "isCurrency"; //$NON-NLS-1$
                public static final String IS_AUTOINCREMENTED = "isAutoIncremented"; //$NON-NLS-1$
                public static final String SEARCH_TYPE = "searchType"; //$NON-NLS-1$
            }
        }

        public static class PrimaryKey {
            public static final String TAG = "primaryKey"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String TABLE_NAME = "tableName"; //$NON-NLS-1$
                public static final String PATH_IN_MODEL = "pathInModel"; //$NON-NLS-1$
            }
        }

        public static class PrimaryKeyColumn {
            public static final String TAG = "column"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
            }
        }

        public static class UniqueKey {
            public static final String TAG = "uniqueKey"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String TABLE_NAME = "tableName"; //$NON-NLS-1$
                public static final String PATH_IN_MODEL = "pathInModel"; //$NON-NLS-1$
            }
        }

        public static class UniqueKeyColumn {
            public static final String TAG = "column"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
            }
        }

        public static class ForeignKey {
            public static final String TAG = "foreignKey"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String TABLE_NAME = "tableName"; //$NON-NLS-1$
                public static final String PK_TABLE_NAME = "pkTableName"; //$NON-NLS-1$
                public static final String PATH_IN_MODEL = "pathInModel"; //$NON-NLS-1$
            }
        }

        public static class ForignKeyColumn {
            public static final String TAG = "column"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String PK_COLUMN_NAME = "pkColumnName"; //$NON-NLS-1$
            }
        }

        public static class Index {
            public static final String TAG = "index"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
                public static final String TABLE_NAME = "tableName"; //$NON-NLS-1$
                public static final String PATH_IN_MODEL = "pathInModel"; //$NON-NLS-1$
            }
        }

        public static class IndexColumn {
            public static final String TAG = "column"; //$NON-NLS-1$

            public static class Attributes {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String UUID = "uuid"; //$NON-NLS-1$
            }
        }

        public static class IndexColumns {
            public static final String TAG = "indexColumn"; //$NON-NLS-1$
        }

    }

    public class RelationalEntityUniquenessEnforcer implements ModelVisitor {

        private final Set primaryKeys;
        private final Set foreignKeys;
        private final Set uniqueKeys;
        private final Set indexes;

        /**
         * Construct an instance of RelationalObjectFinder.
         */
        public RelationalEntityUniquenessEnforcer() {
            super();
            this.primaryKeys = new HashSet();
            this.foreignKeys = new HashSet();
            this.uniqueKeys = new HashSet();
            this.indexes = new HashSet();
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
         */
        public boolean visit( final EObject object ) {
            if (object instanceof Catalog || object instanceof Schema || object instanceof Table) {
                return true;
            } else if (object instanceof PrimaryKey) {
                found((PrimaryKey)object, this.primaryKeys);
            } else if (object instanceof ForeignKey) {
                found((ForeignKey)object, this.foreignKeys);
            } else if (object instanceof UniqueKey) {
                found((UniqueKey)object, this.uniqueKeys);
            } else if (object instanceof Index) {
                found((Index)object, this.indexes);
            }
            return false;
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
         */
        public boolean visit( final Resource resource ) {
            return true;
        }

        protected void found( final RelationalEntity entity,
                              final Set uniqueEntities ) {
            String name = IntermediateFormat.this.getObjectNameInDdl(entity);
            if (uniqueEntities.contains(name)) {
                boolean isNameUnique = false;
                int index = 1;
                while (!isNameUnique) {
                    String newName = name + String.valueOf(index);
                    if (!uniqueEntities.contains(newName)) {
                        name = newName;
                        break;
                    }
                    index++;
                }
            }
            uniqueEntities.add(name);
            IntermediateFormat.this.relationalEntities.put(entity, name);
        }
    }

    final Map relationalEntities;
    private final List modelWrappers;

    private final DdlOptions options;
    // private final IProgressMonitor monitor;
    private String exporterTool;
    private String exporterVersion;
    private final ModelEditor editor;

    /**
     * Construct an instance of IntermediateFormat.
     */
    public IntermediateFormat( final ModelWrapper modelWrapper,
                               final DdlOptions options,
                               final IProgressMonitor monitor ) {
        super();
        ArgCheck.isNotNull(modelWrapper);
        ArgCheck.isNotNull(options);
        this.modelWrappers = new ArrayList();
        this.modelWrappers.add(modelWrapper);
        this.options = options;
        // this.monitor = monitor != null ? monitor : new NullProgressMonitor();
        this.editor = ModelerCore.getModelEditor();
        this.relationalEntities = new HashMap();
    }

    /**
     * Construct an instance of IntermediateFormat.
     */
    public IntermediateFormat( final List modelWrappers,
                               final DdlOptions options,
                               final IProgressMonitor monitor ) {
        super();
        ArgCheck.isNotNull(modelWrappers);
        ArgCheck.isNotNull(options);
        this.modelWrappers = new ArrayList(modelWrappers);
        this.options = options;
        // this.monitor = monitor != null ? monitor : new NullProgressMonitor();
        this.editor = ModelerCore.getModelEditor();
        this.relationalEntities = new HashMap();
    }

    protected ModelEditor getModelEditor() {
        return this.editor;
    }

    /**
     * @return
     */
    public String getExporterTool() {
        return exporterTool;
    }

    /**
     * @return
     */
    public String getExporterVersion() {
        return exporterVersion;
    }

    /**
     * @param string
     */
    public void setExporterTool( String string ) {
        exporterTool = string;
    }

    /**
     * @param string
     */
    public void setExporterVersion( String string ) {
        exporterVersion = string;
    }

    // =========================================================================
    // Helper methods for setting attributes
    // =========================================================================

    protected void setAttribute( final Element element,
                                 final String attributeName,
                                 final String value ) {
        if (value != null && value.trim().length() != 0) {
            final Attribute attrib = new Attribute(attributeName, value);
            element.setAttribute(attrib);
        }
    }

    protected void setAttribute( final Element element,
                                 final String attributeName,
                                 final Object value ) {
        if (value != null) {
            final Attribute attrib = new Attribute(attributeName, value.toString());
            element.setAttribute(attrib);
        }
    }

    protected void setAttribute( final Element element,
                                 final String attributeName,
                                 final boolean value ) {
        final String strValue = value ? TRUE_VALUE : FALSE_VALUE;
        final Attribute attrib = new Attribute(attributeName, strValue);
        element.setAttribute(attrib);
    }

    protected void setAttribute( final Element element,
                                 final String attributeName,
                                 final int value ) {
        final String strValue = Integer.toString(value);
        final Attribute attrib = new Attribute(attributeName, strValue);
        element.setAttribute(attrib);
    }

    // =========================================================================
    // Methods to create document content from RelationalEntities
    // =========================================================================

    /**
     * @param emfResource
     * @param monitor
     * @return
     */
    public Document createDocument() {
        final Element ddlElement = new Element(Xml.DDL.TAG);
        final Document doc = new Document(ddlElement);

        final Date now = new Date(System.currentTimeMillis());

        // Set the model-level attributes ...
        setAttribute(ddlElement, Xml.Model.Attributes.GENERATE_COMMENTS, options.isGenerateComments());
        setAttribute(ddlElement, Xml.Model.Attributes.GENERATE_DROPS, options.isGenerateDropStatements());
        setAttribute(ddlElement, Xml.Model.Attributes.EXPORT_TOOL, exporterTool);
        setAttribute(ddlElement, Xml.Model.Attributes.EXPORT_VERSION, exporterVersion);
        setAttribute(ddlElement, Xml.Model.Attributes.EXPORT_DATE, DATE_FORMATTER.format(now));
        setAttribute(ddlElement, Xml.Model.Attributes.EXPORT_TIME, TIME_FORMATTER.format(now));

        // -------------------------------------------------------------------------
        // Walk the models that have at least some content to be written out ...
        // -------------------------------------------------------------------------

        // visit the model here
        final ModelVisitor visitor = new RelationalEntityUniquenessEnforcer();
        final ModelVisitorProcessor processor = new ConcurrentModelVisitorProcessor(visitor);

        final Iterator iter = modelWrappers.iterator();
        while (iter.hasNext()) {
            final ModelWrapper wrapper = (ModelWrapper)iter.next();
            try {
                processor.walk(wrapper.getEmfResource(), ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException e) {
                // handle the exception
            }
            create(ddlElement, wrapper); // this method calls other methods to walk the tree
        }

        return doc;
    }

    /**
     * Method to create document contents for a ModelWrapper object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the ModelWrapper that contains some objects to be written to the document content; may not be null
     */
    protected void create( final Element parent,
                           final ModelWrapper wrapper ) {
        final Resource emfResource = wrapper.getEmfResource();
        final ModelAnnotation modelAnnotation = wrapper.getContents().getModelAnnotation();

        final ModelType modelTypeEnum = modelAnnotation.getModelType();
        String modelType = null;
        switch (modelTypeEnum.getValue()) {
            case ModelType.PHYSICAL:
                modelType = ModelTypes.PHYSICAL;
                break;
            case ModelType.VIRTUAL:
                modelType = ModelTypes.VIRTUAL;
                break;
            case ModelType.UNKNOWN:
                modelType = ModelTypes.UNKNOWN;
                break;
            default:
                modelType = modelTypeEnum.getName();
        }
        final String primaryMetamodelUri = modelAnnotation.getPrimaryMetamodelUri();
        final MetamodelDescriptor mmDesc = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(primaryMetamodelUri);
        final String primaryMetamodel = mmDesc != null ? mmDesc.getName() : primaryMetamodelUri;

        // Set the model-level attributes ...
        final Element modelElement = new Element(Xml.Model.TAG);
        parent.addContent(modelElement);
        setAttribute(modelElement, Xml.Model.Attributes.NAME, wrapper.getModelName());
        setAttribute(modelElement, Xml.Model.Attributes.UUID, getModelEditor().getObjectID(modelAnnotation));
        setAttribute(modelElement, Xml.Model.Attributes.MODEL_FILENAME, wrapper.getModelFilename());
        if (modelType != null) {
            setAttribute(modelElement, Xml.Model.Attributes.MODEL_TYPE, modelType);
        }
        setAttribute(modelElement, Xml.Model.Attributes.METAMODEL, primaryMetamodel);
        setAttribute(modelElement, Xml.Model.Attributes.METAMODEL_URL, primaryMetamodelUri);

        // -------------------------------------------------------------------------
        // Walk the root-level objects and call the corresponding method ...
        // -------------------------------------------------------------------------

        final Iterator iter = emfResource.getContents().iterator();
        while (iter.hasNext()) {
            final EObject eObj = (EObject)iter.next();
            create(modelElement, eObj, wrapper); // this method calls other methods to walk the tree
        }

    }

    /**
     * Method to create document contents for a general EObject. This method does nothing, since there are overloaded forms of
     * this method that handle specific types of EObjects.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the EObject representing the graph of objects for which document content may be created
     */
    protected void create( final Element parent,
                           final EObject eObj,
                           final ModelWrapper wrapper ) {
        if (eObj instanceof Catalog) {
            create(parent, (Catalog)eObj, wrapper);
        } else if (eObj instanceof Schema) {
            create(parent, (Schema)eObj, wrapper);
        } else if (eObj instanceof Table) {
            create(parent, (Table)eObj, wrapper);
        } else if (eObj instanceof Index) {
            create(parent, (Index)eObj, wrapper);
        } else if (eObj instanceof Column) {
            create(parent, (Column)eObj, wrapper);
        } else if (eObj instanceof ForeignKey) {
            create(parent, (ForeignKey)eObj, wrapper);
        } else if (eObj instanceof PrimaryKey) {
            create(parent, (PrimaryKey)eObj, wrapper);
        } else if (eObj instanceof UniqueKey) {
            create(parent, (UniqueKey)eObj, wrapper);
        }
        // else, do nothing
    }

    /**
     * Method to create document contents for a Catalog object. This method does nothing for processing the Catalog object itself,
     * but
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the Catalog representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final Catalog catalog,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(catalog);

        // Skip this object if it is not to be exported ...
        if (!wrapper.isSelected(catalog)) {
            return;
        }

        // Walk the contents and create any elements for this object's children ...
        final Iterator iter = catalog.eContents().iterator();
        while (iter.hasNext()) {
            final EObject eobj = (EObject)iter.next();
            create(parent, eobj, wrapper);
        }
    }

    /**
     * Method to create document contents for a Schema object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the Schema representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final Schema schema,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(schema);

        // Skip this object if it is not to be exported ...
        if (!wrapper.isSelected(schema)) {
            return;
        }

        Element parentOfNewElements = parent;
        if (this.options.isGenerateSchema()) {
            final Element schemaElement = new Element(Xml.Schema.TAG);
            parent.addContent(schemaElement);

            setAttribute(schemaElement, Xml.Schema.Attributes.NAME, getObjectNameInDdl(schema));
            setAttribute(schemaElement, Xml.Schema.Attributes.UUID, editor.getObjectID(schema));
            setAttribute(schemaElement, Xml.Schema.Attributes.PATH_IN_MODEL, editor.getModelRelativePath(schema));

            parentOfNewElements = schemaElement;
        }

        // Walk the contents and create any elements for this object's children ...
        final Iterator iter = schema.eContents().iterator();
        while (iter.hasNext()) {
            final EObject eobj = (EObject)iter.next();
            create(parentOfNewElements, eobj, wrapper);
        }
    }

    /**
     * Method to create document contents for a Table object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the EObject representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final Table table,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(table);

        // Skip this object if it is not to be exported ...
        if (!wrapper.isSelected(table)) {
            return;
        }

        final Element tableElement = new Element(Xml.Table.TAG);
        parent.addContent(tableElement);

        setAttribute(tableElement, Xml.Table.Attributes.NAME, getObjectNameInDdl(table));
        setAttribute(tableElement, Xml.Table.Attributes.UUID, editor.getObjectID(table));
        setAttribute(tableElement, Xml.Table.Attributes.PATH_IN_MODEL, editor.getModelRelativePath(table));
        final Annotation annotation = wrapper.getContents().getAnnotation(table);
        if (annotation != null) {
            setAttribute(tableElement, Xml.Table.Attributes.DESCRIPTION, annotation.getDescription());
        }

        // Process the columns ...
        final Iterator iter = table.getColumns().iterator();
        while (iter.hasNext()) {
            final EObject eobj = (EObject)iter.next();
            create(tableElement, eobj, wrapper);
        }

        if (table instanceof BaseTable) {
            final BaseTable baseTable = (BaseTable)table;
            // Process the primary key ...
            final PrimaryKey pk = baseTable.getPrimaryKey();
            if (pk != null) {
                create(parent, pk, wrapper); // primary keys go under model, not under table
            }

            // Process the foreign key ...
            final Iterator fkIter = baseTable.getForeignKeys().iterator();
            while (fkIter.hasNext()) {
                final EObject fkey = (EObject)fkIter.next();
                create(parent, fkey, wrapper); // foreign keys go under model, not under table
            }

            // Process the unique keys ...
            final Iterator ukIter = baseTable.getUniqueConstraints().iterator();
            while (ukIter.hasNext()) {
                final EObject ukey = (EObject)ukIter.next();
                create(parent, ukey, wrapper); // foreign keys go under model, not under table
            }
        }
    }

    /**
     * Method to create document contents for a Column object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the EObject representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final Column column,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(column);
        final Element columnElement = new Element(Xml.Column.TAG);
        parent.addContent(columnElement);
        setAttribute(columnElement, Xml.Column.Attributes.NAME, getObjectNameInDdl(column));
        setAttribute(columnElement, Xml.Column.Attributes.UUID, editor.getObjectID(column));
        final Annotation annotation = wrapper.getContents().getAnnotation(column);
        if (annotation != null) {
            setAttribute(columnElement, Xml.Column.Attributes.DESCRIPTION, annotation.getDescription());
        }

        // Set the type and length information
        setAttribute(columnElement, Xml.Column.Attributes.TYPE, getType(column));
        final int precision = column.getPrecision();
        final int scale = column.getScale();
        final int length = column.getLength();
        if (precision != 0) {
            setAttribute(columnElement, Xml.Column.Attributes.PRECISION, precision);
        }
        if (length != 0) {
            setAttribute(columnElement, Xml.Column.Attributes.LENGTH, length);
        }
        if (scale != 0) {
            setAttribute(columnElement, Xml.Column.Attributes.SCALE, scale);
        }
        setAttribute(columnElement, Xml.Column.Attributes.IS_FIXED_LENGTH, column.isFixedLength());

        // Set the default value ...
        final String defaultValue = column.getDefaultValue();
        if (defaultValue != null && defaultValue.trim().length() != 0) {
            setAttribute(columnElement, Xml.Column.Attributes.DEFAULT_VALUE, defaultValue);
        }

        // The column is nullable only if known to be nullable ...
        final boolean isNullable = column.getNullable() != null && NullableType.NULLABLE_LITERAL.equals(column.getNullable());
        setAttribute(columnElement, Xml.Column.Attributes.IS_NULLABLE, isNullable);

        setAttribute(columnElement, Xml.Column.Attributes.SUPPORTS_SELECT, column.isSelectable());
        setAttribute(columnElement, Xml.Column.Attributes.SUPPORTS_UPDATE, column.isUpdateable());
        setAttribute(columnElement, Xml.Column.Attributes.IS_CASE_SENSITIVE, column.isCaseSensitive());
        setAttribute(columnElement, Xml.Column.Attributes.IS_SIGNED, column.isSigned());
        setAttribute(columnElement, Xml.Column.Attributes.IS_CURRENCY, column.isCurrency());
        setAttribute(columnElement, Xml.Column.Attributes.IS_AUTOINCREMENTED, column.isAutoIncremented());
        final SearchabilityType searchType = column.getSearchability();
        if (searchType != null) {
        }
        setAttribute(columnElement, Xml.Column.Attributes.SEARCH_TYPE, null);

    }

    /**
     * Return the datatype name for the supplied column.
     * 
     * @param column the column; may not be null;
     * @return the String name for the datatype; if null, then no datatype name will be written to the
     */
    protected String getType( final Column column ) {
        final String nativeType = column.getNativeType();
        if (nativeType != null && this.options.isNativeTypeUsed()) {
            return nativeType;
        }
        final EObject dt = column.getType();
        if (dt != null) {
            return getType(column, dt);
        }
        return null;
    }

    /**
     * Return the datatype name for the supplied simple datatype.
     * 
     * @param datatype the datatype; may not be null
     * @return the String name for the datatype; if null, then no datatype name will be written to the output
     */
    protected String getType( final Column column,
                              final EObject datatype ) {
        final DatatypeManager dtManager = ModelerCore.getDatatypeManager(column, true);

        // Look for a runtime type on the type ...
        final String runtimeType = dtManager.getRuntimeTypeName(datatype);
        if (runtimeType != null) {
            return runtimeType;
        }

        // Find the supertype that is a builtin ...
        EObject builtin = datatype;
        while (builtin != null) {
            builtin = dtManager.getBaseType(builtin);
            if (builtin == null) {
                break;
            }

            // Look for a runtime type on the type ...
            final String builtinRuntimeType = dtManager.getRuntimeTypeName(builtin);
            if (builtinRuntimeType != null) {
                return builtinRuntimeType;
            }
        }

        // If we've gotten to here, then try to get the JDBC type name ...
        return dtManager.getName(datatype);
    }

    /**
     * Method to create document contents for a PrimaryKey object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the EObject representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final PrimaryKey pkey,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(pkey);
        final Element pkElement = new Element(Xml.PrimaryKey.TAG);
        parent.addContent(pkElement);

        setAttribute(pkElement, Xml.PrimaryKey.Attributes.NAME, getUniqueObjectName(pkey));
        setAttribute(pkElement, Xml.PrimaryKey.Attributes.UUID, editor.getObjectID(pkey));
        setAttribute(pkElement, Xml.PrimaryKey.Attributes.PATH_IN_MODEL, editor.getModelRelativePath(pkey));

        // Process the table that contains the primary key ...
        final Table pkTable = pkey.getTable();
        if (pkTable != null) { // should never be null, but check just in case ...
            setAttribute(pkElement, Xml.PrimaryKey.Attributes.TABLE_NAME, getObjectNameInDdl(pkTable));
        }

        // Process the columns that the primary key references ...
        final List columns = pkey.getColumns();
        final Iterator iter = columns.iterator();
        while (iter.hasNext()) {
            final Column column = (Column)iter.next();
            final Element pkColumnElement = new Element(Xml.PrimaryKeyColumn.TAG);
            setAttribute(pkColumnElement, Xml.PrimaryKeyColumn.Attributes.NAME, getObjectNameInDdl(column));
            setAttribute(pkColumnElement, Xml.PrimaryKeyColumn.Attributes.UUID, editor.getObjectID(column));
            pkElement.addContent(pkColumnElement);
        }

    }

    /**
     * Method to create document contents for a UniqueKey object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the EObject representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final UniqueKey ukey,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(ukey);
        final Element pkElement = new Element(Xml.UniqueKey.TAG);
        parent.addContent(pkElement);
        setAttribute(pkElement, Xml.UniqueKey.Attributes.NAME, getUniqueObjectName(ukey));
        setAttribute(pkElement, Xml.UniqueKey.Attributes.UUID, editor.getObjectID(ukey));
        setAttribute(pkElement, Xml.UniqueKey.Attributes.PATH_IN_MODEL, editor.getModelRelativePath(ukey));

        // Process the table that contains the primary key ...
        final Table ukTable = ukey.getTable();
        if (ukTable != null) { // should never be null, but check just in case ...
            setAttribute(pkElement, Xml.PrimaryKey.Attributes.TABLE_NAME, getObjectNameInDdl(ukTable));
        }

        // Process the columns that the unique key references ...
        final List columns = ukey.getColumns();
        final Iterator iter = columns.iterator();
        while (iter.hasNext()) {
            final Column column = (Column)iter.next();
            final Element pkColumnElement = new Element(Xml.UniqueKeyColumn.TAG);
            setAttribute(pkColumnElement, Xml.UniqueKeyColumn.Attributes.NAME, getObjectNameInDdl(column));
            setAttribute(pkColumnElement, Xml.UniqueKeyColumn.Attributes.UUID, editor.getObjectID(column));
            pkElement.addContent(pkColumnElement);
        }

    }

    /**
     * Method to create document contents for a ForeignKey object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the EObject representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final ForeignKey fkey,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(fkey);
        final Element fkElement = new Element(Xml.ForeignKey.TAG);
        parent.addContent(fkElement);
        setAttribute(fkElement, Xml.ForeignKey.Attributes.NAME, getUniqueObjectName(fkey));
        setAttribute(fkElement, Xml.ForeignKey.Attributes.UUID, editor.getObjectID(fkey));
        setAttribute(fkElement, Xml.ForeignKey.Attributes.PATH_IN_MODEL, editor.getModelRelativePath(fkey));

        // Process the table that contains the foreign key ...
        final Table fkTable = fkey.getTable();
        if (fkTable != null) { // should never be null, but just in case ...
            setAttribute(fkElement, Xml.ForeignKey.Attributes.TABLE_NAME, getObjectNameInDdl(fkTable));
        }

        // Process the table of the primary key ...
        final UniqueKey ukey = fkey.getUniqueKey();
        List pkColumns = null;
        if (ukey != null) { // should never be null, but check just in case ...
            final Table ukeyTable = ukey.getTable();
            if (ukeyTable != null) { // should never be null, but check just in case ...
                setAttribute(fkElement, Xml.ForeignKey.Attributes.PK_TABLE_NAME, getObjectNameInDdl(ukeyTable));
            }
            pkColumns = ukey.getColumns();
        }

        // Process the columns that the foreign key references ...
        final List columns = fkey.getColumns();
        int index = 0;
        final Iterator iter = columns.iterator();
        while (iter.hasNext()) {
            final Column column = (Column)iter.next();
            final Element fkColumnElement = new Element(Xml.ForignKeyColumn.TAG);
            setAttribute(fkColumnElement, Xml.ForignKeyColumn.Attributes.NAME, getObjectNameInDdl(column));
            setAttribute(fkColumnElement, Xml.ForignKeyColumn.Attributes.UUID, editor.getObjectID(column));
            // Find the corresponding column in the primary key; do this by order, but be tolerant
            // of when the # of pk columns is different than the number of fk columns
            if (pkColumns != null && pkColumns.size() > index) {
                final Column pkColumn = (Column)pkColumns.get(index);
                setAttribute(fkColumnElement, Xml.ForignKeyColumn.Attributes.PK_COLUMN_NAME, getObjectNameInDdl(pkColumn));
            }
            fkElement.addContent(fkColumnElement);
            ++index;
        }
    }

    /**
     * Method to create document contents for an Index object.
     * 
     * @param parent the Element that is the parent of any created Elements or Attributes; may not be null
     * @param eobj the Index representing the graph of objects for which document content may be created; may not be null
     */
    protected void create( final Element parent,
                           final Index index,
                           final ModelWrapper wrapper ) {
        ArgCheck.isNotNull(parent);
        ArgCheck.isNotNull(index);

        // Skip this object if it is not to be exported ...
        if (!wrapper.isSelected(index)) {
            return;
        }

        final Element indexElement = new Element(Xml.Index.TAG);
        parent.addContent(indexElement);
        setAttribute(indexElement, Xml.Index.Attributes.NAME, getUniqueObjectName(index));
        setAttribute(indexElement, Xml.Index.Attributes.UUID, editor.getObjectID(index));
        setAttribute(indexElement, Xml.Index.Attributes.PATH_IN_MODEL, editor.getModelRelativePath(index));

        // Process the columns that the primary key references ...
        final List columns = index.getColumns();
        final Element indexColumnElement = new Element(Xml.IndexColumns.TAG);
        indexElement.addContent(indexColumnElement);
        Table indexedTable = null;
        final Iterator iter = columns.iterator();
        while (iter.hasNext()) {
            final Column column = (Column)iter.next();
            final Element indexedColumnElement = new Element(Xml.IndexColumn.TAG);
            setAttribute(indexedColumnElement, Xml.IndexColumn.Attributes.NAME, getObjectNameInDdl(column));
            setAttribute(indexedColumnElement, Xml.IndexColumn.Attributes.UUID, editor.getObjectID(column));
            indexColumnElement.addContent(indexedColumnElement);
            indexedTable = (Table)column.getOwner();
        }

        // Process the table that contains the foreign key ...
        if (indexedTable != null) { // should never be null, but just in case ...
            setAttribute(indexElement, Xml.ForeignKey.Attributes.TABLE_NAME, getObjectNameInDdl(indexedTable));
        }

    }

    /**
     * Generate the name for the DDL object from the relational entity.
     * 
     * @param entity the entity for which the name is to be determined
     * @return the object name for use in the DDL
     */
    protected String getObjectNameInDdl( final RelationalEntity entity ) {
        if (this.options.isNameInSourceUsed()) {
            // Should try to use the name-in-source if there is one ...
            String name = entity.getNameInSource();
            // Ensure there is a name ...
            if (name == null || name.trim().length() == 0) {
                // There is no name-in-source, so just use the entity's name ...
                name = entity.getName();
            }
            return name;
        }
        // Just use the entity's name ...
        return entity.getName();
    }

    /**
     * Generate the name for the DDL object from the relational entity.
     * 
     * @param entity the entity for which the name is to be determined
     * @return the object name for use in the DDL
     */
    protected String getUniqueObjectName( final RelationalEntity entity ) {
        if (this.options.isUniqueNamesEnforced()) {
            Object name = this.relationalEntities.get(entity);
            if (name == null) {
                DdlPlugin.Util.log(IStatus.WARNING, DdlPlugin.Util.getString("IntermediateFormat.0", entity)); //$NON-NLS-1$
                return getObjectNameInDdl(entity);
            }
            return (String)name;
        }
        return getObjectNameInDdl(entity);
    }

}
