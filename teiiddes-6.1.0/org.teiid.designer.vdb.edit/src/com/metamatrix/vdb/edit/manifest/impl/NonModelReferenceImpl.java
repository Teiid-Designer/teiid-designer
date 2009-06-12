/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Non Model Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.NonModelReferenceImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.NonModelReferenceImpl#getPath <em>Path</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.NonModelReferenceImpl#getChecksum <em>Checksum</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.NonModelReferenceImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.NonModelReferenceImpl#getVirtualDatabase <em>Virtual Database</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NonModelReferenceImpl extends EObjectImpl implements NonModelReference {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

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
     * The default value of the '{@link #getPath() <em>Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPath()
     * @generated
     * @ordered
     */
    protected static final String PATH_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPath() <em>Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPath()
     * @generated
     * @ordered
     */
    protected String path = PATH_EDEFAULT;

    /**
     * The default value of the '{@link #getChecksum() <em>Checksum</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChecksum()
     * @generated
     * @ordered
     */
    protected static final long CHECKSUM_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getChecksum() <em>Checksum</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChecksum()
     * @generated
     * @ordered
     */
    protected long checksum = CHECKSUM_EDEFAULT;

    /**
     * The cached value of the '{@link #getProperties() <em>Properties</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProperties()
     * @generated
     * @ordered
     */
    protected EMap properties = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected NonModelReferenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ManifestPackage.eINSTANCE.getNonModelReference();
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
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.NON_MODEL_REFERENCE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getPath() {
        return path;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPath(String newPath) {
        String oldPath = path;
        path = newPath;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.NON_MODEL_REFERENCE__PATH, oldPath, path));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public long getChecksum() {
        return checksum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setChecksum(long newChecksum) {
        long oldChecksum = checksum;
        checksum = newChecksum;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.NON_MODEL_REFERENCE__CHECKSUM, oldChecksum, checksum));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMap getProperties() {
        if (properties == null) {
            properties = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, ManifestPackage.NON_MODEL_REFERENCE__PROPERTIES);
        }
        return properties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public VirtualDatabase getVirtualDatabase() {
        if (eContainerFeatureID != ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE) return null;
        return (VirtualDatabase)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVirtualDatabase(VirtualDatabase newVirtualDatabase) {
        if (newVirtualDatabase != eContainer || (eContainerFeatureID != ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE && newVirtualDatabase != null)) {
            if (EcoreUtil.isAncestor(this, newVirtualDatabase))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newVirtualDatabase != null)
                msgs = ((InternalEObject)newVirtualDatabase).eInverseAdd(this, ManifestPackage.VIRTUAL_DATABASE__NON_MODELS, VirtualDatabase.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newVirtualDatabase, ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE, newVirtualDatabase, newVirtualDatabase));
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
                case ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE, msgs);
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
                case ManifestPackage.NON_MODEL_REFERENCE__PROPERTIES:
                    return ((InternalEList)getProperties()).basicRemove(otherEnd, msgs);
                case ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE:
                    return eBasicSetContainer(null, ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE, msgs);
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
                case ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE:
                    return eContainer.eInverseRemove(this, ManifestPackage.VIRTUAL_DATABASE__NON_MODELS, VirtualDatabase.class, msgs);
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
            case ManifestPackage.NON_MODEL_REFERENCE__NAME:
                return getName();
            case ManifestPackage.NON_MODEL_REFERENCE__PATH:
                return getPath();
            case ManifestPackage.NON_MODEL_REFERENCE__CHECKSUM:
                return new Long(getChecksum());
            case ManifestPackage.NON_MODEL_REFERENCE__PROPERTIES:
                return getProperties();
            case ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE:
                return getVirtualDatabase();
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
            case ManifestPackage.NON_MODEL_REFERENCE__NAME:
                setName((String)newValue);
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__PATH:
                setPath((String)newValue);
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__CHECKSUM:
                setChecksum(((Long)newValue).longValue());
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__PROPERTIES:
                getProperties().clear();
                getProperties().addAll((Collection)newValue);
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE:
                setVirtualDatabase((VirtualDatabase)newValue);
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
            case ManifestPackage.NON_MODEL_REFERENCE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__PATH:
                setPath(PATH_EDEFAULT);
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__CHECKSUM:
                setChecksum(CHECKSUM_EDEFAULT);
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__PROPERTIES:
                getProperties().clear();
                return;
            case ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE:
                setVirtualDatabase((VirtualDatabase)null);
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
            case ManifestPackage.NON_MODEL_REFERENCE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case ManifestPackage.NON_MODEL_REFERENCE__PATH:
                return PATH_EDEFAULT == null ? path != null : !PATH_EDEFAULT.equals(path);
            case ManifestPackage.NON_MODEL_REFERENCE__CHECKSUM:
                return checksum != CHECKSUM_EDEFAULT;
            case ManifestPackage.NON_MODEL_REFERENCE__PROPERTIES:
                return properties != null && !properties.isEmpty();
            case ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE:
                return getVirtualDatabase() != null;
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
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", path: "); //$NON-NLS-1$
        result.append(path);
        result.append(", checksum: "); //$NON-NLS-1$
        result.append(checksum);
        result.append(')');
        return result.toString();
    }

} //NonModelReferenceImpl
