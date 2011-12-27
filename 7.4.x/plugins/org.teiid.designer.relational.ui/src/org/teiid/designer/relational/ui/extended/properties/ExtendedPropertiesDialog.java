/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.extended.properties;

import static com.metamatrix.modeler.relational.ui.UiConstants.Util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelObjectAnnotationHelper;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.relational.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 7.4
 */
public class ExtendedPropertiesDialog extends TitleAreaDialog {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExtendedPropertiesDialog.class);
    private static final String TITLE = Util.getString(I18N_PREFIX + "title"); //$NON-NLS-1$
    private static final String TITLE_READ_ONLY = Util.getString(I18N_PREFIX + "titleReadOnly"); //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private final EObject theEObject;
    private final String theEObjectName;
    private final boolean modelResourceReadOnly;
    protected Properties workingprops = null;
    private Shell parent;
    private TableViewer propertiesViewer;
    // This represents the state of properties when the dialog is first opened
    private Properties initialExtendedProperties = null;
    // This represents the working state of extended properties as the user
    // add/removes properties
    private Properties workingExtendedProperties = null;
    /**
     * Action to add a custom property.
     */
    private IAction addPropertyAction;

    /**
     * Action to remove all properties
     */
    private IAction deleteAllPropertyAction;

    /**
     * Action to delete a property
     */
    private IAction deletePropertyAction;

    /**
     * Action to restore all properties to
     */
    private IAction restorePropertyAction;

    public static ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();

    /**
     * @param parent
     * @param title
     * @since 7.4
     */
    public ExtendedPropertiesDialog( Shell parent,
                                     EObject theEObject,
                                     boolean modelResourceReadOnly ) {

        super(parent);

        this.parent = parent;
        this.modelResourceReadOnly = modelResourceReadOnly;
        this.theEObject = theEObject;
        this.theEObjectName = SqlAspectHelper.getSqlAspect(theEObject).getFullName(theEObject);

        // Initialize extended properties
        try {
            this.workingExtendedProperties = ANNOTATION_HELPER.getExtendedProperties(this.theEObject);
            this.initialExtendedProperties = ANNOTATION_HELPER.getExtendedProperties(this.theEObject);
        } catch (ModelerCoreException e) {
            UiConstants.Util.log(e);
        }

    }

    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        if (this.modelResourceReadOnly) {
            shell.setText(TITLE_READ_ONLY);
        } else {
            shell.setText(TITLE);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        super.createButtonsForButtonBar(parent);
        // if model is read only, disable ok button
        Button okButton = null;
        if (this.modelResourceReadOnly) {
            okButton = getButton(IDialogConstants.OK_ID);
            if (okButton != null) okButton.setEnabled(false);
        }

    }

    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

    }

    @Override
    protected void okPressed() {
        // If user clicks okay, remove properties that existed in the initial state
        // and add properties from the working state. We will only do this if the properties
        // have changed from the original state.

        if (currentStateDifferentFromInitalState()) {

            TableItem[] test = propertiesViewer.getTable().getItems();
            Properties workingExtendedProperties = new Properties();
            for (TableItem item : test) {
                ExtendedProperty property = (ExtendedProperty)item.getData();
                workingExtendedProperties.put(property.getDefinition().getDisplayName(), property.getValue());
            }

            try {
                for (String key : this.initialExtendedProperties.stringPropertyNames()) {
                    ANNOTATION_HELPER.removeProperty(this.theEObject, key);
                }
                ANNOTATION_HELPER.addProperties(this.theEObject, workingExtendedProperties);
                this.theEObject.eResource().setModified(true);
            } catch (ModelerCoreException e) {
                UiConstants.Util.log(e);
                throw new RuntimeException();
            }
        }

        super.okPressed();

    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite contents = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1);

        this.setTitle(Util.getString(I18N_PREFIX + "messageTitle")); //$NON-NLS-1$
        this.setMessage(this.theEObjectName);
        this.setTitleImage(UiPlugin.getDefault().getImage(UiConstants.Images.MANAGE_EXTENDED_PROPERTIES_ICON));
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.propertiesViewer = new TableViewer(contents, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
        ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
        this.propertiesViewer.setContentProvider(new IStructuredContentProvider() {
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
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                Set<ExtendedProperty> properties = getExtendedProperties();

                if (properties == null) {
                    return new Object[0];
                }

                return properties.toArray();
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
                // nothing to do
            }
        });

        // sort the table rows by display name
        this.propertiesViewer.setComparator(new ViewerComparator() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public int compare( Viewer viewer,
                                Object e1,
                                Object e2 ) {
                ExtendedProperty prop1 = (ExtendedProperty)e1;
                ExtendedProperty prop2 = (ExtendedProperty)e2;

                return super.compare(viewer, prop1.getDefinition().getDisplayName(), prop2.getDefinition().getDisplayName());
            }
        });

        final Table table = this.propertiesViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)table.getLayoutData()).horizontalSpan = 2;

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(Util.getString(I18N_PREFIX + "propertyColumn.text")); //$NON-NLS-1$
        column.setLabelProvider(new PropertyNameLabelProvider(true));
        column.getColumn().pack();

        column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(Util.getString(I18N_PREFIX + "valueColumn.text")); //$NON-NLS-1$
        column.setLabelProvider(new PropertyValueLabelProvider());
        column.getColumn().pack();

        try {
            column.setEditingSupport(new ExtendedPropertyEditingSupport(this.propertiesViewer,
                                                                        ModelUtil.getModel(this.theEObject).getResource()));
        } catch (ModelWorkspaceException e) {
            UiConstants.Util.log(e);
            throw new RuntimeException(e);
        }

        column.getColumn().pack();

        this.propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handlePropertySelected(event);
            }
        });

        //
        // add toolbar below the table
        //

        ToolBar toolBar = new ToolBar(parent, SWT.PUSH | SWT.BORDER);
        ToolBarManager toolBarMgr = new ToolBarManager(toolBar);

        //
        // add the add custom property action to the toolbar
        //

        this.addPropertyAction = new Action(Util.getString(I18N_PREFIX + "addPropertyAction.text"), SWT.BORDER) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleAddProperty();
            }
        };

        this.addPropertyAction.setToolTipText(Util.getString(I18N_PREFIX + "addPropertyAction.toolTip")); //$NON-NLS-1$
        if (this.modelResourceReadOnly) {
            this.addPropertyAction.setEnabled(false);
        } else {
            this.addPropertyAction.setEnabled(true);
        }

        toolBarMgr.add(this.addPropertyAction);

        //
        // add the delete custom property action to the toolbar
        //

        this.deletePropertyAction = new Action(Util.getString(I18N_PREFIX + "removePropertyAction.text"), SWT.BORDER) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handlePropertyRemoved();
            }
        };

        this.deletePropertyAction.setToolTipText(Util.getString(I18N_PREFIX + "removePropertyAction.toolTip")); //$NON-NLS-1$
        this.deletePropertyAction.setEnabled(false);
        toolBarMgr.add(new Separator());
        toolBarMgr.add(this.deletePropertyAction);

        //
        // add the delete all custom property action to the toolbar
        //

        this.deleteAllPropertyAction = new Action(Util.getString(I18N_PREFIX + "removeAllPropertyAction.text"), SWT.BORDER) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleAllPropertiesRemoved();
            }
        };

        this.deleteAllPropertyAction.setToolTipText(Util.getString(I18N_PREFIX + "removeAllPropertyAction.toolTip")); //$NON-NLS-1$
        this.deleteAllPropertyAction.setEnabled(false);
        toolBarMgr.add(new Separator());
        toolBarMgr.add(this.deleteAllPropertyAction);

        //
        // add the restore properties action to the toolbar
        //

        this.restorePropertyAction = new Action(Util.getString(I18N_PREFIX + "restorePropertyAction.text"), SWT.BORDER) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleRestoreProperties();
            }
        };

        this.restorePropertyAction.setToolTipText(Util.getString(I18N_PREFIX + "restorePropertyAction.toolTip")); //$NON-NLS-1$
        this.restorePropertyAction.setEnabled(false);
        toolBarMgr.add(new Separator());
        toolBarMgr.add(this.restorePropertyAction);

        // update toolbar to show all actions
        toolBarMgr.update(true);

        // populate with data from model object
        this.propertiesViewer.setInput(this.theEObject);
        WidgetUtil.pack(this.propertiesViewer);
        return contents;
    }

    ExtendedProperty getSelectedExtendedProperty() {
        ISelection selection = this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (ExtendedProperty)((IStructuredSelection)selection).getFirstElement();
    }

    public boolean currentStateDifferentFromInitalState() {

        Properties currentStateProperties = getCurrentState();

        if (currentStateProperties.size() != this.initialExtendedProperties.size()) return true;

        String currentValue;
        String initialValue;

        for (Object key : currentStateProperties.keySet()) {
            if (this.workingExtendedProperties.containsKey(key)) {
                currentValue = this.initialExtendedProperties.getProperty((String)key);
                initialValue = currentStateProperties.getProperty((String)key);
            } else {
                return true;
            }

            if (!currentValue.equals(initialValue)) {
                return true;
            }
        }

        return false;

    }

    private Properties getCurrentState() {
        Properties properties = new Properties();

        TableItem[] itemArray = this.propertiesViewer.getTable().getItems();
        for (TableItem item : itemArray) {
            ExtendedProperty property = (ExtendedProperty)item.getData();
            String key = property.getDefinition().getDisplayName();
            properties.setProperty(key, property.getValue());
        }

        return properties;

    }

    Set<ExtendedProperty> getExtendedProperties() {
        ExtendedProperty extendedProperty = null;

        // Need to update the workingExtendedProperties in case the value was changed in the propertiesViewer.
        TableItem[] itemArray = this.propertiesViewer.getTable().getItems();
        for (TableItem item : itemArray) {
            ExtendedProperty property = (ExtendedProperty)item.getData();
            String key = property.getDefinition().getDisplayName();
            Object workingProp = this.workingExtendedProperties.get(key);
            // workingProp would be null if it was removed from the propertiesViewer
            if (workingProp != null) {
                this.workingExtendedProperties.setProperty(key, property.getValue());
            }
        }

        Set<ExtendedProperty> propertiesSet = new HashSet<ExtendedProperty>();
        for (String key : this.workingExtendedProperties.stringPropertyNames()) {
            String value = (String)this.workingExtendedProperties.get(key);
            extendedProperty = new ExtendedProperty(new ExtendedPropertyDefinition(key, value), value);
            propertiesSet.add(extendedProperty);
        }

        if (propertiesSet.size() > 0) {
            this.deleteAllPropertyAction.setEnabled(true);
        } else {
            this.deleteAllPropertyAction.setEnabled(false);
        }

        return propertiesSet;
    }

    private List<String> getExtendedPropertyNames() {
        List<String> names = new ArrayList<String>(getExtendedProperties().size());

        for (ExtendedProperty property : getExtendedProperties()) {
            names.add(property.getDefinition().getNameWithoutNamespace());
        }

        return names;
    }

    void handleAddProperty() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        AddExtendedPropertyDialog dialog = new AddExtendedPropertyDialog(getShell(), this.theEObjectName,
                                                                         getExtendedPropertyNames());

        if (dialog.open() == Window.OK) {
            // update model
            ExtendedProperty property = dialog.getProperty();
            this.workingExtendedProperties.put(ExtendedPropertyDefinition.EXT_PROPERTY_NAMESPACE
                                               + property.getDefinition().getDisplayName(), property.getValue());

            // update UI from model
            this.propertiesViewer.refresh();
            WidgetUtil.pack(this.propertiesViewer);

            // select the new property
            ExtendedPropertyDefinition propDefn = property.getDefinition();

            for (ExtendedProperty prop : getExtendedProperties()) {
                if (prop.getDefinition().equals(propDefn)) {
                    this.propertiesViewer.setSelection(new StructuredSelection(prop), true);
                    break;
                }
            }

            this.restorePropertyAction.setEnabled(currentStateDifferentFromInitalState());
        }
    }

    void handlePropertyRemoved() {
        ExtendedProperty selectedProperty = getSelectedExtendedProperty();
        assert (selectedProperty != null);

        this.workingExtendedProperties.remove(selectedProperty.getDefinition().getDisplayName());

        this.restorePropertyAction.setEnabled(currentStateDifferentFromInitalState());
        // update UI
        this.propertiesViewer.refresh();
        WidgetUtil.pack(this.propertiesViewer);
    }

    void handleAllPropertiesRemoved() {

        boolean cont = false;
        cont = MessageDialog.openConfirm(this.parent.getShell(), UiConstants.Util.getString(I18N_PREFIX + "confirmRemove"), //$NON-NLS-1$
                                         UiConstants.Util.getString(I18N_PREFIX + "confirmRemoveMessage")); //$NON-NLS-1$

        if (cont) {
            this.workingExtendedProperties = new Properties();

            this.restorePropertyAction.setEnabled(true);
            // update UI
            this.propertiesViewer.refresh();
            WidgetUtil.pack(this.propertiesViewer);
        }

        this.restorePropertyAction.setEnabled(currentStateDifferentFromInitalState());
    }

    void handleRestoreProperties() {

        boolean cont = false;
        cont = MessageDialog.openConfirm(this.parent.getShell(), UiConstants.Util.getString(I18N_PREFIX + "confirmRestore"), //$NON-NLS-1$
                                         UiConstants.Util.getString(I18N_PREFIX + "confirmRestoreMessage")); //$NON-NLS-1$

        if (cont) {
            try {
                this.propertiesViewer.getTable().removeAll();
                this.workingExtendedProperties.clear();
                this.workingExtendedProperties = ANNOTATION_HELPER.getExtendedProperties(this.theEObject);
            } catch (ModelerCoreException e) {

            }

            this.restorePropertyAction.setEnabled(false);

            // update UI
            this.propertiesViewer.refresh();
            WidgetUtil.pack(this.propertiesViewer);
        }
    }

    void handlePropertySelected( SelectionChangedEvent event ) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();

        if (selection.isEmpty()) {
            if (this.deletePropertyAction.isEnabled()) {
                this.deletePropertyAction.setEnabled(false);
            }
        } else {
            this.deletePropertyAction.setEnabled(true);
        }

        this.restorePropertyAction.setEnabled(currentStateDifferentFromInitalState());
    }

    class PropertyNameLabelProvider extends ColumnLabelProvider {

        private final boolean nameColumn;

        public PropertyNameLabelProvider( boolean nameColumn ) {
            this.nameColumn = nameColumn;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            ExtendedProperty property = (ExtendedProperty)element;

            if (this.nameColumn) {
                return property.getDefinition().getNameWithoutNamespace();
            }

            return (property.getDefinition().getDefaultValue());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            ExtendedProperty property = (ExtendedProperty)element;

            return property.getDefinition().isValidValue(property.getValue());
        }
    }

    class PropertyValueLabelProvider extends ColumnLabelProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            ExtendedProperty extendedProperty = (ExtendedProperty)element;
            return extendedProperty.getValue();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            ExtendedProperty extendedProperty = (ExtendedProperty)element;
            return extendedProperty.getDefinition().getDescription();
        }
    }

}
