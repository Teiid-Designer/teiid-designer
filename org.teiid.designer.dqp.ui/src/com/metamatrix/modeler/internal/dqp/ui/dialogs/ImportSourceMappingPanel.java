/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.common.vdb.ModelInfo;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.internal.config.ModelConnectorBindingMapperImpl;
import com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.dqp.ui.views.ConnectorBindingsTreeProvider;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.DefaultContentProvider;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * @since 4.3
 */
public final class ImportSourceMappingPanel extends BaseNewConnectorBindingPanel implements IChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ImportSourceMappingPanel.class);

    private int bindingIndex = -1;

    private List bindingPanelControls;

    private int typeIndex = -1;

    private List connectorTypePanelControls;

    private ModelSource importSource;

    private ModelConnectorBindingMapperImpl mapper;

    private Collection matchingBindings;

    private Collection matchingConnectorTypes;

    private ModelInfo modelInfo;

    private boolean newTypeCreated;

    private String pswd;

    private final InternalVdbEditingContext vdbContext;

    private Button btnExistingBinding;

    private Button btnCreateFromType;

    private Composite pnlCards;

    private Composite pnlMatchingBinding;

    private Composite pnlNewBinding;

    private StackLayout stackLayout;

    private TableViewer bindingsViewer;

    private TableViewer connectorTypesViewer;

    private Text txtBindingName;

    private Text txtPswd;

    /**
     * @param theParent
     * @throws IllegalArgumentException if any of the input parameters is <code>null</code>
     * @since 4.3
     */
    public ImportSourceMappingPanel( Composite theParent,
                                     InternalVdbEditingContext theContext,
                                     ModelInfo theModelInfo,
                                     ModelSource theImportSource ) {
        super(theParent);

        ArgCheck.isNotNull(theParent);
        ArgCheck.isNotNull(theContext);
        ArgCheck.isNotNull(theModelInfo);
        ArgCheck.isNotNull(theImportSource);

        this.vdbContext = theContext;
        this.modelInfo = theModelInfo;
        this.importSource = theImportSource;

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_BOTH));

        createContents();

        // register to receive notice of configuration changes
        DqpPlugin.getInstance().getAdmin().addChangeListener(this);
    }

    private void createContents() {
        final int VERTICAL_INDENT = 10;
        final int HORIZONTAL_INDENT = 20;

        //
        // get data for use in general description
        //

        Object url = ""; //$NON-NLS-1$
        Object user = ""; //$NON-NLS-1$
        Object driverType = ""; //$NON-NLS-1$

        List props = this.importSource.getProperties();
        ModelSourceProperty prop = null;

        for (int numProps = props.size(), j = 0; j < numProps; ++j) {
            prop = (ModelSourceProperty)props.get(j);

            if (prop.getName().equals(JDBCConnectionPropertyNames.JDBC_IMPORT_URL)) {
                url = prop.getValue();
            } else if (prop.getName().equals(JDBCConnectionPropertyNames.JDBC_IMPORT_USERNAME)) {
                user = prop.getValue();
            } else if (prop.getName().equals(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS)) {
                driverType = prop.getValue();
            }
        }

        //
        // create panel description label
        //

        StyledText lblGeneralDescription = new StyledText(this, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
        lblGeneralDescription.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblGeneralDescription.setText(UTIL.getString(PREFIX + "lblGeneralDescription", new Object[] {url, user, driverType})); //$NON-NLS-1$
        lblGeneralDescription.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        //
        // create collections used for enablement of controls
        //

        this.bindingPanelControls = new ArrayList();
        this.connectorTypePanelControls = new ArrayList();

        //
        // create assigne connector binding group
        //

        Group group = WidgetFactory.createGroup(this, getString("group.title"), GridData.FILL_BOTH); //$NON-NLS-1$

        //
        // create radio button panel
        //

        Composite pnlButtons = new Composite(group, SWT.NONE);
        pnlButtons.setLayout(new GridLayout(2, true));
        pnlButtons.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER));

        //
        // create radio button indicating an existing binding will be used.
        //

        this.btnExistingBinding = WidgetFactory.createRadioButton(pnlButtons, getString("btnExistingBinding")); //$NON-NLS-1$
        this.btnExistingBinding.setToolTipText(getString("btnExistingBinding.tip")); //$NON-NLS-1$
        this.btnExistingBinding.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleUseExistingBinding();
            }
        });

        //
        // create radio button indicating a new binding will be created based on a connector type
        //

        this.btnCreateFromType = WidgetFactory.createRadioButton(pnlButtons, getString("btnCreateFromType")); //$NON-NLS-1$
        this.btnCreateFromType.setToolTipText(getString("btnCreateFromType.tip")); //$NON-NLS-1$
        this.btnCreateFromType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleCreateBindingFromType();
            }
        });

        this.pnlCards = new Composite(group, SWT.NONE);
        this.pnlCards.setLayout(this.stackLayout = new StackLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        this.pnlCards.setLayoutData(gd);

        //
        // create matching bindings card content
        //

        this.pnlMatchingBinding = new Composite(this.pnlCards, SWT.NONE);
        this.pnlMatchingBinding.setLayout(new GridLayout());
        this.pnlMatchingBinding.setLayoutData(new GridData(GridData.FILL_BOTH));

        //
        // create description label for matching bindings
        //

        StyledText lblMatchingBindingDescription = new StyledText(this.pnlMatchingBinding, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
        lblMatchingBindingDescription.setText(getString("lblMatchingBindingDescription")); //$NON-NLS-1$
        lblMatchingBindingDescription.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalIndent = HORIZONTAL_INDENT;
        lblMatchingBindingDescription.setLayoutData(gd);

        //
        // create shared label provider for views
        //

        IBaseLabelProvider labelProvider = new ConnectorBindingsTreeProvider();

        //
        // create list containing existing bindings with matching properties
        //

        this.bindingsViewer = WidgetFactory.createTableViewer(this.pnlMatchingBinding, SWT.V_SCROLL | SWT.H_SCROLL);
        this.bindingsViewer.setLabelProvider(labelProvider);
        this.bindingsViewer.setContentProvider(new DefaultContentProvider() {
            @Override
            public Object[] getElements( Object theInputElement ) {
                return getBindings();
            }
        });
        this.bindingsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleBindingSelectionChanged();
            }
        });

        //
        // configure bindings viewer control
        //

        Control c = this.bindingsViewer.getControl();
        c.setToolTipText(getString("tblBindings.tip")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalIndent = HORIZONTAL_INDENT;
        gd.verticalIndent = VERTICAL_INDENT;
        c.setLayoutData(gd);

        this.bindingPanelControls.add(c);

        //
        // create new bindings card content
        //

        this.pnlNewBinding = new Composite(this.pnlCards, SWT.NONE);
        this.pnlNewBinding.setLayout(new GridLayout());
        this.pnlNewBinding.setLayoutData(new GridData(GridData.FILL_BOTH));

        //
        // create description label for matching connector types
        //

        StyledText lblMatchingConnectorTypeDescription = new StyledText(this.pnlNewBinding, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
        lblMatchingConnectorTypeDescription.setText(getString("lblMatchingConnectorTypeDescription")); //$NON-NLS-1$
        lblMatchingConnectorTypeDescription.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalIndent = HORIZONTAL_INDENT;
        lblMatchingConnectorTypeDescription.setLayoutData(gd);

        //
        // create list containing matching connector types
        //

        this.connectorTypesViewer = WidgetFactory.createTableViewer(this.pnlNewBinding, SWT.V_SCROLL | SWT.H_SCROLL);
        this.connectorTypesViewer.setLabelProvider(labelProvider);
        this.connectorTypesViewer.setContentProvider(new DefaultContentProvider() {
            @Override
            public Object[] getElements( Object theInputElement ) {
                return getConnectorTypes();
            }
        });
        this.connectorTypesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleConnectorTypeChanged();
            }
        });

        //
        // configure connector types viewer control
        //

        c = this.connectorTypesViewer.getControl();
        c.setToolTipText(getString("tblTypes.tip")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalIndent = HORIZONTAL_INDENT;
        gd.verticalIndent = VERTICAL_INDENT;
        c.setLayoutData(gd);

        this.connectorTypePanelControls.add(c);

        //
        // create panel to group label/fields of name and password
        //

        Composite pnl = WidgetFactory.createPanel(this.pnlNewBinding, SWT.NONE, GridData.FILL_HORIZONTAL, 1, 2);

        //
        // create binding name label
        //

        CLabel lblBindingName = WidgetFactory.createLabel(pnl, getString("lblBindingName")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalIndent = HORIZONTAL_INDENT;
        gd.horizontalAlignment = SWT.RIGHT;
        lblBindingName.setLayoutData(gd);

        this.connectorTypePanelControls.add(lblBindingName);

        //
        // create binding name field
        //

        this.txtBindingName = WidgetFactory.createTextField(pnl);
        this.txtBindingName.setToolTipText(getString("txtBindingName.tip")); //$NON-NLS-1$
        this.txtBindingName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.txtBindingName.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleBindingNameChanged();
            }
        });

        this.connectorTypePanelControls.add(this.txtBindingName);

        //
        // create password label
        //

        CLabel lblPswd = WidgetFactory.createLabel(pnl, getString("lblPswd")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalIndent = HORIZONTAL_INDENT;
        gd.horizontalAlignment = SWT.RIGHT;
        lblPswd.setLayoutData(gd);

        this.connectorTypePanelControls.add(lblPswd);

        //
        // create password field
        //

        this.txtPswd = WidgetFactory.createPasswordField(pnl);
        this.txtPswd.setToolTipText(getString("txtPswd.tip")); //$NON-NLS-1$
        this.txtPswd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.txtPswd.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handlePasswordChanged();
            }
        });

        this.connectorTypePanelControls.add(this.txtPswd);

        // initialize state
        stateChanged(this);

        // add dispose listener because when I overrode dispose() it never got called
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent theEvent ) {
                handleDispose();
            }
        });
    }

    Object[] getBindings() {
        Collection result = this.matchingBindings;

        return (result == null) ? new Object[] {getString("noMatchingBindings")} //$NON-NLS-1$
        : this.matchingBindings.toArray();
    }

    TableViewer getBindingsViewer() {
        return this.bindingsViewer;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.dialogs.BaseNewConnectorBindingPanel#getConnector()
     * @since 4.3
     */
    @Override
    public Connector getConnector() throws Exception {
        Connector result = null;
        IStatus status = getStatus();

        if (status.getSeverity() != IStatus.ERROR) {
            if (this.btnExistingBinding.getSelection() && (this.matchingBindings != null) && !this.matchingBindings.isEmpty()) {
                IStructuredSelection selection = (IStructuredSelection)this.bindingsViewer.getSelection();
                result = (Connector)selection.getFirstElement();
            } else if (this.btnCreateFromType.getSelection() && (this.matchingConnectorTypes != null)
                       && !this.matchingConnectorTypes.isEmpty()) {
                IStructuredSelection selection = (IStructuredSelection)this.connectorTypesViewer.getSelection();
                ModelReference modelRef = getModelReference();
                result = this.mapper.createConnectorBinding(modelRef,
                                                            (ConnectorType)selection.getFirstElement(),
                                                            getNewBindingName());
            }
        }

        return result;
    }

    private ModelReference getModelReference() {
        return VdbDefnHelper.findModelReference(this.vdbContext, this.modelInfo);
    }

    Object[] getConnectorTypes() {
        Collection result = this.matchingConnectorTypes;

        return (result == null) ? new Object[] {getString("noMatchingConnectorTypes")} //$NON-NLS-1$
        : this.matchingConnectorTypes.toArray();
    }

    TableViewer getConnectorTypesViewer() {
        return this.connectorTypesViewer;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.dialogs.BaseNewConnectorBindingPanel#getI18nPrefix()
     * @since 4.3
     */
    @Override
    protected String getI18nPrefix() {
        return PREFIX;
    }

    private String getNewBindingName() {
        return this.txtBindingName.getText();
    }

    /**
     * Obtains the current value of the password. <strong>Must only be called when in a "new binding" state.
     * 
     * @return the password
     * @throws IllegalStateException if not in a new binding state
     * @since 4.3
     * @see #isNewBindingState()
     */
    public String getPassword() {
        if (this.isNewBindingState()) {
            return this.pswd;
        }

        // this is more of an assertion as this method should never be called when not in proper state
        throw new IllegalStateException("Panel not in new binding state"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.dialogs.BaseNewConnectorBindingPanel#getStatus()
     * @since 4.3
     */
    @Override
    protected IStatus getStatus() {
        int severity = IStatus.ERROR;
        String msg = "Message has not been set"; //$NON-NLS-1$

        if (this.btnExistingBinding.getSelection() && !this.bindingsViewer.getSelection().isEmpty()) {
            // valid
            severity = IStatus.OK;
            msg = UTIL.getString(PREFIX + "okMsg.binding", //$NON-NLS-1$
                                 ((IStructuredSelection)this.bindingsViewer.getSelection()).getFirstElement());
        } else if (this.btnCreateFromType.getSelection() && !this.connectorTypesViewer.getSelection().isEmpty()) {
            // validate name first
            IStatus result = ModelerDqpUtils.isValidBindingName(getNewBindingName());

            if (result.getSeverity() != IStatus.ERROR) {
                VdbDefnHelper helper = getVdbDefnHelper();

                if (ModelerDqpUtils.isUniqueBindingName(getNewBindingName(), helper.getVdbDefn())) {
                    severity = IStatus.OK;
                    msg = UTIL.getString(PREFIX + "okMsg.connectorType", //$NON-NLS-1$
                                         new Object[] {getNewBindingName(),
                                             ((IStructuredSelection)this.connectorTypesViewer.getSelection()).getFirstElement()});
                } else {
                    // binding with that name already exists
                    msg = UTIL.getString(PREFIX + "bindingNameExists", getNewBindingName()); //$NON-NLS-1$
                }
            } else {
                msg = result.getMessage();
            }
        } else {
            // invalid
            msg = getString("errorMsg"); //$NON-NLS-1$
        }

        return BaseNewConnectorBindingPanel.createStatus(severity, msg);
    }

    /**
     * @return
     * @since 5.0
     */
    private VdbDefnHelper getVdbDefnHelper() {
        return DqpPlugin.getInstance().getVdbDefnHelper(this.vdbContext);
    }

    /**
     * Alert listeners of a state change.
     * 
     * @since 4.3
     */
    void handleBindingSelectionChanged() {
        if (this.btnExistingBinding.getSelection()) {
            this.bindingIndex = this.bindingsViewer.getTable().getSelectionIndex();

            fireChangeEvent();
        }
    }

    void handleConnectorTypeChanged() {
        if (this.btnCreateFromType.getSelection()) {
            this.typeIndex = this.connectorTypesViewer.getTable().getSelectionIndex();
            this.txtPswd.setEnabled(/*this.btnCreateFromType.getSelection() &&*/!this.connectorTypesViewer.getSelection().isEmpty());

            populateBindingName();

            fireChangeEvent();
        }
    }

    void handleBindingNameChanged() {
        fireChangeEvent();
    }

    void handleCreateBindingFromType() {
        if (this.btnCreateFromType.getSelection()) {
            // show pnlMatchingBinding card
            this.stackLayout.topControl = this.pnlNewBinding;
            this.pnlCards.layout();

            this.newTypeCreated = true;
            this.btnExistingBinding.setSelection(false);

            // restore selection state in bindings viewer
            if (this.typeIndex == -1) {
                this.typeIndex = 0;
            }

            this.connectorTypesViewer.getTable().setSelection(this.typeIndex);

            // save selected binding index and then remove selection from binding viewer
            this.bindingIndex = this.bindingsViewer.getTable().getSelectionIndex();
            this.bindingsViewer.getTable().deselectAll();

            populateBindingName();

            // alert listeners of a change in state
            fireChangeEvent();
        }
    }

    /**
     * Cleanup for when disposed.
     * 
     * @since 5.0
     */
    void handleDispose() {
        DqpPlugin.getInstance().getAdmin().removeChangeListener(this);
    }

    void handlePasswordChanged() {
        this.pswd = this.txtPswd.getText();
    }

    void handleUseExistingBinding() {
        if (this.btnExistingBinding.getSelection()) {
            // show pnlMatchingBinding card
            this.stackLayout.topControl = this.pnlMatchingBinding;
            this.pnlCards.layout();

            this.newTypeCreated = false;
            this.btnCreateFromType.setSelection(false);

            // restore selection state in bindings viewer or select first row if no previous selection
            if (this.bindingIndex == -1) {
                this.bindingIndex = 0;
            }

            this.bindingsViewer.getTable().setSelection(this.bindingIndex);

            // save selected connector type index and then remove selection from connector type viewer
            this.typeIndex = this.connectorTypesViewer.getTable().getSelectionIndex();
            this.connectorTypesViewer.getTable().deselectAll();

            // alert listeners of a change in state
            fireChangeEvent();
        }
    }

    /**
     * Indicates if the panel is in a state that a new binding will/has been created. The password should only be used when in a
     * new binding state.
     * 
     * @return <code>true</code> if new binding state; <code>false</code> if an existing binding state.
     * @since 4.3
     */
    public boolean isNewBindingState() {
        return this.newTypeCreated;
    }

    private void populateBindingName() {
        // if needed repopulate the name to the default name
        if (getNewBindingName().length() == 0) {
            this.txtBindingName.setText(ModelerDqpUtils.createNewConnectorName(this.modelInfo));
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     */
    public void stateChanged( final IChangeNotifier theSource ) {
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                handleStateChanged(theSource);
            }
        }, false);
    }

    /**
     * Fires when the ConfigurationManager changes
     * 
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 4.3
     */
    void handleStateChanged( IChangeNotifier theSource ) {
        if ((this.bindingsViewer == null) || this.bindingsViewer.getTable().isDisposed()) {
            return;
        }

        // reload the state
        try {
            this.mapper = new ModelConnectorBindingMapperImpl(this.vdbContext);
            ModelReference modelRef = getModelReference();

            // get matching bindings
            this.matchingBindings = mapper.findConnectorBindingMatches(modelRef);

            // get matching connector types
            this.matchingConnectorTypes = mapper.findConnectorTypeMatches(modelRef);
        } catch (Exception theException) {
            UTIL.log(theException);
            theException.printStackTrace();
        }

        // initial enablement of matching bindings controls
        if ((this.matchingBindings == null) || this.matchingBindings.isEmpty()) {
            this.btnExistingBinding.setSelection(false);
            this.btnExistingBinding.setEnabled(false);
        } else {
            final Button btn = this.btnExistingBinding;

            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if (!btn.isDisposed()) {
                        // select radio button and notify listeners
                        btn.setSelection(true);
                        Event event = new Event();
                        event.widget = btn;
                        btn.notifyListeners(SWT.Selection, event);

                        // select first row in table and notify listeners
                        Table tbl = getBindingsViewer().getTable();
                        tbl.setSelection(0);
                        event = new Event();
                        event.widget = tbl;
                        tbl.notifyListeners(SWT.Selection, event);
                    }
                }
            });
        }

        // initial enablement of matching connector type controls
        if ((this.matchingConnectorTypes == null) || this.matchingConnectorTypes.isEmpty()) {
            this.btnCreateFromType.setSelection(false);
            this.btnCreateFromType.setEnabled(false);
        } else {
            final Button bindingBtn = this.btnExistingBinding;
            final Button typeBtn = this.btnCreateFromType;

            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if (!bindingBtn.isDisposed() && !bindingBtn.getSelection()) {
                        // select radio button and notify listeners
                        typeBtn.setSelection(true);
                        Event event = new Event();
                        event.widget = typeBtn;
                        typeBtn.notifyListeners(SWT.Selection, event);

                        // select first row in table and notify listeners
                        Table tbl = getConnectorTypesViewer().getTable();
                        tbl.setSelection(0);
                        event = new Event();
                        event.widget = tbl;
                        tbl.notifyListeners(SWT.Selection, event);
                    }
                }
            });
        }

        // populate the tables
        this.bindingsViewer.setInput(this);
        this.connectorTypesViewer.setInput(this);
    }

}
