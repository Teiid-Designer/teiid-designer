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
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
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
import com.metamatrix.modeler.modelgenerator.wsdl.ModelBuildingException;
import com.metamatrix.modeler.modelgenerator.wsdl.RelationalModelBuilder;
import com.metamatrix.modeler.modelgenerator.wsdl.TableBuilder;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiPlugin;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * Wizard for import of WSDL Source and generation of Relational Model from it.
 */
public class RelationalFromWSDLImportWizard extends AbstractWizard implements IImportWizard, ModelGeneratorWsdlUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RelationalFromWSDLImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = ModelGeneratorWsdlUiPlugin.getDefault().getImageDescriptor(Images.IMPORT_WSDL_ICON);

    /** This manager interfaces with the relational from wsdl generator */
    private WSDLImportWizardManager importManager;

    /** The page where the WSDL source file and Relational target model are selected. */
    private WizardPage selectWsdlPage;

    /** The page where the user selects which WSDL operations to build. */
    private WizardPage selectWsdlOperationsPage;

    private IStructuredSelection selection;

    /**
     * Creates a wizard for generating relational entities from WSDL source.
     */
    public RelationalFromWSDLImportWizard() {
        super(ModelGeneratorWsdlUiPlugin.getDefault(), TITLE, IMAGE);
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
        this.importManager = new WSDLImportWizardManager(UTIL);
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
        this.importManager = new WSDLImportWizardManager(UTIL);

        // construct pages
        this.selectWsdlPage = new SelectWsdlPage(this.importManager);
        this.selectWsdlOperationsPage = new SelectWsdlOperationsPage(this.importManager);
        this.selectWsdlPage.setPageComplete(false);
        this.selectWsdlOperationsPage.setPageComplete(false);
        addPage(this.selectWsdlPage);
        addPage(this.selectWsdlOperationsPage);

        // give the WSDL selection page the current workspace selection
        ((SelectWsdlPage)this.selectWsdlPage).setInitialSelection(theSelection);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = false;

        // Save object selections from previous page
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                // Wrap in transaction so it doesn't result in Significant Undoable
                boolean started = ModelerCore.startTxn(false, false, "Changing Sql Connections", //$NON-NLS-1$
                                                       new Object());
                boolean succeeded = false;
                try {
                    runFinish(monitor);
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
                Throwable t = ((InvocationTargetException)err).getTargetException();
                final IStatus iteStatus = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, getString("importError.msg"), t); //$NON-NLS-1$
                ErrorDialog.openError(this.getShell(), getString("importError.title"), getString("importError.msg"), iteStatus); //$NON-NLS-1$  //$NON-NLS-2$
                t.printStackTrace(System.err);
            } else {
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, getString("importError.msg"), err); //$NON-NLS-1$);
                ErrorDialog.openError(this.getShell(), getString("importError.title"), getString("importError.msg"), status); //$NON-NLS-1$  //$NON-NLS-2$
                err.printStackTrace(System.err);
            }
        } finally {
            dispose();
        }

        return result;
    }

    public void runFinish( IProgressMonitor theMonitor ) throws ModelBuildingException {
        // Target Model Name
        String modelName = this.importManager.getTargetModelName();

        // Target location for the new model
        IContainer container = this.importManager.getTargetModelLocation();

        // The Selected Operations
        List selectedOperations = this.importManager.getSelectedOperations();
        Operation[] opers = new Operation[selectedOperations.size()];
        for (int i = 0; i < selectedOperations.size(); i++) {
            opers[i] = (Operation)selectedOperations.get(i);
        }
        TableBuilder tableBuilder = new TableBuilder();
        Collection tables = tableBuilder.createTables(opers, UTIL);
        RelationalModelBuilder modelBuilder = new RelationalModelBuilder(tableBuilder.getNamespaces());
        modelBuilder.createModel(tables, modelName, container);
    }
}
