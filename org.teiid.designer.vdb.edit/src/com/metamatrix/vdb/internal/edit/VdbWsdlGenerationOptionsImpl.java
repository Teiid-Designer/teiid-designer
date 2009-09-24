/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchema;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.UriUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.webservice.util.ReferencedXSDSchemaFinder;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.io.WsdlWriter;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.EnhancedStringTokenizer;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.webservice.IWsdlGenerator;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbWsdlGenerationOptions;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;

/**
 * @since 4.2
 */
public class VdbWsdlGenerationOptionsImpl implements VdbWsdlGenerationOptions {

    private final VdbEditingContextImpl context;
    private final VdbContextEditor contextEditor;

    /**
     * @since 4.2
     */
    public VdbWsdlGenerationOptionsImpl( final VdbEditingContextImpl context ) {
        super();
        ArgCheck.isNotNull(context);
        this.context = context;
        this.contextEditor = null;
    }

    public VdbWsdlGenerationOptionsImpl( final VdbContextEditor context ) {
        super();
        ArgCheck.isNotNull(context);
        this.context = null;
        this.contextEditor = context;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#isValidUri(java.lang.String)
     * @since 4.2
     */
    public boolean isValidUri( String str ) {
        try {
            new URI(str);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#canWsdlBeGenerated()
     * @since 4.2
     */
    public boolean canWsdlBeGenerated() {

        // See if there any Web Service models in the VDB ...
        final List models = getVirtualDatabase().getModels();
        final Iterator iter = models.iterator();
        while (iter.hasNext()) {
            final ModelReference model = (ModelReference)iter.next();
            if (isWebService(model)) {
                return true;
            }
        }
        return false;
    }

    protected WsdlOptions getWsdlOptions( boolean createIfRequired ) {
        final VirtualDatabase vdb = getVirtualDatabase();
        WsdlOptions options = vdb.getWsdlOptions();
        if (options == null && createIfRequired) {
            // Create the options ...
            options = ManifestFactory.eINSTANCE.createWsdlOptions();
            vdb.setWsdlOptions(options);

            // Set the defaults ...
            options.setDefaultNamespaceUri(WsdlPackage.eNS_URI); // per WS-I
        }
        return options;
    }

    public List getWebServiceModelReferences() {
        final List results = new ArrayList();

        // See if there any Web Service models in the VDB ...
        final List models = getVirtualDatabase().getModels();
        final Iterator iter = models.iterator();
        while (iter.hasNext()) {
            final ModelReference model = (ModelReference)iter.next();
            if (isWebService(model)) {
                results.add(model);
            }
        }
        return results;
    }

    public List getWebServiceResources() {
        final List results = new ArrayList();

        // See if there any Web Service models in the VDB ...
        final List models = getVirtualDatabase().getModels();
        final Iterator iter = models.iterator();
        while (iter.hasNext()) {
            final ModelReference model = (ModelReference)iter.next();
            if (isWebService(model)) {
                results.add(model);
            }
        }
        return results;
    }

    private boolean isWebService( ModelReference model ) {
        String uri = model.getPrimaryMetamodelUri();
        if (WebServicePackage.eNS_URI.equals(uri)) {
            return true;
        }
        // Return false if not XML service model or
        // if it is an XML Service model but it is a source
        if ((!XmlServicePackage.eNS_URI.equals(uri))
            || (XmlServicePackage.eNS_URI.equals(uri) && model.getModelType().getValue() == ModelType.PHYSICAL)) {
            return false;
        }

        try {
            Resource resource = null;
            // Reminder: calls to ModelerCore.getModelWorkspace() fail in MMQuery, so if we ever support web or XML services in
            // that product, we need to add some code to account for this.
            ModelResource mr[] = ModelerCore.getModelWorkspace().getModelResources();
            for (int i = 0; i < mr.length; i++) {
                if (mr[i].getPath().toString().equals(model.getModelLocation())) {
                    resource = mr[i].getEmfResource();
                    break;
                }
            }
            if (resource != null) {
                return WsdlGenerationOptionsHelper.exposeXmlServiceInWsdl(resource);
            }
        } catch (Exception e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }

        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getTargetNamespaceUri()
     * @since 4.2
     */
    public String getTargetNamespaceUri() {
        final WsdlOptions options = getWsdlOptions(false);
        String result = null;
        if (options != null) {
            result = options.getTargetNamespaceUri();
        }
        if (result == null) {
            result = StringUtil.Constants.EMPTY_STRING;
        }
        return result;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#setTargetNamespaceUri(java.lang.String)
     * @since 4.2
     */
    public void setTargetNamespaceUri( final String namespaceUri ) {
        final WsdlOptions options = getWsdlOptions(true);
        if (options != null) {
            options.setTargetNamespaceUri(namespaceUri);
        }

        // Set the default to be the target, if there is no default yet ...
        if (StringUtil.Constants.EMPTY_STRING.equals(this.getDefaultNamespaceUri())) {
            this.setDefaultNamespaceUri(namespaceUri);
        }

        // Check whether this is a valid URI ...
        if (namespaceUri != null && namespaceUri.trim().length() != 0) {
            try {
                new URI(namespaceUri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public Collection getReferencedXsds() throws ModelWorkspaceException {
        // Find all of the Web Service/XML Service Models ...
        final List wsModelRefs = getWebServiceModelReferences();
        HashSet xsdSet = new HashSet();
        if (wsModelRefs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        // Find the XSDs referenced by all of the models ...
        final ReferencedXSDSchemaFinder visitor = new ReferencedXSDSchemaFinder();

        // Find the XSDs referenced by all of the xml service models ...
        final com.metamatrix.metamodels.xmlservice.util.ReferencedXSDSchemaFinder xsVisitor = new com.metamatrix.metamodels.xmlservice.util.ReferencedXSDSchemaFinder();

        // First find all Web Service model schemas
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);

        Resource resource = null;
        ModelReference modelRef = null;
        Iterator iter = wsModelRefs.iterator();
        while (iter.hasNext()) {
            modelRef = (ModelReference)iter.next();
            // Obtain the actual Web Service model ...
            resource = getResource(modelRef);
            try {
                processor.walk(resource, ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException err) {
                throw new ModelWorkspaceException(err);
            }
        }

        // Now find all XML Service model schemas
        final ModelVisitorProcessor xsProcessor = new ModelVisitorProcessor(xsVisitor);
        iter = wsModelRefs.iterator();
        while (iter.hasNext()) {
            modelRef = (ModelReference)iter.next();
            // Obtain the actual XML Service model ...
            resource = getResource(modelRef);
            try {
                xsProcessor.walk(resource, ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException err) {
                throw new ModelWorkspaceException(err);
            }
        }

        xsdSet.addAll(visitor.getXsdSchemas());
        xsdSet.addAll(xsVisitor.getXsdSchemas());

        return xsdSet;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getAllNamespaceUris()
     * @since 4.2
     */
    public String[] getAllNamespaceUris() throws ModelWorkspaceException {
        // Find all of the Web Service Models ...
        final List wsModelRefs = getWebServiceModelReferences();
        if (wsModelRefs.isEmpty()) {
            return new String[] {};
        }

        // Find the XSDs referenced by all of the models ...
        final ReferencedXSDSchemaFinder visitor = new ReferencedXSDSchemaFinder();
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        final Iterator iter = wsModelRefs.iterator();
        while (iter.hasNext()) {
            final ModelReference modelRef = (ModelReference)iter.next();
            // Obtain the actual model ...
            final Resource resource = getResource(modelRef);
            if (resource != null) {
                try {
                    processor.walk(resource, ModelVisitorProcessor.DEPTH_INFINITE);
                } catch (ModelerCoreException err) {
                    throw new ModelWorkspaceException(err);
                }
            }
        }

        // Accumulate the namespaces ...
        final Set uniqueNamespaceUris = new HashSet(visitor.getXsdTargetNamespaces());

        // Also add the target namespace ...
        final String tns = this.getTargetNamespaceUri();
        if (tns != null && tns.trim().length() != 0) {
            uniqueNamespaceUris.add(this.getTargetNamespaceUri());
        }

        // Add the namespace URI of the WSDL spec ...
        uniqueNamespaceUris.add(WsdlPackage.eNS_URI);

        // Add the namespace URI of the XSD Schema of Schemas ...
        final Iterator iter2 = visitor.getXsdSchemas().iterator();
        while (iter2.hasNext()) {
            final XSDSchema schema = (XSDSchema)iter2.next();
            final String sosNamespace = schema.getSchemaForSchemaNamespace();
            uniqueNamespaceUris.add(sosNamespace);
        }

        // Sort ...
        final List namespaceUris = new ArrayList(uniqueNamespaceUris);
        Collections.sort(namespaceUris, StringUtil.CASE_SENSITIVE_ORDER);

        // Return the list ...
        return (String[])namespaceUris.toArray(new String[namespaceUris.size()]);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getDefaultNamespaceUri()
     * @since 4.2
     */
    public String getDefaultNamespaceUri() {
        final WsdlOptions options = getWsdlOptions(false);
        String result = null;
        if (options != null) {
            result = options.getDefaultNamespaceUri();
        }
        if (result == null) {
            result = StringUtil.Constants.EMPTY_STRING;
        }
        return result;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#setDefaultNamespaceUri(java.lang.String)
     * @since 4.2
     */
    public void setDefaultNamespaceUri( String namespaceUri ) {
        // Check whether this is a valid URI ...
        if (namespaceUri != null && namespaceUri.trim().length() != 0) {
            try {
                new URI(namespaceUri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        final WsdlOptions options = getWsdlOptions(true);
        if (options != null) {
            options.setDefaultNamespaceUri(namespaceUri);
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getWsdl(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public InputStream getWsdl( final IProgressMonitor monitor ) throws IOException, VdbEditException {
        final String result = getWsdlAsString(monitor);
        return new ByteArrayInputStream(result.getBytes());
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getWsdlAsString(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public String getWsdlAsString( IProgressMonitor monitor ) throws IOException, VdbEditException {
        // Create a StringBuffer into which the WSDL can be written ...
        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
        final BufferedOutputStream stream = new BufferedOutputStream(bas);
        IStatus status = null;
        try {
            status = generateWsdl(monitor, stream);
        } finally {
            stream.close();
        }
        if (status.getSeverity() == IStatus.ERROR) {
            throw new VdbEditException(status);
        }

        return bas.toString();
    }

    public IStatus generateWsdl( final IProgressMonitor monitor,
                                 final OutputStream stream ) {
        final List problems = new ArrayList();
        if (this.canWsdlBeGenerated()) {
            final IWsdlGenerator wsdlGenerator = WebServicePlugin.createWsdlGenerator();
            wsdlGenerator.setName(getVirtualDatabase().getName());
            wsdlGenerator.setTargetNamespace(this.getTargetNamespaceUri());
            wsdlGenerator.setDefaultNamespaceUri(this.getDefaultNamespaceUri());
            wsdlGenerator.setUrlRootForReferences(VdbEditPlugin.URL_ROOT_FOR_VDB);
            wsdlGenerator.setUrlSuffixForReferences(VdbEditPlugin.URL_SUFFIX_FOR_VDB);
            wsdlGenerator.setUrlForWsdlService(VdbEditPlugin.URL_FOR_DATA_WEBSERVICE);
            wsdlGenerator.setXmlEncoding(WsdlWriter.ENCODING_UTF8);
            // Add the web service models ...
            final List modelRefs = this.getWebServiceModelReferences();
            final Iterator iter = modelRefs.iterator();
            while (iter.hasNext()) {
                final ModelReference modelRef = (ModelReference)iter.next();
                // Obtain the actual model ...
                final Resource resource = getResource(modelRef);
                if (resource != null) {
                    wsdlGenerator.addWebServiceModel(resource);
                }
            }
            // Add the XSDs ...
            final File tempDirFolder = getTempDirectoryFolder();
            try {
                final Collection xsdSchemas = this.getReferencedXsds();
                final Iterator iter2 = xsdSchemas.iterator();
                while (iter2.hasNext()) {
                    final XSDSchema schema = (XSDSchema)iter2.next();
                    if (schema != null) {
                        // Find the model path for this schema ...
                        final Resource emfResource = schema.eResource();
                        if (emfResource != null && emfResource.getURI().isFile()) {
                            final String pathToSchema = getPathRelativeToFolder(tempDirFolder,
                                                                                new File(emfResource.getURI().toFileString()));

                            if (pathToSchema != null) {
                                final IPath path = new Path(pathToSchema);
                                final IPath pathWithEncoding = encodePathForUseInUri(path);
                                wsdlGenerator.addXsdModel(schema, pathWithEncoding);
                            } else {
                                wsdlGenerator.addXsdModel(schema, null); // have it compute it based on the URI
                            }
                        }
                    }
                }
            } catch (ModelWorkspaceException e) {
                final String msg = VdbEditPlugin.Util.getString("VdbWsdlGenerationOptionsImpl.ErrorAccessingXSDs"); //$NON-NLS-1$
                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
            }

            // Generate the WSDL ...
            final IStatus status = wsdlGenerator.generate(monitor);
            if (status.getSeverity() != IStatus.ERROR) {
                // Write the WSDL ...
                try {
                    wsdlGenerator.write(stream);
                } catch (IOException e) {
                    final String msg = VdbEditPlugin.Util.getString("VdbWsdlGenerationOptionsImpl.ProblemsGeneratingWsdl"); //$NON-NLS-1$
                    problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
                }
            } else {
                if (status instanceof MultiStatus) {
                    final IStatus[] children = ((MultiStatus)status).getChildren();
                    for (int i = 0; i < children.length; i++) {
                        problems.add(children[i]);
                    }
                } else {
                    problems.add(status);
                }
            }
        }

        // Process the problems ...
        if (problems.size() == 1) {
            return (IStatus)problems.get(0);
        }
        if (problems.size() == 0) {
            final String msg = VdbEditPlugin.Util.getString("VdbWsdlGenerationOptionsImpl.SuccessfullyGeneratedWsdl"); //$NON-NLS-1$
            return new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
        }
        final String msg = VdbEditPlugin.Util.getString("VdbWsdlGenerationOptionsImpl.ProblemsGeneratingWsdl"); //$NON-NLS-1$
        final IStatus[] problemArray = (IStatus[])problems.toArray(new IStatus[problems.size()]);
        return new MultiStatus(VdbEditPlugin.PLUGIN_ID, 0, problemArray, msg, null);
    }

    private VirtualDatabase getVirtualDatabase() {
        VirtualDatabase vdb = null;
        if (this.context != null) {
            vdb = this.context.getVirtualDatabase();
        } else if (this.contextEditor != null) {
            vdb = this.contextEditor.getVirtualDatabase();
        }
        return vdb;
    }

    private File getTempDirectoryFolder() {
        File tempDirFolder = null;
        if (this.context != null) {
            tempDirFolder = new File(this.context.getTempDirectory().getPath());
        } else if (this.contextEditor != null) {
            tempDirFolder = new File(this.contextEditor.getTempDirectory().getPath());
        }
        return tempDirFolder;
    }

    private Resource getResource( final ModelReference modelRef ) {
        ArgCheck.isNotNull(modelRef);
        Resource resource = null;
        if (this.context != null) {
            resource = this.context.findInternalResource(modelRef.getModelLocation());
        } else if (this.contextEditor != null) {
            File tempDirFolder = new File(this.contextEditor.getTempDirectory().getPath());
            File modelFile = new File(tempDirFolder, modelRef.getModelLocation());
            org.eclipse.emf.common.util.URI modelFileUri = org.eclipse.emf.common.util.URI.createFileURI(modelFile.getAbsolutePath());
            resource = this.contextEditor.getVdbResourceSet().getResource(modelFileUri, true);
        }
        return resource;
    }

    protected String getPathRelativeToFolder( final File parentFolder,
                                              final File f ) {
        ArgCheck.isNotNull(parentFolder);
        ArgCheck.isNotNull(f);

        String relativePath = null;
        try {
            String folderPath = parentFolder.getCanonicalPath();
            String filePath = f.getCanonicalPath();
            if (filePath.startsWith(folderPath)) {
                relativePath = filePath.substring(folderPath.length());
            }
        } catch (IOException e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }
        return relativePath;
    }

    protected IPath encodePathForUseInUri( final IPath pathToResourceInVdb ) {
        final String[] pathSegments = pathToResourceInVdb.segments();
        // Go through the segments and check each for validity ...
        IPath pathWithEncoding = Path.ROOT;
        for (int i = 0; i < pathSegments.length; i++) {
            final String segment = pathSegments[i];

            // Handle the '.' character explicity ...
            final StringBuffer encodedSegment = new StringBuffer();
            final String tokenDelimSet = ".?_"; //$NON-NLS-1$
            final EnhancedStringTokenizer segmentTokens = new EnhancedStringTokenizer(segment, tokenDelimSet);
            while (segmentTokens.hasMoreTokens()) {
                final String token = segmentTokens.nextToken();
                final String delims = segmentTokens.nextDelimiters();
                final String encodedSegmentToken = UriUtil.escape(token);
                encodedSegment.append(encodedSegmentToken);
                if (delims != null) {
                    encodedSegment.append(delims);
                }
            }

            pathWithEncoding = pathWithEncoding.append(encodedSegment.toString());
        }
        return pathWithEncoding;
    }

}
