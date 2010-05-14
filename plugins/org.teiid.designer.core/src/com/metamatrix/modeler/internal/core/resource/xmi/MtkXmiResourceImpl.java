/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource.xmi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import com.metamatrix.common.xmi.XMIHeader;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.Identifiable;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ObjectManager;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.resource.XResource;
import com.metamatrix.modeler.core.resource.XmlXResourceDelegate;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSet;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil;

/**
 * This class extends the XMIResourceImpl class to provide the capability to account for UUIDs (which is explicitly ignored in
 * XMIResourceImpl). In addition the load methods are overridden to provide hooks into our loading mechanisms.
 * 
 * @author Lance Phillips
 * @since 3.1
 */
public class MtkXmiResourceImpl extends XMIResourceImpl implements EmfResource, XResource {

    public static final char UUID_PROTOCOL_DELIMITER = '/';

    /**
     * The map from {@link EObject} to {@link #getUuid UUID}. It is used to store UUIDs for objects that have been detached.
     */
    public static final Map<EObject, String> DETACHED_EOBJECT_TO_UUID_MAP = XMLResourceImpl.DETACHED_EOBJECT_TO_ID_MAP;
    public static final Map<String, EObject> DETACHED_UUID_TO_EOBJECT_MAP = Collections.synchronizedMap(new WeakHashMap<String, EObject>());

    // The Container, MetamodelRegistry and ProxiedObjectManager instances; may be null
    private Container container;
    private MetamodelRegistry registry;
    private ObjectManager objectManager;

    private List prefixesToURIs;

    private ModelContents modelContents;

    private XmlXResourceDelegate delegate = new XmlXResourceDelegate();

    /**
     * Constructor for MtkXMIResourceImpl.
     * 
     * @param uri
     */
    public MtkXmiResourceImpl( final URI uri ) {
        super(uri);
        if (uri == null) {
            throw new IllegalArgumentException(
                                               ModelerCore.Util.getString("MtkXmiResourceImpl.The_URI_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        this.modelContents = new ModelContents(this);
        this.prefixesToURIs = new ArrayList();

        delegate.initialize(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#attachedHelper(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected void attachedHelper( EObject eObject ) {
        if (isTrackingModification()) {
            eObject.eAdapters().add(modificationTrackingAdapter);
        }
        delegate.attachedHelper(this, eObject);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#detachedHelper(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected void detachedHelper( EObject eObject ) {
        delegate.detachedHelper(this, eObject);
        if (isTrackingModification()) {
            eObject.eAdapters().remove(modificationTrackingAdapter);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#isAttachedDetachedHelperRequired()
     */
    @Override
    protected boolean isAttachedDetachedHelperRequired() {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.XResource#isLoading()
     * @since 5.0.3
     */
    @Override
    public boolean isLoading() {
        return delegate.isLoading();
    }

    /**
     * @see com.metamatrix.modeler.core.resource.XResource#isUnloading()
     * @since 5.0.3
     */
    public boolean isUnloading() {
        return delegate.isUnloading();
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource#load(Map)
     */
    @Override
    public void load( final Map options ) throws IOException {
        if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.MtkXMIResourceImpl.load(Map)_1")); //$NON-NLS-1$
        }

        // make sure to replace any existing ref to the model contents helper ...
        this.modelContents = new ModelContents(this);

        // If the target of the load operation can be scanned, then verify that
        // it represents a valid Teiid Designer model file
        if (this.uri != null && this.uri.isFile()) {
            File f = new File(this.uri.toFileString());
            if (f.exists()) {
                XMIHeader header = ModelFileUtil.getXmiHeader(f);
                // If the file is an XMI 1.x version file then we cannot process it
                if (header != null && header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) { //$NON-NLS-1$
                    Object[] params = new Object[] {this.uri};
                    String msg = ModelerCore.Util.getString("MtkXmiResourceImpl.The_file,_0,_is_an_older_model_format_that_must_be_converted._1", params); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                    return;
                }
            }
        }

        // Get current txn : loads should be performed in txn due to all of the setValues that occur directly
        // on ENotifyingLists. Lists are not proxies and thus will not create their own declarative txn via
        // interceptor stack. However, they still try to process their notifications which causes an error since
        // they are not in a txn. 6/4/03 LLP
        UnitOfWork txn = null;
        boolean selfStarted = false;
        if (container != null) {
            txn = container.getEmfTransactionProvider().getCurrent();
            if (!txn.isStarted()) {
                try {
                    txn.begin();
                } catch (ModelerCoreException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
                selfStarted = true;
            }
        }

        // If the XMI file currently being read has a reference to a URI
        // that is in the metamodel registry then force a load of this metamodel
        final URI uri = super.getURI();
        final MetamodelRegistry registry = this.getMetamodelRegistry();
        if (registry != null && registry.containsURI(uri)) {
            final Resource r = this.registry.getResource(uri);
            CoreArgCheck.isTrue(r.isLoaded(), "Resource " + r.getURI() + " must be loaded"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Otherwise load the resource using it's URI
        if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.Loading_model_using_URI_3", new Object[] {uri})); //$NON-NLS-1$
        }
        super.load(options);

        // commit the txn if we started it.
        if (selfStarted) {
            try {
                txn.commit();
            } catch (ModelerCoreException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }

        if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.Returning_from_MtkXMIResourceImpl.load(Map)_1")); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl#doLoad(InputStream, Map)
     */
    @Override
    public void doLoad( final InputStream inputStream,
                        final Map options ) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException(
                                               ModelerCore.Util.getString("MtkXmiResourceImpl.The_InputStream_reference_may_not_be_null_3")); //$NON-NLS-1$
        }
        if (delegate.isLoading()) {
            return;
        }
        delegate.setLoading(true);
        try {
            if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
                ModelerCore.Util.log(IStatus.INFO,
                                     ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.MtkXMIResourceImpl.doLoad(InputStream,Map)_4")); //$NON-NLS-1$
            }

            // make sure to replace any existing ref to the model contents helper ...
            this.modelContents = new ModelContents(this);

            // Get current txn : loads should be performed in txn due to all of the setValues that occur directly
            // on ENotifyingLists. Lists are not proxies and thus will not create their own declarative txn via
            // interceptor stack. However, they still try to process their notifications which causes an error since
            // they are not in a txn. 6/4/03 LLP
            UnitOfWork txn = null;
            boolean selfStarted = false;
            if (container != null) {
                txn = container.getEmfTransactionProvider().getCurrent();
                if (!txn.isStarted()) {
                    try {
                        txn.begin();
                    } catch (ModelerCoreException e) {
                        ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                    }
                    selfStarted = true;
                }
            }

            XMLHelper xmiHelper = createXMLHelper();
            MtkXmiResourceLoader loader = new MtkXmiResourceLoader(xmiHelper, getContainer());
            loader.load(this, inputStream, options == null ? Collections.EMPTY_MAP : options);
            // Loop through contents to ensure even transient objects created by EMF during the load have a UUID.
            // This is a very inefficient way of handling this problem, but no other reasonable solution is currently apparent.
            for (Iterator iter = getAllContents(); iter.hasNext();) {
                EObject eObject = (EObject)iter.next();
                if (getUuid(eObject) == null) {
                    String uuid = MtkXmiResourceImpl.DETACHED_EOBJECT_TO_UUID_MAP.remove(eObject);
                    if (uuid == null) {
                        uuid = IDGenerator.getInstance().create().toString();
                    } else {
                        MtkXmiResourceImpl.DETACHED_UUID_TO_EOBJECT_MAP.remove(uuid);
                    }
                    setID(eObject, uuid);
                }
            }
            if (xmiHelper instanceof MtkXmiHelper) {
                this.prefixesToURIs = ((MtkXmiHelper)xmiHelper).getPrefixesToURIs();
            }

            // commit the txn if we started it.
            if (selfStarted) {
                try {
                    txn.commit();
                } catch (ModelerCoreException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }

            if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
                ModelerCore.Util.log(IStatus.INFO,
                                     ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.Returning_from_MtkXMIResourceImpl.doLoad(InputStream,Map)_2")); //$NON-NLS-1$
            }
            // testUuidToEObjectMap("--> doLoad "+this.getURI()); //$NON-NLS-1$
        } finally {
            delegate.setLoading(false);
        }
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doSave(java.io.OutputStream, java.util.Map)
     */
    @Override
    public void doSave( OutputStream outputStream,
                        Map options ) throws IOException {
        // Check that the model annotation still exists in the resource and is at element 0...
        ModelAnnotation modelAnnotation = null;
        if (this.modelContents != null && this.modelContents.getModelAnnotation() != null) {
            modelAnnotation = this.modelContents.getModelAnnotation();
            final Resource resource = modelAnnotation.eResource();
            if (resource == null) {
                // It was somehow yanked, but we always want to keep the ModelAnnotation
                this.getContents().add(0, modelAnnotation);
            }
        }

        if (modelAnnotation != null && !(this.getContents().get(0) instanceof ModelAnnotation)) {
            this.getContents().move(0, modelAnnotation);
        }

        // Set the product name and version information on the ModelAnnotation
        if (modelAnnotation != null) {
            modelAnnotation.setProducerName(ModelerCore.ILicense.PRODUCER_NAME);
            modelAnnotation.setProducerVersion(ModelerCore.ILicense.VERSION);
        }

        // Set the XML save option for dangling hrefs to RECORD. The default option for this
        // is THROW which will save the rest of the model but throws an exception at the end.
        // The option of RECORD will save the model, add the expection of the resource's
        // error list, but will not throw an exception.
        final Map saveOptions = options == null ? new HashMap() : new HashMap(options);
        saveOptions.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

        super.doSave(outputStream, saveOptions);
        // testUuidToEObjectMap("--> doSave "+this.getURI()); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLHelper()
     */
    @Override
    protected XMLHelper createXMLHelper() {
        return new MtkXmiHelper();
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#doUnload()
     */
    @Override
    protected void doUnload() {
        if (delegate.isUnloading()) {
            return;
        }
        delegate.setUnloading(true);
        try {
            super.doUnload();

            setModified(false);
            this.modelContents = null;
            // testUuidToEObjectMap("--> doUnload "+this.getURI()); //$NON-NLS-1$
        } finally {
            delegate.setUnloading(false);
        }
    }

    /**
     * Records the UUID of the EObject instance. The implementation updates the {@link #uuidToEObjectMap}. If recursive is true,
     * the method will traverse the contents of this EObject recording their UUIDs in the map
     * 
     * @param eObject the object.
     * @param recursive whether to recursively record the UUIDs of all contents of this EObject by calling
     *        EObject..eAllContents().
     */
    void ensureUuid( final EObject eObject,
                     final boolean recursive ) {
        if (eObject == null) {
            throw new IllegalArgumentException(
                                               ModelerCore.Util.getString("MtkXmiResourceImpl.The_EObject_reference_may_not_be_null")); //$NON-NLS-1$
        }

        String uuid = getID(eObject);

        if (uuid == null) {
            setID(eObject, IDGenerator.getInstance().create().toString());

            if (recursive) {
                for (final Iterator iter = eObject.eContents().iterator(); iter.hasNext();) {
                    final Object next = iter.next();
                    if (next instanceof EObject) {
                        final EObject nextEObj = (EObject)next;
                        ensureUuid(nextEObj, recursive);
                    }
                }
            }
        }
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource.Internal#attached(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public void attached( EObject eObject ) {
        ensureUuid(eObject, true);
        super.attached(eObject);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLSave()
     */
    @Override
    protected XMLSave createXMLSave() {
        return new MtkXmiSaveImpl(super.createXMLHelper(), this);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource#getContents()
     */
    @Override
    public EList getContents() {
        if (contents == null) {
            contents = new MtkContentsEList(this);
        }
        return contents;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getDescription()
     * @since 4.3
     */
    public String getDescription() {
        String description = null;
        if (isLoaded() && getModelContents().getModelAnnotation() != null) {
            description = getModelContents().getModelAnnotation().getDescription();
        } else {
            XMIHeader header = doGetXmiHeader();
            if (header != null) {
                description = header.getDescription();
            }
        }
        return description;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getModelType()
     * @since 4.3
     */
    public ModelType getModelType() {
        ModelType type = null;
        if (isLoaded() && getModelContents().getModelAnnotation() != null) {
            type = getModelContents().getModelAnnotation().getModelType();
        } else {
            XMIHeader header = doGetXmiHeader();
            if (header != null) {
                type = ModelType.get(header.getModelType());
            }
        }
        return type;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getPrimaryMetamodelUri()
     * @since 4.3
     */
    public URI getPrimaryMetamodelUri() {
        String primaryMetamodelUri = null;
        if (isLoaded() && getModelContents().getModelAnnotation() != null) {
            primaryMetamodelUri = getModelContents().getModelAnnotation().getPrimaryMetamodelUri();
        } else {
            XMIHeader header = doGetXmiHeader();
            if (header != null) {
                primaryMetamodelUri = header.getPrimaryMetamodelURI();
            }
        }
        if (CoreStringUtil.isEmpty(primaryMetamodelUri)) {
            return null;
        }
        return URI.createURI(primaryMetamodelUri);
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#isVisible()
     * @since 5.0
     */
    public boolean isVisible() {
        boolean isVisible = true;
        if (isLoaded() && getModelContents().getModelAnnotation() != null) {
            isVisible = getModelContents().getModelAnnotation().isVisible();
        } else {
            XMIHeader header = doGetXmiHeader();
            if (header != null) {
                isVisible = header.isVisible();
            }
        }
        return isVisible;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getUuid()
     * @since 4.3
     */
    public ObjectID getUuid() {
        ObjectID uuid = null;
        if (isLoaded() && getModelContents().getModelAnnotation() != null) {
            uuid = getObjectIDFromString(getID(getModelAnnotation()));
        } else {
            XMIHeader header = doGetXmiHeader();
            if (header != null && header.getUUID() != null) {
                uuid = getObjectIDFromString(header.getUUID());
            }
        }
        return uuid;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.resource.XResource#getUuid(org.eclipse.emf.ecore.EObject)
     */
    public String getUuid( EObject object ) {
        return getID(object);
    }

    protected XMIHeader doGetXmiHeader() {
        XMIHeader header = null;
        if (getURI().isFile()) {
            File f = new File(getURI().toFileString());
            if (f.exists()) {
                header = ModelFileUtil.getXmiHeader(f);
            }
        }
        return header;
    }

    protected class MtkContentsEList extends XMIResourceImpl.ContentsEList {

        private static final long serialVersionUID = 1L;
        private final MtkXmiResourceImpl owner;

        public MtkContentsEList( final MtkXmiResourceImpl owner ) {
            this.owner = owner;
        }

        @Override
        protected void didChange() {
            MtkXmiResourceImpl.this.setModified(true);
        }

        /**
         * @see java.util.Collection#addAll(java.util.Collection)
         */
        @Override
        public boolean removeAll( final Collection c ) {
            if (c == null || c.isEmpty()) {
                return false;
            }

            final boolean startTxn = getTxn();
            try {
                final EList vals = new BasicEList(c);
                final int[] removedIndexes = getIndexes(vals);

                owner.removeMany(c);

                if (vals.size() == 1) {
                    eNotify(createNotification(Notification.REMOVE, vals.get(0), null, removedIndexes[0]));
                } else {
                    eNotify(createNotification(Notification.REMOVE_MANY, vals, removedIndexes, removedIndexes[0]));
                }

                return true;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        private int[] getIndexes( final Collection vals ) {
            final BasicEList tmp = new BasicEList(this);
            final int[] result = new int[vals.size()];
            final Iterator it = tmp.iterator();
            int count = 0;
            int index = 0;
            while (it.hasNext()) {
                Object next = it.next();
                if (vals.contains(next)) {
                    result[count++] = index;
                }

                index++;
            }

            return result;
        }

        /**
         * @see java.util.Collection#addAll(java.util.Collection)
         */
        @Override
        public boolean addAll( final Collection c ) {
            if (c == null || c.isEmpty()) {
                return false;
            }

            final boolean startTxn = getTxn();
            try {
                // super.addAll(c);
                final int index = size;
                owner.addMany(c);
                final EList vals = new BasicEList(c);
                eNotify(createNotification(Notification.ADD_MANY, null, vals, index, true));
                return true;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see java.util.List#add(int, java.lang.Object)
         */
        @Override
        public void add( final int index,
                         final Object element ) {
            final boolean startTxn = getTxn();
            try {
                super.add(index, element);
                attachToResource(element);
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }

        }

        /**
         * @see java.util.Collection#add(java.lang.Object)
         */
        @Override
        public boolean add( final Object o ) {
            final boolean startTxn = getTxn();
            try {
                final boolean result = super.add(o);
                attachToResource(o);
                return result;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }

        }

        /**
         * @see java.util.List#addAll(int, java.util.Collection)
         */
        @Override
        public boolean addAll( final int index,
                               final Collection c ) {
            final boolean startTxn = getTxn();
            try {
                final boolean result = super.addAll(index, c);
                attachToResource(c);
                return result;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see java.util.Collection#remove(java.lang.Object)
         */
        @Override
        public boolean remove( final Object o ) {
            final boolean startTxn = getTxn();
            try {
                return super.remove(o);
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.NotifyingListImpl#inverseAdd(java.lang.Object,
         *      org.eclipse.emf.common.notify.NotificationChain)
         */
        @Override
        public NotificationChain inverseAdd( final Object object,
                                             final NotificationChain notifications ) {
            if (object == null) {
                return notifications;
            }
            final boolean startTxn = getTxn();
            try {
                final NotificationChain result = super.inverseAdd(object, notifications);
                attachToResource(object);
                return result;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.NotifyingListImpl#inverseRemove(java.lang.Object,
         *      org.eclipse.emf.common.notify.NotificationChain)
         */
        @Override
        public NotificationChain inverseRemove( final Object object,
                                                final NotificationChain notifications ) {
            if (object == null) {
                return notifications;
            }
            final boolean startTxn = getTxn();
            try {
                return super.inverseRemove(object, notifications);
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.util.BasicEList#addAllUnique(java.util.Collection)
         */
        @Override
        public boolean addAllUnique( final Collection collection ) {
            final boolean startTxn = getTxn();
            try {
                final boolean result = super.addAllUnique(collection);
                attachToResource(collection);
                return result;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.util.BasicEList#addAllUnique(int, java.util.Collection)
         */
        @Override
        public boolean addAllUnique( final int index,
                                     final Collection collection ) {
            final boolean startTxn = getTxn();
            try {
                final boolean result = super.addAllUnique(index, collection);
                attachToResource(collection);
                return result;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.util.BasicEList#addUnique(int, java.lang.Object)
         */
        @Override
        public void addUnique( final int index,
                               final Object object ) {
            final boolean startTxn = getTxn();
            try {
                super.addUnique(index, object);
                attachToResource(object);
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.util.BasicEList#addUnique(java.lang.Object)
         */
        @Override
        public void addUnique( final Object object ) {
            final boolean startTxn = getTxn();
            try {
                super.addUnique(object);
                attachToResource(object);
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.NotifyingListImpl#basicAdd(java.lang.Object,
         *      org.eclipse.emf.common.notify.NotificationChain)
         */
        @Override
        public NotificationChain basicAdd( final Object object,
                                           final NotificationChain notifications ) {
            final boolean startTxn = getTxn();
            try {
                final NotificationChain result = super.basicAdd(object, notifications);
                attachToResource(object);
                return result;
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.notify.impl.NotifyingListImpl#basicRemove(java.lang.Object,
         *      org.eclipse.emf.common.notify.NotificationChain)
         */
        @Override
        public NotificationChain basicRemove( final Object object,
                                              final NotificationChain notifications ) {
            final boolean startTxn = getTxn();
            try {
                return super.basicRemove(object, notifications);
            } finally {
                if (startTxn) {
                    commitTxn();
                }
            }
        }

        void attachToResource( final Object object ) {
            if (object instanceof Identifiable) {
                // Identifiable instances represent SimpleDatatype instances
                // found in the deprecated SDT metamodel
                String uuidString = ((Identifiable)object).getUuid();
                if (uuidString != null && object instanceof EObject) {
                    EObject eObject = (EObject)object;
                    ModelerCore.setObjectId(eObject, uuidString);
                    ensureUuid(eObject, true);
                }
            } else if (object instanceof EObject) {
                final EObject eObj = (EObject)object;
                ensureUuid(eObj, true);
            } else {
                if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.Attaching_non-proxy_object_5", new Object[] {object})); //$NON-NLS-1$
                }
            }
        }

        private boolean getTxn() {
            return ModelerCore.startTxn(null, this);
        }

        private void commitTxn() {
            ModelerCore.commitTxn();
        }

    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getModelAnnotation()
     */
    public ModelAnnotation getModelAnnotation() {
        if (this.modelContents != null) {
            return this.modelContents.getModelAnnotation();
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getModelContents()
     */
    public ModelContents getModelContents() {
        if (this.modelContents == null) {
            this.modelContents = new ModelContents(this);
        }
        return this.modelContents;
    }

    public List getNamespacePrefixToUris() {
        return this.prefixesToURIs;
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource#getURIFragment(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getURIFragment( final EObject eObject ) {
        String uuid = getID(eObject);
        return (uuid == null ? null : uuid.replace(ObjectID.DELIMITER, UUID_PROTOCOL_DELIMITER));
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#getEObjectForURIFragmentRootSegment(java.lang.String)
     */
    @Override
    protected EObject getEObjectForURIFragmentRootSegment( final String uriFragmentRootSegment ) {
        // See if the string is an ObjectID ...
        final ObjectID id = getObjectIDFromString(uriFragmentRootSegment);
        if (id != null) {
            final EObject result = this.getEObject(id);
            if (result != null) {
                return result;
            }
        }
        return super.getEObjectForURIFragmentRootSegment(uriFragmentRootSegment);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource#getEObject(java.lang.String)
     */
    @Override
    public EObject getEObject( final String uriFragment ) {
        if (uriFragment != null) {
            // See if the string is an ObjectID ...
            final ObjectID id = getObjectIDFromString(uriFragment);
            if (id != null) {
                // Lookup the UUID in the resource
                return this.getEObject(id);
            }
            return super.getEObject(uriFragment);
        }
        return null;
    }

    /**
     * Override the XMLResourceImpl.getEObjectByID implementation to fix defect 12085 and prevent any
     * ConcurrentModificationException that may occur when using the getAllContents() TreeIterator.
     * 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#getEObjectByID(java.lang.String)
     * @since 4.2
     */
    @Override
    protected EObject getEObjectByID( String id ) {
        return idToEObjectMap.get(id);
    }

    /**
     * @see com.metamatrix.mtk.emf.resource.EmfResource#getContainer()
     */
    public Container getContainer() {
        if (this.container == null) {
            final ResourceSet resourceSet = super.resourceSet;
            if (resourceSet == null) {
                final String msg = ModelerCore.Util.getString("MtkXmiResourceImpl.The_ResourceSet_reference_may_not_be_null_4"); //$NON-NLS-1$
                throw new AssertionError(msg);
            }
            if (resourceSet instanceof EmfResourceSet) {
                this.container = ((EmfResourceSet)resourceSet).getContainer();
                if (this.container == null) {
                    final String msg = ModelerCore.Util.getString("MtkXmiResourceImpl.MtkXmiResourceImpl.The_Container_reference_may_not_be_null_5"); //$NON-NLS-1$
                    throw new AssertionError(msg);
                }
            }
            if (this.container == null) {
                if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.The_Container_reference_is_null_for_this_MtkXmiResourceImpl_6")); //$NON-NLS-1$
                }
            }
        }
        return this.container;
    }

    public ObjectManager getObjectManager() {
        if (this.objectManager == null) {
            final Container ctnr = this.getContainer(); // may load/create/initialize it
            if (ctnr instanceof ContainerImpl) {
                this.objectManager = ((ContainerImpl)ctnr).getObjectManager();
            } else {
                this.objectManager = doGetDefaultObjectManager();
            }
        }
        return this.objectManager;
    }

    protected ObjectManager doGetDefaultObjectManager() {
        return new ObjectManager() {
            public String getObjectId( EObject object ) {
                return null;
            }

            public void setObjectId( EObject object,
                                     String uuid ) {
            }

            public EObject findEObject( String id ) {
                return null;
            }

            public EObject findEObject( String id,
                                        Resource resource ) {
                return null;
            }

        };
    }

    /**
     * @see com.metamatrix.mtk.emf.resource.EmfResource#getEObject(Object)
     */
    public EObject getEObject( final Object object ) {
        if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.MtkXmiResourceImpl.getEObject()_7", new Object[] {object})); //$NON-NLS-1$
        }

        if (object instanceof URI) {
            return getResourceSet().getEObject(uri, true);
        } else if (object instanceof ObjectID) {
            return getEObjectByID(((ObjectID)object).toString());
        }

        throw new ModelerCoreRuntimeException(
                                              ModelerCore.Util.getString("MtkXmiResourceImpl.MtkXmiResourceImpl.getEObject()_invalid_param.__Key_must_be_a_Proxy,_a_URI,_or_an_ObjectID_6", new Object[] {object})); //$NON-NLS-1$
    }

    /**
     * This is strictly a performance method. It should only be used when adding large numbers of new root Objects as the
     * EContentsList is modified directly in this method to avoid the overhead of all the EObject callbacks.
     * 
     * @param newRoots to add to this Resource.
     */
    public void addMany( final Collection newRoots ) {
        final Collection newRootsToAdd = new ArrayList(newRoots);
        final Collection allRoots = new ArrayList(this.getContents());
        allRoots.addAll(newRootsToAdd);

        final Object[] rootArray = allRoots.toArray();

        // Reset data object for Contents List.
        // NOTE : This is not considered a safe opperation as no callbacks are executed for
        // the added objects. See java doc for BasicEList.setData for additional details.
        ((BasicEList)this.getContents()).setData(rootArray.length, rootArray);

        // Attach all these objects to this resource
        for (Iterator iter = newRootsToAdd.iterator(); iter.hasNext();) {
            Object object = iter.next();
            ((MtkContentsEList)this.contents).attachToResource(object);

            if (object instanceof BasicEObjectImpl) {
                ((BasicEObjectImpl)object).eSetResource(this, null);
            }
        }

        // Performing the attach to resource and eSetResource is sufficient to ensure
        // that the Eobjects get added to the ObjectManager
        // //Add all the objects to the object manager
        // getObjectManager().processMassAdd(newRoots,this);
    }

    /**
     * This is strictly a performance method. It should only be used when removing large numbers of root Objects as the
     * EContentsList is modified directly in this method to avoid the overhead of all the EObject callbacks.
     * 
     * @param roots to remove from this Resource.
     */
    public void removeMany( final Collection roots ) {
        final Collection allRoots = new ArrayList(this.getContents());
        allRoots.removeAll(roots);

        final Object[] rootArray = allRoots.toArray();

        // Reset data object for Contents List.
        // NOTE : This is not considered a safe opperation as no callbacks are executed for
        // the added objects. See java doc for BasicEList.setData for additional details.
        ((BasicEList)this.getContents()).setData(rootArray.length, rootArray);

        // Detach from Resource and eSetResource are sufficent to ensure that
        // the EObject is removed from the ObjectManager.
        // //Remove all the objects from the object manager
        // getObjectManager().processMassRemove(roots);
        //
        // Remove all these objects to this resource
        for (Iterator iter = roots.iterator(); iter.hasNext();) {
            Object object = iter.next();
            if (object instanceof EObjectImpl) {
                ((EObjectImpl)object).eSetResource(null, null);
            }
        }
    }

    /**
     * Returns the {@link MetamodelRegistry} instance associated with this resource.
     * 
     * @return MetamodelRegistry; may be null
     */
    public MetamodelRegistry getMetamodelRegistry() {
        if (this.registry == null) {
            Container emfContainer = this.getContainer();
            if (container != null) {
                this.registry = emfContainer.getMetamodelRegistry();
                if (this.registry == null) {
                    final String msg = ModelerCore.Util.getString("MtkXmiResourceImpl.The_MetamodelRegistry_reference_may_not_be_null_7"); //$NON-NLS-1$
                    throw new AssertionError(msg);
                }
            }
            if (this.registry == null) {
                if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
                    ModelerCore.Util.log(IStatus.INFO,
                                         ModelerCore.Util.getString("MtkXmiResourceImpl.DEBUG.The_MetamodelRegistry_reference_is_null_for_this_MtkXmiResourceImpl_8")); //$NON-NLS-1$
                }
            }
        }
        return this.registry;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.MMXmiResource#getAnnotation(org.eclipse.emf.ecore.EObject)
     */
    public Annotation getAnnotation( EObject eobj ) {
        return getModelContents().getAnnotation(eobj);
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.MMXmiResource#getAnnotationContainer(boolean)
     */
    public AnnotationContainer getAnnotationContainer( boolean createIfNeeded ) {
        return getModelContents().getAnnotationContainer(createIfNeeded);
    }

    protected EObject resolveEObject( final ObjectID id ) {
        if (id == null) {
            throw new IllegalArgumentException(
                                               ModelerCore.Util.getString("MtkXmiResourceImpl.The_ObjectID_reference_may_not_be_null")); //$NON-NLS-1$
        }
        return getEObjectByID(id.toString());
    }

    private ObjectID getObjectIDFromString( final String uuidString ) {
        if (uuidString == null || uuidString.length() == 0) {
            return null;
        }
        try {
            return IDGenerator.getInstance().stringToObject(uuidString);
        } catch (InvalidIDException e) {
            // do nothing ...
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.resource.XResource#setUuid(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    public void setUuid( EObject object,
                         String uuid ) {
        super.setID(object, uuid);
    }

    /**
     * Test method to validate the internal uuidToEObjectMap. The method validates if the map contains an entry for every EObject
     * instance currently in the modeler
     * 
     * @param description
     * @since 4.2
     */
    public void testUuidToEObjectMap( final String description ) {
        int missingMapEntryCount = 0;
        int eObjectCount = 0;
        for (final Iterator iter = this.getContents().iterator(); iter.hasNext();) {
            final EObject eObject = (EObject)iter.next();
            if (eObject != null) {
                eObjectCount++;
                ObjectID id = ModelerCore.getObjectId(eObject);
                if (id == null || getEObjectByID(id.toString()) == null) {
                    missingMapEntryCount++;
                }
                for (final Iterator iter2 = eObject.eAllContents(); iter2.hasNext();) {
                    final EObject eObj = (EObject)iter2.next();
                    if (eObj != null) {
                        eObjectCount++;
                        id = ModelerCore.getObjectId(eObj);
                        if (id == null || getEObjectByID(id.toString()) == null) {
                            missingMapEntryCount++;
                        }
                    }
                }
            }
        }
        ModelerCore.Util.log(IStatus.INFO, description);
        ModelerCore.Util.log(IStatus.INFO, "Number of EObject instances in model           = " + eObjectCount); //$NON-NLS-1$
        ModelerCore.Util.log(IStatus.INFO, "Number of entries in the map                   = " + this.idToEObjectMap.size()); //$NON-NLS-1$
        final int severity = (missingMapEntryCount == 0 ? IStatus.INFO : IStatus.ERROR);
        ModelerCore.Util.log(severity, "Number of EObject instances missing in the map = " + missingMapEntryCount); //$NON-NLS-1$
    }
}
