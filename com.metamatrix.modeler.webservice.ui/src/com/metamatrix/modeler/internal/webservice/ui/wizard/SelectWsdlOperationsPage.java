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
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Operation;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.Service;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceResource;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
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
    implements Listener, FileUtils.Constants, StringUtil.Constants, IInternalUiConstants.Images {

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

    /** The import manager. */
    IWebServiceModelBuilder importBuilder;

    /** The WSDL model representation */
    private List wsdlDefinitions = null;
    HashMap definitionsLabelMap = new HashMap();

    private boolean initializing = false;

    /**
     * Constructs the page with the provided import manager
     * 
     * @param theImportManager the import manager object
     */
    public SelectWsdlOperationsPage( IWebServiceModelBuilder builder ) {
        super(SelectWsdlOperationsPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importBuilder = builder;
        setImageDescriptor(WebServiceUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
    }

    /**
     * Event handler
     * 
     * @param event the Event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {
            boolean validate = false;

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
        // $NON-NLS-1$
        createCheckboxTreeComposite(splitter, getString("checkboxTreeGroup.title")); //$NON-NLS-1$

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
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        saveState();
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return IInternalUiConstants.UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
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
        WizardUtil.setPageComplete(this);

        getContainer().updateButtons();
    }

    @Override
    public void setVisible( boolean isVisible ) {
        if (isVisible) {
            definitionsLabelMap.clear();

            this.wsdlDefinitions = getInputFromBuilder();

            this.treeViewer.setInput(this.wsdlDefinitions);
            this.importBuilder.setSelectedOperations(new ArrayList());

            this.treeViewer.expandToLevel(3);
            setAllNodesSelected(true);
            setPageStatus();
        }
        super.setVisible(isVisible);
    }

    private List getInputFromBuilder() {
        // Go through the WSDL models and obtain all root-level WSDL objects ...
        List allRoots = new ArrayList();

        final Collection resources = this.importBuilder.getWSDLResources();
        final Iterator iter = resources.iterator();
        while (iter.hasNext()) {
            final IWebServiceResource wsr = (IWebServiceResource)iter.next();

            // Load the WSD ...
            final Resource emfResource = this.importBuilder.getEmfResource(wsr);
            if (emfResource != null) {
                final List roots = emfResource.getContents();
                final Iterator rootIter = roots.iterator();
                while (rootIter.hasNext()) {
                    final EObject root = (EObject)rootIter.next();
                    if (root instanceof Definitions) {
                        allRoots.add(root);
                        definitionsLabelMap.put(root, "Definitions: " + emfResource.getURI()); //$NON-NLS-1$
                    }
                }
            }
        }

        return allRoots;
    }

    private void setAllNodesSelected( boolean bSelected ) {
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
        if (object instanceof Definitions) {
            List services = ((Definitions)object).getServices();
            for (Iterator iter = services.iterator(); iter.hasNext();) {
                if (hasValidOperation(iter.next())) {
                    hasValid = true;
                    break;
                }
            }
        } else if (object instanceof Service) {
            List ports = ((Service)object).getPorts();
            for (Iterator iter = ports.iterator(); iter.hasNext();) {
                if (hasValidOperation(iter.next())) {
                    hasValid = true;
                    break;
                }
            }
        } else if (object instanceof PortType) {
            List operations = ((PortType)object).getOperations();
            for (Iterator iter = operations.iterator(); iter.hasNext();) {
                if (hasValidOperation(iter.next())) {
                    hasValid = true;
                    break;
                }
            }
        } else if (object instanceof Operation) {
            hasValid = true;
            // hasValid = ((Operation)object).canModel();
        }
        return hasValid;
    }

    class CheckboxTreeLabelProvider extends LabelProvider {
        private final Image SERVICE_ICON_IMG = WebServiceUiPlugin.getDefault().getImage(SERVICE_ICON);
        private final Image PORT_ICON_IMG = WebServiceUiPlugin.getDefault().getImage(PORT_ICON);
        private final Image OPERATION_ICON_IMG = WebServiceUiPlugin.getDefault().getImage(OPERATION_ICON);
        // private final Image BINDING_ICON_IMG = WebServiceUiPlugin.getDefault().getImage(BINDING_ICON);

        final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();

        @Override
        public Image getImage( final Object node ) {
            if (node instanceof Definitions) {
                return SERVICE_ICON_IMG;
            } else if (node instanceof PortType) {
                return PORT_ICON_IMG;
            } else if (node instanceof Operation) {
                return OPERATION_ICON_IMG;
            }
            return null;
        }

        @Override
        public String getText( final Object node ) {
            if (node instanceof Definitions) {
                String theText = (String)definitionsLabelMap.get(node);
                if (theText == null) {
                    theText = "Definitions"; //$NON-NLS-1$
                }
                return theText;
            } else if (node instanceof PortType) {
                return ((PortType)node).getName();
            } else if (node instanceof Operation) {
                return ((Operation)node).getName();
            }

            return "unknownElement"; //$NON-NLS-1$
        }
    }

    class CheckboxTreeContentProvider implements ITreeContentProvider {
        public void dispose() {
        }

        public Object[] getChildren( final Object node ) {
            // if(wsdlModel!=null) {
            if (node instanceof Collection) {
                return ((Collection)node).toArray();
            } else if (node instanceof Definitions) {
                return ((Definitions)node).getPortTypes().toArray();
            } else if (node instanceof PortType) {
                return ((PortType)node).getOperations().toArray();
            }

            // }
            return EMPTY_STRING_ARRAY;
        }

        public Object[] getElements( final Object inputElement ) {
            return getChildren(inputElement);
        }

        public Object getParent( final Object node ) {
            // if(wsdlModel!=null) {
            if (node instanceof Definitions) {
                return null;
            }
            if (node instanceof PortType) {
                return ((PortType)node).eContainer();
            } else if (node instanceof Operation) {
                return ((Operation)node).eContainer();
            }
            // }
            return null;
        }

        public boolean hasChildren( final Object node ) {
            // if(wsdlModel!=null) {
            if (node instanceof Collection) {
                return !((Collection)node).isEmpty();
            } else if (node instanceof Definitions) {
                return !((Definitions)node).getPortTypes().isEmpty();
            } else if (node instanceof PortType) {
                return !((PortType)node).getOperations().isEmpty();
            }
            // }
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
            if (node instanceof Service || node instanceof PortType || node instanceof Operation) {
                return hasValidOperation(node);
            }
            return false;
        }

        /**
         * <p>
         * </p>
         * 
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
            Collection selectedOperations = importBuilder.getSelectedOperations();
            if (checked && !selectedOperations.contains(operation)) {
                selectedOperations.add(operation);
                importBuilder.setSelectedOperations(selectedOperations);
                setPageStatus();
            } else if (!checked && selectedOperations.contains(operation)) {
                selectedOperations.remove(operation);
                importBuilder.setSelectedOperations(selectedOperations);
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
        }

        @Override
        public void itemCollapsed( final TreeExpansionEvent event ) {
            super.itemCollapsed(event);
        }

    }
}
