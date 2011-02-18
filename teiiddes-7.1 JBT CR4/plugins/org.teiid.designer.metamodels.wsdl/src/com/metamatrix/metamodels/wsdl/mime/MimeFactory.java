/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.wsdl.mime.MimePackage
 * @generated
 */
public interface MimeFactory extends EFactory {

    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    MimeFactory eINSTANCE = new com.metamatrix.metamodels.wsdl.mime.impl.MimeFactoryImpl();

    /**
     * Returns a new object of class '<em>Content</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Content</em>'.
     * @generated
     */
    MimeContent createMimeContent();

    /**
     * Returns a new object of class '<em>Multipart Related</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Multipart Related</em>'.
     * @generated
     */
    MimeMultipartRelated createMimeMultipartRelated();

    /**
     * Returns a new object of class '<em>Part</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Part</em>'.
     * @generated
     */
    MimePart createMimePart();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    MimePackage getMimePackage(); // NO_UCD

} // MimeFactory
