/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.wsdl.Attribute;
import com.metamatrix.metamodels.wsdl.AttributeOwner;
import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.BindingInput;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.BindingOutput;
import com.metamatrix.metamodels.wsdl.BindingParam;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
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
import com.metamatrix.metamodels.wsdl.ParamType;
import com.metamatrix.metamodels.wsdl.Port;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.Service;
import com.metamatrix.metamodels.wsdl.Types;
import com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity;
import com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage
 * @generated
 */
public class WsdlAdapterFactory extends AdapterFactoryImpl {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The cached model package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected static WsdlPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public WsdlAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = WsdlPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
	@Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected WsdlSwitch modelSwitch =
        new WsdlSwitch() {
            @Override
            public Object caseDefinitions(Definitions object) {
                return createDefinitionsAdapter();
            }
            @Override
            public Object caseDocumentation(Documentation object) {
                return createDocumentationAdapter();
            }
            @Override
            public Object caseDocumented(Documented object) {
                return createDocumentedAdapter();
            }
            @Override
            public Object caseExtensibleAttributesDocumented(ExtensibleAttributesDocumented object) {
                return createExtensibleAttributesDocumentedAdapter();
            }
            @Override
            public Object caseAttribute(Attribute object) {
                return createAttributeAdapter();
            }
            @Override
            public Object caseMessage(Message object) {
                return createMessageAdapter();
            }
            @Override
            public Object casePortType(PortType object) {
                return createPortTypeAdapter();
            }
            @Override
            public Object caseBinding(Binding object) {
                return createBindingAdapter();
            }
            @Override
            public Object caseService(Service object) {
                return createServiceAdapter();
            }
            @Override
            public Object caseImport(Import object) {
                return createImportAdapter();
            }
            @Override
            public Object casePort(Port object) {
                return createPortAdapter();
            }
            @Override
            public Object caseElement(Element object) {
                return createElementAdapter();
            }
            @Override
            public Object caseAttributeOwner(AttributeOwner object) {
                return createAttributeOwnerAdapter();
            }
            @Override
            public Object caseElementOwner(ElementOwner object) {
                return createElementOwnerAdapter();
            }
            @Override
            public Object caseWsdlNameRequiredEntity(WsdlNameRequiredEntity object) {
                return createWsdlNameRequiredEntityAdapter();
            }
            @Override
            public Object caseTypes(Types object) {
                return createTypesAdapter();
            }
            @Override
            public Object caseMessagePart(MessagePart object) {
                return createMessagePartAdapter();
            }
            @Override
            public Object caseWsdlNameOptionalEntity(WsdlNameOptionalEntity object) {
                return createWsdlNameOptionalEntityAdapter();
            }
            @Override
            public Object caseOperation(Operation object) {
                return createOperationAdapter();
            }
            @Override
            public Object caseInput(Input object) {
                return createInputAdapter();
            }
            @Override
            public Object caseOutput(Output object) {
                return createOutputAdapter();
            }
            @Override
            public Object caseFault(Fault object) {
                return createFaultAdapter();
            }
            @Override
            public Object caseParamType(ParamType object) {
                return createParamTypeAdapter();
            }
            @Override
            public Object caseExtensibleDocumented(ExtensibleDocumented object) {
                return createExtensibleDocumentedAdapter();
            }
            @Override
            public Object caseBindingOperation(BindingOperation object) {
                return createBindingOperationAdapter();
            }
            @Override
            public Object caseBindingInput(BindingInput object) {
                return createBindingInputAdapter();
            }
            @Override
            public Object caseBindingOutput(BindingOutput object) {
                return createBindingOutputAdapter();
            }
            @Override
            public Object caseBindingFault(BindingFault object) {
                return createBindingFaultAdapter();
            }
            @Override
            public Object caseBindingParam(BindingParam object) {
                return createBindingParamAdapter();
            }
            @Override
            public Object caseNamespaceDeclaration(NamespaceDeclaration object) {
                return createNamespaceDeclarationAdapter();
            }
            @Override
            public Object caseNamespaceDeclarationOwner(NamespaceDeclarationOwner object) {
                return createNamespaceDeclarationOwnerAdapter();
            }
            @Override
            public Object caseMimeElementOwner(MimeElementOwner object) {
                return createMimeElementOwnerAdapter();
            }
            @Override
            public Object defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
	@Override
    public Adapter createAdapter(Notifier target) {
        return (Adapter)modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Definitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Definitions
     * @generated
     */
	public Adapter createDefinitionsAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Documentation <em>Documentation</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Documentation
     * @generated
     */
	public Adapter createDocumentationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Documented <em>Documented</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Documented
     * @generated
     */
	public Adapter createDocumentedAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented <em>Extensible Attributes Documented</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented
     * @generated
     */
	public Adapter createExtensibleAttributesDocumentedAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Attribute <em>Attribute</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Attribute
     * @generated
     */
	public Adapter createAttributeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Message <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Message
     * @generated
     */
	public Adapter createMessageAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.PortType <em>Port Type</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.PortType
     * @generated
     */
	public Adapter createPortTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Binding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Binding
     * @generated
     */
	public Adapter createBindingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Service <em>Service</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Service
     * @generated
     */
	public Adapter createServiceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Import <em>Import</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Import
     * @generated
     */
	public Adapter createImportAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Port <em>Port</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Port
     * @generated
     */
	public Adapter createPortAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Element <em>Element</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Element
     * @generated
     */
	public Adapter createElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.AttributeOwner <em>Attribute Owner</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.AttributeOwner
     * @generated
     */
	public Adapter createAttributeOwnerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.ElementOwner <em>Element Owner</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.ElementOwner
     * @generated
     */
	public Adapter createElementOwnerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity <em>Name Required Entity</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity
     * @generated
     */
	public Adapter createWsdlNameRequiredEntityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Types <em>Types</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Types
     * @generated
     */
	public Adapter createTypesAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.MessagePart <em>Message Part</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.MessagePart
     * @generated
     */
	public Adapter createMessagePartAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity <em>Name Optional Entity</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity
     * @generated
     */
	public Adapter createWsdlNameOptionalEntityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Operation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Operation
     * @generated
     */
	public Adapter createOperationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Input <em>Input</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Input
     * @generated
     */
	public Adapter createInputAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Output <em>Output</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Output
     * @generated
     */
	public Adapter createOutputAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.Fault <em>Fault</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.Fault
     * @generated
     */
	public Adapter createFaultAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.ParamType <em>Param Type</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.ParamType
     * @generated
     */
	public Adapter createParamTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.ExtensibleDocumented <em>Extensible Documented</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.ExtensibleDocumented
     * @generated
     */
	public Adapter createExtensibleDocumentedAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.BindingOperation <em>Binding Operation</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation
     * @generated
     */
	public Adapter createBindingOperationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.BindingInput <em>Binding Input</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.BindingInput
     * @generated
     */
	public Adapter createBindingInputAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.BindingOutput <em>Binding Output</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.BindingOutput
     * @generated
     */
	public Adapter createBindingOutputAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.BindingFault <em>Binding Fault</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.BindingFault
     * @generated
     */
	public Adapter createBindingFaultAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.BindingParam <em>Binding Param</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.BindingParam
     * @generated
     */
	public Adapter createBindingParamAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration <em>Namespace Declaration</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclaration
     * @generated
     */
	public Adapter createNamespaceDeclarationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner <em>Namespace Declaration Owner</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner
     * @generated
     */
	public Adapter createNamespaceDeclarationOwnerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.wsdl.mime.MimeElementOwner <em>Element Owner</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElementOwner
     * @generated
     */
	public Adapter createMimeElementOwnerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
	public Adapter createEObjectAdapter() {
        return null;
    }

} //WsdlAdapterFactory
