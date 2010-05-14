/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * AliasEntryPanel
 */
public class ColumnEntryPanel extends Composite implements ModifyListener {

    private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;

    private static final String START_MESSAGE = UiConstants.Util.getString("ColumnEntryPanel.startMessage"); //$NON-NLS-1$
    private static final String COLUMN_LABEL_TEXT = UiConstants.Util.getString("ColumnEntryPanel.columnLabel.text"); //$NON-NLS-1$
    private static final String DATATYPE_LABEL_TEXT = UiConstants.Util.getString("ColumnEntryPanel.datatypeLabel.text"); //$NON-NLS-1$
    private static final String SET_DATATYPE_BUTTON_TEXT = UiConstants.Util.getString("ColumnEntryPanel.setDatatypeButton.text"); //$NON-NLS-1$
    private static final String NO_COLUMN_NAME_ENTERED = UiConstants.Util.getString("ColumnEntryPanel.noColumnNameEntered"); //$NON-NLS-1$
    private static final String NO_DATATYPE_ENTERED = UiConstants.Util.getString("ColumnEntryPanel.noDatatypeSelected"); //$NON-NLS-1$
    private static final String INVALID_COLUMN_NAME_ = UiConstants.Util.getString("ColumnEntryPanel.invalidColumnName"); //$NON-NLS-1$

    private Text columnText;
    private Text datatypeText;
    private CLabel messageLabel;
    private EObject datatype;
    private int length = 0;
    private ColumnEntryDialog dlg;
    private Button datatypeButton;
    private StringNameValidator nameValidator;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public ColumnEntryPanel( ColumnEntryDialog dlg,
                             Composite parent ) {
        super(parent, SWT.NONE);
        this.dlg = dlg;
        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {
        // ------------------------------
        // Set layout for the Composite
        // ------------------------------
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 5;
        this.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        this.setLayoutData(gridData);

        Label columnLabel = new Label(this, SWT.NONE);
        columnLabel.setText(COLUMN_LABEL_TEXT + " "); //$NON-NLS-1$
        GridData columnLabelGridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        columnLabelGridData.horizontalSpan = 1;
        columnLabel.setLayoutData(columnLabelGridData);

        columnText = WidgetFactory.createTextField(this);
        columnText.setTextLimit(50);
        columnText.addModifyListener(this);
        GridData columnTextGridData = new GridData(GridData.FILL_HORIZONTAL);
        columnTextGridData.horizontalSpan = 4;
        columnTextGridData.widthHint = 100;
        columnText.setLayoutData(columnTextGridData);

        Label datatypeLabel = new Label(this, SWT.NONE);
        datatypeLabel.setText(DATATYPE_LABEL_TEXT);
        GridData datatypeLabelGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END
                                                      | GridData.VERTICAL_ALIGN_CENTER);
        datatypeLabelGridData.horizontalSpan = 1;
        datatypeLabel.setLayoutData(datatypeLabelGridData);

        datatypeText = WidgetFactory.createTextField(this);
        // datatypeText.setTextLimit(50);
        datatypeText.addModifyListener(this);
        datatypeText.setEditable(false);
        GridData datatypeTextGridData = new GridData(GridData.FILL_HORIZONTAL);
        datatypeTextGridData.horizontalSpan = 3;
        datatypeTextGridData.widthHint = 100;
        datatypeText.setLayoutData(datatypeTextGridData);

        datatypeButton = WidgetFactory.createButton(this, SET_DATATYPE_BUTTON_TEXT, BUTTON_GRID_STYLE);
        datatypeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                datatypeButtonPressed();
            }
        });

        this.messageLabel = WidgetFactory.createLabel(this);
        this.messageLabel.setText(START_MESSAGE);
        GridData messageData = new GridData(GridData.FILL_BOTH);
        messageData.grabExcessHorizontalSpace = true;
        messageData.grabExcessVerticalSpace = true;
        messageData.horizontalSpan = 3;
        this.messageLabel.setLayoutData(messageData);
    }

    void datatypeButtonPressed() {
        // configure dialog
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(shell, null);
        boolean canSetLength = false;
        dialog.setEditLength(canSetLength);
        // show dialog
        int status = dialog.open();

        // process dialog
        if (status == Window.OK) {
            Object[] result = dialog.getResult();

            // return the selected value
            if (result.length > 0) {
                if (canSetLength) {
                    length = dialog.getLength();
                }

                datatype = (EObject)result[0];
                datatypeText.setText(ModelerCore.getModelEditor().getName(datatype));
            }
        }

        validate();
    }

    /**
     * @return columnName String
     * @since 4.2
     */
    public String getColumnName() {
        return columnText.getText();
    }

    /*
     * Private method to validate column name and datatype
     */
    private void validate() {
        String text = this.columnText.getText();
        if ((text == null) || (text.trim().equals(""))) { //$NON-NLS-1$
            setError(NO_COLUMN_NAME_ENTERED);
            dlg.setOkEnabled(false);
        } else if (!isValidColumn(text)) {
            setError(INVALID_COLUMN_NAME_);
            dlg.setOkEnabled(false);
        } else if (datatype == null) {
            setError(NO_DATATYPE_ENTERED);
            dlg.setOkEnabled(false);
        } else {
            setError(null);
            dlg.setOkEnabled(true);
        }
    }

    /**
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    public void modifyText( ModifyEvent e ) {
        // call validation to set any error message and button states.
        validate();
    }

    /**
     * @param text
     * @return
     */
    private boolean isValidColumn( String text ) {
        if (nameValidator == null) nameValidator = new StringNameValidator();

        return nameValidator.isValidName(text);
    }

    private void setError( String message ) {
        if (messageLabel != null) {
            if (message == null) {
                this.messageLabel.setImage(null);
            } else {
                Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
                this.messageLabel.setImage(image);
            }
            this.messageLabel.setText(message);
            this.messageLabel.redraw();
        }
    }

    public EObject getDatatype() {
        return this.datatype;
    }

    public int getLength() {
        return this.length;
    }
}
