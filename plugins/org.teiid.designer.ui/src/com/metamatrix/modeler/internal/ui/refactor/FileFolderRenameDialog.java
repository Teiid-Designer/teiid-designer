/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.refactor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import com.metamatrix.modeler.core.refactor.ResourceRefactorCommand;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.MessageLabel;

/**
 * FileFolderRenameDialog is a dialog that displays the ModelObjectPropertySheet for an EObject
 */
public class FileFolderRenameDialog extends SelectionDialog {

    private static final String TITLE_PREFIX = UiConstants.Util.getString("FileFolderRenameDialog.titlePrefix.text"); //$NON-NLS-1$
    private static final String MESSAGE_LABEL = UiConstants.Util.getString("FileFolderRenameDialog.messageLabel.text") + ' '; //$NON-NLS-1$

    private static final String MODEL_FILE = UiConstants.Util.getString("FileFolderRenameDialog.modelFile.text") + ' '; //$NON-NLS-1$
    private static final String SCHEMA_FILE = UiConstants.Util.getString("FileFolderRenameDialog.schemaFile.text") + ' '; //$NON-NLS-1$
    private static final String VDB_FILE = UiConstants.Util.getString("FileFolderRenameDialog.vdbDefinitionFile.text") + ' '; //$NON-NLS-1$
    private static final String FOLDER = UiConstants.Util.getString("FileFolderRenameDialog.folder.text") + ' '; //$NON-NLS-1$

    private static final int WIDTH = 500;
    private static final int HEIGHT = 100;

    private IResource resource;
    // MyDefect : Added
    private ResourceRefactorCommand command;
    private Text txtOldNameNewName;
    private String sNewName;
    MessageLabel messageLabel;
    private String sOriginalName;

    /**
     * Construct an instance of PropertyDialog.
     * 
     * @param propertiedObject the EObject to display in this
     * @param title
     */
    public FileFolderRenameDialog( Shell parent,
                                   ResourceRefactorCommand command,
                                   IResource resource ) {
        super(parent);
        this.command = command;
        this.resource = resource;

        setTitle(TITLE_PREFIX + ' ' + getResourceType());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite container ) {

        Composite parent = (Composite)super.createDialogArea(container);
        GridLayout gridLayout = new GridLayout();
        parent.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = WIDTH;
        gd.heightHint = HEIGHT;
        parent.setLayoutData(gd);

        Composite pnlNewName = new Composite(parent, SWT.NONE);

        GridLayout gridLayout2 = new GridLayout();
        pnlNewName.setLayout(gridLayout2);
        gridLayout2.numColumns = 4;

        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        pnlNewName.setLayoutData(gd2);

        WidgetFactory.createLabel(pnlNewName, MESSAGE_LABEL);

        // 'Old Name New Name' textfield
        sOriginalName = getResourceName();
        txtOldNameNewName = WidgetFactory.createTextField(pnlNewName, GridData.FILL_HORIZONTAL, sOriginalName);
        GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
        gd3.horizontalSpan = 3;
        txtOldNameNewName.setLayoutData(gd3);

        messageLabel = new MessageLabel(parent);
        messageLabel.setAlignment(SWT.LEFT);
        messageLabel.setErrorStatus(null);
        messageLabel.setFont(parent.getFont());
        GridData messageLabelGridData = new GridData();
        messageLabelGridData.horizontalAlignment = GridData.FILL;
        messageLabelGridData.verticalAlignment = GridData.FILL;
        messageLabel.setLayoutData(messageLabelGridData);

        registerControls();

        return parent;
    }

    /*
     * Override createButton in order to disable the OK button on startup
     *  (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {

        Button btnNew = super.createButton(parent, id, label, defaultButton);
        Integer iId = (Integer)btnNew.getData();
        if (iId.intValue() == IDialogConstants.OK_ID) {
            btnNew.setEnabled(false);
        }
        return btnNew;
    }

    public String getNewName() {
        return sNewName;
    }

    /* (non-Javadoc)
     * Overridden to make the shell resizable.
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
    }

    private String getResourceType() {

        String sResourceType = PluginConstants.EMPTY_STRING;
        if (ModelUtil.isXsdFile(resource)) {
            sResourceType = SCHEMA_FILE;
        } else if (ModelUtilities.isModelFile(resource)) {
            sResourceType = MODEL_FILE;
        } else if (ModelUtil.isVdbArchiveFile(resource)) {
            sResourceType = VDB_FILE;
        } else if (resource.getType() == IResource.FOLDER) {
            sResourceType = FOLDER;
        }

        return sResourceType;
    }

    private String getResourceName() {
        return resource.getFullPath().removeFileExtension().lastSegment();
    }

    private void setErrorMessage( final IStatus status ) {
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                if (messageLabel != null && !messageLabel.isDisposed()) {
                    messageLabel.setErrorStatus(status);
                }
            }
        });
    }

    private void registerControls() {
        txtOldNameNewName.addFocusListener(new FocusListener() {
            public void focusGained( final FocusEvent event ) {
                // no action
            }

            public void focusLost( final FocusEvent event ) {
                handleFocusLost();
            }
        });

        txtOldNameNewName.addKeyListener(new KeyListener() {

            public void keyPressed( final KeyEvent event ) {
            }

            public void keyReleased( final KeyEvent event ) {
                handleKeyReleased();
            }
        });
    }

    void handleFocusLost() {
        // set the new string on the command
        sNewName = txtOldNameNewName.getText();
        command.setNewName(sNewName);

        IStatus status = command.canExecute();
        setErrorMessage(status);
        getButton(IDialogConstants.OK_ID).setEnabled(status.isOK());
    }

    void handleKeyReleased() {
        sNewName = txtOldNameNewName.getText();

        // set the new string on the command
        command.setNewName(sNewName);

        // test canExecute
        IStatus status = command.canExecute();
        setErrorMessage(status);
        getButton(IDialogConstants.OK_ID).setEnabled(status.isOK());
    }
}
