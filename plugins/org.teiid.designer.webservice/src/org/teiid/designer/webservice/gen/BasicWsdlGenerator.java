/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice.gen;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.webservice.Input;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.webservice.Output;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServicePackage;
import org.teiid.designer.metamodels.webservice.impl.OperationImpl;
import org.teiid.designer.metamodels.wsdl.Binding;
import org.teiid.designer.metamodels.wsdl.BindingInput;
import org.teiid.designer.metamodels.wsdl.BindingOperation;
import org.teiid.designer.metamodels.wsdl.BindingOutput;
import org.teiid.designer.metamodels.wsdl.Definitions;
import org.teiid.designer.metamodels.wsdl.Documentation;
import org.teiid.designer.metamodels.wsdl.Documented;
import org.teiid.designer.metamodels.wsdl.Import;
import org.teiid.designer.metamodels.wsdl.MessagePart;
import org.teiid.designer.metamodels.wsdl.NamespaceDeclaration;
import org.teiid.designer.metamodels.wsdl.Operation;
import org.teiid.designer.metamodels.wsdl.Port;
import org.teiid.designer.metamodels.wsdl.PortType;
import org.teiid.designer.metamodels.wsdl.Service;
import org.teiid.designer.metamodels.wsdl.Types;
import org.teiid.designer.metamodels.wsdl.WsdlFactory;
import org.teiid.designer.metamodels.wsdl.WsdlNameRequiredEntity;
import org.teiid.designer.metamodels.wsdl.WsdlPackage;
import org.teiid.designer.metamodels.wsdl.io.WsdlResourceFactoryImpl;
import org.teiid.designer.metamodels.wsdl.io.WsdlWriter;
import org.teiid.designer.metamodels.wsdl.soap.SoapAddress;
import org.teiid.designer.metamodels.wsdl.soap.SoapBinding;
import org.teiid.designer.metamodels.wsdl.soap.SoapBody;
import org.teiid.designer.metamodels.wsdl.soap.SoapFactory;
import org.teiid.designer.metamodels.wsdl.soap.SoapOperation;
import org.teiid.designer.metamodels.wsdl.soap.SoapPackage;
import org.teiid.designer.metamodels.wsdl.soap.SoapStyleType;
import org.teiid.designer.metamodels.wsdl.soap.SoapUseType;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.metamodels.xml.XmlRoot;
import org.teiid.designer.webservice.IWsdlGenerator;
import org.teiid.designer.webservice.WebServicePlugin;


/**
 * Basic implementation of the {@link org.teiid.designer.webservice.IWsdlGenerator} interface.
 * 
 * @since 8.0
 */
public class BasicWsdlGenerator implements IWsdlGenerator {

    private static final String XML_LITERAL_TYPE_URI_STRING = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI).appendFragment(DatatypeConstants.BuiltInNames.XML_LITERAL).toString();

    public static final String XSD_SCHEMA_FOR_SCHEMA_TARGET_NAMESPACE = XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001;
    public static final String XSD_SCHEMA_FOR_SCHEMA_PREFIX = XSDPackage.eNS_PREFIX;

    private Map operationToProcedureMap;

    private final List ports;
    private final List webServiceModels;
    private final List xmlSchemas;
    private final List webServiceModelsUnmodifiable;
    private final List xmlSchemasUnmodifiable;
    private final Resource wsdlResource;
    private final Resource.Factory wsdlResourceFactory;
    private final URI wsdlResourceUri;
    private final int wsdlVersion = WSDL_VERSION_DEFAULT;
    private final Map xmlSchemasToLocationPath;
    private String name;
    private String targetNamespace;
    private String urlRootForReferences;
    private String urlSuffixForReferences;
    private String urlForService;
    private String defaultNamespaceUri;
    private String xmlEncoding = WsdlWriter.ENCODING_UTF8;

    public static final int WSDL_VERSION_1_1 = 1;
    public static final int WSDL_VERSION_2_0 = 2;
    public static final int WSDL_VERSION_DEFAULT = WSDL_VERSION_1_1;

    public static final int ERROR_NO_WEB_SERVICES = 21301;
    public static final int MULTIPLE_MESSAGES = 21302;
    public static final int ERROR_NO_WEB_SERVICE_OBJECTS = 21303;
    public static final int INVALID_URL = 21304;

    public static final String DEFAULT_URI = "GeneratedWsdl.wsdl"; //$NON-NLS-1$

    public BasicWsdlGenerator() {
        this(URI.createURI(DEFAULT_URI));
    }

    public BasicWsdlGenerator( final URI uri ) {
        CoreArgCheck.isNotNull(uri);

        this.operationToProcedureMap = new HashMap();
        this.webServiceModels = new ArrayList();
        this.ports = new ArrayList();
        this.xmlSchemas = new ArrayList();
        this.xmlSchemasToLocationPath = new HashMap();
        this.webServiceModelsUnmodifiable = Collections.unmodifiableList(this.webServiceModels);
        this.xmlSchemasUnmodifiable = Collections.unmodifiableList(this.xmlSchemas);

        // Create the WSDL resource ...
        this.wsdlResourceUri = uri;
        this.wsdlResourceFactory = new WsdlResourceFactoryImpl();
        this.wsdlResource = wsdlResourceFactory.createResource(this.wsdlResourceUri);

        // Set the initial default ...
        this.defaultNamespaceUri = INITIAL_DEFAULT_NAMESPACE_URI;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getName()
     * @since 4.2
     */
    @Override
	public String getName() {
        return name;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getTargetNamespace()
     * @since 4.2
     */
    @Override
	public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#setName(java.lang.String)
     * @since 4.2
     */
    @Override
	public void setName( String name ) {
        this.name = name;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#setTargetNamespace(java.lang.String)
     * @since 4.2
     */
    @Override
	public void setTargetNamespace( String targetNamspace ) {
        this.targetNamespace = targetNamspace;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getUrlRootForReferences()
     * @since 4.2
     */
    @Override
	public String getUrlRootForReferences() {
        return urlRootForReferences;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#setUrlRootForReferences(java.lang.String)
     * @since 4.2
     */
    @Override
	public void setUrlRootForReferences( String url ) {
        this.urlRootForReferences = url;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getUrlSuffixForReferences()
     * @since 4.2
     */
    @Override
	public String getUrlSuffixForReferences() {
        return this.urlSuffixForReferences;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#setUrlSuffixForReferences(java.lang.String)
     * @since 4.2
     */
    @Override
	public void setUrlSuffixForReferences( String suffix ) {
        this.urlSuffixForReferences = suffix;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getUrlForWsdlService()
     * @since 4.2
     */
    @Override
	public String getUrlForWsdlService() {
        return this.urlForService;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#setUrlForWsdlService(java.lang.String)
     * @since 4.2
     */
    @Override
	public void setUrlForWsdlService( final String serviceUrl ) {
        this.urlForService = serviceUrl;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getDefaultNamespaceUri()
     * @since 4.2
     */
    @Override
	public String getDefaultNamespaceUri() {
        return this.defaultNamespaceUri;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#setDefaultNamespaceUri(java.lang.String)
     * @since 4.2
     */
    @Override
	public void setDefaultNamespaceUri( String namespaceUri ) {
        this.defaultNamespaceUri = namespaceUri;
    }

    /**
     * @return ports
     */
    public List getPorts() {
        return ports;
    }

    /**
     * @return operationToProcedureMap
     */
    public Map getOperationToProcedureMap() {
        return operationToProcedureMap;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getXmlEncoding()
     * @since 4.2
     */
    @Override
	public String getXmlEncoding() {
        return this.xmlEncoding;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#setXmlEncoding(java.lang.String)
     * @since 4.2
     */
    @Override
	public void setXmlEncoding( String xmlEncoding ) {
        if (xmlEncoding == null || xmlEncoding.trim().length() == 0) {
            this.xmlEncoding = WsdlWriter.ENCODING_UTF8;
        } else {
            this.xmlEncoding = xmlEncoding;
        }
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#addWebServiceModel(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    @Override
	public boolean addWebServiceModel( Resource resource ) {
        CoreArgCheck.isNotNull(resource);

        // See if already added ...
        if (this.webServiceModels.contains(resource)) {
            return false;
        }

        // Check whether it is a valid model, and add only if it is ...
        final boolean valid = (isValidWebServiceModel(resource));
        if (valid) {
            this.webServiceModels.add(resource);
        }
        return valid;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#addXsdModel(org.eclipse.xsd.XSDSchema)
     * @since 4.2
     */
    @Override
	public boolean addXsdModel( XSDSchema xmlSchema,
                                final IPath pathForLocation ) {
        CoreArgCheck.isNotNull(xmlSchema);

        // See if already added ...
        if (this.xmlSchemas.contains(xmlSchema)) {
            return false;
        }

        this.xmlSchemas.add(xmlSchema);
        this.xmlSchemasToLocationPath.put(xmlSchema, pathForLocation);
        return true;
    }

    public IPath getLocationPathForXsdModel( XSDSchema xmlSchema ) {
        return (IPath)this.xmlSchemasToLocationPath.get(xmlSchema);
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getWebServiceModels()
     * @since 4.2
     */
    @Override
	public List getWebServiceModels() {
        return this.webServiceModelsUnmodifiable;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#getXSDSchemas()
     * @since 4.2
     */
    @Override
	public List getXSDSchemas() {
        return this.xmlSchemasUnmodifiable;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#generate(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    @Override
	public IStatus generate( IProgressMonitor monitor ) {
        // See if there are any web service models ...
        if (this.webServiceModels.isEmpty()) {
            final String msg = WebServicePlugin.Util.getString("BasicWsdlGenerator.NoWebServiceModelsSupplied"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, ERROR_NO_WEB_SERVICES, msg, null);
        }

        // Remove all existing objects from the existing resource ...
        this.wsdlResource.getContents().clear();

        // Delegate to the internal method ...
        final List problems = new ArrayList();
        doGenerate(monitor, this.wsdlResource, problems);

        // Construct the resulting MultiStatus ...
        IStatus result = null;
        if (problems.size() == 0) {
            final String msg = WebServicePlugin.Util.getString("BasicWsdlGenerator.NoErrorsOrWarningsWhileGenerating"); //$NON-NLS-1$
            result = new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, msg, null);
        } else if (problems.size() == 1) {
            result = (IStatus)problems.get(0);
        } else {
            // Iterate through the problems to see the worst case ...
            int errors = 0;
            int warnings = 0;
            final Iterator firstPastIter = problems.iterator();
            while (firstPastIter.hasNext()) {
                final IStatus problem = (IStatus)firstPastIter.next();
                if (problem.getSeverity() == IStatus.ERROR) {
                    ++errors;
                }
                if (problem.getSeverity() == IStatus.WARNING) {
                    ++warnings;
                }
            }

            // Create the MultiStatus ...
            String msg = null;
            if (errors != 0 || warnings != 0) {
                final Object[] params = new Object[] {new Integer(errors), new Integer(warnings)};
                msg = WebServicePlugin.Util.getString("BasicWsdlGenerator.ErrorsAndWarningsWhileGenerating", params); //$NON-NLS-1$
            } else {
                msg = WebServicePlugin.Util.getString("BasicWsdlGenerator.NoErrorsOrWarningsWhileGenerating"); //$NON-NLS-1$
            }
            final IStatus[] children = (IStatus[])problems.toArray(new IStatus[problems.size()]);
            final MultiStatus multi = new MultiStatus(WebServicePlugin.PLUGIN_ID, MULTIPLE_MESSAGES, children, msg, null);
            result = multi;
        }

        return result;
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#write(java.io.OutputStream)
     * @since 4.2
     */
    @Override
	public void write( OutputStream stream ) throws IOException {
        if (this.wsdlResource != null && this.wsdlResource.getContents().size() != 0) {
            // Write out the resource to the supplied stream ...
            final Map options = new HashMap();
            options.put(XMLResource.OPTION_ENCODING, xmlEncoding);
            this.wsdlResource.save(stream, options);
        }
    }

    /**
     * @see org.teiid.designer.webservice.IWsdlGenerator#close()
     * @since 4.2
     */
    @Override
	public void close() {
        this.webServiceModels.clear();
        this.xmlSchemas.clear();
    }

    // --------------------------------------------------------------------------------------------------------
    // OVERRIDABLE METHODS
    // --------------------------------------------------------------------------------------------------------

    protected IStatus doCheckValidUri( final String location ) {
        try {
            new java.net.URI(location);
        } catch (URISyntaxException err) {
            final IStatus problem = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, INVALID_URL, err.getLocalizedMessage(),
                                               null);
            return problem;
        }
        return null;
    }

    protected boolean isValidWebServiceModel( Resource resource ) {
        // Look for root-level WebService objects ...
        final Iterator iter = resource.getContents().iterator();
        while (iter.hasNext()) {
            final EObject root = (EObject)iter.next();
            final EClass eclass = root.eClass();
            if (eclass != null) {
                final EPackage epackage = eclass.getEPackage();
                if (WebServicePackage.eINSTANCE.equals(epackage)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Generate the WSDL into the WSDL resource. This method may be overridden to supply specific behavior
     * 
     * @param monitor
     * @param wsdlResource
     * @param problems
     * @since 4.2
     */
    protected void doGenerate( final IProgressMonitor monitor,
                               final Resource wsdlResource,
                               final List problems ) {
        // Obtain the list of all root-level WebService objects ...
        final List rootWsObjects = new ArrayList();
        final Iterator iter = this.webServiceModels.iterator();
        while (iter.hasNext()) {
            final Resource webServiceModel = (Resource)iter.next();
            final List roots = webServiceModel.getContents();
            final Iterator rootIter = roots.iterator();
            while (rootIter.hasNext()) {
                final EObject root = (EObject)rootIter.next();
                final EClass eclass = root.eClass();
                if (eclass != null) {
                    final EPackage epackage = eclass.getEPackage();
                    if (WebServicePackage.eINSTANCE.equals(epackage)) {
                        rootWsObjects.add(root);
                    }
                }
            }
        }

        if (rootWsObjects.isEmpty()) {
            // No objects were found ...
            final Object[] params = new Object[] {new Integer(this.webServiceModels.size())};
            final String msg = WebServicePlugin.Util.getString("BasicWsdlGenerator.NoWebServiceObjectsFound", params); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, ERROR_NO_WEB_SERVICE_OBJECTS, msg, null));
        } else {
            // At least some objects were found ...

            // Create the correct visitor for the desired version ...
            Wsdl11Generator visitor = null;
            final String urlRoot = this.getUrlRootForReferences();
            final String urlSuffix = this.getUrlSuffixForReferences();
            final String urlService = this.getUrlForWsdlService();
            final String defNsUri = this.getDefaultNamespaceUri();
            switch (this.wsdlVersion) {
                case WSDL_VERSION_2_0: {
                    visitor = new Wsdl20Generator(wsdlResource, getName(), getTargetNamespace(), defNsUri, urlRoot, urlSuffix,
                                                  urlService, problems);
                    break;
                }
                default: {
                    visitor = new Wsdl11Generator(wsdlResource, getName(), getTargetNamespace(), defNsUri, urlRoot, urlSuffix,
                                                  urlService, problems);
                }
            }

            // generate namespaces first so that they are available when generating the other WSDL elements
            visitor.doGenerateStandardNamespaceDeclarations();

            // Walk the objects and use the generator ...
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
            try {
                processor.walk(rootWsObjects, ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException e) {
                // handle the exception
            }

            visitor.doGenerateTargetNamespaceNamespaceDeclaration();

            operationToProcedureMap = visitor.operationToProcedureMap;

            // Signal the generator to complete the WSDL (adding any binding information, etc.)
            visitor.complete();
        }
    }

    // ========================================================================================================
    // WSDL 1.1 Generator
    // ========================================================================================================

    protected class Wsdl11Generator implements ModelVisitor { // extends WebServiceSwitch implements ModelVisitor {
        private final List problems;
        protected final Resource resource;
        protected final ModelContents contents;
        private final WsdlFactory factory;
        private final SoapFactory soapFactory;
        private final XSDFactory xsdFactory;
        final Map operationToProcedureMap;
        private Definitions definitions; // the one created ...
        private Service service; // the one created ...
        private PortType portType; // the last one created ...
        private Operation operation; // the last one created ...
        private Binding binding; // the last one created ...
        private BindingOperation bindingOp; // the last one created ...
        private final Set referencedXsds;
        private final Map xsdPrefixByTargetNamespace;
        private int schemaIndex;
        private final String urlRoot;
        private final String urlSuffix;
        private final String urlService;
        private final Map messagePartNameByWebServiceMessage;
        private String wsdlTnsPrefix = "tns"; //$NON-NLS-1$
        private final String name;
        private final String targetNamespace;
        private final String defaultNamespace;
        private boolean xsdImportsAsWsdlImports = false;
        private final Set namespacePrefixes = new HashSet();

        protected Wsdl11Generator( final Resource wsdlResource,
                                   final String name,
                                   final String targetNamespace,
                                   final String defaultNamespace,
                                   final String urlRoot,
                                   final String urlSuffix,
                                   final String urlService,
                                   final List problems ) {

            this.name = name;
            this.targetNamespace = targetNamespace;
            this.problems = problems;
            this.resource = wsdlResource;
            this.factory = WsdlFactory.eINSTANCE;
            this.soapFactory = SoapFactory.eINSTANCE;
            this.xsdFactory = XSDFactory.eINSTANCE;
            this.contents = new ModelContents(this.resource);
            this.referencedXsds = new HashSet();
            this.xsdPrefixByTargetNamespace = new HashMap();
            this.urlRoot = urlRoot;
            this.urlSuffix = urlSuffix;
            this.urlService = urlService;
            this.messagePartNameByWebServiceMessage = new HashMap();
            this.defaultNamespace = defaultNamespace;
            this.operationToProcedureMap = new HashMap<String, String>();
        }

        public Definitions getDefinitions() {
            if (this.definitions == null) {
                // Create the root definitions ...
                this.definitions = this.factory.createDefinitions();
                this.definitions.setName(this.name);
                this.definitions.setTargetNamespace(this.targetNamespace);
                this.resource.getContents().add(this.definitions);

                // Add the service ...
                this.service = this.factory.createService();
                this.service.setName(this.name);
                this.service.setDefinitions(this.definitions);
            }
            return this.definitions;
        }

        protected void addError( final int code,
                                 final String msg,
                                 final Throwable t ) {
            this.problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, code, msg, t));
        }

        protected void addWarning( final int code,
                                   final String msg,
                                   final Throwable t ) {
            this.problems.add(new Status(IStatus.WARNING, WebServicePlugin.PLUGIN_ID, code, msg, t));
        }

        @Override
		public boolean visit( EObject object ) {
            doSwitch(object);
            return true;
        }

        @Override
		public boolean visit( Resource resource ) {
            return true;
        }

        protected String getDescription( EObject eObj ) {
            Annotation annotation = null;
            final Resource eResource = eObj.eResource();
            if (eResource instanceof EmfResource) {
                final ModelContents contents = ((EmfResource)eResource).getModelContents();
                annotation = contents.getAnnotation(eObj);
            }
            if (annotation == null) {
                return null;
            }
            final String desc = annotation.getDescription();
            return desc;
        }

        protected void addDocumentation( WebServiceComponent component,
                                         final Documented documented ) {
            final String desc = getDescription(component);
            addDocumentation(desc, documented);
        }

        protected void addDocumentation( String docText,
                                         final Documented documented ) {
            if (docText != null && docText.trim().length() != 0) {
                // Create a documentation under the documented ...
                final Documentation doc = this.factory.createDocumentation();
                doc.setTextContent(docText);
                doc.setDocumented(documented);
            }
        }

        /**
         * Obtain the identifier by which the WSDL references the supplied XSD component.
         * 
         * @param comp the named component
         * @return
         * @since 4.2
         */
        protected String getXsdComponentIdentifier( final XSDNamedComponent comp ) {
            final String name = comp.getName();
            final String targetNsUri = comp.getTargetNamespace();
            if (isDefaultNamespace(targetNsUri)) {
                return name;
            }
            final String prefix = (String)this.xsdPrefixByTargetNamespace.get(targetNsUri);
            return prefix + ':' + name;
        }

        protected boolean isDefaultNamespace( final String namespaceUri ) {
            if (this.defaultNamespace != null && this.defaultNamespace.equals(namespaceUri)) {
                return true;
            }
            return false;
        }

        protected void addReference( final XSDComponent xsdComp ) {
            final XSDSchema schema = xsdComp.getSchema();
            final boolean added = schema != null && this.referencedXsds.add(schema);
            if (added) {
                // Add a prefix ...
                final String targetNamespace = schema.getTargetNamespace();

                // See if it's the default ...
                if (isDefaultNamespace(targetNamespace)) {
                    this.xsdPrefixByTargetNamespace.put(targetNamespace, null);
                } else {
                    String prefix = "schema" + ++schemaIndex; //$NON-NLS-1$
                    if (this.xsdPrefixByTargetNamespace.put(targetNamespace, prefix) != null) {
                        String msg = WebServicePlugin.Util.getString("BasicWsdlGenerator.NonUniqueNamespace", targetNamespace); //$NON-NLS-1$
                        addError(IStatus.ERROR, msg, null);
                    }
                }
            }
        }


        private void addToOperationToProcedureMap( final EObject object ) {
            final IPath path = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(object);
            final StringBuffer sb = new StringBuffer();
            final String[] segments = path.segments();
            for (int i = 0; i < segments.length; i++) {
                if (i != 0) {
                    sb.append('.');
                }
                final String segment = segments[i];
                sb.append(segment);
            }

            this.operationToProcedureMap.put(((OperationImpl)object).getName(), sb.toString());
        }

        protected String doGetFullyQualifiedName( final WsdlNameRequiredEntity object ) {
            if (this.isDefaultNamespace(this.targetNamespace)) {
                // Not qualified ...
                return object.getName();
            }

            // Otherwise, prefix it ...
            return this.wsdlTnsPrefix + ':' + object.getName();
        }

        protected void addNamespaceDeclaration( final String prefix,
                                                final String namespaceUri ) {
            final NamespaceDeclaration decl = this.factory.createNamespaceDeclaration();

            this.namespacePrefixes.add(prefix);
            this.xsdPrefixByTargetNamespace.put(namespaceUri, prefix);
            // Add the namespace declaration ...
            if (!isDefaultNamespace(namespaceUri)) {
                decl.setPrefix(prefix);
            }
            decl.setUri(namespaceUri);
            decl.setOwner(getDefinitions());
        }

        protected void doGenerateImports() {
            // Add (in sorted order) the details for each referenced XSD ...
            final List refedXsds = new LinkedList(this.referencedXsds);
            Collections.sort(refedXsds, new XsdComparator());
            final Iterator iter = refedXsds.iterator();
            while (iter.hasNext()) {
                final XSDSchema xsd = (XSDSchema)iter.next();
                final String namespaceUri = xsd.getTargetNamespace();
                String prefix = (String)this.xsdPrefixByTargetNamespace.get(namespaceUri);

                // Compute the location ...
                String location = null;
                final IPath xsdPath = BasicWsdlGenerator.this.getLocationPathForXsdModel(xsd);
                if (xsdPath != null) {
                    location = xsdPath.toString();
                } else {
                    final Resource xsdResource = xsd.eResource();
                    if (xsdResource != null) {
                        final URI xsdUri = xsd.eResource().getURI();
                        location = xsdUri.lastSegment();
                    }
                }

                // Add the tokens ...
                if (urlRoot != null) {
                    location = urlRoot + location;
                }

                if (urlSuffix != null) {
                    location = location + urlSuffix;
                }

                // Check that the URL location is a valid URI ...
                final IStatus problem = doCheckValidUri(location);
                if (problem != null) {
                    problems.add(problem);
                }

                // Add the namespace declaration to the WSDL file (since the namespaces could be used in values) ...
                addNamespaceDeclaration(prefix, namespaceUri);

                // Construct the import
                if (xsdImportsAsWsdlImports) {

                    // Add the import as a WSDL import ...
                    final Import importObj = this.factory.createImport();
                    importObj.setNamespace(namespaceUri);
                    importObj.setLocation(location);
                    importObj.setDefinitions(getDefinitions());
                } else {

                    // Per the WS-I spec, schema imports should be done in
                    // the wsdl:types section within the context of a xsd:schema element
                    // Add the import as XSD imports ...
                    final XSDSchema nestedSchema = getSchemaInTypes(true);

                    if (namespaceUri != null && namespaceUri.trim().length() != 0) {

                        // Create an import ...
                        final XSDImport xsdImport = this.xsdFactory.createXSDImport();
                        xsdImport.setNamespace(namespaceUri);
                        xsdImport.setSchemaLocation(location);
                        nestedSchema.getContents().add(xsdImport);

                        // Add the XML Namespace declaration for the imported namespace ...
                        // (Skip this for now, since it really isn't needed)
                        // Map qNamePrefixToNamespaceMap = nestedSchema.getQNamePrefixToNamespaceMap();
                        // qNamePrefixToNamespaceMap.put(prefix,namespaceUri);
                    } else {
                        // The namespace URI of the referenced XSD is null, meaning its global
                        // Therefore, must include these!

                        // Create an include ...
                        final XSDInclude xsdInclude = this.xsdFactory.createXSDInclude();
                        xsdInclude.setSchemaLocation(location);
                        nestedSchema.getContents().add(xsdInclude);
                    }
                }
            }

        }

        protected Types getTypes( boolean createIfRequired ) {
            Types types = getDefinitions().getTypes();
            if (types == null && createIfRequired) {
                types = this.factory.createTypes();
                types.setDefinitions(getDefinitions());
            }
            return types;
        }

        protected XSDSchema getSchemaInTypes( boolean createIfRequired ) {
            XSDSchema schema = null;
            final Types types = getTypes(createIfRequired);
            if (types != null) {
                final List schemas = types.getSchemas();
                if (schemas.isEmpty()) {
                    if (createIfRequired) {
                        schema = this.xsdFactory.createXSDSchema();
                        // Initialize the schema ...
                        schema.setSchemaForSchemaQNamePrefix("xsd"); //$NON-NLS-1$
                        schema.setTargetNamespace(types.getDefinitions().getTargetNamespace());

                        Map qNamePrefixToNamespaceMap = schema.getQNamePrefixToNamespaceMap();
                        qNamePrefixToNamespaceMap.put(schema.getSchemaForSchemaQNamePrefix(),
                                                      XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
                        qNamePrefixToNamespaceMap.put(WsdlPackage.eNS_PREFIX, WsdlPackage.eNS_URI);

                        // Add the schema to the 'types' object ...
                        types.getSchemas().add(schema);
                    }
                } else {
                    schema = (XSDSchema)types.getSchemas().get(0);
                }
            }
            return schema;
        }

        protected class XsdComparator implements Comparator {
            @Override
			public int compare( Object o1,
                                Object o2 ) {
                if (o1 instanceof XSDSchema && o2 instanceof XSDSchema) {
                    final String tn1 = ((XSDSchema)o1).getTargetNamespace();
                    final String tn2 = ((XSDSchema)o2).getTargetNamespace();
                    if (tn1 == null && tn2 == null) {
                        return 0;
                    } else if (tn1 == null) {
                        return -1;
                    } else if (tn2 == null) {
                        return 1;
                    } else {
                        return tn1.compareTo(tn2);
                    }
                }
                return 0;
            }
        }

        protected void doGenerateStandardNamespaceDeclarations() {
            // Add the namespace declarations for WSDL, SOAP and XSD ...
            addNamespaceDeclaration(XSD_SCHEMA_FOR_SCHEMA_PREFIX, XSD_SCHEMA_FOR_SCHEMA_TARGET_NAMESPACE);
            addNamespaceDeclaration(WsdlPackage.eNS_PREFIX, WsdlPackage.eNS_URI);
            addNamespaceDeclaration(SoapPackage.eNS_PREFIX, SoapPackage.eNS_URI);
        }

        protected void doGenerateTargetNamespaceNamespaceDeclaration() {

            if (this.xsdPrefixByTargetNamespace.put(this.targetNamespace, this.wsdlTnsPrefix) != null) {
                String msg = WebServicePlugin.Util.getString("BasicWsdlGenerator.NonUniqueWSDLNamespace", targetNamespace); //$NON-NLS-1$
                addError(IStatus.ERROR, msg, null);
            }
            addNamespaceDeclaration(this.wsdlTnsPrefix, this.targetNamespace);
        }

        /**
         * Signals the generator to add the bindings, etc.
         * 
         * @since 4.2
         */
        public void complete() {
            // Generate the imports to XSDs ...
            doGenerateImports();
        }

        // --------------------------------------------------------------------------------------------------------
        // Xml/Web Service Switches
        // --------------------------------------------------------------------------------------------------------

        protected Object doSwitch( EObject theEObject ) {
            return doSwitch(theEObject.eClass(), theEObject);
        }

        protected Object doSwitch( EClass theEClass,
                                   EObject theEObject ) {
            if (theEClass.eContainer() == WebServicePackage.eINSTANCE) {
                return doWebServiceSwitch(theEClass.getClassifierID(), theEObject);
            }
            List eSuperTypes = theEClass.getESuperTypes();
            return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch((EClass)eSuperTypes.get(0), theEObject);
        }

        protected Object doWebServiceSwitch( int classifierID,
                                             EObject theEObject ) {
            switch (classifierID) {
                case WebServicePackage.OPERATION: {
                    org.teiid.designer.metamodels.webservice.Operation operation = (org.teiid.designer.metamodels.webservice.Operation)theEObject;
                    Object result = caseOperation(operation);
                    return result;
                }
                case WebServicePackage.INPUT: {
                    Input input = (Input)theEObject;
                    Object result = caseInput(input);
                    return result;
                }
                case WebServicePackage.OUTPUT: {
                    Output output = (Output)theEObject;
                    Object result = caseOutput(output);
                    return result;
                }
                case WebServicePackage.INTERFACE: {
                    Interface interface_ = (Interface)theEObject;
                    Object result = caseInterface(interface_);
                    return result;
                }
                default:
                    return defaultCase(theEObject);
            }
        }


        // --------------------------------------------------------------------------------------------------------
        // Web Service Processing Methods
        // --------------------------------------------------------------------------------------------------------

        public Object defaultCase( EObject object ) {
            return null;
        }

        /**
         * @see org.teiid.designer.metamodels.webservice.util.WebServiceSwitch#caseInterface(org.teiid.designer.metamodels.webservice.Interface)
         * @since 4.2
         */
        public Object caseInterface( Interface object ) {
            final String interfaceName = object.getName();
            ports.add(interfaceName);

            // Create a WSDL port type
            this.portType = this.factory.createPortType();
            this.portType.setName(interfaceName);
            this.portType.setDefinitions(this.getDefinitions()); // adds to parent

            // Add the documentation, if it exists ...
            addDocumentation(object, this.portType);

            // --------------------------------------------------------------------------------------------------------
            // Binding information
            // --------------------------------------------------------------------------------------------------------
            final String bindingType = doGetFullyQualifiedName(this.portType);

            // Add a binding for this interface ...
            this.binding = this.factory.createBinding();
            this.binding.setName(interfaceName);
            this.binding.setType(bindingType);
            this.binding.setDefinitions(getDefinitions());

            // Add the SOAP binding ...
            final SoapBinding soapBinding = this.soapFactory.createSoapBinding();
            soapBinding.setStyle(SoapStyleType.DOCUMENT_LITERAL);
            soapBinding.setTransport("http://schemas.xmlsoap.org/soap/http"); //$NON-NLS-1$
            this.binding.setSoapBinding(soapBinding);

            // --------------------------------------------------------------------------------------------------------
            // Port information
            // --------------------------------------------------------------------------------------------------------
            final String portTypeName = this.doGetFullyQualifiedName(this.portType);

            final Port port = this.factory.createPort();
            port.setName(interfaceName);
            port.setBinding(portTypeName);
            port.setService(this.service);

            String serviceUrlWithPortName = service.getName();
            // Check that the URL location is a valid URI ...
            if (this.urlService != null) {
                serviceUrlWithPortName = this.urlService + port.getName();
                final IStatus problem = doCheckValidUri(serviceUrlWithPortName);
                if (problem != null) {
                    problems.add(problem);
                }
            }

            // Add the SOAP address ...
            final SoapAddress address = this.soapFactory.createSoapAddress();
            address.setLocation(serviceUrlWithPortName);
            address.setPort(port);

            return object; // this method handled it, so return non-null
        }

        /**
         * @see org.teiid.designer.metamodels.webservice.util.WebServiceSwitch#caseOperation(org.teiid.designer.metamodels.webservice.Operation)
         * @since 4.2
         */
        public Object caseOperation( org.teiid.designer.metamodels.webservice.Operation object ) {
            final String opName = object.getName();

            // Create a WSDL operation
            this.operation = this.factory.createOperation();
            this.operation.setName(opName);
            this.operation.setPortType(this.portType); // adds to parent

            // Add the documentation, if it exists ...
            addDocumentation(object, this.operation);

            // Add the binding information ...
            this.bindingOp = this.factory.createBindingOperation();
            this.bindingOp.setName(opName);
            this.bindingOp.setBinding(this.binding);

            // Add the SOAP operation information ...
            final SoapOperation soapOp = this.soapFactory.createSoapOperation();
            final String action = CoreStringUtil.Constants.EMPTY_STRING;
            soapOp.setAction(action);
            addToOperationToProcedureMap(object);
            soapOp.setStyle(SoapStyleType.DOCUMENT_LITERAL);
            soapOp.setBindingOperation(this.bindingOp);

            // If the web service Operation we are processing has an Output but no corresponding Input
            // then we need to create a dummy WSDL Input/Message construct to make it WS-I compliant
            // (see defect 20554)
            if (object.getInput() == null) {
                createDummyInput();
            }

            return object; // this method handled it, so return non-null
        }

        /**
         * @see org.teiid.designer.metamodels.webservice.util.WebServiceSwitch#caseInput(org.teiid.designer.metamodels.webservice.Input)
         * @since 4.2
         */
        public Object caseInput( Input object ) {
            // Create a WSDL input ...
            final org.teiid.designer.metamodels.wsdl.Input input = this.factory.createInput();

            // Set the name on the input ...
            String inputName = object.getName();
            if (inputName == null || inputName.trim().length() == 0) {
                inputName = WebServicePlugin.Util.getString("BasicWsdlGenerator.DefaultInputName"); //$NON-NLS-1$
            }
            // Defect 22988 - Removed optional attribute "name" since it can cause problems
            // generating some clients if names are duplicated.
            // input.setName(inputName);

            // Create the corresponding message ...
            final Object[] params = new Object[] {this.portType.getName(), this.operation.getName(), inputName};
            final String msgName = WebServicePlugin.Util.getString("BasicWsdlGenerator.InputMessageName_PortTypeName_OperationName_InputName", params); //$NON-NLS-1$
            final org.teiid.designer.metamodels.wsdl.Message message = this.factory.createMessage();
            message.setName(msgName);
            message.setDefinitions(getDefinitions()); // adds to parent

            // And the message part ...
            final MessagePart part = this.factory.createMessagePart();
            final XSDElementDeclaration xsdElement = object.getContentElement();
            final XSDComplexTypeDefinition xsdComplexType = object.getContentComplexType();
            final XSDSimpleTypeDefinition xsdSimpleType = object.getContentSimpleType();
            if (xsdElement != null) {
                this.addReference(xsdElement);
                final String value = getXsdComponentIdentifier(xsdElement);
                part.setElement(value);
            } else if (xsdComplexType != null) {
                this.addReference(xsdComplexType);
                final String value = getXsdComponentIdentifier(xsdComplexType);
                part.setType(value);
            } else if (xsdSimpleType != null) {
                this.addReference(xsdSimpleType);
                final String value = getXsdComponentIdentifier(xsdSimpleType);
                part.setType(value);
            }
            part.setName(msgName);
            part.setMessage(message);

            // Set the message name on the input ...
            this.messagePartNameByWebServiceMessage.put(object, msgName);
            input.setMessage(this.doGetFullyQualifiedName(message));
            input.setOperation(this.operation); // adds to parent

            // Add the documentation, if it exists ...
            addDocumentation(object, input);

            // Add the documentation, if it exists ...
            String inputMsg = null;
            final String inputDesc = getDescription(object);
            if (inputDesc != null && inputDesc.trim().length() != 0) {
                final Object[] params2 = new Object[] {this.portType.getName(), this.operation.getName(), inputDesc};
                inputMsg = WebServicePlugin.Util.getString("BasicWsdlGenerator.InputMsgDesc_PortTypeName_OperationName_InputDesc", params2); //$NON-NLS-1$
            } else {
                final Object[] params2 = new Object[] {this.portType.getName(), this.operation.getName()};
                inputMsg = WebServicePlugin.Util.getString("BasicWsdlGenerator.InputMsgDesc_PortTypeName_OperationName", params2); //$NON-NLS-1$
            }
            addDocumentation(inputMsg, message);

            // Create the binding operation input ...
            final BindingInput bindingInput = this.factory.createBindingInput();
            bindingInput.setBindingOperation(this.bindingOp);
            // Defect 22988 - Removed optional attribute "name" since it can cause problems
            // generating some clients if names are duplicated.
            // bindingInput.setName(inputName);
            // must match portType/operation/input/@name
            final SoapBody soapBody = this.soapFactory.createSoapBody();
            // per Jim Poulsen this next line is not required. Also it doesn't need to be a qualified name.
            // soapBody.getParts().add(this.doGetFullyQualifiedName(message));
            soapBody.setUse(SoapUseType.LITERAL_LITERAL);
            bindingInput.setSoapBody(soapBody);

            return object; // this method handled it, so return non-null
        }

        /**
         * @see org.teiid.designer.metamodels.webservice.util.WebServiceSwitch#caseOutput(org.teiid.designer.metamodels.webservice.Output)
         * @since 4.2
         */
        public Object caseOutput( Output object ) {
            // Create a WSDL output ...
            final org.teiid.designer.metamodels.wsdl.Output output = this.factory.createOutput();

            // Set the name on the output ...
            String outputName = object.getName();
            if (outputName == null || outputName.trim().length() == 0) {
                outputName = WebServicePlugin.Util.getString("BasicWsdlGenerator.DefaultOutputName"); //$NON-NLS-1$
            }
            // Defect 22988 - Removed optional attribute "name" since it can cause problems
            // generating some clients if names are duplicated.
            // output.setName(outputName);

            // Create the corresponding message ...
            final Object[] params = new Object[] {this.portType.getName(), this.operation.getName(), outputName};
            final String msgName = WebServicePlugin.Util.getString("BasicWsdlGenerator.OutputMessageName_PortTypeName_OperationName_OutputName", params); //$NON-NLS-1$
            final org.teiid.designer.metamodels.wsdl.Message message = this.factory.createMessage();
            message.setName(msgName);
            message.setDefinitions(getDefinitions()); // adds to parent

            // And the message part ...
            final MessagePart part = this.factory.createMessagePart();
            XSDElementDeclaration xsdElement = object.getContentElement();
            if (xsdElement == null) {
                // There should be an element ref, but if not look for it via the XML document ref
                final XmlDocument xmlDoc = object.getXmlDocument();
                if (xmlDoc != null) {
                    final XmlRoot root = xmlDoc.getRoot();
                    if (root != null) {
                        final XSDComponent comp = root.getXsdComponent();
                        if (comp instanceof XSDElementDeclaration) {
                            xsdElement = (XSDElementDeclaration)comp;
                        }
                    }
                }
            }
            if (xsdElement != null) {
                this.addReference(xsdElement);
                final String value = getXsdComponentIdentifier(xsdElement);
                part.setElement(value);
            } else {
                final XSDComplexTypeDefinition xsdComplexType = object.getContentComplexType();
                final XSDSimpleTypeDefinition xsdSimpleType = object.getContentSimpleType();
                if (xsdComplexType != null) {
                    this.addReference(xsdComplexType);
                    final String value = getXsdComponentIdentifier(xsdComplexType);
                    part.setType(value);
                } else if (xsdSimpleType != null) {
                    this.addReference(xsdSimpleType);
                    final String value = getXsdComponentIdentifier(xsdSimpleType);
                    part.setType(value);
                }
            }
            part.setName(msgName);
            part.setMessage(message);

            // Set the message name on the input ...
            this.messagePartNameByWebServiceMessage.put(object, msgName);
            output.setMessage(this.doGetFullyQualifiedName(message));
            output.setOperation(this.operation); // adds to parent

            // Add the documentation, if it exists ...
            addDocumentation(object, output);

            // Add the documentation, if it exists ...
            String outputMsg = null;
            final String outputDesc = getDescription(object);
            if (outputDesc != null && outputDesc.trim().length() != 0) {
                final Object[] params2 = new Object[] {this.portType.getName(), this.operation.getName(), outputDesc};
                outputMsg = WebServicePlugin.Util.getString("BasicWsdlGenerator.OutputMsgDesc_PortTypeName_OperationName_OutputDesc", params2); //$NON-NLS-1$
            } else {
                final Object[] params2 = new Object[] {this.portType.getName(), this.operation.getName()};
                outputMsg = WebServicePlugin.Util.getString("BasicWsdlGenerator.OutputMsgDesc_PortTypeName_OperationName", params2); //$NON-NLS-1$
            }
            addDocumentation(outputMsg, message);

            // Create the binding operation input ...
            final BindingOutput bindingOutput = this.factory.createBindingOutput();
            bindingOutput.setBindingOperation(this.bindingOp);
            // Defect 22988 - Removed optional attribute "name" since it can cause problems
            // generating some clients if names are duplicated.
            // bindingOutput.setName(outputName); // must match portType/operation/output/@name
            final SoapBody soapBody = this.soapFactory.createSoapBody();
            // per Jim Poulsen this next line is not required. Also it doesn't need to be a qualified name.
            // soapBody.getParts().add(this.doGetFullyQualifiedName(message));
            soapBody.setUse(SoapUseType.LITERAL_LITERAL);
            bindingOutput.setSoapBody(soapBody);

            return object; // this method handled it, so return non-null
        }

        /**
         * Create a dummy Input/Message construct in the WSDL with no �parts�. This should be valid per the WS-I profile which
         * states that "Use of wsdl:message elements with zero parts is permitted in Document styles to permit operations that can
         * send or receive envelopes with empty soap:Bodys. Use of wsdl:message elements with zero parts is permitted in RPC
         * styles to permit operations that have no (zero) parameters and/or a return value". The dummy Input/Message construct is
         * to allow users to create web services that require no input message whatsoever.
         */
        protected void createDummyInput() {
            // Create a WSDL input ...
            final org.teiid.designer.metamodels.wsdl.Input input = this.factory.createInput();

            // Set the name on the input ...
            final String inputName = WebServicePlugin.Util.getString("BasicWsdlGenerator.DefaultInputName"); //$NON-NLS-1$
            // Defect 22988 - Removed optional attribute "name" since it can cause problems
            // generating some clients if names are duplicated.
            // input.setName(inputName);

            // Create the corresponding message ...
            final Object[] params = new Object[] {this.portType.getName(), this.operation.getName(), inputName};
            final String msgName = WebServicePlugin.Util.getString("BasicWsdlGenerator.InputMessageName_PortTypeName_OperationName_InputName", params); //$NON-NLS-1$
            final org.teiid.designer.metamodels.wsdl.Message message = this.factory.createMessage();
            message.setName(msgName);
            message.setDefinitions(getDefinitions()); // adds to parent

            // Set the message name on the input ...
            input.setMessage(this.doGetFullyQualifiedName(message));
            input.setOperation(this.operation); // adds to parent

            // Add the documentation, if it exists ...
            final Object[] params2 = new Object[] {this.portType.getName(), this.operation.getName()};
            final String inputMsg = WebServicePlugin.Util.getString("BasicWsdlGenerator.InputMsgDesc_PortTypeName_OperationName", params2); //$NON-NLS-1$
            addDocumentation(inputMsg, message);

            // Create the binding operation input ...
            final BindingInput bindingInput = this.factory.createBindingInput();
            bindingInput.setBindingOperation(this.bindingOp);
            // Defect 22988 - Removed optional attribute "name" since it can cause problems
            // generating some clients if names are duplicated.
            // bindingInput.setName(inputName); // must match portType/operation/input/@name
            final SoapBody soapBody = this.soapFactory.createSoapBody();
            soapBody.setUse(SoapUseType.LITERAL_LITERAL);
            bindingInput.setSoapBody(soapBody);
        }
    }

    // ========================================================================================================
    // WSDL 2.0 Generator
    // ========================================================================================================

    protected class Wsdl20Generator extends Wsdl11Generator {
        protected Wsdl20Generator( final Resource wsdlResource,
                                   final String name,
                                   final String targetNamespace,
                                   final String defaultNamespace,
                                   final String urlRoot,
                                   final String urlSuffix,
                                   final String urlService,
                                   final List problems ) {
            super(wsdlResource, name, targetNamespace, defaultNamespace, urlRoot, urlSuffix, urlService, problems);
        }
    }

}
