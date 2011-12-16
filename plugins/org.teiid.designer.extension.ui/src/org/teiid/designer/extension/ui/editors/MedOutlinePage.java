/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinition.PropertyName;
import org.teiid.designer.extension.ui.model.MedContentProvider;
import org.teiid.designer.extension.ui.model.MedLabelProvider;

/**
 * MedOutlinePage is the ContentOutlinePage for the ModelEditor. It contains a PageBook which can display a TreeViewer of the
 * Model, plus any other controls that are contributed by ModelEditorPage extensions.
 */
public class MedOutlinePage extends ContentOutlinePage implements PropertyChangeListener, ISelectionListener {

    private ModelExtensionDefinition med;
    private ModelExtensionDefinitionEditor medEditor;

    public MedOutlinePage( ModelExtensionDefinitionEditor editor ) {
        super();
        this.medEditor = editor;
        this.med = this.medEditor.getMed();
        this.med.addListener(this);
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

        // hook up a listeners
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        viewer.addSelectionChangedListener(this);

        // populate view 
        viewer.setInput(this.med);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.Page#dispose()
     */
    @Override
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);

        if (this.med != null) {
            this.med.removeListener(this);
        }

        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public final void propertyChange( PropertyChangeEvent e ) {
        TreeViewer viewer = getTreeViewer();
        String propName = e.getPropertyName();

        if (PropertyName.PROPERTY_DEFINITION.toString().equals(propName)) {
            viewer.refresh();
        } else if (PropertyName.METACLASS.toString().equals(propName)) {
            viewer.refresh();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        // TODO: Implement selection changes, based on ModelEditor selections.
    }

}
