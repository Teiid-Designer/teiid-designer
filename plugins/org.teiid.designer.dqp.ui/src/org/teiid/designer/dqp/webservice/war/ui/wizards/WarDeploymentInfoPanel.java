/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiStringUtil;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 7.1
 */
public abstract class WarDeploymentInfoPanel extends Composite implements InternalModelerWarUiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(WarDeploymentInfoDialog.class);
    protected static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    protected IDialogSettings settings;
    protected WarDeploymentInfoDialog dialog;
    protected Text txfWarFileDeploymentLocation;
    protected Text txfContext;
    protected Text txfNamespace;
    protected Text txfHost;
    protected Text txfPort;
    protected Text txfJNDIName;
    private Button warBrowseButton;
    private Button restoreDefaultButton;

    protected IFile theVdb;

    protected String WARFILELOCATION;
    protected String NAMESPACE;
    protected String CONTEXTNAME;
    protected String HOST;
    protected String PORT;
    protected String JNDI_NAME;

    /**
     * @param parent
     * @param dialog
     * @param theVdb
     * @param theVdbContext
     * @since 7.1
     */
    public WarDeploymentInfoPanel( Composite parent,
                                   WarDeploymentInfoDialog dialog,
                                   IFile theVdb ) {
        super(parent, SWT.NONE);
        this.dialog = dialog;
        this.theVdb = theVdb;
        this.setLayout(new GridLayout());
        this.setLayoutData(new GridData(GridData.FILL_BOTH));
        init(this);
    }

    /**
     * @param id
     * @return
     * @since 7.1
     */
    protected static String getString( final String id ) {
        return DqpUiStringUtil.getString(I18N_PREFIX + id);
    }

    /**
     * @since 7.1
     */
    protected void loadData() {

        try {
            // war file location
            String text = (this.settings.get(WARFILELOCATION) == null ? WarDataserviceModel.getInstance().getWarFileLocation() : this.settings.get(WARFILELOCATION));
            txfWarFileDeploymentLocation.setText(text);

            // TNS Name
            text = (this.settings.get(NAMESPACE) == null ? WarDataserviceModel.getInstance().getTns() : this.settings.get(NAMESPACE));
            txfNamespace.setText(text);

            // context name should be populated as default, not from the settings.
            text = WarDataserviceModel.getInstance().getContextName();
            txfContext.setText(text);

            // host name
            text = (this.settings.get(HOST) == null ? WarDataserviceModel.getInstance().getHostName() : this.settings.get(HOST));
            txfHost.setText(text);

            // port
            text = (this.settings.get(PORT) == null ? WarDataserviceModel.getInstance().getPort() : this.settings.get(PORT));
            txfPort.setText(text);

            // JNDI Name
            text = (this.settings.get(JNDI_NAME) == null ? WarDataserviceModel.getInstance().getJndiName() : this.settings.get(JNDI_NAME));
            txfJNDIName.setText(text);

        } catch (RuntimeException err) {
            DqpUiPlugin.UTIL.log(err);
        }
    }

    /**
     * @param isValid
     * @since 7.1
     */
    protected void setDialogMessage( boolean isValid ) {

        this.dialog.setMessage(INITIAL_MESSAGE);
        this.dialog.setOkButtonEnable(isValid);
    }

    /**
     * @param status
     * @since 7.1
     */
    protected void setDialogMessage( IStatus status ) {
        boolean isError = (status.getSeverity() == IStatus.ERROR);

        /**
         * Need to convert the error status code from 4 to 3 because error code in setMessage() is not mapped correctly or
         * IStatus.ERROR != IMessageProvider.ERROR
         */
        int statusCode = (status.getSeverity() == IStatus.ERROR ? IMessageProvider.ERROR : status.getSeverity());

        this.dialog.setMessage(INITIAL_MESSAGE);
        if (!status.isOK()) {
            this.dialog.setMessage(status.getMessage(), statusCode);
        }

        this.dialog.setOkButtonEnable(!isError);
    }

    /**
     * @since 7.1
     */
    protected abstract void validatePage();

    /**
     * @param parent
     * @since 7.1
     */
    private void init( Composite parent ) {

        createDeploymentInfoComposite(parent);
        createRestoreDefault(parent);

        this.settings = WidgetUtil.initializeSettings(this, DqpUiPlugin.getDefault());

        addListeners();
    }

    /**
     * @param parent
     * @since 7.1
     */
    private void createDeploymentInfoComposite( final Composite parent ) {

        // Create info group panel
        String text = getString("grpPanelText"); //$NON-NLS-1$
        Group pnlContents = WidgetFactory.createGroup(parent, text, GridData.FILL_HORIZONTAL, 3, 3);
        // ------------------------------------
        // Web Service WAR components
        // ------------------------------------
        // contextLabel
        CONTEXTNAME = getString("contextLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_END, 1, CONTEXTNAME);

        // context name
        txfContext = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("contextTooltip"); //$NON-NLS-1$
        txfContext.setToolTipText(text);

        // hostLabel
        HOST = getString("hostLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_END, 1, HOST);

        // host name
        txfHost = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("hostTooltip"); //$NON-NLS-1$
        txfHost.setToolTipText(text);

        // portLabel
        PORT = getString("portLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_END, 1, PORT);

        // port
        txfPort = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("portTooltip"); //$NON-NLS-1$
        txfPort.setToolTipText(text);

        // JNDILabel
        JNDI_NAME = getString("jndiLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_END, 1, JNDI_NAME);

        // jndi name
        txfJNDIName = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("jndiTooltip"); //$NON-NLS-1$
        txfJNDIName.setToolTipText(text);

        // namespace Label
        NAMESPACE = getString("namespaceLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_END, 1, NAMESPACE);

        // namespace
        txfNamespace = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("namespaceTooltip"); //$NON-NLS-1$
        txfNamespace.setToolTipText(text);

        // WAR file save location Label
        WARFILELOCATION = getString("warFileSaveLocationLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_END, 1, WARFILELOCATION);

        // WAR file save location textfield
        txfWarFileDeploymentLocation = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("warFileSaveLocationTooltip"); //$NON-NLS-1$
        txfWarFileDeploymentLocation.setToolTipText(text);

        // WAR folder browse button
        warBrowseButton = WidgetFactory.createButton(pnlContents, InternalUiConstants.Widgets.BROWSE_BUTTON);
        warBrowseButton.setText(getString("changeButtonText")); //$NON-NLS-1$
        warBrowseButton.setToolTipText(text);
        warBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleWarBrowseSourceSelected();
            }
        });

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.minimumHeight = 80;
        gridData.verticalIndent = 10;
        gridData.minimumWidth = 530;
        pnlContents.setLayoutData(gridData);
    }

    /**
     * @param parent
     * @since 7.1
     */
    private void createRestoreDefault( final Composite parent ) {

        // Create page
        Composite restoreDefault = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_HORIZONTAL, 1);

        GridLayout layout = new GridLayout();
        restoreDefault.setLayout(layout);
        layout.numColumns = 2;

        // Restore default button
        String text = getString("restoreDefaultButtonText"); //$NON-NLS-1$ 
        restoreDefaultButton = WidgetFactory.createButton(restoreDefault, text, GridData.END);
        text = getString("restoreDefaultTooltip"); //$NON-NLS-1$
        restoreDefaultButton.setToolTipText(text);
        restoreDefaultButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected( final SelectionEvent event ) {
                restoreDefaultButtonPressed();
            }
        });

    }

    /**
     * @since 7.1
     */
    private void addListeners() {

        ModifyListener modifyListener = new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                validatePage();
                setWarFileNameInDialog();
            }
        };

        txfWarFileDeploymentLocation.addModifyListener(modifyListener);
        txfContext.addModifyListener(modifyListener);
        txfHost.addModifyListener(modifyListener);
        txfPort.addModifyListener(modifyListener);
        txfJNDIName.addModifyListener(modifyListener);
    }

    protected void setWarFileNameInDialog() {
        dialog.setWarFileName(txfContext.getText());
    }

    /**
     * restore default values for text fields.
     * 
     * @since 7.1
     */
    private void restoreDefaultButtonPressed() {
        txfWarFileDeploymentLocation.setText(WarDataserviceModel.getInstance().getWarFilenameDefault());
        txfContext.setText(WarDataserviceModel.getInstance().getContextNameDefault());
        txfHost.setText(WarDataserviceModel.getInstance().getHostNameDefault());
        txfPort.setText(WarDataserviceModel.getInstance().getPortDefault());
        txfNamespace.setText(WarDataserviceModel.getInstance().getTnsDefault());
        txfJNDIName.setText(WarDataserviceModel.getInstance().getJndiNameDefault());
    }

    void handleWarBrowseSourceSelected() {
        DirectoryDialog folderDialog = new DirectoryDialog(getShell());
        folderDialog.setText(getString("warTitle")); //$NON-NLS-1$
        folderDialog.setMessage(getString("warMessage")); //$NON-NLS-1$
        folderDialog.setFilterPath(txfWarFileDeploymentLocation.getText());
        String selectedUnit = folderDialog.open();

        // modify history if new model selected
        if (selectedUnit != null) {
            this.txfWarFileDeploymentLocation.setText(selectedUnit);
        }
    }

}
