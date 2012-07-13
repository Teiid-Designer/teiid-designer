/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.teiid.designer.metamodels.wsdl.Attribute;
import org.teiid.designer.metamodels.wsdl.Binding;
import org.teiid.designer.metamodels.wsdl.BindingFault;
import org.teiid.designer.metamodels.wsdl.BindingInput;
import org.teiid.designer.metamodels.wsdl.BindingOperation;
import org.teiid.designer.metamodels.wsdl.BindingOutput;
import org.teiid.designer.metamodels.wsdl.Definitions;
import org.teiid.designer.metamodels.wsdl.Documentation;
import org.teiid.designer.metamodels.wsdl.Element;
import org.teiid.designer.metamodels.wsdl.Fault;
import org.teiid.designer.metamodels.wsdl.Import;
import org.teiid.designer.metamodels.wsdl.Input;
import org.teiid.designer.metamodels.wsdl.Message;
import org.teiid.designer.metamodels.wsdl.MessagePart;
import org.teiid.designer.metamodels.wsdl.NamespaceDeclaration;
import org.teiid.designer.metamodels.wsdl.Operation;
import org.teiid.designer.metamodels.wsdl.Output;
import org.teiid.designer.metamodels.wsdl.Port;
import org.teiid.designer.metamodels.wsdl.PortType;
import org.teiid.designer.metamodels.wsdl.Service;
import org.teiid.designer.metamodels.wsdl.Types;
import org.teiid.designer.metamodels.wsdl.WsdlFactory;
import org.teiid.designer.metamodels.wsdl.WsdlPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class WsdlFactoryImpl extends EFactoryImpl implements WsdlFactory {

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
    @Override
	public Definitions createDefinitions() {
        DefinitionsImpl definitions = new DefinitionsImpl();
        return definitions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Documentation createDocumentation() {
        DocumentationImpl documentation = new DocumentationImpl();
        return documentation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Attribute createAttribute() {
        AttributeImpl attribute = new AttributeImpl();
        return attribute;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Message createMessage() {
        MessageImpl message = new MessageImpl();
        return message;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public PortType createPortType() {
        PortTypeImpl portType = new PortTypeImpl();
        return portType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Binding createBinding() {
        BindingImpl binding = new BindingImpl();
        return binding;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Service createService() {
        ServiceImpl service = new ServiceImpl();
        return service;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Import createImport() {
        ImportImpl import_ = new ImportImpl();
        return import_;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Port createPort() {
        PortImpl port = new PortImpl();
        return port;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Element createElement() {
        ElementImpl element = new ElementImpl();
        return element;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Types createTypes() {
        TypesImpl types = new TypesImpl();
        return types;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public MessagePart createMessagePart() {
        MessagePartImpl messagePart = new MessagePartImpl();
        return messagePart;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Operation createOperation() {
        OperationImpl operation = new OperationImpl();
        return operation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Input createInput() {
        InputImpl input = new InputImpl();
        return input;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Output createOutput() {
        OutputImpl output = new OutputImpl();
        return output;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Fault createFault() {
        FaultImpl fault = new FaultImpl();
        return fault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public BindingOperation createBindingOperation() {
        BindingOperationImpl bindingOperation = new BindingOperationImpl();
        return bindingOperation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public BindingInput createBindingInput() {
        BindingInputImpl bindingInput = new BindingInputImpl();
        return bindingInput;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public BindingOutput createBindingOutput() {
        BindingOutputImpl bindingOutput = new BindingOutputImpl();
        return bindingOutput;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public BindingFault createBindingFault() {
        BindingFaultImpl bindingFault = new BindingFaultImpl();
        return bindingFault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
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
    @Override
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
    public static WsdlPackage getPackage() { // NO_UCD
        return WsdlPackage.eINSTANCE;
    }

} // WsdlFactoryImpl
