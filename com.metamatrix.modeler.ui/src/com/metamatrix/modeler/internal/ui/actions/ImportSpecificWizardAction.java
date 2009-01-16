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
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.wizards.ImportWizardExtensionManager;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;

/**
 * @since 4.3
 */
public class ImportSpecificWizardAction extends Action implements ICheatSheetAction {

    private static final String DEFAULT_IMPORT_WIZARD_ID = UiConstants.ExtensionPoints.ImportWizards.ContributionIds.JDBC;

    private String wizardId = DEFAULT_IMPORT_WIZARD_ID;
    boolean wizardResult = false;

    static final String VDB_REQUIRED_TITLE = UiConstants.Util.getString("RemoveProjectAction.noVdbWarningTitle"); //$NON-NLS-1$
    static final String VDB_REQUIRED_MESSAGE = UiConstants.Util.getString("RemoveProjectAction.noVdbWarningMessage"); //$NON-NLS-1$

    public ImportSpecificWizardAction() {
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.4
     */
    @Override
    public void run() {
        wizardResult = false;
        launchImportWizard(wizardId);
        notifyResult(wizardResult);
    }

    public void run( String[] params,
                     ICheatSheetManager manager ) {
        if (params != null && params.length > 0) {
            wizardResult = false;
            launchImportWizard(params[0]);
        }

        notifyResult(wizardResult);
    }

    public void launchImportWizard( final String wizardId ) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                IWizard wizard = ImportWizardExtensionManager.getWizard(wizardId);
                if (wizard != null) {
                    IWorkbench workbench = UiPlugin.getDefault().getWorkbench();
                    IStructuredSelection selection = (StructuredSelection)UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
                    if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
                        Object hiddenProject = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject();
                        if (hiddenProject != null) {
                            selection = new StructuredSelection(hiddenProject);
                        } else {
                            MessageDialog.openWarning(workbench.getActiveWorkbenchWindow().getShell(),
                                                      VDB_REQUIRED_TITLE,
                                                      VDB_REQUIRED_MESSAGE);
                            // END THE IMPORT
                            return;
                        }
                    }

                    ((IImportWizard)wizard).init(workbench, selection);
                    final WizardDialog dialog = new WizardDialog(((Wizard)wizard).getShell(), wizard);
                    dialog.open();
                    if (dialog.getReturnCode() == Window.OK) {
                        wizardResult = true;
                    }
                }
            }
        });
    }

    /**
     * @return Returns the wizardId.
     * @since 4.3
     */
    public String getWizardId() {
        return this.wizardId;
    }

    /**
     * @param theWizardId The wizardId to set.
     * @since 4.3
     */
    public void setWizardId( String theWizardId ) {
        this.wizardId = theWizardId;
    }
}
