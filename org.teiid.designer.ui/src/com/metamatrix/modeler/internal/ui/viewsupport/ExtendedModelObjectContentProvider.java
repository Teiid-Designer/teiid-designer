/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ITreeContentProvider;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.ui.UiConstants;


/** 
 * Content provider class for contributed model objects.
 * @since 5.0
 */
public class ExtendedModelObjectContentProvider implements UiConstants.ExtensionPoints.ExtendedModelContentProvider {
    
    /** list of ITreeContentProviders from the extendedModelProvider extension point */
    private static final ArrayList extendedModelProviders = new ArrayList();
    
    private static boolean loaded = false;
    
    /** 
     * 
     * @since 5.0
     */
    public ExtendedModelObjectContentProvider() {
        super();
        loadProviderList();
    }

    
    private void loadProviderList() {
        // if already loaded, return. We don't want to load them for all instances of this class.
        if( loaded ) {
            return;
        }
        
        loaded = true;
        //  -------------------------------------------------------------------------------------------------------
        // build a list of all ExtendedModelContentProvider contributions of type IContentProvider
        // -------------------------------------------------------------------------------------------------------
        
        // get the ExtendedModelContentProvider extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, EXTENDED_MODEL_ID);
        // get the all extensions to the DiagramProvider extension point
        IExtension[] extensions = extensionPoint.getExtensions();
    
        // walk through the extensions and find all ITreeContentProviders
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            try {
    
                // find the content provider instance and add it to the instance list
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(EXTENDED_MODEL_CLASS)) {
                        Object provider = elements[j].createExecutableExtension(EXTENDED_MODEL_CLASSNAME);
                        if ( provider instanceof ITreeContentProvider ) {
                            extendedModelProviders.add(provider);
                            break;
                        }
                    }
                }
                
            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("ModelObjectContentProvider.configurationErrorMessage", //$NON-NLS-1$
                            extensions[i].getUniqueIdentifier()); 
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }
        // -------------------------------------------------------------------------------------------------------
        
    }
    
    
    /**
     * Walks through all ExtendedModelContentProvider contributions and asks them for children
     * of the parentElement. Any children they return are integrated into the child array
     * returned from the getChildren method.
     * @param parentElement
     * @return
     */
    private ArrayList getExtendedModelChildren(Object parentElement) {  
        ArrayList result = new ArrayList();
        
        if(isXsdObject(parentElement) ){
            return result;
        }
        
        final boolean startedTxn = ModelerCore.startTxn(false, true, null, this);          
        boolean succeeded = false;
        try{
            for ( Iterator iter = extendedModelProviders.iterator() ; iter.hasNext() ; ) {
                ITreeContentProvider provider = (ITreeContentProvider) iter.next();
                try {
                    Object[] extendedObjs = provider.getChildren(parentElement);
                    if ( extendedObjs != null && extendedObjs.length > 0 ) {
                        result.addAll(Arrays.asList(extendedObjs));
                    }
                } catch (Exception e) {
                    // catch any Exception that occurred in the provider and log it
                    String message = UiConstants.Util.getString("ModelObjectContentProvider.extendedModelProviderErrorMessage"); //$NON-NLS-1$
                    UiConstants.Util.log(IStatus.ERROR, e, message);
                }
            }
            // not really sure if I should roll back if any exceptions get caught.
            succeeded = true;
        } finally {
            if( startedTxn ){
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return result;
    }
    
    private boolean isXsdObject(final Object obj){
        if(obj == null){
            return false;
        }
        
        if(obj instanceof ModelResource){
            return ((ModelResource)obj).isXsd();
        }
        
        if(obj instanceof EObject){
            final Resource rsrc = ((EObject)obj).eResource();
            return ModelUtil.isXsdFile(rsrc);
        }
        
        if(obj instanceof Resource){
            return ModelUtil.isXsdFile( (Resource)obj );
        }
            
        return false;
    }


    /**
     *  
     * @param theParentElement
     * @return
     * @since 5.0
     */
    public Object[] getChildren(Object theParentElement) {
        
        ArrayList extendedChildren = getExtendedModelChildren(theParentElement);
        if( !extendedChildren.isEmpty() ) {
            return extendedChildren.toArray();
        }
        
        return null;
    }

    /**
     *  
     * @param theElement
     * @return
     * @since 5.0
     */
    public Object getParent(Object theElement) {
        Object result = null;

        for ( Iterator iter = extendedModelProviders.iterator() ; iter.hasNext() ; ) {
            ITreeContentProvider provider = (ITreeContentProvider) iter.next();
            try {
                result = provider.getParent(theElement);
            } catch (Exception e) {
                // catch any Exception that occurred in the diagram provider and log it
                String message = UiConstants.Util.getString("ModelObjectContentProvider.extendedModelProviderErrorMessage"); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
            if( result != null ) {
                break;
            }
        }
        return null;
    }

    /**
     *  
     * @param theElement
     * @return
     * @since 5.0
     */
    public boolean hasChildren(Object theElement) {               
        return hasExtendedModelChildren(theElement);
    }
    
    /*
     * 
     */
    private boolean hasExtendedModelChildren(Object parentElement) {
        if(isXsdObject(parentElement) ){
            return false;
        }
        
        for ( Iterator iter = extendedModelProviders.iterator() ; iter.hasNext() ; ) {
            ITreeContentProvider provider = (ITreeContentProvider) iter.next();
            if( provider.hasChildren(parentElement) )
                return true;
        }
        
        return false;
    }
}
