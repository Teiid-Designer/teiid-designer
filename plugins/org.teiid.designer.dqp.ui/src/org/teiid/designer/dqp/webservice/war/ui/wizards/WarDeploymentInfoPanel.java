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
import org.eclipse.swt.custom.CLabel;
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
import org.teiid.core.util.StringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
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
    private static final String SECURITY_OPTIONS_GROUP = getString("securityOptionsGroup"); //$NON-NLS-1$
    private static final String GENERAL_OPTIONS_GROUP = getString("generalOptionsGroup"); //$NON-NLS-1$
    private static final String BASIC_OPTIONS_GROUP = getString("basicOptionsGroup"); //$NON-NLS-1$
    private static final String WS_SECURITY_OPTIONS_GROUP = getString("wsSecurityOptionsGroup"); //$NON-NLS-1$

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
    protected Text txfSecurityRealm;
    protected Text txfSecurityRole;
    protected Text txfSecurityUsername;
    protected Text txfSecurityPassword;
    public static final String NOSECURITY = getString("noSecurityButton"); //$NON-NLS-1$
    public static final String BASIC = getString("basicButton"); //$NON-NLS-1$
    public static final String WSSE = getString("wsSecurityButton"); //$NON-NLS-1$
    public static final String MTOM = getString("mtomButton"); //$NON-NLS-1$
    protected Text txfJNDIName;
    protected Button noSecurityButton, basicSecurityButton, wsSecurityButton;
    protected Button mtomButton;
    private Button warBrowseButton;
    private Button restoreDefaultButton;

    protected IFile theVdb;

    protected String WARFILELOCATION;
    protected String NAMESPACE;
    protected String CONTEXTNAME;
    protected String HOST;
    protected String PORT;
    protected String JNDI_NAME;
    protected String SECURITY_TYPE;
    protected String SECURITY_REALM;
    protected String SECURITY_ROLE;
    protected String SECURITY_USERNAME;
    protected String SECURITY_PASSWORD;

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

            // context name should be populated as default, not from the
            // settings.
            text = WarDataserviceModel.getInstance().getContextName();
            txfContext.setText(text);

            // host name
            text = (this.settings.get(HOST) == null ? WarDataserviceModel.getInstance().getHostName() : this.settings.get(HOST));
            txfHost.setText(text);

            // port
            text = (this.settings.get(PORT) == null ? WarDataserviceModel.getInstance().getPort() : this.settings.get(PORT));
            txfPort.setText(text);

            // JNDI Name
            text = WarDataserviceModel.getInstance().getJndiName();
            txfJNDIName.setText(text);

            // Security Realm Name
            text = (this.settings.get(SECURITY_REALM) == null ? WarDataserviceModel.getInstance().getSecurityRealm() : this.settings.get(SECURITY_REALM));
            txfSecurityRealm.setText(text);

            // Security Role Name
            text = (this.settings.get(SECURITY_ROLE) == null ? WarDataserviceModel.getInstance().getSecurityRole() : this.settings.get(SECURITY_ROLE));
            txfSecurityRole.setText(text);

            // Security Username
            text = (this.settings.get(SECURITY_USERNAME) == null ? WarDataserviceModel.getInstance().getSecurityUsername() : this.settings.get(SECURITY_USERNAME));
            txfSecurityUsername.setText(text);

            // Security Password
            text = (this.settings.get(SECURITY_PASSWORD) == null ? WarDataserviceModel.getInstance().getSecurityPassword() : this.settings.get(SECURITY_PASSWORD));
            txfSecurityPassword.setEchoChar('*'); //$NON-NLS-1$
            txfSecurityPassword.setText(text);
            

            // Security type
            text = (this.settings.get(SECURITY_TYPE) == null ? WarDataserviceModel.getInstance().getSecurityType() : this.settings.get(SECURITY_TYPE));

            if (text.equals(NOSECURITY)) {
                this.noSecurityButton.setSelection(true);
            } else if (text.equals(BASIC)) {
                this.basicSecurityButton.setSelection(true);
            } else if (text.equals(WSSE)) {
                this.basicSecurityButton.setSelection(true);
            } else this.noSecurityButton.setSelection(true);

        } catch (RuntimeException err) {
            DqpUiConstants.UTIL.log(err);
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
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, CONTEXTNAME);

        // context name
        txfContext = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("contextTooltip"); //$NON-NLS-1$
        txfContext.setToolTipText(text);

        // hostLabel
        HOST = getString("hostLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, HOST);

        // host name
        txfHost = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("hostTooltip"); //$NON-NLS-1$
        txfHost.setToolTipText(text);

        // portLabel
        PORT = getString("portLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, PORT);

        // port
        txfPort = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("portTooltip"); //$NON-NLS-1$
        txfPort.setToolTipText(text);

        // JNDILabel
        JNDI_NAME = getString("jndiLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, JNDI_NAME);

        // jndi name
        txfJNDIName = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("jndiTooltip"); //$NON-NLS-1$
        txfJNDIName.setToolTipText(text);

        final Group securityOptionsGroup = WidgetFactory.createGroup(pnlContents,
                                                                     SECURITY_OPTIONS_GROUP,
                                                                     GridData.FILL_HORIZONTAL,
                                                                     3);
        {
            CLabel label3 = new CLabel(securityOptionsGroup, SWT.WRAP);
            label3.setText("When using HTTPBasic security, a local Teiid connection is required using the PassthroughAuthentication property."); //$NON-NLS-1$
            final GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
            gridData3.horizontalSpan = 1;
            label3.setLayoutData(gridData3);

            this.noSecurityButton = WidgetFactory.createRadioButton(securityOptionsGroup, NOSECURITY);
            this.noSecurityButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    noSecurityButtonSelected();
                }
            });
            this.noSecurityButton.setSelection(true);
            this.basicSecurityButton = WidgetFactory.createRadioButton(securityOptionsGroup, BASIC);
            this.basicSecurityButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    basicSecurityButtonSelected();
                }
            });
            this.wsSecurityButton = WidgetFactory.createRadioButton(securityOptionsGroup, WSSE);
            this.wsSecurityButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    wsSecurityButtonSelected();
                }
            });

            // HTTPBasic Options
            final Group httpBasicFieldsGroup = WidgetFactory.createGroup(securityOptionsGroup,
                                                                         BASIC_OPTIONS_GROUP,
                                                                         GridData.FILL_HORIZONTAL,
                                                                         3,
                                                                         3);

            // security realm Label
            this.SECURITY_REALM = getString("securityRealmLabel"); //$NON-NLS-1$       
            WidgetFactory.createLabel(httpBasicFieldsGroup, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, SECURITY_REALM);

            // security realm
            this.txfSecurityRealm = WidgetFactory.createTextField(httpBasicFieldsGroup, GridData.FILL_HORIZONTAL, 2);
            text = getString("securityRealmTooltip"); //$NON-NLS-1$;
            this.txfSecurityRealm.setToolTipText(text);
            this.txfSecurityRealm.setEnabled(false);

            // security role Label
            this.SECURITY_ROLE = getString("securityRoleLabel"); //$NON-NLS-1$       
            WidgetFactory.createLabel(httpBasicFieldsGroup, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, SECURITY_ROLE);

            // security role
            this.txfSecurityRole = WidgetFactory.createTextField(httpBasicFieldsGroup, GridData.FILL_HORIZONTAL, 2);
            text = getString("securityRoleTooltip"); //$NON-NLS-1$;
            this.txfSecurityRole.setToolTipText(text);
            this.txfSecurityRole.setEnabled(false);

            // WS-Security Options
            final Group wsSecurityFieldsGroup = WidgetFactory.createGroup(securityOptionsGroup,
                                                                          WS_SECURITY_OPTIONS_GROUP,
                                                                          GridData.FILL_HORIZONTAL,
                                                                          3,
                                                                          3);

            // security username Label
            this.SECURITY_USERNAME = getString("securityUsernameLabel"); //$NON-NLS-1$       
            WidgetFactory.createLabel(wsSecurityFieldsGroup, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, SECURITY_USERNAME);

            // security username
            this.txfSecurityUsername = WidgetFactory.createTextField(wsSecurityFieldsGroup, GridData.FILL_HORIZONTAL, 2);
            text = getString("securityUsernameTooltip"); //$NON-NLS-1$;
            this.txfSecurityUsername.setToolTipText(text);
            this.txfSecurityUsername.setEnabled(false);

            // security password Label
            this.SECURITY_PASSWORD = getString("securityPasswordLabel"); //$NON-NLS-1$       
            WidgetFactory.createLabel(wsSecurityFieldsGroup, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, SECURITY_PASSWORD);

            // security password
            this.txfSecurityPassword = WidgetFactory.createTextField(wsSecurityFieldsGroup, GridData.FILL_HORIZONTAL, 2);
            text = getString("securityPasswordTooltip"); //$NON-NLS-1$;
            this.txfSecurityPassword.setToolTipText(text);
            this.txfSecurityPassword.setEnabled(false);
        }

        final Group generalOptionsGroup = WidgetFactory.createGroup(pnlContents,
                                                                    GENERAL_OPTIONS_GROUP,
                                                                    GridData.FILL_HORIZONTAL,
                                                                    3);
        {

            this.mtomButton = WidgetFactory.createCheckBox(generalOptionsGroup, MTOM, false);
            text = getString("mtomTooltip"); //$NON-NLS-1$
            this.mtomButton.setToolTipText(text);
            this.mtomButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    handleMtomButtonSelected();
                }
            });
        }

        // namespace Label
        this.NAMESPACE = getString("namespaceLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, this.NAMESPACE);

        // namespace
        this.txfNamespace = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("namespaceTooltip"); //$NON-NLS-1$
        this.txfNamespace.setToolTipText(text);

        // WAR file save location Label
        this.WARFILELOCATION = getString("warFileSaveLocationLabel"); //$NON-NLS-1$       
        WidgetFactory.createLabel(pnlContents, GridData.HORIZONTAL_ALIGN_BEGINNING, 1, this.WARFILELOCATION);

        // WAR file save location textfield
        this.txfWarFileDeploymentLocation = WidgetFactory.createTextField(pnlContents, GridData.FILL_HORIZONTAL, 2);
        text = getString("warFileSaveLocationTooltip"); //$NON-NLS-1$
        this.txfWarFileDeploymentLocation.setToolTipText(text);

        // WAR folder browse button
        this.warBrowseButton = WidgetFactory.createButton(pnlContents, InternalUiConstants.Widgets.BROWSE_BUTTON);
        this.warBrowseButton.setText(getString("changeButtonText")); //$NON-NLS-1$
        this.warBrowseButton.setToolTipText(text);
        this.warBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleWarBrowseSourceSelected();
            }
        });

    }

    void setConnectionTypeModified() {
        // disable JNDI name text
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
        this.restoreDefaultButton = WidgetFactory.createButton(restoreDefault, text, GridData.END);
        text = getString("restoreDefaultTooltip"); //$NON-NLS-1$
        this.restoreDefaultButton.setToolTipText(text);
        this.restoreDefaultButton.addSelectionListener(new SelectionAdapter() {
            @Override
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
            @Override
            public void modifyText( ModifyEvent theEvent ) {
                validatePage();
                setWarFileNameInDialog();
            }
        };

        this.txfWarFileDeploymentLocation.addModifyListener(modifyListener);
        this.txfContext.addModifyListener(modifyListener);
        this.txfHost.addModifyListener(modifyListener);
        this.txfPort.addModifyListener(modifyListener);
        this.txfJNDIName.addModifyListener(modifyListener);
        this.txfNamespace.addModifyListener(modifyListener);
        this.txfSecurityRealm.addModifyListener(modifyListener);
        this.txfSecurityRole.addModifyListener(modifyListener);
        this.txfSecurityUsername.addModifyListener(modifyListener);
        this.txfSecurityPassword.addModifyListener(modifyListener);
    }

    protected void setWarFileNameInDialog() {
        dialog.setWarFileName(txfContext.getText());
    }

    void restoreDefaultButtonPressed() {
        this.txfWarFileDeploymentLocation.setText(WarDataserviceModel.getInstance().getWarFilenameDefault());
        this.txfContext.setText(WarDataserviceModel.getInstance().getContextNameDefault());
        this.txfHost.setText(WarDataserviceModel.getInstance().getHostNameDefault());
        this.txfPort.setText(WarDataserviceModel.getInstance().getPortDefault());
        this.txfNamespace.setText(WarDataserviceModel.getInstance().getTnsDefault());
        this.txfJNDIName.setText(WarDataserviceModel.getInstance().getJndiNameDefault());
        this.noSecurityButton.setSelection(true);
        this.basicSecurityButton.setSelection(false);
        this.wsSecurityButton.setSelection(false);
        this.mtomButton.setSelection(false);
        this.txfSecurityRealm.setText(StringUtil.Constants.EMPTY_STRING);
        this.txfSecurityRealm.setEnabled(false);
        this.txfSecurityRole.setText(StringUtil.Constants.EMPTY_STRING);
        this.txfSecurityRole.setEnabled(false);
        this.txfSecurityUsername.setText(StringUtil.Constants.EMPTY_STRING);
        this.txfSecurityUsername.setEnabled(false);
        this.txfSecurityPassword.setText(StringUtil.Constants.EMPTY_STRING);
        this.txfSecurityPassword.setEnabled(false);
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

    /**
     * @since 7.5
     */
    void handleMtomButtonSelected() {
        boolean selection = this.mtomButton.getSelection();
        WarDataserviceModel.getInstance().setUseMtom(selection);
    }

    /**
     * @since 7.1.1
     */
    void noSecurityButtonSelected() {
        if (this.noSecurityButton.getSelection()) {
            WarDataserviceModel.getInstance().setSecurityTypeDefault(NOSECURITY);
            this.txfSecurityRealm.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityRealm.setEnabled(false);
            this.txfSecurityRole.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityRole.setEnabled(false);
            this.txfSecurityUsername.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityUsername.setEnabled(false);
            this.txfSecurityPassword.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityPassword.setEnabled(false);
        }
    }

    /**
     * @since 7.1.1
     */
    void basicSecurityButtonSelected() {
        if (this.basicSecurityButton.getSelection()) {
            WarDataserviceModel.getInstance().setSecurityTypeDefault(BASIC);
            this.txfSecurityRealm.setText("teiid-security"); //$NON-NLS-1$
            WarDataserviceModel.getInstance().setSecurityRealmDefault("teiid-security"); //$NON-NLS-1$
            this.txfSecurityRealm.setEnabled(true);
            this.txfSecurityRole.setText("MyRole"); //$NON-NLS-1$
            WarDataserviceModel.getInstance().setSecurityRoleDefault("MyRole"); //$NON-NLS-1$
            this.txfSecurityRole.setEnabled(true);
            this.txfSecurityUsername.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityUsername.setEnabled(false);
            this.txfSecurityPassword.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityPassword.setEnabled(false);
        }
    }

    /**
     * @since 7.1.1
     */
    void wsSecurityButtonSelected() {
        if (this.wsSecurityButton.getSelection()) {
            WarDataserviceModel.getInstance().setSecurityTypeDefault(WSSE);
            this.txfSecurityRealm.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityRealm.setEnabled(false);
            this.txfSecurityRole.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityRole.setEnabled(false);
            this.txfSecurityUsername.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityUsername.setEnabled(true);
            this.txfSecurityPassword.setText(StringUtil.Constants.EMPTY_STRING);
            this.txfSecurityPassword.setEnabled(true);
        }
    }

}
