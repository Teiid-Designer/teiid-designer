/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.ui.tree.DifferenceReportsPanel;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class ShowDifferencesPage extends AbstractWizardPage
    implements ModelGeneratorSalesforceUiConstants, ModelGeneratorSalesforceUiConstants.Images,
    ModelGeneratorSalesforceUiConstants.HelpContexts {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ShowDifferencesPage.class);

    public ShowDifferencesPage( SalesforceImportWizardManager importManager ) {
        super(ShowDifferencesPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = importManager;
    }

    private DifferenceReportsPanel pnlDiffReport;
    private List lstDIfferenceReports;
    SalesforceImportWizardManager importManager;
    private boolean bIsVisible;
    protected DifferenceReport shuntDiffReport;

    private static final String MESSAGE = getString("message"); //$NON-NLS-1$
    private static final String DIFF_DESCRIPTOR_TITLE = getString("diffDescriptorTitle"); //$NON-NLS-1$

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createControl( Composite parent ) {

        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout());
        pg.setLayoutData(new GridData(GridData.FILL_BOTH));
        setControl(pg);

        // Create the Difference Reports Panel
        String treeTitle = ""; //$NON-NLS-1$
        String tableTitle = DIFF_DESCRIPTOR_TITLE;

        boolean enableProperySelection = true;
        boolean showCheckboxes = true;
        pnlDiffReport = new DifferenceReportsPanel(pg, treeTitle, tableTitle, enableProperySelection, showCheckboxes, true, true);

        pnlDiffReport.setMessage(""); //$NON-NLS-1$
        super.setMessage(MESSAGE);

        TableViewer tableViewer = pnlDiffReport.getTableViewer();
        if (tableViewer instanceof CheckboxTableViewer) {
            ((CheckboxTableViewer)tableViewer).addCheckStateListener(new ICheckStateListener() {
                @Override
                public void checkStateChanged( CheckStateChangedEvent theEvent ) {
                    Object checkedObject = theEvent.getElement();
                    boolean isChecked = theEvent.getChecked();
                    if (checkedObject instanceof PropertyDifference) {
                        PropertyDifference propDiff = (PropertyDifference)checkedObject;
                        propDiff.setSkip(!isChecked);
                    }
                }
            });
        }
    }

    @Override
    public void setVisible( boolean bIsVisible ) {
        this.bIsVisible = bIsVisible;

        if (bIsVisible) {
            IRunnableWithProgress op = new IRunnableWithProgress() {
                @Override
                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        shuntDiffReport = importManager.getDifferenceReport(monitor);
                        monitor.done();
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    }
                }
            };
            try {
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                Shell shell = this.getShell();
                Status status = new Status(IStatus.ERROR, PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
                ErrorDialog.openError(shell,
                                      getString("dialog.dataModelCreationError.title"), cause.getLocalizedMessage(), status); //$NON-NLS-1$  
                super.setVisible(false);
                return;
            } catch (InterruptedException e) {
                super.setVisible(false);
                return;
            }
            setDifferenceReport(shuntDiffReport);
            importManager.setCanFinish(true);
        }

        validatePage();

        super.setVisible(bIsVisible);
    }

    private void validatePage() {
        WizardUtil.setPageComplete(this);
    }

    public boolean isVisible() {
        return bIsVisible;
    }

    public DifferenceReport getDifferenceReport() {
        return pnlDiffReport.getDifferenceReport();
    }

    public void setDifferenceReport( DifferenceReport drDifferenceReport ) {
        lstDIfferenceReports = new ArrayList(1);
        this.lstDIfferenceReports.add(drDifferenceReport);
        if (pnlDiffReport != null) {
            pnlDiffReport.setDifferenceReports(lstDIfferenceReports);
        }
    }

    public void setDifferenceReports( List lstDIfferenceReports ) {
        this.lstDIfferenceReports = lstDIfferenceReports;

        if (pnlDiffReport != null) {
            pnlDiffReport.setDifferenceReports(lstDIfferenceReports);
        }
    }

    @Override
    public void setMessage( String sMessage ) {
    }

    public void setModelName( String sModelName ) {
        if (pnlDiffReport != null) {
            pnlDiffReport.setModelName(sModelName);
        }
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }
}
