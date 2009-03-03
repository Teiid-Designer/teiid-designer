/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.ui.wizard;

import java.util.List;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.compare.ui.tree.DifferenceReportsPanel;
import com.metamatrix.rose.internal.RoseImporter;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * ModelUpdatePage
 */
public final class ModelUpdatePage extends AbstractWizardPage implements IRoseUiConstants {

    /** Wizard page identifier. */
    public static final String PAGE_ID = ModelUpdatePage.class.getSimpleName();

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ModelUpdatePage.class);

    private DifferenceReportsPanel pnlDiffReports;

    private CheckboxTreeViewer viewer;

    /**
     * Constructs a <code>ModelUpdatePage</code> wizard page using the specified business object.
     * 
     * @param theImporter the wizard business object
     */
    public ModelUpdatePage( RoseImporter theImporter ) {
        super(PAGE_ID, UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
        setPageComplete(false);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.1
     */
    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;

        Composite pnlMain = WidgetFactory.createPanel(theParent);
        pnlMain.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnlMain);

        createMainPanelContents(pnlMain, COLUMNS);
    }

    private void createMainPanelContents( Composite theParent,
                                          int theColumns ) {
        final int COLUMNS = 1;
        ViewForm viewForm = WidgetFactory.createViewForm(theParent, SWT.BORDER, GridData.FILL_BOTH, theColumns);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, UTIL.getString(PREFIX + "label.viewForm"))); //$NON-NLS-1$
        createViewFormContents(viewForm, COLUMNS);
    }

    private void createViewFormContents( ViewForm theViewForm,
                                         int theColumns ) {
        final int COLUMNS = 1;
        Composite pnl = WidgetFactory.createPanel(theViewForm, SWT.NONE, GridData.FILL_BOTH, theColumns, COLUMNS);
        theViewForm.setContent(pnl);

        this.pnlDiffReports = new DifferenceReportsPanel(pnl, UTIL.getString(PREFIX + "pnl.diffReports.treeTitle"), //$NON-NLS-1$
                                                         UTIL.getString(PREFIX + "pnl.diffReports.tableTitle"), //$NON-NLS-1$
                                                         false, true, false, true);
        this.pnlDiffReports.setLayoutData(new GridData(GridData.FILL_BOTH));

        // setup checkbox listener
        this.viewer = (CheckboxTreeViewer)this.pnlDiffReports.getTreeViewer();
        viewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged( CheckStateChangedEvent theEvent ) {
                updatePageStatus();
            }
        });
    }

    DifferenceReportsPanel getDifferenceReportsPanel() {
        return this.pnlDiffReports;
    }

    /**
     * @param theDifferenceReports
     * @since 4.1
     */
    public void setDifferenceReports( final List theDifferenceReports ) {
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                getDifferenceReportsPanel().setDifferenceReports(theDifferenceReports);
                updatePageStatus();
            }
        });
    }

    void updatePageStatus() {
        TreeItem[] roots = viewer.getTree().getItems();
        int numUnchecked = 0;

        for (int i = 0; i < roots.length; i++) {
            if (!roots[i].getChecked()) {
                ++numUnchecked;
            }
        }

        boolean complete = (numUnchecked != roots.length);

        if (isPageComplete() != complete) {
            setPageComplete(complete);

            // set page message
            if (isPageComplete()) {
                setErrorMessage(null); // must clear in order to get other messages to show
                setMessage(UTIL.getString(PREFIX + "page.completeMsg")); //$NON-NLS-1$
            } else {
                setErrorMessage(UTIL.getString(PREFIX + "page.incompleteMsg")); //$NON-NLS-1$
            }
        }
    }
}
