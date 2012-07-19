/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.core;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.core.AnnotationContainer#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.core.CorePackage#getAnnotationContainer()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface AnnotationContainer extends EObject {

    /**
     * Returns the value of the '<em><b>Annotations</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.core.Annotation}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.core.Annotation#getAnnotationContainer <em>Annotation Container</em>}'. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Annotations</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Annotations</em>' containment reference list.
     * @see org.teiid.designer.metamodels.core.CorePackage#getAnnotationContainer_Annotations()
     * @see org.teiid.designer.metamodels.core.Annotation#getAnnotationContainer
     * @model type="org.teiid.designer.metamodels.core.Annotation" opposite="annotationContainer" containment="true"
     * @generated
     */
    EList getAnnotations();

    Annotation findAnnotation( final EObject annotatedObject );

} // AnnotationContainer
