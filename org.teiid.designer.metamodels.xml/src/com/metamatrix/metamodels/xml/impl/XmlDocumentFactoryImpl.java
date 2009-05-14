/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ChoiceErrorMode;
import com.metamatrix.metamodels.xml.NormalizationType;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.SoapEncoding;
import com.metamatrix.metamodels.xml.ValueType;
import com.metamatrix.metamodels.xml.XmlAll;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlFragmentUse;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.XmlSequence;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class XmlDocumentFactoryImpl extends EFactoryImpl implements XmlDocumentFactory {
    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlDocumentFactoryImpl() {
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
            case XmlDocumentPackage.XML_FRAGMENT:
                return createXmlFragment();
            case XmlDocumentPackage.XML_DOCUMENT:
                return createXmlDocument();
            case XmlDocumentPackage.XML_ELEMENT:
                return createXmlElement();
            case XmlDocumentPackage.XML_ATTRIBUTE:
                return createXmlAttribute();
            case XmlDocumentPackage.XML_ROOT:
                return createXmlRoot();
            case XmlDocumentPackage.XML_COMMENT:
                return createXmlComment();
            case XmlDocumentPackage.XML_NAMESPACE:
                return createXmlNamespace();
            case XmlDocumentPackage.XML_SEQUENCE:
                return createXmlSequence();
            case XmlDocumentPackage.XML_ALL:
                return createXmlAll();
            case XmlDocumentPackage.XML_CHOICE:
                return createXmlChoice();
            case XmlDocumentPackage.PROCESSING_INSTRUCTION:
                return createProcessingInstruction();
            case XmlDocumentPackage.XML_FRAGMENT_USE:
                return createXmlFragmentUse();
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
            case XmlDocumentPackage.SOAP_ENCODING: {
                SoapEncoding result = SoapEncoding.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.CHOICE_ERROR_MODE: {
                ChoiceErrorMode result = ChoiceErrorMode.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.VALUE_TYPE: {
                ValueType result = ValueType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.BUILD_STATUS: {
                BuildStatus result = BuildStatus.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.NORMALIZATION_TYPE: {
                NormalizationType result = NormalizationType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.LIST:
                return createListFromString(eDataType, initialValue);
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
            case XmlDocumentPackage.SOAP_ENCODING:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.CHOICE_ERROR_MODE:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.VALUE_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.BUILD_STATUS:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.NORMALIZATION_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.LIST:
                return convertListToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlFragment createXmlFragment() {
        XmlFragmentImpl xmlFragment = new XmlFragmentImpl();
        return xmlFragment;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlDocument createXmlDocument() {
        XmlDocumentImpl xmlDocument = new XmlDocumentImpl();
        return xmlDocument;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlElement createXmlElement() {
        XmlElementImpl xmlElement = new XmlElementImpl();
        return xmlElement;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlAttribute createXmlAttribute() {
        XmlAttributeImpl xmlAttribute = new XmlAttributeImpl();
        return xmlAttribute;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlRoot createXmlRoot() {
        XmlRootImpl xmlRoot = new XmlRootImpl();
        return xmlRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlComment createXmlComment() {
        XmlCommentImpl xmlComment = new XmlCommentImpl();
        return xmlComment;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlNamespace createXmlNamespace() {
        XmlNamespaceImpl xmlNamespace = new XmlNamespaceImpl();
        return xmlNamespace;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlSequence createXmlSequence() {
        XmlSequenceImpl xmlSequence = new XmlSequenceImpl();
        return xmlSequence;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlAll createXmlAll() {
        XmlAllImpl xmlAll = new XmlAllImpl();
        return xmlAll;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlChoice createXmlChoice() {
        XmlChoiceImpl xmlChoice = new XmlChoiceImpl();
        return xmlChoice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ProcessingInstruction createProcessingInstruction() {
        ProcessingInstructionImpl processingInstruction = new ProcessingInstructionImpl();
        return processingInstruction;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlFragmentUse createXmlFragmentUse() {
        XmlFragmentUseImpl xmlFragmentUse = new XmlFragmentUseImpl();
        return xmlFragmentUse;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List createListFromString( EDataType eDataType,
                                      String initialValue ) {
        return (List)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertListToString( EDataType eDataType,
                                       Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlDocumentPackage getXmlDocumentPackage() {
        return (XmlDocumentPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static XmlDocumentPackage getPackage() {
        return XmlDocumentPackage.eINSTANCE;
    }

} // XmlDocumentFactoryImpl
