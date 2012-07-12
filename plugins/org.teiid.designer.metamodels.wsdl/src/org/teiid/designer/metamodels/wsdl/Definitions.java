/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Definitions</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Definitions#getTargetNamespace <em>Target Namespace</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Definitions#getMessages <em>Messages</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Definitions#getPortTypes <em>Port Types</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Definitions#getBindings <em>Bindings</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Definitions#getServices <em>Services</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Definitions#getImports <em>Imports</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Definitions#getTypes <em>Types</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions()
 * @model
 * @generated
 */
public interface Definitions extends WsdlNameOptionalEntity, ExtensibleDocumented {

    /**
     * Returns the value of the '<em><b>Target Namespace</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target Namespace</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Target Namespace</em>' attribute.
     * @see #setTargetNamespace(String)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions_TargetNamespace()
     * @model
     * @generated
     */
    String getTargetNamespace();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Definitions#getTargetNamespace <em>Target Namespace</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Target Namespace</em>' attribute.
     * @see #getTargetNamespace()
     * @generated
     */
    void setTargetNamespace( String value );

    /**
     * Returns the value of the '<em><b>Messages</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.Message}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Message#getDefinitions <em>Definitions</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Messages</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Messages</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions_Messages()
     * @see org.teiid.designer.metamodels.wsdl.Message#getDefinitions
     * @model type="org.teiid.designer.metamodels.wsdl.Message" opposite="definitions" containment="true"
     * @generated
     */
    EList getMessages();

    /**
     * Returns the value of the '<em><b>Port Types</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.PortType}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.PortType#getDefinitions <em>Definitions</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Port Types</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Port Types</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions_PortTypes()
     * @see org.teiid.designer.metamodels.wsdl.PortType#getDefinitions
     * @model type="org.teiid.designer.metamodels.wsdl.PortType" opposite="definitions" containment="true"
     * @generated
     */
    EList getPortTypes();

    /**
     * Returns the value of the '<em><b>Bindings</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.Binding}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Binding#getDefinitions <em>Definitions</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Bindings</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Bindings</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions_Bindings()
     * @see org.teiid.designer.metamodels.wsdl.Binding#getDefinitions
     * @model type="org.teiid.designer.metamodels.wsdl.Binding" opposite="definitions" containment="true"
     * @generated
     */
    EList getBindings();

    /**
     * Returns the value of the '<em><b>Services</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.Service}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Service#getDefinitions <em>Definitions</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Services</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Services</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions_Services()
     * @see org.teiid.designer.metamodels.wsdl.Service#getDefinitions
     * @model type="org.teiid.designer.metamodels.wsdl.Service" opposite="definitions" containment="true"
     * @generated
     */
    EList getServices();

    /**
     * Returns the value of the '<em><b>Imports</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.Import}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Import#getDefinitions <em>Definitions</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Imports</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Imports</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions_Imports()
     * @see org.teiid.designer.metamodels.wsdl.Import#getDefinitions
     * @model type="org.teiid.designer.metamodels.wsdl.Import" opposite="definitions" containment="true"
     * @generated
     */
    EList getImports();

    /**
     * Returns the value of the '<em><b>Types</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Types#getDefinitions <em>Definitions</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Types</em>' containment reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Types</em>' containment reference.
     * @see #setTypes(Types)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDefinitions_Types()
     * @see org.teiid.designer.metamodels.wsdl.Types#getDefinitions
     * @model opposite="definitions" containment="true"
     * @generated
     */
    Types getTypes();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Definitions#getTypes <em>Types</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Types</em>' containment reference.
     * @see #getTypes()
     * @generated
     */
    void setTypes( Types value );

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model dataType="org.teiid.designer.metamodels.wsdl.IStatus" parameters=""
     * @generated
     */
    IStatus isValid(); // NO_UCD

} // Definitions
