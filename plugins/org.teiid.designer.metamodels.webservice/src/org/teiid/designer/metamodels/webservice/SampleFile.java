/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Sample File</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.webservice.SampleFile#getName <em>Name</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.webservice.SampleFile#getUrl <em>Url</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.webservice.SampleFile#getSampleMessages <em>Sample Messages</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleFile()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface SampleFile extends EObject {

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleFile_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.SampleFile#getName <em>Name</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName( String value );

    /**
     * Returns the value of the '<em><b>Url</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Url</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Url</em>' attribute.
     * @see #setUrl(String)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleFile_Url()
     * @model
     * @generated
     */
    String getUrl();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.SampleFile#getUrl <em>Url</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Url</em>' attribute.
     * @see #getUrl()
     * @generated
     */
    void setUrl( String value );

    /**
     * Returns the value of the '<em><b>Sample Messages</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.webservice.SampleMessages#getSampleFiles <em>Sample Files</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sample Messages</em>' container reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Sample Messages</em>' container reference.
     * @see #setSampleMessages(SampleMessages)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleFile_SampleMessages()
     * @see org.teiid.designer.metamodels.webservice.SampleMessages#getSampleFiles
     * @model opposite="sampleFiles" required="true"
     * @generated
     */
    SampleMessages getSampleMessages();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.SampleFile#getSampleMessages <em>Sample Messages</em>}'
     * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Sample Messages</em>' container reference.
     * @see #getSampleMessages()
     * @generated
     */
    void setSampleMessages( SampleMessages value );

} // SampleFile
