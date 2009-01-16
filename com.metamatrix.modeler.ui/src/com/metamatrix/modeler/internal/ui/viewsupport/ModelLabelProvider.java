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

package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * ModelLabelProvider
 */
public class ModelLabelProvider extends LabelProvider implements PluginConstants.Images {

    private ILabelProvider workbenchProvider = new WorkbenchLabelProvider();
    private boolean fullpath = false;


    public ModelLabelProvider() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
        if ( element instanceof IFile && ModelUtilities.isModelFile((IFile) element) ) {
            ModelResource modelResource;
            try {
                modelResource = ModelUtilities.getModelResource((IFile)element, false);
                if ( modelResource == null ) {
                    return workbenchProvider.getImage(element);
                }
                
                Image image = ModelIdentifier.getModelImage(modelResource);
                if( image != null ) {
                    return image;
                }
                
                return UiPlugin.getDefault().getImage(MODEL);
            } catch (ModelWorkspaceException e) {
                e.printStackTrace();
            }
        }
        return workbenchProvider.getImage(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        if ( fullpath ) {
            if ( element instanceof IResource ) {
                return ((IResource) element).getFullPath().makeRelative().toString();
            }
        } else {
            if ( element instanceof IResource ) {
                return ((IResource) element).getName();
            }
        }
        
        return super.getText(element);
    }

    /**
     * sets the text to display the full path of the file; default is just the name.
     */
    public void setFullpath(boolean showPath) {
        fullpath = showPath;
    }

}
