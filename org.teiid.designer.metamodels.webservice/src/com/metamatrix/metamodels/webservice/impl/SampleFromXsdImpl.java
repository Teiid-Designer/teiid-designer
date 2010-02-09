/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.SampleFromXsd;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.xml.XmlDocumentBuilder;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Sample From Xsd</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.SampleFromXsdImpl#getMaxNumberOfLevelsToBuild <em>Max Number Of Levels To
 * Build</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.SampleFromXsdImpl#getSampleFragment <em>Sample Fragment</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.SampleFromXsdImpl#getSampleMessages <em>Sample Messages</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class SampleFromXsdImpl extends EObjectImpl implements SampleFromXsd {

    /**
     * The default value of the '{@link #getMaxNumberOfLevelsToBuild() <em>Max Number Of Levels To Build</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMaxNumberOfLevelsToBuild()
     * @generated
     * @ordered
     */
    protected static final int MAX_NUMBER_OF_LEVELS_TO_BUILD_EDEFAULT = 30;

    /**
     * The cached value of the '{@link #getMaxNumberOfLevelsToBuild() <em>Max Number Of Levels To Build</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMaxNumberOfLevelsToBuild()
     * @generated
     * @ordered
     */
    protected int maxNumberOfLevelsToBuild = MAX_NUMBER_OF_LEVELS_TO_BUILD_EDEFAULT;

    /**
     * The cached value of the '{@link #getSampleFragment() <em>Sample Fragment</em>}' containment reference. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getSampleFragment()
     * @generated
     * @ordered
     */
    protected XmlElement sampleFragment = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected SampleFromXsdImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getSampleFromXsd();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public int getMaxNumberOfLevelsToBuild() {
        return maxNumberOfLevelsToBuild;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void setMaxNumberOfLevelsToBuild( int newMaxNumberOfLevelsToBuild ) {
        int oldMaxNumberOfLevelsToBuild = maxNumberOfLevelsToBuild;
        if (oldMaxNumberOfLevelsToBuild != newMaxNumberOfLevelsToBuild) {
            maxNumberOfLevelsToBuild = newMaxNumberOfLevelsToBuild;
            if (eNotificationRequired()) {
                eNotify(new ENotificationImpl(this, Notification.SET,
                                              WebServicePackage.SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD,
                                              oldMaxNumberOfLevelsToBuild, maxNumberOfLevelsToBuild));
            }
            // Clear out the generated sample ...
            setSampleFragment(null);
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMaxNumberOfLevelsToBuildGen( int newMaxNumberOfLevelsToBuild ) {
        int oldMaxNumberOfLevelsToBuild = maxNumberOfLevelsToBuild;
        maxNumberOfLevelsToBuild = newMaxNumberOfLevelsToBuild;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(
                                                                   this,
                                                                   Notification.SET,
                                                                   WebServicePackage.SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD,
                                                                   oldMaxNumberOfLevelsToBuild, maxNumberOfLevelsToBuild));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public XmlElement getSampleFragment() {
        if (sampleFragment == null) {
            this.setSampleFragment(generateSampleFragment());
        }
        return sampleFragment;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlElement getSampleFragmentGen() {
        return sampleFragment;
    }

    protected XmlElement generateSampleFragment() {
        // Get the schema component for the owner of the SampleMessages ...
        final SampleMessages sampleMessages = this.getSampleMessages();
        if (sampleMessages != null) {
            final Message message = sampleMessages.getMessage();
            if (message != null) {
                // final XSDSimpleTypeDefinition sTypeDefn = message.getContentSimpleType();
                // if ( sTypeDefn != null ) {
                // return generateSampleFragment(sTypeDefn);
                // }
                final XSDComplexTypeDefinition cTypeDefn = message.getContentComplexType();
                if (cTypeDefn != null) {
                    return generateSampleFragment(cTypeDefn);
                }
                final XSDElementDeclaration element = message.getContentElement();
                if (element != null) {
                    return generateSampleFragment(element);
                }
            }
        }
        return null;
    }

    protected XmlElement generateSampleFragment( final XSDNamedComponent schemaComponent ) {
        final XmlDocumentBuilder builder = XmlDocumentPlugin.createDocumentBuilder();
        final int levelsToBuild = getMaxNumberOfLevelsToBuild();
        if (levelsToBuild < 1) {
            return null;
        }
        builder.setNumberOfLevelsToBuild(levelsToBuild);

        // Create the root ...
        final XmlDocumentFactory factory = XmlDocumentFactory.eINSTANCE;
        final XmlRoot docRoot = factory.createXmlRoot();
        docRoot.setName(schemaComponent.getName());
        docRoot.setXsdComponent(schemaComponent);

        try {
            builder.buildDocument(docRoot, null);
        } catch (ModelerCoreException err) {
            // Do nothing ...
        }

        return docRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetSampleFragment( XmlElement newSampleFragment,
                                                     NotificationChain msgs ) {
        XmlElement oldSampleFragment = sampleFragment;
        sampleFragment = newSampleFragment;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                                                                   WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT,
                                                                   oldSampleFragment, newSampleFragment);
            if (msgs == null) msgs = notification;
            else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSampleFragment( XmlElement newSampleFragment ) {
        if (newSampleFragment != sampleFragment) {
            NotificationChain msgs = null;
            if (sampleFragment != null) msgs = ((InternalEObject)sampleFragment).eInverseRemove(this,
                                                                                                EOPPOSITE_FEATURE_BASE
                                                                                                - WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT,
                                                                                                null,
                                                                                                msgs);
            if (newSampleFragment != null) msgs = ((InternalEObject)newSampleFragment).eInverseAdd(this,
                                                                                                   EOPPOSITE_FEATURE_BASE
                                                                                                   - WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT,
                                                                                                   null,
                                                                                                   msgs);
            msgs = basicSetSampleFragment(newSampleFragment, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT,
                                                                          newSampleFragment, newSampleFragment));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SampleMessages getSampleMessages() {
        if (eContainerFeatureID != WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES) return null;
        return (SampleMessages)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSampleMessages( SampleMessages newSampleMessages ) {
        if (newSampleMessages != eContainer
            || (eContainerFeatureID != WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES && newSampleMessages != null)) {
            if (EcoreUtil.isAncestor(this, newSampleMessages)) throw new IllegalArgumentException(
                                                                                                  "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newSampleMessages != null) msgs = ((InternalEObject)newSampleMessages).eInverseAdd(this,
                                                                                                   WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD,
                                                                                                   SampleMessages.class,
                                                                                                   msgs);
            msgs = eBasicSetContainer((InternalEObject)newSampleMessages,
                                      WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES,
                                      msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES,
                                                                          newSampleMessages, newSampleMessages));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( InternalEObject otherEnd,
                                          int featureID,
                                          Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( InternalEObject otherEnd,
                                             int featureID,
                                             Class baseClass,
                                             NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT:
                    return basicSetSampleFragment(null, msgs);
                case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES:
                    return eBasicSetContainer(null, WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES:
                    return eContainer.eInverseRemove(this,
                                                     WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FROM_XSD,
                                                     SampleMessages.class,
                                                     msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet( EStructuralFeature eFeature,
                        boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WebServicePackage.SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD:
                return new Integer(getMaxNumberOfLevelsToBuild());
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT:
                return getSampleFragment();
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES:
                return getSampleMessages();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet( EStructuralFeature eFeature,
                      Object newValue ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WebServicePackage.SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD:
                setMaxNumberOfLevelsToBuild(((Integer)newValue).intValue());
                return;
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT:
                setSampleFragment((XmlElement)newValue);
                return;
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES:
                setSampleMessages((SampleMessages)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WebServicePackage.SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD:
                setMaxNumberOfLevelsToBuild(MAX_NUMBER_OF_LEVELS_TO_BUILD_EDEFAULT);
                return;
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT:
                setSampleFragment((XmlElement)null);
                return;
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES:
                setSampleMessages((SampleMessages)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WebServicePackage.SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD:
                return maxNumberOfLevelsToBuild != MAX_NUMBER_OF_LEVELS_TO_BUILD_EDEFAULT;
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_FRAGMENT:
                return sampleFragment != null;
            case WebServicePackage.SAMPLE_FROM_XSD__SAMPLE_MESSAGES:
                return getSampleMessages() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (maxNumberOfLevelsToBuild: "); //$NON-NLS-1$
        result.append(maxNumberOfLevelsToBuild);
        result.append(')');
        return result.toString();
    }

} // SampleFromXsdImpl
