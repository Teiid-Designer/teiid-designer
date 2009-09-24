/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.outline;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IFileEditorInput;

import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;

/**
 * ModelOutlineContentProvider extends the ModelExplorerContentProvider to provide
 * additional content to the Outline view.
 */
public class ModelOutlineContentProvider extends ModelExplorerContentProvider {

    private Object root;
    
    public ModelOutlineContentProvider(Object rootNode) {
        this.root = rootNode;
        super.setShowModelContent(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if ( parentElement instanceof IFileEditorInput ) {
            return new Object[] { ((IFileEditorInput) parentElement).getFile() };
        }
        
        return super.getChildren(parentElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object element) {
        if ( element instanceof IResource ) {
            return root;
        }
        return super.getParent(element);
    }

}
