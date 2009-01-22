/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.ui.UiConstants;


/** 
 * @since 5.0
 */
public class ExtendedModelObjectLabelProvider implements ILabelProvider, UiConstants.ExtensionPoints.ExtendedModelLabelProvider {

    private ArrayList extendedModelProviders = new ArrayList();
    
    public ExtendedModelObjectLabelProvider() {
        loadProviderList();
    }

    private void loadProviderList() {
        // build a map of all DiagramProvider contributions of type ILabelProvider
        
        // get the DiagramProvider extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENDED_MODEL_ID);
        // get the all extensions to the DiagramProvider extension point
        IExtension[] extensions = extensionPoint.getExtensions();
    
        // walk through the extensions and find all ITreeContentProviders
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            try {
    
                // first, find the content provider instance and add it to the instance list
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(EXTENDED_MODEL_CLASS)) {
                        Object provider = elements[j].createExecutableExtension(EXTENDED_MODEL_CLASSNAME);
                        if ( provider instanceof ILabelProvider ) {
                            extendedModelProviders.add(provider);
                            break;
                        }
                    }
                }
                
            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("ExtendedModelObjectLabelProvider.configurationErrorMessage", //$NON-NLS-1$
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
        
        
        for ( Iterator iter = extendedModelProviders.iterator() ; iter.hasNext() ; ) {
            ILabelProvider provider = (ILabelProvider) iter.next();
            result = provider.getImage(element);
            if( result != null ) {
                return result;
            }
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element) {
        String result = null;
        
        for ( Iterator iter = extendedModelProviders.iterator() ; iter.hasNext() ; ) {
            ILabelProvider provider = (ILabelProvider) iter.next();
            result = provider.getText(element);
            if( result != null ) {
                return result;
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
        if ( extendedModelProviders != null ) {
            for ( Iterator iter = extendedModelProviders.iterator() ; iter.hasNext() ; ) {
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
