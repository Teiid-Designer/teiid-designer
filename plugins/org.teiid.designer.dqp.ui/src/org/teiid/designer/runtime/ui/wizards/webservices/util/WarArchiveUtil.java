/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.wizards.webservices.util;

import static org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants.NAMESPACE_PROVIDER;
import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper;
import org.teiid.designer.core.workspace.ModelObjectAnnotationHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.webservice.gen.BasicWsdlGenerator;


/**
 * 
 *
 * @since 8.0
 */
public class WarArchiveUtil {

    /**
     * Target Namespace Key
     */
    public static final String TARGETNS = "targetNs"; //$NON-NLS-1$

    /**
     * Web Service Name Key
     */
    public static final String WEBSERVICENAME = "webserviceName"; //$NON-NLS-1$

    /**
     * Url Root Key
     */
    public static final String URLROOT = "urlRoot"; //$NON-NLS-1$

    /**
     * Service Url Key
     */
    public static final String SERVICEURL = "serviceUrl"; //$NON-NLS-1$

    /**
     * WSDL File Extension
     */
    public static final String WSDLFILE_EXT = "wsdl"; //$NON-NLS-1$

    private static final ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();


    /**
     * @param path
     * @return schema
     */
    public XSDSchema importSchema( String path ) {
        XSDParser parser = new XSDParser(null);
        parser.parse(path);
        XSDSchema schema = parser.getSchema();
        schema.setSchemaLocation(path);
        return schema;
    }

    /**
     * @param uri
     * @return path parameters
     */
    public static ArrayList<String> getPathParameters( String uri ) {
        ArrayList pathParams = new ArrayList();
        String param;
        if (uri.contains("{")) { //$NON-NLS-1$
            while (uri.indexOf("}") > -1) { //$NON-NLS-1$
                int start = uri.indexOf("{"); //$NON-NLS-1$
                int end = uri.indexOf("}"); //$NON-NLS-1$
                param = uri.substring(start + 1, end);
                uri = uri.substring(end + 1);
                pathParams.add(param);
            }
        }
        return pathParams;
    }

    /**
     * Generate a WSDL file using passed in WS Model Resources and user supplied values
     * @param wsModelResourceList
     * @param userSuppliedValues 
     * 
     * @since 7.1
     */
    public void generateWSDL( ArrayList<ModelResource> wsModelResourceList,
                              Properties userSuppliedValues ) {

        BasicWsdlGenerator wsdlGenerator = new BasicWsdlGenerator();
        ModelResource wsModel = null;
        // This will be overwritten by the web service model name
        String webServiceName = userSuppliedValues.getProperty(WEBSERVICENAME, "TeiidWS"); //$NON-NLS-1$
        for (ModelResource webServiceModel : wsModelResourceList) {
            try {
                wsModel = webServiceModel;
                wsdlGenerator.addWebServiceModel(webServiceModel.getEmfResource());
                webServiceName = webServiceModel.getItemName();
                List<? extends IResource> iResources = WorkspaceResourceFinderUtil.getDependentResources(webServiceModel.getResource());
                for (IResource iResource : iResources) {
                    if (ModelIdentifier.isSchemaModel(iResource)) {
                        wsdlGenerator.addXsdModel(importSchema(iResource.getLocation().toOSString()), iResource.getLocation());
                    }
                }
            } catch (ModelWorkspaceException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // TODO: Create wizard to override these default values as part of the soap war generator in 7.1
        webServiceName = webServiceName.substring(0, webServiceName.lastIndexOf(".")); //$NON-NLS-1$
        wsdlGenerator.setName(webServiceName);
        wsdlGenerator.setTargetNamespace(userSuppliedValues.getProperty(TARGETNS, "http://teiid.org")); //$NON-NLS-1$
        wsdlGenerator.setUrlRootForReferences(userSuppliedValues.getProperty(URLROOT, "")); //$NON-NLS-1$
        wsdlGenerator.setUrlSuffixForReferences(""); //$NON-NLS-1$
        wsdlGenerator.setUrlForWsdlService(userSuppliedValues.getProperty(SERVICEURL, "http://serverName:port/warName/")); //$NON-NLS-1$
        final IStatus status = wsdlGenerator.generate(new NullProgressMonitor());

        // nothing more to do if an error is expected
        if (status.getSeverity() == IStatus.ERROR) {
            throw new RuntimeException("Unable to generate WSDL"); //$NON-NLS-1$
        }

        String fileName = webServiceName + "." + WSDLFILE_EXT; //$NON-NLS-1$
        try {
            // Create our WSDL file and write to it
            String path = wsModel.getResource().getLocation().toOSString();
            OutputStream stream = new FileOutputStream(new File(path.substring(0, path.lastIndexOf("/")), fileName)); //$NON-NLS-1$
            wsdlGenerator.write(stream);
            // Get an iFile instance to refresh our workspace
            IFile iFile = wsModel.getModelProject().getProject().getFile(fileName);
            iFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (CoreException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    
    /**
     * @param vdbFile
     * @return is the given file a rest war vdb
     * @throws Exception
     */
    public static boolean isRestWarVdb(IFile vdbFile) throws Exception {
        if (! isVdb(vdbFile))
            return false;

        boolean result = false;
        try {
            Vdb vdb = new XmiVdb(vdbFile);
            Set<VdbModelEntry> modelEntrySet = vdb.getModelEntries();
            for (VdbEntry vdbModelEntry : modelEntrySet) {
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(vdbModelEntry.getPath());
                if (! ModelIdentifier.isVirtualModelType(modelResource))
                    continue;

                result = hasRestProcedures(modelResource);
                if (result) {
                    break;
                }
            }
        } catch (Exception ex) {
            new RuntimeException(ex);
        }
        return result;
    }
    
    /**
     * @param result
     * @param obj
     */
    private static boolean isVdb(Object obj) {
        if (obj == null)
            return false;

        if (! (obj instanceof IFile))
            return false;

        return ITeiidVdb.VDB_EXTENSION.equals(((IFile) obj).getFileExtension());
    }
    
    /**
     * @param eObjectList
     * @return boolean true if model contains a REST procedure
     */
    private static boolean hasRestProcedures(ModelResource modelResource) throws Exception {
        Collection<EObject> eObjectList = modelResource.getEObjects();
        boolean result = false;
        for (EObject eObject : eObjectList) {
            if (SqlAspectHelper.isProcedure(eObject)) {
                IPath path = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(eObject);
                final StringBuffer sb = new StringBuffer();
                final String[] segments = path.segments();
                for (int i = 0; i < segments.length; i++) {
                    if (i != 0) {
                        sb.append('.');
                    }
                    final String segment = segments[i];
                    sb.append(segment);
                    Procedure procedure = (Procedure)eObject;
                    String restMethod = getRestMethod(procedure);
                    String uri = null;
                    if (restMethod != null) {
                        uri = getUri(procedure);
                    }
                    if (uri != null && restMethod != null){
                    	result = true;
                    	break;
                    }
                }
            }
        }

        return result;
    }
    
    /**
     * @param procedure the procedure
     * @return String uri value
     */
    public static String getUri( Procedure procedure ) {
        String uri = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            uri = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.URI);

            if (CoreStringUtil.isEmpty(uri)) {
                uri = (String)ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                        ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                + "URI"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        return uri;
    }
    
    /**
     * @param procedure the procedure
     * @return String rest method
     */
    public static String getRestMethod( Procedure procedure ) {
        String restMethod = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            restMethod = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.REST_METHOD);

            if (CoreStringUtil.isEmpty(restMethod.trim())) {
                // try old way
                restMethod = (String)ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                               ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                       + "REST-METHOD"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        return restMethod;
    }

}
