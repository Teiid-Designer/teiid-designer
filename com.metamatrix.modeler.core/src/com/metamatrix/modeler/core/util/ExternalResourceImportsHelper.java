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

package com.metamatrix.modeler.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/** 
 * @since 5.0
 */
public class ExternalResourceImportsHelper {
  
    /**
     * Method which processes a given notification for model import status. If a notification results in an external reference
     * being set or added a call to addMissingImports() will be made. If an external resource is detected to be removed, the method
     * returns a ProcessNotificationResult which stores the removed external resources.
     * @param theNotification
     * @return
     * @since 5.0
     */
    public static ProcessedNotificationResult processNotification(final Notification theNotification) {


        Resource resource = null;
        Object notifier = theNotification.getNotifier();
        
        // mark the resource modified if the notification is for (1) a resource that is not a member of
        // an external resource set or for (2) an EObject of a resource.
        if (notifier instanceof Resource) {
            // make sure the feature is NOT the modified feature
            resource = (Resource)notifier;
            int featureId = theNotification.getFeatureID(resource.getClass());
            if (theNotification.getEventType() == Notification.REMOVING_ADAPTER 
                || featureId == Resource.RESOURCE__IS_LOADED
                || featureId == Resource.RESOURCE__IS_MODIFIED
                || featureId == Resource.RESOURCE__RESOURCE_SET 
                || isExternalResourceSetMember(resource) ) {
                // DO NOT PROCESS NOTIFICATION. WILL NOT HAVE AN EFFECT ON IMPORTS
                return null;
            }
        } else if (notifier instanceof EObject) {
            resource = ((EObject)notifier).eResource();
        }
        
        // Resource may be null here. Deleting a DiagramEntity, for instance, will result in a notification where the notifier
        // is the DiagramEntity and the "Diagram" reference is being set to NULL, however the DiagramEntity's EResource will 
        // be NULL.  Just return and DO NOT PROCESS.
        if( resource == null ) {
            return null;
        }
        ProcessedNotificationResult result = new ProcessedNotificationResult(resource);
        
        boolean wasProcessed = false;
        
        if( NotificationUtilities.isAdded(theNotification)) {
            // For ADD notifications, we just need to walk the added children and check for any external references. This might
            // happen if you copy/paste a Column with a datatype into a model with columns that have no datatype!!
            EObject[] newChildren = NotificationUtilities.getAddedChildren(theNotification);
            
            boolean importsWereAdded = false;
            
            for(int i=0; i < newChildren.length; i++ ) {
                importsWereAdded = ImportUtilities.addMissingImports(resource, newChildren[i]);
                if( importsWereAdded ) {
                    result.setImportsWereAdded(true);
                }
            }
            wasProcessed = true;
        } else if(NotificationUtilities.isRemoved(theNotification)) {
            // For REMOVE notifications, we need to detect if any external references are included in the deleted objects or any
            // children of those objects.
            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(theNotification);
            Collection externalResources = new HashSet();
            
            // Search for each removed child (recursively)
            for(int i=0; i < removedChildren.length; i++ ) {
                externalResources.addAll(findExternalResourceReferences(resource, removedChildren[i]));
            }
            wasProcessed = true;
            
            // If we find any, we need to make sure rebuildImports is set to TRUE, so the caller knows.
            if( ! externalResources.isEmpty() ) {
                // add the resources to the dereferenced list.
                result.addDereferencedResources(externalResources);
            }
        }
        
        if( ! wasProcessed ) {
            switch( theNotification.getEventType()) {
                case Notification.ADD:
                case Notification.ADD_MANY:
                case Notification.MOVE:
                case Notification.REMOVE:
                case Notification.REMOVE_MANY:
                case Notification.REMOVING_ADAPTER:
                case Notification.RESOLVE:
                break;
                
                case Notification.SET: {
                    // If an EReference feature is being set
                    if( theNotification.getFeature() instanceof EReference ) {
                        Object newValue = theNotification.getNewValue();
                        Object oldValue = theNotification.getOldValue();
                        if( newValue != null && oldValue == null ) {
                            if( newValue instanceof EObject && ((EObject)newValue).eResource() != null ) {
                                Resource refResource = ((EObject)newValue).eResource();
                                boolean importsWereAdded = false;
                                if( resource != refResource && resource instanceof EmfResource) {
                                    importsWereAdded = ImportUtilities.addMissingImport(resource, refResource);
                                    if( importsWereAdded ) {
                                        result.setImportsWereAdded(true);
                                    }
                                }
                            }
                        } else if (newValue != oldValue) {
                            // If newValue is NULL, get Old Value and add the resource to the dereferenced list.
                            if( newValue == null && oldValue != null ) {
                                if( oldValue instanceof EObject && ((EObject)oldValue).eResource() != null ) {
                                    Resource refResource = ((EObject)oldValue).eResource();

                                    if( refResource != null && resource instanceof EmfResource) {
                                        result.addDereferencedResource(refResource);
                                    }
                                    
                                }
                            } else if(newValue != null && oldValue != null ) {
                                // This is the case where we've swapped out an EReference
                                // Need to both ADD an import for the new EReference (if needed) and also
                                // Put the OLD resource up for removal...
                                if( newValue instanceof EObject && ((EObject)newValue).eResource() != null ) {
                                    Resource refResource = ((EObject)newValue).eResource();
                                    boolean importsWereAdded = false;
                                    if( resource != refResource && resource instanceof EmfResource) {
                                        importsWereAdded = ImportUtilities.addMissingImport(resource, refResource);
                                        if( importsWereAdded ) {
                                            result.setImportsWereAdded(true);
                                        }
                                    }
                                }
                                
                                if( oldValue instanceof EObject && ((EObject)oldValue).eResource() != null ) {
                                    Resource refResource = ((EObject)oldValue).eResource();
    
                                    if( refResource != null && resource instanceof EmfResource) {
                                        result.addDereferencedResource(refResource);
                                    }
                                    
                                }
                            }
                        }
                    }
                } break;
                
                case Notification.UNSET: {
                    // Don't know use case??
                } break;
                
                default: break;
            }
        }
        
        return result;
    }
    
    /**
     * Method designed to  
     * @param notificationResultsList
     * @since 5.0
     */
    public static void processNotificationResults(Collection notificationResultsList) {
        // Walk through each result and create a map of changed resources to a Collection of dereferenced external resources
        ProcessedNotificationResult nextResult = null;
        for( Iterator iter = notificationResultsList.iterator(); iter.hasNext(); ) {
            nextResult = (ProcessedNotificationResult)iter.next();
            // For the target resource, walk the model with a list of references. Once a reference is 
            // First create a copy of the procesedNotificationResult
            ProcessedNotificationResult tempResult = new ProcessedNotificationResult(nextResult);
            checkRemainingExternalReferences(tempResult);
            
            // Now if there are ANY REMAINING, we need to remove the imports for these objects
            for( Iterator iter2 = tempResult.getDereferencedResources().iterator(); iter2.hasNext(); ) {
                Resource nextRes = (Resource)iter2.next();
                ImportUtilities.removeExistingImport(tempResult.getTargetResource(), nextRes);
            }
        }
        
    }
    
    /**
     * Indicates if the specified <code>Resource</codee> is a member of an external resource set. 
     * @param theResource the resource being checked
     * @return <code>true</code> if a member; <code>false</code> otherwise.
     * @since 5.0.2
     */
    private static boolean isExternalResourceSetMember(Resource theResource) {
        boolean result = false;
        ResourceSet[] sets = ModelerCore.getExternalResourceSets();

        for (int ndx = sets.length; --ndx >= 0;) {
            ResourceSet set = sets[ndx];
        
            if (theResource.getResourceSet() == set) {
                result = true;
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Find and return a list of external references contain within or below the target provided.
     * This method looks for both EReference and EReference.isMany() properties.
     * @param target Object
     * @return Collection of external referenced EResources
     * @since 5.0
     */
    public static Collection findExternalResourceReferences(final Resource notifyingResource, final Object target) {
        Collection externalResources = new HashSet();
        
        if( target instanceof EObject ) {
            externalResources = findExternalResourceReferences(notifyingResource, (EObject)target);
        } else if( target instanceof Resource ) {
            externalResources = findExternalResourceReferences((Resource)target);
        } else if( target instanceof ModelResource ) {
            externalResources = findExternalResourceReferences((ModelResource)target);
        }
        
        return externalResources;
    }
    
    /**
     * Find and return a list of external references contain within or below the EObject target provided.
     * This method looks for both EReference and EReference.isMany() properties.
     * @param target EObject
     * @return Collection of external referenced EResources
     * @since 5.0
     */
    private static Collection findExternalResourceReferences(final Resource notifyingResource, final EObject target) {
        Resource targetResource = target.eResource();
        
        // resource == null, then it was deleted and we need to use the notifyingResource
        if( targetResource == null ) {
            targetResource = notifyingResource;
        }
        
        Collection externalResources  = new HashSet();

        // Iterate over all the features that are non-containment ...
        for (Iterator iter = target.eAllContents(); iter.hasNext();) {
            EObject eObject = (EObject) iter.next();
            
            // Iterate over all the EReferences looking for external resource references
            for (Iterator iter2 = eObject.eClass().getEAllReferences().iterator(); iter2.hasNext();) {
                final EReference eReference = (EReference)iter2.next();
                
                if ( !eReference.isContainment() && !eReference.isContainer() ) {
                    // The reference is NOT the container NOR a containment feature ...
                    final Object value = eObject.eGet(eReference,false);
                        
                    if ( eReference.isMany() ) {
                        // There may be many values ...
                        final Iterator valueIter = ((List)value).iterator();
                        while (valueIter.hasNext()) {
                            final Object valueInList = valueIter.next();
                            if ( valueInList != null && valueInList instanceof EObject ) {
                                final Resource valueResource = ((EObject)valueInList).eResource();
                                if (targetResource != valueResource && !externalResources.contains(valueResource)) {
                                    externalResources.add(valueResource);
                                }
                            }
                        }
                        
                    } else {
                        // There may be 0..1 value ...
                        if ( value != null && value instanceof EObject ) {
                            EObject eObj = (EObject)value;
                            if( eObj.eIsProxy() ) {
                                eObj = EcoreUtil.resolve(eObj, targetResource.getResourceSet());
                            }
                            final Resource valueResource = eObj.eResource();
                            
                            if (valueResource != null && targetResource != valueResource && !externalResources.contains(valueResource)) {
                                externalResources.add(valueResource);
                            }
                        }
                    }
                }
            }
        }

        // Check cross references
        for (EContentsEList.FeatureIterator featureIterator = 
            (EContentsEList.FeatureIterator)target.eCrossReferences().iterator();
                featureIterator.hasNext();  ) {
            EObject eObject = (EObject)featureIterator.next();
            Resource refResource = eObject.eResource();
            externalResources.add(refResource);
        }



        return externalResources;
    }
    
    /**
     * Find and return a list of external references contain within or below the EObject target provided.
     * This method looks for both EReference and EReference.isMany() properties.
     * @param target EObject
     * @return Collection of external referenced EResources
     * @since 5.0
     */
    private static void checkRemainingExternalReferences(
                                          final Resource notifyingResource,
                                          final EObject target,
                                          final ProcessedNotificationResult tempResult) {
        Resource targetResource = target.eResource();
        
        // resource == null, then it was deleted and we need to use the notifyingResource
        if( targetResource == null ) {
            targetResource = notifyingResource;
        }

        // Iterate over all the features that are non-containment ...
        for (Iterator iter = target.eAllContents(); iter.hasNext();) {
            EObject eObject = (EObject) iter.next();
            
            // Iterate over all the EReferences looking for external resource references
            for (Iterator iter2 = eObject.eClass().getEAllReferences().iterator(); iter2.hasNext();) {
                final EReference eReference = (EReference)iter2.next();
                
                if ( !eReference.isContainment() && !eReference.isContainer() ) {
                    // The reference is NOT the container NOR a containment feature ...
                    final Object value = eObject.eGet(eReference,false);
                        
                    if ( eReference.isMany() ) {
                        // There may be many values ...
                        final Iterator valueIter = ((List)value).iterator();
                        while (valueIter.hasNext()) {
                            final Object valueInList = valueIter.next();
                            if ( valueInList != null && valueInList instanceof EObject ) {
                                final Resource valueResource = ((EObject)valueInList).eResource();
                                // If this resource is contained in the tempResult, assume that it still needs to be referenced
                                // as an Import, so we can remove it from the tempResult search
                                if( tempResult.getDereferencedResources().contains(valueResource)) {
                                    tempResult.removeDereferencedResource(valueResource);
                                }
                            }
                            if( tempResult.getDereferencedResources().isEmpty() ) {
                                // IF there are NO MORE external references to search for, we return, our job is done.
                                return;
                            }
                        }
                        
                    } else {
                        // There may be 0..1 value ...
                        if ( value != null && value instanceof EObject ) {
                            EObject eObj = (EObject)value;
                            if( eObj.eIsProxy() ) {
                                eObj = EcoreUtil.resolve(eObj, targetResource.getResourceSet());
                            }
                            final Resource valueResource = eObj.eResource();
                            // If this resource is contained in the tempResult, assume that it still needs to be referenced
                            // as an Import, so we can remove it from the tempResult search
                            if( tempResult.getDereferencedResources().contains(valueResource)) {
                                tempResult.removeDereferencedResource(valueResource);
                            }
                        }
                    }
                }
                if( tempResult.getDereferencedResources().isEmpty() ) {
                    // IF there are NO MORE external references to search for, we return, our job is done.
                    return;
                }
            }
            if( tempResult.getDereferencedResources().isEmpty() ) {
                // IF there are NO MORE external references to search for, we return, our job is done.
                return;
            }
        }

        // Check cross references
        for (EContentsEList.FeatureIterator featureIterator = 
            (EContentsEList.FeatureIterator)target.eCrossReferences().iterator();
                featureIterator.hasNext();  ) {
            EObject eObject = (EObject)featureIterator.next();
            Resource refResource = eObject.eResource();
            if( tempResult.getDereferencedResources().contains(refResource)) {
                tempResult.removeDereferencedResource(refResource);
            }
            if( tempResult.getDereferencedResources().isEmpty() ) {
                // IF there are NO MORE external references to search for, we return, our job is done.
                return;
            }
        }
    }
    
    private static Collection findExternalResourceReferences(final ModelResource modelResource) {
        Resource eResource = null;
        
        try {
            eResource = modelResource.getEmfResource();
        } catch (ModelWorkspaceException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
        }
        
        if( eResource != null ) {
            return findExternalResourceReferences(eResource);
        }
        
        return Collections.EMPTY_LIST;
    }
    
    private static Collection findExternalResourceReferences(final Resource resource) {
        Collection externalResources = new HashSet();
        
        EList modelContents = resource.getContents();
        for(Iterator iter = modelContents.iterator(); iter.hasNext(); ) {
            externalResources.addAll(findExternalResourceReferences(resource, (EObject)iter.next()));
        }
        
        return externalResources;
    }
    
    private static void checkRemainingExternalReferences(final ProcessedNotificationResult tempResult) {
        Resource resource = tempResult.getTargetResource();
        EList modelContents = resource.getContents();
        for(Iterator iter = modelContents.iterator(); iter.hasNext(); ) {
            checkRemainingExternalReferences(resource, (EObject)iter.next(), tempResult);
            if( tempResult.getDereferencedResources().isEmpty() ) {
                // IF there are NO MORE external references to search for, we return, our job is done.
                return;
            }
        }
    }

}
