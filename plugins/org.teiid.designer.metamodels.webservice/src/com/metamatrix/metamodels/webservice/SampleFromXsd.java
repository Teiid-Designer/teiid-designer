/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.xml.XmlElement;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Sample From Xsd</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getMaxNumberOfLevelsToBuild <em>Max Number Of Levels To Build
 * </em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleFragment <em>Sample Fragment</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleMessages <em>Sample Messages</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFromXsd()
 * @model
 * @generated
 */
public interface SampleFromXsd extends EObject {

    /**
     * Returns the value of the '<em><b>Max Number Of Levels To Build</b></em>' attribute. The default value is <code>"30"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Max Number Of Levels To Build</em>' attribute isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Max Number Of Levels To Build</em>' attribute.
     * @see #setMaxNumberOfLevelsToBuild(int)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFromXsd_MaxNumberOfLevelsToBuild()
     * @model default="30"
     * @generated
     */
    int getMaxNumberOfLevelsToBuild();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getMaxNumberOfLevelsToBuild
     * <em>Max Number Of Levels To Build</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Max Number Of Levels To Build</em>' attribute.
     * @see #getMaxNumberOfLevelsToBuild()
     * @generated
     */
    void setMaxNumberOfLevelsToBuild( int value );

    /**
     * Returns the value of the '<em><b>Sample Fragment</b></em>' containment reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sample Fragment</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Sample Fragment</em>' containment reference.
     * @see #setSampleFragment(XmlElement)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFromXsd_SampleFragment()
     * @model containment="true"
     * @generated
     */
    XmlElement getSampleFragment();

    /**
     * Returns the value of the '<em><b>Sample Messages</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.webservice.SampleMessages#getSampleFromXsd <em>Sample From Xsd</em>}'. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Sample Messages</em>' container reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Sample Messages</em>' container reference.
     * @see #setSampleMessages(SampleMessages)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFromXsd_SampleMessages()
     * @see com.metamatrix.metamodels.webservice.SampleMessages#getSampleFromXsd
     * @model opposite="sampleFromXsd" required="true"
     * @generated
     */
    SampleMessages getSampleMessages();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleMessages
     * <em>Sample Messages</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Sample Messages</em>' container reference.
     * @see #getSampleMessages()
     * @generated
     */
    void setSampleMessages( SampleMessages value );

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleFragment
     * <em>Sample Fragment</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Sample Fragment</em>' containment reference.
     * @see #getSampleFragment()
     * @generated
     */
    void setSampleFragment( XmlElement value );

} // SampleFromXsd
