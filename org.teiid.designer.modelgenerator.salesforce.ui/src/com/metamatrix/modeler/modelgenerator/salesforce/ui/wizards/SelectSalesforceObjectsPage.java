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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;
import com.metamatrix.modeler.modelgenerator.salesforce.model.DataModel;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceField;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceObject;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class SelectSalesforceObjectsPage extends AbstractWizardPage
    implements Listener, FileUtils.Constants, StringUtil.Constants, ModelGeneratorSalesforceUiConstants,
    ModelGeneratorSalesforceUiConstants.Images, ModelGeneratorSalesforceUiConstants.HelpContexts, ISelectionChangedListener,
    ICheckStateListener {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SelectSalesforceObjectsPage.class);

    SalesforceImportWizardManager importManager;

    private CheckboxTableViewer ctv;
    private Table table;
    private TableViewer tableViewer;

    private Button selectAllButton;

    private Button deselectAllButton;

    private boolean initializing;

    protected DataModel salesforceMetadata;

    boolean updateUI;

    public SelectSalesforceObjectsPage( SalesforceImportWizardManager importManager ) {
        super(SelectSalesforceObjectsPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = importManager;
        setImageDescriptor(SalesforceUIUtil.getImageDescriptor(NEW_MODEL_BANNER));
    }

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        DataModel model = importManager.createDataModel(monitor);
                        if (salesforceMetadata != model) {
                            salesforceMetadata = model;
                            updateUI = true;
                        } else {
                            updateUI = false;
                        }
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
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
            try {
                if (updateUI) {
                    ctv.setInput(salesforceMetadata);
                    StructuredSelection selection = new StructuredSelection(ctv.getElementAt(0));
                    ctv.setSelection(selection);
                    setAllNodesSelected(true);
                    getControl().setVisible(visible);
                    setPageStatus();
                }
            } catch (Exception e) {
                Shell shell = this.getShell();
                Status status = new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getLocalizedMessage(), e);
                ErrorDialog.openError(shell, getString("dialog.dataModelCreationError.title"), e.getLocalizedMessage(), status); //$NON-NLS-1$  
                super.setVisible(false);
                getWizard().getContainer().showPage(getPreviousPage());
                return;
            }
        }
        super.setVisible(visible);
    }

    public void createControl( Composite theParent ) {
        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(theParent, OBJECT_SELECTION_PAGE);

        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_VERTICAL);
        // pnl.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnl);
        SashForm outerSplitter = new SashForm(pnl, SWT.VERTICAL);
        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        outerSplitter.setLayoutData(gid);

        Composite metadata = WidgetFactory.createPanel(outerSplitter, SWT.FILL, GridData.FILL_HORIZONTAL);

        SashForm splitter = new SashForm(metadata, SWT.HORIZONTAL);
        GridData innerGid = new GridData();
        innerGid.grabExcessHorizontalSpace = innerGid.grabExcessVerticalSpace = true;
        innerGid.horizontalAlignment = innerGid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(innerGid);

        createCheckboxComposite(splitter, getString("salesforce.objects")); //$NON-NLS-1$

        createTable(splitter);

        splitter.setWeights(new int[] {25, 75});

        Composite buttons = WidgetFactory.createPanel(outerSplitter, SWT.FILL, GridData.FILL_HORIZONTAL);

        createButtons(buttons);
        outerSplitter.setWeights(new int[] {80, 20});

    }

    /**
     * create the checkbox tree Composite
     * 
     * @param parent the parent composite
     * @param title the group title
     */
    private void createCheckboxComposite( Composite parent,
                                          String title ) {
        Composite checkBoxTreeComposite = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(1, false);
        checkBoxTreeComposite.setLayout(layout);

        // --------------------------
        // Group for checkbox list
        // --------------------------
        Group group = WidgetFactory.createGroup(checkBoxTreeComposite, title, GridData.FILL_BOTH, 1, 2);

        // ----------------------------
        // TreeViewer
        // ----------------------------
        ctv = CheckboxTableViewer.newCheckList(group, SWT.BORDER);
        ctv.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        ctv.setContentProvider(new ListContentProvider());
        ctv.setLabelProvider(new ListLabelProvider());
        ctv.addSelectionChangedListener(this);
        ctv.addCheckStateListener(this);
        ctv.setSorter(new ViewerSorter());
        ctv.setInput(null);
    }

    private void createTable( Composite parent ) {
        Composite tableComposite = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(1, false);
        tableComposite.setLayout(layout);
        Group group = WidgetFactory.createGroup(tableComposite, Messages.getString("SelectSalesforceObjectsPage.column.details"), //$NON-NLS-1$
                                                GridData.FILL_BOTH,
                                                1,
                                                2);

        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        table = new Table(group, style);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 4;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText(Messages.getString("SelectSalesforceObjectsPage.visible.name")); //$NON-NLS-1$
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText(Messages.getString("SelectSalesforceObjectsPage.name.in.src")); //$NON-NLS-1$
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText(Messages.getString("SelectSalesforceObjectsPage.type")); //$NON-NLS-1$
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 3);
        column.setText(Messages.getString("SelectSalesforceObjectsPage.searchable")); //$NON-NLS-1$
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 4);
        column.setText(Messages.getString("SelectSalesforceObjectsPage.updatable")); //$NON-NLS-1$
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 5);
        column.setText(Messages.getString("SelectSalesforceObjectsPage.audit.field")); //$NON-NLS-1$
        column.setWidth(100);

        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);
        tableViewer.setContentProvider(new TableContentProvider());
        tableViewer.setLabelProvider(new TableLabelProvider());

        tableViewer.setColumnProperties(new String[] {
            Messages.getString("SelectSalesforceObjectsPage.visible.name"), Messages.getString("SelectSalesforceObjectsPage.name.in.src"), Messages.getString("SelectSalesforceObjectsPage.type"), Messages.getString("SelectSalesforceObjectsPage.searchable"), Messages.getString("SelectSalesforceObjectsPage.updatable"), Messages.getString("SelectSalesforceObjectsPage.audit.field")}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    }

    private void createButtons( Composite parent ) {
        // ----------------------------
        // Select/DeSelect Buttons
        // ----------------------------
        Composite buttonComposite = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_VERTICAL);
        GridLayout layout = new GridLayout(1, false);
        buttonComposite.setLayout(layout);
        this.selectAllButton = WidgetFactory.createButton(buttonComposite,
                                                          getString("selectAllButton.text"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.selectAllButton.setToolTipText(getString("selectAllButton.tipText")); //$NON-NLS-1$
        this.deselectAllButton = WidgetFactory.createButton(buttonComposite,
                                                            getString("deselectAllButton.text"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.deselectAllButton.setToolTipText(getString("deselectAllButton.tipText")); //$NON-NLS-1$

        this.selectAllButton.addListener(SWT.Selection, this);
        this.deselectAllButton.addListener(SWT.Selection, this);
    }

    /**
     * Event handler
     * 
     * @param event the Event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {

            // box checked
            if (event.detail == SWT.CHECK && ((TableItem)event.data).getGrayed()) {
                TableItem item = (TableItem)event.data;
                ((SalesforceObject)item.getData()).setSelected(item.getChecked());
            }

            // SelectAll button selected
            if (event.widget == this.selectAllButton) {
                setAllNodesSelected(true);
            }

            // DeselectAll button selected
            if (event.widget == this.deselectAllButton) {
                setAllNodesSelected(false);
            }

            setPageStatus();
        }
    }

    /**
     * Sets the wizard page status message.
     * 
     * @since 4.2
     */
    private void setPageStatus() {
        if (!this.importManager.getDataModel().hasSelectedObjects()) {
            WizardUtil.setPageComplete(this, getString("noSelections.msg"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        }

        WizardUtil.setPageComplete(this);

        getContainer().updateButtons();
    }

    private void setAllNodesSelected( boolean bSelected ) {
        Object[] sfObjects = importManager.getDataModel().getSalesforceObjects();
        for (int i = 0; i < sfObjects.length; i++) {
            SalesforceObject obj = (SalesforceObject)sfObjects[i];
            obj.setSelected(bSelected);
            ctv.setChecked(obj, bSelected);
        }
    }

    public void selectionChanged( SelectionChangedEvent event ) {
        Object obj;
        if (event.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection)event.getSelection();
            if (!selection.isEmpty()) {
                obj = selection.iterator().next();
                tableViewer.setInput(obj);
            }
        } else {
            throw new RuntimeException(Messages.getString("SelectSalesforceObjectsPage.unsupported.type")); //$NON-NLS-1$
        }
    }

    public void checkStateChanged( CheckStateChangedEvent event ) {
        SalesforceObject obj = (SalesforceObject)event.getElement();
        boolean checked = event.getChecked();
        obj.setSelected(checked);
        setPageStatus();
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

    static class ListContentProvider implements IStructuredContentProvider {

        public void dispose() {
        }

        public Object[] getElements( final Object node ) {
            if (node instanceof DataModel) {
                return ((DataModel)node).getSalesforceObjects();
            }
            return EMPTY_STRING_ARRAY;
        }

        public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput ) {
        }
    }

    static class ListLabelProvider extends LabelProvider {

        final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();

        @Override
        public Image getImage( final Object node ) {
            return null;
        }

        @Override
        public String getText( final Object node ) {
            if (node instanceof DataModel) {
                return "theModel"; //$NON-NLS-1$
            } else if (node instanceof SalesforceObject) {
                return ((SalesforceObject)node).getLabel();
            }
            return "unknownElement"; //$NON-NLS-1$
        }
    }

    class TableContentProvider implements IStructuredContentProvider {

        public Object[] getElements( Object inputElement ) {
            Object[] result = null;
            if (inputElement instanceof SalesforceObject) {
                SalesforceObject object = (SalesforceObject)inputElement;
                if (!importManager.isModelAuditFields()) {
                    ArrayList<SalesforceField> nonAuditFields = new ArrayList<SalesforceField>();
                    SalesforceField[] fields = object.getFields();
                    for (int i = 0; i < fields.length; i++) {
                        SalesforceField field = fields[i];
                        if (!field.isAuditField()) {
                            nonAuditFields.add(field);
                        }
                    }
                    result = nonAuditFields.toArray();
                } else {
                    result = object.getFields();
                }

            }
            return result;
        }

        public void dispose() {
        }

        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }

    }

    static class TableLabelProvider implements ITableLabelProvider {

        public void dispose() {
        }

        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }

        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            return null;
        }

        public String getColumnText( Object element,
                                     int columnIndex ) {

            SalesforceField field = (SalesforceField)element;
            String result = ""; //$NON-NLS-1$
            switch (columnIndex) {
                case 0: // COMPLETED_COLUMN
                    result = field.getLabel();
                    break;
                case 1:
                    result = field.getName();
                    break;
                case 2:
                    result = field.getType();
                    break;
                case 3:
                    result = field.isSearchable() ? "True" : "False"; //$NON-NLS-1$ //$NON-NLS-2$
                    break;
                case 4:
                    result = field.isUpdateable() ? "True" : "False"; //$NON-NLS-1$ //$NON-NLS-2$
                    break;
                case 5:
                    result = field.isAuditField() ? "True" : "False"; //$NON-NLS-1$ //$NON-NLS-2$
                    break;
                default:
                    break;
            }
            return result;
        }

        public void addListener( ILabelProviderListener listener ) {
        }

        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        public void removeListener( ILabelProviderListener listener ) {
        }
    }
}
