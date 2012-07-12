/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.transformation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Expression Owner</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.transformation.ExpressionOwner#getExpressions <em>Expressions</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getExpressionOwner()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ExpressionOwner extends EObject {

    /**
     * Returns the value of the '<em><b>Expressions</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.transformation.Expression}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.transformation.Expression#getOwner <em>Owner</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Expressions</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Expressions</em>' containment reference list.
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getExpressionOwner_Expressions()
     * @see org.teiid.designer.metamodels.transformation.Expression#getOwner
     * @model type="org.teiid.designer.metamodels.transformation.Expression" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getExpressions();

} // ExpressionOwner
