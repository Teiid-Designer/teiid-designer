/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * DiagramDecoratorHandler
 * This class provides the DiagramEditor a way to maintain a map of applicable IMarkers (errors and warnings)
 * for the current diagram.  This is needed because when IMarkers go away (errors and warnings are fixed),
 * there is no easy way to tell which ones were fixed.
 */
public class DiagramDecoratorHandler {
    private DiagramEditor dEditor;
    private HashMap currentMarkerEObjectMap;
    
    /**
     * Construct an instance of DiagramDecoratorHandler.
     * 
     */
    public DiagramDecoratorHandler(DiagramEditor editor) {
        super();
        dEditor = editor;
    }

    public void initialize() {
        //System.out.println(" DDH:  initialize() Refreshing marker list for current diagram ----- START ");
        currentMarkerEObjectMap = new HashMap(getMarkersForDiagramResource(dEditor.getCurrentModel()));
        //System.out.println(" DDH:  initialize() ------------------------------------------ ----- END ");
    }

    public void reset() {
        initialize();
    }
    
    public void clear() {
        currentMarkerEObjectMap = new HashMap();
    }

    // PRIVATE METHODS
    private HashMap getMarkersForDiagramResource(DiagramModelNode diagram) {
        //System.out.println(" DDH:          getMarkerListForDiagram() --- START --------------------");
        HashMap newMap = new HashMap();

        if( diagram != null ) {
    
            IMarker[] markers = null;
            
            IResource resrc = null;
            
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(diagram.getModelObject());
            if (modelResource != null && modelResource.exists() ) {
                resrc = modelResource.getResource();
                
            }
            if( resrc != null ) {
                boolean errorOccurred = false;
                try {
                    markers = resrc.findMarkers(IMarker.PROBLEM, false, 
                            IResource.DEPTH_INFINITE);
                } catch (CoreException ex) {
                    DiagramUiConstants.Util.log(ex);
                    errorOccurred = true;
                }
                if(!errorOccurred) {
                    for (int ndx = markers.length;  --ndx >= 0;) {
                        IMarker marker = markers[ndx];
                        EObject targetEObject = ModelWorkspaceManager.getModelWorkspaceManager().getMarkerManager().getMarkedEObject(resrc, marker);

                        if( targetEObject != null && newMap.get(targetEObject) == null ) {
                            //System.out.println("             --->> Added Marker for EObject = " + ModelerCore.getModelEditor().getName(targetEObject));
                            newMap.put(targetEObject, marker);
                        }
                    }
                }
            }
        }
        //System.out.println(" DDH:          getMarkerListForDiagram() --- END --------------------");
        if( newMap.isEmpty() )
            return new HashMap(1);
            
        return newMap;
    }
    
    public void handleLabelProviderChanged() {
        //System.out.println(" DDH:  handleLabelProviderChanged() --- END --------------------");
        // Get list of new markers
        HashMap newEObjectMap = getMarkersForDiagramResource(dEditor.getCurrentModel());
        HashMap updateableEObjects = new HashMap(newEObjectMap);
        
		if( currentMarkerEObjectMap != null && !currentMarkerEObjectMap.isEmpty() ) {
	        Iterator iter = currentMarkerEObjectMap.keySet().iterator();
	        EObject nextEObject = null;
	        while( iter.hasNext() ) {
	            nextEObject = (EObject)iter.next();
	            // check vs old marker EObjects
	            if( newEObjectMap.get(nextEObject) == null && updateableEObjects.get(nextEObject) == null)
	                updateableEObjects.put(nextEObject, currentMarkerEObjectMap.get(nextEObject));
	        }
		}
        // reset the current EObject list to the new
        currentMarkerEObjectMap = new HashMap(newEObjectMap);
        
        // Then we call update
        dEditor.getModelFactory().handleLabelProviderChanged(dEditor.getCurrentModel(), new ArrayList(updateableEObjects.keySet()));
        
        //System.out.println(" DDH:  handleLabelProviderChanged() --- END --------------------");
    }
    
    public boolean handleResouceChanged() {
        //System.out.println(" DDH:  handleResouceChanged() --- START --------------------");
        
        DiagramModelNode currentDiagramNode = dEditor.getCurrentModel();

        // Check to see if diagram has any errors or warnings
        boolean hasErrorOrWarning = DiagramUiUtilities.hasEObjectsWithErrorsOrWarnings(currentDiagramNode);
        // If diagram has decorators, refresh no matter what
        // if it doesn't have any, and there are markers for this resource, refresh
		if( hasErrorOrWarning ) {
		    return true;
        }
        // If we get this far, check for any new markers
        // Defect 23570 - DON't call initialize() because we don't want to MUCK with the currentMarkerEObjectMap !!!!
        // Just get any markers for the current diagram node.
        HashMap currentMarkers = getMarkersForDiagramResource(currentDiagramNode);
        
        if( !currentMarkers.isEmpty()) {
            //System.out.println(" DDH:  handleResouceChanged() --- END --------------------");
			return true;
		} 
        //System.out.println(" DDH:  handleResouceChanged() --- END --------------------");
		return false;
    }
    
    public int getErrorState(EObject eObj) {
        int errorState = DiagramUiConstants.NO_ERRORS;
        
        if( currentMarkerEObjectMap == null )
            initialize();
        
        Object marker = currentMarkerEObjectMap.get(eObj);
        if( marker != null ) {
            final Object attr = MarkerUtilities.getMarkerAttribute((IMarker)marker, IMarker.SEVERITY); //((IMarker)marker).getAttribute(IMarker.SEVERITY);
            if(attr != null) {
                // Asserting attr is an Integer...
                final int severity = ((Integer)attr).intValue();
                if(severity == IMarker.SEVERITY_ERROR) {
                    errorState = DiagramUiConstants.HAS_ERROR;
                } else if(errorState == DiagramUiConstants.NO_ERRORS &&
                        severity == IMarker.SEVERITY_WARNING) {
                     errorState = DiagramUiConstants.HAS_WARNING;
                }
            } else {
                currentMarkerEObjectMap.remove(eObj);
            }
        }
        
        return errorState;
    }
}
