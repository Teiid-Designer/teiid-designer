/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.teiid.designer.extension.ui.model.MedContentProvider;
import org.teiid.designer.extension.ui.model.MedLabelProvider;
import org.teiid.designer.extension.ui.model.MedModelNode;
import org.teiid.designer.extension.ui.model.MedModelNode.ModelType;

/**
 * MedOutlinePage is the ContentOutlinePage for the ModelEditor. It contains a PageBook which can display a TreeViewer of the Model,
 * plus any other controls that are contributed by ModelEditorPage extensions.
 */
public class MedOutlinePage extends ContentOutlinePage {

    private ModelExtensionDefinitionEditor medEditor;
    private MedSelectionProvider selectionProvider;

    public MedOutlinePage( ModelExtensionDefinitionEditor editor ) {
        super();
        this.medEditor = editor;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        super.createControl(parent);

        TreeViewer viewer = getTreeViewer();
        viewer.setAutoExpandLevel(3);
        viewer.setContentProvider(new MedContentProvider());
        viewer.setLabelProvider(new MedLabelProvider());
        ColumnViewerToolTipSupport.enableFor(viewer);

        // hook selection synchronizer
        MedSelectionSynchronizer selectionSynchronizer = getSelectionSynchronizer();
        this.selectionProvider = new OutlineSelectionProvider(viewer);
        selectionSynchronizer.addSelectionProvider(this.selectionProvider);

        // populate view
        viewer.setInput(this.medEditor);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.Page#dispose()
     */
    @Override
    public void dispose() {
        getSelectionSynchronizer().removeSelectionProvider(this.selectionProvider);
        super.dispose();
    }

    MedSelectionSynchronizer getSelectionSynchronizer() {
        return this.medEditor.getSelectionSynchronizer();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();
        this.selectionProvider.setSelection(getSelectionSynchronizer().getSelection());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setSelection( ISelection selection ) {
    }

    class OutlineSelectionProvider implements MedSelectionProvider {

        private final List<ISelectionChangedListener> listeners;
        private final Viewer viewer;

        public OutlineSelectionProvider( Viewer viewer ) {
            this.viewer = viewer;
            this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                 */
                @Override
                public void selectionChanged( SelectionChangedEvent event ) {
                    fireSelectionChanged(event);
                }
            });

            this.listeners = new ArrayList<ISelectionChangedListener>(1); // should only be synchronizer
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
         */
        @Override
        public void addSelectionChangedListener( ISelectionChangedListener listener ) {
            if (!this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }

        void fireSelectionChanged( SelectionChangedEvent viewerEvent ) {
            SelectionChangedEvent event = new SelectionChangedEvent(this, viewerEvent.getSelection());

            for (ISelectionChangedListener listener : this.listeners) {
                listener.selectionChanged(event);
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.ui.editors.MedSelectionProvider#getMedEditorPage()
         */
        @Override
        public MedEditorPage getMedEditorPage() {
            return null; // not connected to a editor page
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.ui.editors.MedSelectionProvider#getSelectedNode(org.teiid.designer.extension.ui.model.MedModelNode.ModelType)
         */
        @Override
        public MedModelNode getSelectedNode( ModelType type ) {
            IStructuredSelection selection = (IStructuredSelection)getSelection();

            if (!selection.isEmpty()) {
                MedModelNode modelNode = (MedModelNode)selection.getFirstElement();

                if (modelNode.getType() == type) {
                    return modelNode;
                }

                // walk up ancestry to find
                modelNode = modelNode.getParent();

                while (modelNode != null) {
                    if (modelNode.getType() == type) {
                        return modelNode;
                    }

                    modelNode = modelNode.getParent();
                }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
         */
        @Override
        public ISelection getSelection() {
            return this.viewer.getSelection();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.ui.editors.MedSelectionProvider#isApplicable(org.eclipse.jface.viewers.IStructuredSelection)
         */
        @Override
        public boolean isApplicable( IStructuredSelection selection ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.ui.editors.MedSelectionProvider#refresh()
         */
        @Override
        public void refresh() {
            this.viewer.refresh();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
         */
        @Override
        public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
            this.listeners.remove(listener);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
         */
        @Override
        public void setSelection( ISelection selection ) {
            if (!selection.equals(this.viewer.getSelection())) {
                this.viewer.setSelection(selection);
            }
        }

    }

}
