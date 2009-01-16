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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Binding;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Fault;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;
import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.widget.DefaultTreeViewerController;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * WSDL Operations Selection page. This page of the WSDL to Relational Importer is used to select the operations in the source
 * wsdl that will be used to generate the relational entities.
 */
public class SelectWsdlOperationsPage extends AbstractWizardPage
    implements Listener, FileUtils.Constants, StringUtil.Constants, ModelGeneratorWsdlUiConstants,
    ModelGeneratorWsdlUiConstants.Images {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SelectWsdlOperationsPage.class);

    /** <code>IDialogSetting</code>s key for saved dialog height. */
    private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog width. */
    private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog X position. */
    private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog Y position. */
    private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

    /** The checkbox treeViewer */
    private TreeViewer treeViewer;
    private Tree tree;
    boolean treeExpanded = false;
    private CheckboxTreeController controller;

    /** Buttons for tree selection */
    private Button selectAllButton;
    private Button deselectAllButton;

    /** Text area for display of selection details */
    private Text detailsTextBox;

    /** The import manager. */
    WSDLImportWizardManager importManager;

    /** The WSDL model representation */
    Model wsdlModel = null;

    private boolean initializing = false;

    /**
     * Constructs the page with the provided import manager
     * 
     * @param theImportManager the import manager object
     */
    public SelectWsdlOperationsPage( WSDLImportWizardManager theImportManager ) {
        super(SelectWsdlOperationsPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = theImportManager;
        this.importManager.setSelectedOperations(new ArrayList());
        setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
    }

    /**
     * Event handler
     * 
     * @param event the Event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {
            boolean validate = false;

            // Tree node selected
            if (event.widget == this.tree) {
                updateTreeSelectionDetails();
            }

            // SelectAll button selected
            if (event.widget == this.selectAllButton) {
                setAllNodesSelected(true);
            }

            // DeselectAll button selected
            if (event.widget == this.deselectAllButton) {
                setAllNodesSelected(false);
            }

            if (validate) {
                setPageStatus();
            }
        }
    }

    void updateTreeSelectionDetails() {
        TreeItem[] selections = this.tree.getSelection();
        if (selections != null && selections.length > 0) {
            TreeItem selectedItem = selections[0];
            updateSelectionDetailsArea(selectedItem.getData());
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(COLUMNS, false);
        pnlMain.setLayout(layout);
        setControl(pnlMain);

        SashForm splitter = new SashForm(pnlMain, SWT.VERTICAL);
        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(gid);

        createCheckboxTreeComposite(splitter, getString("checkboxTreeGroup.title")); //$NON-NLS-1$

        createDetailsComposite(splitter);

        splitter.setWeights(new int[] {70, 30});

        restoreState();
    }

    /**
     * create the checkbox tree Composite
     * 
     * @param parent the parent composite
     * @param title the group title
     */
    private void createCheckboxTreeComposite( Composite parent,
                                              String title ) {
        Composite checkBoxTreeComposite = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(1, false);
        checkBoxTreeComposite.setLayout(layout);

        // --------------------------
        // Group for checkbox tree
        // --------------------------
        Group group = WidgetFactory.createGroup(checkBoxTreeComposite, title, GridData.FILL_BOTH, 1, 2);

        // ----------------------------
        // TreeViewer
        // ----------------------------
        this.controller = new CheckboxTreeController();
        this.treeViewer = WidgetFactory.createTreeViewer(group, SWT.SINGLE | SWT.CHECK, GridData.FILL_BOTH, controller);

        this.tree = this.treeViewer.getTree();
        tree.addListener(SWT.Selection, this);

        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.treeViewer.setContentProvider(new CheckboxTreeContentProvider());
        this.treeViewer.setLabelProvider(new CheckboxTreeLabelProvider());

        this.treeViewer.setInput(null);

        // ----------------------------
        // Select/DeSelect Buttons
        // ----------------------------
        Composite buttonComposite = WidgetFactory.createPanel(group, SWT.NONE, GridData.FILL_VERTICAL);
        layout = new GridLayout(1, false);
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
     * create the selection details text Composite
     * 
     * @param parent the parent composite
     */
    private void createDetailsComposite( Composite parent ) {
        Composite detailsComposite = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(1, false);
        detailsComposite.setLayout(layout);

        // Have to remove WidgetFactory.createLabel since it breaks 4.3.3
        // at runtime due to a change to the return type to a type that
        // does not exist in 4.3.3

        //WidgetFactory.createLabel(detailsComposite, SWT.NONE, getString("detailsTextbox.title"));  //$NON-NLS-1$
        CLabel theLabel = new CLabel(detailsComposite, SWT.NONE);
        theLabel.setText(getString("detailsTextbox.title")); //$NON-NLS-1$
        final GridData gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        theLabel.setLayoutData(gridData);

        this.detailsTextBox = WidgetFactory.createTextBox(detailsComposite, SWT.NONE, GridData.FILL_BOTH);
        detailsTextBox.setEditable(false);
    }

    /**
     * update method for selection details area
     */
    private void updateSelectionDetailsArea( Object selectedObject ) {
        StringBuffer sb = new StringBuffer();
        if (selectedObject instanceof Service) {
            Service theService = (Service)selectedObject;
            sb.append(theService.getName() + " [Service]\n"); //$NON-NLS-1$
            sb.append("id: " + theService.getId()); //$NON-NLS-1$
        } else if (selectedObject instanceof Port) {
            Port thePort = (Port)selectedObject;
            sb.append(thePort.getName() + " [Port]\n"); //$NON-NLS-1$
            sb.append("id: " + thePort.getId()); //$NON-NLS-1$
        } else if (selectedObject instanceof Binding) {
            addBindingDetails((Binding)selectedObject, sb);
        } else if (selectedObject instanceof Operation) {
            addOperationDetails((Operation)selectedObject, sb);
        } else if (selectedObject instanceof Message) {
            Message theMessage = (Message)selectedObject;
            sb.append(theMessage.getName() + " [Message]\n"); //$NON-NLS-1$
            sb.append("id: " + theMessage.getId()); //$NON-NLS-1$
        }
        this.detailsTextBox.setText(sb.toString());
    }

    private void addBindingDetails( Binding binding,
                                    StringBuffer sb ) {
        sb.append(binding.getName() + " [Binding]\n"); //$NON-NLS-1$
        sb.append("id: \t\t" + binding.getId() + '\n'); //$NON-NLS-1$
        sb.append("uri: \t" + binding.getTransportURI() + '\n'); //$NON-NLS-1$
        sb.append("style: \t" + binding.getStyle()); //$NON-NLS-1$
    }

    private void addOperationDetails( Operation operation,
                                      StringBuffer sb ) {
        sb.append(operation.getName() + " [Operation]\n"); //$NON-NLS-1$
        sb.append("id: \t\t\t\t\t" + operation.getId() + '\n'); //$NON-NLS-1$
        sb.append("input message: \t" + operation.getInputMessage().getName() + '\n'); //$NON-NLS-1$
        sb.append("output message: \t" + operation.getOutputMessage().getName() + '\n'); //$NON-NLS-1$
        Fault[] faults = operation.getFaults();
        sb.append("fault names: \t\t"); //$NON-NLS-1$
        if (faults == null || faults.length == 0) {
            sb.append("none"); //$NON-NLS-1$
        } else {
            for (int i = 0; i < faults.length; i++) {
                Fault theFault = faults[i];
                sb.append(theFault.getName());
                if (i != faults.length - 1) {
                    sb.append(", "); //$NON-NLS-1$
                }
            }
        }
        // --------------------------------------------------------
        // Add Problem Messages
        // --------------------------------------------------------
        if (!operation.canModel()) {
            String[] errorMsgs = operation.getProblemMessages();
            for (int i = 0; i < errorMsgs.length; i++) {
                sb.append('\n');
                sb.append(errorMsgs[i]);
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        saveState();
    }

    /**
     * Override to replace the NewModelWizard settings with the section devoted to the Web Service Model Wizard.
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     * @since 4.2
     */
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();

        if (settings != null) {
            // get the right section of the NewModelWizard settings
            IDialogSettings temp = settings.getSection(DIALOG_SETTINGS_SECTION);

            if (temp == null) {
                settings = settings.addNewSection(DIALOG_SETTINGS_SECTION);
            } else {
                settings = temp;
            }
        }

        return settings;
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

    /**
     * Restores dialog size and position of the last time wizard ran.
     * 
     * @since 4.2
     */
    private void restoreState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null) {
            Shell shell = getContainer().getShell();

            if (shell != null) {
                try {
                    int x = settings.getInt(DIALOG_X);
                    int y = settings.getInt(DIALOG_Y);
                    int width = settings.getInt(DIALOG_WIDTH);
                    int height = settings.getInt(DIALOG_HEIGHT);
                    shell.setBounds(x, y, width, height);
                } catch (NumberFormatException theException) {
                    // getInt(String) throws exception if not found.
                    // just means no settings exist yet.
                }
            }
        }
    }

    /**
     * Persists dialog size and position.
     * 
     * @since 4.2
     */
    private void saveState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null) {
            Shell shell = getContainer().getShell();

            if (shell != null) {
                Rectangle r = shell.getBounds();
                settings.put(DIALOG_X, r.x);
                settings.put(DIALOG_Y, r.y);
                settings.put(DIALOG_WIDTH, r.width);
                settings.put(DIALOG_HEIGHT, r.height);
            }
        }
    }

    /**
     * Sets the wizard page status message.
     * 
     * @since 4.2
     */
    void setPageStatus() {
        // Determine if any of the operations are checked
        boolean hasSelections = this.importManager.getSelectedOperations().size() > 0;
        if (!hasSelections) {
            WizardUtil.setPageComplete(this, getString("noSelections.msg"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        }

        WizardUtil.setPageComplete(this);

        getContainer().updateButtons();
    }

    @Override
    public void setVisible( boolean isVisible ) {
        if (isVisible) {
            try {
                this.wsdlModel = this.importManager.getWSDLModel();
            } catch (ModelGenerationException e) {
                this.wsdlModel = null;
                Status exStatus = new Status(IStatus.ERROR, PLUGIN_ID, 0, getString("dialog.wsdlParseError.msg"), e); //$NON-NLS-1$
                Shell shell = this.getShell();
                ErrorDialog.openError(shell, null, getString("dialog.wsdlParseError.title"), exStatus); //$NON-NLS-1$  
            }
            this.treeViewer.setInput(this.wsdlModel);
            this.importManager.setSelectedOperations(new ArrayList());
            this.treeViewer.expandToLevel(4);
            setAllNodesSelected(true);
            setPageStatus();
        }
        super.setVisible(isVisible);
    }

    private void setAllNodesSelected( boolean bSelected ) {
        // System.out.println("[CompareTreePanel.setAllNodesSelected] bSelected: " + bSelected ); //$NON-NLS-1$

        TreeItem[] items = tree.getItems();
        for (int i = 0; i < items.length; i++) {
            setAllSelected(items[i], bSelected);
        }
    }

    private void setAllSelected( final TreeItem item,
                                 final boolean checked ) {
        WidgetUtil.setChecked(item, checked, false, this.controller);

        // Apply same checked state to any children
        final TreeItem[] children = item.getItems();
        for (int ndx = 0; ndx < children.length; ndx++) {
            setAllSelected(children[ndx], checked);
        }
    }

    /**
     * Determine if the object has a 'valid' operation underneath it's heirarchy. Valid operation has 'canModel' set to true.
     */
    boolean hasValidOperation( Object object ) {
        boolean hasValid = false;
        if (object instanceof Model) {
            Object[] services = ((Model)object).getServices();
            for (int i = 0; i < services.length; i++) {
                if (hasValidOperation(services[i])) {
                    hasValid = true;
                    break;
                }
            }
        } else if (object instanceof Service) {
            Object[] ports = ((Service)object).getPorts();
            for (int i = 0; i < ports.length; i++) {
                if (hasValidOperation(ports[i])) {
                    hasValid = true;
                    break;
                }
            }
        } else if (object instanceof Port) {
            Object binding = ((Port)object).getBinding();
            if (hasValidOperation(binding)) {
                hasValid = true;
            }
        } else if (object instanceof Binding) {
            Object[] operations = ((Binding)object).getOperations();
            for (int i = 0; i < operations.length; i++) {
                if (hasValidOperation(operations[i])) {
                    hasValid = true;
                    break;
                }
            }
        } else if (object instanceof Operation) {
            hasValid = ((Operation)object).canModel();
        }
        return hasValid;
    }

    class CheckboxTreeLabelProvider extends LabelProvider {
        private final Image SERVICE_ICON_IMG = ModelGeneratorWsdlUiUtil.getImage(SERVICE_ICON);
        private final Image PORT_ICON_IMG = ModelGeneratorWsdlUiUtil.getImage(PORT_ICON);
        private final Image OPERATION_ICON_IMG = ModelGeneratorWsdlUiUtil.getImage(OPERATION_ICON);
        private final Image BINDING_ICON_IMG = ModelGeneratorWsdlUiUtil.getImage(BINDING_ICON);

        final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();

        @Override
        public Image getImage( final Object node ) {
            if (node instanceof Service) {
                return SERVICE_ICON_IMG;
            } else if (node instanceof Port) {
                return PORT_ICON_IMG;
            } else if (node instanceof Operation) {
                return OPERATION_ICON_IMG;
            } else if (node instanceof Binding) {
                return BINDING_ICON_IMG;
            }
            return null;
        }

        @Override
        public String getText( final Object node ) {
            if (node instanceof Model) {
                return "theModel"; //$NON-NLS-1$
            } else if (node instanceof WSDLElement) {
                return ((WSDLElement)node).getName();
            }
            return "unknownElement"; //$NON-NLS-1$
        }
    }

    class CheckboxTreeContentProvider implements ITreeContentProvider {

        public void dispose() {
        }

        public Object[] getChildren( final Object node ) {
            if (wsdlModel != null) {
                if (node instanceof Model) {
                    return ((Model)node).getServices();
                } else if (node instanceof Service) {
                    return ((Service)node).getPorts();
                } else if (node instanceof Port) {
                    return new Object[] {((Port)node).getBinding()};
                } else if (node instanceof Binding) {
                    return ((Binding)node).getOperations();
                }
            }
            return EMPTY_STRING_ARRAY;
        }

        public Object[] getElements( final Object inputElement ) {
            return getChildren(inputElement);
        }

        public Object getParent( final Object node ) {
            if (wsdlModel != null) {
                if (node instanceof Model) {
                    return null;
                } else if (node instanceof Service) {
                    return null;
                } else if (node instanceof Port) {
                    return ((Port)node).getService();
                } else if (node instanceof Binding) {
                    return ((Binding)node).getPort();
                } else if (node instanceof Operation) {
                    return ((Operation)node).getBinding();
                }
            }
            return null;
        }

        public boolean hasChildren( final Object node ) {
            if (wsdlModel != null) {
                if (node instanceof Model) {
                    return (((Model)node).getServices().length > 0);
                } else if (node instanceof Service) {
                    return (((Service)node).getPorts().length > 0);
                } else if (node instanceof Port) {
                    return ((Port)node).getBinding() != null;
                } else if (node instanceof Binding) {
                    return (((Binding)node).getOperations().length > 0);
                }
            }
            return false;
        }

        public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput ) {
        }
    }

    class CheckboxTreeController extends DefaultTreeViewerController {
        /**
         * @see com.metamatrix.ui.internal.widget.DefaultTreeViewerController#checkedStateToggled(org.eclipse.swt.widgets.TreeItem)
         */
        @Override
        public void checkedStateToggled( TreeItem item ) {
        }

        /**
         * @see com.metamatrix.ui.internal.widget.ITreeViewerController#isItemCheckable(org.eclipse.swt.widgets.TreeItem)
         */
        @Override
        public boolean isItemCheckable( final TreeItem item ) {
            final Object node = item.getData();
            if (node instanceof Service || node instanceof Binding || node instanceof Port || node instanceof Operation) {
                return hasValidOperation(node);
            }
            return false;
        }

        /**
         * @see com.metamatrix.ui.internal.widget.ITreeViewerController#update(org.eclipse.swt.widgets.TreeItem, boolean)
         * @since 4.0
         */
        @Override
        public void update( final TreeItem item,
                            final boolean selected ) {
            Object dataObj = item.getData();
            if (dataObj != null) {
                final boolean checked = !WidgetUtil.isUnchecked(item);
                if (isItemCheckable(item)) {
                    item.setChecked(checked);
                }
                item.setGrayed(WidgetUtil.isPartiallyChecked(item));

                if (selected) {
                    updateChildren(item, checked);
                    for (TreeItem parent = item.getParentItem(); parent != null; parent = parent.getParentItem()) {
                        int state = PARTIALLY_CHECKED;
                        final TreeItem[] children = parent.getItems();
                        for (int ndx = children.length; --ndx >= 0;) {
                            final TreeItem child = children[ndx];
                            if (WidgetUtil.isPartiallyChecked(child)) {
                                state = PARTIALLY_CHECKED;
                                break;
                            }
                            final int childState = WidgetUtil.getCheckedState(child);
                            if (state == PARTIALLY_CHECKED) {
                                state = childState;
                            } else if (state != childState) {
                                state = PARTIALLY_CHECKED;
                                break;
                            }
                        }
                        if (state != WidgetUtil.getCheckedState(parent)) {
                            WidgetUtil.setCheckedState(parent, state, false, this);
                        }
                    }
                }
                if (dataObj instanceof Operation && hasValidOperation(dataObj)) {
                    updateCheckedOperations((Operation)dataObj, checked);
                }
                if (!isItemCheckable(item)) {
                    item.setGrayed(true);
                    item.setChecked(false);
                } else {
                    item.setGrayed(false);
                }
            }
        }

        private void updateCheckedOperations( Operation operation,
                                              boolean checked ) {
            List selectedOperations = importManager.getSelectedOperations();
            if (checked && operation.canModel() && !selectedOperations.contains(operation)) {
                selectedOperations.add(operation);
                importManager.setSelectedOperations(selectedOperations);
                setPageStatus();
            } else if (!checked && selectedOperations.contains(operation)) {
                selectedOperations.remove(operation);
                importManager.setSelectedOperations(selectedOperations);
                setPageStatus();
            }
        }

        /**
         * @since 4.0
         */
        private void updateChildren( final TreeItem item,
                                     final boolean checked ) {
            final TreeItem[] children = item.getItems();
            for (int ndx = children.length; --ndx >= 0;) {
                final TreeItem child = children[ndx];
                if (child.getData() != null) {
                    updateChildren(child, checked);
                    WidgetUtil.setChecked(child, checked, false, this);
                }
            }
        }

        /**
         * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
         * @since 4.0
         */
        @Override
        public void itemExpanded( final TreeExpansionEvent event ) {
            if (treeExpanded) {
                super.itemExpanded(event);
            } else {
                final TreeItem item = ((TreeViewer)event.getTreeViewer()).getTree().getSelection()[0];
                if (item.getData() != null) {
                    updateChildren(item, false);
                }
                treeExpanded = true;
            }
            updateTreeSelectionDetails();
        }

        @Override
        public void itemCollapsed( final TreeExpansionEvent event ) {
            super.itemCollapsed(event);
            updateTreeSelectionDetails();
        }

    }
}
