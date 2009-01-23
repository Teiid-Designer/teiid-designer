/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.mime.MimeFactory
 * @generated
 */
public interface MimePackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNAME = "mime"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_URI = "http://schemas.xmlsoap.org/wsdl/mime/"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_PREFIX = "mime"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	MimePackage eINSTANCE = com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.mime.MimeElement <em>Element</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElement
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl#getMimeElement()
     * @generated
     */
	int MIME_ELEMENT = 4;

    /**
     * The feature id for the '<em><b>Mime Element Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_ELEMENT__MIME_ELEMENT_OWNER = 0;

    /**
     * The number of structural features of the the '<em>Element</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_ELEMENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeContentImpl <em>Content</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimeContentImpl
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl#getMimeContent()
     * @generated
     */
	int MIME_CONTENT = 0;

    /**
     * The feature id for the '<em><b>Mime Element Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_CONTENT__MIME_ELEMENT_OWNER = MIME_ELEMENT__MIME_ELEMENT_OWNER;

    /**
     * The feature id for the '<em><b>Message Part</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_CONTENT__MESSAGE_PART = MIME_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_CONTENT__TYPE = MIME_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Xml</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_CONTENT__XML = MIME_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Content</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_CONTENT_FEATURE_COUNT = MIME_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeMultipartRelatedImpl <em>Multipart Related</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimeMultipartRelatedImpl
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl#getMimeMultipartRelated()
     * @generated
     */
	int MIME_MULTIPART_RELATED = 1;

    /**
     * The feature id for the '<em><b>Mime Element Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER = MIME_ELEMENT__MIME_ELEMENT_OWNER;

    /**
     * The feature id for the '<em><b>Mime Parts</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_MULTIPART_RELATED__MIME_PARTS = MIME_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Multipart Related</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_MULTIPART_RELATED_FEATURE_COUNT = MIME_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.mime.MimeElementOwner <em>Element Owner</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElementOwner
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl#getMimeElementOwner()
     * @generated
     */
	int MIME_ELEMENT_OWNER = 3;

    /**
     * The feature id for the '<em><b>Mime Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_ELEMENT_OWNER__MIME_ELEMENTS = 0;

    /**
     * The number of structural features of the the '<em>Element Owner</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_ELEMENT_OWNER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.mime.impl.MimePartImpl <em>Part</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimePartImpl
     * @see com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl#getMimePart()
     * @generated
     */
	int MIME_PART = 2;

    /**
     * The feature id for the '<em><b>Mime Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_PART__MIME_ELEMENTS = MIME_ELEMENT_OWNER__MIME_ELEMENTS;

    /**
     * The feature id for the '<em><b>Mime Multipart Related</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_PART__MIME_MULTIPART_RELATED = MIME_ELEMENT_OWNER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Part</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MIME_PART_FEATURE_COUNT = MIME_ELEMENT_OWNER_FEATURE_COUNT + 1;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.mime.MimeContent <em>Content</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Content</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeContent
     * @generated
     */
	EClass getMimeContent();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeContent#getType()
     * @see #getMimeContent()
     * @generated
     */
	EAttribute getMimeContent_Type();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#isXml <em>Xml</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Xml</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeContent#isXml()
     * @see #getMimeContent()
     * @generated
     */
	EAttribute getMimeContent_Xml();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#getMessagePart <em>Message Part</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Message Part</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeContent#getMessagePart()
     * @see #getMimeContent()
     * @generated
     */
	EReference getMimeContent_MessagePart();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated <em>Multipart Related</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Multipart Related</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated
     * @generated
     */
	EClass getMimeMultipartRelated();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated#getMimeParts <em>Mime Parts</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Mime Parts</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated#getMimeParts()
     * @see #getMimeMultipartRelated()
     * @generated
     */
	EReference getMimeMultipartRelated_MimeParts();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.mime.MimePart <em>Part</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Part</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimePart
     * @generated
     */
	EClass getMimePart();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated <em>Mime Multipart Related</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Mime Multipart Related</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated()
     * @see #getMimePart()
     * @generated
     */
	EReference getMimePart_MimeMultipartRelated();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.mime.MimeElementOwner <em>Element Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Element Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElementOwner
     * @generated
     */
	EClass getMimeElementOwner();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.mime.MimeElementOwner#getMimeElements <em>Mime Elements</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Mime Elements</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElementOwner#getMimeElements()
     * @see #getMimeElementOwner()
     * @generated
     */
	EReference getMimeElementOwner_MimeElements();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.mime.MimeElement <em>Element</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Element</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElement
     * @generated
     */
	EClass getMimeElement();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.mime.MimeElement#getMimeElementOwner <em>Mime Element Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Mime Element Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElement#getMimeElementOwner()
     * @see #getMimeElement()
     * @generated
     */
	EReference getMimeElement_MimeElementOwner();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	MimeFactory getMimeFactory();

} //MimePackage
