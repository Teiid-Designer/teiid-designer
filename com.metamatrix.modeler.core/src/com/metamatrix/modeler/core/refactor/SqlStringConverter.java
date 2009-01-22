/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * SqlStringConverter
 */
public class SqlStringConverter {

	/** Number of characters in a UUID */
	public static final int UUID_STRING_LENGTH = 43;

    /** List containing the global, shared system resources (System.xmi, SystemPhysical.xmi) */
    private static final List SYSTEM_RESOURCES =  Arrays.asList(ModelerCore.getSystemVdbResources());

    /**
     * Converts a String containing UUIDs of the form "mmuuid:2b7de341-7836-1e3f-be17-fda290a3df83"
     * into resolved MetaObject full names as much as possible.  If an object cannot be found
     * in the specified MetadataSession, it is left as a uuid.
     */
    public static String convertUUIDsToFullNames(final String uuidString, final Collection eResources) {
        if (StringUtil.isEmpty(uuidString)) {
            return StringUtil.Constants.EMPTY_STRING;
        }
        final StringBuffer sb = new StringBuffer();
        
        // If there are no UUIDs in the string return 
        int index = uuidString.indexOf(UUID.PROTOCOL);
        if ( index == -1 ) {
            return uuidString;
        }
        
        sb.append(uuidString.substring(0, index));
        while ( index != -1 ) {  
            String id = null;              
            try {
                id = uuidString.substring(index, index + UUID_STRING_LENGTH);
                
                final ObjectID uuid = IDGenerator.getInstance().stringToObject(id, UUID.PROTOCOL);
                final EObject obj = findEObjectInResourceSet(uuid.toString(), eResources);
                if ( obj != null ) {
                    final SqlAspect aspect = AspectManager.getSqlAspect(obj);
                    String name = null;
                    if (aspect != null) {
                        // check the character preceeding UUID.PROTOCOL
                        if (index > 0 && uuidString.charAt(index - 1) == '.') {
                            // If the preceeding character was '.' then this is an 
                            // aliased element symbol, so use the short name
                            name = aspect.getName(obj);
                        } else {
                            name = aspect.getFullName(obj);
                        }
                        if (!StringUtil.isEmpty(name)) {
                            sb.append(name);
                        }
                    }

                } else {
                    sb.append(id);
                    String msg = ModelerCore.Util.getString("SqlStringConverter.unable_to_find_eobject_with_uuid",id); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                }

                final int nextIndex = uuidString.indexOf(UUID.PROTOCOL, index + UUID_STRING_LENGTH);
                if ( nextIndex == -1 ) {
                    sb.append(uuidString.substring(index + UUID_STRING_LENGTH));
                    break;
                }
                sb.append(uuidString.substring(index + UUID_STRING_LENGTH, nextIndex));
                index = nextIndex;
            } catch (InvalidIDException e) {
                ModelerCore.Util.log(e);
                sb.append(id);
                final int nextIndex = uuidString.indexOf(UUID.PROTOCOL, index + UUID_STRING_LENGTH);
                sb.append(uuidString.substring(index + UUID_STRING_LENGTH, nextIndex));
                index = nextIndex;
            }
        }

        return sb.toString();
    }
    
    private static EObject findEObjectInResourceSet(final String uuid, final Collection eResources) {
        for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
            final Resource resource = (Resource)iter.next();
            // Load any EmfResource instance so that UUIDs can be resolved
            if (resource instanceof EmfResource) {
                if (!resource.isLoaded()) {
                    try {
                        resource.load(Collections.EMPTY_MAP);
                    } catch (IOException e) {
                        ModelerCore.Util.log(IStatus.ERROR,e.getLocalizedMessage());
                    }
                }
                final EObject eObj = resource.getEObject(uuid);
                if (eObj != null) {
                    return eObj;
                }
            }
        }
        
        // if not found, check the System Resources
        return findEobjectInSystemResources(uuid);
    }
    
    private static EObject findEobjectInSystemResources(final String uuid) {
        // if not found, check the System Resources
        for (final Iterator iter = SYSTEM_RESOURCES.iterator(); iter.hasNext();) {
            final Resource resource = (Resource)iter.next();
            if (resource instanceof EmfResource) {
                final EObject eObj = resource.getEObject(uuid);
                if (eObj != null) {
                    return eObj;
                }
            }
        }
        return null;
    }
}
