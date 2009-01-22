/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relationship.FileReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.RelationshipPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>File Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.FileReferenceImpl#getToolName <em>Tool Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.FileReferenceImpl#getToolVersion <em>Tool Version</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.FileReferenceImpl#getFormatName <em>Format Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.FileReferenceImpl#getFormatVersion <em>Format Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FileReferenceImpl extends UriReferenceImpl implements FileReference {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getToolName() <em>Tool Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getToolName()
     * @generated
     * @ordered
     */
    protected static final String TOOL_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getToolName() <em>Tool Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getToolName()
     * @generated
     * @ordered
     */
    protected String toolName = TOOL_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getToolVersion() <em>Tool Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getToolVersion()
     * @generated
     * @ordered
     */
    protected static final String TOOL_VERSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getToolVersion() <em>Tool Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getToolVersion()
     * @generated
     * @ordered
     */
    protected String toolVersion = TOOL_VERSION_EDEFAULT;

    /**
     * The default value of the '{@link #getFormatName() <em>Format Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFormatName()
     * @generated
     * @ordered
     */
    protected static final String FORMAT_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFormatName() <em>Format Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFormatName()
     * @generated
     * @ordered
     */
    protected String formatName = FORMAT_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getFormatVersion() <em>Format Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFormatVersion()
     * @generated
     * @ordered
     */
    protected static final String FORMAT_VERSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFormatVersion() <em>Format Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFormatVersion()
     * @generated
     * @ordered
     */
    protected String formatVersion = FORMAT_VERSION_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected FileReferenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getFileReference();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setToolName(String newToolName) {
        String oldToolName = toolName;
        toolName = newToolName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.FILE_REFERENCE__TOOL_NAME, oldToolName, toolName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getToolVersion() {
        return toolVersion;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setToolVersion(String newToolVersion) {
        String oldToolVersion = toolVersion;
        toolVersion = newToolVersion;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.FILE_REFERENCE__TOOL_VERSION, oldToolVersion, toolVersion));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFormatName(String newFormatName) {
        String oldFormatName = formatName;
        formatName = newFormatName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.FILE_REFERENCE__FORMAT_NAME, oldFormatName, formatName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFormatVersion() {
        return formatVersion;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFormatVersion(String newFormatVersion) {
        String oldFormatVersion = formatVersion;
        formatVersion = newFormatVersion;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.FILE_REFERENCE__FORMAT_VERSION, oldFormatVersion, formatVersion));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getDisplayableName() {
        return super.getDisplayableName();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getDisplayableNameGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
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
                case RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, msgs);
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
                case RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    return eBasicSetContainer(null, RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, msgs);
                case RelationshipPackage.FILE_REFERENCE__PROPERTIES:
                    return ((InternalEList)getProperties()).basicRemove(otherEnd, msgs);
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
                case RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    return eContainer.eInverseRemove(this, RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS, PlaceholderReferenceContainer.class, msgs);
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
            case RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                return getPlaceholderReferenceContainer();
            case RelationshipPackage.FILE_REFERENCE__NAME:
                return getName();
            case RelationshipPackage.FILE_REFERENCE__URI:
                return getUri();
            case RelationshipPackage.FILE_REFERENCE__RESOLVABLE:
                return isResolvable() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.FILE_REFERENCE__ENCODING:
                return getEncoding();
            case RelationshipPackage.FILE_REFERENCE__ABSTRACT:
                return getAbstract();
            case RelationshipPackage.FILE_REFERENCE__KEYWORDS:
                return getKeywords();
            case RelationshipPackage.FILE_REFERENCE__RELATED_URIS:
                return getRelatedUris();
            case RelationshipPackage.FILE_REFERENCE__PROPERTIES:
                return getProperties();
            case RelationshipPackage.FILE_REFERENCE__TOOL_NAME:
                return getToolName();
            case RelationshipPackage.FILE_REFERENCE__TOOL_VERSION:
                return getToolVersion();
            case RelationshipPackage.FILE_REFERENCE__FORMAT_NAME:
                return getFormatName();
            case RelationshipPackage.FILE_REFERENCE__FORMAT_VERSION:
                return getFormatVersion();
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
            case RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                setPlaceholderReferenceContainer((PlaceholderReferenceContainer)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__NAME:
                setName((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__URI:
                setUri((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__RESOLVABLE:
                setResolvable(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.FILE_REFERENCE__ENCODING:
                setEncoding((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__ABSTRACT:
                setAbstract((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__KEYWORDS:
                setKeywords((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__RELATED_URIS:
                getRelatedUris().clear();
                getRelatedUris().addAll((Collection)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__PROPERTIES:
                getProperties().clear();
                getProperties().addAll((Collection)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__TOOL_NAME:
                setToolName((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__TOOL_VERSION:
                setToolVersion((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__FORMAT_NAME:
                setFormatName((String)newValue);
                return;
            case RelationshipPackage.FILE_REFERENCE__FORMAT_VERSION:
                setFormatVersion((String)newValue);
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
            case RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                setPlaceholderReferenceContainer((PlaceholderReferenceContainer)null);
                return;
            case RelationshipPackage.FILE_REFERENCE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__URI:
                setUri(URI_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__RESOLVABLE:
                setResolvable(RESOLVABLE_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__ENCODING:
                setEncoding(ENCODING_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__ABSTRACT:
                setAbstract(ABSTRACT_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__KEYWORDS:
                setKeywords(KEYWORDS_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__RELATED_URIS:
                getRelatedUris().clear();
                return;
            case RelationshipPackage.FILE_REFERENCE__PROPERTIES:
                getProperties().clear();
                return;
            case RelationshipPackage.FILE_REFERENCE__TOOL_NAME:
                setToolName(TOOL_NAME_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__TOOL_VERSION:
                setToolVersion(TOOL_VERSION_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__FORMAT_NAME:
                setFormatName(FORMAT_NAME_EDEFAULT);
                return;
            case RelationshipPackage.FILE_REFERENCE__FORMAT_VERSION:
                setFormatVersion(FORMAT_VERSION_EDEFAULT);
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
            case RelationshipPackage.FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                return getPlaceholderReferenceContainer() != null;
            case RelationshipPackage.FILE_REFERENCE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationshipPackage.FILE_REFERENCE__URI:
                return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
            case RelationshipPackage.FILE_REFERENCE__RESOLVABLE:
                return resolvable != RESOLVABLE_EDEFAULT;
            case RelationshipPackage.FILE_REFERENCE__ENCODING:
                return ENCODING_EDEFAULT == null ? encoding != null : !ENCODING_EDEFAULT.equals(encoding);
            case RelationshipPackage.FILE_REFERENCE__ABSTRACT:
                return ABSTRACT_EDEFAULT == null ? abstract_ != null : !ABSTRACT_EDEFAULT.equals(abstract_);
            case RelationshipPackage.FILE_REFERENCE__KEYWORDS:
                return KEYWORDS_EDEFAULT == null ? keywords != null : !KEYWORDS_EDEFAULT.equals(keywords);
            case RelationshipPackage.FILE_REFERENCE__RELATED_URIS:
                return relatedUris != null && !relatedUris.isEmpty();
            case RelationshipPackage.FILE_REFERENCE__PROPERTIES:
                return properties != null && !properties.isEmpty();
            case RelationshipPackage.FILE_REFERENCE__TOOL_NAME:
                return TOOL_NAME_EDEFAULT == null ? toolName != null : !TOOL_NAME_EDEFAULT.equals(toolName);
            case RelationshipPackage.FILE_REFERENCE__TOOL_VERSION:
                return TOOL_VERSION_EDEFAULT == null ? toolVersion != null : !TOOL_VERSION_EDEFAULT.equals(toolVersion);
            case RelationshipPackage.FILE_REFERENCE__FORMAT_NAME:
                return FORMAT_NAME_EDEFAULT == null ? formatName != null : !FORMAT_NAME_EDEFAULT.equals(formatName);
            case RelationshipPackage.FILE_REFERENCE__FORMAT_VERSION:
                return FORMAT_VERSION_EDEFAULT == null ? formatVersion != null : !FORMAT_VERSION_EDEFAULT.equals(formatVersion);
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
        result.append(" (toolName: "); //$NON-NLS-1$
        result.append(toolName);
        result.append(", toolVersion: "); //$NON-NLS-1$
        result.append(toolVersion);
        result.append(", formatName: "); //$NON-NLS-1$
        result.append(formatName);
        result.append(", formatVersion: "); //$NON-NLS-1$
        result.append(formatVersion);
        result.append(')');
        return result.toString();
    }

} //FileReferenceImpl
