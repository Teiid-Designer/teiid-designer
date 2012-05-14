/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards.wsdl;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.ui.ICredentialsCommon.SecurityType;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 *
 */
public class WsdlFileSelectionComposite extends Composite implements UiConstants {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil
            .getPropertyPrefix(WsdlFileSelectionComposite.class);

    public static enum FileSelectionButtons {
        WORKSPACE, FILESYSTEM, URL;
    }

    public static interface IFileSelectionCallback {
        public void execute(File wsdlFile);
    }

    public static interface IURLSelectionCallback {
        public void execute(URL url, SecurityType securityType, String userName, String password);
    }

    WsdlFilter wsdlFilter = new WsdlFilter();

    /** Validator that makes sure the selection contains all WSDL files. */
    private ISelectionStatusValidator wsdlValidator = new WsdlValidator();

    private Button btnWorkspaceAdd;
    private Button btnFileSystemAdd;
    private Button btnURLAdd;

    private IFileSelectionCallback fileSystemCallback;

    private IFileSelectionCallback workspaceCallback;

    private IURLSelectionCallback urlCallback;

    public WsdlFileSelectionComposite(Composite parent, int style) {
        super(parent, style);
        GridLayoutFactory.swtDefaults().numColumns(3).applyTo(this);

        // workspace add button
        if (!ProductCustomizerMgr.getInstance().getProductCharacteristics()
                .isHiddenProjectCentric()) {
            btnWorkspaceAdd = WidgetFactory.createButton(this,
                    Util.getString(PREFIX + "button.addFile.workspace"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
            btnWorkspaceAdd.setToolTipText(Util.getString(PREFIX + "button.addFile.workspace.tip")); //$NON-NLS-1$
            GridDataFactory.swtDefaults().applyTo(btnWorkspaceAdd);
            btnWorkspaceAdd.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent theEvent) {
                    handleAddWorkspaceWsdlFile();
                }
            });
        }

        // file system add button
        btnFileSystemAdd = WidgetFactory.createButton(this,
                Util.getString(PREFIX + "button.addFile.filesystem"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        btnFileSystemAdd.setToolTipText(Util.getString(PREFIX + "button.addFile.filesystem.tip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(btnFileSystemAdd);
        btnFileSystemAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent theEvent) {
                handleAddFileSystemWsdlFile();
            }
        });

        // URL add button
        btnURLAdd = WidgetFactory.createButton(this,
                Util.getString(PREFIX + "button.addFile.URL"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        btnURLAdd.setToolTipText(Util.getString(PREFIX + "button.addFile.URL.tip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(btnURLAdd);
        btnURLAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent theEvent) {
                handleAddURLWsdlFile();
            }
        });
    }

    /**
     * @param workspace
     * @return
     */
    public boolean isVisible(FileSelectionButtons buttonType) {
        switch (buttonType) {
        case WORKSPACE:
            return btnWorkspaceAdd != null && btnWorkspaceAdd.isVisible();
        case FILESYSTEM:
            return btnFileSystemAdd.isVisible();
        case URL:
            return btnURLAdd.isVisible();
        default:
            return false;
        }
    }

    /**
     * @param workspace
     */
    public void setFocus(FileSelectionButtons buttonType) {
        switch (buttonType) {
        case WORKSPACE:
            if (btnWorkspaceAdd != null) {
                btnWorkspaceAdd.setFocus();
            }
        case FILESYSTEM:
            btnFileSystemAdd.setFocus();
        case URL:
            btnURLAdd.setFocus();
        default:
            // Do nothing
        }
    }

    /**
     * Set the functions that should be performed once selections have been made
     * in each of the dialogs.
     * 
     * @param fileSystemCallback
     * @param workspaceCallback
     * @param urlSelectionCallback
     */
    public void setCallbacks(IFileSelectionCallback fileSystemCallback,
            IFileSelectionCallback workspaceCallback, IURLSelectionCallback urlSelectionCallback) {
        this.fileSystemCallback = fileSystemCallback;
        this.workspaceCallback = workspaceCallback;
        this.urlCallback = urlSelectionCallback;
    }

    /**
     * Handler for choosing file system files.
     */
    public void handleAddFileSystemWsdlFile() {
        FileDialog dialog = new FileDialog(getShell(), SWT.MULTI);
        dialog.setText(Util.getString(PREFIX + "dialog.addWsdl.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(WsdlFileExtensions.FILE_DIALOG_WSDL_EXTENSIONS);

        if (dialog.open() == null)
            return;

        final String directory = dialog.getFilterPath();
        final String[] filenames = dialog.getFileNames();

        if (filenames == null || filenames.length == 0)
            return;

        boolean validFiles = true;

        for (int i = 0; i < filenames.length; i++) {
            String path = new StringBuffer().append(directory).append(File.separatorChar)
                    .append(filenames[i]).toString();
            File wsdlFile = new File(path);

            // make sure the right type of file was selected. since the
            // user can enter *.* in file name
            // field of the dialog they can view all files regardless of
            // the filter extensions. this allows
            // them to actually select invalid file types.

            if (WebServicePlugin.isWsdlFile((File) wsdlFile)) {
                fileSystemCallback.execute(wsdlFile);
            } else {
                validFiles = false;
            }
        }

        if (!validFiles) {
            // open file chooser again based on if user OK'd dialog
            if (MessageDialog.openQuestion(getShell(),
                    Util.getString(PREFIX + "dialog.wrongFileType.title"), //$NON-NLS-1$
                    Util.getString(PREFIX + "dialog.wrongFileType.msg"))) { //$NON-NLS-1$
                handleAddFileSystemWsdlFile();
            }
        }
    }

    /**
     * Handler for browsing to add one or more workspace WSDL files to the list.
     */
    private void handleAddWorkspaceWsdlFile() {
        Object[] wsdlFiles = WidgetUtil.showWorkspaceObjectSelectionDialog(
                Util.getString(PREFIX + "dialog.addWsdl.title"), //$NON-NLS-1$
                Util.getString(PREFIX + "dialog.addWsdl.msg"), //$NON-NLS-1$
                true, null, new ModelingResourceFilter(wsdlFilter), this.wsdlValidator,
                new ModelExplorerLabelProvider());

        /*
         * Defect 18786: In order to get over the problems with the 'Workspace
         * WsDL File' case, we will turn it into the 'File System Wsdl File'
         * case: 1. convert this array of IFiles into an array of Files 2. call
         * addWsdlFiles() with a value of 'false'.
         */

        if (wsdlFiles == null || wsdlFiles.length == 0)
            return;

        for (int i = 0; i < wsdlFiles.length; i++) {
            IFile ifFile = (IFile) wsdlFiles[i];

            // Convert the IFile object to a File object
            File fNew = ifFile.getLocation().toFile();
            workspaceCallback.execute(fNew);
        }
    }

    /**
     * Handler for entering and adding a WSDL Url to the list.
     * 
     * @since 5.1
     */
    void handleAddURLWsdlFile() {
        final WsdlUrlDialog dialog = new WsdlUrlDialog(getShell());
        // construct/display dialog
        dialog.create();
        dialog.open();

        if (dialog.getReturnCode() != Window.OK) {
            return;
        }

        urlCallback.execute(dialog.getUrlObject(), dialog.getSecurityOption(), dialog.getUserName(), dialog.getPassword());
    }
}
