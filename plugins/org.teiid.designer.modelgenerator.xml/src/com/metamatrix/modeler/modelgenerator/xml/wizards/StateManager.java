/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.modelgenerator.xml.model.UserSettings;
import com.metamatrix.modeler.schema.tools.model.schema.QName;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessorFactory;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessor;
import com.metamatrix.modeler.schema.tools.processing.SchemaUtil;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;

public class StateManager {

    /** Key=File or IFile, Value=Resource. */
    protected Map xsds = new HashMap();
    private ResourceSet resourceSet;

    // The model of the accumulated schemas
    private SchemaModel schemaModel;

    // The schemaModel for rollup (modification)
    private SchemaModel processedModel;

    // The list of root elements that the user has selected.
    private Collection selectedRoots;

    // Processing objects
    private SchemaProcessor processor;
    private RelationshipProcessor relationshipProcessor;

    // Internal data indicating the state of the state.
    private boolean schemasModified = false;
    private boolean modelDirty = true;

    // Other State
    UserSettings userSettings;
    int catalogType = XsdAsRelationalImportWizard.NO_CATALOG_VAL;

    private String customCatalogName;

    public static final int SOURCE_DOCUMENT = 0;
    public static final int SOURCE_HTTP_NO_PARAMS = 1;
    public static final int SOURCE_HTTP_PARAMS = 2;
    public static final int SOURCE_HTTP_REQUEST_DOC = 3;
    public static final int SOURCE_ACS = 4;

    private boolean acsMode = false;

    public StateManager( UserSettings userSettings ) {
        this.userSettings = userSettings;
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("wsdl", new XSDResourceFactoryImpl()); //$NON-NLS-1$
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl()); //$NON-NLS-1$
        resourceSet = new ResourceSetImpl();
    }

    public void addSchema( Object key,
                           URI uri ) {
        xsds.put(key, uri);
        schemasModified = true;
    }

    public Set getSchemaKeySet() {
        return xsds.keySet();
    }

    public int getSchemaCount() {
        return xsds.size();
    }

    public Object removeSchema( Object key ) {
        return xsds.remove(key);
    }

    public List getSchemaURIs() {
        return new ArrayList(xsds.values());
    }

    public Collection getPotentialRoots() throws Exception {
        if (null == processor) {
            processor = new SchemaProcessorImpl(UserSettings.getMergedChildSep());
        }

        if (null == schemaModel || schemasModified) {
            initSchemaModel();
            schemasModified = false;
        }

        if (null == selectedRoots) {
            selectedRoots = new HashSet();
        }

        List potentialRoots = schemaModel.getPotentialRootElements();
        for (Iterator iter = potentialRoots.iterator(); iter.hasNext();) {
            RootElement elem = (RootElement)iter.next();
            if (elem.isUseAsRoot()) {
                selectedRoots.add(elem);
            }
        }
        return potentialRoots;
    }

    public void setSelectedRoots( Collection selectedRoots ) {
        this.selectedRoots = selectedRoots;
        modelDirty = true;
    }

    public boolean isUserSelectedRoot( SchemaObject element ) {
        for (Iterator iter = selectedRoots.iterator(); iter.hasNext();) {
            RootElement root = (RootElement)iter.next();
            if (root.getKey() == element.getKey()) {
                return true;
            }
        }
        return false;
    }

    private void initSchemaModel() throws Exception {
        List schemaUriList = Arrays.asList(xsds.values().toArray());
        List schemas = new ArrayList(schemaUriList.size());
        Iterator iter = schemaUriList.iterator();
        while (iter.hasNext()) {
            URI uri = (URI)iter.next();
            URI resourceUri;
            Resource res;
            File file = new File(uri.toString());
            if ((uri.scheme() != null && uri.scheme().equals("ACSResponse")) || //$NON-NLS-1$
                (uri.scheme() != null && uri.scheme().equals("ACSRequest"))) { //$NON-NLS-1$
                InputStream stream = new ByteArrayInputStream(uri.fragment().getBytes("UTF-8")); //$NON-NLS-1$
                XSDResourceImpl acsResource = new XSDResourceImpl();
                acsResource.setURI(uri); // get around a defect in XSDResourceImpl.
                resourceSet.getResources().add(acsResource);
                acsResource.load(stream, new HashMap());
                res = acsResource;
            } else {
                if (file.isFile()) {
                    resourceUri = URI.createFileURI(file.getCanonicalPath().toString());
                } else {
                    resourceUri = uri;
                }
                res = resourceSet.getResource(resourceUri, true);
            }
            schemas.add(((XSDResourceImpl)res).getSchema());
        }
        XSDSchema[] result = new XSDSchema[schemas.size()];
        schemas.toArray(result);
        processor.processSchemas(result);
        schemaModel = processor.getSchemaModel();
    }

    public SchemaModel getProcessedModel() {
        if (null == processedModel || !modelDirty) {
            if (null == relationshipProcessor) {
                relationshipProcessor = RelationshipProcessorFactory.getQueryOptimizingProcessor(userSettings.get_C_threshold(),
                                                                                                 userSettings.get_P_threshold(),
                                                                                                 userSettings.get_F_threshold());
            }

            processedModel = schemaModel.copy();
            HashSet roots = new HashSet(selectedRoots);
            processedModel.setSelectedRootElements(roots);
            relationshipProcessor.calculateRelationshipTypes(processedModel);
        }
        modelDirty = false;
        return processedModel;
    }

    public SchemaModel getSchemaModel() {
        return schemaModel;
    }

    public RelationshipProcessor getRelationshipProcessor() {
        return relationshipProcessor;
    }

    public List getCatalogs() {
        List retVal = null;
        switch (getCatalogType()) {
            case XsdAsRelationalImportWizard.NAMESPACE_CATALOG_VAL:
            case XsdAsRelationalImportWizard.NO_CATALOG_VAL:
                retVal = new ArrayList(0);
                break;
            case XsdAsRelationalImportWizard.FILENAME_CATALOG_VAL:
                retVal = getFileNames();
                break;
            case XsdAsRelationalImportWizard.CUSTOM_CATALOG_VAL:
                retVal = new ArrayList(1);
                retVal.add(customCatalogName);
                break;
            default:
                retVal = new ArrayList();
                break;
        }
        return retVal;
    }

    public int getCatalogType() {
        return catalogType;
    }

    public void setCatalogType( int catalogType ) {
        this.catalogType = catalogType;
    }

    private List getFileNames() {
        ArrayList retVal = new ArrayList(xsds.size());
        Collection xsdValues = xsds.values();
        for (Iterator i = xsdValues.iterator(); i.hasNext();) {
            URI uri = (URI)i.next();
            retVal.add(SchemaUtil.shortenFileName(SchemaProcessorImpl.getSchemaFromURI(uri).getSchemaLocation()));
        }
        return retVal;
    }

    public void setCustomCatalogName( String name ) {
        customCatalogName = name;
    }

    public String getCustomCatalogName() {
        return customCatalogName;
    }

    public boolean isUsingNoCatalog() {
        return catalogType == XsdAsRelationalImportWizard.NO_CATALOG_VAL;
    }

    public boolean isUsingNamespaces() {
        return catalogType == XsdAsRelationalImportWizard.NAMESPACE_CATALOG_VAL;
    }

    public boolean isUsingFileNames() {
        return catalogType == XsdAsRelationalImportWizard.FILENAME_CATALOG_VAL;
    }

    public boolean isUsingCustom() {
        return catalogType == XsdAsRelationalImportWizard.CUSTOM_CATALOG_VAL;
    }

    public Map getNamespaces() {
        return schemaModel.getNamespaces();
    }

    public String getFirstCatalog() {
        String catalog = ""; //$NON-NLS-1$
        for (Iterator resourceIter = xsds.values().iterator(); resourceIter.hasNext();) {
            Object o = resourceIter.next();
            URI uri = (URI)o;
            catalog = SchemaProcessorImpl.getSchemaFromURI(uri).getTargetNamespace();
        }
        return catalog;
    }

    // Returns the qname of the request table (whether or not request-response
    // is enabled). More accurately, it returns the qname of what the request table
    // would be if thee is one.
    public QName getRequestResponseTable() {
        List catalogs = getCatalogs();
        int global = -1;
        int countExcludingGlobal = catalogs.size();
        for (int i = 0; i < catalogs.size(); i++) {
            if (catalogs.get(i) == null || catalogs.get(i).equals(StateManager.globalNamespace)) {
                global = i;
                --countExcludingGlobal;
                break;
            }
        }
        String namespace;
        switch (countExcludingGlobal) {
            case 0:
                // only namespace is global: use it
                namespace = StateManager.globalNamespace;
                break;
            case 1:
                // only one namespace other than global: use the namespace that is not global
                if (global == 0) {
                    namespace = (String)catalogs.get(1);
                } else {
                    namespace = (String)catalogs.get(0);
                }
                break;
            default:
                // more than one namespace other than global: since we don't know which
                // to use, use global it it exists, otherwise use the first namespace
                // alphabetically
                if (global == -1) {
                    TreeSet treeSet = new TreeSet();
                    for (int i = 0; i < catalogs.size(); i++) {
                        treeSet.add(catalogs.get(i));
                    }
                    namespace = (String)treeSet.first();
                } else {
                    namespace = (String)catalogs.get(global);
                }
        }
        return SchemaUtil.getQName(namespace, userSettings.getRequestTableLocalName());
    }

    public Boolean isRequestOrResponseTable( QName qname ) {
        if (SOURCE_DOCUMENT == userSettings.getSourceType() || SOURCE_HTTP_NO_PARAMS == userSettings.getSourceType()) {
            return null;
        }

        boolean retval = qname.equals(getRequestResponseTable());
        return new Boolean(retval);
    }

    public static String globalNamespace = XmlImporterUiPlugin.getDefault().getPluginUtil().getString("XsdAsRelationalImportWizard.globalNamespace"); //$NON-NLS-1$

    public String getCatalog( SchemaObject table ) {
        String catalog = ""; //$NON-NLS-1$
        if (isUsingNamespaces() || isUsingNoCatalog()) {
            String namespace = table.getNamespace();
            if (namespace == null) {
                catalog = globalNamespace;
            } else {
                catalog = CoreValidationRulesUtil.getValidString(namespace, null, -1);
            }
        }
        if (isUsingFileNames()) {
            catalog = table.getFileName();
        }
        if (isUsingCustom()) {
            catalog = (String)getCatalogs().get(0);
        }
        return catalog;
    }

    public void setACSMode( boolean acsMode ) {
        this.acsMode = acsMode;
    }

    public boolean getACSMode() {
        return acsMode;
    }

}
