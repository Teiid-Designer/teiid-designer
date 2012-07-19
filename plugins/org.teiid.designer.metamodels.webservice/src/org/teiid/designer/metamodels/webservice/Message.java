/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Message</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.webservice.Message#getContentElement <em>Content Element</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.webservice.Message#getSamples <em>Samples</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.webservice.Message#getContentComplexType <em>Content Complex Type</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.webservice.Message#getContentSimpleType <em>Content Simple Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getMessage()
 * @model abstract="true"
 * @generated
 *
 * @since 8.0
 */
public interface Message extends WebServiceComponent {

    /**
     * Returns the value of the '<em><b>Content Element</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Content Element</em>' reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Content Element</em>' reference.
     * @see #setContentElement(XSDElementDeclaration)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getMessage_ContentElement()
     * @model
     * @generated
     */
    XSDElementDeclaration getContentElement();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.Message#getContentElement <em>Content Element</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Content Element</em>' reference.
     * @see #getContentElement()
     * @generated
     */
    void setContentElement( XSDElementDeclaration value );

    /**
     * Returns the value of the '<em><b>Samples</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.webservice.SampleMessages#getMessage <em>Message</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Samples</em>' containment reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Samples</em>' containment reference.
     * @see #setSamples(SampleMessages)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getMessage_Samples()
     * @see org.teiid.designer.metamodels.webservice.SampleMessages#getMessage
     * @model opposite="message" containment="true"
     * @generated
     */
    SampleMessages getSamples();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.Message#getSamples <em>Samples</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Samples</em>' containment reference.
     * @see #getSamples()
     * @generated
     */
    void setSamples( SampleMessages value );

    /**
     * Returns the value of the '<em><b>Content Complex Type</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Content Complex Type</em>' reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Content Complex Type</em>' reference.
     * @see #setContentComplexType(XSDComplexTypeDefinition)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getMessage_ContentComplexType()
     * @model
     * @generated
     */
    XSDComplexTypeDefinition getContentComplexType();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.Message#getContentComplexType
     * <em>Content Complex Type</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Content Complex Type</em>' reference.
     * @see #getContentComplexType()
     * @generated
     */
    void setContentComplexType( XSDComplexTypeDefinition value );

    /**
     * Returns the value of the '<em><b>Content Simple Type</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Content Simple Type</em>' reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Content Simple Type</em>' reference.
     * @see #setContentSimpleType(XSDSimpleTypeDefinition)
     * @see org.teiid.designer.metamodels.webservice.WebServicePackage#getMessage_ContentSimpleType()
     * @model
     * @generated
     */
    XSDSimpleTypeDefinition getContentSimpleType();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.webservice.Message#getContentSimpleType
     * <em>Content Simple Type</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Content Simple Type</em>' reference.
     * @see #getContentSimpleType()
     * @generated
     */
    void setContentSimpleType( XSDSimpleTypeDefinition value );

} // Message
