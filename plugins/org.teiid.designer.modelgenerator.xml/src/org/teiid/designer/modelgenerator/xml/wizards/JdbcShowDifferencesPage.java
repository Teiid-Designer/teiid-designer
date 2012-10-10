/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.wizards;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.PluginUtil;
import org.teiid.designer.compare.DifferenceReport;
import org.teiid.designer.compare.PropertyDifference;
import org.teiid.designer.compare.ui.tree.DifferenceReportsPanel;
import org.teiid.designer.modelgenerator.xml.IUiConstants;
import org.teiid.designer.modelgenerator.xml.XmlImporterUiPlugin;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WizardUtil;


/**
 * @since 8.0
 */
public class JdbcShowDifferencesPage extends WizardPage
    implements InternalUiConstants.Widgets, IUiConstants, IUiConstants.Images, IUiConstants.ProductInfo,
    IUiConstants.ProductInfo.Capabilities {

    private final XsdAsRelationalImportWizard jiwWizard;
    private DifferenceReportsPanel pnlDiffReport;
    // private DifferenceReport drDifferenceReport;
    private List lstDIfferenceReports;

    // private String sMessage;
    // private String sModelName;
    private boolean bIsVisible;

    private static PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();

    private static final String TITLE = util.getString("JdbcShowDifferencesPage.title"); //$NON-NLS-1$
    private static final String MESSAGE = util.getString("JdbcShowDifferencesPage.message"); //$NON-NLS-1$
    // private static final String TREE_TITLE
    //        = Util.getString("JdbcShowDifferencesPage.treeTitle"); //$NON-NLS-1$
    private static final String DIFF_DESCRIPTOR_TITLE = util.getString("JdbcShowDifferencesPage.diffDescriptorTitle"); //$NON-NLS-1$

    public JdbcShowDifferencesPage( final XsdAsRelationalImportWizard jiwWizard ) {
        super(JdbcShowDifferencesPage.class.getSimpleName(), TITLE, null);
        this.jiwWizard = jiwWizard;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
	public void createControl( final Composite parent ) {

        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout());
        pg.setLayoutData(new GridData(GridData.FILL_BOTH));
        setControl(pg);

        // Create the Difference Reports Panel
        final String treeTitle = ""; //$NON-NLS-1$
        final String tableTitle = DIFF_DESCRIPTOR_TITLE;

        final boolean enableProperySelection = true;
        final boolean showCheckboxes = true;
        /*
         * DifferenceReportsPanel( Composite theParent,
                                    String theTreeTitle,
                                    String theTableTitle,
                                    boolean enablePropertySelection,
                                    boolean showCheckboxes,
                                    boolean showMessage,                                    
                                    boolean bDisplayOnlyPrimaryMetamodelObjects ) {
         */
        pnlDiffReport = new DifferenceReportsPanel(pg, treeTitle, tableTitle, enableProperySelection, showCheckboxes, true, true);

        pnlDiffReport.setMessage(""); //$NON-NLS-1$
        super.setMessage(MESSAGE);

        final TableViewer tableViewer = pnlDiffReport.getTableViewer();
        if (tableViewer instanceof CheckboxTableViewer) ((CheckboxTableViewer)tableViewer).addCheckStateListener(new ICheckStateListener() {
            @Override
			public void checkStateChanged( final CheckStateChangedEvent theEvent ) {
                final Object checkedObject = theEvent.getElement();
                final boolean isChecked = theEvent.getChecked();
                if (checkedObject instanceof PropertyDifference) {
                    final PropertyDifference propDiff = (PropertyDifference)checkedObject;
                    propDiff.setSkip(!isChecked);
                }
            }
        });
    }

    public DifferenceReport getDifferenceReport() {
        return pnlDiffReport.getDifferenceReport();
    }

    public boolean isVisible() {
        return bIsVisible;
    }

    public void setDifferenceReport( final DifferenceReport drDifferenceReport ) {
        lstDIfferenceReports = new ArrayList(1);
        this.lstDIfferenceReports.add(drDifferenceReport);
        if (pnlDiffReport != null) pnlDiffReport.setDifferenceReports(lstDIfferenceReports);
    }

    public void setDifferenceReports( final List lstDIfferenceReports ) {
        this.lstDIfferenceReports = lstDIfferenceReports;

        if (pnlDiffReport != null) pnlDiffReport.setDifferenceReports(lstDIfferenceReports);
    }

    @Override
    public void setMessage( final String sMessage ) {
        // super.setMessage( sMessage );
    }

    public void setModelName( final String sModelName ) {
        // this.sModelName = sModelName;
        if (pnlDiffReport != null) pnlDiffReport.setModelName(sModelName);
    }

    @Override
    public void setVisible( final boolean bIsVisible ) {
        this.bIsVisible = bIsVisible;

        if (bIsVisible) setDifferenceReport(jiwWizard.getDifferenceReport());

        validatePage();

        super.setVisible(bIsVisible);
    }

    private void validatePage() {
        WizardUtil.setPageComplete(this);
    }
}
