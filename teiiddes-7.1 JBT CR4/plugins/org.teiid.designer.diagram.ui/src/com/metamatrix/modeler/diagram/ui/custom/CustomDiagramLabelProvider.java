/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
/**
 * PackageDiagramLabelProvider
 * @since 4.0
 */
public class CustomDiagramLabelProvider extends LabelProvider
implements ILightweightLabelDecorator, DiagramUiConstants, PluginConstants.Images {
    //============================================================================================================================
    // Static Variables
    
    //============================================================================================================================
    // Variables

    private ILabelProvider defaultProvider;
    
    //============================================================================================================================
    // Constructors
    
    /**
     * @since 4.0
     */
    public CustomDiagramLabelProvider() {
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
                     ((Diagram)element).getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID))
                    return DiagramUiPlugin.getDefault().getImage(CUSTOM_DIAGRAM_ICON);
        
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
		if (element instanceof Diagram) {
			if (((Diagram)element).getType() != null
				&& ((Diagram)element).getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID)) {
				String name = ModelerCore.getModelEditor().getName((EObject)element);
				if( name != null )
					return name;
					
				return Util.getString("DiagramNames.customDiagram"); //$NON-NLS-1$
			}
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
                final Object attr = MarkerUtilities.getMarkerAttribute(markers[ndx], IMarker.SEVERITY); //markers[ndx].getAttribute(IMarker.SEVERITY);
                if (attr == null) {
                    continue;
                }
                // Asserting attr is an Integer...
                final int severity = ((Integer)attr).intValue();
                if (severity == IMarker.SEVERITY_ERROR) {
                    icon = DiagramUiPlugin.getDefault().getImageDescriptor(ERROR_ICON);
                    break;
                }
                if (icon == null  &&  severity == IMarker.SEVERITY_WARNING) {
                    icon = DiagramUiPlugin.getDefault().getImageDescriptor(WARNING_ICON);
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

