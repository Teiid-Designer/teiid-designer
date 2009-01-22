/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.metamodels.xmlservice.OperationUpdateCount;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlMessage;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlOutput;
import com.metamatrix.metamodels.xmlservice.XmlResult;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.metamodels.xmlservice.XmlServiceFactory;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class XmlServiceFactoryImpl extends EFactoryImpl implements XmlServiceFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlServiceFactoryImpl() {
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
            case XmlServicePackage.XML_OPERATION:
                return createXmlOperation();
            case XmlServicePackage.XML_INPUT:
                return createXmlInput();
            case XmlServicePackage.XML_SERVICE_COMPONENT:
                return createXmlServiceComponent();
            case XmlServicePackage.XML_OUTPUT:
                return createXmlOutput();
            case XmlServicePackage.XML_MESSAGE:
                return createXmlMessage();
            case XmlServicePackage.XML_RESULT:
                return createXmlResult();
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
            case XmlServicePackage.OPERATION_UPDATE_COUNT: {
                OperationUpdateCount result = OperationUpdateCount.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
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
            case XmlServicePackage.OPERATION_UPDATE_COUNT:
                return instanceValue == null ? null : instanceValue.toString();
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlOperation createXmlOperation() {
        XmlOperationImpl xmlOperation = new XmlOperationImpl();
        return xmlOperation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlInput createXmlInput() {
        XmlInputImpl xmlInput = new XmlInputImpl();
        return xmlInput;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlServiceComponent createXmlServiceComponent() {
        XmlServiceComponentImpl xmlServiceComponent = new XmlServiceComponentImpl();
        return xmlServiceComponent;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlOutput createXmlOutput() {
        XmlOutputImpl xmlOutput = new XmlOutputImpl();
        return xmlOutput;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlMessage createXmlMessage() {
        XmlMessageImpl xmlMessage = new XmlMessageImpl();
        return xmlMessage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlResult createXmlResult() {
        XmlResultImpl xmlResult = new XmlResultImpl();
        return xmlResult;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlServicePackage getXmlServicePackage() {
        return (XmlServicePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static XmlServicePackage getPackage() {
        return XmlServicePackage.eINSTANCE;
    }

} // XmlServiceFactoryImpl
