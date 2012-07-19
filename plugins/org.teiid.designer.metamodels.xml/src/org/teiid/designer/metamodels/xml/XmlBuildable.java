/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Buildable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.xml.XmlBuildable#getBuildState <em>Build State</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlBuildable()
 * @model interface="true" abstract="true"
 * @generated
 *
 * @since 8.0
 */
public interface XmlBuildable extends EObject {
    /**
     * Returns the value of the '<em><b>Build State</b></em>' attribute.
     * The default value is <code>"COMPLETE"</code>.
     * The literals are from the enumeration {@link org.teiid.designer.metamodels.xml.BuildStatus}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Build State</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Build State</em>' attribute.
     * @see org.teiid.designer.metamodels.xml.BuildStatus
     * @see #setBuildState(BuildStatus)
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlBuildable_BuildState()
     * @model default="COMPLETE"
     * @generated
     */
    BuildStatus getBuildState();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.xml.XmlBuildable#getBuildState <em>Build State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Build State</em>' attribute.
     * @see org.teiid.designer.metamodels.xml.BuildStatus
     * @see #getBuildState()
     * @generated
     */
    void setBuildState(BuildStatus value);

} // XmlBuildable
