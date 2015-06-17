package org.teiid.datatools.views;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.datatools.connectivity.ui.Activator;
import org.teiid.datatools.connectivity.ui.Messages;
import org.teiid.datatools.connectivity.ui.PreferenceConstants;
import org.teiid.datatools.connectivity.ui.plan.ExecutionPlanConverter;

/**
 * ExecutionPlanView
 *
 * @since 8.0
 */

public class ExecutionPlanView extends ViewPart implements IEclipsePreferences.IPreferenceChangeListener {

    /**
     * View ID of this view
     */
    public static final String VIEW_ID = "views.executionPlanView"; //$NON-NLS-1$

    private static final String IMPORT_PLAN_IMG = "icons/import_from_file.gif"; //$NON-NLS-1$
    private static final String EXPORT_PLAN_IMG = "icons/export_to_file.gif"; //$NON-NLS-1$

    private static final String EXECUTION_PLAN_HELP_ID = "org.teiid.designer.runtime.ui.executionPlanOverview"; //$NON-NLS-1$
    private static final String PREFIX = "ExecutionPlanView."; //$NON-NLS-1$

    private Browser visualisation;
    private StyledText sqlTextArea;
    private TreeViewer planTreeViewer;
    private StyledText planTextArea;

    private Label panelDescriptionLabel;
    private Label panelPlansEnabledLabel;
    private String planString;

    private IAction saveToFileAction;
    private IAction readFromFileAction;

    private Button expandAll;
    private Button collapseAll;

    /**
     * The constructor.
     */
    public ExecutionPlanView() {
        super();
    }

    /**
     * This is a call-back that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl( Composite parent ) {
        createActions();
        createPanel(parent);

        IActionBars actionBars = getViewSite().getActionBars();
        configureToolBar(actionBars.getToolBarManager());

        IWorkbenchHelpSystem helpSystem = Activator.getDefault().getWorkbench().getHelpSystem();
        helpSystem.setHelp(parent, EXECUTION_PLAN_HELP_ID);

        updateButtonEnablements();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

    private void configureToolBar( IToolBarManager toolBarMgr ) {
        toolBarMgr.add(this.saveToFileAction);
        toolBarMgr.add(this.readFromFileAction);
        
        toolBarMgr.update(true);
    }

    private void createActions() {
        String savePlanToFileActionText = Messages.getString(PREFIX + "savePlanToFileButton.text"); //$NON-NLS-1$
        String savePlanToFileActionTooltip = Messages.getString(PREFIX + "savePlanToFileButton.tooltip"); //$NON-NLS-1$

        this.saveToFileAction = new Action(savePlanToFileActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleSavePlanToFile();
            }
        };
        this.saveToFileAction.setToolTipText(savePlanToFileActionTooltip);
        this.saveToFileAction.setEnabled(false);
        this.saveToFileAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(EXPORT_PLAN_IMG));

        String readPlanFromFileActionText = Messages.getString(PREFIX + "readPlanFromFileButton.text"); //$NON-NLS-1$
        String readPlanFromFileActionTooltip = Messages.getString(PREFIX + "readPlanFromFileButton.tooltip"); //$NON-NLS-1$

        this.readFromFileAction = new Action(readPlanFromFileActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleReadPlanFromFile();
            }
        };
        this.readFromFileAction.setToolTipText(readPlanFromFileActionTooltip);
        this.readFromFileAction.setEnabled(true);
        this.readFromFileAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(IMPORT_PLAN_IMG));
    }

    /*
     * Create the Main Panel of the Viewer
     */
    private void createPanel( Composite parent ) {
        Composite mainPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        mainPanel.setLayout(gridLayout);
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        // --------------------------------------------
        // Panel for selected Object display
        // --------------------------------------------
        
        // Panel containing descripton and preference controls
        createDescriptionPanel(mainPanel);

        SashForm splitter = new SashForm(mainPanel, SWT.VERTICAL);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = 3;
        splitter.setLayoutData(gd);

        // --------------------------------------------
        // Panel for Submitted SQL
        // --------------------------------------------
        createSqlGroup(splitter);

        // --------------------------------------------
        // Panel for Execution Plan Display
        // --------------------------------------------
        createPlanViewerGroup(splitter);

        splitter.setWeights(new int[] {25, 75});
        
        IEclipsePreferences prefs = Activator.getDefault().getPreferences();
        prefs.addPreferenceChangeListener(this);

    }
    
    private String getPlansEnabledText() {
        boolean showPlan = Activator.getDefault().getPreferences().getBoolean(PreferenceConstants.TEIID_QUERYPLANS_ENABLED, PreferenceConstants.TEIID_QUERYPLANS_ENABLED_DEFAULT);

        StringBuffer sb = new StringBuffer(Messages.getString(PREFIX + "queryPlansEnabledLabel")); //$NON-NLS-1$

        if(showPlan) {
        	sb.append(" "+Messages.getString(PREFIX + "queryPlansEnabledStateText")); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
        	sb.append(" "+Messages.getString(PREFIX + "queryPlansDisabledStateText")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return sb.toString();
    }

    /*
     * Create the Group containing the SQL Text Viewer (not editable)
     */
    private void createSqlGroup( Composite parent ) {
        Group sqlTextGroup = new Group(parent, SWT.NONE);
        sqlTextGroup.setText(Messages.getString(PREFIX + "sqlGroup")); //$NON-NLS-1$

        sqlTextGroup.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_BOTH);
        sqlTextGroup.setLayoutData(gd);

        sqlTextArea = new StyledText(sqlTextGroup, SWT.NONE | SWT.V_SCROLL);
        GridData gdt = new GridData(GridData.FILL_BOTH);
        sqlTextArea.setLayoutData(gdt);

        sqlTextArea.setEditable(false);
        sqlTextArea.setWordWrap(true);
        sqlTextArea.setBackground(sqlTextGroup.getBackground());
    }

    /*
     * Create the Group containing the Plan TreeViewer (not editable)
     */
    private void createPlanViewerGroup( Composite parent ) {
        Group planViewerGroup = new Group(parent, SWT.NONE);
        planViewerGroup.setText(Messages.getString(PREFIX + "planViewerGroup")); //$NON-NLS-1$

        planViewerGroup.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_BOTH);
        planViewerGroup.setLayoutData(gd);

        TabFolder tabFolder = new TabFolder(planViewerGroup, SWT.TOP);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        createPlanHTMLTab(tabFolder);
        createPlanTreeTab(tabFolder);
        createPlanTextTab(tabFolder);
    }

    /*
     * Create the Execution Plan HTML Tab
     */
    private void createPlanHTMLTab(TabFolder folderParent) {
        Composite tabPanel = createHTMLViewerPanel(folderParent);
        if (tabPanel == null) {
            // An error occurred getting the browser widget
            // Do not show this tab if no panel available
            return;
        }

        TabItem planHTMLTab = new TabItem(folderParent, SWT.NONE);
        planHTMLTab.setControl(tabPanel);
        planHTMLTab.setText(Messages.getString(PREFIX + "planViewerHTMLTab.name")); //$NON-NLS-1$
    }

    /*
     * Create the Execution Plan Text Tab
     */
    private void createPlanTextTab( TabFolder folderParent ) {
        Composite tabPanel = createTextViewerPanel(folderParent);
        TabItem planTextTab = new TabItem(folderParent, SWT.NONE);

        planTextTab.setControl(tabPanel);
        planTextTab.setText(Messages.getString(PREFIX + "planViewerTextTab.name")); //$NON-NLS-1$
    }

    /*
     * Create the Execution Plan TreeView Tab
     */
    private void createPlanTreeTab( TabFolder folderParent ) {
        Composite tabPanel = createTreeViewerPanel(folderParent);
        TabItem planTreeTab = new TabItem(folderParent, SWT.NONE);

        planTreeTab.setControl(tabPanel);
        planTreeTab.setText(Messages.getString(PREFIX + "planViewerTreeTab.name")); //$NON-NLS-1$
    }

    /*
     * Create the Panel which defines the plan text view tab
     */
    private Composite createHTMLViewerPanel( Composite parent ) {
        try {
            visualisation = new Browser(parent, SWT.NONE);
        } catch (Throwable ex) {
            //
            // Exception occurred while trying to initialise browser object
            //
            Activator.log(ex);
        }

        return visualisation;
    }

    /*
     * Create the Panel which defines the plan text view tab
     */
    private Composite createTextViewerPanel( Composite parent ) {
        Composite textViewerPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        textViewerPanel.setLayout(gridLayout);
        textViewerPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        planTextArea = new StyledText(textViewerPanel, SWT.NONE | SWT.V_SCROLL);
        GridData gdt = new GridData(GridData.FILL_BOTH);
        planTextArea.setLayoutData(gdt);
        planTextArea.setEditable(false);
        planTextArea.setWordWrap(true);
        return textViewerPanel;
    }

    /*
     * Create the Panel which defines the plan tree view tab
     */
    private Composite createTreeViewerPanel( Composite parent ) {
        Composite treeViewerPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        treeViewerPanel.setLayout(gridLayout);
        treeViewerPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Create Button Controls
        createTreeViewerButtonsPanel(treeViewerPanel);

        // Create the TreeViewer
        planTreeViewer = new TreeViewer(treeViewerPanel, SWT.H_SCROLL | SWT.V_SCROLL);

        // TreeViewer ContentProvider
        planTreeViewer.setContentProvider(new PlanTreeContentProvider());

        // TreeViewer LabelProvider
        planTreeViewer.setLabelProvider(new LabelProvider() {

            @Override
            public Image getImage( Object element ) {
                return null;
            }

            @Override
            public String getText( Object element ) {
                return getNodeDisplayName((PlanElement)element);
            }
        });

        planTreeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        return treeViewerPanel;
    }
    
    private void createDescriptionPanel( Composite parent ) {
        Composite descPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        descPanel.setLayout(gridLayout);
        descPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;

        // DescriptionLabel
        panelDescriptionLabel = new Label(descPanel, SWT.NONE);
        panelDescriptionLabel.setLayoutData(gd);
        panelDescriptionLabel.setText(Messages.getString(PREFIX + "emptyPanelText")); //$NON-NLS-1$
        
        // Plan preferences label
        panelPlansEnabledLabel = new Label(descPanel, SWT.NONE);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = GridData.END;
        gd2.verticalAlignment = GridData.BEGINNING;
        gd2.grabExcessHorizontalSpace = false;
        gd2.horizontalSpan = 1;
        panelPlansEnabledLabel.setLayoutData(gd2);
        panelPlansEnabledLabel.setText(getPlansEnabledText()); 
        
        Button changeButton = new Button(descPanel, SWT.PUSH);
        GridData gd3 = new GridData();
        gd3.horizontalAlignment = GridData.END;
        gd3.verticalAlignment = GridData.BEGINNING;
        gd3.grabExcessHorizontalSpace = false;
        gd3.horizontalSpan = 1;
        changeButton.setLayoutData(gd3);
        changeButton.setText(Messages.getString(PREFIX + "changePrefButton.text")); //$NON-NLS-1$
        changeButton.setToolTipText(Messages.getString(PREFIX + "changePrefButton.tooltip")); //$NON-NLS-1$
        changeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
            	IEclipsePreferences prefs = Activator.getDefault().getPreferences();
                boolean currentSetting = prefs.getBoolean(PreferenceConstants.TEIID_QUERYPLANS_ENABLED, PreferenceConstants.TEIID_QUERYPLANS_ENABLED_DEFAULT);
                prefs.putBoolean(PreferenceConstants.TEIID_QUERYPLANS_ENABLED, !currentSetting);

                // save
                try {
                    prefs.flush();
                } catch (BackingStoreException e) {
                }
            }
        });
    }

    /*
     * Create the Buttons Panel used on the TreeViewer panel
     */
    private void createTreeViewerButtonsPanel( Composite parent ) {
        Composite buttonPanel = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        buttonPanel.setLayout(gridLayout);
        buttonPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        String expandAllButtonText = Messages.getString(PREFIX + "expandAllButton.text"); //$NON-NLS-1$
        String expandAllButtonTooltip = Messages.getString(PREFIX + "expandAllButton.tooltip"); //$NON-NLS-1$
        String collapseAllButtonText = Messages.getString(PREFIX + "collapseAllButton.text"); //$NON-NLS-1$
        String collapseAllButtonTooltip = Messages.getString(PREFIX + "collapseAllButton.tooltip"); //$NON-NLS-1$

        expandAll = new Button(buttonPanel, SWT.PUSH);
        expandAll.setText(expandAllButtonText);
        collapseAll = new Button(buttonPanel, SWT.PUSH);
        collapseAll.setText(collapseAllButtonText);

        expandAll.setToolTipText(expandAllButtonTooltip);
        expandAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                planTreeViewer.expandAll();
            }
        });
        collapseAll.setToolTipText(collapseAllButtonTooltip);
        collapseAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                planTreeViewer.collapseAll();
            }
        });
    }

    /*
     * Get the Text to display for the provided PlanElement node.  For 'node' and 'property' elements, the 'name' attribute is used.
     * For 'value' elements the value of the element is used.
     */
    private String getNodeDisplayName( PlanElement element ) {
        String displayName = null;

        String nodeName = element.getName();
        // 'node' and 'property' elements - use the name attribute
        if (ExecutionPlanParser.NODE_ELEM.equalsIgnoreCase(nodeName)
            || ExecutionPlanParser.PROPERTY_ELEM.equalsIgnoreCase(nodeName)) {
            displayName = element.getAttributeValue(ExecutionPlanParser.NAME_ATTR);
            // 'value' elements - use the element value
        } else if (ExecutionPlanParser.VALUE_ELEM.equalsIgnoreCase(nodeName)) {
            displayName = element.getValue();
        }

        return displayName;
    }

    /**
     * Save the current Execution Plan xml to a file.
     */
    private void handleSavePlanToFile() {
        Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        // Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$ 
        dlg.setText(Messages.getString(PREFIX + "savePlanDialog.title")); //$NON-NLS-1$
        dlg.setFileName(Messages.getString(PREFIX + "savePlanDialog.defaultFileName")); //$NON-NLS-1$
        String fileStr = dlg.open();
        // If there is no file extension, add .sql
        if (fileStr != null && fileStr.indexOf('.') == -1) {
            fileStr = fileStr + "." + Messages.getString(PREFIX + "savePlanDialog.defaultFileExt"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (fileStr != null) {
            FileWriter fw = null;
            BufferedWriter out = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(fileStr);
                out = new BufferedWriter(fw);
                pw = new PrintWriter(out);
                pw.write(planString);

            } catch (Exception e) {
                IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                                            Messages.getString(PREFIX + "savePlanDialog.saveError"), e); //$NON-NLS-1$
                Activator.getDefault().getLog().log(status);
            } finally {
                pw.close();
                try {
                    out.close();
                } catch (java.io.IOException e) {
                }
                try {
                    fw.close();
                } catch (java.io.IOException e) {
                }
            }
        }
    }
    
    /**
     * Save the current Execution Plan xml to a file.
     */
    private void handleReadPlanFromFile() {
        Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        // Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog dlg = new FileDialog(shell, SWT.OPEN);
        dlg.setFilterExtensions(new String[] {"*.xml", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$ 
        dlg.setText(Messages.getString(PREFIX + "importPlanDialog.title")); //$NON-NLS-1$
        dlg.setFileName(Messages.getString(PREFIX + "importPlanDialog.defaultFileName")); //$NON-NLS-1$
        String fileStr = dlg.open();
                
        if (fileStr != null) {
            FileReader fr = null;
            BufferedReader in = null;
            try {
                fr = new FileReader(fileStr);
                in = new BufferedReader(fr);
                String str;
                StringBuffer all = new StringBuffer();
                while ((str = in.readLine()) != null) {
                    all.append(str);
                }
                String planText = all.toString();
                String sqlStr = Messages.getString(PREFIX + "readPlanFromFile.sql"); //$NON-NLS-1$
                String descriptionStr = Messages.getString(PREFIX + "readPlanFromFile.description"); //$NON-NLS-1$
                
                updateContents(descriptionStr,sqlStr,planText);

            } catch (Exception e) {
                IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                                            Messages.getString(PREFIX + "importPlanDialog.error"), e); //$NON-NLS-1$
                Activator.getDefault().getLog().log(status);
            } finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                } catch (java.io.IOException e) {
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (java.io.IOException e) {
                }

            }
        }
    }

    private void displayNoExecPlanMessage() {
        if (visualisation == null)
            return; // No visualisation component so nothing to do

        try {
            File noPlanFile = new File(ExecutionPlanConverter.TEMP_DIRECTORY, "NoExecutionPlan.html");  //$NON-NLS-1$
            if (! noPlanFile.exists()) {
                noPlanFile.createNewFile();
                PrintWriter writer = new PrintWriter(noPlanFile);
                writer.println(ExecutionPlanConverter.NO_EXECUTION_PLAN_HTML);
                writer.flush();
                writer.close();
            }
            noPlanFile.deleteOnExit();
            
            visualisation.setUrl(noPlanFile.toURI().toURL().toString());
        } catch (IOException ex) {
            Activator.log(ex);
        }
    }

    private void displayHTMLExecPlan(String execPlan) {
        if (visualisation == null)
            return; // No visualisation component so nothing to do

        try {
            ExecutionPlanConverter converter = new ExecutionPlanConverter();
            String url = converter.convert(execPlan);
            visualisation.setUrl(url);
        } catch (Exception ex) {
            Activator.log(ex);
            displayNoExecPlanMessage();
        }
    }

    /**
     * Update the state of the ExecutionPlan panel
     * @param description the text description for the panel
     * @param sql the SQL to display
     * @param planStr the ExecutionPlan text
     */
    public void updateContents( String description, String sql, String planStr ) {
        this.planString = planStr;
        // Update the Object Name Label
        if(description!=null) {
            panelDescriptionLabel.setText(description);
        } else {
            panelDescriptionLabel.setText(""); //$NON-NLS-1$
        }

        // Update the SQL String
        if (sql != null) {
            sqlTextArea.setText(sql);
        } else {
            sqlTextArea.setText(""); //$NON-NLS-1$
        }

        // Update the browser visualisation
        if (planStr != null) {
            displayHTMLExecPlan(planStr);
        } else {
            displayNoExecPlanMessage();
        }
        
        // Update the Text area
        if (planStr != null) {
            planTextArea.setText(planStr);
        } else {
            planTextArea.setText(""); //$NON-NLS-1$
        }

        // Update the Execution Plan Tree
        ExecutionPlanParser planParser = new ExecutionPlanParser();
        if (planStr != null && !planStr.isEmpty()) {
            planParser.parse(planStr);

            ExecutionPlan executionPlan = planParser.getExecutionPlan();
            // Set TreeViewer Input to Plan Root Node
            planTreeViewer.setInput(executionPlan);
        } else {
            planTreeViewer.setInput(null);
        }
        planTreeViewer.refresh();
        planTreeViewer.expandToLevel(2);

        panelDescriptionLabel.pack();
        panelDescriptionLabel.getParent().layout();
        updateButtonEnablements();
    }

    /*
     * Update the button enabled states.
     */
    private void updateButtonEnablements() {
        if (this.planString != null) {
            this.saveToFileAction.setEnabled(true);
            this.expandAll.setEnabled(true);
            this.collapseAll.setEnabled(true);
        } else {
            this.saveToFileAction.setEnabled(false);
            this.expandAll.setEnabled(false);
            this.collapseAll.setEnabled(false);
        }
    }

    /*
     * Content provider for the plan tree viewer.
     */
    class PlanTreeContentProvider implements ITreeContentProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        @Override
        public void dispose() {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        @Override
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        @Override
        public Object getParent( Object element ) {
            return ((PlanElement)element).getParent();
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         * @since 5.0.1
         */
        @Override
		public Object[] getChildren( Object element ) {
            if (element instanceof ExecutionPlan) {
                Object[] result = new Object[1];
                result[0] = ((ExecutionPlan)element).getRoot();
                return result;
            }
            return ((PlanElement)element).getChildElements();
        }

        /**
         * @since 5.0.1
         */
        @Override
        public Object[] getElements( Object inputElement ) {
            return getChildren(inputElement);
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         * @since 4.0
         */
        @Override
		public boolean hasChildren( Object element ) {
            Object[] children = ((PlanElement)element).getChildElements();
            return (children.length > 0);
        }

    }

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
	 */
	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		// Updates the PlansEnabled text on Preference change
        if(!panelPlansEnabledLabel.isDisposed()) {
        	panelPlansEnabledLabel.setText(getPlansEnabledText()); 
            panelPlansEnabledLabel.getParent().layout();
        }
	}

}
