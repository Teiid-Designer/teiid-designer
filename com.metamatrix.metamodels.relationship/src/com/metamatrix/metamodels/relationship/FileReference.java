/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.FileReference#getToolName <em>Tool Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.FileReference#getToolVersion <em>Tool Version</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.FileReference#getFormatName <em>Format Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.FileReference#getFormatVersion <em>Format Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getFileReference()
 * @model
 * @generated
 */
public interface FileReference extends UriReference{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Tool Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tool Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The name of the tool that produced the file content.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Tool Name</em>' attribute.
     * @see #setToolName(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getFileReference_ToolName()
     * @model
     * @generated
     */
    String getToolName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.FileReference#getToolName <em>Tool Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Tool Name</em>' attribute.
     * @see #getToolName()
     * @generated
     */
    void setToolName(String value);

    /**
     * Returns the value of the '<em><b>Tool Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tool Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The version of the tool that produced the file content.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Tool Version</em>' attribute.
     * @see #setToolVersion(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getFileReference_ToolVersion()
     * @model
     * @generated
     */
    String getToolVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.FileReference#getToolVersion <em>Tool Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Tool Version</em>' attribute.
     * @see #getToolVersion()
     * @generated
     */
    void setToolVersion(String value);

    /**
     * Returns the value of the '<em><b>Format Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Format Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The name of the format of the content.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Format Name</em>' attribute.
     * @see #setFormatName(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getFileReference_FormatName()
     * @model
     * @generated
     */
    String getFormatName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.FileReference#getFormatName <em>Format Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Format Name</em>' attribute.
     * @see #getFormatName()
     * @generated
     */
    void setFormatName(String value);

    /**
     * Returns the value of the '<em><b>Format Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Format Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The version of the format of the content.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Format Version</em>' attribute.
     * @see #setFormatVersion(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getFileReference_FormatVersion()
     * @model
     * @generated
     */
    String getFormatVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.FileReference#getFormatVersion <em>Format Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Format Version</em>' attribute.
     * @see #getFormatVersion()
     * @generated
     */
    void setFormatVersion(String value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getDisplayableName();

} // FileReference
