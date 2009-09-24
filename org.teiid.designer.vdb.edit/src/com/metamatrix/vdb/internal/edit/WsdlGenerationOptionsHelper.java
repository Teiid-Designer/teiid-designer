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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.UriUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.webservice.util.ReferencedXSDSchemaFinder;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.io.WsdlWriter;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.util.EnhancedStringTokenizer;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.webservice.IWsdlGenerator;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbWsdlGenerationOptions;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;

/**
 * @since 4.2
 */
public class WsdlGenerationOptionsHelper implements VdbWsdlGenerationOptions {

    private static final String XML_LITERAL_TYPE_URI_STRING = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI).appendFragment(DatatypeConstants.BuiltInNames.XML_LITERAL).toString();

    private final List eResources;
    private final WsdlOptions wsdlOptions;
    private final Map eResourceToPath; // Map of Resource to IPath location

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    public WsdlGenerationOptionsHelper( final List eResources,
                                        final WsdlOptions wsdlOptions,
                                        final Map eResourceToPath ) {
        super();
        ArgCheck.isNotNull(eResources);
        ArgCheck.isNotNull(wsdlOptions);
        ArgCheck.isNotNull(eResourceToPath);
        this.eResources = eResources;
        this.wsdlOptions = wsdlOptions;
        this.eResourceToPath = eResourceToPath;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getWebServiceModelReferences()
     * @since 5.0
     */
    public List getWebServiceModelReferences() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#setDefaultNamespaceUri(java.lang.String)
     * @since 5.0
     */
    public void setDefaultNamespaceUri( String namespaceUri ) {
        // Check whether this is a valid URI ...
        if (namespaceUri != null && namespaceUri.trim().length() != 0) {
            try {
                new java.net.URI(namespaceUri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        if (this.wsdlOptions != null) {
            this.wsdlOptions.setDefaultNamespaceUri(namespaceUri);
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#setTargetNamespaceUri(java.lang.String)
     * @since 5.0
     */
    public void setTargetNamespaceUri( String namespaceUri ) {
        if (this.wsdlOptions != null) {
            this.wsdlOptions.setTargetNamespaceUri(namespaceUri);
        }

        // Set the default to be the target, if there is no default yet ...
        if (StringUtil.Constants.EMPTY_STRING.equals(this.getDefaultNamespaceUri())) {
            this.setDefaultNamespaceUri(namespaceUri);
        }

        // Check whether this is a valid URI ...
        if (namespaceUri != null && namespaceUri.trim().length() != 0) {
            try {
                new java.net.URI(namespaceUri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#isValidUri(java.lang.String)
     * @since 5.0
     */
    public boolean isValidUri( final String str ) {
        try {
            new java.net.URI(str);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#canWsdlBeGenerated()
     * @since 5.0
     */
    public boolean canWsdlBeGenerated() {
        // See if there any XmlService/WebService models in the eResources list ...
        for (Iterator i = this.eResources.iterator(); i.hasNext();) {
            Resource r = (Resource)i.next();
            if (r instanceof EmfResource) {
                if (((EmfResource)r).getPrimaryMetamodelUri() == null) {
                    return false;
                }
                String primaryMetamodelUri = ((EmfResource)r).getPrimaryMetamodelUri().toString();
                if (WebServicePackage.eNS_URI.equals(primaryMetamodelUri)) {
                    return true;
                }
                if (XmlServicePackage.eNS_URI.equals(primaryMetamodelUri) && exposeXmlServiceInWsdl(r)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getTargetNamespaceUri()
     * @since 5.0
     */
    public String getTargetNamespaceUri() {
        String result = null;
        if (this.wsdlOptions != null) {
            result = this.wsdlOptions.getTargetNamespaceUri();
        }
        if (result == null) {
            result = StringUtil.Constants.EMPTY_STRING;
        }
        return result;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getAllNamespaceUris()
     * @since 5.0
     */
    public String[] getAllNamespaceUris() throws ModelWorkspaceException {
        // Find all of the xml service models ...
        final List xsResources = getXmlServiceResources();

        // Find all of the web service models ...
        final List wsResources = getWebServiceResources();
        if (xsResources.isEmpty() && wsResources.isEmpty()) {
            return new String[] {};
        }

        // Find the XSDs referenced by all of the web service models ...
        final ReferencedXSDSchemaFinder wsVisitor = new ReferencedXSDSchemaFinder();
        executeModelVisitor(wsResources, wsVisitor);

        // Find the XSDs referenced by all of the xml service models ...
        final com.metamatrix.metamodels.xmlservice.util.ReferencedXSDSchemaFinder xsVisitor = new com.metamatrix.metamodels.xmlservice.util.ReferencedXSDSchemaFinder();
        executeModelVisitor(xsResources, xsVisitor);

        // Accumulate the namespaces ...
        final Set uniqueNamespaceUris = new HashSet();
        uniqueNamespaceUris.addAll(wsVisitor.getXsdTargetNamespaces());
        uniqueNamespaceUris.addAll(xsVisitor.getXsdTargetNamespaces());

        // Also add the target namespace ...
        final String tns = this.getTargetNamespaceUri();
        if (tns != null && tns.trim().length() != 0) {
            uniqueNamespaceUris.add(this.getTargetNamespaceUri());
        }

        // Add the namespace URI of the WSDL spec ...
        uniqueNamespaceUris.add(WsdlPackage.eNS_URI);

        // Accumulate the schemas ...
        final Collection schemas = new HashSet();
        schemas.addAll(wsVisitor.getXsdSchemas());
        schemas.addAll(xsVisitor.getXsdSchemas());

        // Add the namespace URI of the XSD Schema of Schemas ...
        for (Iterator i = schemas.iterator(); i.hasNext();) {
            final XSDSchema schema = (XSDSchema)i.next();
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
     * @since 5.0
     */
    public String getDefaultNamespaceUri() {
        String result = null;
        if (this.wsdlOptions != null) {
            result = this.wsdlOptions.getDefaultNamespaceUri();
        }
        if (result == null) {
            result = StringUtil.Constants.EMPTY_STRING;
        }
        return result;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getWsdl(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.0
     */
    public InputStream getWsdl( final IProgressMonitor monitor ) throws IOException, VdbEditException {
        final String result = getWsdlAsString(monitor);
        return new ByteArrayInputStream(result.getBytes());
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbWsdlGenerationOptions#getWsdlAsString(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.0
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

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    public List getXmlServiceResources() {
        final List results = new ArrayList();

        // Collect any Xml Service models in the eResources list that should be exposed in the WSDL ...
        for (Iterator i = this.eResources.iterator(); i.hasNext();) {
            Resource r = (Resource)i.next();
            if (r instanceof EmfResource && ((EmfResource)r).getModelType().getValue() == ModelType.VIRTUAL
                && ((EmfResource)r).getPrimaryMetamodelUri() != null) {
                String primaryMetamodelUri = ((EmfResource)r).getPrimaryMetamodelUri().toString();
                if (XmlServicePackage.eNS_URI.equals(primaryMetamodelUri) && exposeXmlServiceInWsdl(r)) {
                    results.add(r);
                }
            }
        }
        return results;
    }

    public List getWebServiceResources() {
        final List results = new ArrayList();

        // Collect all Web Service models in the eResources list ...
        for (Iterator i = this.eResources.iterator(); i.hasNext();) {
            Resource r = (Resource)i.next();
            if (r instanceof EmfResource && ((EmfResource)r).getPrimaryMetamodelUri() != null) {
                String primaryMetamodelUri = ((EmfResource)r).getPrimaryMetamodelUri().toString();
                if (WebServicePackage.eNS_URI.equals(primaryMetamodelUri)) {
                    results.add(r);
                }
            }
        }
        return results;
    }

    public Collection getReferencedXsds() throws ModelWorkspaceException {
        // Find all of the xml service models ...
        final List xsResources = getXmlServiceResources();

        // Find all of the web service models ...
        final List wsResources = getWebServiceResources();
        if (xsResources.isEmpty() && wsResources.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        // Find the XSDs referenced by all of the web service models ...
        final ReferencedXSDSchemaFinder wsVisitor = new ReferencedXSDSchemaFinder();
        executeModelVisitor(wsResources, wsVisitor);

        // Find the XSDs referenced by all of the xml service models ...
        final com.metamatrix.metamodels.xmlservice.util.ReferencedXSDSchemaFinder xsVisitor = new com.metamatrix.metamodels.xmlservice.util.ReferencedXSDSchemaFinder();
        executeModelVisitor(xsResources, xsVisitor);

        final Collection schemas = new HashSet();
        schemas.addAll(wsVisitor.getXsdSchemas());
        schemas.addAll(xsVisitor.getXsdSchemas());
        return schemas;
    }

    public IStatus generateWsdl( final IProgressMonitor monitor,
                                 final OutputStream stream ) {
        final List problems = new ArrayList();
        if (this.canWsdlBeGenerated()) {
            final IWsdlGenerator wsdlGenerator = WebServicePlugin.createWsdlGenerator();
            wsdlGenerator.setName(wsdlOptions.getVirtualDatabase().getName());
            wsdlGenerator.setTargetNamespace(getTargetNamespaceUri());
            wsdlGenerator.setDefaultNamespaceUri(getDefaultNamespaceUri());
            wsdlGenerator.setUrlRootForReferences(VdbEditPlugin.URL_ROOT_FOR_VDB);
            wsdlGenerator.setUrlSuffixForReferences(VdbEditPlugin.URL_SUFFIX_FOR_VDB);
            wsdlGenerator.setUrlForWsdlService(VdbEditPlugin.URL_FOR_DATA_WEBSERVICE);
            wsdlGenerator.setXmlEncoding(WsdlWriter.ENCODING_UTF8);

            // Add the web service models ...
            final List eResources = new ArrayList();
            eResources.addAll(getWebServiceResources());
            eResources.addAll(getXmlServiceResources());
            for (Iterator i = eResources.iterator(); i.hasNext();) {
                Resource wsResource = (Resource)i.next();
                if (wsResource != null) {
                    wsdlGenerator.addWebServiceModel(wsResource);
                }
            }

            // Add the XSDs ...
            try {
                final Collection xsdSchemas = getReferencedXsds();
                for (Iterator i = xsdSchemas.iterator(); i.hasNext();) {
                    XSDSchema schema = (XSDSchema)i.next();
                    if (schema != null) {
                        // Find the model path for this schema ...
                        final Resource eResource = schema.eResource();
                        if (eResource != null) {
                            IPath pathToSchema = (IPath)this.eResourceToPath.get(eResource);
                            if (pathToSchema != null) {
                                final IPath pathWithEncoding = encodePathForUseInUri(pathToSchema);
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
            // But do it in a transaction

            IStatus generateWsdlStatus = null;

            boolean requiredStart = ModelerCore.startTxn(false, false, "Generate WSDL", this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                generateWsdlStatus = wsdlGenerator.generate(monitor);
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

            final IStatus status = generateWsdlStatus;
            if (status != null) {
                if (status.getSeverity() != IStatus.ERROR) {
                    // Write the WSDL ...
                    boolean reqStart = ModelerCore.startTxn(false, false, "Write WSDL", this); //$NON-NLS-1$
                    boolean succ = false;
                    try {
                        wsdlGenerator.write(stream);
                    } catch (IOException e) {
                        final String msg = VdbEditPlugin.Util.getString("VdbWsdlGenerationOptionsImpl.ProblemsGeneratingWsdl"); //$NON-NLS-1$
                        problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
                        succ = true;
                    } finally {
                        // If we start txn, commit it
                        if (reqStart) {
                            if (succ) {
                                ModelerCore.commitTxn();
                            } else {
                                ModelerCore.rollbackTxn();
                            }
                        }
                    }
                } else {
                    if (status instanceof MultiStatus) {
                        final IStatus[] children = ((MultiStatus)status).getChildren();
                        for (int i = 0; i < children.length; i++) {
                            if (children[i] != null) {
                                problems.add(children[i]);
                            }
                        }
                    } else {
                        problems.add(status);
                    }
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

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected static boolean exposeXmlServiceInWsdl( final Resource r ) {
        if (r instanceof EmfResource) {
            if (((EmfResource)r).getPrimaryMetamodelUri() == null) {
                return false;
            }
            String primaryMetamodelUri = ((EmfResource)r).getPrimaryMetamodelUri().toString();
            if (XmlServicePackage.eNS_URI.equals(primaryMetamodelUri)) {
                // Ensure that the resource is loaded so that we can navigate its contents
                if (!r.isLoaded()) {
                    Map loadOptions = (r.getResourceSet() != null ? r.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
                    try {
                        r.load(loadOptions);
                    } catch (Throwable e) {
                        VdbEditPlugin.Util.log(e);
                    }
                }
                for (Iterator i = r.getContents().iterator(); i.hasNext();) {
                    EObject eObj = (EObject)i.next();
                    if (eObj instanceof XmlOperation) {
                        // Only XML Services that take a single input of type XMLLiteral should be exposed
                        XmlOperation op = (XmlOperation)eObj;
                        if (op.getInputs().size() == 1) {
                            XmlInput input = (XmlInput)op.getInputs().get(0);
                            if (input != null && input.getType() != null) {
                                XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)input.getType();
                                if (type.eIsProxy()
                                    && XML_LITERAL_TYPE_URI_STRING.equals(((InternalEObject)type).eProxyURI().toString())) {
                                    return true;
                                } else if (XML_LITERAL_TYPE_URI_STRING.equals(type.getURI())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Utility method to encode the segments of the supplied path such that the total path can be used within a URI. For example,
     * the path "<code>/Project Name/folder name/model name.txt</code>" cannot be directly placed into a URL, but instead needs to
     * be escaped: <code>/Project%20Name/folder%20name/model%20name.txt</code>.
     */
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

    protected void executeModelVisitor( final List eResources,
                                        final ModelVisitor visitor ) throws ModelWorkspaceException {
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        for (Iterator i = eResources.iterator(); i.hasNext();) {
            Resource r = (Resource)i.next();
            if (r != null) {
                try {
                    processor.walk(r, ModelVisitorProcessor.DEPTH_INFINITE);
                } catch (ModelerCoreException err) {
                    throw new ModelWorkspaceException(err);
                }
            }
        }
    }

}
