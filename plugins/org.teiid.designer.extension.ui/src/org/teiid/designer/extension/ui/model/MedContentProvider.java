/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.model;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IFileEditorInput;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;

import com.metamatrix.core.util.ArrayUtil;

/**
 * 
 */
public class MedContentProvider implements ITreeContentProvider {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren( Object parentElement ) {
        if (parentElement instanceof MedModelNode) {
            return ((MedModelNode)parentElement).getChildren();
        }

        if (parentElement instanceof ModelExtensionDefinition) {
            return new Object[] { MedModelNode.createMedNode((ModelExtensionDefinition)parentElement) };
        }

        if (parentElement instanceof IFile) {
            InputStream stream = null;

            try {
                stream = ((IFile)parentElement).getContents();
                ModelExtensionDefinition med = ExtensionPlugin.getInstance().parse(stream);
                return getChildren(med);
            } catch (Exception e) {
                UTIL.log(e);
                return ArrayUtil.Constants.EMPTY_ARRAY;
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        UTIL.log(e);
                    }
                }
            }
        }

        if (parentElement instanceof IFileEditorInput) {
            return getChildren(((IFileEditorInput)parentElement).getFile());
        }

        return ArrayUtil.Constants.EMPTY_ARRAY;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements( Object inputElement ) {
        return getChildren(inputElement);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent( Object element ) {
        if (element instanceof MedModelNode) {
            return ((MedModelNode)element).getParent();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren( Object element ) {
        return (getChildren(element).length != 0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
        // nothing to do
    }

}
