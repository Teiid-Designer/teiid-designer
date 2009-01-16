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

package com.metamatrix.modeler.internal.core.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;


/** This class provides an efficient mechanism to track marker changes for IResources and maps markers to their corresponding
 * or associated EObjects.  This was prompted by use of eclipse's decorator scheduler framework. Our content providers
 * were resolving EObjects for UUID's on ALL markers for a given resource. This was very inefficient. This class keeps track
 * of markers for existing resources and refreshes a map of marker-to-eObject for each resource.
 * @since 5.0
 */
public class ModelMarkerManager implements IResourceChangeListener {

    private HashMap resourceMap = new HashMap();

    /** 
     * 
     * @since 5.0
     */
    public ModelMarkerManager() {
        super();
        if (ResourcesPlugin.getPlugin() != null) {
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        }
    }
    
    /**
     * Responds to resource change events, looks to see if resource/marker/eobject maps need to get refreshed. 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     * @since 5.0
     */
    public void resourceChanged(IResourceChangeEvent event) {

        boolean refreshNeeded = false;

        IMarkerDelta[] markerDeltas = event.findMarkerDeltas(IMarker.PROBLEM, true);
            
        List changes = new ArrayList(markerDeltas.length);

        examineDelta(markerDeltas, changes);

        if (markerDeltas.length != changes.size()) {
            refreshNeeded = true;
        }

        if (  refreshNeeded ) {
            
            List visitedResources = new ArrayList();
            // boolean which will break this method out of it's loop
            // We only need to refresh the diagram once here
            
            List changedResources = new ArrayList();
            
            for( int i=0; i<markerDeltas.length; i++ ) {
                IResource eventResource = markerDeltas[i].getResource();
                // Need to only look at a delta's resource if we haven't looked at it before.
                if( !visitedResources.contains(eventResource) ) {
                    if( ModelUtil.isModelFile(eventResource)) {
                        ModelResource mr = null;
                        try {
                            mr = ModelUtil.getModelResource((IFile)eventResource, false);
                        } catch (ModelWorkspaceException e) {
                            ModelerCore.Util.log(e);
                        }
                        if( mr != null  ) {
                            if( !changedResources.contains(eventResource) ) {
                                changedResources.add(eventResource);
                            }
                        }
                    }
                    visitedResources.add(eventResource);
                }
            }
            refreshMarkers(changedResources);
        }
    }

    protected String[] getMarkerTypes() {
        return new String[] { IMarker.PROBLEM };
    }
    
    /*
     * looks for changed markers
     */
    private static void examineDelta(IMarkerDelta[] deltas, List changes) {
        for (int idx = 0; idx < deltas.length; idx++) {
            IMarkerDelta delta = deltas[idx];
            int kind = delta.getKind();

            if (kind == IResourceDelta.CHANGED) {
                changes.add(deltas[idx].getMarker());
            }
        }
    }
    
    /*
     * refreshes marker maps for changed resources
     */
    private void refreshMarkers(List changedResources) {
        
        clearStaleResources();
        
        // walk through the changed resoures
        
        for( Iterator iter = changedResources.iterator(); iter.hasNext(); ) {
            IResource theIResource = (IResource)iter.next();
            if( theIResource.exists() ) {
                Object obj = resourceMap.get(theIResource);
                HashMap theMarkerMap = null;
    
                // Check resourceMap, if exists, get markerMap, clear it and re-populate with resource markers
                if( obj != null && obj instanceof HashMap ) {
                    theMarkerMap = (HashMap)obj;
                    theMarkerMap.clear();
                }
                
                // If resourceMap doesn't contain markerMap for a resource, create one, add it to the resourceMap and re-populate with 
                // resource markers.
                if( theMarkerMap == null ) {
                    theMarkerMap = new HashMap();
                    resourceMap.put(theIResource, theMarkerMap);
                }
                populateMarkerMap(theMarkerMap, theIResource);
            } else {
                resourceMap.remove(theIResource);
            }

        }
        
        

    }
    
    /*
     * Clears the marker maps for stale/non-existing resources
     */
    private void clearStaleResources() {
        List staleKeys = new ArrayList();
        for( Iterator iter = resourceMap.keySet().iterator(); iter.hasNext(); ) {
            Object nextKey = iter.next();
            if( nextKey instanceof IResource ) {
                if( !((IResource)nextKey).exists() ) {
                    staleKeys.add(nextKey);
                }
            }
        }
        for( Iterator iter = staleKeys.iterator(); iter.hasNext(); ) {
            resourceMap.remove(iter.next());
        }
    }
    
    /*
     * re-populates the marker-to-eobject map for a given resource
     */
    private void populateMarkerMap(HashMap markerMap, IResource theResource) {
        IMarker[] markers = null;
        boolean errorOccurred = false;
        try {
            markers = theResource.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
        } catch (CoreException ex) {
            ModelerCore.Util.log(ex);
            errorOccurred = true;
        }
        if (!errorOccurred) {
            // We have the markers, let's create a MAP of EObjects to 
            for (int ndx = markers.length;  --ndx >= 0;) {
                IMarker marker = markers[ndx];
                
                EObject targetEObject = getMarkedEObject(marker);
                if( targetEObject != null ) {
                    markerMap.put(marker, targetEObject);
                }
            }
        }
    }
    

    /**
     * For a given iMarker, this method resolves the eObject from the UUID reference on the marker. 
     * @param resrc
     * @param iMarker
     * @return
     * @since 5.0
     */
    public EObject getMarkedEObject(IResource resrc, IMarker iMarker) {
        ArgCheck.isNotNull(resrc);

        EObject theMarkedEObject = null;
        
        if( resrc.exists() ) {
    
            Object obj = resourceMap.get(resrc);
            HashMap theMarkerMap = null;
    
            // Check resourceMap, if exists, then get the value for the iMarker key
            if( obj != null && obj instanceof HashMap ) {
                theMarkerMap = (HashMap)obj;
                Object value = theMarkerMap.get(iMarker);
                if( value != null && value instanceof EObject ) {
                    theMarkedEObject = (EObject)value;
                }
            }
        }

        return theMarkedEObject;
    }
    
    /*
     * Private method which does the actual EObject resolution
     */
    private EObject getMarkedEObject(IMarker iMarker) {
        EObject target = null;
        String uri = (String)getMarkerAttribute(iMarker, ModelerCore.MARKER_URI_PROPERTY);
        
        if( uri != null ) {
            URI theURI = URI.createURI(uri);
            if( theURI != null ) {
                try {
                    target = ModelerCore.getModelContainer().getEObject(theURI, true);
                    // Need to 
                } catch (CoreException e1) {
                    ModelerCore.Util.log(e1);
                }
            }
        }

        return target; 
    }
    
    /*
     * Private method for obtaining an attribute property from an IMarker
     */
    private Object getMarkerAttribute(IMarker iMarker, String attributeName) {
        Object attribute = null;
        if( iMarker != null ) {
            try {
                attribute = iMarker.getAttribute(attributeName);
            } catch (CoreException e) {
                // ResourceException is caught here because some calls to getAttribute() may be on an IMarker who's resource
                // does not exist in the workspace any more.  (Defect 15552)
                if (!(e instanceof ResourceException)) {
                    String message = ModelerCore.Util.getString("ModelMarkerManager.getMarkerAttribute.errorMessage", attributeName); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, e, message);
                }
            }
        }
        return attribute;
    }
    
    /**
     * Public method used by the owner of an instance of this class. This method provides a mechanism to clear out the marker maps. 
     * 
     * @since 5.0
     */
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        this.resourceMap.clear();
    }
}
