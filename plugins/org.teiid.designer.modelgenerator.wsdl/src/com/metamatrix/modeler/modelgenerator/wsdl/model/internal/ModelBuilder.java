/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.xml.namespace.QName;

import org.eclipse.xsd.XSDSchema;

import com.ibm.wsdl.ImportImpl;
import com.metamatrix.modeler.modelgenerator.wsdl.ModelGeneratorWsdlPlugin;
import com.metamatrix.modeler.modelgenerator.wsdl.SoapBindingInfo;
import com.metamatrix.modeler.modelgenerator.wsdl.TableBuilder;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Binding;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Fault;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;
import com.metamatrix.modeler.modelgenerator.wsdl.util.ExtendedWSDLReader;
import com.metamatrix.modeler.modelgenerator.wsdl.util.WSDLSchemaExtractor;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessor;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;
import com.metamatrix.ui.ICredentialsCommon.SecurityType;

/**
 * This class generates a lightweight model of the WSDL file leaving out a lot of the things from the javax.wsdl model and even
 * more from the eclipse emf wsdl model It uses IBM's WSDL4J model to build our own model Its lightweight to avoid memory
 * consumption problems that were seen in the schema importer The usage model is as follows: String myWSDL = "c:\temp\my.wsdl";
 * ModelBuilder builder = new ModelBuilder(); builder.setWSDL(myWSDL); if(!builder.isWSDLParsed()) { WSDLException myEx =
 * builder.getWSDLException(); handleExceptionInSomeWay(myEx); } Model wsdlModel = builder.getModel();
 */
public class ModelBuilder {

    private String m_wsdlURI;
    private SecurityType m_securityType;
    private String m_userName;
    private String m_password;

    private Definition m_wsdlDef;
    private Exception m_wsdlException;
    private XSDSchema[] m_schemas;
    private WSDLSchemaExtractor extractor;
    private static String DEFAULT_STYLE = "document"; //$NON-NLS-1$

    public ModelBuilder() {

    }

    public void setAuthentication(SecurityType securityType, String userName, String password) {
		m_securityType = securityType;
		m_userName = userName;
		m_password = password;
		m_wsdlDef = null;
    }

    public void setWSDL( String wsdlUri ) {
        m_wsdlURI = wsdlUri;
        try {
            m_wsdlDef = getDefinition();
        } catch (WSDLException wx) {
            m_wsdlDef = null;
            m_wsdlException = wx;
        }
        extractor = new WSDLSchemaExtractor();
    }

    private ExtendedWSDLReader getWSDLReader() {
        return new ExtendedWSDLReader();
    }

    private Definition getDefinition() throws WSDLException {
        ExtendedWSDLReader reader = getWSDLReader();
        Definition def;
        if (SecurityType.None.equals(m_securityType)) {
            def = reader.readWSDL(m_wsdlURI);
        } else {
            def = reader.readWSDL(m_wsdlURI, m_userName, m_password);
        }
        return def;
    }

    public boolean isWSDLParsed() {
        return !(m_wsdlDef == null);
    }

    public Exception getWSDLException() {
        return m_wsdlException;
    }

    public Model getModel() throws Exception {
        // wsdl isn't set or can't be read
        if (m_wsdlDef == null) return null;
        Model theModel = createModel();
        return theModel;
    }

    private Model createModel() throws Exception {
        Model theModel = new ModelImpl();
        Map namespaceMap = m_wsdlDef.getNamespaces();
        theModel.setNamespaces(namespaceMap);
        extractor.findSchema(m_wsdlURI, m_securityType, m_userName, m_password);

        // Get the embedded schema from imported WSDLs
        extractImportedWSDL(m_wsdlDef);

        m_schemas = extractor.getSchemas();
        setSchemaModel();
        theModel.setSchemas(m_schemas);
        Service[] svcs = createServices(getServices(), theModel);
        theModel.setServices(svcs);
        return theModel;
    }

    private void extractImportedWSDL( Definition def ) throws Exception {
        Map imports = def.getImports();
        if (!imports.isEmpty()) {
            Set keys = imports.keySet();
            for (Iterator iter = keys.iterator(); iter.hasNext();) {
                String namespace = (String)iter.next();
                List importImpls = (List)imports.get(namespace);
                for (Iterator itera = importImpls.iterator(); itera.hasNext();) {
                    ImportImpl impImpl = (ImportImpl)itera.next();
                    Definition imported = impImpl.getDefinition();
                    URI baseURI = new URI(imported.getDocumentBaseURI());
                    URL baseURL = baseURI.toURL();
                    extractor.findSchema(baseURL.toString(), m_securityType, m_userName, m_password);
                    extractImportedWSDL(imported);
                }

            }
        }
    }

    private Map getServices() {
        Map services = m_wsdlDef.getServices();
        Map imports = m_wsdlDef.getImports();
        if (!imports.isEmpty()) {
            Set keys = imports.keySet();
            for (Iterator iter = keys.iterator(); iter.hasNext();) {
                String namespace = (String)iter.next();
                List importImpls = (List)imports.get(namespace);
                for (Iterator itera = importImpls.iterator(); itera.hasNext();) {
                    ImportImpl impImpl = (ImportImpl)itera.next();
                    Map importedServices = impImpl.getDefinition().getServices();
                    services.putAll(importedServices);
                }

            }
        }
        return services;
    }

    private Service[] createServices( Map services,
                                      Model theModel ) {
        Service[] retVal = new Service[services.size()];
        int arrayCtr = 0;
        Iterator keyIter = services.keySet().iterator();
        while (keyIter.hasNext()) {
            QName key = (QName)keyIter.next();
            ServiceImpl impl = new ServiceImpl();
            impl.setModel(theModel);
            javax.wsdl.Service svc = (javax.wsdl.Service)services.get(key);
            impl.setId(key.toString());
            impl.setName(key.getLocalPart());
            impl.setNamespaceURI(key.getNamespaceURI());
            impl.setPorts(getPortsForService(impl, svc.getPorts()));
            retVal[arrayCtr++] = impl;
        }
        return retVal;
    }

    private Port[] getPortsForService( Service service,
                                       Map ports ) {
        Port[] retVal = new Port[ports.size()];
        int arrayCtr = 0;
        String bindingTypeURI = null;
        Iterator qnameIter = ports.keySet().iterator();
        while (qnameIter.hasNext()) {
            String name = (String)qnameIter.next();
            PortImpl port = new PortImpl(service);
            javax.wsdl.Port pt = (javax.wsdl.Port)ports.get(name);
            port.setId(name);
            port.setName(name);
            port.setNamespaceURI(service.getNamespaceURI());
            List port_extens_elements = pt.getExtensibilityElements();
            Iterator p_iter = port_extens_elements.iterator();
            while (p_iter.hasNext()) {
                Object pe_next = p_iter.next();
                if (pe_next instanceof SOAPAddress) {
                    SOAPAddress address = (SOAPAddress)pe_next;
                    bindingTypeURI = address.getElementType().getNamespaceURI();
                    port.setBindingTypeURI(bindingTypeURI);
                    port.setLocationURI(address.getLocationURI());
                } else {
                	if (pe_next instanceof SOAP12Address) {
                		SOAP12Address address = (SOAP12Address)pe_next;
                        bindingTypeURI = address.getElementType().getNamespaceURI();
                        port.setBindingTypeURI(bindingTypeURI);
                        port.setLocationURI(address.getLocationURI());
                } else {
                	if (pe_next instanceof HTTPAddress) {
                		HTTPAddress address = (HTTPAddress)pe_next;
                        bindingTypeURI = address.getElementType().getNamespaceURI();
                        port.setBindingTypeURI(bindingTypeURI);
                        port.setLocationURI(address.getLocationURI()); 
                	}
                }
                }
            }
            
            Binding binding = new BindingImpl(port);
            javax.wsdl.Binding bind = pt.getBinding();
            binding.setId(bind.getQName().toString());
            binding.setName(bind.getQName().getLocalPart());
            List extens = bind.getExtensibilityElements();
            Iterator iter = extens.iterator();
            while (iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof SOAPBinding) {
                    SOAPBinding soap = (SOAPBinding)next;
                    binding.setTransportURI(soap.getTransportURI());
                    binding.setStyle(soap.getStyle());
                }
            }
            binding.setOperations(getOperationsForBinding(binding, bind.getBindingOperations()));
            port.setBinding(binding);
            retVal[arrayCtr++] = port;
        }
        return retVal;
    }

    private Operation[] getOperationsForBinding( Binding binding,
                                                 List operations ) {
        Operation[] retVal = new Operation[operations.size()];
        int arrayPtr = 0;
        Iterator opIter = operations.iterator();
        while (opIter.hasNext()) {
            javax.wsdl.BindingOperation boper = (javax.wsdl.BindingOperation)opIter.next();
            javax.wsdl.Operation oper = boper.getOperation();
            Operation operation = new OperationImpl(binding);
            operation.setId(binding.getId() + "." + oper.getName()); //$NON-NLS-1$
            operation.setName(oper.getName());
            OperationType style = oper.getStyle();
            if (style != null) operation.setStyle(style.toString());
            operation.setInputMessage(getInputMessageForOperation(operation, oper.getInput(), boper.getBindingInput()));
            if (oper.getOutput() != null) {
                operation.setOutputMessage(getOutputMessageForOperation(operation, oper.getOutput(), boper.getBindingOutput()));
            }
            Map faults = oper.getFaults();
            Map bFaults = boper.getBindingFaults();
            operation.setFaults(getFaultsForOperation(operation, faults, bFaults));
            List elems = boper.getExtensibilityElements();
            for (Iterator iter = elems.iterator(); iter.hasNext();) {
                Object next = iter.next();
                if (next instanceof SOAPOperation) {
                    SOAPOperation soapO = (SOAPOperation)next;
                    operation.setSOAPAction(soapO.getSoapActionURI());
                    // sometimes the style is specified on the soap operation
                    if (binding.getStyle() == null) {
                        String soapStyle = soapO.getStyle();
                        if (soapStyle != null) {
                            binding.setStyle(soapStyle);
                        }
                    }
                } else if (next instanceof SOAP12Operation) {
                	SOAP12Operation soapO = (SOAP12Operation)next;
                    operation.setSOAPAction(soapO.getSoapActionURI());
                    // sometimes the style is specified on the soap operation
                    if (binding.getStyle() == null) {
                        String soapStyle = soapO.getStyle();
                        if (soapStyle != null) {
                            binding.setStyle(soapStyle);
                        }
                    }
                } 
            }
            if (operation.getBinding().getStyle() == null) {
            	//Per the spec, if the binding style is not set (rpc or document)
            	//the default should be "document".
            	//see http://www.w3.org/TR/wsdl#_soap:binding
            	operation.getBinding().setStyle(DEFAULT_STYLE);
            	createBindingInfo(operation);
            }
            retVal[arrayPtr++] = operation;
        }
        return retVal;
    }

    private Fault[] getFaultsForOperation( Operation operation,
                                           Map faults,
                                           Map bindingFaults ) {
        Fault[] faultArr = new Fault[faults.size()];
        Iterator faultIter = faults.keySet().iterator();
        Iterator faultBIter = bindingFaults.keySet().iterator();
        int ctr = 0;
        while (faultIter.hasNext()) {
            Object key = faultIter.next();
            Object bKey = faultBIter.next();
            javax.wsdl.Fault fault = (javax.wsdl.Fault)faults.get(key);
            BindingFault bFault = (BindingFault)bindingFaults.get(bKey);
            Fault newFault = new FaultImpl(operation);
            newFault.setName(fault.getName());
            newFault.setId(fault.getName());
            Message message = new MessageImpl(newFault);
            javax.wsdl.Message msg = fault.getMessage();
            message.setName(newFault.getName());
            message.setId(msg.getQName().toString());
            message.setType(Message.FAULT_TYPE);

            List elems = bFault.getExtensibilityElements();
            for (Iterator iter = elems.iterator(); iter.hasNext();) {
                Object next = iter.next();
                if (next instanceof SOAPBody) {
                    SOAPBody body = (SOAPBody)next;
                    if (body.getEncodingStyles() != null) {
                        StringBuffer buff = new StringBuffer();
                        for (Iterator encIter = body.getEncodingStyles().iterator(); encIter.hasNext();) {
                            buff.append(encIter.next().toString());
                            buff.append(" "); //$NON-NLS-1$
                        }
                        message.setEncodingStyle(buff.toString().trim());
                    }
                    message.setUse(body.getUse());
                    message.setNamespaceURI(body.getNamespaceURI());
                    if (body.getNamespaceURI() != null && !body.getNamespaceURI().equals("")) { //$NON-NLS-1$
                        Model theModel = operation.getBinding().getPort().getService().getModel();
                        theModel.addNamespaceToMap(body.getNamespaceURI());
                    }
                }
            }
            Part[] parts = getPartsForMessage(message, msg.getParts());
            message.setParts(parts);
            newFault.setMessage(message);
            faultArr[ctr++] = newFault;
        }
        return faultArr;
    }

    private Message getInputMessageForOperation( Operation operation,
                                                 Input input,
                                                 BindingInput bIn ) {
        Message inputMessage = new MessageImpl(operation);
        javax.wsdl.Message message = input.getMessage();
        inputMessage.setName(message.getQName().getLocalPart());
        inputMessage.setId(message.getQName().toString());
        inputMessage.setType(Message.REQUEST_TYPE);
        List elems = bIn.getExtensibilityElements();
        for (Iterator iter = elems.iterator(); iter.hasNext();) {
            Object next = iter.next();
            if (next instanceof SOAPBody) {
                SOAPBody body = (SOAPBody)next;
                if (body.getEncodingStyles() != null) {
                    StringBuffer buff = new StringBuffer();
                    for (Iterator encIter = body.getEncodingStyles().iterator(); encIter.hasNext();) {
                        buff.append(encIter.next().toString());
                        buff.append(" "); //$NON-NLS-1$
                    }
                    inputMessage.setEncodingStyle(buff.toString().trim());
                }
                inputMessage.setUse(body.getUse());
                inputMessage.setNamespaceURI(body.getNamespaceURI());
                if (body.getNamespaceURI() != null && !body.getNamespaceURI().equals("")) { //$NON-NLS-1$
                    Model theModel = operation.getBinding().getPort().getService().getModel();
                    theModel.addNamespaceToMap(body.getNamespaceURI());
                }
            }
        }

        Part[] parts = getPartsForMessage(inputMessage, message.getParts());
        inputMessage.setParts(parts);
        return inputMessage;
    }

    private Message getOutputMessageForOperation( Operation operation,
                                                  Output output,
                                                  BindingOutput bOut ) {
        Message outputMessage = new MessageImpl(operation);
        javax.wsdl.Message message = output.getMessage();
        outputMessage.setName(message.getQName().getLocalPart());
        outputMessage.setId(message.getQName().toString());
        outputMessage.setType(Message.RESPONSE_TYPE);
        List elems = bOut.getExtensibilityElements();
        for (Iterator iter = elems.iterator(); iter.hasNext();) {
            Object next = iter.next();
            if (next instanceof SOAPBody) {
                SOAPBody body = (SOAPBody)next;
                if (body.getEncodingStyles() != null) {
                    StringBuffer buff = new StringBuffer();
                    for (Iterator encIter = body.getEncodingStyles().iterator(); encIter.hasNext();) {
                        buff.append(encIter.next().toString());
                        buff.append(" "); //$NON-NLS-1$
                    }
                    outputMessage.setEncodingStyle(buff.toString().trim());
                }
                outputMessage.setUse(body.getUse());
                outputMessage.setNamespaceURI(body.getNamespaceURI());
                if (body.getNamespaceURI() != null && !body.getNamespaceURI().equals("")) { //$NON-NLS-1$
                    Model theModel = operation.getBinding().getPort().getService().getModel();
                    theModel.addNamespaceToMap(body.getNamespaceURI());
                }
            }
        }
        Part[] parts = getPartsForMessage(outputMessage, message.getParts());
        outputMessage.setParts(parts);
        return outputMessage;
    }

    private Part[] getPartsForMessage( Message msg,
                                       Map parts ) {
        Part[] partArr = new Part[parts.size()];
        int ctr = 0;
        Iterator partIter = parts.keySet().iterator();
        while (partIter.hasNext()) {
            Object key = partIter.next();
            javax.wsdl.Part part = (javax.wsdl.Part)parts.get(key);
            Part newPart = new PartImpl(msg);
            newPart.setName(part.getName());
            newPart.setId(part.getName());
            if (part.getElementName() != null) {
                newPart.setElementName(part.getElementName().getLocalPart());
                newPart.setElementNamespace(part.getElementName().getNamespaceURI());
            }
            if (part.getTypeName() != null) {
                newPart.setTypeName(part.getTypeName().getLocalPart());
                newPart.setTypeNamespace(part.getTypeName().getNamespaceURI());
            }
            Operation oper;
            if (newPart.getMessage().getFault() == null) {
                oper = newPart.getMessage().getOperation();
            } else {
                oper = newPart.getMessage().getFault().getOperation();
            }
            if (!resolvePart(newPart)) {
                oper.setCanModel(false);
                String message = ModelGeneratorWsdlPlugin.Util.getString("ModelBuilder.cannot.resolve.element"); //$NON-NLS-1$
                String fMsg = MessageFormat.format(message, newPart.getName(), newPart.getMessage().getName());
                oper.addProblemMessage(fMsg);
            }
            partArr[ctr++] = newPart;
        }
        return partArr;
    }

    private void setSchemaModel() throws Exception {
        // process the schemas and get the schema model
        SchemaProcessor processor = new SchemaProcessorImpl(null);
        processor.representTypes(true);
        processor.processSchemas(m_schemas);
        processor.getSchemaModel();
    }

    private boolean resolvePart( Part newPart ) {
        String elementNS = newPart.getElementNamespace();
        String typeNS = newPart.getTypeNamespace();
        // schema types are always good
        if (TableBuilder.XML_SCHEMA_URI.equalsIgnoreCase(elementNS) || TableBuilder.XML_SCHEMA_URI.equalsIgnoreCase(typeNS)) {
            return true;
        }

        boolean resolved = false;
        for (int i = 0; i < m_schemas.length; i++) {
            String tns = m_schemas[i].getTargetNamespace();
            if (null != tns) {
                if (tns.equals(typeNS)) resolved = true;
                if (tns.equals(elementNS)) resolved = true;
            }
        }
        return resolved;
    }

    private void createBindingInfo( Operation oper ) {
        Binding bind = oper.getBinding();
        Port targetport = bind.getPort();
        SoapBindingInfo info = new SoapBindingInfo();
        // info.setDestinationURL(bind.getTransportURI());
        info.setDestinationURL(targetport.getLocationURI());
        String docType = bind.getStyle();
        String encType = oper.getInputMessage().getEncodingStyle();
        if (docType.equalsIgnoreCase("RPC")) { //$NON-NLS-1$
            if (encType != null) {
                info.setStyle(SoapBindingInfo.STYLE_RPC_ENCODED);
            } else {
                info.setStyle(SoapBindingInfo.STYLE_RPC_LITERAL);
            }
        } else {
            if (encType != null) {
                info.setStyle(SoapBindingInfo.STYLE_DOCUMENT_ENCODED);
            } else {
                info.setStyle(SoapBindingInfo.STYLE_DOCUMENT_LITERAL);
            }
        }
        info.setOperationName(oper.getBinding().getPort().getService().getName());
        oper.setSoapBindingInfo(info);
    }
}
