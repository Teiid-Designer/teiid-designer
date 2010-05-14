/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLDefaultHandler;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.adapters.DOMAdapter;
import org.jdom.adapters.XercesDOMAdapter;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.internal.core.xml.JdomHelper;
import com.metamatrix.metamodels.wsdl.Attribute;
import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.BindingInput;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.BindingOutput;
import com.metamatrix.metamodels.wsdl.BindingParam;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented;
import com.metamatrix.metamodels.wsdl.ExtensibleDocumented;
import com.metamatrix.metamodels.wsdl.Fault;
import com.metamatrix.metamodels.wsdl.Import;
import com.metamatrix.metamodels.wsdl.Input;
import com.metamatrix.metamodels.wsdl.Message;
import com.metamatrix.metamodels.wsdl.MessagePart;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.Operation;
import com.metamatrix.metamodels.wsdl.Output;
import com.metamatrix.metamodels.wsdl.Port;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.Service;
import com.metamatrix.metamodels.wsdl.Types;
import com.metamatrix.metamodels.wsdl.WsdlFactory;
import com.metamatrix.metamodels.wsdl.WsdlMetamodelPlugin;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpAddress;
import com.metamatrix.metamodels.wsdl.http.HttpBinding;
import com.metamatrix.metamodels.wsdl.http.HttpFactory;
import com.metamatrix.metamodels.wsdl.http.HttpOperation;
import com.metamatrix.metamodels.wsdl.soap.SoapAddress;
import com.metamatrix.metamodels.wsdl.soap.SoapBinding;
import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapFactory;
import com.metamatrix.metamodels.wsdl.soap.SoapFault;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;
import com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault;
import com.metamatrix.metamodels.wsdl.soap.SoapOperation;
import com.metamatrix.metamodels.wsdl.soap.SoapStyleType;
import com.metamatrix.metamodels.wsdl.soap.SoapUseType;

/**
 * This loader reads a WSDL file from an input stream and populates a {@link com.metamatrix.metamodels.wsdl.io.WsdlResourceImpl}
 * with the corresponding objects using the {@link WsdlPackage WSDL},{@link SoapPackage SOAP},{@link HttpPackage HTTP}and
 * {@link MimePackage MIME}metamodels. Elements and attributes from outside these namespaces are maintained (using the
 * {@link com.metamatrix.metamodels.wsdl.Element WSDL:Element}and {@link com.metamatrix.metamodels.wsdl.Attribute WSDL:Attribute}
 * metaclasses.
 * <p>
 * Currently, this loader does <i>not </i> process the XML Schema components that are defined within the
 * <code>definitions/types</code> fragment.
 * </p>
 * 
 * @since 4.2
 */
public class WsdlLoader implements XMLLoad, WsdlConstants {

    private WsdlFactory wsdlFactory;
    private SoapFactory soapFactory;
    private HttpFactory httpFactory;
    // private MimeFactory mimeFactory;
    private boolean loadXsdObjects;

    /**
     * Construct an instance
     * 
     * @since 4.2
     */
    public WsdlLoader() {
        this.wsdlFactory = WsdlFactory.eINSTANCE;
        this.soapFactory = SoapFactory.eINSTANCE;
        this.httpFactory = HttpFactory.eINSTANCE;
        // this.mimeFactory = MimeFactory.eINSTANCE;
    }

    protected void init( final XMLResource resource,
                         final Map options ) {
        this.loadXsdObjects = true;
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.XMLLoad#load(org.eclipse.emf.ecore.xmi.XMLResource, java.io.InputStream, java.util.Map)
     * @since 4.2
     */
    public void load( final XMLResource resource,
                      final InputStream inputStream,
                      final Map loadOptions ) throws IOException {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(inputStream);
        init(resource, loadOptions);
        // final Map options = loadOptions != null ? loadOptions : Collections.EMPTY_MAP;

        // Load the stream contents into a JDOM document ...
        Document doc = null;
        org.w3c.dom.Document domDoc = null;
        if (this.loadXsdObjects) {
            domDoc = doOpenDom(inputStream);
            doc = doBuildFromDom(domDoc);
        } else {
            doc = doOpen(inputStream);
        }

        // Process the JDOM document and construct the model ...
        final Element root = doc.getRootElement();
        final Definitions definitions = doLoad(root, resource);

        if (definitions != null && this.loadXsdObjects) {
            doLoadXsds(domDoc, definitions);
        }
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.XMLLoad#load(org.eclipse.emf.ecore.xmi.XMLResource, org.w3c.dom.Node, java.util.Map)
     * @since 4.3
     */
    public void load( final XMLResource resource,
                      final Node node,
                      final Map options ) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.xmi.XMLLoad#load(org.eclipse.emf.ecore.xmi.XMLResource, org.xml.sax.InputSource, java.util.Map)
     */
    public void load( XMLResource resource,
                      InputSource inputSource,
                      Map<?, ?> options ) throws IOException {
        load(resource, inputSource.getByteStream(), options);
    }

    protected Document doOpen( final InputStream inputStream ) throws IOException {
        Document doc = null;
        try {
            doc = JdomHelper.buildDocument(inputStream);
        } catch (JDOMException err) {
            // Wrap the exception (like XMLLoadImpl does)
            throw new Resource.IOWrappedException(err);
        }
        return doc;
    }

    // ------------------ X S D - R E L A T E D M E T H O D S ---------------------

    protected org.w3c.dom.Document doOpenDom( final InputStream inputStream ) throws IOException {
        org.w3c.dom.Document doc = null;
        try {
            DOMAdapter adapter = new XercesDOMAdapter();
            doc = adapter.getDocument(inputStream, false);
        } catch (IOException err) {
            throw err;
        } catch (Exception err) {
            // Wrap the exception (like XMLLoadImpl does)
            throw new Resource.IOWrappedException(err);
        }
        return doc;
    }

    protected Document doBuildFromDom( final org.w3c.dom.Document domDocument ) {
        final DOMBuilder builder = new DOMBuilder();
        final Document doc = builder.build(domDocument);
        return doc;
    }

    protected List doLoadXsds( final org.w3c.dom.Document domDoc,
                               final Definitions defns ) {

        // Look for the 'definitions/types' ...
        final Types types = defns.getTypes();
        if (types == null) {
            return Collections.EMPTY_LIST;
        }

        final List schemaElements = new ArrayList();

        // Look for the 'definitions/types/schema' element in the DOM document ...
        final org.w3c.dom.Element docElement = domDoc.getDocumentElement();
        NodeList typesList = docElement.getElementsByTagNameNS(WsdlPackage.eNS_URI, TYPES);
        if (typesList != null && typesList.getLength() != 0) {
            final org.w3c.dom.Element typesElement = (org.w3c.dom.Element)typesList.item(0);

            // Look for the schema node (by NS) ...
            final String[] schemaNS = new String[] {XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001,
                XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10, XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999};
            for (int i = 0; i != schemaNS.length; ++i) {
                final NodeList typesChildren = typesElement.getElementsByTagNameNS(schemaNS[i], "schema"); //$NON-NLS-1$
                if (typesChildren != null) {
                    for (int j = 0; j != typesChildren.getLength(); ++j) {
                        final org.w3c.dom.Element schemaElement = (org.w3c.dom.Element)typesChildren.item(j);
                        if (schemaElement != null) {
                            schemaElements.add(schemaElement);
                        }
                    }
                }
            }
        }

        // Parse the schema fragment ...
        final List xsdSchemas = new ArrayList();
        if (schemaElements.size() != 0) {
            final Iterator iter = schemaElements.iterator();
            while (iter.hasNext()) {
                final org.w3c.dom.Element schemaElement = (org.w3c.dom.Element)iter.next();
                final String key = XSDResourceImpl.XSD_PROGRESS_MONITOR;
                ResourceSet globalResourceSet = XSDSchemaImpl.getGlobalResourceSet();
                Object oldMonitor = globalResourceSet.getLoadOptions().get(key);
                XSDSchema xsdSchema = null;
                try {
                    XSDSchemaImpl.getGlobalResourceSet().getLoadOptions().put(key, null);
                    xsdSchema = XSDSchemaImpl.createSchema(schemaElement);
                } finally {
                    XSDSchemaImpl.getGlobalResourceSet().getLoadOptions().put(key, oldMonitor);
                }

                // Place the XSD Schema onto the types ...
                if (xsdSchema != null) {
                    types.getSchemas().add(xsdSchema);
                    xsdSchemas.add(xsdSchema);
                }
            }
        }

        return xsdSchemas;
    }

    // ------------------ W S D L - R E L A T E D M E T H O D S ---------------------

    protected Definitions doLoad( final Element root,
                                  final XMLResource resource ) {

        // Make sure the root element is wsdl:definition
        final String rootName = root.getName();
        final String rootNsUri = root.getNamespaceURI();
        if (!DEFINITIONS.equals(rootName) || !NAMESPACE.equals(rootNsUri)) {
            final Object[] params = new Object[] {NAMESPACE_PREFIX, DEFINITIONS};
            final String msg = WsdlMetamodelPlugin.Util.getString("WsdlLoader.RootXmlElementWasNotWsdlDefinitions", params); //$NON-NLS-1$
            addError(resource, msg);
            return null; // can't go any further ...
        }

        final Definitions defns = this.wsdlFactory.createDefinitions();
        doProcess(defns, root);
        resource.getContents().add(defns);
        return defns;
    }

    // -----------------------------------------------------------------------
    // WSDL Processing methods
    // -----------------------------------------------------------------------

    protected void doProcess( final Definitions defns,
                              final Element element ) {
        // Set the name ...
        final String name = element.getAttributeValue(DEFINITIONS_NAME);
        defns.setName(name);

        // Set the target namespace ...
        final String tns = element.getAttributeValue(DEFINITIONS_TARGETNAMESPACE);
        defns.setTargetNamespace(tns);

        // Get and process the children ...
        final List children = element.getChildren();
        final Iterator iter = children.iterator();
        while (iter.hasNext()) {
            final Element child = (Element)iter.next();
            final String nsUri = child.getNamespaceURI();
            if (NAMESPACE.equals(nsUri)) {
                // This is the WSDL namespace ..
                final String childName = child.getName();

                if (MESSAGE.equals(childName)) {
                    // Process the message element ...
                    final Message message = this.wsdlFactory.createMessage();
                    doProcess(message, child);
                    defns.getMessages().add(message);

                } else if (PORTTYPE.equals(childName)) {
                    // Process the portType element ...
                    final PortType portType = this.wsdlFactory.createPortType();
                    doProcess(portType, child);
                    defns.getPortTypes().add(portType);

                } else if (BINDING.equals(childName)) {
                    // Process the binding element ...
                    final Binding binding = this.wsdlFactory.createBinding();
                    doProcess(binding, child);
                    defns.getBindings().add(binding);

                } else if (SERVICE.equals(childName)) {
                    // Process the service element ...
                    final Service service = this.wsdlFactory.createService();
                    doProcess(service, child);
                    defns.getServices().add(service);

                } else if (TYPES.equals(childName)) {
                    // Process the types element ...
                    final Types types = this.wsdlFactory.createTypes();
                    doProcess(types, child);
                    defns.setTypes(types);

                } else if (IMPORT.equals(childName)) {
                    // Process the import element ...
                    final Import importObj = this.wsdlFactory.createImport();
                    doProcess(importObj, child);
                    defns.getImports().add(importObj);

                    // } else if ( DOCUMENTATION.equals(childName) ) {
                    // handled later on generically

                }
                // Shouldn't be anything else in this namespace, but ignore if there is ...
            } else {
                // Not the WSDL namespace, so process as generic elements ...
                final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                doProcess(elementObj, child);
                defns.getElements().add(elementObj);
            }

        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(defns, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(defns, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(defns, element);
        // doProcessAdditionalAttributes(defns, element);
    }

    protected void doProcess( final Message message,
                              final Element element ) {
        final String name = element.getAttributeValue(MESSAGE_NAME);
        message.setName(name);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String childName = child.getName();
            if (PART.equals(childName)) {
                // Process the 'part' element ...
                final MessagePart part = this.wsdlFactory.createMessagePart();
                doProcess(part, child);
                message.getParts().add(part);
            }
        }

        // Process for nested 'documentation' ...
        doProcessDocumented(message, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(message, element);
        // doProcessAdditionalAttributes(message, element);
    }

    protected void doProcess( final PortType portType,
                              final Element element ) {
        final String name = element.getAttributeValue(PORTTYPE_NAME);
        portType.setName(name);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String childName = child.getName();
            if (OPERATION.equals(childName)) {
                // Process the 'operation' element ...
                final Operation operation = this.wsdlFactory.createOperation();
                doProcess(operation, child);
                portType.getOperations().add(operation);
            }
        }

        // Process for nested 'documentation' ...
        doProcessDocumented(portType, element);

        // Process for additional elements and attributes ...
        // doProcessAdditionalElements(portType, element);
        doProcessAdditionalAttributes(portType, element);
    }

    protected void doProcess( final Binding binding,
                              final Element element ) {
        final String name = element.getAttributeValue(BINDING_NAME);
        binding.setName(name);
        final String type = element.getAttributeValue(BINDING_TYPE);
        binding.setType(type);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String nsUri = child.getNamespaceURI();
            if (NAMESPACE.equals(nsUri)) {
                // This is the WSDL namespace ..
                final String childName = child.getName();
                if (BINDINGOPERATION.equals(childName)) {
                    // Process the 'operation' element ...
                    final BindingOperation operation = this.wsdlFactory.createBindingOperation();
                    doProcess(operation, child);
                    binding.getBindingOperations().add(operation);
                }
            } else if (Soap.NAMESPACE.equals(nsUri)) {
                final String childName = child.getName();
                if (Soap.BINDING.equals(childName)) {
                    // Process the 'soap:binding' element ...
                    final SoapBinding soapBinding = this.soapFactory.createSoapBinding();
                    doProcess(soapBinding, child);
                    binding.setSoapBinding(soapBinding);
                }
            } else if (Http.NAMESPACE.equals(nsUri)) {
                final String childName = child.getName();
                if (Http.BINDING.equals(childName)) {
                    // Process the 'http:binding' element ...
                    final HttpBinding httpBinding = this.httpFactory.createHttpBinding();
                    doProcess(httpBinding, child);
                    binding.setHttpBinding(httpBinding);
                }
            } else {
                // Not the WSDL, SOAP, or HTTP namespace, so process as generic elements ...
                final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                doProcess(elementObj, child);
                binding.getElements().add(elementObj);
            }
        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(binding, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(binding, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(binding, element);
        // doProcessAdditionalAttributes(binding, element);
    }

    protected void doProcess( final Service service,
                              final Element element ) {
        final String name = element.getAttributeValue(SERVICE_NAME);
        service.setName(name);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String nsUri = child.getNamespaceURI();
            if (NAMESPACE.equals(nsUri)) {
                // This is the WSDL namespace ..
                final String childName = child.getName();
                if (PORT.equals(childName)) {
                    // Process the 'port' element ...
                    final Port port = this.wsdlFactory.createPort();
                    doProcess(port, child);
                    service.getPorts().add(port);
                }
            } else {
                // Not the WSDL namespace, so process as generic elements ...
                final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                doProcess(elementObj, child);
                service.getElements().add(elementObj);
            }
        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(service, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(service, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(service, element);
        // doProcessAdditionalAttributes(service, element);
    }

    protected void doProcess( final Types types,
                              final Element element ) {
        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String childName = child.getName();
            final String childNsUri = child.getNamespaceURI();
            if (Xsd.SCHEMA.equals(childName)
                && (Xsd.NAMESPACE_2001.equals(childNsUri) || Xsd.NAMESPACE_2000.equals(childNsUri) || Xsd.NAMESPACE_1999.equals(childNsUri))) {
                // This element is the schema element for XSD ...

                // // TODO: Correctly rocess the 'xsd:schema' element
                //
                // // TEMPORARILY PROCESS AND ADD AS EXTRA ELEMENTS
                // final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                // doProcess(elementObj, child);
                // types.getElements().add(elementObj);

            } else {
                // Not the XSD namespace, so process as generic elements ...
                final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                doProcess(elementObj, child);
                types.getElements().add(elementObj);
            }
        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(types, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(types, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(types, element);
        // doProcessAdditionalAttributes(types, element);
    }

    /**
     * Process the supplied 'import' JDOM element and populate the WSDL Import.
     * 
     * @param wsdlImport
     * @param element
     * @since 4.2
     */
    protected void doProcess( final Import wsdlImport,
                              final Element element ) {
        final String namespace = element.getAttributeValue(IMPORT_NAMESPACE);
        final String location = element.getAttributeValue(IMPORT_LOCATION);
        wsdlImport.setNamespace(namespace);
        wsdlImport.setLocation(location);

        // Process for nested 'documentation' ...
        doProcessDocumented(wsdlImport, element);

        // Process for additional elements and attributes ...
        // doProcessAdditionalElements(wsdlImport, element);
        doProcessAdditionalAttributes(wsdlImport, element);
    }

    protected void doProcess( final MessagePart part,
                              final Element element ) {
        final String name = element.getAttributeValue(PART_NAME);
        part.setName(name);
        final String type = element.getAttributeValue(PART_TYPE);
        part.setType(type);
        final String partElement = element.getAttributeValue(PART_ELEMENT);
        part.setElement(partElement);

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(part, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(part, element);

        // Process for additional elements and attributes ...
        // doProcessAdditionalElements(part, element);
        doProcessAdditionalAttributes(part, element);
    }

    protected void doProcess( final Operation operation,
                              final Element element ) {
        final String name = element.getAttributeValue(OPERATION_NAME);
        operation.setName(name);
        final String paramOrder = element.getAttributeValue(OPERATION_PARAMETER_ORDER);
        operation.setParameterOrder(paramOrder);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String nsUri = child.getNamespaceURI();
            if (NAMESPACE.equals(nsUri)) {
                // This is the WSDL namespace ..
                final String childName = child.getName();
                if (INPUT.equals(childName)) {
                    // Process the 'input' element ...
                    final Input input = this.wsdlFactory.createInput();
                    doProcess(input, child);
                    operation.setInput(input);
                } else if (OUTPUT.equals(childName)) {
                    // Process the 'output' element ...
                    final Output output = this.wsdlFactory.createOutput();
                    doProcess(output, child);
                    operation.setOutput(output);
                } else if (FAULT.equals(childName)) {
                    // Process the 'fault' element ...
                    final Fault fault = this.wsdlFactory.createFault();
                    doProcess(fault, child);
                    operation.getFaults().add(fault);
                }
            }
        }

        // Process for nested 'documentation' ...
        doProcessDocumented(operation, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(operation, element);
        // doProcessAdditionalAttributes(operation, element);
    }

    protected void doProcess( final BindingOperation operation,
                              final Element element ) {
        final String name = element.getAttributeValue(BINDINGOPERATION_NAME);
        operation.setName(name);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String nsUri = child.getNamespaceURI();
            if (NAMESPACE.equals(nsUri)) {
                // This is the WSDL namespace ..
                final String childName = child.getName();
                if (INPUT.equals(childName)) {
                    // Process the 'input' element ...
                    final BindingInput input = this.wsdlFactory.createBindingInput();
                    doProcess(input, child);
                    operation.setBindingInput(input);
                } else if (OUTPUT.equals(childName)) {
                    // Process the 'output' element ...
                    final BindingOutput output = this.wsdlFactory.createBindingOutput();
                    doProcess(output, child);
                    operation.setBindingOutput(output);
                } else if (FAULT.equals(childName)) {
                    // Process the 'fault' element ...
                    final BindingFault fault = this.wsdlFactory.createBindingFault();
                    doProcess(fault, child);
                    operation.getBindingFaults().add(fault);
                }
            } else if (Soap.NAMESPACE.equals(nsUri)) {
                // This is the SOAP namespace ..
                final String childName = child.getName();
                if (Soap.OPERATION.equals(childName)) {
                    // Process the 'soap:operation' element ...
                    final SoapOperation soapOp = this.soapFactory.createSoapOperation();
                    doProcess(soapOp, child);
                    operation.setSoapOperation(soapOp);
                }

            } else if (Http.NAMESPACE.equals(nsUri)) {
                // This is the HTTP namespace ..
                final String childName = child.getName();
                if (Http.OPERATION.equals(childName)) {
                    // Process the 'http:operation' element ...
                    final HttpOperation httpOp = this.httpFactory.createHttpOperation();
                    doProcess(httpOp, child);
                    operation.setHttpOperation(httpOp);
                }

            }
        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(operation, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(operation, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(operation, element);
        // doProcessAdditionalAttributes(operation, element);
    }

    protected void doProcess( final Port port,
                              final Element element ) {
        final String name = element.getAttributeValue(PORT_NAME);
        port.setName(name);
        final String binding = element.getAttributeValue(PORT_BINDING);
        port.setBinding(binding);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String nsUri = child.getNamespaceURI();
            if (NAMESPACE.equals(nsUri)) {
                // Skip; should be process explicitly
            } else if (Soap.NAMESPACE.equals(nsUri)) {
                final String childName = child.getName();
                if (Soap.ADDRESS.equals(childName)) {
                    final SoapAddress address = this.soapFactory.createSoapAddress();
                    doProcess(address, child);
                    port.setSoapAddress(address);
                }
            } else if (Http.NAMESPACE.equals(nsUri)) {
                final String childName = child.getName();
                if (Http.ADDRESS.equals(childName)) {
                    final HttpAddress address = this.httpFactory.createHttpAddress();
                    doProcess(address, child);
                    port.setHttpAddress(address);
                }
            } else {
                // Not the WSDL namespace, so process as generic elements ...
                final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                doProcess(elementObj, child);
                port.getElements().add(elementObj);
            }
        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(port, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(port, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(port, element);
        // doProcessAdditionalAttributes(port, element);
    }

    protected void doProcess( final Input input,
                              final Element element ) {
        final String name = element.getAttributeValue(INPUT_NAME);
        input.setName(name);
        final String message = element.getAttributeValue(INPUT_MESSAGE);
        input.setMessage(message);

        // Process for nested 'documentation' ...
        doProcessDocumented(input, element);
    }

    protected void doProcess( final Output output,
                              final Element element ) {
        final String name = element.getAttributeValue(OUTPUT_NAME);
        output.setName(name);
        final String message = element.getAttributeValue(OUTPUT_MESSAGE);
        output.setMessage(message);

        // Process for nested 'documentation' ...
        doProcessDocumented(output, element);

        // Process for additional elements and attributes ...
        // doProcessAdditionalElements(output, element);
        doProcessAdditionalAttributes(output, element);
    }

    protected void doProcess( final Fault fault,
                              final Element element ) {
        final String name = element.getAttributeValue(FAULT_NAME);
        fault.setName(name);
        final String message = element.getAttributeValue(FAULT_MESSAGE);
        fault.setMessage(message);

        // Process for nested 'documentation' ...
        doProcessDocumented(fault, element);

        // Process for additional elements and attributes ...
        // doProcessAdditionalElements(fault, element);
        doProcessAdditionalAttributes(fault, element);
    }

    protected void doProcess( final BindingParam inputOrOutput,
                              final Element element ) {
        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String nsUri = child.getNamespaceURI();
            if (Soap.NAMESPACE.equals(nsUri)) {
                // This is the SOAP namespace ..
                final String childName = child.getName();
                if (Soap.BODY.equals(childName)) {
                    // Process the 'soap:body' element ...
                    final SoapBody soapBody = this.soapFactory.createSoapBody();
                    doProcess(soapBody, child);
                    inputOrOutput.setSoapBody(soapBody);

                } else if (Soap.BODY.equals(childName)) {
                    // Process the 'soap:header' element ...
                    final SoapHeader soapHeader = this.soapFactory.createSoapHeader();
                    doProcess(soapHeader, child);
                    inputOrOutput.setSoapHeader(soapHeader);
                }
            } else if (NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Http.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Mime.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else {
                // Not the WSDL,SOAP, HTTP or MIME namespace, so process as generic elements ...
                final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                doProcess(elementObj, child);
                inputOrOutput.getElements().add(elementObj);
            }

        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(inputOrOutput, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(inputOrOutput, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(inputOrOutput, element);
        // doProcessAdditionalAttributes(inputOrOutput, element);
    }

    protected void doProcess( final BindingFault fault,
                              final Element element ) {
        final String name = element.getAttributeValue(BINDINGFAULT_NAME);
        fault.setName(name);

        // Process the child elements ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final String nsUri = child.getNamespaceURI();
            if (NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Soap.NAMESPACE.equals(nsUri)) {
                final String childName = child.getName();
                if (Soap.FAULT.equals(childName)) {
                    // Process the 'soap:fault' element ...
                    final SoapFault soapFault = this.soapFactory.createSoapFault();
                    doProcess(soapFault, child);
                    fault.setSoapFault(soapFault);
                }
            } else if (Http.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Mime.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else {
                // Not the WSDL,SOAP, HTTP or MIME namespace, so process as generic elements ...
                final com.metamatrix.metamodels.wsdl.Element elementObj = this.wsdlFactory.createElement();
                doProcess(elementObj, child);
                fault.getElements().add(elementObj);
            }

        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(fault, element);

        // Process for nested 'documentation' ...
        doProcessDocumented(fault, element);

        // Process for additional elements and attributes ...
        doProcessAdditionalElements(fault, element);
        // doProcessAdditionalAttributes(fault, element);
    }

    /**
     * Process the supplied JDOM Element and populate the WSDL Element. This method may result in a recursive call.
     * 
     * @param wsdlElement
     * @param element
     * @since 4.2
     */
    protected void doProcess( final com.metamatrix.metamodels.wsdl.Element wsdlElement,
                              final Element element ) {
        final String name = element.getName();
        final String prefix = element.getNamespacePrefix();
        final String textContext = element.getTextTrim();
        final String nsUri = element.getNamespaceURI();
        wsdlElement.setName(name);
        wsdlElement.setPrefix(prefix);
        wsdlElement.setNamespaceUri(nsUri);
        wsdlElement.setTextContent(textContext);

        // Process the attributes ...
        final List attribs = element.getAttributes();
        final Iterator iter = attribs.iterator();
        while (iter.hasNext()) {
            final org.jdom.Attribute attribute = (org.jdom.Attribute)iter.next();
            final Attribute wsdlAttribute = this.wsdlFactory.createAttribute();
            doProcess(wsdlAttribute, attribute);
            wsdlElement.getAttributes().add(wsdlAttribute);
        }

        // Process the child elements (recursively) ...
        final List children = element.getChildren();
        final Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            final Element child = (Element)childIter.next();
            final com.metamatrix.metamodels.wsdl.Element wsdlChild = this.wsdlFactory.createElement();
            doProcess(wsdlChild, child);
            wsdlElement.getElements().add(wsdlChild);
        }

        // Process for namespace declarations ...
        doProcessNamespaceDeclarationOwner(wsdlElement, element);
    }

    /**
     * Process the supplied JDOM Attribute and populate the WSDL Attribute.
     * 
     * @param wsdlAttribute
     * @param attribute
     * @since 4.2
     */
    protected void doProcess( final Attribute wsdlAttribute,
                              final org.jdom.Attribute attribute ) {
        final String name = attribute.getName();
        final String prefix = attribute.getNamespacePrefix();
        final String textContext = attribute.getValue();
        final String nsUri = attribute.getNamespaceURI();
        wsdlAttribute.setName(name);
        wsdlAttribute.setPrefix(prefix);
        wsdlAttribute.setNamespaceUri(nsUri);
        wsdlAttribute.setValue(textContext);
    }

    /**
     * Process a element for any contained namespace declarations.
     * 
     * @param owner
     * @param element
     * @since 4.2
     */
    protected void doProcessNamespaceDeclarationOwner( final NamespaceDeclarationOwner owner,
                                                       final Element element ) {
        final List namespaces = element.getAdditionalNamespaces();
        final Iterator iter = namespaces.iterator();
        while (iter.hasNext()) {
            final Namespace namespace = (Namespace)iter.next();
            final String prefix = namespace.getPrefix();
            final String uri = namespace.getURI();

            // Construct a wsdl:NamespaceDeclaration object ...
            final NamespaceDeclaration decl = this.wsdlFactory.createNamespaceDeclaration();
            decl.setPrefix(prefix);
            decl.setUri(uri);

            // Add the declaration to the owner ...
            decl.setOwner(owner);
        }
    }

    /**
     * Process a Documented object for any potential documentation and any additional elements
     * 
     * @param documented
     * @param documentedElement
     * @return @since 4.2
     */
    protected void doProcessDocumented( final Documented documented,
                                        final Element documentedElement ) {
        // Look for the 'documentation' child ...
        final Element doc = documentedElement.getChild(DOCUMENTATION);
        if (doc != null) {
            final Documentation wsdlDoc = this.wsdlFactory.createDocumentation();

            // Set any content on the doc node ...
            final String text = doc.getTextTrim();
            wsdlDoc.setTextContent(text);

            // Process extra elements ...
            final List children = doc.getChildren();
            final Iterator childIter = children.iterator();
            while (childIter.hasNext()) {
                final Element child = (Element)childIter.next();
                final com.metamatrix.metamodels.wsdl.Element wsdlChild = this.wsdlFactory.createElement();
                doProcess(wsdlChild, child);
                wsdlDoc.getElements().add(wsdlChild);
            }
        }
    }

    protected void doProcessAdditionalAttributes( final ExtensibleAttributesDocumented openAtts,
                                                  final Element element ) {
        // Process any extra (non-wsdl) attributes ...
        final List attribs = element.getAttributes();
        final Iterator iter = attribs.iterator();
        while (iter.hasNext()) {
            final org.jdom.Attribute attribute = (org.jdom.Attribute)iter.next();
            final String nsUri = attribute.getNamespaceURI();

            if (NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Soap.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Http.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Mime.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            }

            // Else ...
            else {
                // Else process as an additional attribute ...
                final Attribute wsdlAttribute = this.wsdlFactory.createAttribute();
                doProcess(wsdlAttribute, attribute);
                openAtts.getAttributes().add(wsdlAttribute);
            }
        }
    }

    protected void doProcessAdditionalElements( final ExtensibleDocumented extensibleDocumented,
                                                final Element element ) {
        // Process any extra (non-wsdl) attributes ...
        final List elements = element.getChildren();
        final Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            final org.jdom.Element child = (org.jdom.Element)iter.next();
            final String nsUri = child.getNamespaceURI();

            if (NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Soap.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Http.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Mime.NAMESPACE.equals(nsUri)) {
                // skip this; should be processed explicitly ...
            } else if (Xsd.SCHEMA.equals(child.getName())
                       && (Xsd.NAMESPACE_2001.equals(nsUri) || Xsd.NAMESPACE_2000.equals(nsUri) || Xsd.NAMESPACE_1999.equals(nsUri))) {
                // skip this; should be processed explicitly ...
            }

            // Else ...
            else {
                // Else process as an additional attribute ...
                final com.metamatrix.metamodels.wsdl.Element wsdlChild = this.wsdlFactory.createElement();
                doProcess(wsdlChild, child);
                extensibleDocumented.getElements().add(wsdlChild);
            }
        }
    }

    // -----------------------------------------------------------------------
    // SOAP Processing methods
    // -----------------------------------------------------------------------

    protected void doProcess( final SoapBinding binding,
                              final Element element ) {
        final String transport = element.getAttributeValue(Soap.BINDING_TRANSPORT);
        binding.setTransport(transport);

        final String style = element.getAttributeValue(Soap.BINDING_STYLE);
        if (Soap.ENUM_STYLE_RPC.equals(style)) {
            binding.setStyle(SoapStyleType.RPC_LITERAL);
        } else if (Soap.ENUM_STYLE_DOC.equals(style)) {
            binding.setStyle(SoapStyleType.DOCUMENT_LITERAL);
        }
    }

    protected void doProcess( final SoapBody body,
                              final Element element ) {
        final String namespace = element.getAttributeValue(Soap.BODY_NAMESPACE);
        body.setNamespace(namespace);

        final String parts = element.getAttributeValue(Soap.BODY_PARTS);
        if (parts != null && parts.trim().length() != 0) {
            final List partList = body.getParts();
            final StringTokenizer tokenizer = new StringTokenizer(parts, NCNAMES_DELIM);
            while (tokenizer.hasMoreElements()) {
                final String token = (String)tokenizer.nextElement();
                if (token != null && token.length() != 0) {
                    partList.add(token);
                }
            }
        }

        final String styles = element.getAttributeValue(Soap.BODY_ENCODINGSTYLE);
        if (styles != null && styles.trim().length() != 0) {
            final List styleList = body.getEncodingStyles();
            final StringTokenizer tokenizer2 = new StringTokenizer(styles, ANYURI_DELIM);
            while (tokenizer2.hasMoreElements()) {
                final String token = (String)tokenizer2.nextElement();
                if (token != null && token.length() != 0) {
                    styleList.add(token);
                }
            }
        }

        final String use = element.getAttributeValue(Soap.BODY_USE);
        if (Soap.ENUM_USE_ENCODED.equals(use)) {
            body.setUse(SoapUseType.ENCODED_LITERAL);
        } else if (Soap.ENUM_USE_LITERAL.equals(use)) {
            body.setUse(SoapUseType.LITERAL_LITERAL);
        }
    }

    protected void doProcess( final SoapHeader header,
                              final Element element ) {
        final String namespace = element.getAttributeValue(Soap.HEADER_NAMESPACE);
        header.setNamespace(namespace);

        final String message = element.getAttributeValue(Soap.HEADER_MESSAGE);
        header.setMessage(message);

        final String parts = element.getAttributeValue(Soap.HEADER_PARTS);
        if (parts != null && parts.trim().length() != 0) {
            final List partList = header.getParts();
            final StringTokenizer tokenizer = new StringTokenizer(parts, NCNAMES_DELIM);
            while (tokenizer.hasMoreElements()) {
                final String token = (String)tokenizer.nextElement();
                if (token != null && token.length() != 0) {
                    partList.add(token);
                }
            }
        }

        final String styles = element.getAttributeValue(Soap.HEADER_ENCODINGSTYLE);
        if (styles != null && styles.trim().length() != 0) {
            final List styleList = header.getEncodingStyles();
            final StringTokenizer tokenizer2 = new StringTokenizer(styles, ANYURI_DELIM);
            while (tokenizer2.hasMoreElements()) {
                final String token = (String)tokenizer2.nextElement();
                if (token != null && token.length() != 0) {
                    styleList.add(token);
                }
            }
        }

        final String use = element.getAttributeValue(Soap.HEADER_USE);
        if (Soap.ENUM_USE_ENCODED.equals(use)) {
            header.setUse(SoapUseType.ENCODED_LITERAL);
        } else if (Soap.ENUM_USE_LITERAL.equals(use)) {
            header.setUse(SoapUseType.LITERAL_LITERAL);
        }

        // Process the 'soap:headerfault' child ...
        final Element headerFaultElement = element.getChild(Soap.HEADERFAULT);
        if (headerFaultElement != null) {
            final SoapHeaderFault headerFault = this.soapFactory.createSoapHeaderFault();
            doProcess(headerFault, headerFaultElement);
            header.setHeaderFault(headerFault);
        }
    }

    protected void doProcess( final SoapAddress address,
                              final Element element ) {
        final String location = element.getAttributeValue(Soap.ADDRESS_LOCATION);
        address.setLocation(location);
    }

    protected void doProcess( final SoapOperation operation,
                              final Element element ) {
        final String action = element.getAttributeValue(Soap.OPERATION_SOAPACTION);
        operation.setAction(action);

        final String style = element.getAttributeValue(Soap.OPERATION_STYLE);
        if (Soap.ENUM_STYLE_RPC.equals(style)) {
            operation.setStyle(SoapStyleType.RPC_LITERAL);
        } else if (Soap.ENUM_STYLE_DOC.equals(style)) {
            operation.setStyle(SoapStyleType.DOCUMENT_LITERAL);
        }
    }

    protected void doProcess( final SoapFault fault,
                              final Element element ) {
        final String namespace = element.getAttributeValue(Soap.FAULT_NAMESPACE);
        fault.setNamespace(namespace);

        final String styles = element.getAttributeValue(Soap.FAULT_ENCODINGSTYLE);
        if (styles != null && styles.trim().length() != 0) {
            final List styleList = fault.getEncodingStyles();
            final StringTokenizer tokenizer2 = new StringTokenizer(styles, ANYURI_DELIM);
            while (tokenizer2.hasMoreElements()) {
                final String token = (String)tokenizer2.nextElement();
                if (token != null && token.length() != 0) {
                    styleList.add(token);
                }
            }
        }

        final String use = element.getAttributeValue(Soap.FAULT_USE);
        if (Soap.ENUM_USE_ENCODED.equals(use)) {
            fault.setUse(SoapUseType.ENCODED_LITERAL);
        } else if (Soap.ENUM_USE_LITERAL.equals(use)) {
            fault.setUse(SoapUseType.LITERAL_LITERAL);
        }
    }

    protected void doProcess( final SoapHeaderFault headerFault,
                              final Element element ) {
        this.doProcess((SoapFault)headerFault, element);

        final String message = element.getAttributeValue(Soap.HEADERFAULT_MESSAGE);
        headerFault.setMessage(message);

        final String parts = element.getAttributeValue(Soap.HEADERFAULT_PARTS);
        if (parts != null && parts.trim().length() != 0) {
            final List partList = headerFault.getParts();
            final StringTokenizer tokenizer = new StringTokenizer(parts, NCNAMES_DELIM);
            while (tokenizer.hasMoreElements()) {
                final String token = (String)tokenizer.nextElement();
                if (token != null && token.length() != 0) {
                    partList.add(token);
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // HTTP Processing methods
    // -----------------------------------------------------------------------

    protected void doProcess( final HttpBinding binding,
                              final Element element ) {
        final String verb = element.getAttributeValue(Http.BINDING_VERB);
        binding.setVerb(verb);

    }

    protected void doProcess( final HttpAddress address,
                              final Element element ) {
        final String location = element.getAttributeValue(Http.ADDRESS_LOCATION);
        address.setLocation(location);

    }

    protected void doProcess( final HttpOperation operation,
                              final Element element ) {
        final String location = element.getAttributeValue(Http.OPERATION_LOCATION);
        operation.setLocation(location);
    }

    // -----------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------

    protected void addError( final XMLResource resource,
                             final String msg ) {
        final List errors = resource.getErrors();
        final WsdlDiagnostic diag = new WsdlDiagnostic(msg);
        errors.add(diag);
    }

    protected void addWarning( final XMLResource resource, // NO_UCD
                               final String msg ) {
        final List warnings = resource.getWarnings();
        final WsdlDiagnostic diag = new WsdlDiagnostic(msg);
        warnings.add(diag);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.xmi.XMLLoad#createDefaultHandler()
     */
    public XMLDefaultHandler createDefaultHandler() {
        throw new UnsupportedOperationException();
    }
}
