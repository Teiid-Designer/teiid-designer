/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.io;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.xmi.XMLResource.XMLInfo;
import org.eclipse.emf.ecore.xmi.impl.XMLInfoImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;

/**
 * @since 4.2
 */
public class WsdlXmlMap extends XMLMapImpl implements WsdlConstants {
    
    /** 
     * 
     * @since 4.2
     */
    public WsdlXmlMap() {
        super();
        init();
    }
    
    
    /**
     * Initialize this map with all of the XMLInfo instances ... 
     * 
     * @since 4.2
     */
    protected void init() {
        final WsdlPackage wsdlPkg = WsdlPackage.eINSTANCE;
        final SoapPackage soapPkg = SoapPackage.eINSTANCE;
        final HttpPackage httpPkg = HttpPackage.eINSTANCE;
        //final MimePackage mimePkg = MimePackage.eINSTANCE;
        
        final String wsdl = wsdlPkg.getNsURI();
        final String soap = soapPkg.getNsURI();
        final String http = httpPkg.getNsURI();
        //final String mime = mimePkg.getNsURI();

        // wsdl:*.name
        addAttributeInfo(wsdlPkg.getWsdlNameRequiredEntity_Name(),DEFINITIONS_NAME,wsdl);
        addAttributeInfo(wsdlPkg.getWsdlNameOptionalEntity_Name(),DEFINITIONS_NAME,wsdl);
        
        // wsdl:definitions
        addElementInfo(wsdlPkg.getDefinitions(),DEFINITIONS,wsdl);
        addAttributeInfo(wsdlPkg.getDefinitions_TargetNamespace(),DEFINITIONS_TARGETNAMESPACE,wsdl);
        addElementInfo(wsdlPkg.getDefinitions_Bindings(),BINDING,wsdl);
        addElementInfo(wsdlPkg.getDefinitions_Imports(),IMPORT,wsdl);
        addElementInfo(wsdlPkg.getDefinitions_Messages(),MESSAGE,wsdl);
        addElementInfo(wsdlPkg.getDefinitions_PortTypes(),PORTTYPE,wsdl);
        addElementInfo(wsdlPkg.getDefinitions_Services(),SERVICE,wsdl);
        addElementInfo(wsdlPkg.getDefinitions_Types(),TYPES,wsdl);

        // wsdl:documentation
        addElementInfo(wsdlPkg.getDocumentation(),DOCUMENTATION,wsdl);
        addContentInfo(wsdlPkg.getDocumentation_TextContent(),wsdl);

        // wsdl:types
        addElementInfo(wsdlPkg.getTypes(),TYPES,wsdl);

        // wsdl:import
        addElementInfo(wsdlPkg.getImport(),IMPORT,wsdl);
        addAttributeInfo(wsdlPkg.getImport_Location(),IMPORT_LOCATION,wsdl);
        addAttributeInfo(wsdlPkg.getImport_Namespace(),IMPORT_NAMESPACE,wsdl);

        // wsdl:message
        addElementInfo(wsdlPkg.getMessage(),MESSAGE,wsdl);
        addAttributeInfo(wsdlPkg.getMessage(),MESSAGE,wsdl);
        addElementInfo(wsdlPkg.getMessage_Parts(),PART,wsdl);

        // wsdl:messagePart
        addElementInfo(wsdlPkg.getMessagePart(),PART,wsdl);
        addAttributeInfo(wsdlPkg.getMessagePart_Type(),PART_TYPE,wsdl);
        addAttributeInfo(wsdlPkg.getMessagePart_Element(),PART_ELEMENT,wsdl);

        // wsdl:portType
        addElementInfo(wsdlPkg.getPortType(),PORTTYPE,wsdl);
        addElementInfo(wsdlPkg.getPortType_Operations(),OPERATION,wsdl);

        // wsdl:operation
        addElementInfo(wsdlPkg.getOperation(),OPERATION,wsdl);
        addElementInfo(wsdlPkg.getOperation_Faults(),FAULT,wsdl);
        addElementInfo(wsdlPkg.getOperation_Input(),INPUT,wsdl);
        addElementInfo(wsdlPkg.getOperation_Output(),OUTPUT,wsdl);

        // wsdl:input
        addElementInfo(wsdlPkg.getInput(),INPUT,wsdl);

        // wsdl:output
        addElementInfo(wsdlPkg.getOutput(),OUTPUT,wsdl);

        // wsdl:fault
        addElementInfo(wsdlPkg.getFault(),FAULT,wsdl);
        addAttributeInfo(wsdlPkg.getFault_Message(),FAULT_MESSAGE,wsdl);

        // wsdl:binding
        addElementInfo(wsdlPkg.getBinding(),BINDING,wsdl);
        addAttributeInfo(wsdlPkg.getBinding_Type(),BINDING_TYPE,wsdl);
        addElementInfo(wsdlPkg.getBinding_BindingOperations(),BINDINGOPERATION,wsdl);
        addElementInfo(wsdlPkg.getBinding_HttpBinding(),Http.BINDING,http);
        addElementInfo(wsdlPkg.getBinding_SoapBinding(),Soap.BINDING,soap);

        // wsdl:bindingOperation
        addElementInfo(wsdlPkg.getBindingOperation(),BINDINGOPERATION,wsdl);
        addElementInfo(wsdlPkg.getBindingOperation_BindingFaults(),BINDINGFAULT,wsdl);
        addElementInfo(wsdlPkg.getBindingOperation_BindingInput(),BINDINGINPUT,wsdl);
        addElementInfo(wsdlPkg.getBindingOperation_BindingOutput(),BINDINGOUTPUT,wsdl);
        addElementInfo(wsdlPkg.getBindingOperation_HttpOperation(),Http.OPERATION,http);
        addElementInfo(wsdlPkg.getBindingOperation_SoapOperation(),Soap.OPERATION,soap);

        
        // wsdl:service
        addElementInfo(wsdlPkg.getService(),SERVICE,wsdl);
        
        // soap:binding
        addElementInfo(soapPkg.getSoapBinding_Binding(),Soap.BINDING,soap);
        addAttributeInfo(soapPkg.getSoapBinding_Style(),Soap.BINDING_STYLE,soap);
        addAttributeInfo(soapPkg.getSoapBinding_Transport(),Soap.BINDING_TRANSPORT,soap);
        
        // http:operation
        addElementInfo(httpPkg.getHttpOperation(),Http.BINDING,http);
        addAttributeInfo(httpPkg.getHttpOperation_Location(),Http.OPERATION_LOCATION,http);
    }
    
    protected XMLInfo addElementInfo( final ENamedElement namedElement, final String name, final String targetNs ) {
        final XMLInfo info = new XMLInfoImpl();
        info.setName(name);
        info.setTargetNamespace(targetNs);
        info.setXMLRepresentation(XMLInfo.ELEMENT);
        super.add(namedElement,info);
        return info;
    }
    
    protected XMLInfo addAttributeInfo( final ENamedElement namedElement, final String name, final String targetNs ) {
        final XMLInfo info = new XMLInfoImpl();
        info.setName(name);
        info.setTargetNamespace(targetNs);
        info.setXMLRepresentation(XMLInfo.ATTRIBUTE);
        super.add(namedElement,info);
        return info;
    }
    
    protected XMLInfo addContentInfo( final ENamedElement namedElement, final String targetNs ) {
        final XMLInfo info = new XMLInfoImpl();
        info.setTargetNamespace(targetNs);
        info.setXMLRepresentation(XMLInfo.CONTENT);
        super.add(namedElement,info);
        return info;
    }
    
}
