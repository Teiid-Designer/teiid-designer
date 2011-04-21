/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.id.UUID;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.IPasteContributor;

/**
 * @since 4.3.1
 */
public class PasteContributor implements
                             IPasteContributor {

    // ===========================================================================================================================
    // Controller Methods

    /**
     * @see com.metamatrix.modeler.core.metamodel.IPasteContributor#contribute(java.util.Map, java.lang.String)
     * @since 4.3.2
     */
    public void contribute(final Map map,
                           final String uri) {
        // Contribute if pasting into an XML Document model
        if (XmlDocumentPackage.eNS_URI.equals(uri)) {
            // Create UUID map for copied objects
            final Map uuidMap = new HashMap(map.size());
            for (final Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                final Entry entry = (Entry)iter.next();
                final Object obj = entry.getKey();
                if (obj instanceof EObject) {
                    uuidMap.put(ModelerCore.getObjectIdString((EObject)obj),
					            ModelerCore.getObjectIdString((EObject)entry.getValue()));
                }
            }
            // Replace UUID's of clipboard objects in pasted object choice criteria with UUID's of corresponding pasted objects
            for (final Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                final Entry entry = (Entry)iter.next();
                final Object obj = entry.getValue();
                if (obj instanceof XmlElement) {
                    final XmlElement pastedElem = (XmlElement)obj;
                    final String criteria = pastedElem.getChoiceCriteria();
                    if (criteria != null) {
                        final StringBuffer newCriteria = new StringBuffer();
                        int startNdx = 0;
                        int uuidNdx = criteria.indexOf(UUID.PROTOCOL);
                        while (uuidNdx >= 0) {
                            newCriteria.append(criteria.substring(startNdx, uuidNdx));
                            final int endNdx = uuidNdx + UUID.FQ_LENGTH;
                            final String uuid = criteria.substring(uuidNdx, endNdx);
                            final String pastedUuid = (String)uuidMap.get(uuid);
                            if (pastedUuid == null) {
                            	// Must be either not one of the pasted objects or an invalid UUID to begin with
                                newCriteria.append(uuid);
                            } else {
                                newCriteria.append(pastedUuid);
                            }
                            startNdx = endNdx;
                            uuidNdx = criteria.indexOf(UUID.PROTOCOL, startNdx);
                        }
                        newCriteria.append(criteria.substring(startNdx));
                        pastedElem.setChoiceCriteria(newCriteria.toString());
                    }
                }
            }
        }
    }
}
