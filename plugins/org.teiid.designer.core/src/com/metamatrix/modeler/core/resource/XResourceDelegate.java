/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.teiid.core.id.IDGenerator;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;

/**
 * @author John Verhaeg
 */
public final class XResourceDelegate extends XmlXResourceDelegate {

    private Map<String, EObject> uuidToEObjectMap = new HashMap<String, EObject>();
    private Map<EObject, String> eObjectToUuidMap = new HashMap<EObject, String>();

    public void attachedHelper( EObject eObject ) {
        String uuid = eObjectToUuidMap.get(eObject);
        if (uuid == null) {
            uuid = MtkXmiResourceImpl.DETACHED_EOBJECT_TO_UUID_MAP.remove(eObject);
            if (uuid == null) {
                uuid = IDGenerator.getInstance().create().toString();
            } else {
                MtkXmiResourceImpl.DETACHED_UUID_TO_EOBJECT_MAP.remove(uuid);
            }
            setUuid(eObject, uuid);
        } else {
            uuidToEObjectMap.put(uuid, eObject);
        }
    }

    /**
     * Unsupported operation.
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see com.metamatrix.modeler.core.resource.XmlXResourceDelegate#attachedHelper(org.eclipse.emf.ecore.xmi.XMLResource,
     *      org.eclipse.emf.ecore.EObject)
     */
    @Override
    public void attachedHelper( XMLResource resource,
                                EObject object ) {
        throw new UnsupportedOperationException();
    }

    public void detachedHelper( EObject eObject ) {
        String uuid = getUuid(eObject);
        MtkXmiResourceImpl.DETACHED_EOBJECT_TO_UUID_MAP.put(eObject, uuid);
        MtkXmiResourceImpl.DETACHED_UUID_TO_EOBJECT_MAP.put(uuid, eObject);
        setUuid(eObject, null);
    }

    /**
     * Unsupported operation.
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see com.metamatrix.modeler.core.resource.XmlXResourceDelegate#detachedHelper(org.eclipse.emf.ecore.xmi.XMLResource,
     *      org.eclipse.emf.ecore.EObject)
     */
    @Override
    public void detachedHelper( XMLResource resource,
                                EObject object ) {
        throw new UnsupportedOperationException();
    }

    public void doUnload() {
        eObjectToUuidMap.clear();
        uuidToEObjectMap.clear();
    }

    public EObject getEObjectById( String uuid ) {
        return uuidToEObjectMap.get(uuid);
    }

    public String getUuid( EObject eObject ) {
        return eObjectToUuidMap.get(eObject);
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see com.metamatrix.modeler.core.resource.XmlXResourceDelegate#initialize(org.eclipse.emf.ecore.xmi.XMLResource)
     */
    @Override
    public void initialize( XMLResource resource ) {
        throw new UnsupportedOperationException();
    }

    public void setUuid( EObject eObject,
                         String uuid ) {
        Object oldUuid = uuid != null ? eObjectToUuidMap.put(eObject, uuid) : eObjectToUuidMap.remove(eObject);

        if (oldUuid != null) {
            uuidToEObjectMap.remove(oldUuid);
        }

        if (uuid != null) {
            uuidToEObjectMap.put(uuid, eObject);
        }
    }

    public String toKeyString() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().toString());
        if (uuidToEObjectMap != null) {
            TreeMap<String, String> tree = new TreeMap<String, String>();
            for (String key : uuidToEObjectMap.keySet()) {
                if (key != null) {
                    tree.put(key, key);
                }
            }

            // add the key/value pairs to the output string
            for (String key : tree.values()) {
                Object value = uuidToEObjectMap.get(key);
                result.append("\r\n\t[").append(key).append("=").append(value).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        return result.toString();
    }
}
