/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.Identifiable;
import com.metamatrix.modeler.core.ModelerCore;

/**<p>
 * </p>
 * @since 4.0
 */
public class EResourceXmiSaveImpl extends XMISaveImpl {

    protected static final char UUID_PROTOCOL_DELIMITER = '/';
    protected static final String XMI_UUID = "uuid"; //$NON-NLS-1$
    protected static final String XMI_UUID_NS = XMIResource.XMI_NS + ":" + XMI_UUID; // xmi:uuid //$NON-NLS-1$
    protected static final String BAD_DATATYPE_HREF = "#null"; //$NON-NLS-1$

    private static final String AMP  = "&amp;"; //$NON-NLS-1$
    private static final String LT   = "&lt;"; //$NON-NLS-1$
    private static final String QUOT = "&quot;"; //$NON-NLS-1$

    private static final String HEX_PREFIX = "&#x"; //$NON-NLS-1$

    private static final boolean DEBUG = false;

    /**
     * Exists to handle defect 11213.
     * @since 4.0
     */
    private boolean escaped;

    private final EResourceImpl eResource;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**<p>
     * </p>
     * @since 4.0
     */
    public EResourceXmiSaveImpl(XMLHelper helper, EResourceImpl theEResource) {
        super(helper);
        CoreArgCheck.isNotNull(theEResource);
        this.eResource = theEResource;
    }

    //==================================================================================
    //                   O V E R R I D D E N   M E T H O D S
    //==================================================================================

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
        String id = helper.getID(o);
        if (id != null) {
            doc.addAttribute(idAttributeName, id);
        }
        String uuid = ModelerCore.getObjectIdString(o);
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

        if ( saveConvertedHref(remote,f) ) {
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
        } else {
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

    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================

    /**<p>
     * </p>
     * @since 4.0
     */
    protected String convert(final String value) {
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


    protected boolean saveConvertedHref(final EObject remote, final EStructuralFeature f) {

        // If the resource is the built-in datatypes resource then we possibly need to convert
        // from a physical URI to a logical URI.  For example, if the physical URI was
        // "file:/E:/.../cache/www.w3.org/2001/XMLSchema.xsd#//string;XSDSimpleTypeDefinition=7"
        // we need to remap this to the logical URI of "http://www.w3.org/2001/XMLSchema#string"
        URI logicalUri = null;
        if (eResource.getResourceSet() instanceof EResourceSetImpl) {
            EResourceSetImpl rs = (EResourceSetImpl)eResource.getResourceSet();
            if (rs.getEObjectHrefConverter() != null) {
                logicalUri = rs.getEObjectHrefConverter().getLogicalURI(remote);
            }
        }

        if (logicalUri != null) {
            String href = logicalUri.toString();
            String name = helper.getQName(f);
            doc.startElement(name);
            EClass eClass = remote.eClass();

            EClass expectedType = (EClass)f.getEType();
            if (eClass != expectedType && expectedType.isAbstract()) {
                saveTypeAttribute(eClass);
            }

            doc.addAttribute(XMLResource.HREF, href);
            doc.endEmptyElement();

            if (DEBUG) {
                ModelerCore.Util.log("EResourceXmiSaveImpl.saveConvertedHref(): " + EcoreUtil.getURI(remote) + " -> " + href); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return true;
        }
        return false;
    }

}
