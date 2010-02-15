/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.teiid.designer.udf.UdfModelImporter;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.modeler.util.FileUtil.Extensions;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * A wizard page to allow the user to choose the UDF model zip file to import.
 * 
 * @since 6.0.0
 */
public final class SourceSelectionPage extends AbstractWizardPage {

    /**
     * A prefix for I18n message keys.
     * 
     * @since 6.0.0
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SourceSelectionPage.class);

    private interface DialogSettingsProperties {
        String PAGE_SECTION = SourceSelectionPage.class.getSimpleName();
        String LOCATIONS = "locations"; //$NON-NLS-1$
    }

    /**
     * MRU of previously imported files.
     * 
     * @since 6.0.0
     */
    private Combo cbxZipFileName;

    /**
     * The business object.
     * 
     * @since 6.0.0
     */
    private final UdfModelImporter importer;

    /**
     * @param importer the wizard business object
     * @since 6.0.0
     */
    public SourceSelectionPage( UdfModelImporter importer ) {
        super(SourceSelectionPage.class.getSimpleName(), UdfUiPlugin.UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
        this.importer = importer;
    }

    /**
     * @return the business object
     * @since 6.0.0
     */
    UdfModelImporter accessImporter() {
        return this.importer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 6.0.0
     */
    @Override
    public void createControl( Composite parent ) {
        Composite mainControl = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        setControl(mainControl);

        // pnlBrowse looks like:
        // label textfield button
        Composite pnlBrowse = WidgetFactory.createPanel(mainControl, SWT.NONE, GridData.FILL_HORIZONTAL, 1, 3);

        // add label
        WidgetFactory.createLabel(pnlBrowse, UdfUiPlugin.UTIL.getString(PREFIX + "lblImportLocation")); //$NON-NLS-1$

        // add MRU combo box of previously imported files
        this.cbxZipFileName = WidgetFactory.createCombo(pnlBrowse,
                                                        SWT.READ_ONLY,
                                                        GridData.FILL_HORIZONTAL,
                                                        getDialogSettings().getArray(DialogSettingsProperties.LOCATIONS));
        this.cbxZipFileName.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleInputFileModified();
            }
        });

        // add browse button
        Button btnBrowse = WidgetFactory.createButton(pnlBrowse, UdfUiPlugin.UTIL.getString(PREFIX + "btnBrowse")); //$NON-NLS-1$
        btnBrowse.setToolTipText(UdfUiPlugin.UTIL.getString(PREFIX + "btnBrowse.tip")); //$NON-NLS-1$
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleBrowse();
            }
        });
        WidgetUtil.setLayoutData(btnBrowse);

        // set initial state
        setPageComplete(false);
        setMessage(UdfUiPlugin.UTIL.getString(PREFIX + "initialMsg")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     * @since 6.0.0
     */
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings temp = super.getDialogSettings();
        IDialogSettings settings = temp.getSection(DialogSettingsProperties.PAGE_SECTION);

        // add a section in the wizard's dialog setting for this page
        if (settings == null) {
            settings = temp.addNewSection(DialogSettingsProperties.PAGE_SECTION);
        }

        return settings;
    }

    /**
     * Handler for when the browse button is clicked.
     * 
     * @since 6.0.0
     */
    void handleBrowse() {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        dialog.setText(UdfUiPlugin.UTIL.getString(PREFIX + "browseDialogtitle")); //$NON-NLS-1$

        // set starting directory if necessary
        String currentPath = this.importer.getSourcePath();

        if (currentPath != null) {
            int index = currentPath.lastIndexOf(File.separator);

            if (index != -1) {
                dialog.setFilterPath(currentPath.substring(0, index));
            }
        }

        String path = dialog.open();

        // modify MRU history if new zip file name selected
        if (path != null) {
            // make sure file extension exists
            if (!FileUtil.isZipFileName(path)) {
                path += Extensions.ZIP;
            }

            ArrayList items = new ArrayList(Arrays.asList(this.cbxZipFileName.getItems()));

            if (!items.contains(path)) {
                items.add(path);
                WidgetUtil.setComboItems(this.cbxZipFileName, items, null, false, path);
            } else {
                this.cbxZipFileName.setText(path);
            }
        }

        updateState();
    }

    /**
     * Handler for when the input file name has been changed.
     * 
     * @since 6.0.0
     */
    void handleInputFileModified() {
        this.importer.setSourcePath(this.cbxZipFileName.getText());
        updateState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizardPage#saveSettings()
     * @since 6.0.0
     */
    @Override
    public void saveSettings() {
        IDialogSettings settings = getDialogSettings();
        WidgetUtil.saveSettings(settings, DialogSettingsProperties.LOCATIONS, this.cbxZipFileName);
        WidgetUtil.removeMissingResources(settings, DialogSettingsProperties.LOCATIONS);
    }

    /**
     * Refreshes the UI to reflect the current state of the business object.
     * 
     * @since 6.0.0
     */
    void updateState() {

        IStatus status = this.importer.isValidZipFile();
        setPageComplete(status.isOK());

        if (isPageComplete()) {
            setErrorMessage(null);
            setMessage(UdfUiPlugin.UTIL.getString(PREFIX + "archivesBeingImported")); //$NON-NLS-1$

            // this.viewer.refresh();
        } else {
            setErrorMessage(status.getMessage());
        }
    }
}
