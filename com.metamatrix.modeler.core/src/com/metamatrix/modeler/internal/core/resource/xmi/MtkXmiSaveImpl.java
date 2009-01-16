/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.core.resource.xmi;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.metamodels.core.Identifiable;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**<p>
 * </p>
 * @since 4.0
 */
public class MtkXmiSaveImpl extends XMISaveImpl {
    //============================================================================================================================
    // Constants

    protected static final String URI_REFERENCE_DELIMITER = DatatypeConstants.URI_REFERENCE_DELIMITER;

    protected static final String XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX = ModelerCore.XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX;
    protected static final String XML_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX          = "XMLSchema.xsd"; //$NON-NLS-1$
    protected static final String XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX    = "MagicXMLSchema.xsd"; //$NON-NLS-1$
    protected static final String XML_SCHEMA_INSTANCE_ECLIPSE_PLATFORM_URI_SUFFIX = "XMLSchema-instance.xsd"; //$NON-NLS-1$

    protected static final char UUID_PROTOCOL_DELIMITER = '/';
    protected static final String XMI_UUID = "uuid"; //$NON-NLS-1$
    protected static final String XMI_UUID_NS = XMIResource.XMI_NS + ":" + XMI_UUID; // xmi:uuid //$NON-NLS-1$
    protected static final String BAD_DATATYPE_HREF = "#null"; //$NON-NLS-1$

    private static final String AMP  = "&amp;"; //$NON-NLS-1$
    private static final String LT   = "&lt;"; //$NON-NLS-1$
    private static final String QUOT = "&quot;"; //$NON-NLS-1$

    private static final String HEX_PREFIX = "&#x"; //$NON-NLS-1$


    /**
     * Exists to handle defect 11213.
     * @since 4.0
     */
    private boolean escaped;

    private final MtkXmiResourceImpl xmiResource;
    private final DatatypeManager dtMgr;

    //============================================================================================================================
    // Constructors

    /**<p>
     * </p>
     * @since 4.0
     */
    public MtkXmiSaveImpl(XMLHelper helper, MtkXmiResourceImpl xmiResource) {
        super(helper);
        if (xmiResource == null) {
            final String msg = ModelerCore.Util.getString("MtkXmiSaveImpl.The_MtkXMIResourceImpl_reference_may_not_be_null_3"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        this.xmiResource = xmiResource;
        this.dtMgr = xmiResource.getContainer().getDatatypeManager();
        this.idAttributeName = XMI_UUID;
		this.idAttributeNS = XMI_UUID_NS;
    }

    //============================================================================================================================
    // Overridden Methods

    /**<p>
     * Overridden solely to handle defect 11213.  Code is identical to superclass except for if block regarding
     * <code>escaped</code> variable.
     * </p>
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#getContent(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature[])
     * @since 4.0
     */
    @Override
    protected String getContent(EObject o, EStructuralFeature[] features) {
        final XMLResource.XMLMap map = helper.getXMLMap();
        if (map == null) {
            return null;
        }

        for (int i = 0;  i < features.length;  i++) {
            final XMLResource.XMLInfo info = map.getInfo(features[i]);
            if (info != null && info.getXMLRepresentation() == XMLResource.XMLInfo.CONTENT) {
                Object value = helper.getValue(o, features[i]);
                if (value == null) {
                    return null;
                }
                EDataType d = (EDataType) features[i].getEType();
                EPackage ePackage = d.getEPackage();
                EFactory fac = ePackage.getEFactoryInstance();
                String svalue = fac.convertToString(d, value);
                if (this.escaped) {
                    svalue = convert(svalue);
                }
                return svalue;
            }
        }

        return null;
    }

    /**<p>
     * Overridden solely to handle defect 11213.  Code is identical to superclass except for if block regarding
     * <code>escaped</code> variable.
     * </p>
     * @see org.eclipse.emf.ecore.xmi.impl.XMISaveImpl#init(org.eclipse.emf.ecore.xmi.XMLResource, java.util.Map)
     * @since 4.0
     */
    @Override
    protected void init(final XMLResource resource, final Map options) {
        super.init(resource, options);
        this.escape = null;
        this.escaped = !Boolean.TRUE.equals(options.get(XMLResource.OPTION_SKIP_ESCAPE));
    }

    /**<p>
     * Overridden solely to handle defect 11213.  Code is identical to superclass except for if block regarding
     * <code>escaped</code> variable.
     * </p>
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveDataTypeElementSingle(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.0
     */
    @Override
    protected void saveDataTypeElementSingle(EObject o, EStructuralFeature f) {
        String name = helper.getQName(f);
        Object value = helper.getValue(o, f);
        if (value == null)
        {
          doc.startElement(name);
          doc.addAttribute(XSI_NIL, "true"); //$NON-NLS-1$
          doc.endEmptyElement();
          declareXSI = true;
        }
        else
        {
          EDataType d = (EDataType)f.getEType();
          EPackage ePackage = d.getEPackage();
          EFactory fac = ePackage.getEFactoryInstance();
          doc.startElement(name);
          String svalue = fac.convertToString(d, value);

          if (this.escaped)
          {
            svalue = convert(svalue);
          }

          doc.endContentElement(svalue);
        }
    }

    /**<p>
     * Overridden solely to handle defect 11213.  Code is identical to superclass except for if block regarding
     * <code>escaped</code> variable.
     * </p>
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveDataTypeSingle(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.0
     */
    @Override
    protected void saveDataTypeSingle(EObject o, EStructuralFeature f) {
        EDataType d = (EDataType)f.getEType();
        EPackage ePackage = d.getEPackage();
        EFactory fac = ePackage.getEFactoryInstance();
        Object value = helper.getValue(o, f);
        if (value != null) {
            String svalue = fac.convertToString(d, value);
            if (this.escaped) {
                svalue = convert(svalue);
            }
            doc.addAttribute(helper.getQName(f), svalue);
        }
    }

    /**<p>
     * Overridden solely to handle defect 11213.  Code is identical to superclass except for if block regarding
     * <code>escaped</code> variable.
     * </p>
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveDataTypeMany(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.0
     */
    @Override
    protected void saveDataTypeMany(EObject o, EStructuralFeature f) {
        EDataType d = (EDataType)f.getEType();
        EPackage ePackage = d.getEPackage();
        EFactory fac = ePackage.getEFactoryInstance();

        List values = (List)helper.getValue(o, f);
        int size = values.size();
        if (size > 0)
        {
          String name = helper.getQName(f);
          for (int i = 0; i < size; ++i)
          {
            Object value = values.get(i);
            if (value == null)
            {
              doc.startElement(name);
              doc.addAttribute(XSI_NIL, "true"); //$NON-NLS-1$
              doc.endEmptyElement();
              declareXSI = true;
            }
            else
            {
              doc.startElement(name);
              String svalue = fac.convertToString(d, value);
              if (this.escaped)
              {
                svalue = convert(svalue);
              }
              doc.endContentElement(svalue);
            }
          }
        }
    }

    @Override
    protected void saveElementID(EObject o) {
        String uuid = this.xmiResource.getID(o);
        if (uuid != null) {
            doc.addAttribute(XMI_UUID_NS, uuid);
        }
        saveFeatures(o);
    }

    /**
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveElementReference(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    protected void saveElementReference(final EObject remote, final EStructuralFeature f) {
        if (remote instanceof Identifiable) {
            String name = helper.getQName(f);
            String href = ((Identifiable)remote).getUuid();
            if (href != null) {
              href = href.replace(ObjectID.DELIMITER,UUID_PROTOCOL_DELIMITER);
              doc.startElement(name);
              doc.endContentElement(href);
            }
            return;
        }
        super.saveElementReference(remote, f);
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveHref(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    protected void saveHref(final EObject remote, final EStructuralFeature f) {
        if ( saveDatatypeHref(remote,f) ) {
            return;
        }
        if ( saveIdentifiableHref(remote,f) ) {
            return;
        }
        if ( saveXmlSchemaHref(remote,f) ) {
            return;
        }
        super.saveHref(remote,f);
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveIDRefSingle(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    protected void saveIDRefSingle(final EObject eObject, final EStructuralFeature f) {
        EObject value = (EObject)helper.getValue(eObject, f);
        if (value != null && value instanceof Identifiable){
            String name = helper.getQName(f);
            String id = ((Identifiable)value).getUuid();
            id = id.replace(ObjectID.DELIMITER,UUID_PROTOCOL_DELIMITER);
            doc.addAttribute(name, id);
        }else{
            super.saveIDRefSingle(eObject, f);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveIDRefMany(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    protected void saveIDRefMany(final EObject eObject, EStructuralFeature f) {
        InternalEList values = (InternalEList)helper.getValue(eObject, f);
        if (!values.isEmpty()) {
            String name = helper.getQName(f);
            StringBuffer ids = new StringBuffer(values.size() * 10);
            for (Iterator i = values.basicIterator();;) {
                EObject value = (EObject)i.next();

                String id = null;
                if (value instanceof Identifiable) {
                    id = ((Identifiable)value).getUuid();
                    id = id.replace(ObjectID.DELIMITER,UUID_PROTOCOL_DELIMITER);
                } else {
                    id = helper.getIDREF(value);
                }

                ids.append(id);
                if (i.hasNext()) {
                    ids.append(" "); //$NON-NLS-1$
                } else {
                    break;
                }
            }
            doc.addAttribute(name, ids.toString());
        }
    }

	/**
	 * Defect 13086: saveHRefMany method in org.eclipse.emf.ecore.xmi.impl.XMISaveImple is trying
	 * to resolve proxys before trying to save it, resolving proxys is causing the resource for
	 * the proxy to load (which trie to refresh the IResource in the workspace, that is blocked on the save
	 * resulting in the dead lock situation), overridden this method in
	 * MtkXMISaveImpl not to resolve proxys.
	 */
	@Override
    protected void saveHRefMany(EObject o, EStructuralFeature f)
	{
	  InternalEList values = (InternalEList)helper.getValue(o, f);

	  for (Iterator basicIterator = values.basicIterator();basicIterator.hasNext(); )
	  {
		saveHref((EObject)basicIterator.next(), f);
	  }
	}

    //============================================================================================================================
    // Utility Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private String convert(final String value) {
        if (value == null) {
            return null;
        }
        final char[] chrs = new char[value.length()];
        value.getChars(0, chrs.length, chrs, 0);
        final StringBuffer newVal = new StringBuffer();
        for (int ndx = 0, len = chrs.length;  ndx < len;  ++ndx) {
            final char chr = chrs[ndx];
            switch (chr) {
                case '&': {
                    newVal.append(AMP);
                    break;
                }
                case '<': {
                    newVal.append(LT);
                    break;
                }
                case '"': {
                    newVal.append(QUOT);
                    break;
                }
                default: {
                    if (chr < ' '  ||  chr > 0x7F) {
                        newVal.append(HEX_PREFIX + Integer.toHexString(chr) + ';');
                    } else {
                        newVal.append(chr);
                    }
                }
            }
        }
        return newVal.toString();
    }

    private boolean saveDatatypeHref(final EObject remote, final EStructuralFeature f) {
        // Even if 'remote' is a proxy, the XMI files for an XML document model always
        // have the 'xsi:type=<metaclass>' attribute for XSD components and for Datatype refs;
        // thus, 'remote' will indeed be an instance of the correct Java class
        // (meaning we can do 'instanceof')
        if (dtMgr.isBuiltInDatatype(remote) ) {
            final XSDSimpleTypeDefinition datatype = (XSDSimpleTypeDefinition)remote;
            final String name = helper.getQName(f);
            final boolean remoteIsProxy = remote.eIsProxy();
            String href = remoteIsProxy ? EcoreUtil.getURI(remote).toString() : datatype.getURI();     // works even if a proxy
            if (BAD_DATATYPE_HREF.equals(href)) {
                final Object[] params = new Object[]{remote};
                final String msg = ModelerCore.Util.getString("MtkXmiSaveImpl.The_href_for_Datatype_0_is_bad._1",params); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.ERROR,msg);
            }
            // If not a proxy ...
            if ( !remoteIsProxy ) {
                // If there is no target namespace defined for this href then
                // prepend the relative path to the emf resource
                if ( href.startsWith(URI_REFERENCE_DELIMITER) && remote.eResource() != null) {
                    Resource eResource = remote.eResource();
                    String resourcePath = WorkspaceResourceFinderUtil.getWorkspaceUri(eResource);
                    if (resourcePath != null) {
                        href = resourcePath + href;
                    } else {
                        href = eResource.getURI().toString() + href;
                    }
                }
            } // otherwose it is a proxy, so just use the proxy's URI (already the href value)
            if (href != null) {
                doc.startElement(name);
                EClass eClass = remote.eClass();

                EClass expectedType = (EClass)f.getEType();
                if (eClass != expectedType && expectedType.isAbstract()) {
                    saveTypeAttribute(eClass);
                }

                doc.addAttribute(XMLResource.HREF, href);
                doc.endEmptyElement();
            }
            return true;
        }
        return false;
    }

    private boolean saveIdentifiableHref(final EObject remote, final EStructuralFeature f) {
        // Even if 'remote' is a proxy, the XMI files for an XML document model always
        // have the 'xsi:type=<metaclass>' attribute for XSD components and for Datatype refs;
        // thus, 'remote' will indeed be an instance of the correct Java class
        // (meaning we can do 'instanceof')
        if (remote instanceof Identifiable) {
            final Identifiable idable = (Identifiable) remote;
            final String name = helper.getQName(f);
            final boolean remoteIsProxy = remote.eIsProxy();
            String href = null;
            if ( !remoteIsProxy ) {
                href = idable.getUuid();
                // Prepend the relative path to the emf resource
                if (remote.eResource() != null) {
                    Resource eResource = remote.eResource();
                    String resourcePath = WorkspaceResourceFinderUtil.getWorkspaceUri(eResource);
                    if (resourcePath != null) {
                        href = resourcePath + URI_REFERENCE_DELIMITER + href;
                    } else {
                        href = eResource.getURI().toString() + URI_REFERENCE_DELIMITER + href;
                    }
                }
            } else {
                // The remote is a proxy, so just use the URI stored in the proxy ...
                href = EcoreUtil.getURI(remote).toString();
            }
            if (href != null) {
                doc.startElement(name);
                EClass eClass = remote.eClass();

                EClass expectedType = (EClass)f.getEType();
                if (eClass != expectedType && expectedType.isAbstract()) {
                    saveTypeAttribute(eClass);
                }

                doc.addAttribute(XMLResource.HREF, href);
                doc.endEmptyElement();
            }
            return false;
        }
        return false;
    }

    private boolean saveXmlSchemaHref(EObject remote, final EStructuralFeature f) {
        // Even if 'remote' is a proxy, the XMI files for an XML document model always
        // have the 'xsi:type=<metaclass>' attribute for XSD components and for Datatype refs;
        // thus, 'remote' will indeed be an instance of the correct Java class
        // (meaning we can do 'instanceof')

        // Get the URI for the object (this works if it's a proxy) ...
        final URI uri = EcoreUtil.getURI(remote);

        // Replace the installation specific XMLSchema URI,
        // e.g. "platform:/plugin/org.eclipse.xsd_1.1.1/cache/www.w3.org/2001/XMLSchema.xsd",
        // with the general XMLSchema URI of "http://www.w3.org/2001/XMLSchema"
        // If the resource is one of the Emf XMLSchema resources then
        // return the specific logical URI for one of those models
        final URI resourceUri = uri.trimFragment();
        final String resourceUriString = resourceUri.toString();
        String href = null;
        if (resourceUriString.startsWith(XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX)) {
            // MagicXMLSchema.xsd suffix on the resource URI
            if (resourceUriString.endsWith(XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX)) {
                href = ModelerCore.XML_MAGIC_SCHEMA_GENERAL_URI + URI_REFERENCE_DELIMITER + uri.fragment();
            }
            // XMLSchema.xsd suffix on the resource URI
            else if (resourceUriString.endsWith(XML_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX)) {
                href = ModelerCore.XML_SCHEMA_GENERAL_URI + URI_REFERENCE_DELIMITER + uri.fragment();
            }
            // XMLSchema-instance.xsd suffix on the resource URI
            else if (resourceUriString.endsWith(XML_SCHEMA_INSTANCE_ECLIPSE_PLATFORM_URI_SUFFIX)) {
                href = ModelerCore.XML_SCHEMA_INSTANCE_GENERAL_URI + URI_REFERENCE_DELIMITER + uri.fragment();
            }
        }
        // If the href is still null, just use the remote object's URI
        if ( href == null ) {
            // The URI wasn't to a global XSD resource, so return false and let the
            // super.saveHref(...) method handle it; see this.saveHref(...)
            return false;
        }
        String name    = helper.getQName(f);
        doc.startElement(name);
        EClass eClass = remote.eClass();

        EClass expectedType = (EClass)f.getEType();
        if (eClass != expectedType && expectedType.isAbstract()) {
            saveTypeAttribute(eClass);
        }

        doc.addAttribute(XMLResource.HREF, href);
        doc.endEmptyElement();
        return true;
    }
}
