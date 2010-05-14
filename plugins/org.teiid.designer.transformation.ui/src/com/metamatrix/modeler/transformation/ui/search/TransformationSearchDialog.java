/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.search;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * Dialog which provides a transfomration SQL search capability. Users can: 1) enter a search string 2) perform search 3) view
 * list of transformation targets that contain SQL which contains matches 4) Select target object or SELECT, INSERT, UPDATE or
 * DELETE tree node and view SQL 5) Select EDIT button to open the selected transformation in a ModelEditor
 * 
 * @since 5.0
 */
public class TransformationSearchDialog extends Dialog {

    TransformationSearchPanel panel;
    private Button editButton;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;
    private static final int EDIT_ID = 991;
    private static final String EDIT_LABEL = UiConstants.Util.getString("TransformationSearchDialog.editText"); //$NON-NLS-1$ 
    private static final String CLOSE_LABEL = UiConstants.Util.getString("TransformationSearchDialog.closeText"); //$NON-NLS-1$ 

    /**
     * TransformationSearchDialog constructor.
     * 
     * @param parent parent of this dialog
     * @param title dialog display title
     */
    public TransformationSearchDialog( Shell parent,
                                       String title ) {
        super(parent, title);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite mainComposite = (Composite)super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        mainComposite.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = WIDTH;
        gd.heightHint = HEIGHT;
        mainComposite.setLayoutData(gd);

        panel = new TransformationSearchPanel(mainComposite, this);

        return mainComposite;
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     * @since 5.0
     */
    @Override
    public void create() {
        super.create();
        setOkEnabled(true);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 5.0
     */
    @Override
    protected void okPressed() {
        super.okPressed();
    }

    /**
     * Allows setting OK button (i.e. Close) enablement state
     * 
     * @param enabled
     * @since 5.0
     */
    public void setOkEnabled( boolean enabled ) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }

    public void openAndEdit( EObject eObj ) {
        ModelEditorManager.edit(eObj, PluginConstants.TRANSFORMATION_EDITOR_ID);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        // Create an Edit button and wire the selection to "Open and Edit" given the EObject selection from the panel
        editButton = createButton(parent, EDIT_ID, EDIT_LABEL, false);

        editButton.setEnabled(false);

        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                EObject eObj = panel.getLastSelectedTarget(); // SelectionUtilities.getSelectedEObject(selection);
                if (eObj != null) {
                    openAndEdit(eObj);
                }
            }
        });

        // Create the OK button, but use a "Close" label instead.
        createButton(parent, IDialogConstants.OK_ID, CLOSE_LABEL, false);
    }

    /**
     * Allows setting EDIT button enablement state
     * 
     * @param value
     * @since 5.0
     */
    public void setEditEnabled( boolean value ) {
        editButton.setEnabled(value);
    }
}
