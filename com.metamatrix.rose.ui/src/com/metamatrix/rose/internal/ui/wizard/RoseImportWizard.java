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
package com.metamatrix.rose.internal.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.rose.internal.IMessage;
import com.metamatrix.rose.internal.RoseImporter;
import com.metamatrix.rose.internal.handler.UmlHandler;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.rose.internal.ui.RoseUiPlugin;
import com.metamatrix.rose.internal.ui.util.MessageTableDialog;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * @since 4.1
 */
public final class RoseImportWizard extends AbstractWizard
    implements IImportWizard, IRoseUiConstants, IRoseUiConstants.Images, IWorkbenchWizard {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RoseImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final ImageDescriptor IMAGE = RoseUiPlugin.getDefault().getImageDescriptor(IMPORT_ICON);

    /**
     * @param id
     * @return @since 4.1
     */
    static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    private List diffReports;

    /** Business object for widget. */
    private RoseImporter importer;

    private SourceUnitsPage sourcePage;

    private TargetModelPage targetPage;

    private ModelUpdatePage updatePage;

    private AmbiguousTypeNamePage ambiguousTypePage;

    private ParsingResultsPage messagesPage;

    /**
     * @since 4.1
     */
    public RoseImportWizard() {
        super(RoseUiPlugin.getDefault(), TITLE, IMAGE);

        this.importer = new RoseImporter(new UmlHandler());
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     * @since 4.1
     */
    @Override
    public void addPages() {
        if (this.importer != null) {
            this.sourcePage = new SourceUnitsPage(this.importer);
            addPage(this.sourcePage);

            this.targetPage = new TargetModelPage(this.importer);
            addPage(this.targetPage);

            this.ambiguousTypePage = new AmbiguousTypeNamePage(this.importer);
            addPage(this.ambiguousTypePage);

            this.messagesPage = new ParsingResultsPage(this.importer);
            addPage(this.messagesPage);

            this.updatePage = new ModelUpdatePage(this.importer);
            addPage(this.updatePage);
        } else {
            // Create empty page
            WizardPage page = new WizardPage(RoseImportWizard.class.getSimpleName(), getString("notLicensedPageTitle"), //$NON-NLS-1$
                                             null) {
                public void createControl( Composite theParent ) {
                    setControl(new Composite(theParent, SWT.NONE));
                }
            };
            page.setMessage(getString("msg.notLicensed"), IMessageProvider.ERROR); //$NON-NLS-1$

            addPage(page);
        }
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        boolean result = (this.importer != null);
        if (result) {
            IWizardPage currPg = getContainer().getCurrentPage();
            // currPg maybe null if the wizard is not showing yet.
            result = ((currPg != null) && (currPg instanceof ModelUpdatePage) && ((ModelUpdatePage)currPg).isPageComplete());
        }

        return result;
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        super.createPageControls(pageContainer, false);
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#finish()
     * @since 4.1
     */
    @Override
    public boolean finish() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( IProgressMonitor theMonitor ) throws InvocationTargetException {
                try {
                    List problems = getImporter().importModels(theMonitor);

                    if (!problems.isEmpty()) {
                        // log problems first
                        final String PREFIX = getString("importProblemPrefix"); //$NON-NLS-1$

                        for (int size = problems.size(), i = 0; i < size; i++) {
                            IMessage problem = (IMessage)problems.get(i);
                            UTIL.log(problem.getType(), PREFIX + problem.getText());
                        }

                        // show user the problems
                        MessageTableDialog.openInformation(getShell(), problems);
                    }
                } catch (Exception theException) {
                    theMonitor.setCanceled(true);
                    throw new InvocationTargetException(theException);
                } finally {
                    theMonitor.done();
                }
            }
        };

        try {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(false, false, op);
        } catch (InvocationTargetException theException) {
            UTIL.log(theException);
            WidgetUtil.showError(theException);
        } catch (InterruptedException theException) {
        }

        return true;
    }

    private boolean generateDifferenceReports() {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( IProgressMonitor theMonitor ) throws InvocationTargetException {
                try {
                    setDifferenceReports(getImporter().generateDifferenceReports(theMonitor));
                } catch (Exception theException) {
                    theMonitor.setCanceled(true);
                    throw new InvocationTargetException(theException);
                } finally {
                    theMonitor.done();
                }
            }
        };

        DifferenceReportRunner runner = new DifferenceReportRunner(op);
        getShell().getDisplay().syncExec(runner);

        return runner.isCompleted();
    }

    private class DifferenceReportRunner implements Runnable {
        boolean completed = false;
        IRunnableWithProgress op;

        public DifferenceReportRunner( IRunnableWithProgress theOperation ) {
            this.op = theOperation;
        }

        public boolean isCompleted() {
            return this.completed;
        }

        /**
         * @see java.lang.Runnable#run()
         * @since 4.2
         */
        public void run() {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());

            try {
                dlg.run(true, true, this.op);
            } catch (InvocationTargetException theException) {
                UTIL.log(theException);
                WidgetUtil.showError(theException);
            } catch (InterruptedException theException) {
            }

            this.completed = !dlg.getProgressMonitor().isCanceled();
        }
    }

    private IWizardPage getAmbiguousTypePage() {
        if (this.ambiguousTypePage == null) {
            this.ambiguousTypePage = new AmbiguousTypeNamePage(this.importer);
        }

        return this.ambiguousTypePage;
    }

    RoseImporter getImporter() {
        return this.importer;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( final IWizardPage thePage ) {
        /*----- Design Notes -----------------------------------
         Pages:
            A. sourcePage
            B. targetPage
            C. ambiguousTypePage (optional)
            D. messagesPage (optional)
            E. updatePage

         Scenarios:
             1. A, B, E
             2. A, B, D, E
             3. A, B, C, D, E
             4. A, B, C, E
             5. A, B, parse cancelled stay on B
             6. A, B, diff reports cancelled stay on B
             7. A, B, C, diff reports cancelled stay on C
        --------------------------------------------------------*/

        IWizardPage result = null;

        // only need to define logic for those pages where the next page is dynamic.
        // the call to super will handle everything else.

        if (thePage == this.sourcePage) {
            result = this.targetPage;
        } else if (thePage == this.targetPage) {
            // make sure editor is saved
            this.targetPage.handleUnsavedEditor();

            // all scenarios
            if (parseSelectedUnits()) {
                if (this.importer.getAmbiguousReferences().isEmpty()) {
                    if (generateDifferenceReports()) {
                        if (getImporter().getParseProblems().isEmpty()) {
                            // scenario 1
                            result = this.updatePage;
                        } else {
                            // scenario 2
                            result = this.messagesPage;
                        }
                    } else {
                        // scenario 6
                        result = this.targetPage;
                    }
                } else {
                    // scenarios 3, 4, & 7
                    result = getAmbiguousTypePage();
                }
            } else {
                // scenario 5
                result = this.targetPage;
            }
        } else if (thePage == this.ambiguousTypePage) {
            // scenarios 3, 4, & 7
            if (generateDifferenceReports()) {
                if (this.importer.getParseProblems().isEmpty()) {
                    // scenario 4
                    result = this.updatePage;
                } else {
                    // scenerio 3
                    result = this.messagesPage;
                }
            } else {
                // scenario 7
                result = this.ambiguousTypePage;
            }
        } else if (thePage == this.messagesPage) {
            result = this.updatePage;
        } else if (thePage != this.updatePage) {
            Assertion.failed("Unexpected Rose Import Wizard Page:" + thePage); //$NON-NLS-1$
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage( IWizardPage thePage ) {
        if (thePage == this.targetPage) {
            // make sure editor is saved
            this.targetPage.handleUnsavedEditor();
        }

        return super.getPreviousPage(thePage);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.1
     */
    public void init( IWorkbench theWorkbench,
                      IStructuredSelection theSelection ) {
    }

    private boolean parseSelectedUnits() {
        boolean result = false;

        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( IProgressMonitor theMonitor ) throws InvocationTargetException {
                try {
                    // parseSelectedUnits will create ambiguous references if needed which are stored in the importer
                    getImporter().parseSelectedUnits(theMonitor);
                } catch (Exception theException) {
                    theMonitor.setCanceled(true);
                    throw new InvocationTargetException(theException);
                }
            }
        };

        try {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(true, true, op);
            result = !dlg.getProgressMonitor().isCanceled();
        } catch (InvocationTargetException theException) {
            UTIL.log(theException);
            WidgetUtil.showError(theException);
        } catch (InterruptedException theException) {
        }

        return result;
    }

    /**
     * @param theDifferenceReports
     * @since 4.1
     */
    public void setDifferenceReports( List theDifferenceReports ) {
        this.diffReports = theDifferenceReports;
        this.updatePage.setDifferenceReports(this.diffReports);
    }
}
