/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.eclipse.xsd.XSDPackage;
import org.xml.sax.Attributes;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.ReflectionHelper;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * @since 4.3
 */
public class EResourceXmiHandler extends SAXXMIHandler {

    private static final String MANIFEST_MODEL_NAME = "MetaMatrix-VdbManifestModel.xmi"; //$NON-NLS-1$
    private static final DateFormat [] DATE_FORMATS = { new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"), //$NON-NLS-1$
                                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSZ"), //$NON-NLS-1$
                                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS"), //$NON-NLS-1$
                                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), //$NON-NLS-1$
                                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"), //$NON-NLS-1$
                                                        new SimpleDateFormat("yyyy-MM-dd") }; //$NON-NLS-1$

    private final IDGenerator idGenerator;
    private final EResourceImpl eResource;
    private final Collection roots;
    private final Collection proxyResourceURIs;
    private final Collection modelImportsToConvert;
    private boolean isXsdResource;

//    private static final boolean DEBUG = false;

    /**
     * Constructor for EResourceXmiHandler.
     * @param xmiResource
     * @param helper
     * @param options
     */
    public EResourceXmiHandler(final XMIResource xmiResource, final XMLHelper helper, final Map options) {
        super(xmiResource, helper, options);

        ArgCheck.isNotNull(xmiResource);
        ArgCheck.isInstanceOf(EResourceImpl.class, xmiResource);

        this.eResource   = (EResourceImpl) xmiResource;
        this.idGenerator = IDGenerator.getInstance();
        this.roots       = new ArrayList();
        this.proxyResourceURIs = new HashSet();
        this.modelImportsToConvert = new HashSet();

        this.isXsdResource = false;
    }

    //==================================================================================
    //                   O V E R R I D D E N   M E T H O D S
    //==================================================================================

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#startElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void startElement(String uri, String localName, String name) {
        super.startElement(uri, localName, name);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#createTopObject(java.lang.String,java.lang.String)
   	 */
    @Override
    protected void createTopObject(final String prefix, final String name){
        if (isXsdPrefix(prefix)) {
            this.isXsdResource = true;
        }
        super.createTopObject(prefix, name);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#processTopObject(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected void processTopObject(EObject object) {
        super.processObject(object);
        this.roots.add(object);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler#handleObjectAttribs(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected void handleObjectAttribs(final EObject obj) {
        if (attribs != null) {
            InternalEObject internalEObject = (InternalEObject)obj;
            for (int i = 0, size = attribs.getLength(); i < size; ++i) {
                String qName = attribs.getQName(i);

                // If the xmi:id attribute is encountered ...
                if (qName.equals(ID_ATTRIB) ){
                    this.xmlResource.setID(internalEObject, attribs.getValue(i));

                // If the xmi:uuid attribute is encountered ...
                } else if (qName.equals(UUID_ATTRIB)) {
                    ObjectID uuid = getObjectIDFromString(attribs.getValue(i));
                    Assertion.isNotNull(uuid);

                    // Apply patch for defect 14449
                    uuid = patch_defect14449(obj,uuid,i);

                    // Set the xmi:uuid value on the EObject instance
                    ModelerCore.setObjectId(obj, uuid);

                    // Add the EObject to the EResource local cache
                    this.eResource.addToEObjectCache(obj,false);

                // If the xmi:href attribute is encountered ...
                } else if (qName.equals(XMLResource.HREF)) {
                    String value = attribs.getValue(i);
                    // If no resource URI prepends the href then the reference is assumed to be inside this resource
                    if(value.startsWith("#") ){ //$NON-NLS-1$
                        value = this.resourceURI.toString() + value;
                    }

                    handleProxy(internalEObject, value);

                // For all other attributes in which the prefix is not "xmlns" and is not one of the ignored features ...
                } else if (!qName.startsWith(XMLResource.XML_NS) && !notFeatures.contains(qName)) {
                    EStructuralFeature feature = obj.eClass().getEStructuralFeature(qName);
                    if (feature != null && feature.isChangeable()) {
                        setAttribValue(obj, qName, attribs.getValue(i));
                    }
                    // If we are setting a namespace URI on a new EPackage instance then register it
                    if (obj instanceof EPackage && EcorePackage.eINSTANCE.getEPackage_NsURI().equals(feature)) {
                        EPackage.Registry.INSTANCE.put(attribs.getValue(i),obj);
                    }
                }
            }

            // First of two patches to convert the Core::ModelImport "path" feature value found in models
            // created prior to 4.4 to their new "modelLocation" value
            patchA_modelImport(obj, attribs);
        }
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#handleProxy(org.eclipse.emf.ecore.InternalEObject, java.lang.String)
     * @since 4.3
     */
    @Override
    protected void handleProxy(InternalEObject proxy,
                               String uriLiteral) {
        super.handleProxy(proxy, uriLiteral);

        // Save the URI of the proxy's resource
        this.proxyResourceURIs.add(proxy.eProxyURI().trimFragment());

        // If the resource is the built-in datatypes resource then we possibly need to convert
        // from a logical URI to a physical URI.  For example, if the logical URI was
        // "http://www.w3.org/2001/XMLSchema#string" we need to remap this to the physical URI of
        // "file:/E:/.../cache/www.w3.org/2001/XMLSchema.xsd#//string;XSDSimpleTypeDefinition=7".
        URI physicalUri = null;
        if (eResource.getResourceSet() instanceof EResourceSetImpl) {
            EResourceSetImpl rs = (EResourceSetImpl)eResource.getResourceSet();
            if (rs.getEObjectHrefConverter() != null) {
                physicalUri = rs.getEObjectHrefConverter().getPhysicalURI(proxy.eProxyURI());
            }
        }
        if (physicalUri != null) {
            proxy.eSetProxyURI(physicalUri);
        }
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#setFeatureValue(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object, int)
     */
    @Override
    protected void setFeatureValue(EObject object, EStructuralFeature feature, Object value, int position) {
        // If the object for which the value is being set in XMLSchema model ...
        if (this.isXsdResource || isXsdPrefix(object.eClass().getEPackage().getNsPrefix())) {
            setValue(object,feature,value,position);
            return;
        }

        // If the feature has a datatype of java.util.Date then we need to ensure that the format of the
        // value adheres to one of the accepted formats in EFactoryImpl.EDATE_FORMATS.
        if (feature instanceof EAttribute && value instanceof String) {
            if (Date.class.equals(((EAttribute)feature).getEAttributeType().getInstanceClass())) {
                value = convertDateFormat((String)value);
            }
        }

        // Process as a normal feature value
        super.setFeatureValue(object,feature,value,position);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#createObjectFromFactory(org.eclipse.emf.ecore.EFactory, java.lang.String)
     */
    @Override
    protected EObject createObjectFromFactory(EFactory factory, String typeName) {
        EObject newObject = null;

        if (factory != null) {
            // Get the eProxy from the EResource local cache.  If one is found,
            // as a side-effect, the eProxy is removed from that cache.
            newObject = findEProxy();

            // If an eProxy could not be found then create a new EObject
            if (newObject == null) {
                newObject = helper.createObject(factory, helper.getType(factory, typeName));
            }

            if (newObject != null) {
                if (disableNotify) {
                    newObject.eSetDeliver(false);
                }

                handleObjectAttribs(newObject);

                // Fix for defect 12764. Ensure that any EObject instances from the
                // "http://www.eclipse.org/emf/2002/Ecore" metamodel are resolved immediately
                newObject = patch_defect12764(newObject, attribs);
            }
        }

        return newObject;
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#endDocument()
     */
    @Override
    public void endDocument() {
        // Execute XSDSchema.update() on any XSDSchema instances found as top level objects
        if (this.isXsdResource) {
            final List topObjects = this.xmlResource.getContents();
            for (Iterator iter = topObjects.iterator(); iter.hasNext();) {
                updateSchema((EObject)iter.next());
            }
        }

        this.eResource.getContents().addAll(this.roots);

        // This second patch reconciles the "modelLocation" value that was simply transfered from
        /// the "path" value in the first patch to its correct relative URI path
        patchB_modelImport();

        super.endDocument();
    }

    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================

    protected EObject findEProxy() {
        for (int i = 0, size = attribs.getLength(); i < size; ++i){
            String qName = attribs.getQName(i);

            // If the xmi:uuid attribute is encountered ...
            if (qName.equals(UUID_ATTRIB)) {
                ObjectID uuid = getObjectIDFromString(attribs.getValue(i));
                Assertion.isNotNull(uuid);

                EObject eProxy = this.eResource.findInEProxyCache(uuid);

                // Found the eProxy so remove it from the map and unset the eProxy URI
                if (eProxy != null) {
                    this.eResource.removeFromEProxyCache(uuid);
                    ((InternalEObject)eProxy).eSetProxyURI(null);
                }
                return eProxy;
            }
        }
        return null;
    }

    protected ObjectID getObjectIDFromString(final String uuidString) {
        if (uuidString == null || uuidString.length() == 0) {
            return null;
        }
        try {
            return IDGenerator.getInstance().stringToObject(uuidString);
        } catch ( InvalidIDException e ) {
            // do nothing ...
        }
        return null;
    }

    /**
     * For a newly created EObject along with its associated attributes, check that EObject
     * has an "href" attribte to a "http://www.eclipse.org/emf/2002/Ecore" instance and resolve
     * it immediately, otherwise return the original object.
     * Fix for defect 12764.
     */
    protected EObject patch_defect12764(final EObject obj, final Attributes attribs) {
        if (attribs != null) {
            for (int i = 0, size = attribs.getLength(); i < size; ++i) {
                String qName = attribs.getQName(i);
                String value = attribs.getValue(i);

                // If the xmi:href attribute is encountered ...
                if (qName.equals(XMLResource.HREF)) {
                    URI uri = URI.createURI(value);
                    if (EcorePackage.eNS_URI.equals(uri.trimFragment().toString())) {
                        EcorePackageImpl.init();
                    }
                }
            }
        }
        return obj;
    }

    /**
     * Fix to defect 14449, where Core::ModelImport objects were created with the same UUID as the model
     * that they reference.  This fix only addresses the side effect of the defect in existing models; see
     * a little lower in this method for the fix so that we don't reset it upon reading in (the original
     * cause of the problem)
     * <p>
     * Example shown below in which the EObject xmi:uuid is the same as the "uuid" feature value.  The "uuid"
     * feature value represents the UUID of the model being referenced by the import.
     * <modelImports xmi:uuid="mmuuid:bfa9eec0-c497-1fa8-a032-93e40f299f77" uuid="mmuuid:bfa9eec0-c497-1fa8-a032-93e40f299f77" ...
     * </p>
     * @param obj EObject being processed
     * @param objUuid current UUID of the EObject being processed
     * @param attribsIndex current index into the Attributes collection
     * @return new ObjectID instance if the fix applies to this EObject, otherwise objUuid is returned
     * @since 4.3
     */
    protected ObjectID patch_defect14449(final EObject obj, final ObjectID objUuid, final int attribsIndex) {
        Assertion.assertTrue(attribsIndex >= 0 && attribsIndex < attribs.getLength());
        Assertion.assertTrue(attribs.getQName(attribsIndex).equals(UUID_ATTRIB));

        if ( obj instanceof ModelImport ) {
            final ModelImport modelImport = (ModelImport)obj;

            // The stringified UUID of the xmi:uuid attribute
            final String uuidString = attribs.getValue(attribsIndex);

            // Get the UUID of the referenced model (first on the object instance) ...
            String uuidOfRefedModel = modelImport.getUuid();
            if ( uuidOfRefedModel == null ) {
                // That attribute hasn't yet been read in, so look in attributes that haven't yet been processed ...
                final String uuidFeatureName = CorePackage.eINSTANCE.getModelImport_Uuid().getName();

                for (int k = (attribsIndex+1); k < attribs.getLength(); ++k ){    // start at the next unread attribute!!!
                    String qName = attribs.getQName(k);
                    if ( qName.equals(uuidFeatureName) ) {
                        uuidOfRefedModel = attribs.getValue(k);
                    }
                }
            }

            // Check if the xmi:uuid of the EObject is the same as the "uuid" feature value representing
            // the model referenced by the ModelImport.  If they are the same then the EObject xmi:uuid
            // needs to be recreated. [Note: both values use the "mmuuid:" form, so okay to compare
            // the value strings]
            if ( uuidString.equals(uuidOfRefedModel) ) {
                return this.idGenerator.create();
            }
        }
        return objUuid;
    }

    /**
     * First of two patches to convert the Core::ModelImport "path" feature value found in models
     * created prior to 4.4 to their new "modelLocation" value.  In 4.4, the "path" feature on
     * Core::ModelImport was changed to be transient and volatile to be replaced by a new feature,
     * "modelLocation".  The new feature stores the relative URI path of the referenced resource
     * instead of the "path" relative to the workspace root which assumed an Eclipse runtime workspace.
     * This first patch simply transfers the "path" value to the new "modelLocation" feature.
     */
    protected EObject patchA_modelImport(final EObject obj, final Attributes attribs) {
        if (obj instanceof ModelImport && attribs != null) {
            for (int i = 0, size = attribs.getLength(); i < size; ++i) {
                String qName = attribs.getQName(i);
                String value = attribs.getValue(i);
                if (qName.equals("path")) { //$NON-NLS-1$
                    ((ModelImport)obj).setModelLocation(value);
                    this.modelImportsToConvert.add(obj);
                }
            }
        }
        return obj;
    }

    /**
     * Second of two patches to convert the Core::ModelImport "path" feature value found in models
     * created prior to 4.4 to their new "modelLocation" value.  In 4.4, the "path" feature on
     * Core::ModelImport was changed to be transient and volatile to be replaced by a new feature,
     * "modelLocation".  The new feature stores the relative URI path of the referenced resource
     * instead of the "path" relative to the workspace root which assumed an Eclipse runtime workspace.
     * This second patch reconciles the "modelLocation" value that was simply transfered from
     * the "path" value in the first patch to its correct relative URI path
     */
    protected void patchB_modelImport() {
        // If no ModelImport conversion work is required, return
        if (this.modelImportsToConvert.isEmpty()) {
            return;
        }

        // If the resource being loaded is the "MetaMatrix-VdbManifestModel.xmi" resource contained in a VDB ...
        URI eResourceURI = this.eResource.getURI();
        if (MANIFEST_MODEL_NAME.equals(eResourceURI.lastSegment())) {
            // do nothing
            return;
        }

        // Preprocess the collection of proxy resource URIs ...
        removeBadProxyResourceUris(this.proxyResourceURIs);

        // Reconcile the workspace relative paths, stored in the "modelLocation" feature,
        // against the resource URIs of the proxies
        Collection unconvertedImports = new HashSet(this.modelImportsToConvert);
        for (Iterator i = this.modelImportsToConvert.iterator(); i.hasNext();) {
            ModelImport modelImport = (ModelImport)i.next();
            String modelLocation    = modelImport.getModelLocation().toLowerCase();

//            // If the modelLocation value represents a logical location of a built-in resource then ignore it
//            if (modelLocation.startsWith("http") || //$NON-NLS-1$
//                modelLocation.startsWith(EResourceFinder.METAMATRIX_METAMODEL_PREFIX) ||
//                modelLocation.startsWith(EResourceFinder.UML2_METAMODELS_PREFIX)) {
//                unconvertedImports.remove(modelImport);
//                continue;
//            }

            for (Iterator j = this.proxyResourceURIs.iterator(); j.hasNext();) {
                URI importURI    = (URI)j.next();
                String uriString = URI.decode(importURI.toString()).toLowerCase();

                // If the URI of the proxy resource is a logical URI of the form "http://..." then ignore it
                if (uriString.startsWith("http") && !uriString.endsWith("xmi")) { //$NON-NLS-1$ //$NON-NLS-2$
                    continue;
                }

                // Match the modelLocation, currently of the form "/project/.../model.xmi" to
                // a resource URI of the form "file:/C:/.../project/.../model.xmi"
                if (uriString.endsWith(modelLocation)) {
                    boolean deresolve = (eResourceURI != null && !eResourceURI.isRelative() && eResourceURI.isHierarchical());
                    if (deresolve && !importURI.isRelative()) {
                        URI deresolvedURI = importURI.deresolve(eResourceURI, true, true, false);
                        if (deresolvedURI.hasRelativePath()) {
                            importURI = deresolvedURI;
                        }
                    }
                    modelImport.setModelLocation(URI.decode(importURI.toString()));
                    unconvertedImports.remove(modelImport);
                    break;
                }
            }
        }

        // Reconcile the workspace relative paths, stored in the "modelLocation" feature,
        // against the model names found in the resource URIs of the proxies
        for (Iterator i = unconvertedImports.iterator(); i.hasNext();) {
            ModelImport modelImport = (ModelImport)i.next();
            String modelLocation    = removeProjectNameFromLocation(modelImport.getModelLocation()).toLowerCase();

            for (Iterator j = this.proxyResourceURIs.iterator(); j.hasNext();) {
                URI importURI    = (URI)j.next();
                String uriString = URI.decode(importURI.toString()).toLowerCase();

                // If the URI of the proxy resource is a logical URI of the form "http://..." then ignore it
                if (uriString.startsWith("http") && !uriString.endsWith("xmi")) { //$NON-NLS-1$ //$NON-NLS-2$
                    continue;
                }

                // Match the modelLocation, currently a project relative path, to a resource URI
                if (uriString.endsWith(modelLocation)) {
                    boolean deresolve = (eResourceURI != null && !eResourceURI.isRelative() && eResourceURI.isHierarchical());
                    if (deresolve && !importURI.isRelative()) {
                        URI deresolvedURI = importURI.deresolve(eResourceURI, true, true, false);
                        if (deresolvedURI.hasRelativePath()) {
                            importURI = deresolvedURI;
                        }
                    }
                    modelImport.setModelLocation(URI.decode(importURI.toString()));
                    i.remove();
                    break;
                }
            }
        }

        // Remove any model imports that could not be matched to a proxy resource
        for (Iterator i = unconvertedImports.iterator(); i.hasNext();) {
            ModelImport modelImport = (ModelImport)i.next();
            modelImport.setModel(null);
        }
    }

    protected String removeProjectNameFromLocation(final String location) {
        String newLocation = location;
        URI uri = URI.createURI(location);
        if (uri.segmentCount() > 1) {
            StringBuffer sb = new StringBuffer(location.length());
            String[] segments = uri.segments();
            for (int i = 1; i != segments.length; ++i) {
                sb.append("/"); //$NON-NLS-1$
                sb.append(segments[i]);
            }
            newLocation = sb.toString();
        }
        return newLocation;
    }

    /**
     * If any of the resource URIs begin with '/' then it may represent a bad
     * href (Eclipse workspace relative path of the form "/project/.../model.xmi")
     * which are sometimes found in old model files.  Check if the collection
     * contains the correct file URI so that the bad one can be removed
     */
    protected void removeBadProxyResourceUris(final Collection proxyResourceUris) {
        if (proxyResourceUris == null || proxyResourceUris.isEmpty()) {
            return;
        }
        // Collect all the URIs that are identified as being bad and
        // remove them from the collection of proxy resource URIs
        final List badUris = new ArrayList(proxyResourceUris.size());
        for (Iterator i = proxyResourceUris.iterator(); i.hasNext();) {
            URI uri = (URI)i.next();
            String uriString = uri.toString();
            if (uriString.charAt(0) == '/') {
                badUris.add(uri);
                i.remove();
            }
        }
        if (badUris.isEmpty()) {
            return;
        }
        // Check the collection of remaining proxy resource URIs attempting to
        // match the bad Eclipse workspace relative path to a good file URI path.
        // If a match is not found then re-add the bad path
        for (Iterator i = proxyResourceUris.iterator(); i.hasNext();) {
            URI uri = (URI)i.next();
            String uriString = URI.decode(uri.toString()).toLowerCase();
            for (Iterator j = badUris.iterator(); j.hasNext();) {
                URI badUri = (URI)j.next();
                String badUriString = URI.decode(badUri.toString()).toLowerCase();
                if (uriString.endsWith(badUriString)) {
                    j.remove();
                }
            }
        }
        if (!badUris.isEmpty()) {
            proxyResourceUris.addAll(badUris);
        }
    }

    protected void setValue(EObject object, EStructuralFeature feature, Object value, int position) {
        int kind = helper.getFeatureKind(feature);

        switch (kind) {
            case XMLHelper.DATATYPE_SINGLE :
            case XMLHelper.DATATYPE_IS_MANY :
                EClassifier eMetaObject = feature.getEType();
                EDataType eDataType = (EDataType)eMetaObject;
                EFactory eFactory = eDataType.getEPackage().getEFactoryInstance();

                if (kind == XMLHelper.DATATYPE_IS_MANY) {
                    BasicEList list = (BasicEList)object.eGet(feature);
                    if (position == -2) {
                        for (StringTokenizer stringTokenizer = new StringTokenizer((String)value, " "); //$NON-NLS-1$
                            stringTokenizer.hasMoreTokens();
                            ) {
                            String token = stringTokenizer.nextToken();
                            list.addUnique(eFactory.createFromString(eDataType, token));
                        }

                        // Make sure that the list will appear to be set to be empty.
                        if (list.isEmpty()) {
                            list.clear();
                        }
                    } else if (value == null) {
                        list.addUnique(null);
                    } else {
                        list.addUnique(eFactory.createFromString(eDataType, (String)value));
                    }
                } else if (value == null) {
                    object.eSet(feature, null);
                } else {
                    object.eSet(feature, eFactory.createFromString(eDataType, (String)value));
                }
                break;
            case XMLHelper.IS_MANY_ADD :
            case XMLHelper.IS_MANY_MOVE :
                BasicEList list = (BasicEList)object.eGet(feature);

                if (position == -1) {
                    list.addUnique(value);
                } else if (position == -2) {
                    list.clear();
                } else if (kind == XMLHelper.IS_MANY_ADD) {
                    list.addUnique(position, value);
                } else {
                    list.move(position, value);
                }
                break;
            default :
                object.eSet(feature, value);
        }
    }

    protected boolean isXsdPrefix(final String prefix) {
        if (prefix.equalsIgnoreCase(XSDPackage.eNS_PREFIX)) {
            return true;
        }
        return false;
    }

    protected String convertDateFormat(final String value) {
        Date valueAsDate = null;
        for (int i = 0; i < DATE_FORMATS.length; ++i) {
            try {
                valueAsDate = DATE_FORMATS[i].parse(value);
                break;
            } catch (ParseException parseException) {
                // do nothing
            }
        }
        if (valueAsDate == null) {
            final String msg = ModelerCore.Util.getString("EResourceXmiHandler.Error_parsing_date_string",value); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR,msg);
            return value;
        }
        return DateUtil.getDateAsString(valueAsDate);
    }

    /**
     * Update any XMLSchema entity found in the model file.  By calling update()
     * on a XMLSchema entity we are forcing a datatype analysis and validation
     * of the whole schema.  This is necessary to ensure that datatypes defined
     * within the schema are properly constrained.
     * @param topObject
     */
    protected void updateSchema( final EObject topObject ) {
        // Create the arguments to the method ...
        final Object[] args = new Object[]{};

        final Class xmlSchemaClass = topObject.getClass();

        final ReflectionHelper helper = new ReflectionHelper(xmlSchemaClass);
        Method updateSchemaMethod = null;
        try {
            updateSchemaMethod = helper.findBestMethodOnTarget("update", args); //$NON-NLS-1$
        } catch (SecurityException e) {
            final String msg = ModelerCore.Util.getString("EResourceXmiHandler.Error_executing_XSD_update_method",xmlSchemaClass); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR,e,msg);
        } catch (NoSuchMethodException e) {
            // do nothing
        }

        // Execute the XSDSchema.update() method
        if (updateSchemaMethod != null) {
            try {
                updateSchemaMethod.invoke(topObject,args);
            } catch ( Throwable t ) {
                final String msg = ModelerCore.Util.getString("EResourceXmiHandler.Error_executing_XSD_update_method",updateSchemaMethod); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.ERROR,msg);
            }
        }
    }
}
