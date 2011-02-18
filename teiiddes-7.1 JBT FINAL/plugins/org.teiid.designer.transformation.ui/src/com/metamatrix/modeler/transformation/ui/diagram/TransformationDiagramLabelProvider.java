/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.diagram;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
/**
 * TransformationDiagramLabelProvider
 * @since 4.0
 */
public class TransformationDiagramLabelProvider extends LabelProvider
implements ILightweightLabelDecorator, UiConstants, PluginConstants.Images {
    //============================================================================================================================
    // Static Variables
    
    public static boolean debug = false;
    
    //============================================================================================================================
    // Variables

    private ILabelProvider defaultProvider;
    
    //============================================================================================================================
    // Constructors
    
    /**
     * @since 4.0
     */
    public TransformationDiagramLabelProvider() {
    }

    //============================================================================================================================
    // Property methods
    
    /**
     * @since 4.0
     */
    private ILabelProvider getDefaultProvider() {
        if ( defaultProvider == null ) { 
            defaultProvider = new WorkbenchLabelProvider();
        }
        return defaultProvider;
    }
    
    //============================================================================================================================
    // Overridden methods

    /**
     * Figures out which provider to delegate to.
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.0
     */
    @Override
    public Image getImage(Object element) {
        try {        

            if ( element instanceof Diagram ) {
                if ( ((Diagram)element).getType() != null && 
                     ((Diagram)element).getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID))
                    return UiPlugin.getDefault().getImage(TRANSFORMATION_DIAGRAM_ICON);
        
            } else {
                return getDefaultProvider().getImage(element);
            }
        
        } catch (final Exception err) {
            Util.log(err);
        }
                
        return super.getImage(element);
    }

    /**
     * Figures out which provider to delegate to.
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.0
     */
    @Override
    public String getText(Object element) {
        if ( element instanceof Diagram ) {
                if ( ((Diagram)element).getType() != null && 
                     ((Diagram)element).getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID))
                    return Util.getString("DiagramNames.transformationDiagram"); //$NON-NLS-1$ 
        } 
        
        return getDefaultProvider().getText(element);
        
    }

    //============================================================================================================================
    // ILightweightLabelDecorator implementation
    
    /**
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     * @since 4.0
     */
    public void decorate(final Object element, final IDecoration decoration) {
        final IResource resrc = getResource(element);
        if (resrc == null 
            || ! resrc.exists() 
            || ( (resrc instanceof IProject) && ! ((IProject) resrc).isOpen() ) ) {
            return;
        }
        try {
            final IMarker[] markers = resrc.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
            ImageDescriptor icon = null;
            for (int ndx = markers.length;  --ndx >= 0;) {
                final Object attr = MarkerUtilities.getMarkerAttribute(markers[ndx], IMarker.SEVERITY);
                if (attr == null) {
                    continue;
                }
                // Asserting attr is an Integer...
                final int severity = ((Integer)attr).intValue();
                if (severity == IMarker.SEVERITY_ERROR) {
                    icon = UiPlugin.getDefault().getImageDescriptor(ERROR_ICON);
                    break;
                }
                if (icon == null  &&  severity == IMarker.SEVERITY_WARNING) {
                    icon = UiPlugin.getDefault().getImageDescriptor(WARNING_ICON);
                }
            }
            if (icon != null) {
                decoration.addOverlay(icon);
            }
        } catch (final CoreException err) {
            Util.log(err);
        }
    }

    //============================================================================================================================
    // Utility methods

    /**
     * Returns the resource for the specified element, or null if there is no resource associated with it.
     * @param element The element for which to find an associated resource
     * @return The resource for the specified element; may be null.
     * @since 4.0
     */
     private IResource getResource(final Object element) {
        if (element instanceof IResource) {
            return (IResource)element;
        }
        if (element instanceof IAdaptable) {
            return (IResource)((IAdaptable)element).getAdapter(IResource.class);
        }
        return null;
     }
}

