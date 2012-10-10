/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.WorkbenchException;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;



/** This class provides various IMarker utilities that wrap IMarker calls to allow catching certain exceptions,
 * logging additional information, allow additional input object checking (i.e. resources, etc..)
 * @since 8.0
 */
public abstract class MarkerUtilities {
    
    /**
     * A value indicating that no problem markers were found.
     * @since 5.0
     */
    public static final int SEVERITY_OK = -1;
    
    /**
     * Helper method to generically wrap the IMarker.getAttribute() method so ResourceException can be caught and managed
     * 
     * @param iMarker
     * @param attributeName
     * @return attribute
     * @since 4.2
     */
    public static Object getMarkerAttribute(IMarker iMarker,
                                            String attributeName) {
        Object attribute = null;
        if( iMarker != null ) {
            try {
                attribute = iMarker.getAttribute(attributeName);
            } catch (CoreException e) {
                // ResourceException is caught here because some calls to getAttribute() may be on an IMarker who's resource
                // does not exist in the workspace any more.  (Defect 15552)
                if (e instanceof ModelerCoreException || 
                		e instanceof WorkbenchException) {
                    String message = UiConstants.Util.getString("MarkerUtilities.getMarkerAttribute.errorMessage", attributeName); //$NON-NLS-1$
                    UiConstants.Util.log(IStatus.ERROR, e, message);
                }
            }
        }
        return attribute;
    }
    
    /**
     * Helper method to generically wrap the IMarker.getAttribute() method so ResourceException can be caught and managed.
     * This method provides an additional check if input model resource != null to verify that it exists() and isOpen()
     * @param iMarker
     * @param attributeName
     * @param resource
     * @return attribute
     * @since 4.2
     */
    public static Object getMarkerAttribute(IMarker iMarker,
                                            String attributeName, ModelResource resource ) {
        if( resource == null || (resource.exists() && resource.isOpen()) ) {
            return getMarkerAttribute(iMarker, attributeName);
        }
        
        return null;
    }

    public static int getMarkerStatus(IStatus status) {
        int sev;
        switch (status.getSeverity()) {
            case IStatus.ERROR:
              sev = IMarker.SEVERITY_ERROR;
            break;
    
            case IStatus.WARNING:
              sev = IMarker.SEVERITY_WARNING;
            break;
    
            case IStatus.INFO:
              sev = IMarker.SEVERITY_INFO;
            break;
    
            default:
              sev = -1;
            break;
        } // endswitch
    
        return sev;
    }
    
    /**
     * Obtains the worst severity of all the problem markers for the specified resource. 
     * @param theResource the resource whose problem markers are being checked
     * @return the severity
     * @throws CoreException if problem finding the problem markers
     * @see #SEVERITY_OK
     * @see IMarker#SEVERITY_ERROR
     * @see IMarker#SEVERITY_WARNING
     * @see IMarker#SEVERITY_INFO
     * @see IResource#findMarkers(java.lang.String, boolean, int)
     * @since 5.0
     */
    public static int getWorstMarkerSeverity(IResource theResource) throws CoreException {
        int result = SEVERITY_OK;
        IMarker[] markers = theResource.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
        
        if ((markers != null) && (markers.length != 0)) {
            for (int i = 0; i < markers.length; ++i) {
                Object attr = MarkerUtilities.getMarkerAttribute(markers[i], IMarker.SEVERITY);
                
                if (attr != null) {
                    int severity = ((Integer)attr).intValue();
                    
                    if (severity == IMarker.SEVERITY_ERROR) {
                        result = IMarker.SEVERITY_ERROR;
                        break;
                    }
                    
                    if (result == SEVERITY_OK) {
                        result = severity;
                    } else if ((severity == IMarker.SEVERITY_WARNING) && (result == IMarker.SEVERITY_INFO)) {
                        result = severity;
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Obtains the worst severity of all the problem markers for any of the specified resources. 
     * @param theResources the resources whose problem markers are being checked
     * @return the severity
     * @throws CoreException if problem finding the problem markers
     * @see #SEVERITY_OK
     * @see #getWorstMarkerSeverity(IResource)
     * @see IMarker#SEVERITY_ERROR
     * @see IMarker#SEVERITY_WARNING
     * @see IMarker#SEVERITY_INFO
     * @see IResource#findMarkers(java.lang.String, boolean, int)
     * @since 5.0
     */
    public static int getWorstMarkerSeverity(IResource[] theResources) throws CoreException {
        int result = SEVERITY_OK;
        
        if ((theResources != null) && (theResources.length != 0)) {
            for (int i = 0; i < theResources.length; ++i) {
                int severity = getWorstMarkerSeverity(theResources[i]);
                
                if (severity == IMarker.SEVERITY_ERROR) {
                    result = IMarker.SEVERITY_ERROR;
                    break;
                }
                
                if (result == SEVERITY_OK) {
                    result = severity;
                } else if ((severity == IMarker.SEVERITY_WARNING) && (result == IMarker.SEVERITY_INFO)) {
                    result = severity;
                }
            }
        }
            
        return result;
    }
    
    /**
     * Obtains the decoration image appropriate for the specified marker severity. Images are provided
     * for errors and warnings only.
     * @param theMarkerSeverity the severity
     * @return the image or <code>null</code>
     * @since 5.0
     * @see IMarker#SEVERITY_ERROR
     * @see IMarker#SEVERITY_WARNING
     */
    public static ImageDescriptor getDecorationIcon(int theMarkerSeverity) {
        ImageDescriptor result = null;
        
        if (theMarkerSeverity == IMarker.SEVERITY_ERROR) {
            result = UiPlugin.getDefault().getErrorDecoratorImage();
        } else if (theMarkerSeverity == IMarker.SEVERITY_WARNING) {
            result = UiPlugin.getDefault().getWarningDecoratorImage();
        }
        
        return result;
    }
        
}
