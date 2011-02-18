/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.xml.SoapEncoding;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlRoot;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XmlDocument</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentImpl#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentImpl#isFormatted <em>Formatted</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentImpl#isStandalone <em>Standalone</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlDocumentImpl#getSoapEncoding <em>Soap Encoding</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlDocumentImpl extends XmlFragmentImpl implements XmlDocument {
    /**
     * The default value of the '{@link #getEncoding() <em>Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEncoding()
     * @generated
     * @ordered
     */
    protected static final String ENCODING_EDEFAULT = "UTF-8"; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getEncoding() <em>Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEncoding()
     * @generated
     * @ordered
     */
    protected String encoding = ENCODING_EDEFAULT;

    /**
     * The default value of the '{@link #isFormatted() <em>Formatted</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFormatted()
     * @generated
     * @ordered
     */
    protected static final boolean FORMATTED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isFormatted() <em>Formatted</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFormatted()
     * @generated
     * @ordered
     */
    protected boolean formatted = FORMATTED_EDEFAULT;

    /**
     * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected static final String VERSION_EDEFAULT = "1.0"; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected String version = VERSION_EDEFAULT;

    /**
     * The default value of the '{@link #isStandalone() <em>Standalone</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isStandalone()
     * @generated
     * @ordered
     */
    protected static final boolean STANDALONE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isStandalone() <em>Standalone</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isStandalone()
     * @generated
     * @ordered
     */
    protected boolean standalone = STANDALONE_EDEFAULT;

    /**
     * The default value of the '{@link #getSoapEncoding() <em>Soap Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSoapEncoding()
     * @generated
     * @ordered
     */
    protected static final SoapEncoding SOAP_ENCODING_EDEFAULT = SoapEncoding.NONE_LITERAL;

    /**
     * The cached value of the '{@link #getSoapEncoding() <em>Soap Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSoapEncoding()
     * @generated
     * @ordered
     */
    protected SoapEncoding soapEncoding = SOAP_ENCODING_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlDocumentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlDocument();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEncoding(String newEncoding) {
        String oldEncoding = encoding;
        encoding = newEncoding;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT__ENCODING, oldEncoding, encoding));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isFormatted() {
        return formatted;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFormatted(boolean newFormatted) {
        boolean oldFormatted = formatted;
        formatted = newFormatted;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT__FORMATTED, oldFormatted, formatted));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getVersion() {
        return version;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVersion(String newVersion) {
        String oldVersion = version;
        version = newVersion;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT__VERSION, oldVersion, version));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isStandalone() {
        return standalone;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStandalone(boolean newStandalone) {
        boolean oldStandalone = standalone;
        standalone = newStandalone;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT__STANDALONE, oldStandalone, standalone));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SoapEncoding getSoapEncoding() {
        return soapEncoding;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSoapEncoding(SoapEncoding newSoapEncoding) {
        SoapEncoding oldSoapEncoding = soapEncoding;
        soapEncoding = newSoapEncoding == null ? SOAP_ENCODING_EDEFAULT : newSoapEncoding;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_DOCUMENT__SOAP_ENCODING, oldSoapEncoding, soapEncoding));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case XmlDocumentPackage.XML_DOCUMENT__COMMENTS:
                    return ((InternalEList)getComments()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_DOCUMENT__PROCESSING_INSTRUCTIONS:
                    return ((InternalEList)getProcessingInstructions()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_DOCUMENT__ROOT:
                    if (root != null)
                        msgs = ((InternalEObject)root).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - XmlDocumentPackage.XML_DOCUMENT__ROOT, null, msgs);
                    return basicSetRoot((XmlRoot)otherEnd, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case XmlDocumentPackage.XML_DOCUMENT__COMMENTS:
                    return ((InternalEList)getComments()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_DOCUMENT__PROCESSING_INSTRUCTIONS:
                    return ((InternalEList)getProcessingInstructions()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_DOCUMENT__ROOT:
                    return basicSetRoot(null, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_DOCUMENT__COMMENTS:
                return getComments();
            case XmlDocumentPackage.XML_DOCUMENT__PROCESSING_INSTRUCTIONS:
                return getProcessingInstructions();
            case XmlDocumentPackage.XML_DOCUMENT__NAME:
                return getName();
            case XmlDocumentPackage.XML_DOCUMENT__ROOT:
                return getRoot();
            case XmlDocumentPackage.XML_DOCUMENT__ENCODING:
                return getEncoding();
            case XmlDocumentPackage.XML_DOCUMENT__FORMATTED:
                return isFormatted() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_DOCUMENT__VERSION:
                return getVersion();
            case XmlDocumentPackage.XML_DOCUMENT__STANDALONE:
                return isStandalone() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_DOCUMENT__SOAP_ENCODING:
                return getSoapEncoding();
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
            case XmlDocumentPackage.XML_DOCUMENT__COMMENTS:
                getComments().clear();
                getComments().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                getProcessingInstructions().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__ROOT:
                setRoot((XmlRoot)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__ENCODING:
                setEncoding((String)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__FORMATTED:
                setFormatted(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_DOCUMENT__VERSION:
                setVersion((String)newValue);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__STANDALONE:
                setStandalone(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_DOCUMENT__SOAP_ENCODING:
                setSoapEncoding((SoapEncoding)newValue);
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
            case XmlDocumentPackage.XML_DOCUMENT__COMMENTS:
                getComments().clear();
                return;
            case XmlDocumentPackage.XML_DOCUMENT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                return;
            case XmlDocumentPackage.XML_DOCUMENT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__ROOT:
                setRoot((XmlRoot)null);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__ENCODING:
                setEncoding(ENCODING_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__FORMATTED:
                setFormatted(FORMATTED_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__VERSION:
                setVersion(VERSION_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__STANDALONE:
                setStandalone(STANDALONE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_DOCUMENT__SOAP_ENCODING:
                setSoapEncoding(SOAP_ENCODING_EDEFAULT);
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
            case XmlDocumentPackage.XML_DOCUMENT__COMMENTS:
                return comments != null && !comments.isEmpty();
            case XmlDocumentPackage.XML_DOCUMENT__PROCESSING_INSTRUCTIONS:
                return processingInstructions != null && !processingInstructions.isEmpty();
            case XmlDocumentPackage.XML_DOCUMENT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_DOCUMENT__ROOT:
                return root != null;
            case XmlDocumentPackage.XML_DOCUMENT__ENCODING:
                return ENCODING_EDEFAULT == null ? encoding != null : !ENCODING_EDEFAULT.equals(encoding);
            case XmlDocumentPackage.XML_DOCUMENT__FORMATTED:
                return formatted != FORMATTED_EDEFAULT;
            case XmlDocumentPackage.XML_DOCUMENT__VERSION:
                return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
            case XmlDocumentPackage.XML_DOCUMENT__STANDALONE:
                return standalone != STANDALONE_EDEFAULT;
            case XmlDocumentPackage.XML_DOCUMENT__SOAP_ENCODING:
                return soapEncoding != SOAP_ENCODING_EDEFAULT;
        }
        return eDynamicIsSet(eFeature);
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
        result.append(" (encoding: "); //$NON-NLS-1$
        result.append(encoding);
        result.append(", formatted: "); //$NON-NLS-1$
        result.append(formatted);
        result.append(", version: "); //$NON-NLS-1$
        result.append(version);
        result.append(", standalone: "); //$NON-NLS-1$
        result.append(standalone);
        result.append(", soapEncoding: "); //$NON-NLS-1$
        result.append(soapEncoding);
        result.append(')');
        return result.toString();
    }

} //XmlDocumentImpl
