/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectContentProvider;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ModelWorkspaceTreeProvider is a basic ITreeContentProvider and ILabelProvider ModelWorkspace.
 * It does not allow navigation into a model and works only with IProject and IResource objects.
 * This class does not perform any filtering of the workspace; use a ViewFilter if you wish to do that.
 */
public class ModelWorkspaceTreeProvider extends ModelExplorerLabelProvider implements ITreeContentProvider {

    private static final Object[] NO_CHILDREN = new Object[0];

    private ITreeContentProvider modelContentProvider = ModelObjectContentProvider.getInstance();

    /**
     * Construct an instance of ModelWorkspaceTreeProvider.
     */
    public ModelWorkspaceTreeProvider( ) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        if ( parentElement instanceof IFolder ) {
            try { 
                return ((IFolder) parentElement).members();
            } catch (CoreException e) {
                UiConstants.Util.log(e);
                return NO_CHILDREN;
            }
        }
        if ( parentElement instanceof IFile ) {
            return NO_CHILDREN;
        } else if ( parentElement instanceof IProject ) {
            try { 
                return ((IProject) parentElement).members();
            } catch (CoreException e) {
                UiConstants.Util.log(e);
                return NO_CHILDREN;
            }
        } else {
            return modelContentProvider.getChildren(parentElement);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        return modelContentProvider.getParent(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        if ( element instanceof IFile ) {
            return false;
        }
        return true;//modelContentProvider.hasChildren(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        Object[] result = ((IWorkspaceRoot) inputElement).getProjects();
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        modelContentProvider.inputChanged(viewer, oldInput, newInput);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
    public void addListener(ILabelProviderListener listener) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
    public void removeListener(ILabelProviderListener listener) {

    }

}
