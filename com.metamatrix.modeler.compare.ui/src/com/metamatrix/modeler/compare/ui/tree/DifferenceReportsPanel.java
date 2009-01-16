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
package com.metamatrix.modeler.compare.ui.tree;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.Label;
import com.metamatrix.ui.internal.widget.StatusLabel;

/**
 * Panel to show the tree of changes, allowing for selection / deselection of parts of them.
 */
public class DifferenceReportsPanel extends Composite implements StringUtil.Constants {

    private static final String OLD_FILE_TEXT = UiConstants.Util.getString("DifferenceReportsPanel.oldFile.text"); //$NON-NLS-1$
    private static final String NEW_FILE_TEXT = UiConstants.Util.getString("DifferenceReportsPanel.newFile.text"); //$NON-NLS-1$

    private static final String FILE_IMG_PFX = UiConstants.Util.getString("DifferenceReportsPanel.fileImagePrefix"); //$NON-NLS-1$
    private static final String FILE_IMG_SFX = UiConstants.Util.getString("DifferenceReportsPanel.fileImageSuffix"); //$NON-NLS-1$

    private static final String FIRST_FILE_TEXT = UiConstants.Util.getString("DifferenceReportsPanel.firstFile.text"); //$NON-NLS-1$
    private static final String SECOND_FILE_TEXT = UiConstants.Util.getString("DifferenceReportsPanel.secondFile.text"); //$NON-NLS-1$

    public static final int USE_OLD_NEW_TERMINOLOGY = 1;
    public static final int USE_FIRST_SECOND_TERMINOLOGY = 2;

    private Composite pnlOuter;
    private Composite pnlHeader;
    private Composite pnlMessage;
    private StatusLabel slblMessage;
    private boolean showMessage = true;

    private Composite pnlNames;
    private Label oldLabel;
    private Label oldImgLabel;
    private Label oldFld;
    private Label newLabel;
    private Label newImgLabel;
    private Label newFld;
    private String sOldObjectName = EMPTY_STRING;
    private String sNewObjectName = EMPTY_STRING;

    private Composite pnlCounts;
    private Label oldOnlyLabel;
    private Label oldOnlyImgLabel;
    private Text txtAdds;
    private Text txtMods;
    private Label newOnlyLabel;
    private Label newOnlyImgLabel;
    private Text txtDeletes;

    private SashForm sfSashform;
    private CompareTreePanel treePanel;
    private DifferenceDescriptorPanel diffDescriptorPanel;
    private String treeTitle;
    private String tableTitle;
    private int[] weights = {6, 4};
    private List<DifferenceReport> theDifferenceReports;
    private String sMessage;
    private String sModelName;
    private int terminology = USE_OLD_NEW_TERMINOLOGY;

    // default this to false:
    private boolean bDisplayOnlyPrimaryMetamodelObjects = false;

    /**
     * constructor
     * 
     * @param the parent composite
     * @param theTreeTitle the title for the treeViewer Composite
     * @param theTableTitle the title for the tableViewer Composite
     */
    public DifferenceReportsPanel( Composite theParent,
                                   String theTreeTitle,
                                   String theTableTitle,
                                   boolean enablePropertySelection,
                                   boolean showCheckboxes ) {
        super(theParent, SWT.NONE);

        this.treeTitle = theTreeTitle;
        this.tableTitle = theTableTitle;
        initialize(enablePropertySelection, showCheckboxes);
    }

    /**
     * constructor
     * 
     * @param the parent composite
     * @param theTreeTitle the title for the treeViewer Composite
     * @param theTableTitle the title for the tableViewer Composite
     */
    public DifferenceReportsPanel( Composite theParent,
                                   String theTreeTitle,
                                   String theTableTitle,
                                   boolean enablePropertySelection,
                                   boolean showCheckboxes,
                                   boolean showMessage ) {
        super(theParent, SWT.NONE);

        this.treeTitle = theTreeTitle;
        this.tableTitle = theTableTitle;
        this.showMessage = showMessage;
        initialize(enablePropertySelection, showCheckboxes);
    }

    /**
     * constructor
     * 
     * @param the parent composite
     * @param theTreeTitle the title for the treeViewer Composite
     * @param theTableTitle the title for the tableViewer Composite
     */
    public DifferenceReportsPanel( Composite theParent,
                                   String theTreeTitle,
                                   String theTableTitle,
                                   boolean enablePropertySelection,
                                   boolean showCheckboxes,
                                   boolean showMessage,
                                   int iTerminology ) {
        super(theParent, SWT.NONE);

        this.treeTitle = theTreeTitle;
        this.tableTitle = theTableTitle;
        this.showMessage = showMessage;
        this.terminology = iTerminology;
        initialize(enablePropertySelection, showCheckboxes);
    }

    /**
     * constructor
     * 
     * @param the parent composite
     * @param theTreeTitle the title for the treeViewer Composite
     * @param theTableTitle the title for the tableViewer Composite
     */
    public DifferenceReportsPanel( Composite theParent,
                                   String theTreeTitle,
                                   String theTableTitle,
                                   boolean enablePropertySelection,
                                   boolean showCheckboxes,
                                   boolean showMessage,
                                   boolean bDisplayOnlyPrimaryMetamodelObjects ) {
        super(theParent, SWT.NONE);

        this.treeTitle = theTreeTitle;
        this.tableTitle = theTableTitle;
        this.showMessage = showMessage;
        this.bDisplayOnlyPrimaryMetamodelObjects = bDisplayOnlyPrimaryMetamodelObjects;
        initialize(enablePropertySelection, showCheckboxes);
    }

    /**
     * constructor
     * 
     * @param the parent composite
     * @param theTreeTitle the title for the treeViewer Composite
     * @param theTableTitle the title for the tableViewer Composite
     * @param diffReport the DifferenceReport
     */
    public DifferenceReportsPanel( Composite parent,
                                   String treeTitle,
                                   String tableTitle,
                                   boolean enablePropertySelection,
                                   boolean showCheckboxes,
                                   DifferenceReport diffReport ) {
        this(parent, treeTitle, tableTitle, enablePropertySelection, showCheckboxes, Collections.singletonList(diffReport));
    }

    /**
     * constructor
     * 
     * @param the parent composite
     * @param theTreeTitle the title for the treeViewer Composite
     * @param theTableTitle the title for the tableViewer Composite
     * @param diffReport the DifferenceReport
     */
    public DifferenceReportsPanel( Composite parent,
                                   String treeTitle,
                                   String tableTitle,
                                   boolean enablePropertySelection,
                                   boolean showCheckboxes,
                                   boolean showMessage,
                                   DifferenceReport diffReport,
                                   int iTerminology ) {
        this(parent, treeTitle, tableTitle, enablePropertySelection, showCheckboxes, showMessage, iTerminology);
        if (diffReport != null) {
            setDifferenceReports(Collections.singletonList(diffReport));
        }
    }

    /**
     * constructor
     * 
     * @param the parent composite
     * @param theTreeTitle the title for the treeViewer Composite
     * @param theTableTitle the title for the tableViewer Composite
     * @param diffReports the List of DifferenceReports
     */
    public DifferenceReportsPanel( Composite parent,
                                   String treeTitle,
                                   String tableTitle,
                                   boolean enablePropertySelection,
                                   boolean showCheckboxes,
                                   List<DifferenceReport> diffReports ) {
        this(parent, treeTitle, tableTitle, enablePropertySelection, showCheckboxes, true);
        setDifferenceReports(diffReports);
    }

    /**
     * get the Composite's TreeViewer
     * 
     * @return the TreeViewer
     */
    public TreeViewer getTreeViewer() {
        return this.treePanel.getTreeViewer();
    }

    /**
     * get the Composite's TableViewer
     * 
     * @return the TableViewer
     */
    public TableViewer getTableViewer() {
        return this.diffDescriptorPanel.getTableViewer();
    }

    public void setDisplayOnlyPrimaryMetamodelObjects( boolean b ) {
        bDisplayOnlyPrimaryMetamodelObjects = b;
    }

    /**
     * Set the DifferenceReports for this Composite
     * 
     * @param theDifferenceReports the List of DifferenceReports
     */
    public void setDifferenceReports( List<DifferenceReport> theDifferenceReports ) {
        final DRStats stats = getStats(theDifferenceReports);
        if (stats.allChanges() > 5000) {
            txtAdds.setText(String.valueOf(stats.adds));
            txtMods.setText(String.valueOf(stats.diffs));
            txtDeletes.setText(String.valueOf(stats.deletes));

            final String msg = UiConstants.Util.getString("DifferenceReportsPanel.tooManyChanges"); //$NON-NLS-1$
            setMessage(msg, IStatus.WARNING);
        } else {
            this.treePanel.setDifferenceReports(theDifferenceReports);
            this.theDifferenceReports = theDifferenceReports;

            if (theDifferenceReports != null && theDifferenceReports.size() > 0 && theDifferenceReports.get(0) != null
                && theDifferenceReports.get(0).getMapping() != null) {

                updateStats();

                treeSetup();

            }
        }
    }

    public DifferenceReport getDifferenceReport() {
        return theDifferenceReports.get(0);
    }

    /**
     * Set the message that will appear at the top
     * 
     * @param sMessage the message indicating what things are being compared
     */
    public void setMessage( String sMessage,
                            int severity ) {
        this.sMessage = sMessage;

        if (this.slblMessage != null) {
            slblMessage.setText(sMessage, severity);
            slblMessage.update();
            // temporarily commenting this out:
            // forceRelayout();
        }
    }

    /**
     * Set the message that will appear at the top
     * 
     * @param sMessage the message indicating what things are being compared
     */
    public void setMessage( String sMessage ) {
        this.sMessage = sMessage;

        if (this.slblMessage != null) {
            slblMessage.setText(sMessage);
            slblMessage.update();
            // temporarily commenting this out:
            // forceRelayout();
        }
    }

    public void setTerminologyStyle( int terminology ) {
        this.terminology = terminology;
        this.diffDescriptorPanel.setTerminologyStyle(terminology);
        this.treePanel.setTerminology(terminology);
    }

    public void setModelName( String sModelName ) {
        this.sModelName = sModelName;
        if (this.sModelName.equals(sMessage)) {
        }
    }

    public String getModelName() {
        return sModelName;
    }

    /**
     * Initialize the Composite
     */
    private void initialize( boolean enablePropertySelection,
                             boolean showCheckboxes ) {

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        this.setLayout(gridLayout);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));

        // pnlOuter
        pnlOuter = new /* PaintAdaptable */Composite(this, SWT.NONE);
        GridLayout gridLayout01 = new GridLayout();
        gridLayout01.numColumns = 1;
        gridLayout01.verticalSpacing = 1;
        gridLayout01.marginHeight = 1;
        pnlOuter.setLayout(gridLayout01);
        GridData gd01 = new GridData(GridData.FILL_BOTH);
        pnlOuter.setLayoutData(gd01);

        // Add Header
        createHeaderPanel(pnlOuter);

        // add SashForm
        sfSashform = new SashForm(pnlOuter, SWT.NONE);
        sfSashform.setOrientation(SWT.VERTICAL);
        sfSashform.setLayoutData(new GridData(GridData.FILL_BOTH));

        // add tree to top of SashForm
        this.treePanel = new CompareTreePanel(sfSashform, this.treeTitle, showCheckboxes, bDisplayOnlyPrimaryMetamodelObjects,
                                              this.terminology);
        this.treePanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        // add table to bottom of SashForm
        Composite pnlBottomOuter = new Composite(sfSashform, SWT.NONE) {

            /**
             * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
             * @since 4.2
             */
            @Override
            public Point computeSize( int wHint,
                                      int hHint,
                                      boolean changed ) {

                return super.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
            }
        };

        GridLayout gridLayout07 = new GridLayout();
        gridLayout07.numColumns = 1;
        gridLayout07.verticalSpacing = 1;
        gridLayout07.marginHeight = 1;
        pnlBottomOuter.setLayout(gridLayout07);
        GridData gd07 = new GridData(GridData.FILL_BOTH);
        pnlBottomOuter.setLayoutData(gd07);

        this.diffDescriptorPanel = new DifferenceDescriptorPanel(pnlBottomOuter, this.tableTitle, enablePropertySelection,
                                                                 showCheckboxes, terminology);

        this.diffDescriptorPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.diffDescriptorPanel.clear();

        // Add a Selection Listener to Tree
        this.treePanel.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged( SelectionChangedEvent event ) {
                // if the selection is empty clear the label
                if (event.getSelection().isEmpty()) {
                    updateDescriptorPanel(null);
                    return;
                }
                if (event.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                    for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
                        Object object = iterator.next();
                        if (object instanceof Mapping) {
                            DifferenceDescriptor descriptor = DifferenceAnalysis.getDifferenceDescriptor((Mapping)object);
                            updateDescriptorPanel(descriptor);
                        }
                    }
                }
            }
        });

        sfSashform.setWeights(weights);
        getTreeViewer().getTree().setFocus();
    }

    /**
     * Create the header panel
     */
    private void createHeaderPanel( Composite parent ) {

        pnlHeader = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.verticalSpacing = 1;
        gridLayout.marginHeight = 1;
        pnlHeader.setLayout(gridLayout);
        GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
        pnlHeader.setLayoutData(gd1);
        gd1.widthHint = 300;

        // message is optional
        if (showMessage) {
            pnlMessage = new Composite(pnlHeader, SWT.NONE);
            GridLayout gridLayout2z = new GridLayout();
            pnlMessage.setLayout(gridLayout2z);
            GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);

            // note: this heightHint fixes weird layout problem caused by WrappingLabel
            gd3.heightHint = 55;
            pnlMessage.setLayoutData(gd3);

            // 'Message' label
            slblMessage = new StatusLabel(pnlMessage);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
            gd.horizontalSpan = 2;
            slblMessage.setLayoutData(gd);

            if (sMessage != null) {
                slblMessage.setText(sMessage);
            } else {
                slblMessage.setText(EMPTY_STRING);
            }

        } else {

            // if we do not show the message, we will show the old/new fields

            // file names
            this.pnlNames = WidgetFactory.createPanel(this.pnlHeader, SWT.NO_TRIM, GridData.FILL_HORIZONTAL, 1, 2);
            GridLayout layout = (GridLayout)this.pnlNames.getLayout();
            layout.makeColumnsEqualWidth = true;

            // 'Old' label
            Composite pnlLabel = WidgetFactory.createPanel(this.pnlNames, SWT.NO_TRIM, GridData.FILL_HORIZONTAL, 1, 4);
            layout = (GridLayout)pnlLabel.getLayout();
            layout.horizontalSpacing = 0;
            this.oldLabel = WidgetFactory.createLabel(pnlLabel,
                                                      GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL,
                                                      getOldFileString());
            WidgetFactory.createLabel(pnlLabel, FILE_IMG_PFX).setHorizontalMargin(0);
            this.oldImgLabel = WidgetFactory.createLabel(pnlLabel, MappingLabelDecorator.OLD_IMG);
            this.oldImgLabel.setHorizontalMargin(0);
            WidgetFactory.createLabel(pnlLabel, FILE_IMG_SFX).setLeftMargin(0);

            // 'Old Object name' label
            this.oldFld = WidgetFactory.createLabel(this.pnlNames, GridData.FILL_HORIZONTAL, this.sOldObjectName);

            // 'New' label
            pnlLabel = WidgetFactory.createPanel(this.pnlNames, SWT.NO_TRIM, GridData.FILL_HORIZONTAL, 1, 4);
            layout = (GridLayout)pnlLabel.getLayout();
            layout.horizontalSpacing = 0;
            this.newLabel = WidgetFactory.createLabel(pnlLabel,
                                                      GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL,
                                                      getNewFileString());
            WidgetFactory.createLabel(pnlLabel, FILE_IMG_PFX).setHorizontalMargin(0);
            this.newImgLabel = WidgetFactory.createLabel(pnlLabel, MappingLabelDecorator.NEW_IMG);
            this.newImgLabel.setHorizontalMargin(0);
            WidgetFactory.createLabel(pnlLabel, FILE_IMG_SFX).setLeftMargin(0);

            // 'New Object name' label
            this.newFld = WidgetFactory.createLabel(this.pnlNames, GridData.FILL_HORIZONTAL, this.sNewObjectName);
        }

        // statistics
        pnlCounts = new Composite(pnlHeader, SWT.NONE);
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 21;
        gridLayout2.verticalSpacing = 1;
        gridLayout2.horizontalSpacing = 0;
        gridLayout2.marginHeight = 1;
        pnlCounts.setLayout(gridLayout2);
        GridData gridData7 = new GridData();
        gridData7.horizontalAlignment = GridData.CENTER;
        gridData7.verticalAlignment = GridData.CENTER;
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.grabExcessVerticalSpace = false;

        pnlCounts.setLayoutData(gridData7);

        // 'Deletes' label
        WidgetFactory.createLabel(this.pnlCounts, UiConstants.Util.getString("DifferenceReportsPanel.deletes.text")); //$NON-NLS-1$
        this.oldOnlyLabel = WidgetFactory.createLabel(this.pnlCounts, getOldFileString());
        WidgetFactory.createLabel(this.pnlCounts, FILE_IMG_PFX).setHorizontalMargin(0);
        this.oldOnlyImgLabel = WidgetFactory.createLabel(this.pnlCounts, MappingLabelDecorator.OLD_IMG);
        this.oldOnlyImgLabel.setHorizontalMargin(0);
        WidgetFactory.createLabel(this.pnlCounts, FILE_IMG_SFX).setLeftMargin(0);

        // 'Deletes' textfield
        txtDeletes = WidgetFactory.createTextField(pnlCounts, SWT.NONE, EMPTY_STRING);
        txtDeletes.setEditable(false);
        txtDeletes.setEnabled(true);

        // 'Mods' label
        Label lbl = WidgetFactory.createLabel(this.pnlCounts, UiConstants.Util.getString("DifferenceReportsPanel.mods.text")); //$NON-NLS-1$
        ((GridData)lbl.getLayoutData()).horizontalIndent = 20;
        WidgetFactory.createLabel(this.pnlCounts, FILE_IMG_PFX).setHorizontalMargin(0);
        WidgetFactory.createLabel(this.pnlCounts, MappingLabelDecorator.CHG_IMG).setHorizontalMargin(0);
        WidgetFactory.createLabel(this.pnlCounts, FILE_IMG_SFX).setLeftMargin(0);

        // 'Mods' textfield
        txtMods = WidgetFactory.createTextField(pnlCounts, SWT.NONE, EMPTY_STRING);
        txtMods.setEditable(false);
        txtMods.setEnabled(true);

        // 'Adds' label
        lbl = WidgetFactory.createLabel(this.pnlCounts, UiConstants.Util.getString("DifferenceReportsPanel.adds.text")); //$NON-NLS-1$
        ((GridData)lbl.getLayoutData()).horizontalIndent = 20;
        this.newOnlyLabel = WidgetFactory.createLabel(this.pnlCounts, getNewFileString());
        WidgetFactory.createLabel(this.pnlCounts, FILE_IMG_PFX).setHorizontalMargin(0);
        this.newOnlyImgLabel = WidgetFactory.createLabel(this.pnlCounts, MappingLabelDecorator.NEW_IMG);
        this.newOnlyImgLabel.setHorizontalMargin(0);
        WidgetFactory.createLabel(this.pnlCounts, FILE_IMG_SFX).setLeftMargin(0);

        // 'Adds' textfield
        txtAdds = WidgetFactory.createTextField(pnlCounts, SWT.NONE, EMPTY_STRING);
        txtAdds.setEditable(false);
        txtAdds.setEnabled(true);
    }

    private void refreshNamesPanel() {

        if (this.sOldObjectName != null) {
            if (!this.sOldObjectName.equals(EMPTY_STRING)) {
                String oldLabelText = getOldFileString();
                String newLabelText = getNewFileString();
                if (!this.showMessage) {
                    this.oldLabel.setText(oldLabelText);
                    this.oldImgLabel.setImage(oldLabelText == OLD_FILE_TEXT ? MappingLabelDecorator.OLD_IMG : MappingLabelDecorator.FIRST_IMG);
                    this.oldFld.setText(sOldObjectName);
                    this.newLabel.setText(newLabelText);
                    this.newImgLabel.setImage(newLabelText == NEW_FILE_TEXT ? MappingLabelDecorator.NEW_IMG : MappingLabelDecorator.SECOND_IMG);
                    this.newFld.setText(sNewObjectName);
                }
                this.oldOnlyLabel.setText(oldLabelText);
                this.oldOnlyLabel.layout();
                this.oldOnlyImgLabel.setImage(oldLabelText == OLD_FILE_TEXT ? MappingLabelDecorator.OLD_IMG : MappingLabelDecorator.FIRST_IMG);
                this.newOnlyLabel.setText(newLabelText);
                this.newOnlyImgLabel.setImage(newLabelText == NEW_FILE_TEXT ? MappingLabelDecorator.NEW_IMG : MappingLabelDecorator.SECOND_IMG);
            } else {
                if (!this.showMessage) {
                    this.oldLabel.setText(EMPTY_STRING);
                    this.oldImgLabel.setImage(null);
                    this.oldFld.setText(EMPTY_STRING);
                    this.newLabel.setText(EMPTY_STRING);
                    this.newImgLabel.setImage(null);
                    this.newFld.setText(EMPTY_STRING);
                }
                this.oldOnlyLabel.setText(EMPTY_STRING);
                this.oldOnlyImgLabel.setImage(null);
                this.newOnlyLabel.setText(EMPTY_STRING);
                this.newOnlyImgLabel.setImage(null);
            }
        }
    }

    private String getOldFileString() {
        String sOldFileText = OLD_FILE_TEXT;
        if (terminology == USE_OLD_NEW_TERMINOLOGY) {
            sOldFileText = OLD_FILE_TEXT;
        } else if (terminology == USE_FIRST_SECOND_TERMINOLOGY) {
            sOldFileText = FIRST_FILE_TEXT;
        }

        return sOldFileText;
    }

    private String getNewFileString() {
        String sNewFileText = NEW_FILE_TEXT;
        if (terminology == USE_OLD_NEW_TERMINOLOGY) {
            sNewFileText = NEW_FILE_TEXT;
        } else if (terminology == USE_FIRST_SECOND_TERMINOLOGY) {
            sNewFileText = SECOND_FILE_TEXT;
        }

        return sNewFileText;
    }

    public void setObjectNames( String sOldName,
                                String sNewName ) {
        setOldObjectName(sOldName);
        setNewObjectName(sNewName);
        refreshNamesPanel();
    }

    private void setOldObjectName( String sName ) {
        sOldObjectName = sName;
    }

    private void setNewObjectName( String sName ) {
        sNewObjectName = sName;
    }

    private void treeSetup() {

        // expand all nodes
        getTreeViewer().getTree().setVisible(false);

        // scroll back to the top
        ITreeContentProvider itcp = (ITreeContentProvider)getTreeViewer().getContentProvider();

        Object[] roots = itcp.getElements(getTreeViewer().getInput());

        if (theDifferenceReports.size() == 1) {
            getTreeViewer().reveal(roots[0]);
            getTreeViewer().expandToLevel(2);
            // System.out.println("[DifferenceReportsPanel.treesetup] Expanding to level 2; roots: " + roots ); //$NON-NLS-1$
            // System.out.println("[DifferenceReportsPanel.treesetup] Expanding to level 2; roots length: " + roots.length );
            // //$NON-NLS-1$
        } else if (theDifferenceReports.size() > 1) {
            getTreeViewer().reveal(roots[0]);
            getTreeViewer().expandToLevel(3);
            // System.out.println("[DifferenceReportsPanel.treesetup] Expanding to level 3; roots: " + roots ); //$NON-NLS-1$
            // System.out.println("[DifferenceReportsPanel.treesetup] Expanding to level 3; roots length: " + roots.length );
            // //$NON-NLS-1$
        }

        getTreeViewer().getTree().setVisible(true);
        treePanel.selectFirstDiff();

    }

    private void updateStats() {

        if (theDifferenceReports != null && theDifferenceReports.get(0) != null) {

            treePanel.generateStats();
            treePanel.initTreeSelections();

            // Since there are various reasons why we might not display all of the nodes
            // in the tree, we will always use the counts that have been recalculated
            // by the CompareTreePanel, rather than the original counts from the
            // DifferenceReport.
            txtAdds.setText(String.valueOf(treePanel.getAdditionCount()));
            txtMods.setText(String.valueOf(treePanel.getChangeCount()));
            txtDeletes.setText(String.valueOf(treePanel.getDeletionCount()));

            // since the values in the textfields have changed relayout the parent
            this.pnlHeader.layout(new Control[] {this.txtAdds, this.txtDeletes, this.txtMods});
        }

        if (sMessage != null && slblMessage != null) {
            slblMessage.setText(sMessage);
        }
    }

    /**
     * Update the DifferenceDescriptor Composite
     * 
     * @param descriptor the DifferenceDescriptor
     */
    void updateDescriptorPanel( DifferenceDescriptor descriptor ) {
        if (descriptor != null) {
            this.diffDescriptorPanel.setDescriptor(descriptor);
        } else {
            this.diffDescriptorPanel.clear();
        }
    }

    public void forceRelayout() {
        // System.out.println("\n\n[DifferenceReportsPanel.forceRelayout] TOP, we are: " + System.identityHashCode( this ) );

        // test
        getDialogAncestor();
        // end test

        setRedraw(true);

        int iCount = 0;
        Composite pnlParent = getParent();
        Composite pnlSecondLastParent = null;

        // try to find a SashForm ancestor
        while (true) {

            // quit when you run out of parents or do too many loops
            if (pnlParent == null || iCount > 100) {
                break;
            }

            // quit when you find the SashForm
            if ( /* pnlParent instanceof SashForm || */pnlParent instanceof Shell) {
                break;
            }
            pnlSecondLastParent = pnlParent;

            // get the next parent
            pnlParent = pnlParent.getParent();

            iCount++;
        }

        // if you found a SashForm ancestor, do a pack:
        if (pnlParent != null) {

            if (pnlParent instanceof SashForm) {

                // System.out.println("[DifferenceReportsPanel.forceRelayout] SashForm found...About to call pack on: " +
                // System.identityHashCode( pnlParent ) + " ..." + pnlParent.getClass().getName() );

                if (pnlSecondLastParent != null && pnlSecondLastParent instanceof ViewForm) {
                    pnlSecondLastParent.pack();
                } else {
                    pnlParent.pack();
                }

                wiggle((SashForm)pnlParent);
            } else if (pnlParent instanceof Shell) {
                // System.out.println("[DifferenceReportsPanel.forceRelayout] Shell found...About to call pack on: " +
                // System.identityHashCode( pnlParent ) + " ..." + pnlParent.getClass().getName() );
                ((Shell)pnlParent).pack();
            }
        }
    }

    private Dialog getDialogAncestor() {
        return null;
    }

    private DRStats getStats( final List<DifferenceReport> differenceReports ) {
        final DRStats result = new DRStats();
        if (differenceReports == null) {
            return result;
        }

        final Iterator<DifferenceReport> diffs = differenceReports.iterator();
        while (diffs.hasNext()) {
            final DifferenceReport dr = diffs.next();
            result.adds += dr.getTotalAdditions();
            result.deletes += dr.getTotalDeletions();
            result.diffs += dr.getTotalChanges();
        } // while

        return result;
    }

    private void wiggle( SashForm sf ) {
        int iWiggleFactor = 1;

        int[] weights = sf.getWeights();
        int iCurrentFirstWeight = weights[0];
        weights[0] = iCurrentFirstWeight + iWiggleFactor;
        sf.setWeights(weights);

        weights[0] = iCurrentFirstWeight - iWiggleFactor;
        sf.setWeights(weights);
    }

    class DRStats {
        int diffs = 0;
        int adds = 0;
        int deletes = 0;

        int allChanges() {
            return (diffs + adds + deletes);
        }
    }

    /**
     * A ScrolledComposite that maintains the width of it's parent, but can grow (and scroll) vertically.
     * 
     * @since 4.2
     */
    class PaintAdaptableComposite extends ScrolledComposite implements PaintListener {

        private boolean bCompletedInitialWiggle = false;

        public PaintAdaptableComposite( Composite parent,
                                        int style ) {
            super(parent, style);
            parent.addPaintListener(this);
        }

        public void paintControl( PaintEvent e ) {
            if (!bCompletedInitialWiggle) {
                // or do this dialog-level wiggle:
                int iFudgeFactor = 3;
                Rectangle rect = getShell().getBounds();

                // wiggle to the right
                rect.x += iFudgeFactor;
                getShell().setBounds(rect);
                getShell().pack();

                // wiggle back to the left
                rect.x -= iFudgeFactor;
                getShell().setBounds(rect);
                getShell().pack();

                getShell().layout(true);
                bCompletedInitialWiggle = true;
            }

        }

        @Override
        public Point computeSize( int wHint,
                                  int hHint,
                                  boolean changed ) {
            return super.computeSize(wHint, hHint, changed);
        }

        @Override
        public Point computeSize( int wHint,
                                  int hHint ) {
            return super.computeSize(wHint, hHint);
        }

        @Override
        public void dispose() {
            super.dispose();
            getParent().removePaintListener(this);
        }

    }
}
