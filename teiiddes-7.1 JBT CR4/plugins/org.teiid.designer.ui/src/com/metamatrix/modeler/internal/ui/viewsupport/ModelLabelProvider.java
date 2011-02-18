/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
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
                modelResource = ModelUtil.getModelResource((IFile)element, false);
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
