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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinition.PropertyName;

/**
 * MedOutlinePage is the ContentOutlinePage for the ModelEditor. It contains a PageBook which can display a TreeViewer of the
 * Model, plus any other controls that are contributed by ModelEditorPage extensions.
 */
public class MedOutlinePage extends ContentOutlinePage implements PropertyChangeListener, ISelectionListener {

    ModelExtensionDefinitionEditor medEditor;
    protected TreeViewer contentOutlineViewer;

    public MedOutlinePage( ModelExtensionDefinitionEditor editor ) {
        super();
        this.medEditor = editor;
    }

    @Override
    public void createControl( Composite parent ) {
        super.createControl(parent);
        contentOutlineViewer = getTreeViewer();
        contentOutlineViewer.addSelectionChangedListener(this);
        contentOutlineViewer.setAutoExpandLevel(3);
        // hook up a selection listener to the selection service
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

        // Set the content and label provider
        MedOutlineTreeContentProvider contentProvider = new MedOutlineTreeContentProvider(this.medEditor);
        contentOutlineViewer.setContentProvider(contentProvider);
        contentOutlineViewer.setLabelProvider(contentProvider);
        List<ModelExtensionDefinition> medList = new ArrayList<ModelExtensionDefinition>(1);
        ModelExtensionDefinition med = this.medEditor.getMed();
        med.addListener(this);
        medList.add(med);
        contentOutlineViewer.setInput(medList);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public final void propertyChange( PropertyChangeEvent e ) {
        String propName = e.getPropertyName();

        if (PropertyName.PROPERTY_DEFINITION.toString().equals(propName)) {
            contentOutlineViewer.refresh();
        } else if (PropertyName.METACLASS.toString().equals(propName)) {
            contentOutlineViewer.refresh();
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
