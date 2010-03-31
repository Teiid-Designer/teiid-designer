/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource.xmi;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.ClassNotFoundException;
import org.eclipse.emf.ecore.xmi.IllegalValueException;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.eclipse.xsd.XSDPackage;
import org.xml.sax.Attributes;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.ReflectionHelper;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.Identifiable;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;

/**
 * @since 3.1
 */
public class MtkXmiHandler extends SAXXMIHandler {

    private static final String XSD_URI = XSDPackage.eNS_URI;
    private static final String TARGET_NS_ATTRIBUTE_NAME = "targetNamespace"; //$NON-NLS-1$
    private static final String SDT_DOMAIN_INSTANCE_CLASS_NAME = "com.metamatrix.metamodels.sdt.Domain"; //$NON-NLS-1$

    private static final DateFormat[] DATE_FORMATS = {new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"), //$NON-NLS-1$
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSZ"), //$NON-NLS-1$
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS"), //$NON-NLS-1$
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), //$NON-NLS-1$
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"), //$NON-NLS-1$
        new SimpleDateFormat("yyyy-MM-dd")}; //$NON-NLS-1$

    private final MetamodelRegistry registry;
    private final IDGenerator idGenerator;
    private final Container container;
    private final Resource resource;
    private final Collection roots;
    private boolean isXsdResource;
    private List xmlSchemaProxies;
    private List targetNamespaces;
    private final Collection proxyResourceURIs;
    private final Collection modelImportsToConvert;

    /**
     * Constructor for EmfXMIHandler.
     * 
     * @param xmiResource
     * @param helper
     * @param options
     */
    public MtkXmiHandler( final XMIResource xmiResource,
                          final XMLHelper helper,
                          final Container container,
                          final Map options ) {
        super(xmiResource, helper, options);
        if (xmiResource == null) {
            final String msg = ModelerCore.Util.getString("MtkXmiHandler.The_XMIResource_reference_may_not_be_null_1"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (!(xmiResource instanceof MtkXmiResourceImpl)) {
            final String msg = ModelerCore.Util.getString("MtkXmiHandler.The_XMIResource_must_be_an_instance_of_MtkXMIResourceImpl_2"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        MtkXmiResourceImpl mtkResource = (MtkXmiResourceImpl)xmiResource;

        this.resource = mtkResource;
        this.registry = mtkResource.getMetamodelRegistry();
        this.idGenerator = IDGenerator.getInstance();
        this.container = container;
        this.xmlSchemaProxies = new LinkedList();
        this.targetNamespaces = new LinkedList();
        this.roots = new ArrayList();
        this.proxyResourceURIs = new HashSet();
        this.modelImportsToConvert = new HashSet();
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#startElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void startElement( String uri,
                              String localName,
                              String name ) {
        // System.out.println("startElement "+uri+ ", "+localName+", "+name);
        super.startElement(uri, localName, name);
    }

    /**
     * Attempt to get the namespace for the given prefix, then return ERegister.getPackage() or null.
     */
    @Override
    protected EPackage getPackageForURI( final String uriString ) {
        if (uriString == null) {
            return null;
        }
        // Ensure that any metamodel for this URI is loaded
        if (registry != null && registry.containsURI(uriString)) {
            ensureMetamodelIsLoaded(registry.getURI(uriString));
        }
        return super.getPackageForURI(uriString);
    }

    /**
     * Ensure that the metamodel with the specified URI is loaded
     */
    private void ensureMetamodelIsLoaded( final URI metamodelURI ) {
        if (metamodelURI == null) {
            final String msg = ModelerCore.Util.getString("MtkXmiHandler.The_URI_reference_may_not_be_null_3"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        // Load the metamodel if it is not yet loaded
        final Resource resource = registry.getResource(metamodelURI);
        Assertion.assertTrue(resource.isLoaded());
    }

    private boolean isXsdPrefix( final String prefix ) {
        // MetamodelDescriptor descriptor = ModelerCore.getMetamodels().getMetamodelDescriptor(SDT_URI);
        // if (descriptor != null && descriptor.getNamespacePrefix().equalsIgnoreCase(prefix)) {
        // return true;
        // }
        final URI nsUri = ModelerCore.getMetamodelRegistry().getURI(XSD_URI);
        if (nsUri != null) {
            MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(nsUri);
            if (descriptor != null && descriptor.getNamespacePrefix().equalsIgnoreCase(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a top object based on the prefix and name. Overrides same method in super-class, but wraps resulting object in a
     * java proxy
     */
    @Override
    protected void createTopObject( final String prefix,
                                    final String name ) {
        if (isXsdPrefix(prefix)) {
            isXsdResource = true;
        }
        final EFactory eFactory = getFactoryForPrefix(prefix);
        EObject newObject = null;
        try {
            newObject = createObjectFromFactory(eFactory, name);
        } catch (Throwable t) {
            final Object[] params = new Object[] {prefix, name};
            ModelerCore.Util.log(IStatus.ERROR,
                                 t,
                                 ModelerCore.Util.getString("MtkXmiHandler.Error_in_MtkXmiHandler.createTopObject()_1", params)); //$NON-NLS-1$
        }
        if (ModelerCore.DEBUG || ModelerCore.DEBUG_METAMODEL) {
            final Object[] params = new Object[] {newObject, prefix, name, eFactory};
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("MtkXmiHandler.DEBUG.Created_new_EObject_from_prefix,_name,_and_EFactory_1", params)); //$NON-NLS-1$
        }
        final URI uri = this.xmlResource.getURI();

        // If this URI represents a metamodel that has been registered and is
        // now being loaded then set its namespace URI and namespace URI prefix
        if (newObject instanceof EPackage && this.registry != null && this.registry.containsURI(uri)) {
            try {
                final MetamodelDescriptor descriptor = this.registry.getMetamodelDescriptor(uri);
                if (descriptor == null) {
                    final String msg = ModelerCore.Util.getString("MtkXmiHandler.No_metamodel_descriptor_was_found"); //$NON-NLS-1$
                    throw new AssertionError(msg);
                }
                EPackage ePackage = (EPackage)newObject;
                ePackage.setNsPrefix(descriptor.getNamespacePrefix());
                ePackage.setNsURI(uri.toString());
            } catch (Throwable t) {
                final Object[] params = new Object[] {prefix, name};
                ModelerCore.Util.log(IStatus.ERROR,
                                     t,
                                     ModelerCore.Util.getString("MtkXmiHandler.Error_in_MtkXmiHandler.createTopObject()_1", params)); //$NON-NLS-1$
            }
        }
        try {
            processTopObject(newObject);
        } catch (Throwable t) {
            final Object[] params = new Object[] {prefix, name};
            ModelerCore.Util.log(IStatus.ERROR,
                                 t,
                                 ModelerCore.Util.getString("MtkXmiHandler.Error_in_MtkXmiHandler.createTopObject()_1", params)); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#processTopObject(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected void processTopObject( EObject object ) {
        super.processObject(object);
        roots.add(object);
    }

    /**
     * Process the XMI attributes for the newly created object. Overrides same method in super-class, but handles UUID instead of
     * explicitly ignoring it as the super class does.
     */
    @Override
    protected void handleObjectAttribs( final EObject obj ) {
        if (attribs != null) {
            InternalEObject internalEObject = (InternalEObject)obj;
            for (int i = 0, size = attribs.getLength(); i < size; ++i) {
                String name = attribs.getQName(i);

                // If the xmi:uuid attribute is encountered ...
                if (name.equals(UUID_ATTRIB) && this.idGenerator != null) {
                    try {
                        final String uuidStr = attribs.getValue(i);
                        this.idGenerator.stringToObject(uuidStr);

                        // -------------------------------------------------------------------------------
                        // Fix to defect 14449, where Core::ModelImport objects were created with the same
                        // UUID as the model that they reference. This fix only addresses the side effect
                        // of the defect in existing models; see a little lower in this method
                        // for the fix so that we don't reset it upon reading in (the original cause of the
                        // problem)
                        if (obj instanceof ModelImport) {
                            final ModelImport modelImport = (ModelImport)obj;
                            // Get the UUID of the referenced model (first on the object instance) ...
                            String uuidOfRefedModel = modelImport.getUuid();
                            if (uuidOfRefedModel == null) {
                                // That attribute hasn't yet been read in, so look in attributes that haven't yet been processed
                                // ...
                                final String uuidAttributeName = CorePackage.eINSTANCE.getModelImport_Uuid().getName();
                                for (int k = (i + 1); k < size; ++k) { // start at the next unread attribute!!!
                                    String attributeName = attribs.getQName(k);
                                    if (uuidAttributeName.equals(attributeName)) {
                                        uuidOfRefedModel = attribs.getValue(k);
                                    }
                                }
                            }
                            // Check if the uuuid of the ModelImport is the same as the UUID of the referenced model ...
                            // Note that both values use the "mmuuid:" form, so okay to compare the value strings
                            if (uuidStr.equals(uuidOfRefedModel)) {
                                this.idGenerator.create();
                            }
                        }
                        // End of fix to defect 14449
                        // -------------------------------------------------------------------------------

                        xmlResource.setID(internalEObject, uuidStr);
                    } catch (InvalidIDException e) {
                        ModelerCore.Util.log(IStatus.ERROR,
                                             ModelerCore.Util.getString("MtkXmiHandler.Error_handling_Object_attributes_for_-_5", new Object[] {obj, e.getMessage()})); //$NON-NLS-1$
                    }
                }
                // If the xmi:href attribute is encountered ...
                else if (name.equals(XMLResource.HREF)) {
                    String uri = attribs.getValue(i);
                    if (uri.startsWith("#")) { //$NON-NLS-1$
                        uri = this.resourceURI.toString() + uri;
                    }

                    handleProxy(internalEObject, uri);
                }
                // For all other attributes ...
                else if (!name.startsWith(XMLResource.XML_NS) && !notFeatures.contains(name)) {
                    EStructuralFeature feature = obj.eClass().getEStructuralFeature(name);
                    if (feature != null && feature.isChangeable()) {
                        setAttribValue(obj, name, attribs.getValue(i));
                    } else {
                        if (ModelerCore.DEBUG) {
                            ModelerCore.Util.log(IStatus.WARNING,
                                                 ModelerCore.Util.getString("MtkXmiHandler.DEBUG.Unable_to_set_the_value_on_EAttribute_since_it_is_unchangeable._1", name)); //$NON-NLS-1$
                        }
                    }
                    // If we are setting a namespace URI on a new EPackage instance then register it
                    if (obj instanceof EPackage && EcorePackage.eINSTANCE.getEPackage_NsURI().equals(feature)) {
                        EPackage.Registry.INSTANCE.put(attribs.getValue(i), obj);
                    }
                    // setAttribValue(obj, name, attribs.getValue(i));
                }
                // If the uuid feature name is encountered ...
                if (name.equalsIgnoreCase("uuid") && this.idGenerator != null) { //$NON-NLS-1$
                    // -------------------------------------------------------------------------------
                    // Fix to defect 14449, where we don't want to reset the ObjectID on
                    // a ModelImport, since the "uuid" feature on a ModelImport represents
                    // the UUID of the referenced model.
                    boolean treatAsObjectId = true;
                    if (obj instanceof ModelImport) { // also Manifest::ModelReference, which extends ModelImport
                        treatAsObjectId = false;
                    } else if (!obj.eClass().getEPackage().getNsURI().equals("http://www.metamatrix.com/metamodels/VirtualDatabase") //$NON-NLS-1$
                               && !(obj instanceof Identifiable)) {
                        // Don't know what metaclass this is, so do not treat as an ObjectID ...
                        treatAsObjectId = false;
                        // And log ...
                        final Object[] params = new Object[] {obj.eClass().getEPackage(), obj.eClass()};
                        final String msg = ModelerCore.Util.getString("MtkXmiHandler.UnexpectedUuidFeature", params); //$NON-NLS-1$
                        ModelerCore.Util.log(IStatus.ERROR, msg);
                    }
                    // End of fix to defect 14449
                    // -------------------------------------------------------------------------------
                    if (treatAsObjectId) {
                        xmlResource.setID(internalEObject, attribs.getValue(i));
                    }
                }
                // If the targetNamespace attribute in a com.metamatrix.metamodels.sdt.Domain instance is encountered ...
                if (name.equals(TARGET_NS_ATTRIBUTE_NAME)
                    && SDT_DOMAIN_INSTANCE_CLASS_NAME.equals(obj.eClass().getInstanceClassName())) {
                    final String targetNamespace = attribs.getValue(i);
                    if (targetNamespace != null && targetNamespace.length() > 0) {
                        this.targetNamespaces.add(attribs.getValue(i));
                    }
                }
            }
        }

        // First of two patches to convert the Core::ModelImport "path" feature value found in models
        // created prior to 4.4 to their new "modelLocation" value
        patchA_modelImport(obj, attribs);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#handleProxy(org.eclipse.emf.ecore.InternalEObject, java.lang.String)
     * @since 4.3
     */
    @Override
    protected void handleProxy( InternalEObject proxy,
                                String uriLiteral ) {
        super.handleProxy(proxy, uriLiteral);

        // Save the URI of the proxy's resource
        this.proxyResourceURIs.add(proxy.eProxyURI().trimFragment());
    }

    /**
     * Set the given feature of the given object to the given value.
     */
    @Override
    protected void setFeatureValue( EObject object,
                                    EStructuralFeature feature,
                                    Object value,
                                    int position ) {
        try {
            // If there is a reference to an XMLSchema entity ...
            if (value instanceof EObject && ((EObject)value).eIsProxy()) {
                URI proxyURI = ((InternalEObject)value).eProxyURI();
                if (proxyURI != null && proxyURI.toString().startsWith(MtkXmiSaveImpl.XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX)) {
                    // System.out.println("setFeatureValue - creating ProxyReferenceHolder for "+value+" and feature "+feature.getName());
                    ProxyReferenceHolder refHolder = new ProxyReferenceHolder(object, feature, (EObject)value, position);
                    this.xmlSchemaProxies.add(refHolder);
                }
            }
            // If the object for which the value is being set in XMLSchema model ...
            if (isXsdResource || isXsdPrefix(object.eClass().getEPackage().getNsPrefix())) {
                this.setValue(object, feature, value, position);
                return;
            }

            // If the feature has a datatype of java.util.Date then we need to ensure
            // that the format of the value adheres to one of the accepted formats
            // in EFactoryImpl.EDATE_FORMATS.
            if (feature instanceof EAttribute && value instanceof String) {
                if (Date.class.equals(((EAttribute)feature).getEAttributeType().getInstanceClass())) {
                    value = convertDateFormat((String)value);
                }
            }

            // Process as a normal feature value
            super.setFeatureValue(object, feature, value, position);

        } catch (RuntimeException e) {
            error(new IllegalValueException(object, feature, value, e, getLocation(), getLineNumber(), getColumnNumber()));
        }
    }

    private String convertDateFormat( final String value ) {
        Date valueAsDate = null;
        for (int i = 0; i < DATE_FORMATS.length; ++i) {
            try {
                valueAsDate = DATE_FORMATS[i].parse(value);
                // MyDefect : 18060 Added break to break for the right date formatter. no break was there before.
                break;
            } catch (ParseException parseException) {
                // do nothing
            }
        }
        if (valueAsDate == null) {
            final String msg = ModelerCore.Util.getString("MtkXmiHandler.Error_parsing_date_String", value); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, msg);
            return value;
        }
        return DateUtil.getDateAsString(valueAsDate);
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#createObjectFromFactory(org.eclipse.emf.ecore.EFactory, java.lang.String)
     */
    @Override
    protected EObject createObjectFromFactory( EFactory factory,
                                               String typeName ) {
        EObject newObject = null;

        if (factory != null) {
            newObject = helper.createObject(factory, helper.getType(factory, typeName));
            if (newObject != null) {
                if (disableNotify) newObject.eSetDeliver(false);

                handleObjectAttribs(newObject);

                // Fix for defect 12764. Ensure that any EObject instances from the
                // "http://www.eclipse.org/emf/2002/Ecore" metamodel are resolved immediately
                newObject = handleEcoreProxy(newObject, attribs);

            } else {
                super.error(new ClassNotFoundException(typeName, factory, getLocation(), getLineNumber(), getColumnNumber()));
            }
        }

        return newObject;
    }

    public void setValue( EObject object,
                          EStructuralFeature feature,
                          Object value,
                          int position ) {
        int kind = helper.getFeatureKind(feature);

        switch (kind) {
            case XMLHelper.DATATYPE_SINGLE:
            case XMLHelper.DATATYPE_IS_MANY:
                EClassifier eMetaObject = feature.getEType();
                EDataType eDataType = (EDataType)eMetaObject;
                EFactory eFactory = eDataType.getEPackage().getEFactoryInstance();

                if (kind == XMLHelper.DATATYPE_IS_MANY) {
                    BasicEList list = (BasicEList)object.eGet(feature);
                    if (position == -2) {
                        for (StringTokenizer stringTokenizer = new StringTokenizer((String)value, " "); //$NON-NLS-1$
                        stringTokenizer.hasMoreTokens();) {
                            String token = stringTokenizer.nextToken();
                            list.addUnique(eFactory.createFromString(eDataType, token));
                        }

                        // Make sure that the list will appear to be set to be empty.
                        //
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
            case XMLHelper.IS_MANY_ADD:
            case XMLHelper.IS_MANY_MOVE:
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
            default:
                object.eSet(feature, value);
        }
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#endDocument()
     */
    @Override
    public void endDocument() {
        addNamespaceConversions();

        // Execute XSDSchema.update() on any XSDSchema instances
        // found as top level objects
        if (isXsdResource) {
            final List topObjects = this.xmlResource.getContents();
            for (Iterator iter = topObjects.iterator(); iter.hasNext();) {
                updateSchema((EObject)iter.next());
            }
        }

        // resolveXmlSchemaProxies();
        this.resource.getContents().addAll(roots);

        // This second patch reconciles the "modelLocation" value that was simply transfered from
        // / the "path" value in the first patch to its correct relative URI path
        patchB_modelImport();

        super.endDocument();
    }

    private void addNamespaceConversions() {
        final Resource resource = this.xmlResource;
        final ResourceSet resourceSet = this.resourceSet;
        for (Iterator iter = this.targetNamespaces.iterator(); iter.hasNext();) {
            String targetNamespace = (String)iter.next();
            URI logicalURI = URI.createURI(targetNamespace);
            URI physicalURI = resource.getURI();
            resourceSet.getURIConverter().getURIMap().put(logicalURI, physicalURI);
            // System.out.println("Added URI conversion from "+logicalURI+" to "+physicalURI);
        }

    }

    /**
     * For a newly created EObject along with its associated attributes, check if that EObject has an "href" attribte to a
     * "http://www.eclipse.org/emf/2002/Ecore" instance and resolve it immediately, otherwise return the original object. Fix for
     * defect 12764.
     */
    private EObject handleEcoreProxy( final EObject obj,
                                      final Attributes atts ) {
        if (atts != null) {
            InternalEObject internalEObject = (InternalEObject)obj;
            for (int i = 0, size = atts.getLength(); i < size; ++i) {
                String name = atts.getQName(i);
                String value = atts.getValue(i);

                // If the xmi:href attribute is encountered ...
                if (name.equals(XMLResource.HREF)) {
                    // System.out.println("handleEcoreProxy for "+value);
                    URI uri = URI.createURI(value);
                    if (EcorePackage.eNS_URI.equals(uri.trimFragment().toString())) {
                        if (this.container != null) {
                            return EcoreUtil.resolve(internalEObject, this.container.getMetamodelRegistry().getResource(uri));
                        }
                    }
                }
            }
        }
        return obj;
    }

    /**
     * Update any XMLSchema entity found in the model file. By calling update() on a XMLSchema entity we are forcing a datatype
     * analysis and validation of the whole schema. This is necessary to ensure that datatypes defined within the schema are
     * properly constraned.
     * 
     * @param topObject
     */
    private void updateSchema( final EObject topObject ) {
        // Create the arguments to the method ...
        final Object[] args = new Object[] {};

        final Class xmlSchemaClass = topObject.getClass();

        final ReflectionHelper helper = new ReflectionHelper(xmlSchemaClass);
        Method updateSchemaMethod = null;
        try {
            updateSchemaMethod = helper.findBestMethodOnTarget("update", args); //$NON-NLS-1$
        } catch (SecurityException e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("MtkXmiHandler.Error_execute_the_XSDSchema.update()_method_for_1", topObject)); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            // do nothing
        }

        // Execute the XSDSchema.update() method
        if (updateSchemaMethod != null) {
            // System.out.println("Executing update() on "+topObject);
            try {
                updateSchemaMethod.invoke(topObject, args);
            } catch (Throwable t) {
                ModelerCore.Util.log(IStatus.ERROR,
                                     ModelerCore.Util.getString("MtkXmiHandler.Error_execute_the_XSDSchema.update()_method_for_2", topObject)); //$NON-NLS-1$
            }
        }
    }

    /**
     * First of two patches to convert the Core::ModelImport "path" feature value found in models created prior to 4.4 to their
     * new "modelLocation" value. In 4.4, the "path" feature on Core::ModelImport was changed to be transient and volatile to be
     * replaced by a new feature, "modelLocation". The new feature stores the relative URI path of the referenced resource instead
     * of the "path" relative to the workspace root which assumed an Eclipse runtime workspace. This first patch simply transfers
     * the "path" value to the new "modelLocation" feature.
     */
    protected EObject patchA_modelImport( final EObject obj,
                                          final Attributes attribs ) {
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
     * Second of two patches to convert the Core::ModelImport "path" feature value found in models created prior to 4.4 to their
     * new "modelLocation" value. In 4.4, the "path" feature on Core::ModelImport was changed to be transient and volatile to be
     * replaced by a new feature, "modelLocation". The new feature stores the relative URI path of the referenced resource instead
     * of the "path" relative to the workspace root which assumed an Eclipse runtime workspace. This second patch reconciles the
     * "modelLocation" value that was simply transfered from the "path" value in the first patch to its correct relative URI path
     */
    protected void patchB_modelImport() {
        // If no ModelImport conversion work is required, return
        if (this.modelImportsToConvert.isEmpty()) {
            return;
        }

        URI eResourceURI = this.resource.getURI();

        // Preprocess the collection of proxy resource URIs ...
        removeBadProxyResourceUris(this.proxyResourceURIs);

        // Reconcile the workspace relative paths, stored in the "modelLocation" feature,
        // against the resource URIs of the proxies
        Collection unconvertedImports = new HashSet(this.modelImportsToConvert);
        for (Iterator i = this.modelImportsToConvert.iterator(); i.hasNext();) {
            ModelImport modelImport = (ModelImport)i.next();
            String modelLocation = modelImport.getModelLocation().toLowerCase();

            // If the modelLocation value represents a logical location of a built-in resource then ignore it
            if (modelLocation.startsWith("http") || //$NON-NLS-1$
                modelLocation.startsWith(ResourceFinder.METAMODEL_PREFIX)
                || modelLocation.startsWith(ResourceFinder.UML2_METAMODELS_PREFIX)) {
                unconvertedImports.remove(modelImport);
                continue;
            }

            for (Iterator j = this.proxyResourceURIs.iterator(); j.hasNext();) {
                URI importURI = (URI)j.next();
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
            String modelLocation = removeProjectNameFromLocation(modelImport.getModelLocation()).toLowerCase();

            for (Iterator j = this.proxyResourceURIs.iterator(); j.hasNext();) {
                URI importURI = (URI)j.next();
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

    protected String removeProjectNameFromLocation( final String location ) {
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
     * If any of the resource URIs begin with '/' then it may represent a bad href (Eclipse workspace relative path of the form
     * "/project/.../model.xmi") which are sometimes found in old model files. Check if the collection contains the correct file
     * URI so that the bad one can be removed
     */
    protected void removeBadProxyResourceUris( final Collection proxyResourceUris ) {
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

}

class ProxyReferenceHolder {

    private final EObject owner;
    private final EStructuralFeature sf;
    private final EObject proxy;
    private final int position;

    public ProxyReferenceHolder( final EObject owner,
                                 final EStructuralFeature sf,
                                 final EObject proxy,
                                 final int position ) {
        this.owner = owner;
        this.sf = sf;
        this.proxy = proxy;
        this.position = position;
    }

    public EObject getOwner() {
        return owner;
    }

    public int getPosition() {
        return position;
    }

    public EObject getProxy() {
        return proxy;
    }

    public EStructuralFeature getSf() {
        return sf;
    }

}
