/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.common.vdb.ModelInfo;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 * @since 4.2
 */
public class NewConnectorBindingDialog extends ExtendedTitleAreaDialog implements DqpUiConstants, IChangeListener {

    private static final int NEW_TAB = 0;
    private static final int EXISTING_TAB = 1;
    private static final int MAPPING_TAB = 2;
    private static final String PREFIX = I18nUtil.getPropertyPrefix(NewConnectorBindingDialog.class);

    private static String getString( final String theKey ) {
        return UTIL.getStringOrKey(PREFIX + theKey);
    }

    TabFolder tabPane;
    private ConnectorType currentType;
    private final Vdb vdb;
    private final ModelInfo modelInfo;

    private final ExecutionAdmin admin;
    private Connector connector;

    private final String connectorBindingName;
    private BaseNewConnectorBindingPanel newBindingPanel;
    private BaseNewConnectorBindingPanel existingBindingPanel;

    private BaseNewConnectorBindingPanel importSourcePanel;

    private BaseNewConnectorBindingPanel selectedPanel;

    /**
     * Create a NewConnectorBindingDialog using the specified objects.
     * 
     * @param theParentShell the shell
     * @param vdbDefnHelper the helper
     * @param connector the existing connector or <code>null</code>
     * @param connectorBindingName the connector name if a new connector is created
     * @param vdb the VDB
     * @param theModelInfo the model info
     * @since 4.3
     */
    public NewConnectorBindingDialog( final Shell theParentShell,
                                      final Connector connector,
                                      final String connectorBindingName,
                                      final Vdb vdb,
                                      final ModelInfo theModelInfo ) {
        super(theParentShell, DqpUiPlugin.getDefault());
        this.connector = connector;
        this.connectorBindingName = connectorBindingName;
        this.vdb = vdb; // safe to cast
        this.modelInfo = theModelInfo;
        this.admin = connector.getType().getAdmin();

        // set initial size
        setInitialSizeRelativeToScreen(50, 75);
    }

    /**
     * @see com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog#close()
     * @since 5.5.3
     */
    @Override
    public boolean close() {
        if (getReturnCode() == Window.OK) {
            final TabItem[] tabs = tabPane.getSelection();

            if (tabs.length != 0) {
                final BaseNewConnectorBindingPanel pnl = (BaseNewConnectorBindingPanel)tabs[0].getControl();
                try {
                    this.connector = pnl.getConnector();
                } catch (final Exception error) {
                    DqpUiPlugin.showErrorDialog(getShell(), error);
                    return false;
                }
                try {
                    this.currentType = this.connector.getType();
                } catch (final Exception error) {
                    DqpUiPlugin.showErrorDialog(getShell(), error);
                    return false;
                }
            }
        } else {
            this.connector = null;
            this.currentType = null;
        }

        return super.close();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     * @since 4.3
     */
    @Override
    protected Button createButton( final Composite theParent,
                                   final int theId,
                                   final String theLabel,
                                   final boolean theDefaultButton ) {
        final Button btn = super.createButton(theParent, theId, theLabel, theDefaultButton);

        // disable OK button on construction
        if (theId == Window.OK) btn.setEnabled(false);

        return btn;
    }

    @Override
    protected Control createDialogArea( final Composite parent ) {

        final Composite composite = (Composite)super.createDialogArea(parent);

        this.tabPane = new TabFolder(composite, SWT.NONE);
        this.tabPane.setLayout(new GridLayout());
        this.tabPane.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.tabPane.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent e ) {
                handleTabSelectionChanged();
            }
        });

        //
        // Construct tab controls
        //

        this.newBindingPanel = new NewConnectorBindingPanel(this.tabPane, this.connectorBindingName, getConnectorType(), this.vdb);
        this.newBindingPanel.addChangeListener(this);

        this.existingBindingPanel = new ExistingConnectorBindingPanel(this.tabPane, getConnectorType(), getConnector());
        this.existingBindingPanel.addChangeListener(this);

        // add import source panel if needed
        final ModelSource importSource = ModelerDqpUtils.getModelImportSource(this.vdb, this.modelInfo);

        if (importSource != null) {
            this.importSourcePanel = new ImportSourceMappingPanel(this.tabPane, this.vdb, this.modelInfo, importSource);
            this.importSourcePanel.addChangeListener(this);
        }

        final TabItem[] tabs = new TabItem[(importSource == null) ? 2 : 3];

        tabs[NEW_TAB] = new TabItem(this.tabPane, SWT.NONE);
        tabs[NEW_TAB].setText(getString("tab.newBinding")); //$NON-NLS-1$
        tabs[NEW_TAB].setToolTipText(getString("tab.newBinding.tip")); //$NON-NLS-1$
        tabs[NEW_TAB].setControl(this.newBindingPanel);

        tabs[EXISTING_TAB] = new TabItem(this.tabPane, SWT.NONE);
        tabs[EXISTING_TAB].setText(getString("tab.existingBinding")); //$NON-NLS-1$
        tabs[EXISTING_TAB].setToolTipText(getString("tab.existingBinding.tip")); //$NON-NLS-1$
        tabs[EXISTING_TAB].setControl(this.existingBindingPanel);

        if (importSource != null) {
            tabs[MAPPING_TAB] = new TabItem(this.tabPane, SWT.NONE);
            tabs[MAPPING_TAB].setText(getString("tab.importSource")); //$NON-NLS-1$
            tabs[MAPPING_TAB].setToolTipText(getString("tab.importSource.tip")); //$NON-NLS-1$
            tabs[MAPPING_TAB].setControl(this.importSourcePanel);
        }

        setTitle(getString("title")); //$NON-NLS-1$
        getShell().setText(getString("windowTitle")); //$NON-NLS-1$

        // initialize selected tab
        int tabIndex = NEW_TAB;

        if ((this.connector == null) || !((ExistingConnectorBindingPanel)this.existingBindingPanel).hasLoadedBindings()) tabIndex = (importSource == null) ? NEW_TAB : MAPPING_TAB;
        else {
            this.selectedPanel = this.existingBindingPanel;
            tabIndex = EXISTING_TAB;
        }

        this.tabPane.setSelection(tabIndex);

        return composite;
    }

    public Connector getConnector() {
        return this.connector;
    }

    public ConnectorType getConnectorType() {
        return this.currentType;
    }

    BaseNewConnectorBindingPanel getSelectedPanel() {
        return this.selectedPanel;
    }

    void handleTabSelectionChanged() {
        if (!tabPane.isDisposed()) Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                final TabItem[] tabs = tabPane.getSelection();

                if (tabs.length != 0) // should only have one tab selected
                setSelectedPanel((BaseNewConnectorBindingPanel)tabs[0].getControl());
                else setSelectedPanel(null);

                // get state of current tab panel
                stateChanged(getSelectedPanel());
            }
        });
    }

    void setSelectedPanel( final BaseNewConnectorBindingPanel thePanel ) {
        this.selectedPanel = thePanel;
    }

    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 4.3
     */
    public void stateChanged( final IChangeNotifier theSource ) {
        if (!tabPane.isDisposed()) {
            boolean enable = false;
            String msg = ""; //$NON-NLS-1$
            int msgType = IMessageProvider.NONE;
            String title = ""; //$NON-NLS-1$

            if ((theSource == this.selectedPanel) && (this.selectedPanel != null)) {
                final IStatus status = this.selectedPanel.getStatus();
                enable = (status.getSeverity() != IStatus.ERROR);
                msg = status.getMessage();
                msgType = UiUtil.getDialogMessageType(status);
                title = this.selectedPanel.getTitle();
            }

            getButton(Window.OK).setEnabled(enable);
            setMessage(msg, msgType);
            setTitle(title);
        }
    }
}
