/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.internal.core.xml.xmi.XMIHeaderReader;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.resource.EObjectCache;
import com.metamatrix.modeler.core.resource.EObjectCacheHolder;
import com.metamatrix.modeler.core.resource.EProxyCacheHolder;
import com.metamatrix.modeler.core.resource.EResource;
import com.metamatrix.modeler.core.resource.XResource;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelObjectCollector;

/**
 * This class extends the XMIResourceImpl class to provide the capability to account for UUIDs (which is explicitly ignored in
 * XMIResourceImpl). In addition the load methods are overridden to provide hooks into our loading mechanisms.
 * 
 * @since 5.0
 */
public class EResourceImpl extends XMIResourceImpl
    implements EResource, EProxyCacheHolder, EObjectCacheHolder, EmfResource, XResource {

    public static final char UUID_PROTOCOL_DELIMITER = '/';

    static EObject[] EMPTY_EOBJECT_ARRAY = new EObject[0];

    private static final boolean DEBUG = false;

    private Map prefixesToURIs;
    private ModelContents modelContents;
    private int loadedCount;

    /**
     * Cache to hold eProxys while the resource is unloaded. The eProxys are reused when the resource is reloaded. At the end of
     * the loading the eProxyCache is cleared.
     */
    private EObjectCache eProxyCache;
    /**
     * Cache to hold EObjects while the resource is loading. The contents of this cache are handed off to the EResourceSet's
     * EObjectManager (if it exists) when loading of this resource hass completed. The eObjectCache is then cleared.
     */
    private EObjectCache eObjectCache;

    /**
     * Constructor for EResourceImpl.
     * 
     * @param uri
     */
    public EResourceImpl( final URI uri ) {
        super(uri);
        ArgCheck.isNotNull(uri);

        this.loadedCount = 0;
        this.modelContents = new ModelContents(this);
        this.prefixesToURIs = new HashMap();
        this.eProxyCache = new EObjectCacheImpl();
        this.eObjectCache = new EObjectCacheImpl();

        // Add EContentAdapter instances to monitor changes to the contents
        eAdapters().add(new EObjectCacheAdapter());
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLHelper()
     */
    @Override
    protected XMLHelper createXMLHelper() {
        return new EResourceXmiHelper(this);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLSave()
     */
    @Override
    protected XMLSave createXMLSave() {
        return new EResourceXmiSaveImpl(createXMLHelper(), this);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLLoad()
     * @since 5.0
     */
    @Override
    protected XMLLoad createXMLLoad() {
        return new EResourceLoader(createXMLHelper());
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.MMXmiResource#recordUUID(org.eclipse.emf.ecore.EObject, boolean)
     */
    public void recordUUID( EObject eObject,
                            boolean recursive ) {
        // Do nothing
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

    /**
     * @see org.eclipse.emf.ecore.resource.impl#doLoad(InputStream, Map)
     */
    @Override
    public void doLoad( final InputStream inputStream,
                        final Map options ) throws IOException {
        ArgCheck.isNotNull(inputStream);
        if (DEBUG) {
            ModelerCore.Util.log("EResourceImpl.doLoad(InputStream,Map): Start load " + getURI()); //$NON-NLS-1$
        }

        // Increment the loaded counter that tracks the number of times this resource has been
        // loaded since it's inception. This information is important because if a resource is
        // never loaded then we are unable to find it's EObjects through the eObjectCache,
        // eProxyCache, or EObjectFinder
        this.loadedCount++;

        // // Reset the existing reference to the model contents helper class for the newly loaded resource ...
        // this.modelContents = new ModelContents(this);
        // protoTODO - implement ModelContents

        // Load the resource
        XMLLoad xmlLoad = createXMLLoad();
        xmlLoad.load(this, inputStream, options);

        // Set the map of prefixes to namespace URIs
        setPrefixToUriMap(xmlLoad);

        // Clear the eProxy cache since the resource is now loaded
        getEProxyCache().clear();

        if (DEBUG) {
            ModelerCore.Util.log("EResourceImpl.doLoad(InputStream,Map): eProxyMap.size() = " + getEProxyCache().size()); //$NON-NLS-1$
            ModelerCore.Util.log("EResourceImpl.doLoad(InputStream,Map): End load " + getURI()); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doSave(java.io.OutputStream, java.util.Map)
     */
    @Override
    public void doSave( OutputStream outputStream,
                        Map options ) throws IOException {

        // Check that the model annotation still exists in the resource and is at position 0 in the array...
        moveModelAnnotation();

        // Set product name and version information on the ModelAnnotation
        setProductInfoOnModelAnnotation();

        // Set the XML save option for dangling hrefs to RECORD. The default option for this
        // is THROW which will save the rest of the model but throws an exception at the end.
        // The option of RECORD will save the model, add the expection of the resource's
        // error list, but will not throw an exception.
        // final Map saveOptions = new HashMap(options);
        // saveOptions.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

        super.doSave(outputStream, options);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl#doUnload()
     */
    @Override
    protected void doUnload() {
        if (DEBUG) {
            ModelerCore.Util.log("EResourceImpl.doUnload(): Start unload " + getURI()); //$NON-NLS-1$
        }

        // Collect all the EObject instances in this resource using the
        // ModelObjectCollector class to avoid a ConcurrentModificationException
        // that may occur when using the TreeIterator (i.e. super.getAllContents())
        final ModelObjectCollector moc = new ModelObjectCollector(this);

        // Iterate througth the contents of this resource adding EObjects that
        // are being unloaded and converted into eProxies.
        getEProxyCache().clear();
        for (final Iterator iter = moc.getEObjects().iterator(); iter.hasNext();) {
            addToEProxyCache((EObject)iter.next());
        }

        // Unload the resource
        super.doUnload();

        // Ensure the EObject cache is cleared.
        getEObjectCache().clear();

        setModified(false);
        this.modelContents = null;

        // Unset all feature values to deflate the EObject which is now an eProxy
        for (final Iterator iter = moc.getEObjects().iterator(); iter.hasNext();) {
            EObject eObject = (EObject)iter.next();
            EClass eClass = eObject.eClass();
            for (final Iterator iter2 = eClass.getEAllStructuralFeatures().iterator(); iter2.hasNext();) {
                final EStructuralFeature feature = (EStructuralFeature)iter2.next();
                if (feature.isChangeable()) {
                    eObject.eUnset(feature);
                }
            }
        }

        if (DEBUG) {
            ModelerCore.Util.log("EResourceImpl.doUnload(): eProxyMap.size() = " + getEProxyCache().size()); //$NON-NLS-1$
            ModelerCore.Util.log("EResourceImpl.doUnload(): End unload " + getURI()); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource#getURIFragment(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getURIFragment( final EObject eObject ) {
        if (eObject == null) {
            return null;
        }
        ObjectID id = ModelerCore.getObjectId(eObject);

        return (id == null) ? null : id.toString(UUID_PROTOCOL_DELIMITER);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource#getEObject(java.lang.String)
     */
    @Override
    public EObject getEObject( final String uriFragment ) {
        if (uriFragment != null && uriFragment.startsWith(UUID.PROTOCOL)) {
            final ObjectID id = getObjectIDFromString(uriFragment);
            if (id != null) {
                return findInEObjectCache(id);
            }
        }
        return null;
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#getEObjectByID(java.lang.String)
     */
    @Override
    protected EObject getEObjectByID( String id ) {
        return getEObject(id);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource#getContents()
     */
    @Override
    public EList getContents() {
        if (contents == null) {
            contents = new EResourceContentsEList(this);
        }
        return contents;
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#detached(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public void detached( EObject eObject ) {
        // Remove the EObject and it's contents from the local cache
        removeFromEObjectCache(eObject, true);

        super.detached(eObject);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.Resource.Internal#attached(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public void attached( EObject eObject ) {
        // Add the EObject and it's contents to the local cache
        addToEObjectCache(eObject, true);

        super.attached(eObject);
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getContainer()
     * @since 5.0
     */
    public Container getContainer() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getEObject(java.lang.Object)
     * @since 5.0
     */
    public EObject getEObject( final Object key ) {
        if (key instanceof URI) {
            return getResourceSet().getEObject(uri, true);
        } else if (key instanceof ObjectID) {
            return getEObject((ObjectID)key);
        }
        return null;
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
     * @see com.metamatrix.modeler.internal.core.resource.EmfResource#getNamespacePrefixToUris()
     * @since 5.0
     */
    public List getNamespacePrefixToUris() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EResource#getLoadedCount()
     * @since 5.0
     */
    public int getLoadedCount() {
        return this.loadedCount;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EResource#getDescription()
     * @since 5.0
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
     * @see com.metamatrix.modeler.core.resource.EResource#getModelType()
     * @since 5.0
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
     * @see com.metamatrix.modeler.core.resource.EResource#getPrimaryMetamodelUri()
     * @since 5.0
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
        if (StringUtil.isEmpty(primaryMetamodelUri)) {
            return null;
        }
        return URI.createURI(primaryMetamodelUri);
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EResource#getUuid()
     * @since 5.0
     */
    public ObjectID getUuid() {
        ObjectID uuid = null;
        if (isLoaded() && getModelContents().getModelAnnotation() != null) {
            uuid = ModelerCore.getObjectId(getModelContents().getModelAnnotation());
        } else {
            XMIHeader header = doGetXmiHeader();
            if (header != null && header.getUUID() != null) {
                uuid = getObjectIDFromString(header.getUUID());
            }
        }
        return uuid;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EResource#getModelContents()
     */
    public ModelContents getModelContents() {
        if (this.modelContents == null) {
            this.modelContents = new ModelContents(this);
        }
        return this.modelContents;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EResource#getNamespacePrefixToUrisMap()
     * @since 5.0
     */
    public Map getNamespacePrefixToUrisMap() {
        return this.prefixesToURIs;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCacheHolder#getEObject(com.metamatrix.core.id.ObjectID)
     * @since 5.0
     */
    public EObject getEObject( final ObjectID key ) {
        return findInEObjectCache(key);
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EProxyCacheHolder#getEProxy(com.metamatrix.core.id.ObjectID)
     * @since 5.0
     */
    public EObject getEProxy( final ObjectID key ) {
        return findInEProxyCache(key);
    }

    // dffFIXME - change visibility back to protected after getting rid of EmfResource interface
    // protected ModelAnnotation getModelAnnotation() {
    public ModelAnnotation getModelAnnotation() {
        ModelAnnotation modelAnnotation = null;
        for (Iterator iter = getContents().iterator(); iter.hasNext();) {
            EObject eObj = (EObject)iter.next();
            if (eObj instanceof ModelAnnotation) {
                modelAnnotation = (ModelAnnotation)eObj;
                break;
            }
        }
        return modelAnnotation;
    }

    /**
     * Check that the model annotation still exists in the resource and is at position 0 in the array...
     */
    protected void moveModelAnnotation() {
        ModelAnnotation modelAnnotation = getModelAnnotation();
        if (modelAnnotation != null) {
            final Resource resource = modelAnnotation.eResource();
            if (resource == null) {
                // It was somehow yanked, but we always want to keep the ModelAnnotation
                getContents().add(0, modelAnnotation);
            } else if (!(getContents().get(0) instanceof ModelAnnotation)) {
                getContents().move(0, modelAnnotation);
            }
        }
    }

    /**
     * Set product name and version information on the model annotation
     */
    protected void setProductInfoOnModelAnnotation() {
        ModelAnnotation modelAnnotation = getModelAnnotation();
        if (modelAnnotation != null) {
            modelAnnotation.setProducerName(ModelerCore.ILicense.PRODUCER_NAME);
            modelAnnotation.setProducerVersion(ModelerCore.ILicense.VERSION);
        }
    }

    /**
     * Return the ObjectID instance from the stringified UUID. If the string is null, empty, represents an invalid UUID then null
     * is returned.
     * 
     * @param uuidString
     * @return
     * @since 5.0
     */
    protected ObjectID getObjectIDFromString( final String uuidString ) {
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

    protected XMIHeader doGetXmiHeader( final URI theUri ) {
        XMIHeader header = null;
        if (theUri != null && theUri.isFile()) {
            File f = new File(getURI().toFileString());
            if (f.isFile() && f.exists()) {
                try {
                    header = XMIHeaderReader.readHeader(f);
                } catch (MetaMatrixCoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
        }
        return header;
    }

    protected XMIHeader doGetXmiHeader() {
        return doGetXmiHeader(getURI());
    }

    /**
     * @param xmlLoad
     * @since 5.0
     */
    protected void setPrefixToUriMap( final XMLLoad xmlLoad ) {
        if (xmlLoad instanceof EResourceLoader) {
            XMLHelper xmiHelper = ((EResourceLoader)xmlLoad).getXMLHelper();
            if (xmiHelper instanceof EResourceXmiHelper) {
                this.prefixesToURIs.clear();
                this.prefixesToURIs = ((EResourceXmiHelper)xmiHelper).getPrefixesToURIs();
            }
        }
    }

    /**
     * This is strictly a performance method. It should only be used when adding large numbers of new root Objects as the
     * EContentsList is modified directly in this method to avoid the overhead of all the EObject callbacks.
     * 
     * @param newRoots to add to this Resource.
     */
    protected void addMany( final Collection newRoots ) {
        final Collection allRoots = new ArrayList(this.getContents());
        allRoots.addAll(newRoots);

        final Object[] rootArray = allRoots.toArray();

        // Reset data object for Contents List.
        // NOTE : This is not considered a safe opperation as no callbacks are executed for
        // the added objects. See java doc for BasicEList.setData for additional details.
        ((BasicEList)this.getContents()).setData(rootArray.length, rootArray);

        // Attach all these objects to this resource
        for (Iterator iter = newRoots.iterator(); iter.hasNext();) {
            EObject eObject = (EObject)iter.next();

            if (eObject instanceof EObjectImpl) {
                ((EObjectImpl)eObject).eSetResource(this, null);
            }

            // Add the EObject and it's contents to the local cache
            addToEObjectCache(eObject, true);
        }

    }

    /**
     * This is strictly a performance method. It should only be used when removing large numbers of root Objects as the
     * EContentsList is modified directly in this method to avoid the overhead of all the EObject callbacks.
     * 
     * @param roots to remove from this Resource.
     */
    protected void removeMany( final Collection roots ) {
        final Collection allRoots = new ArrayList(this.getContents());
        allRoots.removeAll(roots);

        final Object[] rootArray = allRoots.toArray();

        // Reset data object for Contents List.
        // NOTE : This is not considered a safe opperation as no callbacks are executed for
        // the added objects. See java doc for BasicEList.setData for additional details.
        ((BasicEList)this.getContents()).setData(rootArray.length, rootArray);

        // Remove all these objects to this resource
        for (Iterator iter = roots.iterator(); iter.hasNext();) {
            EObject eObject = (EObject)iter.next();

            if (eObject instanceof EObjectImpl) {
                ((EObjectImpl)eObject).eSetResource(null, null);
            }

            // Remove the EObject and it's contents from the local cache
            removeFromEObjectCache(eObject, true);

        }
    }

    /**
     * @return Returns the eProxyCache.
     * @since 5.0
     */
    protected EObjectCache getEProxyCache() {
        return this.eProxyCache;
    }

    /**
     * Add the EObject to the local eProxy cache
     * 
     * @param eObject
     * @since 5.0
     */
    protected void addToEProxyCache( final EObject eObject ) {
        if (eObject != null) {
            getEProxyCache().add(eObject, false);
        }
    }

    /**
     * Remove the EObject from the local eProxy cache
     * 
     * @param id
     * @return
     * @since 5.0
     */
    protected EObject removeFromEProxyCache( final ObjectID id ) {
        EObject eObject = getEProxyCache().get(id);
        getEProxyCache().remove(id, false);
        return eObject;
    }

    /**
     * Find the EObject in the local eProxy cache
     * 
     * @param id
     * @return
     * @since 5.0
     */
    protected EObject findInEProxyCache( final ObjectID id ) {
        return (id != null ? (EObject)getEProxyCache().get(id) : null);
    }

    /**
     * @return Returns the eObjectCache.
     * @since 5.0
     */
    protected EObjectCache getEObjectCache() {
        return this.eObjectCache;
    }

    /**
     * Add the EObject to the local eObject cache
     * 
     * @param eObject
     * @since 5.0
     */
    protected void addToEObjectCache( final EObject eObject,
                                      boolean recurse ) {
        if (eObject != null) {
            getEObjectCache().add(eObject, recurse);
        }
    }

    /**
     * Remove the EObject from the local eObject cache
     * 
     * @param id
     * @return
     * @since 5.0
     */
    protected EObject removeFromEObjectCache( final ObjectID id,
                                              boolean recurse ) {
        EObject eObject = getEObjectCache().get(id);
        getEObjectCache().remove(id, recurse);
        return eObject;
    }

    /**
     * Remove the EObject from the local eObject cache
     * 
     * @param id
     * @return
     * @since 5.0
     */
    protected void removeFromEObjectCache( final EObject eObject,
                                           boolean recurse ) {
        getEObjectCache().remove(eObject, recurse);
    }

    /**
     * Find the EObject in either the local eObject cache or, if this resource is contained within an EResourceSet, search the
     * resource set's EObjectManager
     * 
     * @param id
     * @return
     * @since 5.0
     */
    protected EObject findInEObjectCache( final ObjectID id ) {
        EObject eObject = null;
        if (id != null) {
            eObject = getEObjectCache().get(id);
        }
        return eObject;
    }

    /*
     * EResourceContentsEList overrides the XMIResourceImpl.ContentsEList implementation
     */
    protected class EResourceContentsEList extends XMIResourceImpl.ContentsEList {

        /**
         */
        private static final long serialVersionUID = 1L;
        private final EResourceImpl owner;

        public EResourceContentsEList( final EResourceImpl owner ) {
            this.owner = owner;
        }

        /**
         * @see java.util.Collection#addAll(java.util.Collection)
         */
        @Override
        public boolean addAll( final Collection c ) {
            if (c == null || c.isEmpty()) {
                return false;
            }

            // Remove any nulls from the collection of roots to be added
            final Collection roots = new ArrayList(c);
            for (Iterator i = roots.iterator(); i.hasNext();) {
                EObject root = (EObject)i.next();
                if (root == null) {
                    i.remove();
                }
            }

            final int index = size;
            owner.addMany(roots);
            final EList vals = new BasicEList(roots);
            eNotify(createNotification(Notification.ADD_MANY, null, vals, index, true));
            return true;
        }

        /**
         * @see java.util.Collection#addAll(java.util.Collection)
         */
        @Override
        public boolean removeAll( final Collection c ) {
            if (c == null || c.isEmpty()) {
                return false;
            }
            final EList vals = new BasicEList(c);
            final int[] removedIndexes = getIndexes(vals);

            owner.removeMany(c);

            if (vals.size() == 1) {
                eNotify(createNotification(Notification.REMOVE, vals.get(0), null, removedIndexes[0]));
            } else {
                eNotify(createNotification(Notification.REMOVE_MANY, vals, removedIndexes, removedIndexes[0], true));
            }

            return true;
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
    }

    /*
     * EObjectCacheAdapter monitors add and remove notifications to keep the
     * resource's cache in sync with resource changes.
     */
    class EObjectCacheAdapter extends AdapterImpl {

        @Override
        public void notifyChanged( Notification notification ) {
            // If the notification is just a touch, don't do anything.
            if (notification.isTouch()) {
                return;
            }

            final Object feature = notification.getFeature();

            // For root level EObjects on a resource the feature is null so we will not process those
            // notifications as those objects would have been explicitly added by the EResource
            // Defect 18858 - we are not getting non-root-level XSD notifications (i.e. adds on a
            // schema), and there is no one adding the root schema object, so we check for it here:
            if (feature == null || (feature instanceof EReference && ((EReference)feature).isContainment())) {

                final int eventType = notification.getEventType();
                final Object oldVal = notification.getOldValue();
                final Object newVal = notification.getNewValue();

                switch (eventType) {
                    case Notification.REMOVE:
                        if (oldVal instanceof EObject) {
                            getEObjectCache().remove((EObject)oldVal, true);
                        }
                        break;
                    case Notification.REMOVE_MANY:
                        if (oldVal instanceof Collection) {
                            EObject[] oldVals = collectionToEObjectArray((Collection)oldVal);
                            getEObjectCache().remove(oldVals, true);
                        }
                        break;
                    case Notification.ADD:
                        if (newVal instanceof EObject) {
                            getEObjectCache().add((EObject)newVal, true);
                        }
                        break;
                    case Notification.ADD_MANY:
                        if (newVal instanceof Collection) {
                            EObject[] newVals = collectionToEObjectArray((Collection)newVal);
                            getEObjectCache().add(newVals, true);
                        }
                        break;
                    case Notification.SET:
                    case Notification.UNSET:
                    default:
                        // do nothing
                }
            }
        }

        /**
         * Helper methods used to convert a homogeneous collection of EObject instances into an array of EObject instances.
         * 
         * @param values
         * @return
         * @since 5.0
         */
        private EObject[] collectionToEObjectArray( final Collection values ) {
            if (values == null || values.isEmpty()) {
                return EMPTY_EOBJECT_ARRAY;
            }
            final List result = new ArrayList(values.size());
            for (final Iterator i = values.iterator(); i.hasNext();) {
                final Object obj = i.next();
                if (obj instanceof EObject) {
                    result.add(obj);
                }
            }
            return (EObject[])result.toArray(new EObject[result.size()]);
        }
    }

    public String getUuid( EObject object ) {
        // TODO: JBEDSP-407
        return null;
    }

    @Override
    public boolean isLoading() {
        // TODO:JBEDSP-407
        return super.isLoading();
    }

    public boolean isUnloading() {
        // TODO:JBEDSP-407
        return false;
    }

    public void setUuid( EObject object,
                         String uuid ) {
        // TODO:JBEDSP-407
        super.setID(object, uuid);
    }
}
