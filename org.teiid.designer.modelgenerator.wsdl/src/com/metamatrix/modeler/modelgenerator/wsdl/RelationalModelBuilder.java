/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.modelgenerator.wsdl.modelextension.XMLWSDLExtensionManager;
import com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions.SoapDataTypeImpl;
import com.metamatrix.modeler.modelgenerator.wsdl.util.NameUtil;
import com.metamatrix.modeler.schema.tools.model.jdbc.Column;
import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

public class RelationalModelBuilder {

    private static final String RESPONSE_IN_PATTERN_NAME = "ResponseInRequired"; //$NON-NLS-1$

    // private ArrayList m_exceptions;
    private XMLWSDLExtensionManager m_extPropManager = null;
    private Map m_namespaces;
    private ArrayList m_foreignKeys;
    private HashMap m_tables;
    private HashMap m_bindings;
    ModelResource modelResource;
    private HashMap catalogs;

    // Map of CatalogNames:replacementCatalogNames used to resolve name conflicts in model updates
    private HashMap replacementCatalogNames = new HashMap();

    public RelationalModelBuilder( Map namespaces ) {
        // m_exceptions = new ArrayList();
        m_namespaces = namespaces;
        m_tables = new HashMap();
        m_foreignKeys = new ArrayList();
        m_bindings = new HashMap();
    }

    public IFile createModel( Collection tables,
                              String modelFile,
                              IContainer container ) throws ModelBuildingException {

        IFile retVal = null;
        try {
            retVal = createRelationalModel(tables, modelFile, container);

            // add connector binding
            for (Iterator iter = m_bindings.values().iterator(); iter.hasNext();) {
                SoapBindingInfo info = (SoapBindingInfo)iter.next();
                String name = info.generateUniqueName();
                info.createConnectorBinding(modelFile, name);
            }
            container.refreshLocal(IResource.DEPTH_INFINITE, null);

            // refresh the project
            if (!ModelEditorManager.isOpen(retVal)) {
                ModelEditorManager.activate(retVal, true);
            }
        } catch (IOException e) {
            throw new ModelBuildingException(e);
            // m_exceptions.add(new ModelBuildingException(e));
        } catch (CoreException e) {
            // m_exceptions.add(new ModelBuildingException(e));
            throw new ModelBuildingException(e);
        } catch (NullPointerException npe) {
            throw new ModelBuildingException(npe);
            // m_exceptions.add(new ModelBuildingException(npe));
            // npe.printStackTrace(System.err);
        } catch (Exception e) {
            throw new ModelBuildingException(e);
        }
        return retVal;
    }

    private IFile createRelationalModel( Collection tables,
                                         String modelFile,
                                         IContainer container ) throws CoreException, ModelBuildingException {

        // ========================================================================
        // Creating relational models
        // ========================================================================

        // Create a handle to a java.io.File that will become the new relational model.
        // Dimension friendly
        Path modelPath = new Path(modelFile);
        IFile iFile = container.getFile(modelPath);
        File f = iFile.getLocation().toFile();
        boolean newModel = !f.exists();
        modelResource = ModelerCore.create(iFile);

        // Obtain a container to use for creating and holding the new relational model resource -
        // assume the "model container" which is used to hold all workspace resources
        com.metamatrix.modeler.core.ModelerCore.getModelContainer();

        // initialize the extensions property manager
        m_extPropManager = new XMLWSDLExtensionManager();
        m_extPropManager.loadModelExtensions(container, new NullProgressMonitor());

        if (newModel) {
            // Add a ModelAnnotation to the newly created resource to identify it as a physical relational model
            final ModelAnnotation modelAnnotation = modelResource.getModelAnnotation();
            modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);
            modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            modelAnnotation.setExtensionPackage(m_extPropManager.getPackage());
        } else {
            final ModelAnnotation modelAnnotation = modelResource.getModelAnnotation();
            ModelType modelType = modelResource.getModelType();
            if (null == modelType) {
                modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);
            } else if (modelType.getValue() != ModelType.PHYSICAL) {
                String message = ModelGeneratorWsdlPlugin.Util.getString("RelationalModelBuilder.exception.unsupported.metamodel.virtual"); //$NON-NLS-1$
                Exception e = new Exception(message);
                throw new ModelBuildingException(e);
            }
            MetamodelDescriptor descriptor = modelResource.getPrimaryMetamodelDescriptor();
            if ((null == descriptor)) {
                modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            } else if (!(descriptor.getNamespaceURI().equals(RelationalPackage.eNS_URI))) {
                String message = ModelGeneratorWsdlPlugin.Util.getString("RelationalModelBuilder.exception.unsupported.metamodel"); //$NON-NLS-1$
                Exception e = new Exception(message);
                throw new ModelBuildingException(e);
            }

            // Can't find a way to look this up, so we just set it.
            modelAnnotation.setExtensionPackage(m_extPropManager.getPackage());
        }

        Resource resource;
        try {
            resource = modelResource.getEmfResource();
        } catch (ModelWorkspaceException e) {
            throw new ModelBuildingException(e);
        }

        if (!newModel) {
            findOperationNameConflicts(resource, tables);
        }

        createModelContents(tables, resource);

        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

            @Override
            public void execute( final IProgressMonitor monitor ) throws CoreException {
                modelResource.save(monitor, false);
            }
        };

        IProgressMonitor monitor = new NullProgressMonitor();
        try {
            operation.run(monitor);
        } catch (Exception ex) {
            if (ex instanceof InvocationTargetException) {
                throw new ModelBuildingException(((InvocationTargetException)ex).getTargetException());
            }
            throw new ModelBuildingException(ex);
        }

        return iFile;
    }

    private void findOperationNameConflicts( Resource resource,
                                             Collection tables ) {
        if (null == catalogs) {
            catalogs = new HashMap();
        }
        EList contents = resource.getContents();
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof com.metamatrix.metamodels.relational.Catalog) {
                Catalog catalog = (com.metamatrix.metamodels.relational.Catalog)obj;
                catalogs.put(catalog.getName(), catalog);
            }
        }

        for (Iterator tabIter = tables.iterator(); tabIter.hasNext();) {
            SOAPTableImpl tabWrap = (SOAPTableImpl)tabIter.next();
            Table tab = tabWrap.getTable();
            String catalogName = tab.getCatalog();
            if (catalogs.containsKey(catalogName)) {
                Catalog catalog = (Catalog)catalogs.get(catalogName);
                String schemaName = tab.getSchema();
                for (Iterator schemaIter = catalog.getSchemas().iterator(); schemaIter.hasNext();) {
                    Schema schema = (Schema)schemaIter.next();
                    if (schemaName.equals(schema.getName())) {
                        String newCatalog;
                        boolean done = false;
                        int counter = 1;
                        while (!done) {
                            newCatalog = catalogName + '_' + counter;
                            if (!catalogs.containsKey(newCatalog)) {
                                replacementCatalogNames.put(catalogName, newCatalog);
                                done = true;
                            } else {
                                counter++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void createModelContents( Collection tables,
                                      Resource r ) throws ModelBuildingException {

        Iterator tableIter = tables.iterator();
        if (null == catalogs) {
            catalogs = new HashMap();
        }
        HashMap schemas = new HashMap();
        while (tableIter.hasNext()) {
            SOAPTableImpl tabWrap = (SOAPTableImpl)tableIter.next();
            Table tab = tabWrap.getTable();
            SoapBindingInfo info = tabWrap.getSoapBindingInfo();
            if (info != null && m_bindings.get(info) == null) m_bindings.put(info, info);
            boolean isRequest = tabWrap.isRequest();

            String catalogName = tab.getCatalog();
            if (replacementCatalogNames.containsKey(catalogName)) {
                catalogName = (String)replacementCatalogNames.get(catalogName);
            }
            Catalog cat = (Catalog)catalogs.get(catalogName);
            if (cat == null) {
                cat = RelationalPackage.eINSTANCE.getRelationalFactory().createCatalog();
                cat.setName(catalogName);
                r.getContents().add(cat);
                catalogs.put(catalogName, cat);
            }
            Schema schema = (Schema)schemas.get(tab.getSchema());
            if (schema == null) {
                schema = RelationalPackage.eINSTANCE.getRelationalFactory().createSchema();
                schema.setName(tab.getSchema());
                schema.setCatalog(cat);
                schemas.put(tab.getSchema(), schema);
            }

            // create the table
            com.metamatrix.metamodels.relational.BaseTable relTab = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE.getRelationalFactory().createBaseTable();
            relTab.setCatalog(cat);
            relTab.setSchema(schema);
            relTab.setName(tab.getName());
            if (!isRequest) relTab.setNameInSource(tab.getOutputXPath().toString());
            if (isRequest && tabWrap.getSoapAction() != null) {
                m_extPropManager.setSoapAction(relTab, tabWrap.getSoapAction());
            }

            // namespace
            m_extPropManager.setNamespacePrefixesAttribute(relTab, tab.getNamespaceDeclaration());

            // input XPath
            if (isRequest) {
                // the connector requires even request tables to have name in source
                // so we use /
                relTab.setNameInSource("/"); //$NON-NLS-1$
                m_extPropManager.setXPathRootForInputAttribute(relTab, tab.getInputXPath());
            }

            Column[] cols = tab.getColumns();
            for (int i = 0; i < cols.length; i++) {
                if (isRequest && cols[i].getName().endsWith("_" + TableBuilder.MMID)) continue; //$NON-NLS-1$
                try {
                    com.metamatrix.metamodels.relational.Column col = addColumn(relTab, cols[i], isRequest);
                    if (col.getName().equals(TableBuilder.RESPONSE_IN)) addResponseInAccessPattern(relTab, col);
                    if (col.getName().equals(TableBuilder.MMID)) {
                        PrimaryKey key = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE.getRelationalFactory().createPrimaryKey();
                        relTab.setPrimaryKey(key);
                        key.getColumns().add(col);
                        key.setName(col.getName() + "_PK"); //$NON-NLS-1$
                    }
                    if (cols[i].isForeignKey()) {
                        addForeignKey(new FKWrapper(col, relTab, tab.getCatalog(), tab.getSchema()));
                    }
                } catch (ModelerCoreException e) {
                    throw new ModelBuildingException(e);
                    // m_exceptions.add(new ModelBuildingException(e));
                }
            }
            m_tables.put(tab.getCatalog() + '.' + tab.getSchema() + '.' + tab.getName(), relTab);
        }
        createForeignKeys();
    }

    private void addForeignKey( FKWrapper wrapper ) {
        m_foreignKeys.add(wrapper);
    }

    private void addResponseInAccessPattern( com.metamatrix.metamodels.relational.Table relTab,
                                             com.metamatrix.metamodels.relational.Column column ) {
        AccessPattern accessPattern = RelationalPackage.eINSTANCE.getRelationalFactory().createAccessPattern();
        accessPattern.setName(RESPONSE_IN_PATTERN_NAME);
        accessPattern.setTable(relTab);
        accessPattern.getColumns().add(column);
    }

    private com.metamatrix.metamodels.relational.Column addColumn( com.metamatrix.metamodels.relational.Table relTab,
                                                                   Column column,
                                                                   boolean isRequest ) throws ModelerCoreException {
        com.metamatrix.metamodels.relational.Column relCol = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE.getRelationalFactory().createColumn();
        relTab.getColumns().add(relCol);
        relCol.setName(NameUtil.normalizeNameForRelationalTable(column.getName()));
        relCol.setLength(512);
        String prefix = lookupNamespacePrefix(column.getDataType().getTypeNamespace());
        DataType dataType = column.getDataType();
        if (dataType instanceof SoapDataTypeImpl) {
            SoapDataTypeImpl soapDataType = (SoapDataTypeImpl)dataType;
            String arrayNamespacePrefix = lookupNamespacePrefix(soapDataType.getArrayNamespace());
            StringBuffer typeBuffer = new StringBuffer();
            typeBuffer.append(lookupNamespacePrefix(TableBuilder.XML_SCHEMA_URI));
            typeBuffer.append(":type=\""); //$NON-NLS-1$
            typeBuffer.append(arrayNamespacePrefix);
            typeBuffer.append(":"); //$NON-NLS-1$
            typeBuffer.append(soapDataType.getArrayName());
            typeBuffer.append("\";"); //$NON-NLS-1$
            typeBuffer.append(lookupNamespacePrefix("http://schemas.xmlsoap.org/soap/encoding/")); //$NON-NLS-1$
            typeBuffer.append(":arrayType=\""); //$NON-NLS-1$
            typeBuffer.append(arrayNamespacePrefix);
            typeBuffer.append(":"); //$NON-NLS-1$
            typeBuffer.append(soapDataType.getTypeName());
            typeBuffer.append("\";"); //$NON-NLS-1$
            typeBuffer.append(lookupNamespacePrefix(TableBuilder.XML_SCHEMA_URI));
            typeBuffer.append(":"); //$NON-NLS-1$
            typeBuffer.append(arrayNamespacePrefix);
            typeBuffer.append("=\""); //$NON-NLS-1$
            typeBuffer.append(soapDataType.getArrayNamespace());
            typeBuffer.append("\""); //$NON-NLS-1$
            relCol.setNativeType(typeBuffer.toString());
        } else {
            relCol.setNativeType(prefix + ":" + column.getDataType().getTypeName()); //$NON-NLS-1$
        }
        EObject type = getTypeForColumn(column);
        relCol.setType(type);
        relCol.setPrecision(12);
        // ext props
        if (!isRequest) {
            if (column.getOutputXPath() != null) {
                relCol.setNameInSource(column.getOutputXPath().toString());
            }
            if (column.getName().endsWith(TableBuilder.MMID)) {
                relCol.setNullable(NullableType.NO_NULLS_LITERAL);
            }
        }
        if (isRequest) {
            m_extPropManager.setColumnXPathForInput(relCol, column.getInputXPath());
            relCol.setNameInSource(column.getName());
        }

        if (null != column.getMultipleValues()) {
            m_extPropManager.setMultipleValue(relCol, column.getMultipleValues());
        }
        m_extPropManager.setColumnInputParamAttribute(relCol, column.isInputParameter());
        m_extPropManager.setAllowEmptyInputElement(relCol, isRequest);
        m_extPropManager.setColumnRoleAttribute(relCol, column.getRole());
        return relCol;
    }

    private EObject getTypeForColumn( Column column ) throws ModelerCoreException {
        DatatypeManager dtMgr = ModelerCore.getBuiltInTypesManager();
        EObject type = null;
        if (column.getDataType().getTypeNamespace().equals(TableBuilder.XML_SCHEMA_URI)) {
            String name = column.getDataType().getTypeName();
            type = dtMgr.getBuiltInDatatype(name);
            if (type == null) type = dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
        } else {
            type = dtMgr.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
        }
        return type;
    }

    public String lookupNamespacePrefix( String namespaceURI ) {
        String prefix = null;
        for (Iterator iter = m_namespaces.keySet().iterator(); iter.hasNext();) {
            String key = (String)iter.next();
            if (m_namespaces.get(key).equals(namespaceURI)) {
                prefix = key;
                break;
            }
        }
        return prefix;
    }

    private void createForeignKeys() {
        for (Iterator keyIter = m_foreignKeys.iterator(); keyIter.hasNext();) {
            FKWrapper wrap = (FKWrapper)keyIter.next();
            BaseTable tab = wrap.getTable();
            com.metamatrix.metamodels.relational.Column col = wrap.getColumn();
            String colName = col.getName();
            // strip out _mmid;
            String parentTable = colName.substring(0, (colName.length() - (TableBuilder.MMID.length() + 1)));
            String catalogName = NameUtil.normalizeNameForRelationalTable(wrap.getCatalogName());
            String schemaName = NameUtil.normalizeNameForRelationalTable(wrap.getSchemaName());
            String parentTable2 = NameUtil.normalizeNameForRelationalTable(parentTable);
            BaseTable parent = (BaseTable)m_tables.get(catalogName + '.' + schemaName + '.' + parentTable2);
            if (null != parent) {
                PrimaryKey pk = parent.getPrimaryKey();
                ForeignKey fkey = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE.getRelationalFactory().createForeignKey();
                tab.getForeignKeys().add(fkey);
                fkey.setName("FK_" + parentTable); //$NON-NLS-1$
                fkey.setUniqueKey(pk);
                fkey.getColumns().add(col);
            } else {
                // TODO: log an error message - log not throw - that foreign key could not be created.
            }
        }
    }

    private class FKWrapper {

        private com.metamatrix.metamodels.relational.Column m_col;
        private BaseTable m_tab;
        private String m_catalogName;
        private String m_schemaName;

        public FKWrapper( com.metamatrix.metamodels.relational.Column col,
                          com.metamatrix.metamodels.relational.BaseTable tab,
                          String catalogName,
                          String schemaName ) {
            m_col = col;
            m_tab = tab;
            m_catalogName = catalogName;
            m_schemaName = schemaName;
        }

        public com.metamatrix.metamodels.relational.Column getColumn() {
            return m_col;
        }

        public BaseTable getTable() {
            return m_tab;
        }

        public String getCatalogName() {
            return m_catalogName;
        }

        public String getSchemaName() {
            return m_schemaName;
        }
    }
}
