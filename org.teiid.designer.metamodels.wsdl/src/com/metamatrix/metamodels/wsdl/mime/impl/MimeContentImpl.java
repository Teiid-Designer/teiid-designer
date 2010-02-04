/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.wsdl.MessagePart;
import com.metamatrix.metamodels.wsdl.mime.MimeContent;
import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeContentImpl#getMimeElementOwner <em>Mime Element Owner</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeContentImpl#getMessagePart <em>Message Part</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeContentImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeContentImpl#isXml <em>Xml</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MimeContentImpl extends EObjectImpl implements MimeContent {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getMessagePart() <em>Message Part</em>}' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getMessagePart()
     * @generated
     * @ordered
     */
	protected MessagePart messagePart = null;

    /**
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
	protected static final String TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
	protected String type = TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #isXml() <em>Xml</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isXml()
     * @generated
     * @ordered
     */
	protected static final boolean XML_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isXml() <em>Xml</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isXml()
     * @generated
     * @ordered
     */
	protected boolean xml = XML_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected MimeContentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return MimePackage.eINSTANCE.getMimeContent();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public MimeElementOwner getMimeElementOwner() {
        if (eContainerFeatureID != MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER) return null;
        return (MimeElementOwner)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setMimeElementOwner(MimeElementOwner newMimeElementOwner) {
        if (newMimeElementOwner != eContainer || (eContainerFeatureID != MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER && newMimeElementOwner != null)) {
            if (EcoreUtil.isAncestor(this, newMimeElementOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMimeElementOwner != null)
                msgs = ((InternalEObject)newMimeElementOwner).eInverseAdd(this, MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS, MimeElementOwner.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMimeElementOwner, MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER, newMimeElementOwner, newMimeElementOwner));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setType(String newType) {
        String oldType = type;
        type = newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MimePackage.MIME_CONTENT__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public boolean isXml() {
        return xml;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setXml(boolean newXml) {
        boolean oldXml = xml;
        xml = newXml;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MimePackage.MIME_CONTENT__XML, oldXml, xml));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public MessagePart getMessagePart() {
        if (messagePart != null && messagePart.eIsProxy()) {
            MessagePart oldMessagePart = messagePart;
            messagePart = (MessagePart)eResolveProxy((InternalEObject)messagePart);
            if (messagePart != oldMessagePart) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, MimePackage.MIME_CONTENT__MESSAGE_PART, oldMessagePart, messagePart));
            }
        }
        return messagePart;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public MessagePart basicGetMessagePart() {
        return messagePart;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setMessagePart(MessagePart newMessagePart) {
        MessagePart oldMessagePart = messagePart;
        messagePart = newMessagePart;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MimePackage.MIME_CONTENT__MESSAGE_PART, oldMessagePart, messagePart));
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
                case MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER, msgs);
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
                case MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER:
                    return eBasicSetContainer(null, MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER, msgs);
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
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER:
                    return eContainer.eInverseRemove(this, MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS, MimeElementOwner.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER:
                return getMimeElementOwner();
            case MimePackage.MIME_CONTENT__MESSAGE_PART:
                if (resolve) return getMessagePart();
                return basicGetMessagePart();
            case MimePackage.MIME_CONTENT__TYPE:
                return getType();
            case MimePackage.MIME_CONTENT__XML:
                return isXml() ? Boolean.TRUE : Boolean.FALSE;
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
            case MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER:
                setMimeElementOwner((MimeElementOwner)newValue);
                return;
            case MimePackage.MIME_CONTENT__MESSAGE_PART:
                setMessagePart((MessagePart)newValue);
                return;
            case MimePackage.MIME_CONTENT__TYPE:
                setType((String)newValue);
                return;
            case MimePackage.MIME_CONTENT__XML:
                setXml(((Boolean)newValue).booleanValue());
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
            case MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER:
                setMimeElementOwner((MimeElementOwner)null);
                return;
            case MimePackage.MIME_CONTENT__MESSAGE_PART:
                setMessagePart((MessagePart)null);
                return;
            case MimePackage.MIME_CONTENT__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case MimePackage.MIME_CONTENT__XML:
                setXml(XML_EDEFAULT);
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
            case MimePackage.MIME_CONTENT__MIME_ELEMENT_OWNER:
                return getMimeElementOwner() != null;
            case MimePackage.MIME_CONTENT__MESSAGE_PART:
                return messagePart != null;
            case MimePackage.MIME_CONTENT__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case MimePackage.MIME_CONTENT__XML:
                return xml != XML_EDEFAULT;
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
        result.append(" (type: "); //$NON-NLS-1$
        result.append(type);
        result.append(", xml: "); //$NON-NLS-1$
        result.append(xml);
        result.append(')');
        return result.toString();
    }

} //MimeContentImpl
