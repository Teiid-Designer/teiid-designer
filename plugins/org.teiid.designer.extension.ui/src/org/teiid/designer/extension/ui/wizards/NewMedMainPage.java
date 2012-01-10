/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.extension.ui.Messages;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * NewMedMainPage - first page of NewMedWizard, for selection of location and name of the MED
 */
public class NewMedMainPage extends AbstractWizardPage implements InternalUiConstants.Widgets {
    private static final int COLUMN_COUNT = 3;

    private static final StringNameValidator nameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                                     StringNameValidator.DEFAULT_MAXIMUM_LENGTH);

    private IContainer folderLocation; // location to create the MED
    private String medName; // The proposed MED Name

    private ISelectionStatusValidator projectValidator = new ModelProjectSelectionStatusValidator();

    private Text nameText, folderText;
    private Button btnFolderBrowse;
    private Button openInEditorCB;

    public NewMedMainPage( IContainer folderLocation ) {
        super(NewMedMainPage.class.getSimpleName(), Messages.newMedMainPageTitle);
        this.folderLocation = folderLocation;
    }

    public void createControl( Composite theParent ) {

        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(COLUMN_COUNT, false));
        setControl(pnlMain);
        createMainPanel(pnlMain);

        validatePage();
    }
    
    private Composite createMainPanel( Composite parent ) {
        // -----------------------------------------------------
        // Folder Name - target location to put the new .mxd
        // -----------------------------------------------------
        // Folder Label
        WidgetFactory.createLabel(parent, Messages.newMedWizardFolderLabel);
        // Folder Text widget
        final String name = (this.folderLocation == null ? null : this.folderLocation.getFullPath().makeRelative().toString());
        this.folderText = WidgetFactory.createTextField(parent, GridData.FILL_HORIZONTAL, 1, name, SWT.READ_ONLY);
        this.folderText.setToolTipText(Messages.newMedMainPageLocationTooltip);
        this.folderText.addModifyListener(new ModifyListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( final ModifyEvent event ) {
                folderModified();
            }
        });
        // Folder Browse Button
        btnFolderBrowse = WidgetFactory.createButton(parent, BROWSE_BUTTON);
        btnFolderBrowse.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                browseFolderButtonSelected();
            }
        });

        // -----------------------------------------------------
        // MED Name
        // -----------------------------------------------------
        // MED Name Label
        WidgetFactory.createLabel(parent, Messages.newMedWizardNameLabel);
        // MED Name text widget
        this.nameText = WidgetFactory.createTextField(parent, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.nameText.setToolTipText(Messages.newMedMainPageMedNameTooltip);
        this.nameText.addModifyListener(new ModifyListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( final ModifyEvent event ) {
                nameModified();
            }
        });

        this.openInEditorCB = WidgetFactory.createCheckBox(parent,
                                                           Messages.newMedMainPageOpenInEditorLabel,
                                                           GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_END,
                                                           COLUMN_COUNT,
                                                           Boolean.TRUE);
        this.openInEditorCB.setToolTipText(Messages.newMedMainPageOpenInEditorTooltip);

        // set focus to browse button if no folder selected. otherwise set focus to text field
        if (folderLocation == null) {
            btnFolderBrowse.setFocus();
        } else {
            nameText.setFocus();
        }
        
        return parent;
    }
    
    public IContainer getFolderLocation() {
        return this.folderLocation;
    }

    public String getMedName() {
        // append MED file extension if needed
        if (!medName.endsWith(ModelerCore.MED_FILE_EXTENSION)) {
            medName += ModelerCore.MED_FILE_EXTENSION;
        }
        return medName;
    }

    public boolean openInEditorChecked() {
        return this.openInEditorCB.getSelection();
    }

    void browseFolderButtonSelected() {
        this.folderLocation = WidgetUtil.showFolderSelectionDialog(this.folderLocation,
                                                                   new ModelingResourceFilter(),
                                                                   projectValidator);

        if (folderLocation != null) {
            this.folderText.setText(folderLocation.getFullPath().makeRelative().toString());

            if (CoreStringUtil.isEmpty(nameText.getText())) {
                nameText.setFocus();
            }
        }

        validatePage();
    }

    /**
     * @since 7.6
     */
    void folderModified() {
        validatePage();
    }

    /**
     * @since 7.6
     */
    void nameModified() {
        validatePage();
    }

    @Override
    public boolean canFlipToNextPage() {
        return isPageComplete();
    }

    /**
     * Validation logic for the page
     * 
     * @since 7.6
     */
    private void validatePage() {
        final IContainer folder;
        try {
            folder = WizardUtil.validateFileAndFolder(this.nameText, this.folderText, this,
                                                      ModelerCore.MED_FILE_EXTENSION,
                                                      false);
            if (getMessageType() == IMessageProvider.ERROR) {
                // WizardUtil.validateFileAndFolder can set error message and message type so no need to do further
                // validation if an error was already found
                return;
            }

            IStatus status = projectValidator.validate(new Object[] {folder});
            String proposedName = this.nameText.getText();

            if (!status.isOK()) {
                // only update the message if the vFolder is non-null;
                // if WizardUtil returned null, it already set the status
                // this corrects the case where the wrong message shows for
                // a bad filename.
                if (folder != null) {
                    setErrorMessage(status.getMessage());
                    setPageComplete(false);
                } // endif
            } else if (!nameValidator.isValidName(proposedName)) {
                setErrorMessage(Messages.newMedWizardMedNameErrorMsg);
                setPageComplete(false);
            } else {
                setErrorMessage(null);
                setPageComplete(true);
            }

            if (isPageComplete()) {
                this.medName = proposedName;
                this.folderLocation = folder;
            }
        } catch (final CoreException err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
            WizardUtil.setPageComplete(this, err.getLocalizedMessage(), IMessageProvider.ERROR);
        }
    }

}
