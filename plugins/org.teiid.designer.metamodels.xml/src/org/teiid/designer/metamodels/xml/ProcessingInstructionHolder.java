/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Processing Instruction Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.xml.ProcessingInstructionHolder#getProcessingInstructions <em>Processing Instructions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getProcessingInstructionHolder()
 * @model interface="true" abstract="true"
 * @generated
 *
 * @since 8.0
 */
public interface ProcessingInstructionHolder extends EObject{
    /**
     * Returns the value of the '<em><b>Processing Instructions</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.xml.ProcessingInstruction}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.xml.ProcessingInstruction#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Processing Instructions</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Processing Instructions</em>' containment reference list.
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getProcessingInstructionHolder_ProcessingInstructions()
     * @see org.teiid.designer.metamodels.xml.ProcessingInstruction#getParent
     * @model type="org.teiid.designer.metamodels.xml.ProcessingInstruction" opposite="parent" containment="true"
     * @generated
     */
    EList getProcessingInstructions();

} // ProcessingInstructionHolder
