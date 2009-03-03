/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.core.AnnotationContainer#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.core.CorePackage#getAnnotationContainer()
 * @model
 * @generated
 */
public interface AnnotationContainer extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Annotations</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.core.Annotation}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.core.Annotation#getAnnotationContainer <em>Annotation Container</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Annotations</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Annotations</em>' containment reference list.
     * @see com.metamatrix.metamodels.core.CorePackage#getAnnotationContainer_Annotations()
     * @see com.metamatrix.metamodels.core.Annotation#getAnnotationContainer
     * @model type="com.metamatrix.metamodels.core.Annotation" opposite="annotationContainer" containment="true"
     * @generated
     */
    EList getAnnotations();

    Annotation findAnnotation( final EObject annotatedObject );
    


} // AnnotationContainer
