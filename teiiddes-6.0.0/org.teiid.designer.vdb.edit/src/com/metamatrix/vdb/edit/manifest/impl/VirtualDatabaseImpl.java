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
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.core.util.DateUtil;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Virtual Database</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getUuid <em>Uuid</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getProvider <em>Provider</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getTimeLastChanged <em>Time Last Changed</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getTimeLastProduced <em>Time Last Produced</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getTimeLastChangedAsDate <em>Time Last Changed As Date</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getTimeLastProducedAsDate <em>Time Last Produced As Date</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getProducerName <em>Producer Name</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getProducerVersion <em>Producer Version</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#isIncludeModelFiles <em>Include Model Files</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getModels <em>Models</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getWsdlOptions <em>Wsdl Options</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl#getNonModels <em>Non Models</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class VirtualDatabaseImpl extends ProblemMarkerContainerImpl implements VirtualDatabase {
    
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
     * The default value of the '{@link #getIdentifier() <em>Identifier</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIdentifier()
     * @generated
     * @ordered
     */
    protected static final String IDENTIFIER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getIdentifier() <em>Identifier</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIdentifier()
     * @generated
     * @ordered
     */
    protected String identifier = IDENTIFIER_EDEFAULT;

    /**
     * The default value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUuid()
     * @generated
     * @ordered
     */
    protected static final String UUID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUuid()
     * @generated
     * @ordered
     */
    protected String uuid = UUID_EDEFAULT;

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

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
     * The default value of the '{@link #getProvider() <em>Provider</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProvider()
     * @generated
     * @ordered
     */
    protected static final String PROVIDER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProvider() <em>Provider</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProvider()
     * @generated
     * @ordered
     */
    protected String provider = PROVIDER_EDEFAULT;

    /**
     * The default value of the '{@link #getTimeLastChanged() <em>Time Last Changed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeLastChanged()
     * @generated
     * @ordered
     */
    protected static final String TIME_LAST_CHANGED_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTimeLastChanged() <em>Time Last Changed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeLastChanged()
     * @generated
     * @ordered
     */
    protected String timeLastChanged = TIME_LAST_CHANGED_EDEFAULT;

    /**
     * The default value of the '{@link #getTimeLastProduced() <em>Time Last Produced</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeLastProduced()
     * @generated
     * @ordered
     */
    protected static final String TIME_LAST_PRODUCED_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTimeLastProduced() <em>Time Last Produced</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeLastProduced()
     * @generated
     * @ordered
     */
    protected String timeLastProduced = TIME_LAST_PRODUCED_EDEFAULT;

    /**
     * The default value of the '{@link #getTimeLastChangedAsDate() <em>Time Last Changed As Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeLastChangedAsDate()
     * @generated
     * @ordered
     */
    protected static final Date TIME_LAST_CHANGED_AS_DATE_EDEFAULT = null;

    /**
     * The default value of the '{@link #getTimeLastProducedAsDate() <em>Time Last Produced As Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeLastProducedAsDate()
     * @generated
     * @ordered
     */
    protected static final Date TIME_LAST_PRODUCED_AS_DATE_EDEFAULT = null;

    /**
     * The default value of the '{@link #getProducerName() <em>Producer Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProducerName()
     * @generated
     * @ordered
     */
    protected static final String PRODUCER_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProducerName() <em>Producer Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProducerName()
     * @generated
     * @ordered
     */
    protected String producerName = PRODUCER_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getProducerVersion() <em>Producer Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProducerVersion()
     * @generated
     * @ordered
     */
    protected static final String PRODUCER_VERSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProducerVersion() <em>Producer Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProducerVersion()
     * @generated
     * @ordered
     */
    protected String producerVersion = PRODUCER_VERSION_EDEFAULT;

    /**
     * The default value of the '{@link #isIncludeModelFiles() <em>Include Model Files</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeModelFiles()
     * @generated
     * @ordered
     */
    protected static final boolean INCLUDE_MODEL_FILES_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIncludeModelFiles() <em>Include Model Files</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeModelFiles()
     * @generated
     * @ordered
     */
    protected boolean includeModelFiles = INCLUDE_MODEL_FILES_EDEFAULT;

    /**
     * The cached value of the '{@link #getModels() <em>Models</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getModels()
     * @generated
     * @ordered
     */
    protected EList models = null;

    /**
     * The cached value of the '{@link #getWsdlOptions() <em>Wsdl Options</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getWsdlOptions()
     * @generated
     * @ordered
     */
	protected WsdlOptions wsdlOptions = null;

    /**
     * The cached value of the '{@link #getNonModels() <em>Non Models</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNonModels()
     * @generated
     * @ordered
     */
    protected EList nonModels = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VirtualDatabaseImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ManifestPackage.eINSTANCE.getVirtualDatabase();
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
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIdentifier(String newIdentifier) {
        String oldIdentifier = identifier;
        identifier = newIdentifier;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__IDENTIFIER, oldIdentifier, identifier));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUuid(String newUuid) {
        String oldUuid = uuid;
        uuid = newUuid;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__UUID, oldUuid, uuid));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void setDescription(String newDescription) {
        String oldDescription = this.description;
        if (newDescription == null) {
            this.description = null;
        } else {
            char[] chrs = newDescription.toCharArray();
            StringBuffer desc = new StringBuffer();
            for (int ndx = 0, len = chrs.length; ndx < len; ++ndx) {
                char chr = chrs[ndx];
                if ((chr > ' ' && chr < 0x7F) || Character.isWhitespace(chr)) {
                    desc.append(chr);
                } else {
                    desc.append(' ');
                }
            }
            this.description = desc.toString();
        }
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__DESCRIPTION, oldDescription, this.description));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDescriptionGen(String newDescription) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__DESCRIPTION, oldDescription, description));
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
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__VERSION, oldVersion, version));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getProvider() {
        return provider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setProvider(String newProvider) {
        String oldProvider = provider;
        provider = newProvider;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__PROVIDER, oldProvider, provider));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTimeLastChanged() {
        return timeLastChanged;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimeLastChanged(String newTimeLastChanged) {
        String oldTimeLastChanged = timeLastChanged;
        timeLastChanged = newTimeLastChanged;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED, oldTimeLastChanged, timeLastChanged));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTimeLastProduced() {
        return timeLastProduced;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimeLastProduced(String newTimeLastProduced) {
        String oldTimeLastProduced = timeLastProduced;
        timeLastProduced = newTimeLastProduced;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED, oldTimeLastProduced, timeLastProduced));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public Date getTimeLastChangedAsDate() {
        final String timeLastChangedString = this.getTimeLastChanged();
        if ( timeLastChangedString != null ) {
            try {
                final Date result = DateUtil.convertStringToDate(timeLastChangedString);
                return result;
            } catch (ParseException e) {
                try {
                    final long longValue = Long.parseLong(timeLastChangedString);
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
    public Date getTimeLastChangedAsDateGen() {
        // TODO: implement this method to return the 'Time Last Changed As Date' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void setTimeLastChangedAsDate(Date newTimeLastChangedAsDate) {
        if ( newTimeLastChangedAsDate == null ) {
            this.setTimeLastChanged(null);
        } else {
            final String dateString = DateUtil.getDateAsString(newTimeLastChangedAsDate);
            this.setTimeLastChanged(dateString);
        }
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimeLastChangedAsDateGen(Date newTimeLastChangedAsDate) {
        // TODO: implement this method to set the 'Time Last Changed As Date' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public Date getTimeLastProducedAsDate() {
        final String timeLastProducedString = this.getTimeLastProduced();
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
    public Date getTimeLastProducedAsDateGen() {
        // TODO: implement this method to return the 'Time Last Produced As Date' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void setTimeLastProducedAsDate(Date newTimeLastProducedAsDate) {
        if ( newTimeLastProducedAsDate == null ) {
            this.setTimeLastProduced(null);
        } else {
            final String dateString = DateUtil.getDateAsString(newTimeLastProducedAsDate);
            this.setTimeLastProduced(dateString);
        }
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimeLastProducedAsDateGen(Date newTimeLastProducedAsDate) {
        // TODO: implement this method to set the 'Time Last Produced As Date' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getProducerName() {
        return producerName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setProducerName(String newProducerName) {
        String oldProducerName = producerName;
        producerName = newProducerName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__PRODUCER_NAME, oldProducerName, producerName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getProducerVersion() {
        return producerVersion;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setProducerVersion(String newProducerVersion) {
        String oldProducerVersion = producerVersion;
        producerVersion = newProducerVersion;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__PRODUCER_VERSION, oldProducerVersion, producerVersion));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIncludeModelFiles() {
        return includeModelFiles;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIncludeModelFiles(boolean newIncludeModelFiles) {
        boolean oldIncludeModelFiles = includeModelFiles;
        includeModelFiles = newIncludeModelFiles;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__INCLUDE_MODEL_FILES, oldIncludeModelFiles, includeModelFiles));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getModels() {
        if (models == null) {
            models = new EObjectContainmentWithInverseEList(ModelReference.class, this, ManifestPackage.VIRTUAL_DATABASE__MODELS, ManifestPackage.MODEL_REFERENCE__VIRTUAL_DATABASE);
        }
        return models;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public WsdlOptions getWsdlOptions() {
        return wsdlOptions;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetWsdlOptions(WsdlOptions newWsdlOptions, NotificationChain msgs) {
        WsdlOptions oldWsdlOptions = wsdlOptions;
        wsdlOptions = newWsdlOptions;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS, oldWsdlOptions, newWsdlOptions);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setWsdlOptions(WsdlOptions newWsdlOptions) {
        if (newWsdlOptions != wsdlOptions) {
            NotificationChain msgs = null;
            if (wsdlOptions != null)
                msgs = ((InternalEObject)wsdlOptions).eInverseRemove(this, ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE, WsdlOptions.class, msgs);
            if (newWsdlOptions != null)
                msgs = ((InternalEObject)newWsdlOptions).eInverseAdd(this, ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE, WsdlOptions.class, msgs);
            msgs = basicSetWsdlOptions(newWsdlOptions, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS, newWsdlOptions, newWsdlOptions));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getNonModels() {
        if (nonModels == null) {
            nonModels = new EObjectContainmentWithInverseEList(NonModelReference.class, this, ManifestPackage.VIRTUAL_DATABASE__NON_MODELS, ManifestPackage.NON_MODEL_REFERENCE__VIRTUAL_DATABASE);
        }
        return nonModels;
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
                case ManifestPackage.VIRTUAL_DATABASE__MARKERS:
                    return ((InternalEList)getMarkers()).basicAdd(otherEnd, msgs);
                case ManifestPackage.VIRTUAL_DATABASE__MODELS:
                    return ((InternalEList)getModels()).basicAdd(otherEnd, msgs);
                case ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS:
                    if (wsdlOptions != null)
                        msgs = ((InternalEObject)wsdlOptions).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS, null, msgs);
                    return basicSetWsdlOptions((WsdlOptions)otherEnd, msgs);
                case ManifestPackage.VIRTUAL_DATABASE__NON_MODELS:
                    return ((InternalEList)getNonModels()).basicAdd(otherEnd, msgs);
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
                case ManifestPackage.VIRTUAL_DATABASE__MARKERS:
                    return ((InternalEList)getMarkers()).basicRemove(otherEnd, msgs);
                case ManifestPackage.VIRTUAL_DATABASE__MODELS:
                    return ((InternalEList)getModels()).basicRemove(otherEnd, msgs);
                case ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS:
                    return basicSetWsdlOptions(null, msgs);
                case ManifestPackage.VIRTUAL_DATABASE__NON_MODELS:
                    return ((InternalEList)getNonModels()).basicRemove(otherEnd, msgs);
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
            case ManifestPackage.VIRTUAL_DATABASE__SEVERITY:
                return getSeverity();
            case ManifestPackage.VIRTUAL_DATABASE__MARKERS:
                return getMarkers();
            case ManifestPackage.VIRTUAL_DATABASE__NAME:
                return getName();
            case ManifestPackage.VIRTUAL_DATABASE__IDENTIFIER:
                return getIdentifier();
            case ManifestPackage.VIRTUAL_DATABASE__UUID:
                return getUuid();
            case ManifestPackage.VIRTUAL_DATABASE__DESCRIPTION:
                return getDescription();
            case ManifestPackage.VIRTUAL_DATABASE__VERSION:
                return getVersion();
            case ManifestPackage.VIRTUAL_DATABASE__PROVIDER:
                return getProvider();
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED:
                return getTimeLastChanged();
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED:
                return getTimeLastProduced();
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED_AS_DATE:
                return getTimeLastChangedAsDate();
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED_AS_DATE:
                return getTimeLastProducedAsDate();
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_NAME:
                return getProducerName();
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_VERSION:
                return getProducerVersion();
            case ManifestPackage.VIRTUAL_DATABASE__INCLUDE_MODEL_FILES:
                return isIncludeModelFiles() ? Boolean.TRUE : Boolean.FALSE;
            case ManifestPackage.VIRTUAL_DATABASE__MODELS:
                return getModels();
            case ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS:
                return getWsdlOptions();
            case ManifestPackage.VIRTUAL_DATABASE__NON_MODELS:
                return getNonModels();
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
            case ManifestPackage.VIRTUAL_DATABASE__SEVERITY:
                setSeverity((Severity)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__MARKERS:
                getMarkers().clear();
                getMarkers().addAll((Collection)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__NAME:
                setName((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__IDENTIFIER:
                setIdentifier((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__UUID:
                setUuid((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__DESCRIPTION:
                setDescription((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__VERSION:
                setVersion((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__PROVIDER:
                setProvider((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED:
                setTimeLastChanged((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED:
                setTimeLastProduced((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED_AS_DATE:
                setTimeLastChangedAsDate((Date)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED_AS_DATE:
                setTimeLastProducedAsDate((Date)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_NAME:
                setProducerName((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_VERSION:
                setProducerVersion((String)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__INCLUDE_MODEL_FILES:
                setIncludeModelFiles(((Boolean)newValue).booleanValue());
                return;
            case ManifestPackage.VIRTUAL_DATABASE__MODELS:
                getModels().clear();
                getModels().addAll((Collection)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS:
                setWsdlOptions((WsdlOptions)newValue);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__NON_MODELS:
                getNonModels().clear();
                getNonModels().addAll((Collection)newValue);
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
            case ManifestPackage.VIRTUAL_DATABASE__SEVERITY:
                setSeverity(SEVERITY_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__MARKERS:
                getMarkers().clear();
                return;
            case ManifestPackage.VIRTUAL_DATABASE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__IDENTIFIER:
                setIdentifier(IDENTIFIER_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__UUID:
                setUuid(UUID_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__VERSION:
                setVersion(VERSION_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__PROVIDER:
                setProvider(PROVIDER_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED:
                setTimeLastChanged(TIME_LAST_CHANGED_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED:
                setTimeLastProduced(TIME_LAST_PRODUCED_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED_AS_DATE:
                setTimeLastChangedAsDate(TIME_LAST_CHANGED_AS_DATE_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED_AS_DATE:
                setTimeLastProducedAsDate(TIME_LAST_PRODUCED_AS_DATE_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_NAME:
                setProducerName(PRODUCER_NAME_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_VERSION:
                setProducerVersion(PRODUCER_VERSION_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__INCLUDE_MODEL_FILES:
                setIncludeModelFiles(INCLUDE_MODEL_FILES_EDEFAULT);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__MODELS:
                getModels().clear();
                return;
            case ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS:
                setWsdlOptions((WsdlOptions)null);
                return;
            case ManifestPackage.VIRTUAL_DATABASE__NON_MODELS:
                getNonModels().clear();
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
            case ManifestPackage.VIRTUAL_DATABASE__SEVERITY:
                return severity != SEVERITY_EDEFAULT;
            case ManifestPackage.VIRTUAL_DATABASE__MARKERS:
                return markers != null && !markers.isEmpty();
            case ManifestPackage.VIRTUAL_DATABASE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case ManifestPackage.VIRTUAL_DATABASE__IDENTIFIER:
                return IDENTIFIER_EDEFAULT == null ? identifier != null : !IDENTIFIER_EDEFAULT.equals(identifier);
            case ManifestPackage.VIRTUAL_DATABASE__UUID:
                return UUID_EDEFAULT == null ? uuid != null : !UUID_EDEFAULT.equals(uuid);
            case ManifestPackage.VIRTUAL_DATABASE__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
            case ManifestPackage.VIRTUAL_DATABASE__VERSION:
                return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
            case ManifestPackage.VIRTUAL_DATABASE__PROVIDER:
                return PROVIDER_EDEFAULT == null ? provider != null : !PROVIDER_EDEFAULT.equals(provider);
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED:
                return TIME_LAST_CHANGED_EDEFAULT == null ? timeLastChanged != null : !TIME_LAST_CHANGED_EDEFAULT.equals(timeLastChanged);
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED:
                return TIME_LAST_PRODUCED_EDEFAULT == null ? timeLastProduced != null : !TIME_LAST_PRODUCED_EDEFAULT.equals(timeLastProduced);
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_CHANGED_AS_DATE:
                return TIME_LAST_CHANGED_AS_DATE_EDEFAULT == null ? getTimeLastChangedAsDate() != null : !TIME_LAST_CHANGED_AS_DATE_EDEFAULT.equals(getTimeLastChangedAsDate());
            case ManifestPackage.VIRTUAL_DATABASE__TIME_LAST_PRODUCED_AS_DATE:
                return TIME_LAST_PRODUCED_AS_DATE_EDEFAULT == null ? getTimeLastProducedAsDate() != null : !TIME_LAST_PRODUCED_AS_DATE_EDEFAULT.equals(getTimeLastProducedAsDate());
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_NAME:
                return PRODUCER_NAME_EDEFAULT == null ? producerName != null : !PRODUCER_NAME_EDEFAULT.equals(producerName);
            case ManifestPackage.VIRTUAL_DATABASE__PRODUCER_VERSION:
                return PRODUCER_VERSION_EDEFAULT == null ? producerVersion != null : !PRODUCER_VERSION_EDEFAULT.equals(producerVersion);
            case ManifestPackage.VIRTUAL_DATABASE__INCLUDE_MODEL_FILES:
                return includeModelFiles != INCLUDE_MODEL_FILES_EDEFAULT;
            case ManifestPackage.VIRTUAL_DATABASE__MODELS:
                return models != null && !models.isEmpty();
            case ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS:
                return wsdlOptions != null;
            case ManifestPackage.VIRTUAL_DATABASE__NON_MODELS:
                return nonModels != null && !nonModels.isEmpty();
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
        result.append(", identifier: "); //$NON-NLS-1$
        result.append(identifier);
        result.append(", uuid: "); //$NON-NLS-1$
        result.append(uuid);
        result.append(", description: "); //$NON-NLS-1$
        result.append(description);
        result.append(", version: "); //$NON-NLS-1$
        result.append(version);
        result.append(", provider: "); //$NON-NLS-1$
        result.append(provider);
        result.append(", timeLastChanged: "); //$NON-NLS-1$
        result.append(timeLastChanged);
        result.append(", timeLastProduced: "); //$NON-NLS-1$
        result.append(timeLastProduced);
        result.append(", producerName: "); //$NON-NLS-1$
        result.append(producerName);
        result.append(", producerVersion: "); //$NON-NLS-1$
        result.append(producerVersion);
        result.append(", includeModelFiles: "); //$NON-NLS-1$
        result.append(includeModelFiles);
        result.append(')');
        return result.toString();
    }

    @Override
    protected Severity calculateSeverity(Severity initialSeverity) {
        Severity severity = super.calculateSeverity(initialSeverity);
        
        // Iterate over the contained model references and update them ...
        final Iterator iter = this.eContents().iterator();
        while (iter.hasNext()) {
            final Object contained = iter.next();
            if ( contained instanceof ProblemMarkerContainer ) {
                final Severity containerSeverity = ((ProblemMarkerContainer)contained).getSeverity();
                if ( containerSeverity.getValue() > severity.getValue() ) {
                    severity = containerSeverity;
                }
            }
            
        }
        
        return severity;
    }
} //VirtualDatabaseImpl
