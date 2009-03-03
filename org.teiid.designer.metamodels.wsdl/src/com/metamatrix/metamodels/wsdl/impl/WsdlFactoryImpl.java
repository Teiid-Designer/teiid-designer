/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.metamodels.wsdl.Attribute;
import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.BindingInput;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.BindingOutput;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.Fault;
import com.metamatrix.metamodels.wsdl.Import;
import com.metamatrix.metamodels.wsdl.Input;
import com.metamatrix.metamodels.wsdl.Message;
import com.metamatrix.metamodels.wsdl.MessagePart;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.Operation;
import com.metamatrix.metamodels.wsdl.Output;
import com.metamatrix.metamodels.wsdl.Port;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.Service;
import com.metamatrix.metamodels.wsdl.Types;
import com.metamatrix.metamodels.wsdl.WsdlFactory;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class WsdlFactoryImpl extends EFactoryImpl implements WsdlFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public WsdlFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create( EClass eClass ) {
        switch (eClass.getClassifierID()) {
            case WsdlPackage.DEFINITIONS:
                return createDefinitions();
            case WsdlPackage.DOCUMENTATION:
                return createDocumentation();
            case WsdlPackage.ATTRIBUTE:
                return createAttribute();
            case WsdlPackage.MESSAGE:
                return createMessage();
            case WsdlPackage.PORT_TYPE:
                return createPortType();
            case WsdlPackage.BINDING:
                return createBinding();
            case WsdlPackage.SERVICE:
                return createService();
            case WsdlPackage.IMPORT:
                return createImport();
            case WsdlPackage.PORT:
                return createPort();
            case WsdlPackage.ELEMENT:
                return createElement();
            case WsdlPackage.TYPES:
                return createTypes();
            case WsdlPackage.MESSAGE_PART:
                return createMessagePart();
            case WsdlPackage.OPERATION:
                return createOperation();
            case WsdlPackage.INPUT:
                return createInput();
            case WsdlPackage.OUTPUT:
                return createOutput();
            case WsdlPackage.FAULT:
                return createFault();
            case WsdlPackage.BINDING_OPERATION:
                return createBindingOperation();
            case WsdlPackage.BINDING_INPUT:
                return createBindingInput();
            case WsdlPackage.BINDING_OUTPUT:
                return createBindingOutput();
            case WsdlPackage.BINDING_FAULT:
                return createBindingFault();
            case WsdlPackage.NAMESPACE_DECLARATION:
                return createNamespaceDeclaration();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object createFromString( EDataType eDataType,
                                    String initialValue ) {
        switch (eDataType.getClassifierID()) {
            case WsdlPackage.ISTATUS:
                return createIStatusFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String convertToString( EDataType eDataType,
                                   Object instanceValue ) {
        switch (eDataType.getClassifierID()) {
            case WsdlPackage.ISTATUS:
                return convertIStatusToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Definitions createDefinitions() {
        DefinitionsImpl definitions = new DefinitionsImpl();
        return definitions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Documentation createDocumentation() {
        DocumentationImpl documentation = new DocumentationImpl();
        return documentation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Attribute createAttribute() {
        AttributeImpl attribute = new AttributeImpl();
        return attribute;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Message createMessage() {
        MessageImpl message = new MessageImpl();
        return message;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public PortType createPortType() {
        PortTypeImpl portType = new PortTypeImpl();
        return portType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Binding createBinding() {
        BindingImpl binding = new BindingImpl();
        return binding;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Service createService() {
        ServiceImpl service = new ServiceImpl();
        return service;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Import createImport() {
        ImportImpl import_ = new ImportImpl();
        return import_;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Port createPort() {
        PortImpl port = new PortImpl();
        return port;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Element createElement() {
        ElementImpl element = new ElementImpl();
        return element;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Types createTypes() {
        TypesImpl types = new TypesImpl();
        return types;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MessagePart createMessagePart() {
        MessagePartImpl messagePart = new MessagePartImpl();
        return messagePart;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Operation createOperation() {
        OperationImpl operation = new OperationImpl();
        return operation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Input createInput() {
        InputImpl input = new InputImpl();
        return input;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Output createOutput() {
        OutputImpl output = new OutputImpl();
        return output;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Fault createFault() {
        FaultImpl fault = new FaultImpl();
        return fault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public BindingOperation createBindingOperation() {
        BindingOperationImpl bindingOperation = new BindingOperationImpl();
        return bindingOperation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public BindingInput createBindingInput() {
        BindingInputImpl bindingInput = new BindingInputImpl();
        return bindingInput;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public BindingOutput createBindingOutput() {
        BindingOutputImpl bindingOutput = new BindingOutputImpl();
        return bindingOutput;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public BindingFault createBindingFault() {
        BindingFaultImpl bindingFault = new BindingFaultImpl();
        return bindingFault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NamespaceDeclaration createNamespaceDeclaration() {
        NamespaceDeclarationImpl namespaceDeclaration = new NamespaceDeclarationImpl();
        return namespaceDeclaration;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public IStatus createIStatusFromString( EDataType eDataType,
                                            String initialValue ) {
        return (IStatus)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertIStatusToString( EDataType eDataType,
                                          Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public WsdlPackage getWsdlPackage() {
        return (WsdlPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static WsdlPackage getPackage() {
        return WsdlPackage.eINSTANCE;
    }

} // WsdlFactoryImpl
