/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.impl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.core.util.DateUtil;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.impl.ModelImportImpl;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelAccessibility;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getSeverity <em>Severity</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getMarkers <em>Markers</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#isVisible <em>Visible</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getAccessibility <em>Accessibility</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getTimeLastSynchronized <em>Time Last Synchronized</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getTimeLastSynchronizedAsDate <em>Time Last Synchronized As Date</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getChecksum <em>Checksum</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getVirtualDatabase <em>Virtual Database</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getUses <em>Uses</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getUsedBy <em>Used By</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl#getModelSource <em>Model Source</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelReferenceImpl extends ModelImportImpl implements ModelReference {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getSeverity() <em>Severity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSeverity()
     * @generated
     * @ordered
     */
    protected static final Severity SEVERITY_EDEFAULT = Severity.OK_LITERAL;

    /**
     * The cached value of the '{@link #getSeverity() <em>Severity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSeverity()
     * @generated
     * @ordered
     */
    protected Severity severity = SEVERITY_EDEFAULT;

    /**
     * The cached value of the '{@link #getMarkers() <em>Markers</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMarkers()
     * @generated
     * @ordered
     */
    protected EList markers = null;

    /**
     * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected static final String VERSION_EDEFAULT = null;

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
     * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUri()
     * @generated
     * @ordered
     */
    protected static final String URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUri()
     * @generated
     * @ordered
     */
    protected String uri = URI_EDEFAULT;

    /**
     * The default value of the '{@link #isVisible() <em>Visible</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isVisible()
     * @generated
     * @ordered
     */
	protected static final boolean VISIBLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isVisible() <em>Visible</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isVisible()
     * @generated
     * @ordered
     */
	protected boolean visible = VISIBLE_EDEFAULT;

    /**
     * The default value of the '{@link #getAccessibility() <em>Accessibility</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getAccessibility()
     * @generated
     * @ordered
     */
	protected static final ModelAccessibility ACCESSIBILITY_EDEFAULT = ModelAccessibility.PUBLIC_LITERAL;

    /**
     * The cached value of the '{@link #getAccessibility() <em>Accessibility</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getAccessibility()
     * @generated
     * @ordered
     */
	protected ModelAccessibility accessibility = ACCESSIBILITY_EDEFAULT;

    /**
     * The default value of the '{@link #getTimeLastSynchronized() <em>Time Last Synchronized</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTimeLastSynchronized()
     * @generated
     * @ordered
     */
	protected static final String TIME_LAST_SYNCHRONIZED_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTimeLastSynchronized() <em>Time Last Synchronized</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTimeLastSynchronized()
     * @generated
     * @ordered
     */
	protected String timeLastSynchronized = TIME_LAST_SYNCHRONIZED_EDEFAULT;

    /**
     * The default value of the '{@link #getTimeLastSynchronizedAsDate() <em>Time Last Synchronized As Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeLastSynchronizedAsDate()
     * @generated
     * @ordered
     */
    protected static final Date TIME_LAST_SYNCHRONIZED_AS_DATE_EDEFAULT = null;

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
     * The cached value of the '{@link #getUses() <em>Uses</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUses()
     * @generated
     * @ordered
     */
    protected EList uses = null;

    /**
     * The cached value of the '{@link #getUsedBy() <em>Used By</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUsedBy()
     * @generated
     * @ordered
     */
    protected EList usedBy = null;

    /**
     * The cached value of the '{@link #getModelSource() <em>Model Source</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getModelSource()
     * @generated
     * @ordered
     */
    protected ModelSource modelSource = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModelReferenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ManifestPackage.eINSTANCE.getModelReference();
    }

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public Severity getSeverity()
    {
        return calculateSeverity(Severity.OK_LITERAL);
        //return severity;
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Severity getSeverityGen() {
        return severity;
    }

    protected Severity calculateSeverity( final Severity initialSeverity ) {
        Severity severity = initialSeverity;
        if ( this.markers != null ) {
            final Iterator iter = this.markers.iterator();
            while (iter.hasNext()) {
                final ProblemMarker marker = (ProblemMarker)iter.next();
                if ( marker.getSeverity().getValue() > severity.getValue() ) {
                    severity = marker.getSeverity();
                }
            }
        }
        return severity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSeverity(Severity newSeverity) {
        Severity oldSeverity = severity;
        severity = newSeverity == null ? SEVERITY_EDEFAULT : newSeverity;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__SEVERITY, oldSeverity, severity));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getMarkers() {
        if (markers == null) {
            markers = new EObjectContainmentWithInverseEList(ProblemMarker.class, this, ManifestPackage.MODEL_REFERENCE__MARKERS, ManifestPackage.PROBLEM_MARKER__MARKED);
        }
        return markers;
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
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__VERSION, oldVersion, version));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUri() {
        return uri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUri(String newUri) {
        String oldUri = uri;
        uri = newUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__URI, oldUri, uri));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public boolean isVisible() {
        return visible;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setVisible(boolean newVisible) {
        boolean oldVisible = visible;
        visible = newVisible;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__VISIBLE, oldVisible, visible));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public ModelAccessibility getAccessibility() {
        return accessibility;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setAccessibility(ModelAccessibility newAccessibility) {
        ModelAccessibility oldAccessibility = accessibility;
        accessibility = newAccessibility == null ? ACCESSIBILITY_EDEFAULT : newAccessibility;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__ACCESSIBILITY, oldAccessibility, accessibility));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getTimeLastSynchronized() {
        return timeLastSynchronized;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setTimeLastSynchronized(String newTimeLastSynchronized) {
        String oldTimeLastSynchronized = timeLastSynchronized;
        timeLastSynchronized = newTimeLastSynchronized;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED, oldTimeLastSynchronized, timeLastSynchronized));
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Date getTimeLastSynchronizedAsDate()
	{
        final String timeLastProducedString = this.getTimeLastSynchronized();
        if ( timeLastProducedString != null ) {
            try {
                final Date result = DateUtil.convertStringToDate(timeLastProducedString);
                return result;
            } catch (ParseException e) {
                try {
                    final long longValue = Long.parseLong(timeLastProducedString);
                    final Date result = new Date(longValue);
                    return result;
                } catch ( NumberFormatException e1 ) {
                    // Do nothing; was not an old format
                }
                // Do nothing; wasn't a valid date
            }
        }
        return null;
	}
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getTimeLastSynchronizedAsDateGen() {
        // TODO: implement this method to return the 'Time Last Synchronized As Date' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setTimeLastSynchronizedAsDate(Date newTimeLastSynchronizedAsDate)
	{
        if ( newTimeLastSynchronizedAsDate == null ) {
            this.setTimeLastSynchronized(null);
        } else {
            final String dateString = DateUtil.getDateAsString(newTimeLastSynchronizedAsDate);
            this.setTimeLastSynchronized(dateString);
        }
	}
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimeLastSynchronizedAsDateGen(Date newTimeLastSynchronizedAsDate) {
        // TODO: implement this method to set the 'Time Last Synchronized As Date' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
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
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__CHECKSUM, oldChecksum, checksum));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public VirtualDatabase getVirtualDatabase() {
        if (eContainerFeatureID != ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE) return null;
        return (VirtualDatabase)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVirtualDatabase(VirtualDatabase newVirtualDatabase) {
        if (newVirtualDatabase != eContainer || (eContainerFeatureID != ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE && newVirtualDatabase != null)) {
            if (EcoreUtil.isAncestor(this, newVirtualDatabase))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newVirtualDatabase != null)
                msgs = ((InternalEObject)newVirtualDatabase).eInverseAdd(this, ManifestPackage.VIRTUAL_DATABASE__MODELS, VirtualDatabase.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newVirtualDatabase, ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE, newVirtualDatabase, newVirtualDatabase));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getUses() {
        if (uses == null) {
            uses = new EObjectWithInverseResolvingEList.ManyInverse(ModelReference.class, this, ManifestPackage.MODEL_REFERENCE__USES, ManifestPackage.MODEL_REFERENCE__USED_BY);
        }
        return uses;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getUsedBy() {
        if (usedBy == null) {
            usedBy = new EObjectWithInverseResolvingEList.ManyInverse(ModelReference.class, this, ManifestPackage.MODEL_REFERENCE__USED_BY, ManifestPackage.MODEL_REFERENCE__USES);
        }
        return usedBy;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelSource getModelSource() {
        return modelSource;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetModelSource(ModelSource newModelSource, NotificationChain msgs) {
        ModelSource oldModelSource = modelSource;
        modelSource = newModelSource;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE, oldModelSource, newModelSource);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setModelSource(ModelSource newModelSource) {
        if (newModelSource != modelSource) {
            NotificationChain msgs = null;
            if (modelSource != null)
                msgs = ((InternalEObject)modelSource).eInverseRemove(this, ManifestPackage.MODEL_SOURCE__MODEL, ModelSource.class, msgs);
            if (newModelSource != null)
                msgs = ((InternalEObject)newModelSource).eInverseAdd(this, ManifestPackage.MODEL_SOURCE__MODEL, ModelSource.class, msgs);
            msgs = basicSetModelSource(newModelSource, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE, newModelSource, newModelSource));
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
                case ManifestPackage.MODEL_REFERENCE__MODEL:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ManifestPackage.MODEL_REFERENCE__MODEL, msgs);
                case ManifestPackage.MODEL_REFERENCE__MARKERS:
                    return ((InternalEList)getMarkers()).basicAdd(otherEnd, msgs);
                case ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE, msgs);
                case ManifestPackage.MODEL_REFERENCE__USES:
                    return ((InternalEList)getUses()).basicAdd(otherEnd, msgs);
                case ManifestPackage.MODEL_REFERENCE__USED_BY:
                    return ((InternalEList)getUsedBy()).basicAdd(otherEnd, msgs);
                case ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE:
                    if (modelSource != null)
                        msgs = ((InternalEObject)modelSource).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE, null, msgs);
                    return basicSetModelSource((ModelSource)otherEnd, msgs);
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
                case ManifestPackage.MODEL_REFERENCE__MODEL:
                    return eBasicSetContainer(null, ManifestPackage.MODEL_REFERENCE__MODEL, msgs);
                case ManifestPackage.MODEL_REFERENCE__MARKERS:
                    return ((InternalEList)getMarkers()).basicRemove(otherEnd, msgs);
                case ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE:
                    return eBasicSetContainer(null, ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE, msgs);
                case ManifestPackage.MODEL_REFERENCE__USES:
                    return ((InternalEList)getUses()).basicRemove(otherEnd, msgs);
                case ManifestPackage.MODEL_REFERENCE__USED_BY:
                    return ((InternalEList)getUsedBy()).basicRemove(otherEnd, msgs);
                case ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE:
                    return basicSetModelSource(null, msgs);
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
                case ManifestPackage.MODEL_REFERENCE__MODEL:
                    return eContainer.eInverseRemove(this, CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS, ModelAnnotation.class, msgs);
                case ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE:
                    return eContainer.eInverseRemove(this, ManifestPackage.VIRTUAL_DATABASE__MODELS, VirtualDatabase.class, msgs);
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
            case ManifestPackage.MODEL_REFERENCE__NAME:
                return getName();
            case ManifestPackage.MODEL_REFERENCE__PATH:
                return getPath();
            case ManifestPackage.MODEL_REFERENCE__MODEL_LOCATION:
                return getModelLocation();
            case ManifestPackage.MODEL_REFERENCE__UUID:
                return getUuid();
            case ManifestPackage.MODEL_REFERENCE__MODEL_TYPE:
                return getModelType();
            case ManifestPackage.MODEL_REFERENCE__PRIMARY_METAMODEL_URI:
                return getPrimaryMetamodelUri();
            case ManifestPackage.MODEL_REFERENCE__MODEL:
                return getModel();
            case ManifestPackage.MODEL_REFERENCE__SEVERITY:
                return getSeverity();
            case ManifestPackage.MODEL_REFERENCE__MARKERS:
                return getMarkers();
            case ManifestPackage.MODEL_REFERENCE__VERSION:
                return getVersion();
            case ManifestPackage.MODEL_REFERENCE__URI:
                return getUri();
            case ManifestPackage.MODEL_REFERENCE__VISIBLE:
                return isVisible() ? Boolean.TRUE : Boolean.FALSE;
            case ManifestPackage.MODEL_REFERENCE__ACCESSIBILITY:
                return getAccessibility();
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED:
                return getTimeLastSynchronized();
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED_AS_DATE:
                return getTimeLastSynchronizedAsDate();
            case ManifestPackage.MODEL_REFERENCE__CHECKSUM:
                return new Long(getChecksum());
            case ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE:
                return getVirtualDatabase();
            case ManifestPackage.MODEL_REFERENCE__USES:
                return getUses();
            case ManifestPackage.MODEL_REFERENCE__USED_BY:
                return getUsedBy();
            case ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE:
                return getModelSource();
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
            case ManifestPackage.MODEL_REFERENCE__NAME:
                setName((String)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL_LOCATION:
                setModelLocation((String)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__UUID:
                setUuid((String)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL_TYPE:
                setModelType((ModelType)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__PRIMARY_METAMODEL_URI:
                setPrimaryMetamodelUri((String)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL:
                setModel((ModelAnnotation)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__SEVERITY:
                setSeverity((Severity)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__MARKERS:
                getMarkers().clear();
                getMarkers().addAll((Collection)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__VERSION:
                setVersion((String)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__URI:
                setUri((String)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__VISIBLE:
                setVisible(((Boolean)newValue).booleanValue());
                return;
            case ManifestPackage.MODEL_REFERENCE__ACCESSIBILITY:
                setAccessibility((ModelAccessibility)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED:
                setTimeLastSynchronized((String)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED_AS_DATE:
                setTimeLastSynchronizedAsDate((Date)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__CHECKSUM:
                setChecksum(((Long)newValue).longValue());
                return;
            case ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE:
                setVirtualDatabase((VirtualDatabase)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__USES:
                getUses().clear();
                getUses().addAll((Collection)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__USED_BY:
                getUsedBy().clear();
                getUsedBy().addAll((Collection)newValue);
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE:
                setModelSource((ModelSource)newValue);
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
            case ManifestPackage.MODEL_REFERENCE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL_LOCATION:
                setModelLocation(MODEL_LOCATION_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__UUID:
                setUuid(UUID_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL_TYPE:
                setModelType(MODEL_TYPE_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__PRIMARY_METAMODEL_URI:
                setPrimaryMetamodelUri(PRIMARY_METAMODEL_URI_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL:
                setModel((ModelAnnotation)null);
                return;
            case ManifestPackage.MODEL_REFERENCE__SEVERITY:
                setSeverity(SEVERITY_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__MARKERS:
                getMarkers().clear();
                return;
            case ManifestPackage.MODEL_REFERENCE__VERSION:
                setVersion(VERSION_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__URI:
                setUri(URI_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__VISIBLE:
                setVisible(VISIBLE_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__ACCESSIBILITY:
                setAccessibility(ACCESSIBILITY_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED:
                setTimeLastSynchronized(TIME_LAST_SYNCHRONIZED_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED_AS_DATE:
                setTimeLastSynchronizedAsDate(TIME_LAST_SYNCHRONIZED_AS_DATE_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__CHECKSUM:
                setChecksum(CHECKSUM_EDEFAULT);
                return;
            case ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE:
                setVirtualDatabase((VirtualDatabase)null);
                return;
            case ManifestPackage.MODEL_REFERENCE__USES:
                getUses().clear();
                return;
            case ManifestPackage.MODEL_REFERENCE__USED_BY:
                getUsedBy().clear();
                return;
            case ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE:
                setModelSource((ModelSource)null);
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
            case ManifestPackage.MODEL_REFERENCE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case ManifestPackage.MODEL_REFERENCE__PATH:
                return PATH_EDEFAULT == null ? getPath() != null : !PATH_EDEFAULT.equals(getPath());
            case ManifestPackage.MODEL_REFERENCE__MODEL_LOCATION:
                return MODEL_LOCATION_EDEFAULT == null ? modelLocation != null : !MODEL_LOCATION_EDEFAULT.equals(modelLocation);
            case ManifestPackage.MODEL_REFERENCE__UUID:
                return UUID_EDEFAULT == null ? uuid != null : !UUID_EDEFAULT.equals(uuid);
            case ManifestPackage.MODEL_REFERENCE__MODEL_TYPE:
                return modelType != MODEL_TYPE_EDEFAULT;
            case ManifestPackage.MODEL_REFERENCE__PRIMARY_METAMODEL_URI:
                return PRIMARY_METAMODEL_URI_EDEFAULT == null ? primaryMetamodelUri != null : !PRIMARY_METAMODEL_URI_EDEFAULT.equals(primaryMetamodelUri);
            case ManifestPackage.MODEL_REFERENCE__MODEL:
                return getModel() != null;
            case ManifestPackage.MODEL_REFERENCE__SEVERITY:
                return severity != SEVERITY_EDEFAULT;
            case ManifestPackage.MODEL_REFERENCE__MARKERS:
                return markers != null && !markers.isEmpty();
            case ManifestPackage.MODEL_REFERENCE__VERSION:
                return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
            case ManifestPackage.MODEL_REFERENCE__URI:
                return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
            case ManifestPackage.MODEL_REFERENCE__VISIBLE:
                return visible != VISIBLE_EDEFAULT;
            case ManifestPackage.MODEL_REFERENCE__ACCESSIBILITY:
                return accessibility != ACCESSIBILITY_EDEFAULT;
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED:
                return TIME_LAST_SYNCHRONIZED_EDEFAULT == null ? timeLastSynchronized != null : !TIME_LAST_SYNCHRONIZED_EDEFAULT.equals(timeLastSynchronized);
            case ManifestPackage.MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED_AS_DATE:
                return TIME_LAST_SYNCHRONIZED_AS_DATE_EDEFAULT == null ? getTimeLastSynchronizedAsDate() != null : !TIME_LAST_SYNCHRONIZED_AS_DATE_EDEFAULT.equals(getTimeLastSynchronizedAsDate());
            case ManifestPackage.MODEL_REFERENCE__CHECKSUM:
                return checksum != CHECKSUM_EDEFAULT;
            case ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE:
                return getVirtualDatabase() != null;
            case ManifestPackage.MODEL_REFERENCE__USES:
                return uses != null && !uses.isEmpty();
            case ManifestPackage.MODEL_REFERENCE__USED_BY:
                return usedBy != null && !usedBy.isEmpty();
            case ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE:
                return modelSource != null;
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
        if (baseClass == ProblemMarkerContainer.class) {
            switch (derivedFeatureID) {
                case ManifestPackage.MODEL_REFERENCE__SEVERITY: return ManifestPackage.PROBLEM_MARKER_CONTAINER__SEVERITY;
                case ManifestPackage.MODEL_REFERENCE__MARKERS: return ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS;
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
        if (baseClass == ProblemMarkerContainer.class) {
            switch (baseFeatureID) {
                case ManifestPackage.PROBLEM_MARKER_CONTAINER__SEVERITY: return ManifestPackage.MODEL_REFERENCE__SEVERITY;
                case ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS: return ManifestPackage.MODEL_REFERENCE__MARKERS;
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
        result.append(" (severity: "); //$NON-NLS-1$
        result.append(severity);
        result.append(", version: "); //$NON-NLS-1$
        result.append(version);
        result.append(", uri: "); //$NON-NLS-1$
        result.append(uri);
        result.append(", visible: "); //$NON-NLS-1$
        result.append(visible);
        result.append(", accessibility: "); //$NON-NLS-1$
        result.append(accessibility);
        result.append(", timeLastSynchronized: "); //$NON-NLS-1$
        result.append(timeLastSynchronized);
        result.append(", checksum: "); //$NON-NLS-1$
        result.append(checksum);
        result.append(')');
        return result.toString();
    }

} //ModelReferenceImpl
