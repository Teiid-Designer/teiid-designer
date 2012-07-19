/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Procedure</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.Procedure#isFunction <em>Function</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Procedure#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Procedure#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Procedure#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Procedure#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getProcedure()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface Procedure extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Function</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Function</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Function</em>' attribute.
     * @see #setFunction(boolean)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getProcedure_Function()
     * @model
     * @generated
     */
    boolean isFunction();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.Procedure#isFunction <em>Function</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Function</em>' attribute.
     * @see #isFunction()
     * @generated
     */
    void setFunction(boolean value);

    /**
     * Returns the value of the '<em><b>Schema</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Schema#getProcedures <em>Procedures</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schema</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schema</em>' container reference.
     * @see #setSchema(Schema)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getProcedure_Schema()
     * @see org.teiid.designer.metamodels.relational.Schema#getProcedures
     * @model opposite="procedures"
     * @generated
     */
    Schema getSchema();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.Procedure#getSchema <em>Schema</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Schema</em>' container reference.
     * @see #getSchema()
     * @generated
     */
    void setSchema(Schema value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.ProcedureParameter}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.ProcedureParameter#getProcedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getProcedure_Parameters()
     * @see org.teiid.designer.metamodels.relational.ProcedureParameter#getProcedure
     * @model type="org.teiid.designer.metamodels.relational.ProcedureParameter" opposite="procedure" containment="true"
     * @generated
     */
    EList getParameters();

    /**
     * Returns the value of the '<em><b>Catalog</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Catalog#getProcedures <em>Procedures</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Catalog</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' container reference.
     * @see #setCatalog(Catalog)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getProcedure_Catalog()
     * @see org.teiid.designer.metamodels.relational.Catalog#getProcedures
     * @model opposite="procedures"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.Procedure#getCatalog <em>Catalog</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' container reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

    /**
     * Returns the value of the '<em><b>Result</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.ProcedureResult#getProcedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Result</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Result</em>' containment reference.
     * @see #setResult(ProcedureResult)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getProcedure_Result()
     * @see org.teiid.designer.metamodels.relational.ProcedureResult#getProcedure
     * @model opposite="procedure" containment="true"
     * @generated
     */
    ProcedureResult getResult();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.Procedure#getResult <em>Result</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Result</em>' containment reference.
     * @see #getResult()
     * @generated
     */
    void setResult(ProcedureResult value);

    /**
     * Returns the value of the '<em><b>Update Count</b></em>' attribute.
     * The default value is <code>"AUTO"</code>.
     * The literals are from the enumeration {@link org.teiid.designer.metamodels.relational.ProcedureUpdateCount}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update Count</em>' attribute.
     * @see org.teiid.designer.metamodels.relational.ProcedureUpdateCount
     * @see #setUpdateCount(ProcedureUpdateCount)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getProcedure_UpdateCount()
     * @model default="AUTO"
     * @generated
     */
    ProcedureUpdateCount getUpdateCount();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.Procedure#getUpdateCount <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Update Count</em>' attribute.
     * @see org.teiid.designer.metamodels.relational.ProcedureUpdateCount
     * @see #getUpdateCount()
     * @generated
     */
    void setUpdateCount(ProcedureUpdateCount value);

} // Procedure
