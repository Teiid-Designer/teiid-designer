/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Uri Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#getUri <em>Uri</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#isResolvable <em>Resolvable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#getAbstract <em>Abstract</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#getKeywords <em>Keywords</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#getRelatedUris <em>Related Uris</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.UriReference#getProperties <em>Properties</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference()
 * @model
 * @generated
 */
public interface UriReference extends PlaceholderReference{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The name or displayable form of the URI reference.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.UriReference#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The actual URI
     * <!-- end-model-doc -->
     * @return the value of the '<em>Uri</em>' attribute.
     * @see #setUri(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_Uri()
     * @model
     * @generated
     */
    String getUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.UriReference#getUri <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Uri</em>' attribute.
     * @see #getUri()
     * @generated
     */
    void setUri(String value);

    /**
     * Returns the value of the '<em><b>Resolvable</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Resolvable</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Flag denoting whether the URI should be resolvable (that is, it is a URL).  Some URIs are used merely as identifiers and are not intended to be resolvable.  Some URLs are not resolvable to all users (e.g., because of a firewall).
     * <!-- end-model-doc -->
     * @return the value of the '<em>Resolvable</em>' attribute.
     * @see #setResolvable(boolean)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_Resolvable()
     * @model default="true"
     * @generated
     */
    boolean isResolvable();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.UriReference#isResolvable <em>Resolvable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Resolvable</em>' attribute.
     * @see #isResolvable()
     * @generated
     */
    void setResolvable(boolean value);

    /**
     * Returns the value of the '<em><b>Encoding</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Encoding</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The (expected) encoding of the content found at this URI, if applicable.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Encoding</em>' attribute.
     * @see #setEncoding(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_Encoding()
     * @model
     * @generated
     */
    String getEncoding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.UriReference#getEncoding <em>Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Encoding</em>' attribute.
     * @see #getEncoding()
     * @generated
     */
    void setEncoding(String value);

    /**
     * Returns the value of the '<em><b>Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Abstract</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A comment or description about the URI or its content.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Abstract</em>' attribute.
     * @see #setAbstract(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_Abstract()
     * @model
     * @generated
     */
    String getAbstract();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.UriReference#getAbstract <em>Abstract</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Abstract</em>' attribute.
     * @see #getAbstract()
     * @generated
     */
    void setAbstract(String value);

    /**
     * Returns the value of the '<em><b>Keywords</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Keywords</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A list of keywords, separated by whitespace.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Keywords</em>' attribute.
     * @see #setKeywords(String)
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_Keywords()
     * @model
     * @generated
     */
    String getKeywords();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relationship.UriReference#getKeywords <em>Keywords</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Keywords</em>' attribute.
     * @see #getKeywords()
     * @generated
     */
    void setKeywords(String value);

    /**
     * Returns the value of the '<em><b>Related Uris</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Related Uris</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Other URIs that are related to this URI.  These may be alternative forms of the URI, variations of the URI, or simply other URIs that are related.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Related Uris</em>' attribute list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_RelatedUris()
     * @model type="java.lang.String"
     * @generated
     */
    EList getRelatedUris();

    /**
     * Returns the value of the '<em><b>Properties</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Properties</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Name-value pairs that are associated with this URI.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Properties</em>' map.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getUriReference_Properties()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String"
     * @generated
     */
    EMap getProperties();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getDisplayableName();

} // UriReference
