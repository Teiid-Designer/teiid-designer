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

package com.metamatrix.modeler.core.resource;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.INotifyChangedListener;

/**
 * MetaMatrix implementation of a ResourceSet
 */
public interface EResourceSet extends ResourceSet {

//    /**
//     * Obtain the EObject finder component that is used to find EObject instances 
//     * within the resource set.  
//     * @return the finder for this resource set; never null
//     * @since 4.3
//     */
//    EObjectFinder getEObjectFinder();

//    /**
//     * Set the EObject finder component to be used to find EObject instances 
//     * within the resource set.  
//     * @param theEObjMgr EObjectManager instance to use
//     * @return the finder for this resource set; never null
//     * @since 4.3
//     */
//    void setEObjectFinder(EObjectFinder eObjectFinder);

//    /**
//     * Obtain the resource finder component that is used to find Resource instances 
//     * within the resource set.  
//     * @return the finder for this resource set; never null
//     * @since 4.3
//     */
//    EResourceFinder getEResourceFinder();

//    /**
//     * Set the resource finder component to be used to find Resource instances 
//     * within the resource set.  
//     * @param theEObjMgr EObjectManager instance to use
//     * @return the finder for this resource set; never null
//     * @since 4.3
//     */
//    void setEResourceFinder(EResourceFinder eResourceFinder);
    
    /**
     * Get the EObjectHrefConverter instance for this resource set. 
     * The EObjectHrefConverter is used when either loading or saving a resource
     * containing references to external EObjects.  The converter handles the
     * mappings and inverse mappings between any logical URI reference that may
     * exist and its underlying physical URI reference.  The converter by:
     * <p>
     * <li> getEObject(URI,boolean) on EResourceSet </li>
     * <li> handleProxy(InternalEObject,String) on EResourceXmiHandler </li>
     * <li> saveConvertedHref(EObject,EStructuralFeature) on EResourceXmiSaveImpl </li>
     * </p>
     * @return the converter for this resource set; never null
     * @since 5.0
     */
    EObjectHrefConverter getEObjectHrefConverter();

    /**
     * Set the EObjectHrefConverter instance to use with this resource set. 
     * The EObjectHrefConverter is used when either loading or saving a resource
     * containing references to external EObjects.  The converter handles the
     * mappings and inverse mappings between any logical URI reference that may
     * exist and its underlying physical URI reference.   The converter by:
     * <p>
     * <li> getEObject(URI,boolean) on EResourceSet </li>
     * <li> handleProxy(InternalEObject,String) on EResourceXmiHandler </li>
     * <li> saveConvertedHref(EObject,EStructuralFeature) on EResourceXmiSaveImpl </li>
     * </p>
     * @param theEObjHrefConverter EObjectHrefConverter instance to use
     * @return the finder for this resource set; never null
     * @since 5.0
     */
    void setEObjectHrefConverter(EObjectHrefConverter theConverter);
    
    /**
     * Add a ResourceSet respresenting global resources to be shared with this
     * resource set. Any external resource set is treated as a read-only container 
     * only used for resolving URIs to global or shared resources.  No resources are
     * ever added of removed from an external resource set.  
     * <p>
     * The user may optionally specify a physicalToLogicalUri map.  The map provides
     * this EResourceSet with a conversion between a physical URI, associated with a
     * particular resource existing in the ResourceSet being added, and a logical or
     * alternate URI that can be used in cross-document references.
     * An example of a phyical->logical mapping would have the physical URI of
     * "file:/E:/.../cache/www.w3.org/2001/XMLSchema.xsd" being mapped to the 
     * logical URI of "http://www.w3.org/2001/XMLSchema"
     * 
     * @param resourceSet the resource set to add; may not be null
     * @param physicalToLogicalUri map of resource URIs for the resource set being added to 
     */
    void addExternalResourceSet(ResourceSet resourceSet);
//    void addExternalResourceSet(ResourceSet resourceSet, Map physicalToLogicalUri);
    
    /**
     * Return the array of external resource sets registered with this resource set. 
     * @return
     * @since 4.3
     */
    ResourceSet[] getExternalResourceSets();

    /**
     * Add a listener to the resource set to receive EMF change notifications.
     */
    void addListener(INotifyChangedListener notifyChangedListener);

    /**
     * Remove a listener.
     */
    void removeListener(INotifyChangedListener notifyChangedListener);
    
}
