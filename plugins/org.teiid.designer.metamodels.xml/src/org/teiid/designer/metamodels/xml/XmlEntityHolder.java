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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Xml Entity Holder</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.xml.XmlEntityHolder#getEntities <em>Entities</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlEntityHolder()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface XmlEntityHolder extends EObject {
    /**
     * Returns the value of the '<em><b>Entities</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.xml.XmlHolderEntity}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.xml.XmlHolderEntity#getParent <em>Parent</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Entities</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Entities</em>' containment reference list.
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlEntityHolder_Entities()
     * @see org.teiid.designer.metamodels.xml.XmlHolderEntity#getParent
     * @model type="org.teiid.designer.metamodels.xml.XmlHolderEntity" opposite="parent" containment="true"
     * @generated
     */
    EList getEntities();
}
