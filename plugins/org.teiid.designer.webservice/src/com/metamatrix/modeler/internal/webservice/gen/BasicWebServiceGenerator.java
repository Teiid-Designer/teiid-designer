/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.metamodels.webservice.WebServiceFactory;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.Message;
import com.metamatrix.metamodels.wsdl.MessagePart;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.ParamType;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.util.WsdlSwitch;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.util.NewModelObjectHelperManager;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.webservice.IWebServiceGenerator;
import com.metamatrix.modeler.webservice.WebServicePlugin;

/**
 * @since 4.2
 */
public class BasicWebServiceGenerator implements IWebServiceGenerator {

    private Resource webServiceResource;
    protected final List wsdlDefinitions;
    private final List xsdSchemas;
    Collection selectedWsdlOperations;

    /**
     * @since 4.2
     */
    public BasicWebServiceGenerator() {
        super();
        this.wsdlDefinitions = new ArrayList();
        this.xsdSchemas = new ArrayList();
        this.selectedWsdlOperations = new HashSet();
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#setWebServiceResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public void setWebServiceResource( Resource wsModel ) {
        CoreArgCheck.isNotNull(wsModel);
        this.webServiceResource = wsModel;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#getWebServiceResource()
     * @since 4.2
     */
    public Resource getWebServiceResource() {
        return this.webServiceResource;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#addWsdlDefinitions(com.metamatrix.metamodels.wsdl.Definitions)
     * @since 4.2
     */
    public void addWsdlDefinitions( final Definitions wsdlDefinitions ) {
        CoreArgCheck.isNotNull(wsdlDefinitions);
        if (!this.wsdlDefinitions.contains(wsdlDefinitions)) {
            this.wsdlDefinitions.add(wsdlDefinitions);
        }
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#addWsdlDefinitions(java.util.List)
     * @since 4.2
     */
    public void addWsdlDefinitions( List wsdlDefinitions ) {
        CoreArgCheck.isNotNull(wsdlDefinitions);
        final Iterator iter = wsdlDefinitions.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (obj instanceof Definitions) {
                addWsdlDefinitions((Definitions)obj);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#getWsdlDefinitions()
     * @since 4.2
     */
    public List getWsdlDefinitions() {
        return this.wsdlDefinitions;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#addXsdSchema(org.eclipse.xsd.XSDSchema)
     * @since 4.2
     */
    public void addXsdSchema( XSDSchema schema ) {
        CoreArgCheck.isNotNull(schema);
        if (!this.xsdSchemas.contains(schema)) {
            this.xsdSchemas.add(schema);
        }
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#addXsdSchemas(java.util.List)
     * @since 4.2
     */
    public void addXsdSchemas( List schemas ) {
        CoreArgCheck.isNotNull(schemas);
        final Iterator iter = schemas.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (obj instanceof XSDSchema) addXsdSchema((XSDSchema)obj);
        }
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#getXsdSchemas()
     * @since 4.2
     */
    public List getXsdSchemas() {
        return this.xsdSchemas;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#setSelectedOperations(java.util.Collection)
     * @since 5.0
     */
    public void setSelectedOperations( Collection operations ) {
        this.selectedWsdlOperations = operations;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#getSelectedOperations()
     * @since 5.0
     */
    public Collection getSelectedOperations() {
        return this.selectedWsdlOperations;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#generate(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public IStatus generate( IProgressMonitor monitor ) {
        final List problems = new ArrayList();
        if (this.getWebServiceResource() == null) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceGenerator.NoWebServiceModelSpecified"); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, msg, null));
        } else {
            doGenerate(monitor, problems);
        }

        // Convert the problems to a status
        IStatus result = null;
        if (problems.size() == 0) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceGenerator.GenerationOfWebServiceModelSucceeded"); //$NON-NLS-1$
            result = new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, msg, null);
        } else if (problems.size() == 1) {
            result = (IStatus)problems.get(0);
        } else {
            // Iterate over the problems and find the number of warnings and errors ...
            int errors = 0;
            int warnings = 0;
            final Iterator iter = problems.iterator();
            while (iter.hasNext()) {
                final IStatus status = (IStatus)iter.next();
                switch (status.getSeverity()) {
                    case IStatus.ERROR: {
                        ++errors;
                        break;
                    }
                    case IStatus.WARNING: {
                        ++warnings;
                        break;
                    }
                }
            }

            final Object[] params = new Object[] {new Integer(errors), new Integer(warnings)};
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceGenerator.GeneratedWebServiceModelWithErrorsAndWarnings", params); //$NON-NLS-1$
            final IStatus[] children = (IStatus[])problems.toArray(new IStatus[problems.size()]);
            result = new MultiStatus(WebServicePlugin.PLUGIN_ID, 0, children, msg, null);
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceGenerator#generate(org.eclipse.core.runtime.IProgressMonitor,
     *      java.util.List)
     * @since 4.2
     */
    public void generate( IProgressMonitor monitor,
                          List problems ) {
        doGenerate(monitor, problems);
    }

    // ========================================================================================================
    // Overridable methods
    // ========================================================================================================

    protected void doGenerate( IProgressMonitor monitor,
                               final List problems ) {
        // Create the correct visitor for the desired version ...
        final ModelContents contents = doGetModelContents();

        // Set up the correct model information ...
        final ModelAnnotation modelAnnotation = contents.getModelAnnotation();
        modelAnnotation.setModelType(ModelType.VIRTUAL_LITERAL);
        modelAnnotation.setPrimaryMetamodelUri(WebServicePackage.eNS_URI);

        // Set up the description ...
        if (this.wsdlDefinitions.size() != 0) {
            final String desc = modelAnnotation.getDescription();
            if (desc == null || desc.trim().length() == 0) {
                try {
                    final StringBuffer sb = new StringBuffer();
                    final String msg = WebServicePlugin.Util.getString("BasicWebServiceGenerator.DescriptionStartingSection"); //$NON-NLS-1$
                    sb.append(msg);
                    final Iterator iter = this.wsdlDefinitions.iterator();
                    while (iter.hasNext()) {
                        final Definitions defns = (Definitions)iter.next();
                        final String name = defns.getTargetNamespace();
                        final String targetNs = defns.getTargetNamespace();
                        final String line = WebServicePlugin.Util.getString("BasicWebServiceGenerator.DescriptionWsdlNameAndTargetNS", name, targetNs); //$NON-NLS-1$
                        sb.append(CoreStringUtil.LINE_SEPARATOR);
                        sb.append(line);
                    }
                    modelAnnotation.setDescription(sb.toString());
                } catch (RuntimeException t) {
                    final String msg = WebServicePlugin.Util.getString("BasicWebServiceGenerator.UnexpectedProblemBuildingDescription"); //$NON-NLS-1$
                    WebServicePlugin.Util.log(IStatus.WARNING, t, msg);
                }
            }
        }

        final WsdlVisitor visitor = new WsdlVisitor(this.webServiceResource, wsdlDefinitions, contents, problems);

        // Walk the objects and use the generator ...
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            processor.walk(this.wsdlDefinitions, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (ModelerCoreException e) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceGenerator.ErrorWhileProcessingWsdlAndCreatingWebServiceModel"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, msg, e);
            problems.add(status);
        }

        // Signal the generator to complete the WSDL (adding any binding information, etc.)
        visitor.complete();
    }

    protected ModelContents doGetModelContents() {
        final ModelContents contents = this.webServiceResource instanceof EmfResource ? ((EmfResource)this.webServiceResource).getModelContents() : new ModelContents(
                                                                                                                                                                      this.webServiceResource);
        return contents;
    }

    /**
     * @param uri
     * @return
     * @since 4.2
     */
    protected List doFindSchemas( final String uri ) {
        final String uriStr = uri != null ? uri : ""; //$NON-NLS-1$
        final Iterator iter = this.xsdSchemas.iterator();
        final List schemas = new ArrayList(xsdSchemas.size());
        while (iter.hasNext()) {
            final XSDSchema schema = (XSDSchema)iter.next();
            if (uriStr.equals(schema.getTargetNamespace())) {
                schemas.add(schema);
            }
        }
        return schemas;
    }

    // ========================================================================================================
    // Web Service Generator
    // ========================================================================================================

    protected class WsdlVisitor extends WsdlSwitch implements ModelVisitor {

        private final String DEFAULT_NAMESPACE_PREFIX = "DEFAULT_NS_PREFIX_9485338"; //$NON-NLS-1$

        private final List problems;
        protected final Resource resource;
        protected final ModelContents contents;
        private final WebServiceFactory factory;
        private final Map namespaceUrisByPrefix;
        private List definitions;
        private Interface iface; // the one created ...

        /*
         * Map of operation Qname to operation instance object.
         */
        private Map operations;

        protected WsdlVisitor( final Resource webServiceResource,
                               final List wsdlDefinitions,
                               final ModelContents contents,
                               final List problems ) {
            this.problems = problems;
            this.resource = webServiceResource;
            this.factory = WebServiceFactory.eINSTANCE;
            this.namespaceUrisByPrefix = new HashMap();
            this.contents = contents != null ? contents : new ModelContents(this.resource);
            this.definitions = wsdlDefinitions;
            this.operations = new HashMap();
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

        public boolean visit( EObject object ) {
            doSwitch(object);
            return true;
        }

        public boolean visit( Resource resource ) {
            return true;
        }

        protected String addDescription( WebServiceComponent component,
                                         Documented wsdlDocumented ) {
            if (wsdlDocumented != null) {
                final Documentation doc = wsdlDocumented.getDocumentation();
                if (doc != null) {
                    final String desc = doc.getTextContent();
                    if (desc != null && desc.trim().length() != 0) {
                        // Find the model annotation and set the description ...
                        Annotation annotation = this.contents.getAnnotation(component);
                        if (annotation == null) {
                            annotation = ModelResourceContainerFactory.createNewAnnotation(component,
                                                                                           this.contents.getAnnotationContainer(true));
                        }
                        if (annotation != null) {
                            annotation.setDescription(desc);
                            return desc;
                        }
                    }
                }
            }
            return null;
        }

        protected String getNamespaceUriForPrefix( final String prefix ) {
            if (prefix == null || prefix.trim().length() == 0) {
                return (String)this.namespaceUrisByPrefix.get(DEFAULT_NAMESPACE_PREFIX);
            }
            return (String)this.namespaceUrisByPrefix.get(prefix);
        }

        protected Identifier parseIdentifier( final String identifier ) {
            final List idParts = CoreStringUtil.split(identifier, ":"); //$NON-NLS-1$
            if (idParts.isEmpty()) {
                return null;
            }
            String name = null;
            String prefix = null;
            if (idParts.size() == 1) {
                name = (String)idParts.get(0);
            } else {
                prefix = (String)idParts.get(0);
                name = (String)idParts.get(1);
            }

            // Look for the namespace URI ...
            final String uri = getNamespaceUriForPrefix(prefix);
            return new Identifier(prefix, name, uri);
        }

        protected Message findMessage( ParamType paramType ) {
            final String msgId = paramType.getMessage();
            if (msgId == null || msgId.trim().length() == 0) {
                return null;
            }

            final Identifier identifier = parseIdentifier(msgId);
            final String uri = identifier.uri;

            for (Iterator iter = definitions.iterator(); iter.hasNext();) {
                final Definitions def = (Definitions)iter.next();
                // See if the URI is the paramType's target namespace ...
                if (def.getTargetNamespace().equals(uri)) {
                    // Look through the messages to find one with the same name ...
                    final Iterator iter1 = def.getMessages().iterator();
                    while (iter1.hasNext()) {
                        final Message msg = (Message)iter1.next();
                        if (msg.getName() != null && msg.getName().equals(identifier.name)) {
                            return msg;
                        }
                    }

                }
            }

            return null;
        }

        /**
         * Signals the generator to add the bindings, etc.
         * 
         * @since 4.2
         */
        public void complete() {
        }

        // --------------------------------------------------------------------------------------------------------
        // WSDL Processing Methods
        // --------------------------------------------------------------------------------------------------------

        /**
         * @see com.metamatrix.metamodels.wsdl.util.WsdlSwitch#caseDefinitions(com.metamatrix.metamodels.wsdl.Definitions)
         * @since 4.2
         */
        @Override
        public Object caseDefinitions( Definitions object ) {
            // Populate the prefix to namespace URI map ...
            final Iterator iter = object.getDeclaredNamespaces().iterator();
            while (iter.hasNext()) {
                final NamespaceDeclaration nsDecl = (NamespaceDeclaration)iter.next();
                String prefix = nsDecl.getPrefix();
                if (prefix == null || prefix.trim().length() == 0) {
                    prefix = DEFAULT_NAMESPACE_PREFIX;
                }
                final String uri = nsDecl.getUri();
                this.namespaceUrisByPrefix.put(prefix, uri);

                // Look for one of the XSD Schema of Schemas ...
                if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(uri) || XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10.equals(uri)
                    || XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999.equals(uri)) {
                    final XSDSchema sos = XSDSchemaImpl.getSchemaForSchema(uri);
                    if (sos != null) {
                        BasicWebServiceGenerator.this.addXsdSchema(sos);
                    }
                }
            }
            return object;
        }

        /**
         * @see com.metamatrix.metamodels.wsdl.util.WsdlSwitch#casePortType(com.metamatrix.metamodels.wsdl.PortType)
         * @since 4.2
         */
        @Override
        public Object casePortType( PortType object ) {
            // Check the operations under this PortType to see if any are in the selectedWsdlOperations
            if (selectedWsdlOperations != null && !selectedWsdlOperations.contains(object)) {
                boolean doCreate = false;
                for (Iterator iter = object.getOperations().iterator(); iter.hasNext();) {
                    Object nextObj = iter.next();
                    if (nextObj instanceof com.metamatrix.metamodels.wsdl.Operation) {
                        doCreate = selectedWsdlOperations.contains(nextObj);
                    }
                    if (doCreate) {
                        break;
                    }
                }
                if (!doCreate) {
                    return null;
                }
            }

            // Create the Interface ...
            this.iface = this.factory.createInterface();
            StringNameValidator validator = new StringNameValidator();
            String name = object.getName();
            if (!validator.isValidName(name)) {
                name = validator.createValidName(name);
            }
            this.iface.setName(name);

            // Add the interface to the resource ...
            this.resource.getContents().add(this.iface);

            return object;
        }

        /**
         * @see com.metamatrix.metamodels.wsdl.util.WsdlSwitch#caseOperation(com.metamatrix.metamodels.wsdl.Operation)
         * @since 4.2
         */
        @Override
        public Object caseOperation( com.metamatrix.metamodels.wsdl.Operation object ) {
            if (selectedWsdlOperations != null && !selectedWsdlOperations.contains(object)) {
                return null;
            }
            // Create the Interface ...
            final Operation operation = this.factory.createOperation();

            final String name = object.getName();
            final String namespace = object.getPortType().getDefinitions().getTargetNamespace();
            final QName qname = new QName(namespace, name);
            operation.setName(name);

            // Add the operation to the interface ...
            operation.setInterface(this.iface);

            try {
                NewModelObjectHelperManager.helpCreate(operation, null);
            } catch (ModelerCoreException theException) {
                String message = WebServicePlugin.Util.getString("WebServiceBuilderHelper.createOperation.errMsg"); //$NON-NLS-1$
                WebServicePlugin.Util.log(IStatus.ERROR, theException, message);
            }

            /*
             * add this operation to the Map of operations maintained by this visitor.
             */
            operations.put(qname, operation);

            return object;
        }

        /**
         * @see com.metamatrix.metamodels.wsdl.util.WsdlSwitch#caseInput(com.metamatrix.metamodels.wsdl.Input)
         * @since 4.2
         */
        @Override
        public Object caseInput( com.metamatrix.metamodels.wsdl.Input object ) {
            // Create the Interface ...
            final Input input = this.factory.createInput();

            doProcessMessage(input, object);

            /*
             * find the operation that contains this Input
             */
            final String operationName = object.getOperation().getName();
            final String operationNamespace = object.getOperation().getPortType().getDefinitions().getTargetNamespace();
            final Operation operation = (Operation)operations.get(new QName(operationNamespace, operationName));
            // Add the input to the operation ...
            input.setOperation(operation);

            return object;
        }

        /**
         * @see com.metamatrix.metamodels.wsdl.util.WsdlSwitch#caseOutput(com.metamatrix.metamodels.wsdl.Output)
         * @since 4.2
         */
        @Override
        public Object caseOutput( com.metamatrix.metamodels.wsdl.Output object ) {
            // Create the Interface ...
            final Output output = this.factory.createOutput();

            doProcessMessage(output, object);

            /*
             * find the operation that contains this Input
             */
            final String operationName = object.getOperation().getName();
            final String operationNamespace = object.getOperation().getPortType().getDefinitions().getTargetNamespace();
            final Operation operation = (Operation)operations.get(new QName(operationNamespace, operationName));

            // Add the input to the operation ...
            output.setOperation(operation);

            return object;
        }

        /**
         * @see com.metamatrix.metamodels.wsdl.util.WsdlSwitch#caseInput(com.metamatrix.metamodels.wsdl.Input)
         * @since 4.2
         */
        public Object doProcessMessage( final com.metamatrix.metamodels.webservice.Message message,
                                        final com.metamatrix.metamodels.wsdl.ParamType object ) {
            // Create the Interface ...
            Message msg = null;
            String name = object.getName();
            String xsdIdentifier = null;
            if (name == null || name.trim().length() == 0) {
                // Look for the corresponding message part, and get it's name
                msg = findMessage(object);
                if (msg != null) {
                    name = msg.getName();
                    final boolean needName = (name == null || name.trim().length() == 0);

                    // Iterate over the parts ...
                    final Iterator iter = msg.getParts().iterator();
                    while (iter.hasNext()) {
                        final MessagePart part = (MessagePart)iter.next();
                        if (needName) {
                            name = part.getName();
                        }
                        final String type = part.getType();
                        final String element = part.getElement();
                        if (type != null && type.trim().length() != 0) {
                            xsdIdentifier = type;
                        } else if (element != null && element.trim().length() != 0) {
                            xsdIdentifier = element;
                        }
                        break;
                    }
                }
            } else {
                msg = findMessage(object);
                if (msg != null) {
                    if (msg.getParts() != null) {
                        final Iterator mIter = msg.getParts().iterator();
                        while (mIter.hasNext()) {
                            final MessagePart part = (MessagePart)mIter.next();

                            final String type = part.getType();
                            final String element = part.getElement();
                            if (type != null && type.trim().length() != 0) {
                                xsdIdentifier = type;
                            } else if (element != null && element.trim().length() != 0) {
                                xsdIdentifier = element;
                            }
                            break;
                        }
                    }
                }
            }
            message.setName(name);

            // Find the XSD type ...
            if (xsdIdentifier != null && xsdIdentifier.trim().length() != 0) {
                final Identifier ident = parseIdentifier(xsdIdentifier);
                if (ident != null) {
                    /*
                     * HEre we must change the code that is finding the schema.  Currently it is returning the first schema with
                     *  the target namespace that matches the XSD component that we are looking for.  In many cases there can be
                     *  multiple XML schema files with the same target namespace.
                     */
                    final List schemas = doFindSchemas(ident.uri);
                    if (schemas.size() > 0) {
                        for (Iterator iter = schemas.iterator(); iter.hasNext();) {
                            XSDSchema schema = (XSDSchema)iter.next();
                            if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(ident.uri)
                                || XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(ident.uri)
                                || XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(ident.uri)) {
                                // This is a reference to a Schema Of Schemas, so this reference
                                // can only be to a built-in simple type, so resolve it ...
                                final XSDSimpleTypeDefinition st = schema.resolveSimpleTypeDefinition(ident.uri, ident.name);
                                if (st != null) {
                                    message.setContentSimpleType(st);
                                }
                            } else {

                                // Look for an element declaration ...
                                final XSDElementDeclaration ed = schema.resolveElementDeclaration(ident.uri, ident.name);
                                if (ed != null) {
                                    // Try to resolve with our simple datatypes ...
                                    message.setContentElement(ed);
                                } else {
                                    // Look for a complex type ...
                                    final XSDComplexTypeDefinition ct = schema.resolveComplexTypeDefinition(ident.uri, ident.name);
                                    if (ct != null) {
                                        message.setContentComplexType(ct);
                                    } else {
                                        // Look for a simple type ...
                                        final XSDSimpleTypeDefinition st = schema.resolveSimpleTypeDefinition(ident.uri,
                                                                                                              ident.name);
                                        if (st != null) {
                                            message.setContentSimpleType(st);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Set the description, if any ...
            String desc = addDescription(message, object);
            if (desc == null) {
                // Try setting the description to the message ...
                if (msg == null) {
                    msg = findMessage(object);
                    desc = addDescription(message, msg);
                }
            }

            return object;
        }

    }

    protected class Identifier {
        public final String prefix;
        public final String name;
        public final String uri;

        public Identifier( final String prefix,
                           final String name,
                           final String uri ) {
            this.prefix = prefix;
            this.name = name;
            this.uri = uri;
        }
    }

}
