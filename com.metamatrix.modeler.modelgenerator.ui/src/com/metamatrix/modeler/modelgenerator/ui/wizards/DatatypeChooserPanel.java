/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.modelgenerator.ui.ModelerModelGeneratorUiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * DatatypeChooserPanel
 */
public class DatatypeChooserPanel extends Composite implements ModelGeneratorUiConstants {

    private static final String NO_SELECTION = Util.getString("DatatypeChooserPanel.noSelection.text"); //$NON-NLS-1$

    private EObject currentDatatype;
    private CLabel datatypeLabel;
    private Button showDatatypeDialogButton;
    private String dialogTitle;
    private String dialogLabelText;

    /** List of listeners registered for this panels events */
    private List eventListeners;

    /**
     * Constructor
     * 
     * @param parent the parent composite
     * @param dialogTitle title for the chooser dialog
     * @param dialogLabelText text for the datatype label
     */
    public DatatypeChooserPanel( Composite parent,
                                 String dialogTitle,
                                 String dialogLabelText ) {
        super(parent, SWT.NULL);
        this.dialogTitle = dialogTitle;
        this.dialogLabelText = dialogLabelText;
        initialize();
    }

    /**
     * Set the Selected Datatype
     * 
     * @param type the selected datatype
     */
    public void setSelectedDatatype( EObject type ) {
        this.currentDatatype = type;
        this.datatypeLabel.setText(getDatatypeText(type));
        this.datatypeLabel.setImage(getDatatypeImage(type));
        this.layout();
        notifyEventListeners();
    }

    /**
     * Get the Selected Datatype
     * 
     * @return the selected datatype
     */
    public EObject getSelectedDatatype() {
        return currentDatatype;
    }

    // -------------------------------------------------------------------------
    // Methods to Register, UnRegister, Notify Listeners to this Panels Events
    // -------------------------------------------------------------------------
    /**
     * This method will register the listener for all CheckboxSelectionEvents
     * 
     * @param listener the listener to be registered
     */
    public void addEventListener( EventObjectListener listener ) {
        if (eventListeners == null) {
            eventListeners = new ArrayList();
        }
        eventListeners.add(listener);
    }

    /**
     * This method will un-register the listener for all CheckboxSelectionEvents
     * 
     * @param listener the listener to be un-registered
     */
    public void removeEventListener( EventObjectListener listener ) {
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * This method will notify the registered listeners of a CheckboxSelectionEvents
     */
    private void notifyEventListeners() {
        if (eventListeners != null) {
            Iterator iterator = eventListeners.iterator();
            while (iterator.hasNext()) {
                EventObjectListener listener = (EventObjectListener)iterator.next();
                if (listener != null) {
                    listener.processEvent(new EventObject(this));
                }
            }
        }
    }

    /**
     * Initialization
     */
    private void initialize() {
        // Set the layout
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        this.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        this.setLayoutData(gridData);

        // Datatype Label
        String text = getDatatypeText(currentDatatype);
        Image image = getDatatypeImage(currentDatatype);
        this.datatypeLabel = WidgetFactory.createLabel(this, text, image, GridData.HORIZONTAL_ALIGN_BEGINNING
                                                                          | GridData.GRAB_HORIZONTAL);

        // Create the showDatatypeDialog Button
        this.showDatatypeDialogButton = WidgetFactory.createButton(this, "...", GridData.HORIZONTAL_ALIGN_END); //$NON-NLS-1$
        this.showDatatypeDialogButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                showDatatypeDialogPressed();
            }
        });
    }

    /**
     * get text for the provided object
     */
    private String getDatatypeText( Object object ) {
        String result = NO_SELECTION;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getText(object);
            }
        }
        return result;
    }

    /**
     * get image for the provided object
     */
    private Image getDatatypeImage( Object object ) {
        Image result = null;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getImage(object);
            }
        }
        return result;
    }

    /**
     * handler for Datatype chooser dialog button.
     */
    void showDatatypeDialogPressed() {
        Shell shell = ModelerModelGeneratorUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        ListDialog dialog = new ListDialog(shell);
        dialog.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements( Object inputElement ) {
                Object[] result = new Object[0];
                try {
                    result = ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
                } catch (ModelerCoreException e) {
                    Util.log(e);
                }
                return result;
            }

            public void dispose() {
            }

            public void inputChanged( Viewer v,
                                      Object o,
                                      Object o2 ) {
            }
        });
        dialog.setLabelProvider(ModelUtilities.getEMFLabelProvider());
        dialog.setAddCancelButton(true);
        dialog.setTitle(this.dialogTitle);
        dialog.setMessage(this.dialogLabelText);
        dialog.setInput(Collections.EMPTY_LIST);

        Object originalValue = this.currentDatatype;
        Object[] selection = new Object[] {originalValue};
        selection[0] = originalValue;
        dialog.setInitialSelections(selection);

        int status = dialog.open();
        EObject newDatatype = (EObject)originalValue;
        if (status == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 0) {
                // null out the value
                newDatatype = null;
            } else {
                // return the selected value
                newDatatype = (EObject)result[0];
            }
        }
        // If different datatype was chosen, set it on the binding
        if (!newDatatype.equals(originalValue)) {
            setSelectedDatatype(newDatatype);
        }
    }
}
