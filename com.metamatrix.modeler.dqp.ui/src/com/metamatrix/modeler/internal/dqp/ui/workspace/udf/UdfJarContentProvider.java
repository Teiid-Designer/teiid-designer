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

package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;


/** 
 * @since 5.0
 */
public class UdfJarContentProvider implements ITreeContentProvider {

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];
    
    //private static File extensionsDirectory; 
    /** 
     * 
     * @since 5.0
     */
    public UdfJarContentProvider() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public synchronized Object[] getChildren(Object parentElement) {

        Object[] children = NO_CHILDREN;
        
        if (parentElement instanceof ModelResource) {
            ModelResource mResource = (ModelResource) parentElement;
            if ( ModelIdentifier.isFunctionModel(mResource) ) {
                Collection<UdfJarFolder> wrappedFolders = new ArrayList<UdfJarFolder>();

                wrappedFolders.add(new UdfJarFolder(mResource));
                
                children = wrappedFolders.toArray();
            }
        } else if( parentElement instanceof UdfJarFolder ) {
            // Show JAR's
            Collection<UdfJarWrapper> wrappedJars = new ArrayList<UdfJarWrapper>();
            
            // Get the jar files from the extension handler
            List<File> udfJarFiles = DqpPlugin.getInstance().getExtensionsHandler().getUdfJarFiles();
            
            for( File jarFile: udfJarFiles ) {
                wrappedJars .add( new UdfJarWrapper(jarFile));
            }
            
            children = wrappedJars.toArray();
        }
        
        return children;
    }
    
//    private boolean isJarFile(File theFile) {
//        if( theFile.getName().toLowerCase().endsWith(".jar") ) {
//            return true;
//        }
//        
//        return false;
//    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        Object result = null;
        if ( element instanceof UdfJarFolder ) {
            // Find the JDBC Source object in ModelResource
            result = ((UdfJarFolder)element).getUdfResource();

        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        Object[] children= getChildren(element);
        return (children != null) && children.length > 0;
    }

}
