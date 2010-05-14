/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorTemplate;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.IConnectorProperties;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.dqp.ui.workspace.RuntimePropertySourceProvider;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 *
 */
public class ConnectionFactoryPanel extends Composite implements IChangeNotifier, IPropertyChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ConnectionFactoryPanel.class);

    private final ExecutionAdmin admin;
    private final IChangeListener changeListener;
    private ConnectorTemplate connector;
    private final List<ConnectorType> connectorTypes;
    private ConnectorType selectedType;
    private boolean creatingNewConnector = true;
    private RuntimePropertySourceProvider sourceProvider;

    // widgets
    private Button btnShowExpertProps;
    private Combo cbxConnectorTypes;
    private PropertySheetPage propertyPage;
    private Text txtName; // name of new connector

    /**
     * @param parent this panel's container
     * @param admin the server's execution admin (never <code>null</code>)
     * @param changeListener the listener being registered to receive an event when panel state has changed
     */
    private ConnectionFactoryPanel( Composite parent,
                                    ExecutionAdmin admin,
                                    IChangeListener changeListener ) {
        super(parent, SWT.NONE);
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$

        this.admin = admin;
        this.connectorTypes = new ArrayList<ConnectorType>(this.admin.getConnectorTypes());
        this.changeListener = changeListener;

        // layout
        setLayout(new GridLayout());
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    /**
     * @param parent this panel's container
     * @param connector the connector being edited (never <code>null</code>)
     * @param changeListener the listener being registered to receive an event when panel state has changed
     */
    public ConnectionFactoryPanel( Composite parent,
                                   Connector connector,
                                   IChangeListener changeListener ) {
        this(parent, connector.getType().getAdmin(), changeListener);
        this.creatingNewConnector = false;
        this.selectedType = connector.getType();
        this.connector = new ConnectorTemplate(connector);
        createContents();

        // update the property page
        refreshPropertyPage(new StructuredSelection(this.connector));
    }

    /**
     * @param parent this panel's container
     * @param admin the server's execution admin (never <code>null</code>)
     * @param type the initial connector type (can be <code>null</code>)
     * @param changeListener the listener being registered to receive an event when panel state has changed
     */
    public ConnectionFactoryPanel( Composite parent,
                                   ExecutionAdmin admin,
                                   ConnectorType type,
                                   IChangeListener changeListener ) {
        this(parent, admin, changeListener);
        this.selectedType = type;
        createContents();

        // update the property page
        connectorTypeChanged();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @throws UnsupportedOperationException if called
     */
    @Override
    public void addChangeListener( IChangeListener theListener ) {
        // only one change listener allowed
        throw new UnsupportedOperationException();
    }

    void connectorTypeChanged() {
        if (this.creatingNewConnector) {
            int index = this.cbxConnectorTypes.getSelectionIndex();
            this.selectedType = (index == -1) ? null : this.connectorTypes.get(index);
        } else {
            this.selectedType = this.connector.getType();
        }

        // change properties to match new type and let listener know of the type change
        refreshPropertyPage();
        fireChangeEvent();
    }

    private void createContents() {
        // create UI
        createNameAndType(this);
        createProperties(this);
    }

    private void createNameAndType( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(2, false));
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        WidgetFactory.createLabel(panel, UTIL.getString(PREFIX + "lblName")); //$NON-NLS-1$

        if (this.creatingNewConnector) {
            this.txtName = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL);
            this.txtName.setText(UTIL.getString(PREFIX + "initialName")); //$NON-NLS-1$
            this.txtName.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                public void modifyText( ModifyEvent e ) {
                    handleNameChanged();
                }
            });
        } else {
            WidgetFactory.createLabel(panel, this.connector.getName());
        }

        WidgetFactory.createLabel(panel, UTIL.getString(PREFIX + "lblType")); //$NON-NLS-1$

        if (this.creatingNewConnector) {
            this.cbxConnectorTypes = WidgetFactory.createCombo(panel, SWT.READ_ONLY, GridData.FILL_HORIZONTAL);

            this.cbxConnectorTypes.addSelectionListener(new SelectionListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                public void widgetDefaultSelected( SelectionEvent e ) {
                    widgetSelected(e);
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                public void widgetSelected( SelectionEvent e ) {
                    connectorTypeChanged();
                }
            });

            // load combo
            Collections.sort(this.connectorTypes);
            ILabelProvider lp = new LabelProvider() {
                @Override
                public String getText( Object element ) {
                    if (element != null) {
                        return ((ConnectorType)element).getName();
                    }

                    return null;
                }
            };

            String name = (this.selectedType == null ? this.connectorTypes.get(0).getName() : this.selectedType.getName());
            WidgetUtil.setComboItems(this.cbxConnectorTypes, this.connectorTypes, lp, false, name);
            this.cbxConnectorTypes.setVisibleItemCount(Math.min(10, this.connectorTypes.size()));
        } else {
            WidgetFactory.createLabel(panel, this.selectedType.getName());
        }
    }

    private void createProperties( Composite parent ) {
        Group propertyGroup = WidgetFactory.createGroup(parent, UTIL.getString(PREFIX + "lblProperties")); //$NON-NLS-1$
        propertyGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // toggle button to show/hide expert properties
        this.btnShowExpertProps = new Button(propertyGroup, SWT.CHECK);
        this.btnShowExpertProps.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        this.btnShowExpertProps.setText(UTIL.getString(PREFIX + "btnShowExpertProps.text")); //$NON-NLS-1$
        this.btnShowExpertProps.setToolTipText(UTIL.getString(PREFIX + "btnShowExpertProps.tooTip")); //$NON-NLS-1$
        this.btnShowExpertProps.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theE ) {
                handleShowPropertiesSelected();
            }
        });

        this.sourceProvider = new RuntimePropertySourceProvider();
        this.sourceProvider.addPropertyChangeListener(this);
        this.sourceProvider.setEditable(true);

        this.propertyPage = new PropertySheetPage();
        this.propertyPage.createControl(propertyGroup);
        this.propertyPage.setPropertySourceProvider(this.sourceProvider);
        this.propertyPage.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // status line for error message
        final StatusLineManager slMgr = new StatusLineManager();
        Control c = slMgr.createControl(propertyGroup);
        c.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        this.propertyPage.makeContributions(new MenuManager(), new ToolBarManager(), slMgr);
        this.propertyPage.getControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent e ) {
                slMgr.setMessage(null);
            }
        });
    }

    /**
     * Notifies change listener of state change.
     */
    private void fireChangeEvent() {
        this.changeListener.stateChanged(this);
    }

    /**
     * @return the connector being created or modified (can be <code>null</code> if called when status is not OK)
     */
    public ConnectorTemplate getConnector() {
        return this.connector;
    }

    public IStatus getStatus() {
        IStatus result = ModelerDqpUtils.isValidBindingName(this.connector.getName());

        if (result.getSeverity() != IStatus.ERROR) {
            int severity = IStatus.ERROR;
            String msg = null;

            if (this.creatingNewConnector && this.admin.getConnector(this.connector.getName()) != null) {
                // connection factory with that name already exists
                msg = UTIL.getString(PREFIX + "duplicateNameMsg", this.connector.getName()); //$NON-NLS-1$
            } else {
                Collection<String> invalidPropertyNames = this.connector.findInvalidProperties();

                if (invalidPropertyNames.isEmpty()) {
                    // all properties are valid
                    severity = IStatus.OK;
                    msg = UTIL.getString(PREFIX + "allPropertyValuesAreValidMsg"); //$NON-NLS-1$
                } else {
                    // there are invalid properties
                    StringBuilder names = new StringBuilder();

                    for (String name : invalidPropertyNames) {
                        names.append(name).append(", "); //$NON-NLS-1$
                    }

                    // remove last comma
                    msg = UTIL.getString(PREFIX + "invalidPropertyValuesMsg", names.substring(0, names.length() - 2)); //$NON-NLS-1$
                }
            }

            result = new Status(severity, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
        }

        return result;
    }

    void handleNameChanged() {
        this.connector.setName(this.txtName.getText());
        fireChangeEvent();
    }

    /**
     * Handler for when the button to show/hide advanced/expert properties is clicked.
     */
    void handleShowPropertiesSelected() {
        this.sourceProvider.setShowExpertProperties(this.btnShowExpertProps.getSelection());
        this.propertyPage.refresh();
        packPropertiesPage();
    }

    private void packPropertiesPage() {
        Tree tree = (Tree)this.propertyPage.getControl();
        TreeColumn[] treeCols = tree.getColumns();

        for (int i = 0; i < treeCols.length; ++i) {
            treeCols[i].pack();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     */
    public void propertyChange( PropertyChangeEvent theEvent ) {
        fireChangeEvent();
    }

    private void refreshPropertyPage() {
        IStructuredSelection selection;

        if (this.selectedType == null) {
            this.connector = null;
            selection = StructuredSelection.EMPTY;
        } else {
            this.connector = new ConnectorTemplate(this.txtName.getText(), this.selectedType);
            try {
                this.connector.setPropertyValue(IConnectorProperties.CONNECTOR_TYPE, this.selectedType.getName());
                selection = new StructuredSelection(this.connector);
            } catch (Exception e) {
                UTIL.log(e);
                selection = StructuredSelection.EMPTY;
            }
        }

        // notify property page of new selection
        this.propertyPage.selectionChanged(null, selection);
        packPropertiesPage();
    }

    private void refreshPropertyPage( IStructuredSelection selection ) {
        try {
            this.connector.setPropertyValue(IConnectorProperties.CONNECTOR_TYPE, this.selectedType.getName());
            this.propertyPage.selectionChanged(null, selection);
        } catch (Exception e) {
            UTIL.log(e);
            selection = StructuredSelection.EMPTY;
        }

        packPropertiesPage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @throws UnsupportedOperationException if called
     */
    @Override
    public void removeChangeListener( IChangeListener theListener ) {
        throw new UnsupportedOperationException();
    }

}
