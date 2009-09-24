/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.XmlBuildable;
import org.eclipse.xsd.XSDComponent;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xsd.XsdUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl#getBuildState <em>Build State</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl#isExcludeFromDocument <em>Exclude From Document</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl#getMinOccurs <em>Min Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl#getMaxOccurs <em>Max Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl#getXsdComponent <em>Xsd Component</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl#getNamespace <em>Namespace</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class XmlDocumentNodeImpl extends XmlDocumentEntityImpl implements XmlDocumentNode {
    /**
     * The default value of the '{@link #getBuildState() <em>Build State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBuildState()
     * @generated
     * @ordered
     */
    protected static final BuildStatus BUILD_STATE_EDEFAULT = BuildStatus.COMPLETE_LITERAL;

    /**
     * The cached value of the '{@link #getBuildState() <em>Build State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBuildState()
     * @generated
     * @ordered
     */
    protected BuildStatus buildState = BUILD_STATE_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #isExcludeFromDocument() <em>Exclude From Document</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isExcludeFromDocument()
     * @generated
     * @ordered
     */
    protected static final boolean EXCLUDE_FROM_DOCUMENT_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isExcludeFromDocument() <em>Exclude From Document</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isExcludeFromDocument()
     * @generated
     * @ordered
     */
    protected boolean excludeFromDocument = EXCLUDE_FROM_DOCUMENT_EDEFAULT;

    /**
     * The default value of the '{@link #getMinOccurs() <em>Min Occurs</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMinOccurs()
     * @generated
     * @ordered
     */
    protected static final int MIN_OCCURS_EDEFAULT = 1;

    /**
     * The default value of the '{@link #getMaxOccurs() <em>Max Occurs</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxOccurs()
     * @generated
     * @ordered
     */
    protected static final int MAX_OCCURS_EDEFAULT = 1;

    /**
     * The cached value of the '{@link #getXsdComponent() <em>Xsd Component</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getXsdComponent()
     * @generated
     * @ordered
     */
    protected XSDComponent xsdComponent = null;

    /**
     * The cached value of the '{@link #getNamespace() <em>Namespace</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNamespace()
     * @generated
     * @ordered
     */
    protected XmlNamespace namespace = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlDocumentNodeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlDocumentNode();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BuildStatus getBuildState() {
        return buildState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBuildState(BuildStatus newBuildState) {
        BuildStatus oldBuildState = buildState;
        buildState = newBuildState == null ? BUILD_STATE_EDEFAULT : newBuildState;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE, oldBuildState, buildState));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT_NODE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isExcludeFromDocument() {
        return excludeFromDocument;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setExcludeFromDocument(boolean newExcludeFromDocument) {
        boolean oldExcludeFromDocument = excludeFromDocument;
        excludeFromDocument = newExcludeFromDocument;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT, oldExcludeFromDocument, excludeFromDocument));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XSDComponent getXsdComponent() {
        if (xsdComponent != null && xsdComponent.eIsProxy()) {
            XSDComponent oldXsdComponent = xsdComponent;
            xsdComponent = (XSDComponent)eResolveProxy((InternalEObject)xsdComponent);
            if (xsdComponent != oldXsdComponent) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT, oldXsdComponent, xsdComponent));
            }
        }
        return xsdComponent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XSDComponent basicGetXsdComponent() {
        return xsdComponent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setXsdComponent(XSDComponent newXsdComponent) {
        XSDComponent oldXsdComponent = xsdComponent;
        xsdComponent = newXsdComponent;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT, oldXsdComponent, xsdComponent));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlNamespace getNamespace() {
        if (namespace != null && namespace.eIsProxy()) {
            XmlNamespace oldNamespace = namespace;
            namespace = (XmlNamespace)eResolveProxy((InternalEObject)namespace);
            if (namespace != oldNamespace) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE, oldNamespace, namespace));
            }
        }
        return namespace;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlNamespace basicGetNamespace() {
        return namespace;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNamespace(XmlNamespace newNamespace) {
        XmlNamespace oldNamespace = namespace;
        namespace = newNamespace;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE, oldNamespace, namespace));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     */
    public int getMinOccurs() {
        final XSDComponent schemaComp = this.getXsdComponent();
        if ( schemaComp != null ) {
            return XsdUtil.getMinOccurs(schemaComp);
        }
        return MIN_OCCURS_EDEFAULT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     */
    public int getMaxOccurs() {
        final XSDComponent schemaComp = this.getXsdComponent();
        if ( schemaComp != null ) {
            return XsdUtil.getMaxOccurs(schemaComp);
        }
        return MAX_OCCURS_EDEFAULT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAME:
                return getName();
            case XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE:
                if (resolve) return getNamespace();
                return basicGetNamespace();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE:
                setNamespace((XmlNamespace)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE:
                setNamespace((XmlNamespace)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE:
                return namespace != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
        if (baseClass == XmlBuildable.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE: return XmlDocumentPackage.XML_BUILDABLE__BUILD_STATE;
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
        if (baseClass == XmlBuildable.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.XML_BUILDABLE__BUILD_STATE: return XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (buildState: "); //$NON-NLS-1$
        result.append(buildState);
        result.append(", name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", excludeFromDocument: "); //$NON-NLS-1$
        result.append(excludeFromDocument);
        result.append(')');
        return result.toString();
    }

} //XmlDocumentNodeImpl
