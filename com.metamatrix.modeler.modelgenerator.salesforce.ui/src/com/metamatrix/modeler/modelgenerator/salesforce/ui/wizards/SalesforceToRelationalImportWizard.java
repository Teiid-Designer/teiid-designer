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
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.Activator;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import com.metamatrix.modeler.modelgenerator.salesforce.util.ModelBuildingException;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

public class SalesforceToRelationalImportWizard extends AbstractWizard
    implements IImportWizard, ModelGeneratorSalesforceUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(SalesforceToRelationalImportWizard.class);

    private static final String TITLE = getString("Salesforce.Import.Wizard"); //$NON-NLS-1$

    SalesforceImportWizardManager importManager;

    private WizardPage selectSalesforceObjectsPage;

    private IStructuredSelection selection;

    private CredentialsPage credentialsPage;

    private ModelSelectionPage modelSelectionPage;

    private ShowDifferencesPage differencesPage;

    public SalesforceToRelationalImportWizard() {
        super(Activator.getDefault(), TITLE, null);
    }

    /**
     * Get the localized string text for the provided id
     */
    private static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        super.createPageControls(pageContainer);
    }

    /**
     * Method declared on IWorkbenchWizard.
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection currentSelection ) {

        this.importManager = new SalesforceImportWizardManager();
        this.selection = currentSelection;

        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }

        createWizardPages(this.selection);
        setNeedsProgressMonitor(true);
    }

    /**
     * Create Wizard pages for the wizard
     * 
     * @param theSelection the initial workspace selection
     */
    public void createWizardPages( ISelection theSelection ) {
        this.importManager = new SalesforceImportWizardManager();

        // construct pages
        this.credentialsPage = new CredentialsPage(this.importManager);
        this.selectSalesforceObjectsPage = new SelectSalesforceObjectsPage(this.importManager);
        this.modelSelectionPage = new ModelSelectionPage(this.importManager);
        this.differencesPage = new ShowDifferencesPage(this.importManager);

        this.credentialsPage.setPageComplete(false);
        this.selectSalesforceObjectsPage.setPageComplete(false);
        this.modelSelectionPage.setPageComplete(false);
        this.differencesPage.setPageComplete(false);

        addPage(this.credentialsPage);
        addPage(this.selectSalesforceObjectsPage);
        addPage(this.modelSelectionPage);
        addPage(this.differencesPage);

        this.modelSelectionPage.setInitialSelection(theSelection);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = false;

        final IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                // Wrap in transaction so it doesn't result in Significant
                // Undoable
                boolean started = ModelerCore.startTxn(false, false, "Creating Salesforce model", //$NON-NLS-1$
                                                       new Object());
                boolean succeeded = false;
                try {
                    importManager.runFinish(monitor);
                    succeeded = true;
                } catch (ModelBuildingException mbe) {
                    mbe.printStackTrace(System.err);
                    throw new InvocationTargetException(mbe);
                } catch (Throwable t) {
                    throw new InvocationTargetException(t);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        };
        try {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(true, true, op);
            result = true;
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException)err).getCause();
                final IStatus iteStatus = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, getString("importError.msg"), t); //$NON-NLS-1$
                ErrorDialog.openError(this.getShell(), getString("importError.title"), getString("importError.msg"), iteStatus); //$NON-NLS-1$  //$NON-NLS-2$
                Activator.getDefault().getLog().log(iteStatus);
            } else {
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, getString("importError.msg"), err); //$NON-NLS-1$);
                ErrorDialog.openError(this.getShell(), getString("importError.title"), getString("importError.msg"), status); //$NON-NLS-1$  //$NON-NLS-2$
                err.printStackTrace(System.err);
                Activator.getDefault().getLog().log(status);
            }
        } finally {
            dispose();
        }

        return result;
    }

    @Override
    public boolean canFinish() {
        boolean result = super.canFinish();
        result = importManager.canFinish();
        return result;
    }
}
