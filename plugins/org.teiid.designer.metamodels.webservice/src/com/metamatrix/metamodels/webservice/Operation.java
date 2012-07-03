/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Operation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.webservice.Operation#getPattern <em>Pattern</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.Operation#isSafe <em>Safe</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.Operation#getInput <em>Input</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.Operation#getOutput <em>Output</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.Operation#getInterface <em>Interface</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOperation()
 * @model
 * @generated
 */
public interface Operation extends WebServiceComponent {

    /**
     * Returns the value of the '<em><b>Pattern</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Pattern</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Pattern</em>' attribute.
     * @see #setPattern(String)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOperation_Pattern()
     * @model
     * @generated
     */
    String getPattern();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Operation#getPattern <em>Pattern</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Pattern</em>' attribute.
     * @see #getPattern()
     * @generated
     */
    void setPattern( String value );

    /**
     * Returns the value of the '<em><b>Safe</b></em>' attribute. The default value is <code>"false"</code>. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Safe</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Safe</em>' attribute.
     * @see #setSafe(boolean)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOperation_Safe()
     * @model default="false"
     * @generated
     */
    boolean isSafe();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Operation#isSafe <em>Safe</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Safe</em>' attribute.
     * @see #isSafe()
     * @generated
     */
    void setSafe( boolean value );

    /**
     * Returns the value of the '<em><b>Input</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.webservice.Input#getOperation <em>Operation</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input</em>' containment reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Input</em>' containment reference.
     * @see #setInput(Input)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOperation_Input()
     * @see com.metamatrix.metamodels.webservice.Input#getOperation
     * @model opposite="operation" containment="true"
     * @generated
     */
    Input getInput();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Operation#getInput <em>Input</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Input</em>' containment reference.
     * @see #getInput()
     * @generated
     */
    void setInput( Input value );

    /**
     * Returns the value of the '<em><b>Output</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.webservice.Output#getOperation <em>Operation</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Output</em>' containment reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Output</em>' containment reference.
     * @see #setOutput(Output)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOperation_Output()
     * @see com.metamatrix.metamodels.webservice.Output#getOperation
     * @model opposite="operation" containment="true"
     * @generated
     */
    Output getOutput();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Operation#getOutput <em>Output</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Output</em>' containment reference.
     * @see #getOutput()
     * @generated
     */
    void setOutput( Output value );

    /**
     * Returns the value of the '<em><b>Interface</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.webservice.Interface#getOperations <em>Operations</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Interface</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Interface</em>' container reference.
     * @see #setInterface(Interface)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOperation_Interface()
     * @see com.metamatrix.metamodels.webservice.Interface#getOperations
     * @model opposite="operations" required="true"
     * @generated
     */
    Interface getInterface();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Operation#getInterface <em>Interface</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Interface</em>' container reference.
     * @see #getInterface()
     * @generated
     */
    void setInterface( Interface value );
    
    /**
     * Returns the value of the '<em><b>Update Count</b></em>' attribute.
     * The default value is <code>"AUTO"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.webservice.OperationUpdateCount}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update Count</em>' attribute.
     * @see com.metamatrix.metamodels.webservice.OperationUpdateCount
     * @see #setUpdateCount(OperationUpdateCount)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOperation_UpdateCount()
     * @model default="AUTO"
     * @generated
     */
    OperationUpdateCount getUpdateCount();
    
    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Operation#getUpdateCount <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Update Count</em>' attribute.
     * @see com.metamatrix.metamodels.webservice.OperationUpdateCount
     * @see #getUpdateCount()
     * @generated
     */
    void setUpdateCount(OperationUpdateCount newUpdateCount);

} // Operation
