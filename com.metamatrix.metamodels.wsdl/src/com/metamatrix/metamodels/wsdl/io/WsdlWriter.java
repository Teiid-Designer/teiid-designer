/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.wsdl.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.DOMHandler;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.NodeList;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.JdomHelper;
import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.BindingInput;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.BindingOutput;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
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
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpAddress;
import com.metamatrix.metamodels.wsdl.http.HttpBinding;
import com.metamatrix.metamodels.wsdl.http.HttpOperation;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.mime.MimeContent;
import com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.mime.MimePart;
import com.metamatrix.metamodels.wsdl.soap.SoapAddress;
import com.metamatrix.metamodels.wsdl.soap.SoapBinding;
import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapFault;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;
import com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault;
import com.metamatrix.metamodels.wsdl.soap.SoapOperation;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapStyleType;
import com.metamatrix.metamodels.wsdl.soap.SoapUseType;

/**
 * This class provides the ability to write out a WSDL model into a standard WSDL-compliant XML file.
 * <p>
 * RMH 8/3/04 - Implementation note: I tried using the XMLSaveImpl and providing a new WsdlHelper and a WsdlXmlMap class, but ran
 * into a couple of problems. First, when writing out the file, I was having problems with the namespaces being written correctly.
 * I suspect this was a problem with my usage of the EMF framework.
 * </p>
 * <p>
 * Second, the EMF framework appears to write out XML documents without properly handling namespace declarations that are below
 * the top-level element.
 * </p>
 * <p>
 * For these reasons, I've completely implemented my own XMLSave, which uses JDOM to do all the serialization to XML.
 * </p>
 *
 * @since 4.2
 */
public class WsdlWriter implements XMLSave, WsdlConstants {

    public static final String ENCODING_UTF8    = "UTF-8"; //$NON-NLS-1$
    public static final String ENCODING_DEFAULT = ENCODING_UTF8;

    private String encoding;
    private String indentation;
    private boolean insertNewlines;
    private Namespace wsdlNamespace;
    private Namespace soapNamespace;
    private Namespace httpNamespace;
    private Namespace mimeNamespace;
    private boolean useSoap;
    private boolean useHttp;
    private boolean useMime;

    /**
     * @see org.eclipse.emf.ecore.xmi.XMLSave#save(org.eclipse.emf.ecore.xmi.XMLResource, java.io.OutputStream, java.util.Map)
     * @since 4.2
     */
    public void save(final XMLResource resource, final OutputStream outputStream, final Map options) throws IOException {
        ArgCheck.isNotNull(resource);
        ArgCheck.isNotNull(outputStream);
        init(resource,options);
        //final Map options = loadOptions != null ? loadOptions : Collections.EMPTY_MAP;

        // Generate the content ...
        Element wsdlElement = null;
        final Iterator iter = resource.getContents().iterator();
        while ( iter.hasNext() ) {
            final Object obj = iter.next();
            if ( obj instanceof Definitions ) {
                wsdlElement = doGenerate((Definitions)obj);
            }
        }

        // Make sure to generate at least one root-level element ...
        if ( wsdlElement == null ) {
            wsdlElement = doGenerate((Definitions)null);
        }

        // Create the JDOM document ...
        final Document doc = new Document(wsdlElement);

        // Write the JDOM document to the stream ...
        doWrite(doc, outputStream, options);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.XMLSave#save(org.eclipse.emf.ecore.xmi.XMLResource, org.w3c.dom.Document, java.util.Map, org.eclipse.emf.ecore.xmi.DOMHandler)
     * @since 4.3
     */
    public org.w3c.dom.Document save(final XMLResource resource,
                                     final org.w3c.dom.Document document,
                                     final Map options,
                                     final DOMHandler handler) {
        throw new UnsupportedOperationException();
    }

    /**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.xmi.XMLSave#save(org.eclipse.emf.ecore.xmi.XMLResource, java.io.Writer, java.util.Map)
	 */
	public void save( XMLResource resource,
                      Writer writer,
                      Map<?, ?> options ) throws IOException {
        ArgCheck.isNotNull(resource);
		ArgCheck.isNotNull(writer);
		init(resource, options);
		// final Map options = loadOptions != null ? loadOptions : Collections.EMPTY_MAP;

		// Generate the content ...
		Element wsdlElement = null;
		final Iterator iter = resource.getContents().iterator();
		while (iter.hasNext()) {
			final Object obj = iter.next();
			if (obj instanceof Definitions) {
				wsdlElement = doGenerate((Definitions)obj);
			}
		}

		// Make sure to generate at least one root-level element ...
		if (wsdlElement == null) {
			wsdlElement = doGenerate((Definitions)null);
		}

		// Create the JDOM document ...
		final Document doc = new Document(wsdlElement);

		// Write the JDOM document to the stream ...
		doWrite(doc, writer, options);
    }

    protected void init(final XMLResource resource, final Map options) {
        final Object insertNewLinesValue = options.get(WsdlResourceImpl.OPTION_INSERT_NEWLINES);
        if ( insertNewLinesValue != null && Boolean.FALSE.equals(insertNewLinesValue)) {
            insertNewlines = false;
        } else {
            insertNewlines = true;
        }

        String indentationValue = (String)options.get(WsdlResourceImpl.OPTION_INDENTATION);
        indentation = indentationValue != null ?
                      indentationValue :
                      WsdlResourceImpl.DEFAULT_INDENTATION;

        if (options.containsKey(XMLResource.OPTION_ENCODING)) {
            encoding = (String)options.get(XMLResource.OPTION_ENCODING);
        } else {
            encoding = ENCODING_DEFAULT;
        }

        // See what namespaces will be required ...
        final Iterator treeIter = resource.getAllContents();
        while (treeIter.hasNext()) {
            final EObject theEObject = (EObject)treeIter.next();
            final EClass theEClass = theEObject.eClass();
            if (theEClass.getEPackage() == SoapPackage.eINSTANCE ) {
                useSoap = true;
            } else if (theEClass.getEPackage() == HttpPackage.eINSTANCE ) {
                useHttp = true;
            } else if (theEClass.getEPackage() == MimePackage.eINSTANCE ) {
                useMime = true;
            }
            if ( useSoap && useHttp && useMime ) {
                break;  // found them all, so stop
            }
        }

    }

    /**
     * @param doc
     * @param outputStream
     * @since 4.2
     */
    protected void doWrite(final Document doc, final OutputStream outputStream, final Map options) throws IOException {
        // Write the document ...
        Format format = JdomHelper.getFormat(indentation, insertNewlines);
        format.setEncoding(encoding);
        XMLOutputter outputter = new XMLOutputter(format);
        outputter.output(doc, outputStream);
    }

    /**
	 * @param doc
	 * @param outputStream
	 * @since 4.2
	 */
	protected void doWrite( final Document doc,
	                        final Writer writer,
	                        final Map options ) throws IOException {
		// Write the document ...
		Format format = JdomHelper.getFormat(indentation, insertNewlines);
		format.setEncoding(encoding);
		XMLOutputter outputter = new XMLOutputter(format);
		outputter.output(doc, writer);
	}

    // ------------------  W S D L   G E N E R A T I O N    M E T H O D S   ---------------------

    /**
     * @param defns the definitions; may be null if the basic element is to be generated
     * @param parentElement the parent XML element
     * @return the generated element
     */
    protected Element doGenerate(final Definitions defns) {
        // Look for the namespaces ...

        // Create the default WSDL namespace ...
        final NamespaceDeclaration wsdlDecl = doFindNamespace(WsdlPackage.eNS_URI,defns,true);
        this.wsdlNamespace = wsdlDecl.getPrefix() != null ?
                              Namespace.getNamespace(wsdlDecl.getPrefix(),wsdlDecl.getUri()) :
                              Namespace.getNamespace(wsdlDecl.getUri());

        // Create the SOAP, HTTP, and MIME namespaces ...
        if ( useSoap ) {
            final NamespaceDeclaration nsDecl = doFindNamespace(WsdlPackage.eNS_URI,defns,true);
            this.soapNamespace = nsDecl.getPrefix() != null ?
                                  Namespace.getNamespace(nsDecl.getPrefix(),nsDecl.getUri()) :
                                  Namespace.getNamespace(nsDecl.getUri());
        }
        if (useHttp ) {
            final NamespaceDeclaration nsDecl = doFindNamespace(WsdlPackage.eNS_URI,defns,true);
            this.httpNamespace = nsDecl.getPrefix() != null ?
                                  Namespace.getNamespace(nsDecl.getPrefix(),nsDecl.getUri()) :
                                  Namespace.getNamespace(nsDecl.getUri());
        }
        if ( useMime ) {
            final NamespaceDeclaration nsDecl = doFindNamespace(WsdlPackage.eNS_URI,defns,true);
            this.mimeNamespace = nsDecl.getPrefix() != null ?
                                  Namespace.getNamespace(nsDecl.getPrefix(),nsDecl.getUri()) :
                                  Namespace.getNamespace(nsDecl.getUri());
        }

        // Create the element ...
        final Element element = new Element(DEFINITIONS,this.wsdlNamespace);

        // Add the known namespaces ...
        element.addNamespaceDeclaration(this.wsdlNamespace);
        if ( useSoap ) {
            element.addNamespaceDeclaration(this.soapNamespace);
        }
        if ( useHttp ) {
            element.addNamespaceDeclaration(this.httpNamespace);
        }
        if ( useMime ) {
            element.addNamespaceDeclaration(this.mimeNamespace);
        }

        // Add any other namespaces (do this before adding attributes) ...
        addNamespaces(defns,element);

        // Add the attributes ...
        addAttribute(element,DEFINITIONS_NAME,this.wsdlNamespace,defns.getName());
        addAttribute(element,DEFINITIONS_TARGETNAMESPACE,this.wsdlNamespace,defns.getTargetNamespace());

        // Process the contents (in proper WS-I order) ...
        doProcess(defns.getDocumentation(),element);
        doProcess(defns.getElements(),element);
        doProcess(defns.getImports(),element);
        doProcess(defns.getTypes(),element);
        doProcess(defns.getMessages(),element);
        doProcess(defns.getPortTypes(),element);
        doProcess(defns.getBindings(),element);
        doProcess(defns.getServices(),element);

        return element;

    }

    protected Element doGenerate(final BindingFault bindingFault, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(BINDINGFAULT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,BINDINGFAULT_NAME,this.wsdlNamespace,bindingFault.getName());

        return element;
    }

    protected Element doGenerate(final BindingOutput bindingOutput, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(BINDINGOUTPUT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,BINDINGOUTPUT_NAME,this.wsdlNamespace,bindingOutput.getName());

        return element;
    }

    protected Element doGenerate(final BindingInput bindingInput, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(BINDINGINPUT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,BINDINGINPUT_NAME,this.wsdlNamespace,bindingInput.getName());

        return element;
    }

    protected Element doGenerate(final BindingOperation bindingOperation, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(BINDINGOPERATION,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,BINDINGOPERATION_NAME,this.wsdlNamespace,bindingOperation.getName());

        return element;
    }

    protected Element doGenerate(final Fault fault, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(FAULT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,FAULT_NAME,this.wsdlNamespace,fault.getName());
        addAttribute(element,FAULT_MESSAGE,this.wsdlNamespace,fault.getMessage());

        return element;
    }

    protected Element doGenerate(final Output output, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(OUTPUT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,OUTPUT_NAME,this.wsdlNamespace,output.getName());
        addAttribute(element,OUTPUT_MESSAGE,this.wsdlNamespace,output.getMessage());

        return element;
    }

    protected Element doGenerate(final Input input, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(INPUT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,INPUT_NAME,this.wsdlNamespace,input.getName());
        addAttribute(element,INPUT_MESSAGE,this.wsdlNamespace,input.getMessage());

        return element;
    }

    protected Element doGenerate(final Operation operation, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(OPERATION,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,OPERATION_NAME,this.wsdlNamespace,operation.getName());
        addAttribute(element,OPERATION_PARAMETER_ORDER,this.wsdlNamespace,operation.getParameterOrder());

        return element;
    }

    protected Element doGenerate(final MessagePart messagePart, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(PART,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,PART_NAME,this.wsdlNamespace,messagePart.getName());
        addAttribute(element,PART_ELEMENT,this.wsdlNamespace,messagePart.getElement());
        addAttribute(element,PART_TYPE,this.wsdlNamespace,messagePart.getType());

        return element;
    }

    protected Element doGenerate(final Types types, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(TYPES,this.wsdlNamespace);

        return element;
    }

    protected Element doGenerate(final Port port, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(PORT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,PORT_NAME,this.wsdlNamespace,port.getName());
        addAttribute(element,PORT_BINDING,this.wsdlNamespace,port.getBinding());

        return element;
    }

    protected Element doGenerate(final Import import_, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(IMPORT,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,IMPORT_NAMESPACE,this.wsdlNamespace,import_.getNamespace());
        addAttribute(element,IMPORT_LOCATION,this.wsdlNamespace,import_.getLocation());

        return element;
    }

    protected Element doGenerate(final Service service, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(SERVICE,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,SERVICE_NAME,this.wsdlNamespace,service.getName());

        return element;
    }

    protected Element doGenerate(final Binding binding, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(BINDING,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,BINDING_NAME,this.wsdlNamespace,binding.getName());
        addAttribute(element,BINDING_TYPE,this.wsdlNamespace,binding.getType());

        return element;
    }

    protected Element doGenerate(final PortType portType, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(PORTTYPE,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,PORTTYPE_NAME,this.wsdlNamespace,portType.getName());

        return element;
    }

    protected Element doGenerate(final Message message, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(MESSAGE,this.wsdlNamespace);

        // Add the attributes ...
        addAttribute(element,MESSAGE_NAME,this.wsdlNamespace,message.getName());

        return element;
    }

    protected Element doGenerate(final Documentation documentation, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(DOCUMENTATION,this.wsdlNamespace);
        element.setText(documentation.getTextContent());
        return element;
    }

    protected Element doGenerate(final com.metamatrix.metamodels.wsdl.Attribute attribute, final Element parentElement) {
        final String uri = attribute.getNamespaceUri();
        final Namespace namespace = findXmlNamespace(parentElement,uri);
        addAttribute(parentElement,attribute.getName(),namespace,attribute.getValue());
        return null;
    }

    protected Element doGenerate(final com.metamatrix.metamodels.wsdl.Element element, final Element parentElement) {
        final String uri = element.getNamespaceUri();
        Namespace namespace = findXmlNamespace(parentElement,uri);
        if ( namespace == null ) {
            namespace = Namespace.NO_NAMESPACE;
        }
        final Element result = new Element(element.getName(),namespace);

        // Add any content ...
        final String text = element.getTextContent();
        if ( text != null ) {
            result.setText(text);
        }

        return result;
    }

    // ------------------  S O A P   G E N E R A T I O N    M E T H O D S   ---------------------

    protected Element doGenerate(final SoapBinding soapBinding, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Soap.BINDING,this.soapNamespace);

        // Add the attributes ...
        addAttribute(element,Soap.BINDING_STYLE,null,getValue(soapBinding.getStyle()));
        addAttribute(element,Soap.BINDING_TRANSPORT,null,soapBinding.getTransport());

        return element;
    }

    protected Element doGenerate(final SoapOperation soapOperation, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Soap.OPERATION,this.soapNamespace);

        // Add the attributes ...
        addAttribute(element,Soap.OPERATION_STYLE,null,getValue(soapOperation.getStyle()));
        addAttribute(element,Soap.OPERATION_SOAPACTION,null,soapOperation.getAction());

        return element;
    }

    protected Element doGenerate(final SoapBody soapBody, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Soap.BODY,this.soapNamespace);

        // Add the attributes ...
        addAttribute(element,Soap.BODY_ENCODINGSTYLE,null,getStringValues(soapBody.getEncodingStyles()));
        addAttribute(element,Soap.BODY_NAMESPACE,null,soapBody.getNamespace());
        addAttribute(element,Soap.BODY_PARTS,null,getStringValues(soapBody.getParts()));
        addAttribute(element,Soap.BODY_USE,null,getValue(soapBody.getUse()));

        return element;
    }

    protected Element doGenerate(final SoapHeader soapHeader, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Soap.HEADER,this.soapNamespace);

        // Add the attributes ...
        addAttribute(element,Soap.HEADER_ENCODINGSTYLE,null,getStringValues(soapHeader.getEncodingStyles()));
        addAttribute(element,Soap.HEADER_NAMESPACE,null,soapHeader.getNamespace());
        addAttribute(element,Soap.HEADER_MESSAGE,null,soapHeader.getMessage());
        addAttribute(element,Soap.HEADER_PARTS,null,getStringValues(soapHeader.getParts()));
        addAttribute(element,Soap.HEADER_USE,null,getValue(soapHeader.getUse()));

        return element;
    }

    protected Element doGenerate(final SoapFault soapFault, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Soap.FAULT,this.soapNamespace);

        // Add the attributes ...
        addAttribute(element,Soap.FAULT_ENCODINGSTYLE,null,getStringValues(soapFault.getEncodingStyles()));
        addAttribute(element,Soap.FAULT_NAMESPACE,null,soapFault.getNamespace());
        addAttribute(element,Soap.FAULT_USE,null,getValue(soapFault.getUse()));

        return element;
    }

    protected Element doGenerate(final SoapHeaderFault soapHeaderFault, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Soap.HEADERFAULT,this.soapNamespace);

        // Add the attributes ...
        addAttribute(element,Soap.HEADERFAULT_ENCODINGSTYLE,null,getStringValues(soapHeaderFault.getEncodingStyles()));
        addAttribute(element,Soap.HEADERFAULT_NAMESPACE,null,soapHeaderFault.getNamespace());
        addAttribute(element,Soap.HEADERFAULT_MESSAGE,null,soapHeaderFault.getMessage());
        addAttribute(element,Soap.HEADERFAULT_PARTS,null,getStringValues(soapHeaderFault.getParts()));
        addAttribute(element,Soap.HEADERFAULT_USE,null,getValue(soapHeaderFault.getUse()));

        return element;
    }

    protected Element doGenerate(final SoapAddress soapAddress, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Soap.ADDRESS,this.soapNamespace);

        // Add the attributes ...
        addAttribute(element,Soap.ADDRESS_LOCATION,null,soapAddress.getLocation());

        return element;
    }

    protected String getStringValues( final List list ) {
        StringBuffer buffer = null;
        final Iterator iter = list.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if ( obj instanceof String ) {
                if ( buffer == null ) {
                    buffer = new StringBuffer();
                } else {
                    buffer.append(NCNAMES_DELIM);
                }
                buffer.append(obj.toString());
            }
        }
        return buffer != null ? buffer.toString() : null;
    }

    protected String getValue( final SoapStyleType type ) {
        if ( type != null ) {
            switch (type.getValue() ) {
                case SoapStyleType.DOCUMENT: {
                    return Soap.ENUM_STYLE_DOC;
                }
                case SoapStyleType.RPC: {
                    return Soap.ENUM_STYLE_RPC;
                }
            }
        }
        return null;
    }

    protected String getValue( final SoapUseType type ) {
        if ( type != null ) {
            switch (type.getValue() ) {
                case SoapUseType.ENCODED: {
                    return Soap.ENUM_USE_ENCODED;
                }
                case SoapUseType.LITERAL: {
                    return Soap.ENUM_USE_LITERAL;
                }
            }
        }
        return null;
    }

    // ------------------  H T T P   G E N E R A T I O N    M E T H O D S   ---------------------

    protected Element doGenerate(final HttpOperation httpOperation, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Http.OPERATION,this.httpNamespace);

        // Add the attributes ...
        addAttribute(element,Http.OPERATION_LOCATION,null,httpOperation.getLocation());

        return element;
    }

    protected Element doGenerate(final HttpBinding httpBinding, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Http.BINDING,this.httpNamespace);

        // Add the attributes ...
        addAttribute(element,Http.BINDING_VERB,null,httpBinding.getVerb());

        return element;
    }

    protected Element doGenerate(final HttpAddress httpAddress, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Http.ADDRESS,this.httpNamespace);

        // Add the attributes ...
        addAttribute(element,Http.ADDRESS_LOCATION,null,httpAddress.getLocation());

        return element;
    }

    // ------------------  M I M E   G E N E R A T I O N    M E T H O D S   ---------------------

    protected Element doGenerate(final MimePart mimePart, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Mime.PART,this.mimeNamespace);
        return element;
    }

    protected Element doGenerate(final MimeMultipartRelated mimeMultipartRelated, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Mime.MULTIPARTRELATED,this.mimeNamespace);
        return element;
    }

    protected Element doGenerate(final MimeContent mimeContent, final Element parentElement) {
        // Create the element ...
        final Element element = new Element(Mime.CONTENT,this.mimeNamespace);

        // Add the attributes ...
        if ( mimeContent.getMessagePart() != null ) {
            addAttribute(element,Mime.CONTENT_PART,null,mimeContent.getMessagePart().getName());
        }

        return element;
    }

    // ------------------  X S D   G E N E R A T I O N    M E T H O D S   ---------------------

    protected Element doGenerate(final XSDSchema schema, final Element parentElement) {
        // Obtain the DOM document ...
        schema.updateElement(true); // update deeply
        final org.w3c.dom.Document domDoc = schema.getDocument();

        if ( domDoc == null || domDoc.getDocumentElement() == null ) {
            return null;
        }

        org.w3c.dom.Element schemaDomElement = null;
        final String[] schemaNS = new String[] {XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001,
                                                XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10,
                                                XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999};

        // If this was read in, the the 'schema' element will be at '//definitions/types/schema'.
        // However, if this was built from scratch, the 'schema' element will be at '//schema'.

        final org.w3c.dom.Element rootElement = domDoc.getDocumentElement();
        final String elementNsUri = rootElement.getNamespaceURI();
        final String elementLocalName = rootElement.getLocalName();
        for ( int i=0; i!=schemaNS.length; ++i ) {
            // See if the schema node (by NS and name) ...
            if ( schemaNS[i].equals(elementNsUri) && "schema".equals(elementLocalName) ) { //$NON-NLS-1$
                schemaDomElement = rootElement;
                break;
            }
        }

        if ( schemaDomElement == null ) {
            // Look for the 'definitions/types/schema' element in the DOM document ...
            final NodeList typesList = rootElement.getElementsByTagName(TYPES);
            if ( typesList != null && typesList.getLength() != 0 ) {
                final org.w3c.dom.Element typesElement = (org.w3c.dom.Element)typesList.item(0);

                // Look for the schema node (by NS) ...
                for ( int i=0; i!=schemaNS.length; ++i ) {
                    final NodeList typesChildren = typesElement.getElementsByTagNameNS(schemaNS[i],"schema"); //$NON-NLS-1$
                    if ( typesChildren != null && typesChildren.getLength() != 0 ) {
                        schemaDomElement = (org.w3c.dom.Element)typesChildren.item(0);
                        break;
                    }
                }
            }
        }

        if ( schemaDomElement == null ) {
            return null;

        }

        // Create a JDOM document from it ...
        final DOMBuilder builder = new DOMBuilder();
        final Element schemaElement = builder.build(schemaDomElement);

        // Remove the schemaElement from any existing document or parent ...
        final Document doc  = schemaElement.getDocument();
        final Parent parent = schemaElement.getParent();
        if ( parent != null ) {
            parent.removeContent(schemaElement);
        }
        doc.setRootElement(new Element("bogus")); //$NON-NLS-1$

        // And return it ...
        return schemaElement;
    }

    // ------------------------------------------------------------------------------------------------------
    //                          H E L P E R   M E T H O D S
    // ------------------------------------------------------------------------------------------------------

    protected void doProcessContents( final EObject parent, final Element parentElement ) {
        final Iterator iter = parent.eContents().iterator();
        while (iter.hasNext()) {
            final EObject theEObject = (EObject)iter.next();
            doProcess(theEObject,parentElement);
        }
    }

    protected void doProcess( final List children, final Element parentElement ) {
        final Iterator iter = children.iterator();
        while (iter.hasNext()) {
            final EObject theEObject = (EObject)iter.next();
            doProcess(theEObject,parentElement);
        }
    }

    protected void doProcess( final EObject theEObject, final Element parentElement ) {
        if ( theEObject == null ) {
            return;
        }
        boolean processContentDone = false;
        final EClass theEClass = theEObject.eClass();
        Element result = null;
        if (theEClass.eContainer() == WsdlPackage.eINSTANCE ) {
            switch (theEClass.getClassifierID()) {
                //case WsdlPackage.DEFINITIONS:{
                //    Definitions defns = (Definitions)theEObject;
                //    result = doGenerate(defns,parentElement);
                //    break;
                //}
                case WsdlPackage.DOCUMENTATION: {
                    Documentation documentation = (Documentation)theEObject;
                    result = doGenerate(documentation,parentElement);

                    // Process the contents
                    doProcess(documentation.getDeclaredNamespaces(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(documentation.getElements(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.ATTRIBUTE: {
                    com.metamatrix.metamodels.wsdl.Attribute attribute = (com.metamatrix.metamodels.wsdl.Attribute)theEObject;
                    result = doGenerate(attribute,parentElement);
                    break;
                }
                case WsdlPackage.MESSAGE: {
                    Message message = (Message)theEObject;
                    result = doGenerate(message,parentElement);

                    // Process the contents
                    doProcess(message.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(message.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(message.getElements(),result);

                    // Then the WSDL elements (in sequence)
                    doProcess(message.getParts(),result);

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.PORT_TYPE: {
                    PortType portType = (PortType)theEObject;
                    result = doGenerate(portType,parentElement);

                    // Process the contents
                    doProcess(portType.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(portType.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(portType.getAttributes(),result);

                    // Then the WSDL elements (in sequence)
                    doProcess(portType.getOperations(),result);
                    processContentDone = true;
                    break;
                }
                case WsdlPackage.BINDING: {
                    Binding binding = (Binding)theEObject;
                    result = doGenerate(binding,parentElement);

                    // Process the contents
                    doProcess(binding.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(binding.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(binding.getSoapBinding(),result);
                    doProcess(binding.getHttpBinding(),result);
                    doProcess(binding.getElements(),result);

                    // Then the WSDL operations ...
                    doProcess(binding.getBindingOperations(),result);
                    processContentDone = true;
                    break;
                }
                case WsdlPackage.SERVICE: {
                    Service service = (Service)theEObject;
                    result = doGenerate(service,parentElement);

                    // Process the contents
                    doProcess(service.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(service.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(service.getElements(),result);

                    // Then the WSDL elements (in sequence)
                    doProcess(service.getPorts(),result);
                    processContentDone = true;
                    break;
                }
                case WsdlPackage.IMPORT: {
                    Import import_ = (Import)theEObject;
                    result = doGenerate(import_,parentElement);

                    // Process the contents
                    doProcess(import_.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(import_.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(import_.getAttributes(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.PORT: {
                    Port port = (Port)theEObject;
                    result = doGenerate(port,parentElement);

                    // Process the contents
                    doProcess(port.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(port.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(port.getSoapAddress(),result);
                    doProcess(port.getHttpAddress(),result);
                    doProcess(port.getElements(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.ELEMENT: {
                    com.metamatrix.metamodels.wsdl.Element element = (com.metamatrix.metamodels.wsdl.Element)theEObject;
                    result = doGenerate(element,parentElement);
                    break;
                }
                case WsdlPackage.TYPES: {
                    Types types = (Types)theEObject;
                    result = doGenerate(types,parentElement);

                    // Process the contents
                    doProcess(types.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(types.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(types.getElements(),result);

                    // Then the WSDL elements (in sequence)
                    doProcess(types.getSchemas(),result);

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.MESSAGE_PART: {
                    MessagePart messagePart = (MessagePart)theEObject;
                    result = doGenerate(messagePart,parentElement);

                    // Process the contents
                    doProcess(messagePart.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(messagePart.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(messagePart.getAttributes(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.OPERATION: {
                    Operation operation = (Operation)theEObject;
                    result = doGenerate(operation,parentElement);

                    // First documentation ...
                    doProcess(operation.getDocumentation(),result);

                    // Then the WSDL elements (in sequence)
                    if ( operation.getInput() != null ) {
                        doProcess(operation.getInput(),result);
                        doProcess(operation.getOutput(),result);
                    } else {
                        doProcess(operation.getOutput(),result);
                        //doProcess(operation.getInput(),result);
                    }
                    doProcess(operation.getFaults(),result);
                    processContentDone = true;
                    break;
                }
                case WsdlPackage.INPUT: {
                    Input input = (Input)theEObject;
                    result = doGenerate(input,parentElement);

                    // Process the contents
                    doProcess(input.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(input.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(input.getAttributes(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.OUTPUT: {
                    Output output = (Output)theEObject;
                    result = doGenerate(output,parentElement);

                    // Process the contents
                    doProcess(output.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(output.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(output.getAttributes(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.FAULT: {
                    Fault fault = (Fault)theEObject;
                    result = doGenerate(fault,parentElement);

                    // Process the contents
                    doProcess(fault.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(fault.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(fault.getAttributes(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.BINDING_OPERATION: {
                    BindingOperation bindingOperation = (BindingOperation)theEObject;
                    result = doGenerate(bindingOperation,parentElement);

                    // Process the contents
                    doProcess(bindingOperation.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(bindingOperation.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(bindingOperation.getSoapOperation(),result);
                    doProcess(bindingOperation.getHttpOperation(),result);
                    doProcess(bindingOperation.getElements(),result);

                    // Then the WSDL elements (in sequence)
                    doProcess(bindingOperation.getBindingInput(),result);
                    doProcess(bindingOperation.getBindingOutput(),result);
                    doProcess(bindingOperation.getBindingFaults(),result);
                    processContentDone = true;
                    break;
                }
                case WsdlPackage.BINDING_INPUT: {
                    BindingInput bindingInput = (BindingInput)theEObject;
                    result = doGenerate(bindingInput,parentElement);

                    // Process the contents
                    doProcess(bindingInput.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(bindingInput.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(bindingInput.getSoapHeader(),result);
                    doProcess(bindingInput.getSoapBody(),result);
                    doProcess(bindingInput.getMimeElements(),result);
                    doProcess(bindingInput.getElements(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.BINDING_OUTPUT: {
                    BindingOutput bindingOutput = (BindingOutput)theEObject;
                    result = doGenerate(bindingOutput,parentElement);

                    // Process the contents
                    doProcess(bindingOutput.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(bindingOutput.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(bindingOutput.getSoapHeader(),result);
                    doProcess(bindingOutput.getSoapBody(),result);
                    doProcess(bindingOutput.getMimeElements(),result);
                    doProcess(bindingOutput.getElements(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                case WsdlPackage.BINDING_FAULT: {
                    BindingFault bindingFault = (BindingFault)theEObject;
                    result = doGenerate(bindingFault,parentElement);

                    // Process the contents
                    doProcess(bindingFault.getDeclaredNamespaces(),result);

                    // First documentation ...
                    doProcess(bindingFault.getDocumentation(),result);

                    // Then non-WSDL contents first (since schema says 'any' goes first in sequence) ...
                    doProcess(bindingFault.getSoapFault(),result);
                    doProcess(bindingFault.getElements(),result);

                    // Then the WSDL elements (in sequence)

                    processContentDone = true;
                    break;
                }
                //case WsdlPackage.NAMESPACE_DECLARATION: {
                //    NamespaceDeclaration namespaceDeclaration = (NamespaceDeclaration)theEObject;
                //    result = doGenerate(defns,parentElement);
                //    break;
                //}
            }
        } else if (theEClass.eContainer() == SoapPackage.eINSTANCE) {
            switch (theEClass.getClassifierID()) {
                case SoapPackage.SOAP_ADDRESS: {
                    SoapAddress soapAddress = (SoapAddress)theEObject;
                    result = doGenerate(soapAddress,parentElement);
                    break;
                }
                case SoapPackage.SOAP_HEADER_FAULT: {
                    SoapHeaderFault soapHeaderFault = (SoapHeaderFault)theEObject;
                    result = doGenerate(soapHeaderFault,parentElement);
                    break;
                }
                case SoapPackage.SOAP_FAULT: {
                    SoapFault soapFault = (SoapFault)theEObject;
                    result = doGenerate(soapFault,parentElement);
                    break;
                }
                case SoapPackage.SOAP_HEADER: {
                    SoapHeader soapHeader = (SoapHeader)theEObject;
                    result = doGenerate(soapHeader,parentElement);
                    break;
                }
                case SoapPackage.SOAP_BODY: {
                    SoapBody soapBody = (SoapBody)theEObject;
                    result = doGenerate(soapBody,parentElement);
                    break;
                }
                case SoapPackage.SOAP_OPERATION: {
                    SoapOperation soapOperation = (SoapOperation)theEObject;
                    result = doGenerate(soapOperation,parentElement);
                    break;
                }
                case SoapPackage.SOAP_BINDING: {
                    SoapBinding soapBinding = (SoapBinding)theEObject;
                    result = doGenerate(soapBinding,parentElement);
                    break;
                }
            }
        } else if (theEClass.eContainer() == HttpPackage.eINSTANCE) {
            switch (theEClass.getClassifierID()) {
                case HttpPackage.HTTP_ADDRESS: {
                    HttpAddress httpAddress = (HttpAddress)theEObject;
                    result = doGenerate(httpAddress,parentElement);
                    break;
                }
                case HttpPackage.HTTP_BINDING: {
                    HttpBinding httpBinding = (HttpBinding)theEObject;
                    result = doGenerate(httpBinding,parentElement);
                    break;
                }
                case HttpPackage.HTTP_OPERATION: {
                    HttpOperation httpOperation = (HttpOperation)theEObject;
                    result = doGenerate(httpOperation,parentElement);
                    break;
                }
            }
        } else if (theEClass.eContainer() == MimePackage.eINSTANCE) {
            switch (theEClass.getClassifierID()) {
                case MimePackage.MIME_CONTENT: {
                    MimeContent mimeContent = (MimeContent)theEObject;
                    result = doGenerate(mimeContent,parentElement);
                    break;
                }
                case MimePackage.MIME_MULTIPART_RELATED: {
                    MimeMultipartRelated mimeMultipartRelated = (MimeMultipartRelated)theEObject;
                    result = doGenerate(mimeMultipartRelated,parentElement);
                    break;
                }
                case MimePackage.MIME_PART: {
                    MimePart mimePart = (MimePart)theEObject;
                    result = doGenerate(mimePart,parentElement);
                    break;
                }
            }
        } else if (theEClass.eContainer() == XSDPackage.eINSTANCE) {
            switch(theEClass.getClassifierID()) {
                case XSDPackage.XSD_SCHEMA: {
                    XSDSchema schema = (XSDSchema)theEObject;
                    result = doGenerate(schema,parentElement);
                    break;
                }
            }
        }

        if ( result != null ) {
            parentElement.addContent(result);
        }


        if ( !processContentDone ) {
            Element parentElementForChildren = parentElement;
            if ( result != null ) {
                // Add the element to the parent ...
                parentElementForChildren = result;
            }
            // Process the contents and use the same parent ...
            doProcessContents(theEObject,parentElementForChildren);
        }
    }

    protected void addNamespaces(final NamespaceDeclarationOwner owner, final Element element) {
        final Map existingNamespaces = new HashMap();
        final List existingDeclarations = element.getAdditionalNamespaces();
        final Iterator iter = existingDeclarations.iterator();
        while (iter.hasNext()) {
            final Namespace namespace = (Namespace)iter.next();
            existingNamespaces.put(namespace.getURI(),namespace);
        }

        // Iterate through the declared namespaces ...
        final Iterator iter2 = owner.getDeclaredNamespaces().iterator();
        while (iter2.hasNext()) {
            final NamespaceDeclaration nsDecl = (NamespaceDeclaration)iter2.next();
            final String prefix = nsDecl.getPrefix();
            final String uri = nsDecl.getUri();

            Namespace namespace = (Namespace) existingNamespaces.get(uri);
            if ( namespace == null && uri != null && uri.trim().length() != 0 ) {
                // Create the namespace on the JDOM document ...
                namespace = prefix != null ?
                            Namespace.getNamespace(prefix,uri) :
                            Namespace.getNamespace(uri);

                // Add the namespace to the element
                element.addNamespaceDeclaration(namespace);
                existingNamespaces.put(uri,namespace);
            }

            // See if this namespace is one to remember ...
            if ( WsdlPackage.eNS_URI.equals(uri) ) {
                // It's the WSDL namespace ...
                this.wsdlNamespace = namespace;
            } else if ( SoapPackage.eNS_URI.equals(uri) ) {
                // It's the SOAP namespace ...
                this.soapNamespace = namespace;
            } else if ( HttpPackage.eNS_URI.equals(uri) ) {
                // It's the HTTP namespace ...
                this.httpNamespace = namespace;
            } else if ( MimePackage.eNS_URI.equals(uri) ) {
                // It's the MIME namespace ...
                this.mimeNamespace = namespace;
            }

        }
    }

    protected Namespace findXmlNamespace( final Element element, final String uri ) {
        // Iterate through the declared namespaces ...
        final Iterator iter = element.getAdditionalNamespaces().iterator();
        while (iter.hasNext()) {
            final Namespace ns = (Namespace)iter.next();
            if ( uri.equals(ns.getURI()) ) {
                return ns;
            }
        }

        // Look in the parent ...
        final Parent parent = element.getParent();
        if ( parent != null  && parent instanceof Element ) {
            return findXmlNamespace((Element)parent,uri);
        }

        return null;    // none found
    }

    protected NamespaceDeclaration doFindNamespace( final String uri, final NamespaceDeclarationOwner owner,
                                                     final boolean createIfRequired ) {
        if ( uri == null ) {
            return null;
        }

        boolean foundDefaultNamespace = false;

        // Iterate through the declared namespaces ...
        final Iterator iter = owner.getDeclaredNamespaces().iterator();
        while (iter.hasNext()) {
            final NamespaceDeclaration nsDecl = (NamespaceDeclaration)iter.next();
            if ( uri.equals(nsDecl.getUri()) ) {
                return nsDecl;
            }

            final String prefix = nsDecl.getPrefix();
            if ( prefix == null || prefix.trim().length() == 0 ) {
                foundDefaultNamespace = true;
            }
        }

        if ( createIfRequired ) {
            final NamespaceDeclaration nsDecl = WsdlFactory.eINSTANCE.createNamespaceDeclaration();
            nsDecl.setUri(uri);
            if ( WsdlPackage.eNS_URI.equals(uri) ) {
                // It's the WSDL namespace ...
                if ( foundDefaultNamespace ) {
                    nsDecl.setPrefix(WsdlPackage.eNS_PREFIX);
                }
            } else if ( SoapPackage.eNS_URI.equals(uri) ) {
                // It's the SOAP namespace ...
                nsDecl.setPrefix(SoapPackage.eNS_PREFIX);
            } else if ( HttpPackage.eNS_URI.equals(uri) ) {
                // It's the HTTP namespace ...
                nsDecl.setPrefix(HttpPackage.eNS_PREFIX);
            } else if ( MimePackage.eNS_URI.equals(uri) ) {
                // It's the MIME namespace ...
                nsDecl.setPrefix(MimePackage.eNS_PREFIX);
            }
            return nsDecl;
        }

        return null;
    }

    protected Attribute addAttribute( final Element element, final String name, final Namespace namespace,
                                      final String value) {
       return addAttribute(element,name,namespace,value,false);
   }

    protected Attribute addAttribute( final Element element, final String name, final Namespace namespace,
                                      final String value, final boolean forceIfNullOrZeroLength ) {
       final String newValue = (value != null && value.trim().length() != 0) ? value.trim() : null;
       if ( forceIfNullOrZeroLength || newValue != null ) {
           final String v = newValue == null ? StringUtil.Constants.EMPTY_STRING : newValue;
           Namespace theNamespace = Namespace.NO_NAMESPACE;
           if ( namespace != null && namespace.getPrefix() != null && namespace.getPrefix().trim().length() != 0 ) {
               theNamespace = namespace;

               // See if the namespace is WSDL, WSDL/SOAP, WSDL/HTTP or WSDL/MIME, the attribute form default
               // is unqualified, so there should be no prefix ...
               final String uri = namespace.getURI();
               if ( WsdlPackage.eNS_URI.equals(uri) ) {
                   theNamespace = Namespace.NO_NAMESPACE;
               }
               else if ( SoapPackage.eNS_URI.equals(uri) ) {
                   theNamespace = Namespace.NO_NAMESPACE;
               }
               else if ( HttpPackage.eNS_URI.equals(uri) ) {
                   theNamespace = Namespace.NO_NAMESPACE;
               }
               else if ( MimePackage.eNS_URI.equals(uri) ) {
                   theNamespace = Namespace.NO_NAMESPACE;
               }
           }

           final Attribute attribute = theNamespace!=Namespace.NO_NAMESPACE ?
                                        new Attribute(name,v,theNamespace):
                                        new Attribute(name,v);
           element.setAttribute(attribute);
           return attribute;
       }
       return null;
   }

    /**
     * @see org.eclipse.emf.ecore.xmi.XMLSave#toDOM(org.eclipse.emf.ecore.xmi.XMLResource, org.w3c.dom.Document, org.eclipse.emf.ecore.xmi.DOMHandler, java.util.Map)
     * @since 4.3
     */
    public org.w3c.dom.Document toDOM(final XMLResource resource,
                                      final org.w3c.dom.Document document,
                                      final DOMHandler handler,
                                      final Map options) {
        throw new UnsupportedOperationException();
    }
}
