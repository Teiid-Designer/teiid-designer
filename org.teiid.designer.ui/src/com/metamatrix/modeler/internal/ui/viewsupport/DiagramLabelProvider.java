/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * DiagramLabelProvider is the hook for all Diagram plugins to describe and decorate their Diagram objects
 * in the tree.  Any object of type PresentationEntity will be passed through all extensions of the 
 * diagramProvider extension point to render the object.  The first one that provides a non-null label
 * will be used to provide the label and icon.
 */
public class DiagramLabelProvider implements ILabelProvider, UiConstants.ExtensionPoints.DiagramLabelProvider {

    private HashMap providerMap = new HashMap();
    private ILabelProvider lastProvider;
    private String lastType;
    
    public DiagramLabelProvider() {
        loadProviderList();
    }

    private void loadProviderList() {
        // build a map of all DiagramProvider contributions of type ILabelProvider
        
        // get the DiagramProvider extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, DIAGRAM_ID);
        // get the all extensions to the DiagramProvider extension point
        IExtension[] extensions = extensionPoint.getExtensions();
    
        // walk through the extensions and find all ITreeContentProviders
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            try {
    
                // first, find the content provider instance and add it to the instance list
                ILabelProvider labelProvider = null;
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(DIAGRAM_CLASS)) {
                        Object provider = elements[j].createExecutableExtension(DIAGRAM_CLASSNAME);
                        if ( provider instanceof ILabelProvider ) {
                            labelProvider = (ILabelProvider) provider;
                            break;
                        }
                    }
                }

                // second, build a map referencing all the diagram types that this provider supports
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(DIAGRAM_TYPE)) {
                        String type = elements[j].getAttribute(DIAGRAM_TYPE_NAME);
                        providerMap.put(type, labelProvider);
                    }
                }
                
            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("DiagramLabelProvider.configurationErrorMessage", //$NON-NLS-1$
                            extensions[i].getUniqueIdentifier()); 
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }
    }    



    // =========================================
    // ILabelProvider Methods

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element) {
        Image result = null;
        
        if ( element instanceof Diagram ) {
            String type = ((Diagram) element).getType();
            if ( lastType != null ) {
                if ( lastType.equals(type) ) {
                    try {
                        result = lastProvider.getImage(element);
                    } catch (Exception e) {
                        // catch any Exception that occurred in the diagram provider and log it
                        String message = UiConstants.Util.getString("DiagramLabelProvider.diagramProviderErrorMessage"); //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, e, message);
                    }
                }
            }
            
            if ( result == null ) {
                lastType = null;
                lastProvider = (ILabelProvider) providerMap.get(type);
                if ( lastProvider != null ) {

                    try {
                        result = lastProvider.getImage(element);
                        if ( result != null ) {
                            lastType = type;
                        }
                    } catch (Exception e) {
                        // catch any Exception that occurred in the diagram provider and log it
                        String message = UiConstants.Util.getString("DiagramLabelProvider.diagramProviderErrorMessage"); //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, e, message);
                    }
                        
                }
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element) {
        String result = null;
        
        if ( element instanceof Diagram ) {

            String type = ((Diagram) element).getType();
            if ( lastType != null ) {
                if ( lastType.equals(type) ) {
                    
                    try {                    
                        result = lastProvider.getText(element);
                    } catch (Exception e) {
                        // catch any Exception that occurred in the diagram provider and log it
                        String message = UiConstants.Util.getString("DiagramLabelProvider.diagramProviderErrorMessage"); //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, e, message);
                    }
                    
                }
            }
            
            if ( result == null ) {
                lastType = null;
                lastProvider = (ILabelProvider) providerMap.get(type);
                if ( lastProvider != null ) {
                    try {
                        result = lastProvider.getText(element);
                        if ( result != null ) {
                            lastType = type;
                        }
                    } catch (Exception e) {
                        // catch any Exception that occurred in the diagram provider and log it
                        String message = UiConstants.Util.getString("DiagramLabelProvider.diagramProviderErrorMessage"); //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, e, message);
                    }
                }
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        if ( providerMap != null ) {
            for ( Iterator iter = providerMap.values().iterator() ; iter.hasNext() ; ) {
                ILabelProvider provider = (ILabelProvider) iter.next();
                if ( provider != null ) {
                    try {
                        provider.dispose();
                    } catch (Exception e) {
                        // catch any Exception that occurred in the diagram provider and log it
                        String message = UiConstants.Util.getString("DiagramLabelProvider.diagramProviderErrorMessage"); //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, e, message);
                    }
                }
            }
        }
        lastProvider = null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {

    }

}
