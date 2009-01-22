/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.SampleFile;
import com.metamatrix.metamodels.webservice.SampleFromXsd;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServicePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sample Messages</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.SampleMessagesImpl#getMessage <em>Message</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.SampleMessagesImpl#getSampleFiles <em>Sample Files</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.SampleMessagesImpl#getSampleFromXsd <em>Sample From Xsd</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SampleMessagesImpl extends EObjectImpl implements SampleMessages {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getSampleFiles() <em>Sample Files</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSampleFiles()
     * @generated
     * @ordered
     */
	protected EList sampleFiles = null;

    /**
     * The cached value of the '{@link #getSampleFromXsd() <em>Sample From Xsd</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSampleFromXsd()
     * @generated
     * @ordered
     */
	protected SampleFromXsd sampleFromXsd = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SampleMessagesImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getSampleMessages();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Message getMessage() {
        if (eContainerFeatureID != WebServicePackage.SAMPLE_MESSAGES__MESSAGE) return null;
        return (Message)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setMessage(Message newMessage) {
        if (newMessage != eContainer || (eContainerFeatureID != WebServicePackage.SAMPLE_MESSAGES__MESSAGE && newMessage != null)) {
            if (EcoreUtil.isAncestor(this, newMessage))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMessage != null)
                msgs = ((InternalEObject)newMessage).eInverseAdd(this, WebServicePackage.MESSAGE__SAMPLES, Message.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMessage, WebServicePackage.SAMPLE_MESSAGES__MESSAGE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.SAMPLE_MESSAGES__MESSAGE, newMessage, newMessage));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getSampleFiles() {
        if (sampleFiles == null) {
            sampleFiles = new EObjectContainmentWithInverseEList(SampleFile.class, this, WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES, WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES);
        }
        return sampleFiles;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SampleFromXsd getSampleFromXsd() {
        return sampleFromXsd;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetSampleFromXsd(SampleFromXsd newSampleFromXsd, NotificationChain msgs) {
        SampleFromXsd oldSampleFromXsd = sampleFromXsd;
        sampleFromXsd = newSampleFromXsd;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD, oldSampleFromXsd, newSampleFromXsd);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSampleFromXsd(SampleFromXsd newSampleFromXsd) {
        if (newSampleFromXsd != sampleFromXsd) {
            NotificationChain msgs = null;
            if (sampleFromXsd != null)
                msgs = ((InternalEObject)sampleFromXsd).eInverseRemove(this, WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES, SampleFromXsd.class, msgs);
            if (newSampleFromXsd != null)
                msgs = ((InternalEObject)newSampleFromXsd).eInverseAdd(this, WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES, SampleFromXsd.class, msgs);
            msgs = basicSetSampleFromXsd(newSampleFromXsd, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD, newSampleFromXsd, newSampleFromXsd));
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
                case WebServicePackage.SAMPLE_MESSAGES__MESSAGE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WebServicePackage.SAMPLE_MESSAGES__MESSAGE, msgs);
                case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES:
                    return ((InternalEList)getSampleFiles()).basicAdd(otherEnd, msgs);
                case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD:
                    if (sampleFromXsd != null)
                        msgs = ((InternalEObject)sampleFromXsd).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD, null, msgs);
                    return basicSetSampleFromXsd((SampleFromXsd)otherEnd, msgs);
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
                case WebServicePackage.SAMPLE_MESSAGES__MESSAGE:
                    return eBasicSetContainer(null, WebServicePackage.SAMPLE_MESSAGES__MESSAGE, msgs);
                case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES:
                    return ((InternalEList)getSampleFiles()).basicRemove(otherEnd, msgs);
                case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD:
                    return basicSetSampleFromXsd(null, msgs);
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
                case WebServicePackage.SAMPLE_MESSAGES__MESSAGE:
                    return eContainer.eInverseRemove(this, WebServicePackage.MESSAGE__SAMPLES, Message.class, msgs);
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
            case WebServicePackage.SAMPLE_MESSAGES__MESSAGE:
                return getMessage();
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES:
                return getSampleFiles();
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD:
                return getSampleFromXsd();
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
            case WebServicePackage.SAMPLE_MESSAGES__MESSAGE:
                setMessage((Message)newValue);
                return;
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES:
                getSampleFiles().clear();
                getSampleFiles().addAll((Collection)newValue);
                return;
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD:
                setSampleFromXsd((SampleFromXsd)newValue);
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
            case WebServicePackage.SAMPLE_MESSAGES__MESSAGE:
                setMessage((Message)null);
                return;
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES:
                getSampleFiles().clear();
                return;
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD:
                setSampleFromXsd((SampleFromXsd)null);
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
            case WebServicePackage.SAMPLE_MESSAGES__MESSAGE:
                return getMessage() != null;
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES:
                return sampleFiles != null && !sampleFiles.isEmpty();
            case WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD:
                return sampleFromXsd != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //SampleMessagesImpl
