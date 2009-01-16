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
package com.metamatrix.modeler.diagram.ui.printing;

import java.util.ArrayList;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramPrintPreferencePage;
import com.metamatrix.ui.graphics.GlobalUiColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.Spinner;

public class DiagramPrintSummaryDialog extends Dialog implements DiagramUiConstants {

    // TEST STUFF
    Control pnlSummary;
    Control pnlPageSetup;
    Color color1 = GlobalUiColorManager.getColor(new RGB(0, 0, 255));
    Color color2 = GlobalUiColorManager.getColor(new RGB(0, 255, 0));
    Color color3 = GlobalUiColorManager.getColor(new RGB(255, 0, 0));
    // END TEST STUFF DEFAULT_LABEL_WIDTH
    private static final int DEFAULT_LABEL_WIDTH = 235;

    private static final String LABEL_INIT_STRING = "............................................................................................................"; //$NON-NLS-1$
    private static final String TITLE = Util.getString("DiagramPrintSummaryDialog.title"); //$NON-NLS-1$
    // private static final String PRINT_BUTTON_TEXT
    //        = Util.getString("DiagramPrintSummaryDialog.printButton.text"); //$NON-NLS-1$
    private static final String APPLY_BUTTON_TEXT = Util.getString("DiagramPrintSummaryDialog.applyButton.text"); //$NON-NLS-1$
    private static final String RESTORE_DEFAULTS_BUTTON_TEXT = Util.getString("DiagramPrintSummaryDialog.restoreDefaultsButton.text"); //$NON-NLS-1$

    private static final String SUMMARY_TAB_TITLE = Util.getString("DiagramPrintSummaryDialog.summaryTabTitle.text"); //$NON-NLS-1$
    private static final String SUMMARY_TAB_TOOLTIP = Util.getString("DiagramPrintSummaryDialog.summaryTabTooltip.text"); //$NON-NLS-1$
    private static final String PAGE_SETUP_TAB_TITLE = Util.getString("DiagramPrintSummaryDialog.pageSetupTabTitle.text"); //$NON-NLS-1$
    private static final String PAGE_SETUP_TAB_TOOLTIP = Util.getString("DiagramPrintSummaryDialog.pageSetupTabTooltip.text"); //$NON-NLS-1$

    private TabFolder tabFolder;
    private TabItem tabSummary;
    private TabItem tabPageSetup;
    private SelectionAdapter tabListener;

    // ================================================
    // 1. Diagram
    // ================================================
    private static final String DIAGRAM_TITLE = Util.getString("DiagramPrintSummaryDialog.diagram.title"); //$NON-NLS-1$
    private static final String PROJECT_NAME = Util.getString("DiagramPrintSummaryDialog.projectName.text"); //$NON-NLS-1$
    private static final String MODEL_NAME = Util.getString("DiagramPrintSummaryDialog.modelName.text"); //$NON-NLS-1$
    private static final String DIAGRAM_TYPE = Util.getString("DiagramPrintSummaryDialog.diagramType.text"); //$NON-NLS-1$

    private CLabel lblProjectName;
    private CLabel lblModelName;
    private CLabel lblDiagramType;

    // ================================================
    // 2. Page Setup
    // ================================================
    private static final String PAGE_SETUP_TITLE = Util.getString("DiagramPrintSummaryDialog.pageSetup.title"); //$NON-NLS-1$
    private static final String ORIENTATION = Util.getString("DiagramPrintSummaryDialog.orientation.text"); //$NON-NLS-1$
    private static final String PORTRAIT = Util.getString("DiagramPrintSummaryDialog.portrait.text"); //$NON-NLS-1$
    private static final String LANDSCAPE = Util.getString("DiagramPrintSummaryDialog.landscape.text"); //$NON-NLS-1$

    private static final String SCALING = Util.getString("DiagramPrintSummaryDialog.scaling.text"); //$NON-NLS-1$
    private static final String ADJUST_TO = Util.getString("DiagramPrintSummaryDialog.adjustTo.text"); //$NON-NLS-1$
    private static final String PERCENT_NORMAL_SIZE = Util.getString("DiagramPrintSummaryDialog.percentNormalSize.text"); //$NON-NLS-1$
    private static final String FIT_TO_ONE_PAGE = Util.getString("DiagramPrintSummaryDialog.fitToOnePage.text"); //$NON-NLS-1$
    private static final String FIT_TO_ONE_PAGE_HIGH = Util.getString("DiagramPrintSummaryDialog.fitToOnePageHigh.text"); //$NON-NLS-1$
    private static final String FIT_TO_ONE_PAGE_WIDE = Util.getString("DiagramPrintSummaryDialog.fitToOnePageWide.text"); //$NON-NLS-1$

    // Margins
    // private static final String MARGINS_TITLE
    //        = Util.getString( "DiagramPrintSummaryDialog.margins.title" );  //$NON-NLS-1$    
    private static final String MARGINS = Util.getString("DiagramPrintSummaryDialog.margins.text"); //$NON-NLS-1$    
    private static final String TOP = Util.getString("DiagramPrintSummaryDialog.top.text"); //$NON-NLS-1$
    private static final String RIGHT = Util.getString("DiagramPrintSummaryDialog.right.text"); //$NON-NLS-1$
    private static final String BOTTOM = Util.getString("DiagramPrintSummaryDialog.bottom.text"); //$NON-NLS-1$
    private static final String LEFT = Util.getString("DiagramPrintSummaryDialog.left.text"); //$NON-NLS-1$

    private static final String PAGE_ORDER = Util.getString("DiagramPrintSummaryDialog.pageOrder.text"); //$NON-NLS-1$
    private static final String OVER_THEN_DOWN = Util.getString("DiagramPrintSummaryDialog.overThenDown.text"); //$NON-NLS-1$
    private static final String DOWN_THEN_OVER = Util.getString("DiagramPrintSummaryDialog.downThenOver.text"); //$NON-NLS-1$       

    private CLabel lblOrientation;
    private CLabel lblScaling;
    private CLabel lblMargins;
    private CLabel lblPageOrder;

    // ================================================
    // 3. Page Range
    // ================================================
    private static final String PAGE_RANGE_TITLE = Util.getString("DiagramPrintSummaryDialog.pageRange.title"); //$NON-NLS-1$
    private static final String ALL_PAGES = Util.getString("DiagramPrintSummaryDialog.allPages.text"); //$NON-NLS-1$
    private static final String RANGE_FROM = Util.getString("DiagramPrintSummaryDialog.rangeFrom.text"); //$NON-NLS-1$
    private static final String RANGE_TO = Util.getString("DiagramPrintSummaryDialog.rangeTo.text"); //$NON-NLS-1$

    private Button radioAllPages;
    Button radioPagesInRange;
    Spinner spinRangeFrom;
    Spinner spinRangeTo;

    // ================================================
    // 4. Total Pages
    // ================================================
    private static final String TOTAL_PAGES_TITLE = Util.getString("DiagramPrintSummaryDialog.totalPages.title"); //$NON-NLS-1$
    private static final String PAGES_TO_PRINT = Util.getString("DiagramPrintSummaryDialog.pagesToPrint.text"); //$NON-NLS-1$        

    CLabel lblTotalPages;

    private Button btnApply;
    private Button btnRestoreDefaults;
    private Button btnOk;

    DiagramPrintPreferencePage preferencePage;
    private PrintSettings psSettings;
    private DiagramPrintingAnalyzer analyzer;

    /**
     * Construct an instance of DiagramPrintSummaryDialog.
     * 
     * @param shell
     */
    public DiagramPrintSummaryDialog( Shell shell,
                                      PrintSettings psSettings,
                                      DiagramPrintingAnalyzer analyzer ) {
        super(shell, TITLE);
        this.psSettings = psSettings;
        this.analyzer = analyzer;
        init();
    }

    public PrintSettings getSettings() {
        return psSettings;
    }

    private void init() {

        this.setTitle(TITLE);
        int shellStyle = getShellStyle();
        setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);

        // setStatusLineAboveButtons( true );
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layComp = new GridLayout();
        composite.setLayout(layComp);
        GridData gdComp = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gdComp);

        tabFolder = new TabFolder(parent, SWT.NONE);
        GridLayout layTabFolder = new GridLayout();
        tabFolder.setLayout(layTabFolder);
        GridData gdTabFolder = new GridData(GridData.FILL_BOTH);
        tabFolder.setLayoutData(gdTabFolder);

        // ===============================
        // Summary Tab
        // ===============================
        tabSummary = new TabItem(tabFolder, SWT.NONE);
        pnlSummary = createSummaryPanel(tabFolder);

        tabSummary.setControl(pnlSummary);
        tabSummary.setText(SUMMARY_TAB_TITLE);
        tabSummary.setToolTipText(SUMMARY_TAB_TOOLTIP);

        // ===============================
        // Page Setup Tab --- simple test version
        // ===============================
        tabPageSetup = new TabItem(tabFolder, SWT.NONE);

        Composite pnlTest = new Composite(tabFolder, SWT.NONE);
        // LayoutDebugger.debugLayout( composite );
        GridLayout layComp2 = new GridLayout();
        pnlTest.setLayout(layComp2);
        GridData gdComp2 = new GridData(GridData.FILL_BOTH);
        pnlTest.setLayoutData(gdComp2);

        // CLabel lblTemp = WidgetFactory.createLabel( pnlTest, PROJECT_NAME );
        pnlPageSetup = createPreferencePage(pnlTest);

        tabPageSetup.setControl(pnlTest);

        // // ===============================
        // // Page Setup Tab
        // // ===============================
        tabPageSetup.setText(PAGE_SETUP_TAB_TITLE);
        tabPageSetup.setToolTipText(PAGE_SETUP_TAB_TOOLTIP);
        //        
        initModifiableSummaryValuesOnSummaryPanel();
        updateValuesOnSummaryPanel();

        addListeners();

        composite.pack();
        tabSummary.getControl().pack();
        tabPageSetup.getControl().pack();

        return composite;
    }

    private Composite createSummaryPanel( Composite theParent ) {
        //        System.out.println("[DiagramPrintSummaryDialog.createSummaryPanel] TOP" ); //$NON-NLS-1$

        Composite pnlOuter = new Composite(theParent, SWT.NONE) {
            @Override
            public Point computeSize( int wHint,
                                      int hHint,
                                      boolean changed ) {
                return new Point(350, 250);
            }
        };
        // Composite pnlOuter = theParent;
        GridLayout layOuter = new GridLayout();
        pnlOuter.setLayout(layOuter);
        GridData gdOuter = new GridData(GridData.FILL_BOTH);
        pnlOuter.setLayoutData(gdOuter);

        // ================================================
        // 1. Diagram
        // ================================================
        Group grpDiagram = new Group(pnlOuter, SWT.NONE);
        grpDiagram.setText(DIAGRAM_TITLE);

        GridData gdDiagram = new GridData(GridData.FILL_HORIZONTAL);
        grpDiagram.setLayoutData(gdDiagram);

        GridLayout layDiagram = new GridLayout();
        grpDiagram.setLayout(layDiagram);
        layDiagram.numColumns = 2;

        CLabel lblTemp = WidgetFactory.createLabel(grpDiagram, PROJECT_NAME);
        lblTemp.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        lblProjectName = WidgetFactory.createLabel(grpDiagram);
        lblProjectName.setLayoutData(createDefaultDataLabelGridData());

        lblTemp = WidgetFactory.createLabel(grpDiagram, MODEL_NAME);
        lblTemp.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        lblModelName = WidgetFactory.createLabel(grpDiagram);
        lblModelName.setLayoutData(createDefaultDataLabelGridData());

        lblTemp = WidgetFactory.createLabel(grpDiagram, DIAGRAM_TYPE);
        lblTemp.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        lblDiagramType = WidgetFactory.createLabel(grpDiagram);
        lblDiagramType.setLayoutData(createDefaultDataLabelGridData());

        // ================================================
        // 2. Page Setup
        // ================================================
        Group grpPageSetup = new Group(pnlOuter, SWT.NONE);
        grpPageSetup.setText(PAGE_SETUP_TITLE);

        GridData gdPageSetup = new GridData(GridData.FILL_HORIZONTAL);

        grpPageSetup.setLayoutData(gdPageSetup);

        GridLayout layPageSetup = new GridLayout();
        grpPageSetup.setLayout(layPageSetup);
        layPageSetup.numColumns = 2;

        lblTemp = WidgetFactory.createLabel(grpPageSetup, ORIENTATION);
        GridData gdTemp = new GridData(SWT.END, SWT.CENTER, false, false);
        lblTemp.setLayoutData(gdTemp);

        lblOrientation = WidgetFactory.createLabel(grpPageSetup, LABEL_INIT_STRING);
        lblOrientation.setLayoutData(createDefaultDataLabelGridData());

        lblTemp = WidgetFactory.createLabel(grpPageSetup, SCALING);
        gdTemp = new GridData(SWT.END, SWT.CENTER, false, false);
        lblTemp.setLayoutData(gdTemp);

        lblScaling = WidgetFactory.createLabel(grpPageSetup, LABEL_INIT_STRING);
        lblScaling.setLayoutData(createDefaultDataLabelGridData());

        lblTemp = WidgetFactory.createLabel(grpPageSetup, MARGINS);
        gdTemp = new GridData(SWT.END, SWT.CENTER, false, false);
        lblTemp.setLayoutData(gdTemp);

        lblMargins = WidgetFactory.createLabel(grpPageSetup, LABEL_INIT_STRING);
        lblMargins.setLayoutData(createDefaultDataLabelGridData());

        lblTemp = WidgetFactory.createLabel(grpPageSetup, PAGE_ORDER);
        gdTemp = new GridData(SWT.END, SWT.CENTER, false, false);
        lblTemp.setLayoutData(gdTemp);

        lblPageOrder = WidgetFactory.createLabel(grpPageSetup, LABEL_INIT_STRING);
        lblPageOrder.setLayoutData(createDefaultDataLabelGridData());

        // ================================================
        // 3. Page Range
        // ================================================
        Group grpPageRange = new Group(pnlOuter, SWT.NONE);
        grpPageRange.setText(PAGE_RANGE_TITLE);

        GridData gdPageRange = new GridData(GridData.FILL_HORIZONTAL);
        grpPageRange.setLayoutData(gdPageRange);

        GridLayout layPageRange = new GridLayout();
        grpPageRange.setLayout(layPageRange);
        layPageRange.numColumns = 4;

        radioAllPages = WidgetFactory.createRadioButton(grpPageRange, ALL_PAGES);
        GridData gdRadioAllPages = new GridData(GridData.FILL_HORIZONTAL);
        gdRadioAllPages.horizontalSpan = 4;
        radioAllPages.setLayoutData(gdRadioAllPages);

        radioPagesInRange = WidgetFactory.createRadioButton(grpPageRange, RANGE_FROM);

        int iPossiblePageCount = getTotalPossiblePageCount();
        spinRangeFrom = new Spinner(grpPageRange, getPageSelectPossibleValues(1, iPossiblePageCount));
        spinRangeFrom.setWrap(false);

        WidgetFactory.createLabel(grpPageRange, RANGE_TO);

        spinRangeTo = new Spinner(grpPageRange, getPageSelectPossibleValues(1, iPossiblePageCount));
        spinRangeTo.setWrap(false);

        // ================================================
        // 4. Total Pages
        // ================================================
        Group grpTotalPages = new Group(pnlOuter, SWT.NONE);
        grpTotalPages.setText(TOTAL_PAGES_TITLE);

        GridData gdTotalPages = new GridData(GridData.FILL_HORIZONTAL);
        grpTotalPages.setLayoutData(gdTotalPages);

        GridLayout layTotalPages = new GridLayout();
        grpTotalPages.setLayout(layTotalPages);
        layTotalPages.numColumns = 2;

        lblTemp = WidgetFactory.createLabel(grpTotalPages, PAGES_TO_PRINT);
        lblTemp.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        lblTotalPages = WidgetFactory.createLabel(grpTotalPages, LABEL_INIT_STRING);
        lblTotalPages.setLayoutData(createDefaultDataLabelGridData());

        return pnlOuter;
    }

    private void addListeners() {
        // Create listener to populate tabs upon selection. Added to tab folder only after tab selected.
        tabListener = new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                tabSelected();
            }
        };

        tabFolder.addSelectionListener(tabListener);

        radioAllPages.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                lblTotalPages.setText(String.valueOf(getTotalActualPageCount()));
                spinRangeFrom.setEnabled(radioPagesInRange.getSelection());
                spinRangeTo.setEnabled(radioPagesInRange.getSelection());
                applyModifiableSummaryValuesTo();
            }

        });

        radioPagesInRange.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                spinRangeFrom.setEnabled(radioPagesInRange.getSelection());
                spinRangeTo.setEnabled(radioPagesInRange.getSelection());
                applyModifiableSummaryValuesTo();
                lblTotalPages.setText(String.valueOf(getTotalActualPageCount()));
            }
        });

        spinRangeTo.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {

                // update the settings
                applyModifiableSummaryValuesTo();

                lblTotalPages.setText(String.valueOf(getTotalActualPageCount()));
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }
        });

        spinRangeFrom.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                // adjust the 'To' spinner to have the current value of
                // 'From' as its min value
                // jhTODO: first capture the present value, then call setPossibleValues,
                // then determine if the old value will fit in new range
                // if yes, reset the value to the old one
                // if no, reset the value to the new Min

                // capture current 'To'
                int iCurrentToValue = getToPage();

                ArrayList arylVals = getPageSelectPossibleValues(getFromPage(), getTotalPossiblePageCount());
                if (arylVals != null && arylVals.size() > 0) {

                    spinRangeTo.setPossibleValues(arylVals);

                    // after adjusting 'To' possible values, capture 'To' bounds
                    int iToLower = ((Integer)spinRangeTo.getLowerBound()).intValue();
                    int iToUpper = ((Integer)spinRangeTo.getUpperBound()).intValue();

                    // if the former 'To' value no longer fits in the new 'To' range, reset it to
                    // 'lower bound of 'To':
                    if (iCurrentToValue >= iToLower && iCurrentToValue <= iToUpper) {
                        // set it to current, hoping this will make it display
                        spinRangeTo.setValue(new Integer(iCurrentToValue));
                    } else {
                        // set 'To' value to be new lower bound of 'To':
                        spinRangeTo.setValue(new Integer(iToLower));
                    }
                } else {
                    // System.out.println("[DiagramPrintSummaryDialog$SelectionListener] NO POSSIBLE VALUES ARRAY!!! " );
                }

                // update the settings
                applyModifiableSummaryValuesTo();

                // after all of that, update total actual page count
                lblTotalPages.setText(String.valueOf(getTotalActualPageCount()));
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }
        });
    }

    void applyModifiableSummaryValuesTo() {
        if (radioAllPages.getSelection() == true) {
            psSettings.setSetting(PrintSettings.SCOPE, new Integer(PrintSettings.SCOPE_ALL_PAGES));
        }

        if (radioPagesInRange.getSelection() == true) {
            psSettings.setSetting(PrintSettings.SCOPE, new Integer(PrintSettings.SCOPE_PAGE_RANGE));

            Integer IFrom = (Integer)spinRangeFrom.getValue();
            psSettings.setSetting(PrintSettings.START_PAGE, IFrom);

            Integer ITo = (Integer)spinRangeTo.getValue();
            psSettings.setSetting(PrintSettings.END_PAGE, ITo);

        }
    }

    void tabSelected() {
        final TabItem[] tab = tabFolder.getSelection();

        if (tab[0] == this.tabSummary) {
            if (preferencePage != null) {
                preferencePage.performOk();
            }

            // AFTER the performOK update, calc the new possible page count

            analyzer.reAnalyze();

            int iPossiblePageCount = getTotalPossiblePageCount();

            // make sure we update the From and To prefs *first*
            psSettings.setSetting(PrintSettings.START_PAGE, new Integer(1));
            psSettings.setSetting(PrintSettings.END_PAGE, new Integer(iPossiblePageCount));

            updateValuesOnSummaryPanel();
            spinRangeFrom.setValue(new Integer(1));
            spinRangeTo.setValue(new Integer(iPossiblePageCount));

            // update the settings
            applyModifiableSummaryValuesTo();

            // after all of that, update total actual page count
            lblTotalPages.setText(String.valueOf(iPossiblePageCount));

        }

    }

    private GridData createDefaultDataLabelGridData() {
        GridData gdTemp = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
        gdTemp.widthHint = DEFAULT_LABEL_WIDTH;
        return gdTemp;
    }

    ArrayList getPageSelectPossibleValues( int iMin,
                                           int iMax ) {

        ArrayList arylPctPossibleValues = new ArrayList();
        for (int i = iMin; i <= iMax; i += 1) {

            arylPctPossibleValues.add(new Integer(i));
        }

        return arylPctPossibleValues;
    }

    int getTotalActualPageCount() {
        // 
        int iTotalPages = 0;

        if (radioAllPages.getSelection() == true) {
            iTotalPages = getTotalPossiblePageCount();
        } else {
            int iFromPage = ((Integer)spinRangeFrom.getValue()).intValue();
            int iToPage = ((Integer)spinRangeTo.getValue()).intValue();

            iTotalPages = 1 + iToPage - iFromPage;
        }

        return iTotalPages;
    }

    int getTotalPossiblePageCount() {
        return analyzer.getNumberOfPages();
    }

    int getFromPage() {
        Integer IValue = (Integer)spinRangeFrom.getValue();

        return IValue.intValue();
    }

    int getToPage() {
        Integer IValue = (Integer)spinRangeTo.getValue();

        return IValue.intValue();
    }

    private Control createPreferencePage( Composite parent ) {

        // jhTODO: fix this so it does make the dialog freakishly TALL!!!!!
        Composite pnlOuter = new Composite(parent, SWT.NONE) {
            @Override
            public org.eclipse.swt.graphics.Point computeSize( int wHint,
                                                               int hHint,
                                                               boolean changed ) {
                return new Point(290, 250);
            }
        };
        GridLayout layOuter = new GridLayout();

        layOuter.numColumns = 1;
        pnlOuter.setLayout(layOuter);
        GridData gdOuter = new GridData(GridData.FILL_BOTH);
        pnlOuter.setLayoutData(gdOuter);

        preferencePage = new DiagramPrintPreferencePage();

        preferencePage.createContents(pnlOuter);

        Composite pnlButtons = new Composite(pnlOuter, SWT.NONE);
        GridData gdButtons = new GridData(GridData.FILL_HORIZONTAL);
        pnlButtons.setLayoutData(gdButtons);

        GridLayout layButtons = new GridLayout();
        layButtons.numColumns = 3;
        pnlButtons.setLayout(layButtons);

        btnApply = WidgetFactory.createButton(pnlButtons, APPLY_BUTTON_TEXT);

        btnApply.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                preferencePage.performOk();
            }
        });

        btnRestoreDefaults = WidgetFactory.createButton(pnlButtons, RESTORE_DEFAULTS_BUTTON_TEXT);

        btnRestoreDefaults.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                preferencePage.performDefaults();
            }
        });

        return pnlOuter;
    }

    /**
     * Method declared on Dialog.
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        // we'll create our own Ok and Cancel so we can use OK to
        // update the Preferences
        btnOk = createButton(parent, 10991, IDialogConstants.OK_LABEL, true);

        btnOk.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                buttonPressed();
            }
        });

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);

    }

    void buttonPressed() {
        preferencePage.performOk();

        okPressed();
    }

    public Button getOkButton() {
        return getButton(IDialogConstants.OK_ID);
    }

    private void updateValuesOnSummaryPanel() {
        // ================================================
        // 1. Diagram
        // ================================================
        DiagramEditor editor = getActiveDiagramEditor();
        if (editor != null) {
            String sProject = editor.getCurrentModelResource().getModelProject().getPath().toString();
            lblProjectName.setText(sProject);
            lblModelName.setText(editor.getCurrentModelResource().getItemName());
            lblDiagramType.setText(editor.getCurrentModel().getDisplayString());
        } else {
            lblProjectName.setText("..."); //$NON-NLS-1$
            lblModelName.setText("..."); //$NON-NLS-1$
            lblDiagramType.setText("..."); //$NON-NLS-1$             
        }

        // ================================================
        // 2. Page Setup
        // ================================================

        // ==> Orientation
        Boolean BTemp = (Boolean)psSettings.getSetting(PrintSettings.PORTRAIT);
        if (BTemp.booleanValue() == true) {
            lblOrientation.setText(PORTRAIT);
        }

        BTemp = (Boolean)psSettings.getSetting(PrintSettings.LANDSCAPE);
        if (BTemp.booleanValue() == true) {
            lblOrientation.setText(LANDSCAPE);
        }

        // ==> Scaling
        BTemp = (Boolean)psSettings.getSetting(PrintSettings.FIT_TO_ONE_PAGE);
        if (BTemp.booleanValue() == true) {
            lblScaling.setText(FIT_TO_ONE_PAGE);
        }

        BTemp = (Boolean)psSettings.getSetting(PrintSettings.FIT_TO_ONE_PAGE_HIGH);
        if (BTemp.booleanValue() == true) {
            lblScaling.setText(FIT_TO_ONE_PAGE_HIGH);
        }

        BTemp = (Boolean)psSettings.getSetting(PrintSettings.FIT_TO_ONE_PAGE_WIDE);
        if (BTemp.booleanValue() == true) {
            lblScaling.setText(FIT_TO_ONE_PAGE_WIDE);
        }

        BTemp = (Boolean)psSettings.getSetting(PrintSettings.ADJUST_TO_PERCENT);
        if (BTemp.booleanValue() == true) {
            lblScaling.setText(ADJUST_TO + " " + getScalingPercentage() + PERCENT_NORMAL_SIZE); //$NON-NLS-1$
        }

        // ==> Margins
        Double DTop = (Double)psSettings.getSetting(PrintSettings.TOP_MARGIN);
        Double DRight = (Double)psSettings.getSetting(PrintSettings.RIGHT_MARGIN);
        Double DBottom = (Double)psSettings.getSetting(PrintSettings.BOTTOM_MARGIN);
        Double DLeft = (Double)psSettings.getSetting(PrintSettings.LEFT_MARGIN);

        String sMarginPhrase = TOP + ": " + DTop.toString(); //$NON-NLS-1$  
        sMarginPhrase += " | " + RIGHT + ": " + DRight.toString(); //$NON-NLS-1$  //$NON-NLS-2$
        sMarginPhrase += " | " + BOTTOM + ": " + DBottom.toString(); //$NON-NLS-1$  //$NON-NLS-2$
        sMarginPhrase += " | " + LEFT + ": " + DLeft.toString(); //$NON-NLS-1$  //$NON-NLS-2$      

        lblMargins.setText(sMarginPhrase);

        BTemp = (Boolean)psSettings.getSetting(PrintSettings.OVER_THEN_DOWN);
        if (BTemp.booleanValue() == true) {
            lblPageOrder.setText(OVER_THEN_DOWN);
        }

        BTemp = (Boolean)psSettings.getSetting(PrintSettings.DOWN_THEN_OVER);
        if (BTemp.booleanValue() == true) {
            lblPageOrder.setText(DOWN_THEN_OVER);
        }

        // ================================================
        // 3. Page Range
        // ================================================
        int iPossiblePageCount = getTotalPossiblePageCount();

        Integer IScope = (Integer)psSettings.getSetting(PrintSettings.SCOPE);
        if (IScope.intValue() == PrintSettings.SCOPE_ALL_PAGES) {
            radioAllPages.setSelection(true);
            radioPagesInRange.setSelection(false);
            spinRangeFrom.setPossibleValues(getPageSelectPossibleValues(1, iPossiblePageCount));
            spinRangeFrom.setValue(new Integer(1));

            spinRangeTo.setPossibleValues(getPageSelectPossibleValues(getFromPage(), iPossiblePageCount));
            spinRangeTo.setValue(new Integer(iPossiblePageCount));
            spinRangeFrom.setEnabled(radioPagesInRange.getSelection());
            spinRangeTo.setEnabled(radioPagesInRange.getSelection());
        }

        if (IScope.intValue() == PrintSettings.SCOPE_PAGE_RANGE) {
            radioAllPages.setSelection(false);
            radioPagesInRange.setSelection(true);

            // From
            spinRangeFrom.setPossibleValues(getPageSelectPossibleValues(1, iPossiblePageCount));
            if (psSettings.getSetting(PrintSettings.START_PAGE) != null
                && ((Integer)psSettings.getSetting(PrintSettings.START_PAGE)).intValue() > -1) {
                spinRangeFrom.setValue(psSettings.getSetting(PrintSettings.START_PAGE));
            } else {
                spinRangeFrom.setValue(new Integer(1));
            }

            // To
            spinRangeTo.setPossibleValues(getPageSelectPossibleValues(1, iPossiblePageCount));
            if (psSettings.getSetting(PrintSettings.END_PAGE) != null
                && ((Integer)psSettings.getSetting(PrintSettings.END_PAGE)).intValue() > -1) {
                spinRangeTo.setValue(psSettings.getSetting(PrintSettings.END_PAGE));
            } else {
                spinRangeTo.setValue(new Integer(1));
            }

            spinRangeFrom.setEnabled(radioPagesInRange.getSelection());
            spinRangeTo.setEnabled(radioPagesInRange.getSelection());
        }

        // ================================================
        // 4. Total Pages
        // ================================================
        lblTotalPages.setText(String.valueOf(getTotalActualPageCount()));
    }

    private String getScalingPercentage() {
        Integer IScalingPercent = (Integer)psSettings.getSetting(PrintSettings.SCALING_PERCENTAGE);
        return IScalingPercent.toString();
    }

    private DiagramEditor getActiveDiagramEditor() {
        return DiagramEditorUtil.getVisibleDiagramEditor();
    }

    private void initModifiableSummaryValuesOnSummaryPanel() {
        /*
         * init these controls from PrintSettings; these values came from
         * Eclipse PrintData, which we got from the native Print Dialog.
         */

        // ================================================
        // 3. Page Range
        // ================================================
        int iPossiblePageCount = getTotalActualPageCount();

        Integer IScope = (Integer)psSettings.getSetting(PrintSettings.SCOPE);
        if (IScope.intValue() == PrintSettings.SCOPE_ALL_PAGES) {
            radioAllPages.setSelection(true);
            radioPagesInRange.setSelection(false);

            // also set the to and from to min and max
            spinRangeFrom.setValue(new Integer(1));
            spinRangeTo.setValue(new Integer(iPossiblePageCount));

            // propagate these default values back to Settings
            Integer IFrom = (Integer)spinRangeFrom.getValue();
            //            System.out.println("[DiagramPrintSummaryDialog.initModifiableSummaryValuesOnSummaryPanel] about to set store From to: " + IFrom.intValue() ); //$NON-NLS-1$
            psSettings.setSetting(PrintSettings.START_PAGE, IFrom);

            Integer ITo = (Integer)spinRangeTo.getValue();
            //            System.out.println("[DiagramPrintSummaryDialog.initModifiableSummaryValuesOnSummaryPanel] about to set store To to: " + ITo.intValue() ); //$NON-NLS-1$
            psSettings.setSetting(PrintSettings.END_PAGE, ITo);
            lblTotalPages.setText(String.valueOf(getTotalActualPageCount()));
        }

        if (IScope.intValue() == PrintSettings.SCOPE_PAGE_RANGE) {
            radioAllPages.setSelection(false);
            radioPagesInRange.setSelection(true);
            spinRangeFrom.setValue(psSettings.getSetting(PrintSettings.START_PAGE));
            spinRangeTo.setValue(psSettings.getSetting(PrintSettings.END_PAGE));
        }

    }
}
