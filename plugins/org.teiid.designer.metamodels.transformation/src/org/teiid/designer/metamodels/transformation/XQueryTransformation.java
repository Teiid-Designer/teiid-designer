/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.transformation;

import org.eclipse.emf.mapping.MappingHelper;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>XQuery Transformation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.transformation.XQueryTransformation#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getXQueryTransformation()
 * @model
 * @generated
 */
public interface XQueryTransformation extends MappingHelper {

    /**
     * Returns the value of the '<em><b>Expression</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Expression</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Expression</em>' attribute.
     * @see #setExpression(String)
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getXQueryTransformation_Expression()
     * @model
     * @generated
     */
    String getExpression();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.transformation.XQueryTransformation#getExpression
     * <em>Expression</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Expression</em>' attribute.
     * @see #getExpression()
     * @generated
     */
    void setExpression( String value );

} // XQueryTransformation
