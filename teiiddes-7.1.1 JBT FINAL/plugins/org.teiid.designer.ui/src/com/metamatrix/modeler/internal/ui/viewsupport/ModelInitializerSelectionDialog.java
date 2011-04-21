/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * ModelInitializerSelectionDialog is used by the NewModelWizard to allow the user to select from a list of ModelInitializer names
 * provided by the MetamodelDescriptor.
 */
public class ModelInitializerSelectionDialog extends ListDialog implements IStructuredContentProvider {

    private static final String TITLE = UiConstants.Util.getString("ModelInitializerSelectionDialog.title"); //$NON-NLS-1$
    private static final String MESSAGE = UiConstants.Util.getString("ModelInitializerSelectionDialog.message"); //$NON-NLS-1$
    private static final String DESCRIPTION = UiConstants.Util.getString("ModelInitializerSelectionDialog.description"); //$NON-NLS-1$

    MetamodelDescriptor descriptor;
    Text description;
    private Object dialogInput;

    /**
     * Construct an instance of ModelInitializerSelectionDialog.
     * 
     * @param parentShell
     */
    public ModelInitializerSelectionDialog( Shell parentShell,
                                            MetamodelDescriptor descriptor ) {
        super(parentShell);
        this.descriptor = descriptor;
        super.setContentProvider(this);
        super.setLabelProvider(new LabelProvider());
        super.setInput(descriptor);
        dialogInput = descriptor;
        super.setAddCancelButton(false);
        super.setTitle(TITLE);
        super.setMessage(MESSAGE);
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement ) {
        return descriptor.getModelInitializerNames().toArray();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     */
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {

    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Control control = super.createDialogArea(parent);
        super.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                String name = (String)SelectionUtilities.getSelectedObject(event.getSelection());
                if (name != null) {
                    description.setText(descriptor.getModelInitializerDescription(name));
                } else {
                    description.setText(PluginConstants.EMPTY_STRING);
                }
            }
        });
        WidgetFactory.createLabel(parent, DESCRIPTION);
        description = WidgetFactory.createTextField(parent, GridData.FILL_HORIZONTAL);
        description.setEditable(false);
        setInitialDescription();
        return control;
    }

    public void setInitialSelection() {
        List initialSelectionList = new ArrayList(1);
        Object[] listObjects = getElements(dialogInput);
        initialSelectionList.add(listObjects[0]);
        this.setInitialElementSelections(initialSelectionList);
    }

    protected void setInitialDescription() {
        Object[] listObjects = getElements(dialogInput);
        String name = (String)listObjects[0];
        if (name != null) {
            description.setText(descriptor.getModelInitializerDescription(name));
        } else {
            description.setText(PluginConstants.EMPTY_STRING);
        }
    }
}
