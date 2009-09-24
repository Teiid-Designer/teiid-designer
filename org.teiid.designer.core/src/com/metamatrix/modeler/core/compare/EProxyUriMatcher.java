/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

/** 
 * This matcher compares two eObjects one or both of which are eProxys.
 * @since 4.2
 */
public class EProxyUriMatcher extends AbstractEObjectMatcher {

    /** 
     * 
     * @since 4.2
     */
    public EProxyUriMatcher() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots(final List inputs,
                                    final List outputs,
                                    final Mapping mapping,
                                    final MappingFactory factory) {
        addMappings(null,inputs,outputs,mapping,factory);        
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappings(final EReference reference,
                            final List inputs,
                            final List outputs,
                            final Mapping mapping,
                            final MappingFactory factory) {

        final Map inputByCompareString = new HashMap();
        final Map proxyinputByCompareString = new HashMap();
        // Loop over the inputs and accumulate the uri compare strings ...        
        for (final Iterator iter = inputs.iterator();iter.hasNext();) {
            final EObject obj = (EObject)iter.next();
            // get the key which for a proxy eObject
            String key = getProxyURICompareString(obj);
            if ( key != null ) {
                proxyinputByCompareString.put(key,obj);
            } else {
                // get the key which for a non-proxy eObject
                key = getURICompareString(obj);
                inputByCompareString.put(key,obj);
            }
        }

        // Loop over the outputs and compare the uris ...
        for (final Iterator outputIter = outputs.iterator();outputIter.hasNext();) {
            final EObject output = (EObject)outputIter.next();
            EObject inputEntity = null;
            // get the key which for a proxy eObject            
            String key = getProxyURICompareString(output);
            // if the object is a proxy then the input may be a proxy or non-proxy
            if ( key != null ) {
                inputEntity = (EObject) inputByCompareString.get(key);
                if(inputEntity != null) {
                    inputEntity = (EObject) proxyinputByCompareString.get(key);
                }
            } else {
                // get the key which for a non-proxy eObject                
                key = getURICompareString(output);
                // input has to be a proxy as the object is a non-proxy
                inputEntity = (EObject) proxyinputByCompareString.get(key);
            }
            if ( inputEntity != null ) {
                final EClass inputMetaclass = inputEntity.eClass();
                final EClass outputMetaclass = output.eClass();
                if ( inputMetaclass.equals(outputMetaclass) ) {
                    inputs.remove(inputEntity);
                    outputIter.remove();
                    addMapping(inputEntity,output,mapping,factory);
                }
            }
        }
    }

    /**
     * The uri portion to compare is the file name and the fragment inside the file,
     * @param uri The uri of input or output key which needs to be compared.
     * @return The comparision string
     * @since 4.2
     */
    protected String getURICompareString(final EObject eObject) {
        URI uri = EcoreUtil.getURI(eObject);
        return getURICompareString(uri);
    }

    protected String getProxyURICompareString(final EObject entity) {
        if(entity != null && entity.eIsProxy()) {
            if(entity instanceof InternalEObject) {
	            InternalEObject internalEObj = (InternalEObject) entity;
	            URI proxyURI = internalEObj.eProxyURI();
	            return getURICompareString(proxyURI);
            }
        }
        return null;
    }

    /**
     * The uri portion to compare is the file name and the fragment inside the file,
     * @param uri The uri of input or output key which needs to be compared.
     * @return The comparision string
     * @since 4.2
     */
    protected String getURICompareString(final URI uri) {
        if(uri != null) {
	        String lastFragment = uri.lastSegment();
	        String fragment = uri.fragment();
	        if(lastFragment != null && fragment != null) {
	            return lastFragment + fragment;
	        }
	        return lastFragment;
        }
        return null;
    }
}
