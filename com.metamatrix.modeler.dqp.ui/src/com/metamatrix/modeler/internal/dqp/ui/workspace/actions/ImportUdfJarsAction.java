/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.core.util.FileSeparatorUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.config.DqpExtensionsHandler;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.5
 */
public class ImportUdfJarsAction extends Action implements
                                                       DqpUiConstants {
    public static final String ACTION_ID = "ImportUdfJarsAction"; //$NON-NLS-1$
    
    private final static String PREFIX = I18nUtil.getPropertyPrefix(ImportUdfJarsAction.class);
    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 5.5.3
     */
    public ImportUdfJarsAction() {
        setText(UTIL.getString(PREFIX + "label")); //$NON-NLS-1$
        setToolTipText(UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.IMPORT_JAR_ICON));
        setId(ACTION_ID);
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 5.5.3
     */
    @Override
    public void run() {
        Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
        FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.PRIMARY_MODAL | SWT.MULTI);
        dialog.setText(UTIL.getString(PREFIX + "importExtensionJarsDialog.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(DqpUiConstants.IMPORT_FILE_EXTENSIONS_NEW[DqpUiConstants.IMPORT_CONNECTOR_MOD]);
        dialog.setFilterNames(DqpUiConstants.IMPORT_FILE_NAMES_NEW[DqpUiConstants.IMPORT_CONNECTOR_MOD]);
        dialog.open();

        String[] fileNames = dialog.getFileNames();

        if ((fileNames != null) && (fileNames.length > 0)) {
            
            if( !confirmOverwrite(fileNames) ) {
                return;
            }
            
            String filterPath = dialog.getFilterPath();
            final File[] jarFiles = new File[fileNames.length];

            for (int i = 0; i < fileNames.length; ++i) {
                jarFiles[i] = new File(filterPath + FileSeparatorUtil.getOSFileSeparator() + fileNames[i]);
            }

            final boolean[] success = new boolean[1];
            final Object eventSource = this;
            Runnable importOperation = new Runnable() {

                public void run() {
                    DqpExtensionsHandler extensionHandler = DqpPlugin.getInstance().getExtensionsHandler();
                    success[0] = extensionHandler.addUdfJars(eventSource, jarFiles); 
                }
            };
                                                                                                                      
            BusyIndicator.showWhile(shell.getDisplay(), importOperation);

            String title = UTIL.getString(PREFIX + "successTitle"); //$NON-NLS-1$

            if (success[0]) {
                String message = UTIL.getString(PREFIX + "successMessage"); //$NON-NLS-1$ 
                MessageDialog.openInformation(shell, title, message);
            } else {
                String message = UTIL.getString(PREFIX + "errorMessage"); //$NON-NLS-1$ 
                MessageDialog.openError(shell, title, message);
            }
        }
    }
    
    private boolean confirmOverwrite(String[] fileNames) {
        IPath extPath = DqpPlugin.getInstance().getExtensionsHandler().getDqpExtensionsFolderPath();
        boolean existing = false;
        for( String fName: fileNames ) {
            File theFile = extPath.append(fName).toFile();
            if( theFile.exists() ) {
                existing = true;
            }
            if( existing ) {
                break;
            }
        }

        boolean overwrite = false;
        if( existing ) {
            overwrite = MessageDialog.openConfirm(UiUtil.getWorkbenchShellOnlyIfUiThread(), 
                                                  UTIL.getStringOrKey(PREFIX + "confirmOverwrite.title"), //$NON-NLS-1$
                                                  UTIL.getStringOrKey(PREFIX + "confirmOverwrite.msg")); //$NON-NLS-1$
        } else {
            overwrite = true;
        }
        
        return overwrite;
    }
}
