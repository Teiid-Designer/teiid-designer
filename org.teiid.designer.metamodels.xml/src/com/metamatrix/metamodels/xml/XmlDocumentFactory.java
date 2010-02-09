/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage
 * @generated
 */
public interface XmlDocumentFactory extends EFactory {
    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    XmlDocumentFactory eINSTANCE = new com.metamatrix.metamodels.xml.impl.XmlDocumentFactoryImpl();

    /**
     * Returns a new object of class '<em>Xml Fragment</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Fragment</em>'.
     * @generated
     */
    XmlFragment createXmlFragment();

    /**
     * Returns a new object of class '<em>Xml Document</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Document</em>'.
     * @generated
     */
    XmlDocument createXmlDocument();

    /**
     * Returns a new object of class '<em>Xml Element</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Element</em>'.
     * @generated
     */
    XmlElement createXmlElement();

    /**
     * Returns a new object of class '<em>Xml Attribute</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Attribute</em>'.
     * @generated
     */
    XmlAttribute createXmlAttribute();

    /**
     * Returns a new object of class '<em>Xml Root</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Root</em>'.
     * @generated
     */
    XmlRoot createXmlRoot();

    /**
     * Returns a new object of class '<em>Xml Comment</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Comment</em>'.
     * @generated
     */
    XmlComment createXmlComment();

    /**
     * Returns a new object of class '<em>Xml Namespace</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Namespace</em>'.
     * @generated
     */
    XmlNamespace createXmlNamespace();

    /**
     * Returns a new object of class '<em>Xml Sequence</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Sequence</em>'.
     * @generated
     */
    XmlSequence createXmlSequence();

    /**
     * Returns a new object of class '<em>Xml All</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml All</em>'.
     * @generated
     */
    XmlAll createXmlAll();

    /**
     * Returns a new object of class '<em>Xml Choice</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Choice</em>'.
     * @generated
     */
    XmlChoice createXmlChoice();

    /**
     * Returns a new object of class '<em>Processing Instruction</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Processing Instruction</em>'.
     * @generated
     */
    ProcessingInstruction createProcessingInstruction();

    /**
     * Returns a new object of class '<em>Xml Fragment Use</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Xml Fragment Use</em>'.
     * @generated
     */
    XmlFragmentUse createXmlFragmentUse();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    XmlDocumentPackage getXmlDocumentPackage(); // NO_UCD

} // XmlDocumentFactory
