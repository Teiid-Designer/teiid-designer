/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Sample Messages</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.webservice.SampleMessages#getMessage <em>Message</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.webservice.SampleMessages#getSampleFiles <em>Sample Files</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.webservice.SampleMessages#getSampleFromXsd <em>Sample From Xsd</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleMessages()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface SampleMessages extends EObject {

    /**
     * Returns the value of the '<em><b>Message</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.webservice.Message#getSamples <em>Samples</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Message</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Message</em>' container reference.
     * @see #setMessage(Message)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleMessages_Message()
     * @see org.teiid.designer.metamodels.webservice.Message#getSamples
     * @model opposite="samples" required="true"
     * @generated
     */
    Message getMessage();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.SampleMessages#getMessage <em>Message</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Message</em>' container reference.
     * @see #getMessage()
     * @generated
     */
    void setMessage( Message value );

    /**
     * Returns the value of the '<em><b>Sample Files</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.webservice.SampleFile}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.webservice.SampleFile#getSampleMessages <em>Sample Messages</em>}'. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Sample Files</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Sample Files</em>' containment reference list.
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleMessages_SampleFiles()
     * @see org.teiid.designer.metamodels.webservice.SampleFile#getSampleMessages
     * @model type="org.teiid.designer.metamodels.webservice.SampleFile" opposite="sampleMessages" containment="true"
     * @generated
     */
    EList getSampleFiles();

    /**
     * Returns the value of the '<em><b>Sample From Xsd</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.webservice.SampleFromXsd#getSampleMessages <em>Sample Messages</em>}'. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Sample From Xsd</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Sample From Xsd</em>' containment reference.
     * @see #setSampleFromXsd(SampleFromXsd)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getSampleMessages_SampleFromXsd()
     * @see org.teiid.designer.metamodels.webservice.SampleFromXsd#getSampleMessages
     * @model opposite="sampleMessages" containment="true"
     * @generated
     */
    SampleFromXsd getSampleFromXsd();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.SampleMessages#getSampleFromXsd
     * <em>Sample From Xsd</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Sample From Xsd</em>' containment reference.
     * @see #getSampleFromXsd()
     * @generated
     */
    void setSampleFromXsd( SampleFromXsd value );

} // SampleMessages
