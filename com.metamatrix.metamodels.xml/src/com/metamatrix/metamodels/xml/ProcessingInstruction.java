/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Processing Instruction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getRawText <em>Raw Text</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getProcessingInstruction()
 * @model
 * @generated
 */
public interface ProcessingInstruction extends XmlDocumentEntity{
    /**
     * Returns the value of the '<em><b>Raw Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Raw Text</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Raw Text</em>' attribute.
     * @see #setRawText(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getProcessingInstruction_RawText()
     * @model
     * @generated
     */
    String getRawText();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getRawText <em>Raw Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Raw Text</em>' attribute.
     * @see #getRawText()
     * @generated
     */
    void setRawText(String value);

    /**
     * Returns the value of the '<em><b>Target</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target</em>' attribute.
     * @see #setTarget(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getProcessingInstruction_Target()
     * @model
     * @generated
     */
    String getTarget();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getTarget <em>Target</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' attribute.
     * @see #getTarget()
     * @generated
     */
    void setTarget(String value);

    /**
     * Returns the value of the '<em><b>Parent</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.ProcessingInstructionHolder#getProcessingInstructions <em>Processing Instructions</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parent</em>' container reference.
     * @see #setParent(ProcessingInstructionHolder)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getProcessingInstruction_Parent()
     * @see com.metamatrix.metamodels.xml.ProcessingInstructionHolder#getProcessingInstructions
     * @model opposite="processingInstructions" required="true"
     * @generated
     */
    ProcessingInstructionHolder getParent();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getParent <em>Parent</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent</em>' container reference.
     * @see #getParent()
     * @generated
     */
    void setParent(ProcessingInstructionHolder value);

} // ProcessingInstruction
