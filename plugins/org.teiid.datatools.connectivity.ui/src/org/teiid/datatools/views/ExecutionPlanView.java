package org.teiid.datatools.views;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;
import org.teiid.datatools.connectivity.ui.Activator;
import org.teiid.datatools.connectivity.ui.Messages;

/**
 * ExecutionPlanView
 */

public class ExecutionPlanView extends ViewPart {
    public static final String VIEW_ID = "views.executionPlanView"; //$NON-NLS-1$

    private static final String EXECUTION_PLAN_HELP_ID = "org.teiid.designer.runtime.ui.executionPlanOverview"; //$NON-NLS-1$
    private static final String PREFIX = "ExecutionPlanView."; //$NON-NLS-1$

    private StyledText sqlTextArea;
    private TreeViewer planTreeViewer;
    private StyledText planTextArea;

    private Label panelDescriptionLabel;
    private String planString;

    private IAction saveToFileAction;

    Button expandAll;
    Button collapseAll;

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
        this.saveToFileAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
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

        // Model Object Name
        panelDescriptionLabel = new Label(mainPanel, GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        panelDescriptionLabel.setText(Messages.getString(PREFIX + "emptyPanelText")); //$NON-NLS-1$

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
        createPlanTextTab(tabFolder);
        createPlanTreeTab(tabFolder);
    }

    /*
     * Create the Execution Plan Text Tab
     */
    void createPlanTextTab( TabFolder folderParent ) {
        Composite tabPanel = createTextViewerPanel(folderParent);
        TabItem planTextTab = new TabItem(folderParent, SWT.NONE);

        planTextTab.setControl(tabPanel);
        planTextTab.setText(Messages.getString(PREFIX + "planViewerTextTab.name")); //$NON-NLS-1$
    }

    /*
     * Create the Execution Plan TreeView Tab
     */
    void createPlanTreeTab( TabFolder folderParent ) {
        Composite tabPanel = createTreeViewerPanel(folderParent);
        TabItem planTreeTab = new TabItem(folderParent, SWT.NONE);

        planTreeTab.setControl(tabPanel);
        planTreeTab.setText(Messages.getString(PREFIX + "planViewerTreeTab.name")); //$NON-NLS-1$
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

    /*
     * Save the current Execution Plan xml to a file.
     */
    public void handleSavePlanToFile() {
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

    /*
     * Update the state of the ExecutionPlan panel
     * @param description the text description for the panel
     * @param sql the SQL to display
     * @param planStr the ExecutionPlan text
     */
    public void updateContents( String description,
                                String sql,
                                String planStr ) {
        this.planString = planStr;
        // Update the Object Name Label
        panelDescriptionLabel.setText(description);

        // Update the SQL String
        if (sql != null) {
            sqlTextArea.setText(sql);
        } else {
            sqlTextArea.setText(""); //$NON-NLS-1$
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
    public void updateButtonEnablements() {
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
        public Object[] getChildren( Object element ) {
            if (element instanceof ExecutionPlan) {
                Object[] result = new Object[1];
                result[0] = ((ExecutionPlan)element).getRoot();
                return result;
            }
            return ((PlanElement)element).getChildElements();
        }

        /**
         * @see com.metamatrix.ui.internal.widget.DefaultContentProvider#getElements(java.lang.Object)
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
        public boolean hasChildren( Object element ) {
            Object[] children = ((PlanElement)element).getChildElements();
            return (children.length > 0);
        }

    }

}
