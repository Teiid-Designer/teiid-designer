/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.metamodels.wsdl.mime.MimeContent;
import com.metamatrix.metamodels.wsdl.mime.MimeFactory;
import com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.mime.MimePart;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class MimeFactoryImpl extends EFactoryImpl implements MimeFactory {

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MimeFactoryImpl() {
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
            case MimePackage.MIME_CONTENT:
                return createMimeContent();
            case MimePackage.MIME_MULTIPART_RELATED:
                return createMimeMultipartRelated();
            case MimePackage.MIME_PART:
                return createMimePart();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MimeContent createMimeContent() {
        MimeContentImpl mimeContent = new MimeContentImpl();
        return mimeContent;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MimeMultipartRelated createMimeMultipartRelated() {
        MimeMultipartRelatedImpl mimeMultipartRelated = new MimeMultipartRelatedImpl();
        return mimeMultipartRelated;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MimePart createMimePart() {
        MimePartImpl mimePart = new MimePartImpl();
        return mimePart;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MimePackage getMimePackage() {
        return (MimePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static MimePackage getPackage() { // NO_UCD
        return MimePackage.eINSTANCE;
    }

} // MimeFactoryImpl
