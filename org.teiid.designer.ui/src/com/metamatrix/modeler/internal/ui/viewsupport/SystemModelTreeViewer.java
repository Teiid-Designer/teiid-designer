/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * A viewer for displaying the System models in ModelerCore's external container.
 * 
 * @since 4.3
 */
public class SystemModelTreeViewer extends TreeViewer {

    /**
     * @param parent
     * @since 4.3
     */
    public SystemModelTreeViewer( Composite parent ) {
        this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    }

    /**
     * @param parent
     * @param style
     * @since 4.3
     */
    public SystemModelTreeViewer( Composite parent,
                                  int style ) {
        super(parent, style);
        setContentProvider(new SystemModelContentProvider());
        setLabelProvider(new SystemModelLabelProvider());
        addFilter(new SystemModelViewerFilter());
        Resource[] models = ModelerCore.getSystemVdbResources();

        // only display virtual models
        ArrayList virtualModels = new ArrayList(models.length);
        for (int i = 0; i < models.length; ++i) {
            if (models[i] instanceof MtkXmiResourceImpl) {
                // need to check if the model is virtual
                ModelAnnotation annotation = ((MtkXmiResourceImpl)models[i]).getModelAnnotation();
                if (annotation.getModelType() == (ModelType.VIRTUAL_LITERAL)) {
                    virtualModels.add(models[i]);
                }
            }
        }

        Resource[] result = new Resource[virtualModels.size()];
        virtualModels.toArray(result);
        super.setInput(result);
    }

    class SystemModelContentProvider implements ITreeContentProvider {

        private ITreeContentProvider modelObjectProvider;

        public SystemModelContentProvider() {
            modelObjectProvider = ModelUtilities.getModelContentProvider();
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren( Object parentElement ) {
            return modelObjectProvider.getChildren(parentElement);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            return (Object[])inputElement;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent( Object element ) {
            return modelObjectProvider.getParent(element);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren( Object element ) {
            return modelObjectProvider.hasChildren(element);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {

        }

    }

    class SystemModelLabelProvider implements ILabelProvider {

        ILabelProvider delegate = ModelUtilities.getModelObjectLabelProvider();

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         * @since 4.3
         */
        public void addListener( ILabelProviderListener listener ) {
            delegate.addListener(listener);
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         * @since 4.3
         */
        public void dispose() {
            delegate.dispose();
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         * @since 4.3
         */
        public boolean isLabelProperty( Object element,
                                        String property ) {
            return delegate.isLabelProperty(element, property);
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         * @since 4.3
         */
        public void removeListener( ILabelProviderListener listener ) {
            delegate.removeListener(listener);
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         * @since 4.3
         */
        public Image getImage( Object element ) {
            if (element instanceof MtkXmiResourceImpl) {
                // need to check if the model is virtual
                ModelAnnotation annotation = ((MtkXmiResourceImpl)element).getModelAnnotation();
                if (annotation.getModelType() == (ModelType.VIRTUAL_LITERAL)) {
                    return UiPlugin.getDefault().getImage(PluginConstants.Images.VIRTUAL_MODEL);
                }
            }
            return delegate.getImage(element);
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         * @since 4.3
         */
        public String getText( Object element ) {
            if (element instanceof Resource) {
                String filename = ((Resource)element).getURI().lastSegment();
                return filename.substring(0, filename.indexOf('.'));
            }
            return delegate.getText(element);
        }

    }

    class SystemModelViewerFilter extends ViewerFilter {

        @Override
        public boolean select( Viewer viewer,
                               Object parentElement,
                               Object element ) {
            if (element instanceof ModelAnnotation) {
                return false;
            }
            if (element instanceof TransformationContainer) {
                return false;
            }
            if (element instanceof DiagramContainer) {
                return false;
            }
            if (element instanceof AnnotationContainer) {
                return false;
            }
            if (element instanceof MtkXmiResourceImpl) {
                if (((MtkXmiResourceImpl)element).getModelAnnotation().getModelType() == ModelType.VIRTUAL_LITERAL) {
                    return true;
                }
                return false;
            }
            return true;
        }

    }

}
