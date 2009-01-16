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

package com.metamatrix.metamodels.wsdl.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.impl.XSDPackageImpl;

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
import com.metamatrix.metamodels.wsdl.WsdlFactory;
import com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity;
import com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.http.impl.HttpPackageImpl;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WsdlPackageImpl extends EPackageImpl implements WsdlPackage {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass definitionsEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass documentationEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass documentedEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass extensibleAttributesDocumentedEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass attributeEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass messageEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass portTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass bindingEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass serviceEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass importEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass portEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass elementEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass attributeOwnerEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass elementOwnerEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass wsdlNameRequiredEntityEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass typesEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass messagePartEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass wsdlNameOptionalEntityEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass operationEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass inputEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass outputEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass faultEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass paramTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass extensibleDocumentedEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass bindingOperationEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass bindingInputEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass bindingOutputEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass bindingFaultEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass bindingParamEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass namespaceDeclarationEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass namespaceDeclarationOwnerEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EDataType iStatusEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#eNS_URI
     * @see #init()
     * @generated
     */
	private WsdlPackageImpl() {
        super(eNS_URI, WsdlFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
	public static WsdlPackage init() {
        if (isInited) return (WsdlPackage)EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI);

        // Obtain or create and register package
        WsdlPackageImpl theWsdlPackage = (WsdlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof WsdlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new WsdlPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();
        XSDPackageImpl.init();

        // Obtain or create and register interdependencies
        SoapPackageImpl theSoapPackage = (SoapPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI) instanceof SoapPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI) : SoapPackage.eINSTANCE);
        HttpPackageImpl theHttpPackage = (HttpPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI) instanceof HttpPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI) : HttpPackage.eINSTANCE);
        MimePackageImpl theMimePackage = (MimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI) instanceof MimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI) : MimePackage.eINSTANCE);

        // Create package meta-data objects
        theWsdlPackage.createPackageContents();
        theSoapPackage.createPackageContents();
        theHttpPackage.createPackageContents();
        theMimePackage.createPackageContents();

        // Initialize created meta-data
        theWsdlPackage.initializePackageContents();
        theSoapPackage.initializePackageContents();
        theHttpPackage.initializePackageContents();
        theMimePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theWsdlPackage.freeze();

        return theWsdlPackage;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getDefinitions() {
        return definitionsEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getDefinitions_TargetNamespace() {
        return (EAttribute)definitionsEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDefinitions_Messages() {
        return (EReference)definitionsEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDefinitions_PortTypes() {
        return (EReference)definitionsEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDefinitions_Bindings() {
        return (EReference)definitionsEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDefinitions_Services() {
        return (EReference)definitionsEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDefinitions_Imports() {
        return (EReference)definitionsEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDefinitions_Types() {
        return (EReference)definitionsEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getDocumentation() {
        return documentationEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getDocumentation_TextContent() {
        return (EAttribute)documentationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDocumentation_Contents() {
        return (EReference)documentationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDocumentation_Documented() {
        return (EReference)documentationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getDocumented() {
        return documentedEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getDocumented_Documentation() {
        return (EReference)documentedEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getExtensibleAttributesDocumented() {
        return extensibleAttributesDocumentedEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getAttribute() {
        return attributeEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getAttribute_Name() {
        return (EAttribute)attributeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getAttribute_Prefix() {
        return (EAttribute)attributeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getAttribute_Value() {
        return (EAttribute)attributeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getAttribute_NamespaceUri() {
        return (EAttribute)attributeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getAttribute_AttributeOwner() {
        return (EReference)attributeEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getMessage() {
        return messageEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMessage_Definitions() {
        return (EReference)messageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMessage_Parts() {
        return (EReference)messageEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getPortType() {
        return portTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getPortType_Definitions() {
        return (EReference)portTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getPortType_Operations() {
        return (EReference)portTypeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getBinding() {
        return bindingEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getBinding_Type() {
        return (EAttribute)bindingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBinding_Definitions() {
        return (EReference)bindingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBinding_BindingOperations() {
        return (EReference)bindingEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBinding_SoapBinding() {
        return (EReference)bindingEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBinding_HttpBinding() {
        return (EReference)bindingEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getService() {
        return serviceEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getService_Definitions() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getService_Ports() {
        return (EReference)serviceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getImport() {
        return importEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getImport_Namespace() {
        return (EAttribute)importEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getImport_Location() {
        return (EAttribute)importEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getImport_Definitions() {
        return (EReference)importEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getPort() {
        return portEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getPort_Binding() {
        return (EAttribute)portEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getPort_Service() {
        return (EReference)portEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getPort_SoapAddress() {
        return (EReference)portEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getPort_HttpAddress() {
        return (EReference)portEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getElement() {
        return elementEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getElement_Name() {
        return (EAttribute)elementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getElement_Prefix() {
        return (EAttribute)elementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getElement_TextContent() {
        return (EAttribute)elementEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getElement_NamespaceUri() {
        return (EAttribute)elementEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getElement_ElementOwner() {
        return (EReference)elementEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getAttributeOwner() {
        return attributeOwnerEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getAttributeOwner_Attributes() {
        return (EReference)attributeOwnerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getElementOwner() {
        return elementOwnerEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getElementOwner_Elements() {
        return (EReference)elementOwnerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getWsdlNameRequiredEntity() {
        return wsdlNameRequiredEntityEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getWsdlNameRequiredEntity_Name() {
        return (EAttribute)wsdlNameRequiredEntityEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getTypes() {
        return typesEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getTypes_Definitions() {
        return (EReference)typesEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getTypes_Schemas() {
        return (EReference)typesEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getMessagePart() {
        return messagePartEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getMessagePart_Type() {
        return (EAttribute)messagePartEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getMessagePart_Element() {
        return (EAttribute)messagePartEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMessagePart_Message() {
        return (EReference)messagePartEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getWsdlNameOptionalEntity() {
        return wsdlNameOptionalEntityEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getWsdlNameOptionalEntity_Name() {
        return (EAttribute)wsdlNameOptionalEntityEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getOperation() {
        return operationEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getOperation_ParameterOrder() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getOperation_PortType() {
        return (EReference)operationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getOperation_Input() {
        return (EReference)operationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getOperation_Output() {
        return (EReference)operationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getOperation_Faults() {
        return (EReference)operationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getInput() {
        return inputEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getInput_Operation() {
        return (EReference)inputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getOutput() {
        return outputEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getOutput_Operation() {
        return (EReference)outputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getFault() {
        return faultEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getFault_Message() {
        return (EAttribute)faultEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getFault_Operation() {
        return (EReference)faultEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getParamType() {
        return paramTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getParamType_Message() {
        return (EAttribute)paramTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getExtensibleDocumented() {
        return extensibleDocumentedEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getBindingOperation() {
        return bindingOperationEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingOperation_Binding() {
        return (EReference)bindingOperationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingOperation_BindingInput() {
        return (EReference)bindingOperationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingOperation_BindingFaults() {
        return (EReference)bindingOperationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingOperation_BindingOutput() {
        return (EReference)bindingOperationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingOperation_SoapOperation() {
        return (EReference)bindingOperationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingOperation_HttpOperation() {
        return (EReference)bindingOperationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getBindingInput() {
        return bindingInputEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingInput_BindingOperation() {
        return (EReference)bindingInputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getBindingOutput() {
        return bindingOutputEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingOutput_BindingOperation() {
        return (EReference)bindingOutputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getBindingFault() {
        return bindingFaultEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingFault_BindingOperation() {
        return (EReference)bindingFaultEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingFault_SoapFault() {
        return (EReference)bindingFaultEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getBindingParam() {
        return bindingParamEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingParam_SoapHeader() {
        return (EReference)bindingParamEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getBindingParam_SoapBody() {
        return (EReference)bindingParamEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getNamespaceDeclaration() {
        return namespaceDeclarationEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getNamespaceDeclaration_Uri() {
        return (EAttribute)namespaceDeclarationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getNamespaceDeclaration_Prefix() {
        return (EAttribute)namespaceDeclarationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getNamespaceDeclaration_Owner() {
        return (EReference)namespaceDeclarationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getNamespaceDeclarationOwner() {
        return namespaceDeclarationOwnerEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getNamespaceDeclarationOwner_DeclaredNamespaces() {
        return (EReference)namespaceDeclarationOwnerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EDataType getIStatus() {
        return iStatusEDataType;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public WsdlFactory getWsdlFactory() {
        return (WsdlFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        definitionsEClass = createEClass(DEFINITIONS);
        createEAttribute(definitionsEClass, DEFINITIONS__TARGET_NAMESPACE);
        createEReference(definitionsEClass, DEFINITIONS__MESSAGES);
        createEReference(definitionsEClass, DEFINITIONS__PORT_TYPES);
        createEReference(definitionsEClass, DEFINITIONS__BINDINGS);
        createEReference(definitionsEClass, DEFINITIONS__SERVICES);
        createEReference(definitionsEClass, DEFINITIONS__IMPORTS);
        createEReference(definitionsEClass, DEFINITIONS__TYPES);

        documentationEClass = createEClass(DOCUMENTATION);
        createEAttribute(documentationEClass, DOCUMENTATION__TEXT_CONTENT);
        createEReference(documentationEClass, DOCUMENTATION__CONTENTS);
        createEReference(documentationEClass, DOCUMENTATION__DOCUMENTED);

        documentedEClass = createEClass(DOCUMENTED);
        createEReference(documentedEClass, DOCUMENTED__DOCUMENTATION);

        extensibleAttributesDocumentedEClass = createEClass(EXTENSIBLE_ATTRIBUTES_DOCUMENTED);

        attributeEClass = createEClass(ATTRIBUTE);
        createEAttribute(attributeEClass, ATTRIBUTE__NAME);
        createEAttribute(attributeEClass, ATTRIBUTE__PREFIX);
        createEAttribute(attributeEClass, ATTRIBUTE__VALUE);
        createEAttribute(attributeEClass, ATTRIBUTE__NAMESPACE_URI);
        createEReference(attributeEClass, ATTRIBUTE__ATTRIBUTE_OWNER);

        messageEClass = createEClass(MESSAGE);
        createEReference(messageEClass, MESSAGE__DEFINITIONS);
        createEReference(messageEClass, MESSAGE__PARTS);

        portTypeEClass = createEClass(PORT_TYPE);
        createEReference(portTypeEClass, PORT_TYPE__DEFINITIONS);
        createEReference(portTypeEClass, PORT_TYPE__OPERATIONS);

        bindingEClass = createEClass(BINDING);
        createEAttribute(bindingEClass, BINDING__TYPE);
        createEReference(bindingEClass, BINDING__DEFINITIONS);
        createEReference(bindingEClass, BINDING__BINDING_OPERATIONS);
        createEReference(bindingEClass, BINDING__SOAP_BINDING);
        createEReference(bindingEClass, BINDING__HTTP_BINDING);

        serviceEClass = createEClass(SERVICE);
        createEReference(serviceEClass, SERVICE__DEFINITIONS);
        createEReference(serviceEClass, SERVICE__PORTS);

        importEClass = createEClass(IMPORT);
        createEAttribute(importEClass, IMPORT__NAMESPACE);
        createEAttribute(importEClass, IMPORT__LOCATION);
        createEReference(importEClass, IMPORT__DEFINITIONS);

        portEClass = createEClass(PORT);
        createEAttribute(portEClass, PORT__BINDING);
        createEReference(portEClass, PORT__SERVICE);
        createEReference(portEClass, PORT__SOAP_ADDRESS);
        createEReference(portEClass, PORT__HTTP_ADDRESS);

        elementEClass = createEClass(ELEMENT);
        createEAttribute(elementEClass, ELEMENT__NAME);
        createEAttribute(elementEClass, ELEMENT__PREFIX);
        createEAttribute(elementEClass, ELEMENT__TEXT_CONTENT);
        createEAttribute(elementEClass, ELEMENT__NAMESPACE_URI);
        createEReference(elementEClass, ELEMENT__ELEMENT_OWNER);

        attributeOwnerEClass = createEClass(ATTRIBUTE_OWNER);
        createEReference(attributeOwnerEClass, ATTRIBUTE_OWNER__ATTRIBUTES);

        elementOwnerEClass = createEClass(ELEMENT_OWNER);
        createEReference(elementOwnerEClass, ELEMENT_OWNER__ELEMENTS);

        wsdlNameRequiredEntityEClass = createEClass(WSDL_NAME_REQUIRED_ENTITY);
        createEAttribute(wsdlNameRequiredEntityEClass, WSDL_NAME_REQUIRED_ENTITY__NAME);

        typesEClass = createEClass(TYPES);
        createEReference(typesEClass, TYPES__DEFINITIONS);
        createEReference(typesEClass, TYPES__SCHEMAS);

        messagePartEClass = createEClass(MESSAGE_PART);
        createEAttribute(messagePartEClass, MESSAGE_PART__TYPE);
        createEAttribute(messagePartEClass, MESSAGE_PART__ELEMENT);
        createEReference(messagePartEClass, MESSAGE_PART__MESSAGE);

        wsdlNameOptionalEntityEClass = createEClass(WSDL_NAME_OPTIONAL_ENTITY);
        createEAttribute(wsdlNameOptionalEntityEClass, WSDL_NAME_OPTIONAL_ENTITY__NAME);

        operationEClass = createEClass(OPERATION);
        createEAttribute(operationEClass, OPERATION__PARAMETER_ORDER);
        createEReference(operationEClass, OPERATION__PORT_TYPE);
        createEReference(operationEClass, OPERATION__INPUT);
        createEReference(operationEClass, OPERATION__OUTPUT);
        createEReference(operationEClass, OPERATION__FAULTS);

        inputEClass = createEClass(INPUT);
        createEReference(inputEClass, INPUT__OPERATION);

        outputEClass = createEClass(OUTPUT);
        createEReference(outputEClass, OUTPUT__OPERATION);

        faultEClass = createEClass(FAULT);
        createEAttribute(faultEClass, FAULT__MESSAGE);
        createEReference(faultEClass, FAULT__OPERATION);

        paramTypeEClass = createEClass(PARAM_TYPE);
        createEAttribute(paramTypeEClass, PARAM_TYPE__MESSAGE);

        extensibleDocumentedEClass = createEClass(EXTENSIBLE_DOCUMENTED);

        bindingOperationEClass = createEClass(BINDING_OPERATION);
        createEReference(bindingOperationEClass, BINDING_OPERATION__BINDING);
        createEReference(bindingOperationEClass, BINDING_OPERATION__BINDING_INPUT);
        createEReference(bindingOperationEClass, BINDING_OPERATION__BINDING_FAULTS);
        createEReference(bindingOperationEClass, BINDING_OPERATION__BINDING_OUTPUT);
        createEReference(bindingOperationEClass, BINDING_OPERATION__SOAP_OPERATION);
        createEReference(bindingOperationEClass, BINDING_OPERATION__HTTP_OPERATION);

        bindingInputEClass = createEClass(BINDING_INPUT);
        createEReference(bindingInputEClass, BINDING_INPUT__BINDING_OPERATION);

        bindingOutputEClass = createEClass(BINDING_OUTPUT);
        createEReference(bindingOutputEClass, BINDING_OUTPUT__BINDING_OPERATION);

        bindingFaultEClass = createEClass(BINDING_FAULT);
        createEReference(bindingFaultEClass, BINDING_FAULT__BINDING_OPERATION);
        createEReference(bindingFaultEClass, BINDING_FAULT__SOAP_FAULT);

        bindingParamEClass = createEClass(BINDING_PARAM);
        createEReference(bindingParamEClass, BINDING_PARAM__SOAP_HEADER);
        createEReference(bindingParamEClass, BINDING_PARAM__SOAP_BODY);

        namespaceDeclarationEClass = createEClass(NAMESPACE_DECLARATION);
        createEAttribute(namespaceDeclarationEClass, NAMESPACE_DECLARATION__URI);
        createEAttribute(namespaceDeclarationEClass, NAMESPACE_DECLARATION__PREFIX);
        createEReference(namespaceDeclarationEClass, NAMESPACE_DECLARATION__OWNER);

        namespaceDeclarationOwnerEClass = createEClass(NAMESPACE_DECLARATION_OWNER);
        createEReference(namespaceDeclarationOwnerEClass, NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES);

        // Create data types
        iStatusEDataType = createEDataType(ISTATUS);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
        SoapPackageImpl theSoapPackage = (SoapPackageImpl)EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI);
        HttpPackageImpl theHttpPackage = (HttpPackageImpl)EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI);
        XSDPackageImpl theXSDPackage = (XSDPackageImpl)EPackage.Registry.INSTANCE.getEPackage(XSDPackage.eNS_URI);
        MimePackageImpl theMimePackage = (MimePackageImpl)EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI);

        // Add supertypes to classes
        definitionsEClass.getESuperTypes().add(this.getWsdlNameOptionalEntity());
        definitionsEClass.getESuperTypes().add(this.getExtensibleDocumented());
        documentationEClass.getESuperTypes().add(this.getElementOwner());
        extensibleAttributesDocumentedEClass.getESuperTypes().add(this.getDocumented());
        extensibleAttributesDocumentedEClass.getESuperTypes().add(this.getAttributeOwner());
        messageEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        messageEClass.getESuperTypes().add(this.getExtensibleDocumented());
        portTypeEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        portTypeEClass.getESuperTypes().add(this.getExtensibleAttributesDocumented());
        bindingEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        bindingEClass.getESuperTypes().add(this.getExtensibleDocumented());
        serviceEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        serviceEClass.getESuperTypes().add(this.getExtensibleDocumented());
        importEClass.getESuperTypes().add(this.getExtensibleAttributesDocumented());
        portEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        portEClass.getESuperTypes().add(this.getExtensibleDocumented());
        elementEClass.getESuperTypes().add(this.getAttributeOwner());
        elementEClass.getESuperTypes().add(this.getElementOwner());
        attributeOwnerEClass.getESuperTypes().add(this.getNamespaceDeclarationOwner());
        elementOwnerEClass.getESuperTypes().add(this.getNamespaceDeclarationOwner());
        elementOwnerEClass.getESuperTypes().add(this.getDocumented());
        typesEClass.getESuperTypes().add(this.getExtensibleDocumented());
        messagePartEClass.getESuperTypes().add(this.getExtensibleAttributesDocumented());
        messagePartEClass.getESuperTypes().add(this.getWsdlNameOptionalEntity());
        operationEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        operationEClass.getESuperTypes().add(this.getExtensibleDocumented());
        inputEClass.getESuperTypes().add(this.getParamType());
        outputEClass.getESuperTypes().add(this.getParamType());
        faultEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        faultEClass.getESuperTypes().add(this.getExtensibleAttributesDocumented());
        paramTypeEClass.getESuperTypes().add(this.getWsdlNameOptionalEntity());
        paramTypeEClass.getESuperTypes().add(this.getExtensibleAttributesDocumented());
        extensibleDocumentedEClass.getESuperTypes().add(this.getDocumented());
        extensibleDocumentedEClass.getESuperTypes().add(this.getElementOwner());
        bindingOperationEClass.getESuperTypes().add(this.getExtensibleDocumented());
        bindingOperationEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        bindingInputEClass.getESuperTypes().add(this.getBindingParam());
        bindingOutputEClass.getESuperTypes().add(this.getBindingParam());
        bindingFaultEClass.getESuperTypes().add(this.getExtensibleDocumented());
        bindingFaultEClass.getESuperTypes().add(this.getWsdlNameRequiredEntity());
        bindingParamEClass.getESuperTypes().add(this.getExtensibleDocumented());
        bindingParamEClass.getESuperTypes().add(theMimePackage.getMimeElementOwner());
        bindingParamEClass.getESuperTypes().add(this.getWsdlNameOptionalEntity());

        // Initialize classes and features; add operations and parameters
        initEClass(definitionsEClass, Definitions.class, "Definitions", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getDefinitions_TargetNamespace(), ecorePackage.getEString(), "targetNamespace", null, 0, 1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDefinitions_Messages(), this.getMessage(), this.getMessage_Definitions(), "messages", null, 0, -1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDefinitions_PortTypes(), this.getPortType(), this.getPortType_Definitions(), "portTypes", null, 0, -1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDefinitions_Bindings(), this.getBinding(), this.getBinding_Definitions(), "bindings", null, 0, -1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDefinitions_Services(), this.getService(), this.getService_Definitions(), "services", null, 0, -1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDefinitions_Imports(), this.getImport(), this.getImport_Definitions(), "imports", null, 0, -1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDefinitions_Types(), this.getTypes(), this.getTypes_Definitions(), "types", null, 0, 1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(definitionsEClass, this.getIStatus(), "isValid"); //$NON-NLS-1$

        initEClass(documentationEClass, Documentation.class, "Documentation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getDocumentation_TextContent(), ecorePackage.getEString(), "textContent", null, 0, 1, Documentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDocumentation_Contents(), theEcorePackage.getEObject(), null, "contents", null, 0, -1, Documentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDocumentation_Documented(), this.getDocumented(), this.getDocumented_Documentation(), "documented", null, 0, 1, Documentation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(documentedEClass, Documented.class, "Documented", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getDocumented_Documentation(), this.getDocumentation(), this.getDocumentation_Documented(), "documentation", null, 0, 1, Documented.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(extensibleAttributesDocumentedEClass, ExtensibleAttributesDocumented.class, "ExtensibleAttributesDocumented", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(attributeEClass, Attribute.class, "Attribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getAttribute_Name(), ecorePackage.getEString(), "name", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getAttribute_Prefix(), ecorePackage.getEString(), "prefix", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getAttribute_Value(), ecorePackage.getEString(), "value", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getAttribute_NamespaceUri(), ecorePackage.getEString(), "namespaceUri", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getAttribute_AttributeOwner(), this.getAttributeOwner(), this.getAttributeOwner_Attributes(), "attributeOwner", null, 0, 1, Attribute.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(messageEClass, Message.class, "Message", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMessage_Definitions(), this.getDefinitions(), this.getDefinitions_Messages(), "definitions", null, 1, 1, Message.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMessage_Parts(), this.getMessagePart(), this.getMessagePart_Message(), "parts", null, 0, -1, Message.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(messageEClass, this.getIStatus(), "isValid"); //$NON-NLS-1$

        initEClass(portTypeEClass, PortType.class, "PortType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getPortType_Definitions(), this.getDefinitions(), this.getDefinitions_PortTypes(), "definitions", null, 1, 1, PortType.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getPortType_Operations(), this.getOperation(), this.getOperation_PortType(), "operations", null, 0, -1, PortType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(bindingEClass, Binding.class, "Binding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getBinding_Type(), ecorePackage.getEString(), "type", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBinding_Definitions(), this.getDefinitions(), this.getDefinitions_Bindings(), "definitions", null, 1, 1, Binding.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBinding_BindingOperations(), this.getBindingOperation(), this.getBindingOperation_Binding(), "bindingOperations", null, 0, -1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBinding_SoapBinding(), theSoapPackage.getSoapBinding(), theSoapPackage.getSoapBinding_Binding(), "soapBinding", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBinding_HttpBinding(), theHttpPackage.getHttpBinding(), theHttpPackage.getHttpBinding_Binding(), "httpBinding", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(serviceEClass, Service.class, "Service", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getService_Definitions(), this.getDefinitions(), this.getDefinitions_Services(), "definitions", null, 1, 1, Service.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getService_Ports(), this.getPort(), this.getPort_Service(), "ports", null, 0, -1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(importEClass, Import.class, "Import", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getImport_Namespace(), ecorePackage.getEString(), "namespace", null, 0, 1, Import.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getImport_Location(), ecorePackage.getEString(), "location", null, 0, 1, Import.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getImport_Definitions(), this.getDefinitions(), this.getDefinitions_Imports(), "definitions", null, 1, 1, Import.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(portEClass, Port.class, "Port", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getPort_Binding(), ecorePackage.getEString(), "binding", null, 0, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getPort_Service(), this.getService(), this.getService_Ports(), "service", null, 1, 1, Port.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getPort_SoapAddress(), theSoapPackage.getSoapAddress(), theSoapPackage.getSoapAddress_Port(), "soapAddress", null, 0, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getPort_HttpAddress(), theHttpPackage.getHttpAddress(), theHttpPackage.getHttpAddress_Port(), "httpAddress", null, 0, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(elementEClass, Element.class, "Element", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, Element.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getElement_Prefix(), ecorePackage.getEString(), "prefix", null, 0, 1, Element.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getElement_TextContent(), ecorePackage.getEString(), "textContent", null, 0, 1, Element.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getElement_NamespaceUri(), ecorePackage.getEString(), "namespaceUri", null, 0, 1, Element.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getElement_ElementOwner(), this.getElementOwner(), this.getElementOwner_Elements(), "elementOwner", null, 0, 1, Element.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(attributeOwnerEClass, AttributeOwner.class, "AttributeOwner", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getAttributeOwner_Attributes(), this.getAttribute(), this.getAttribute_AttributeOwner(), "attributes", null, 0, -1, AttributeOwner.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(elementOwnerEClass, ElementOwner.class, "ElementOwner", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getElementOwner_Elements(), this.getElement(), this.getElement_ElementOwner(), "elements", null, 0, -1, ElementOwner.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(wsdlNameRequiredEntityEClass, WsdlNameRequiredEntity.class, "WsdlNameRequiredEntity", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getWsdlNameRequiredEntity_Name(), ecorePackage.getEString(), "name", null, 0, 1, WsdlNameRequiredEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(wsdlNameRequiredEntityEClass, ecorePackage.getEBoolean(), "isNameValid"); //$NON-NLS-1$

        initEClass(typesEClass, Types.class, "Types", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getTypes_Definitions(), this.getDefinitions(), this.getDefinitions_Types(), "definitions", null, 1, 1, Types.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getTypes_Schemas(), theXSDPackage.getXSDSchema(), null, "schemas", null, 0, -1, Types.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(messagePartEClass, MessagePart.class, "MessagePart", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getMessagePart_Type(), ecorePackage.getEString(), "type", null, 0, 1, MessagePart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getMessagePart_Element(), ecorePackage.getEString(), "element", null, 0, 1, MessagePart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMessagePart_Message(), this.getMessage(), this.getMessage_Parts(), "message", null, 1, 1, MessagePart.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(wsdlNameOptionalEntityEClass, WsdlNameOptionalEntity.class, "WsdlNameOptionalEntity", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getWsdlNameOptionalEntity_Name(), ecorePackage.getEString(), "name", null, 0, 1, WsdlNameOptionalEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(wsdlNameOptionalEntityEClass, ecorePackage.getEBoolean(), "isNameValid"); //$NON-NLS-1$

        initEClass(operationEClass, Operation.class, "Operation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getOperation_ParameterOrder(), ecorePackage.getEString(), "parameterOrder", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getOperation_PortType(), this.getPortType(), this.getPortType_Operations(), "portType", null, 1, 1, Operation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getOperation_Input(), this.getInput(), this.getInput_Operation(), "input", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getOperation_Output(), this.getOutput(), this.getOutput_Operation(), "output", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getOperation_Faults(), this.getFault(), this.getFault_Operation(), "faults", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(inputEClass, Input.class, "Input", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getInput_Operation(), this.getOperation(), this.getOperation_Input(), "operation", null, 1, 1, Input.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(outputEClass, Output.class, "Output", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getOutput_Operation(), this.getOperation(), this.getOperation_Output(), "operation", null, 1, 1, Output.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(faultEClass, Fault.class, "Fault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getFault_Message(), ecorePackage.getEString(), "message", null, 0, 1, Fault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getFault_Operation(), this.getOperation(), this.getOperation_Faults(), "operation", null, 1, 1, Fault.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(paramTypeEClass, ParamType.class, "ParamType", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getParamType_Message(), ecorePackage.getEString(), "message", null, 0, 1, ParamType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(extensibleDocumentedEClass, ExtensibleDocumented.class, "ExtensibleDocumented", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(bindingOperationEClass, BindingOperation.class, "BindingOperation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getBindingOperation_Binding(), this.getBinding(), this.getBinding_BindingOperations(), "binding", null, 1, 1, BindingOperation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBindingOperation_BindingInput(), this.getBindingInput(), this.getBindingInput_BindingOperation(), "bindingInput", null, 0, 1, BindingOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBindingOperation_BindingFaults(), this.getBindingFault(), this.getBindingFault_BindingOperation(), "bindingFaults", null, 0, -1, BindingOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBindingOperation_BindingOutput(), this.getBindingOutput(), this.getBindingOutput_BindingOperation(), "bindingOutput", null, 0, 1, BindingOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBindingOperation_SoapOperation(), theSoapPackage.getSoapOperation(), theSoapPackage.getSoapOperation_BindingOperation(), "soapOperation", null, 0, 1, BindingOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBindingOperation_HttpOperation(), theHttpPackage.getHttpOperation(), theHttpPackage.getHttpOperation_BindingOperation(), "httpOperation", null, 0, 1, BindingOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(bindingInputEClass, BindingInput.class, "BindingInput", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getBindingInput_BindingOperation(), this.getBindingOperation(), this.getBindingOperation_BindingInput(), "bindingOperation", null, 1, 1, BindingInput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(bindingOutputEClass, BindingOutput.class, "BindingOutput", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getBindingOutput_BindingOperation(), this.getBindingOperation(), this.getBindingOperation_BindingOutput(), "bindingOperation", null, 1, 1, BindingOutput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(bindingFaultEClass, BindingFault.class, "BindingFault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getBindingFault_BindingOperation(), this.getBindingOperation(), this.getBindingOperation_BindingFaults(), "bindingOperation", null, 1, 1, BindingFault.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBindingFault_SoapFault(), theSoapPackage.getSoapFault(), theSoapPackage.getSoapFault_BindingFault(), "soapFault", null, 1, 1, BindingFault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(bindingParamEClass, BindingParam.class, "BindingParam", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getBindingParam_SoapHeader(), theSoapPackage.getSoapHeader(), theSoapPackage.getSoapHeader_BindingParam(), "soapHeader", null, 0, 1, BindingParam.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBindingParam_SoapBody(), theSoapPackage.getSoapBody(), theSoapPackage.getSoapBody_BindingParam(), "soapBody", null, 0, 1, BindingParam.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(namespaceDeclarationEClass, NamespaceDeclaration.class, "NamespaceDeclaration", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getNamespaceDeclaration_Uri(), ecorePackage.getEString(), "uri", null, 0, 1, NamespaceDeclaration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getNamespaceDeclaration_Prefix(), ecorePackage.getEString(), "prefix", null, 0, 1, NamespaceDeclaration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getNamespaceDeclaration_Owner(), this.getNamespaceDeclarationOwner(), this.getNamespaceDeclarationOwner_DeclaredNamespaces(), "owner", null, 1, 1, NamespaceDeclaration.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(namespaceDeclarationOwnerEClass, NamespaceDeclarationOwner.class, "NamespaceDeclarationOwner", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getNamespaceDeclarationOwner_DeclaredNamespaces(), this.getNamespaceDeclaration(), this.getNamespaceDeclaration_Owner(), "declaredNamespaces", null, 0, -1, NamespaceDeclarationOwner.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize data types
        initEDataType(iStatusEDataType, IStatus.class, "IStatus", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //WsdlPackageImpl
