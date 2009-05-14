/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Annotation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.Annotation#getDescription <em>Description</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Annotation#getKeywords <em>Keywords</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Annotation#getTags <em>Tags</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Annotation#getAnnotationContainer <em>Annotation Container</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Annotation#getAnnotatedObject <em>Annotated Object</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Annotation#getExtensionObject <em>Extension Object</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.core.CorePackage#getAnnotation()
 * @model
 * @generated
 */
public interface Annotation extends EObject {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getAnnotation_Description()
     * @model
     * @generated
     */
    String getDescription();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Annotation#getDescription <em>Description</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription( String value );

    /**
     * Returns the value of the '<em><b>Keywords</b></em>' attribute list. The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Keywords</em>' attribute list isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Keywords</em>' attribute list.
     * @see com.metamatrix.metamodels.core.CorePackage#getAnnotation_Keywords()
     * @model type="java.lang.String"
     * @generated
     */
    EList getKeywords();

    /**
     * Returns the value of the '<em><b>Annotated Object</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Annotated Object</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Annotated Object</em>' reference.
     * @see #setAnnotatedObject(EObject)
     * @see com.metamatrix.metamodels.core.CorePackage#getAnnotation_AnnotatedObject()
     * @model
     * @generated
     */
    EObject getAnnotatedObject();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Annotation#getAnnotatedObject <em>Annotated Object</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Annotated Object</em>' reference.
     * @see #getAnnotatedObject()
     * @generated
     */
    void setAnnotatedObject( EObject value );

    /**
     * Returns the value of the '<em><b>Extension Object</b></em>' containment reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Extension Object</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Extension Object</em>' containment reference.
     * @see #setExtensionObject(EObject)
     * @see com.metamatrix.metamodels.core.CorePackage#getAnnotation_ExtensionObject()
     * @model containment="true" transient="true"
     * @generated
     */
    EObject getExtensionObject();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Annotation#getExtensionObject <em>Extension Object</em>}'
     * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Extension Object</em>' containment reference.
     * @see #getExtensionObject()
     * @generated
     */
    void setExtensionObject( EObject value );

    /**
     * Returns the value of the '<em><b>Tags</b></em>' map. The key is of type {@link java.lang.String}, and the value is of type
     * {@link java.lang.String}, <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tags</em>' map isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Tags</em>' map.
     * @see com.metamatrix.metamodels.core.CorePackage#getAnnotation_Tags()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String"
     * @generated
     */
    EMap getTags();

    /**
     * Returns the value of the '<em><b>Annotation Container</b></em>' container reference. It is bidirectional and its opposite
     * is '{@link com.metamatrix.metamodels.core.AnnotationContainer#getAnnotations <em>Annotations</em>}'. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Annotation Container</em>' container reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Annotation Container</em>' container reference.
     * @see #setAnnotationContainer(AnnotationContainer)
     * @see com.metamatrix.metamodels.core.CorePackage#getAnnotation_AnnotationContainer()
     * @see com.metamatrix.metamodels.core.AnnotationContainer#getAnnotations
     * @model opposite="annotations"
     * @generated
     */
    AnnotationContainer getAnnotationContainer();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Annotation#getAnnotationContainer
     * <em>Annotation Container</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Annotation Container</em>' container reference.
     * @see #getAnnotationContainer()
     * @generated
     */
    void setAnnotationContainer( AnnotationContainer value );

} // Annotation
