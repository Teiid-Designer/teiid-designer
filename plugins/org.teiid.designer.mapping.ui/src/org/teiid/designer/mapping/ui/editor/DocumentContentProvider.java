/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * DocumentContentProvider
 */
public class DocumentContentProvider implements ITreeContentProvider {

    private ITreeContentProvider emfProvider;
    private MappingDiagramController diagramController;

    public DocumentContentProvider(MappingDiagramController diagramController) {
        this.emfProvider = ModelUtilities.getModelContentProvider();
        this.diagramController = diagramController;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
	public void dispose() {
        if ( emfProvider != null )
            emfProvider.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
	public Object[] getChildren(Object parentElement) {
        if ( parentElement instanceof EObject ) {
            return diagramController.getMappableTree().getChildren((EObject) parentElement).toArray();
        }
        return new Object[0];
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
	public Object[] getElements(Object inputElement) {
        if ( inputElement instanceof Diagram ) {
            EObject root = ((Diagram) inputElement).getTarget();
            return root.eContents().toArray();
        } else if ( inputElement instanceof EObject ) {
            return ((EObject) inputElement).eContents().toArray();
        }

        return new Object[0];
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
	public Object getParent(Object element) {
        return emfProvider.getParent(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
	public boolean hasChildren(Object element) {
        return emfProvider.hasChildren(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        emfProvider.inputChanged(viewer, oldInput, newInput);        
    }

}
